package com.sathira.miimoneypal.rest.transactions.delete;

import com.sathira.miimoneypal.architecture.AuthenticatedUseCase;
import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import com.sathira.miimoneypal.security.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * UseCase for deleting transactions.
 * Enforces business rules including preventing deletion of system-generated transactions.
 */
@Service
@RequiredArgsConstructor
public class DeleteTransactionUseCase
        implements AuthenticatedUseCase<DeleteTransactionRequest, DeleteTransactionResponse> {

    private final DeleteTransactionDataAccess transactionDataAccess;
    private final DeleteTransactionResponseBuilder responseBuilder;

    @Override
    @Transactional
    public DeleteTransactionResponse execute(DeleteTransactionRequest request, AppUser user) {
        // 1. Find existing transaction (enforces user ownership)
        Transaction existingTransaction = transactionDataAccess.findByIdAndUserId(request.id(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));

        // 2. Prevent deleting system-generated transactions
        if (existingTransaction.type() == TransactionType.GOAL_COMPLETED) {
            throw new BadRequestException(
                    "System-generated transactions cannot be deleted. " +
                    "They are automatically created when marking savings goals as spent."
            );
        }

        // 3. Delete the transaction (hard delete - permanent removal)
        boolean deleted = transactionDataAccess.deleteByIdAndUserId(request.id(), user.getId());

        if (!deleted) {
            // This should not happen since we verified existence above
            throw new IllegalStateException("Transaction deletion failed unexpectedly");
        }

        // 4. Build and return confirmation response
        return responseBuilder.build(request.id());
    }
}
