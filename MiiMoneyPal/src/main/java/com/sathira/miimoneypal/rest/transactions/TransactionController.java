package com.sathira.miimoneypal.rest.transactions;

import com.sathira.miimoneypal.constants.EndPoints;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import com.sathira.miimoneypal.rest.transactions.delete.DeleteTransactionRequest;
import com.sathira.miimoneypal.rest.transactions.delete.DeleteTransactionResponse;
import com.sathira.miimoneypal.rest.transactions.delete.DeleteTransactionUseCase;
import com.sathira.miimoneypal.rest.transactions.get.GetTransactionRequest;
import com.sathira.miimoneypal.rest.transactions.get.GetTransactionResponse;
import com.sathira.miimoneypal.rest.transactions.get.GetTransactionUseCase;
import com.sathira.miimoneypal.rest.transactions.list.ListTransactionsRequest;
import com.sathira.miimoneypal.rest.transactions.list.ListTransactionsResponse;
import com.sathira.miimoneypal.rest.transactions.list.ListTransactionsUseCase;
import com.sathira.miimoneypal.rest.transactions.post.PostTransactionRequest;
import com.sathira.miimoneypal.rest.transactions.post.PostTransactionResponse;
import com.sathira.miimoneypal.rest.transactions.post.PostTransactionUseCase;
import com.sathira.miimoneypal.rest.transactions.put.PutTransactionRequest;
import com.sathira.miimoneypal.rest.transactions.put.PutTransactionResponse;
import com.sathira.miimoneypal.rest.transactions.put.PutTransactionUseCase;
import com.sathira.miimoneypal.security.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST controller for transaction CRUD operations.
 * All endpoints require authentication (JWT token).
 *
 * Endpoints:
 * - POST   /api/transactions       - Create new transaction
 * - GET    /api/transactions/{id}  - Get transaction by ID
 * - GET    /api/transactions       - List transactions with filters
 * - PUT    /api/transactions/{id}  - Update transaction
 * - DELETE /api/transactions/{id}  - Delete transaction
 */
@RestController
@RequestMapping(EndPoints.TRANSACTIONS)
@RequiredArgsConstructor
public class TransactionController {

    private final PostTransactionUseCase postTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final PutTransactionUseCase putTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    /**
     * Create a new transaction.
     *
     * @param request Transaction details (type, amount, date, category/bucket, note)
     * @param user    Authenticated user (injected by Spring Security)
     * @return Created transaction with related entity details
     */
    @PostMapping
    public ResponseEntity<PostTransactionResponse> createTransaction(
            @Valid @RequestBody PostTransactionRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        PostTransactionResponse response = postTransactionUseCase.execute(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a single transaction by ID.
     *
     * @param id   Transaction ID from path variable
     * @param user Authenticated user (injected by Spring Security)
     * @return Transaction details with related entity information
     */
    @GetMapping("/{id}")
    public ResponseEntity<GetTransactionResponse> getTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {
        GetTransactionRequest request = new GetTransactionRequest(id);
        GetTransactionResponse response = getTransactionUseCase.execute(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * List transactions with optional filters and pagination.
     *
     * @param type       Optional: filter by transaction type
     * @param startDate  Optional: filter transactions on or after this date
     * @param endDate    Optional: filter transactions on or before this date
     * @param categoryId Optional: filter by category ID
     * @param bucketId   Optional: filter by bucket ID
     * @param searchTerm Optional: search in transaction notes
     * @param offset     Pagination offset (default: 0)
     * @param limit      Pagination limit (default: 20, max: 100)
     * @param user       Authenticated user (injected by Spring Security)
     * @return Paginated list of transactions with metadata
     */
    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long bucketId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) Integer limit,
            @AuthenticationPrincipal AppUser user
    ) {
        ListTransactionsRequest request = new ListTransactionsRequest(
                type, startDate, endDate, categoryId, bucketId, searchTerm, offset, limit
        );
        ListTransactionsResponse response = listTransactionsUseCase.execute(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing transaction.
     * Transaction type cannot be changed after creation.
     *
     * @param id      Transaction ID from path variable
     * @param request Transaction update details (amount, date, category/bucket, note)
     * @param user    Authenticated user (injected by Spring Security)
     * @return Updated transaction with related entity details
     */
    @PutMapping("/{id}")
    public ResponseEntity<PutTransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody PutTransactionRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        // Ensure path ID matches request body ID
        if (!id.equals(request.id())) {
            throw new IllegalArgumentException("Path ID does not match request body ID");
        }

        PutTransactionResponse response = putTransactionUseCase.execute(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a transaction permanently.
     * System-generated transactions cannot be deleted.
     *
     * @param id   Transaction ID from path variable
     * @param user Authenticated user (injected by Spring Security)
     * @return Deletion confirmation message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteTransactionResponse> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {
        DeleteTransactionRequest request = new DeleteTransactionRequest(id);
        DeleteTransactionResponse response = deleteTransactionUseCase.execute(request, user);
        return ResponseEntity.ok(response);
    }
}
