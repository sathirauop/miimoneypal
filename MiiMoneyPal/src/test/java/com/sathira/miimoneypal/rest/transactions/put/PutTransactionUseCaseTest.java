package com.sathira.miimoneypal.rest.transactions.put;

import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.exception.BusinessRuleException;
import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.bucket.BucketStatus;
import com.sathira.miimoneypal.records.bucket.BucketType;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
import com.sathira.miimoneypal.repository.BucketDataAccess;
import com.sathira.miimoneypal.repository.CategoryDataAccess;
import com.sathira.miimoneypal.security.AppUser;
import com.sathira.miimoneypal.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PutTransactionUseCase.
 * Focuses on balance delta calculation and system transaction protection.
 */
@ExtendWith(MockitoExtension.class)
class PutTransactionUseCaseTest {

    @Mock
    private PutTransactionDataAccess transactionDataAccess;
    @Mock
    private CategoryDataAccess categoryDataAccess;
    @Mock
    private BucketDataAccess bucketDataAccess;
    @Mock
    private PutTransactionResponseBuilder responseBuilder;

    private PutTransactionUseCase useCase;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        useCase = new PutTransactionUseCase(
                transactionDataAccess,
                categoryDataAccess,
                bucketDataAccess,
                responseBuilder
        );

        testUser = AppUser.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should prevent updating system-generated transactions")
    void shouldPreventUpdatingSystemGeneratedTransactions() {
        // Given
        PutTransactionRequest request = new PutTransactionRequest(
                1L,
                new BigDecimal("100.00"),
                LocalDate.now(),
                null,
                1L,
                "Updated note"
        );

        Transaction goalCompletedTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.GOAL_COMPLETED)  // System-generated type
                .amount(new BigDecimal("500.00"))
                .transactionDate(LocalDate.now().minusDays(1))
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(goalCompletedTransaction));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("System-generated transactions cannot be modified");

        verify(transactionDataAccess, never()).update(any());
    }

    @Test
    @DisplayName("Should allow increasing WITHDRAWAL amount when sufficient balance")
    void shouldAllowIncreasingWithdrawalWhenSufficientBalance() {
        // Given - Increasing withdrawal from $100 to $200
        PutTransactionRequest request = new PutTransactionRequest(
                1L,
                new BigDecimal("200.00"),  // New amount (increase)
                LocalDate.now(),
                null,
                1L,
                "Updated withdrawal"
        );

        Transaction existingTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100.00"))  // Old amount
                .transactionDate(LocalDate.now())
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        Bucket bucket = Bucket.builder()
                .id(1L)
                .userId(1L)
                .name("Savings")
                .type(BucketType.PERPETUAL_ASSET)
                .status(BucketStatus.ACTIVE)
                .build();

        Transaction updatedTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("200.00"))
                .transactionDate(LocalDate.now())
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(transactionDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(existingTransaction));
        when(bucketDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(bucket));
        // Current balance: $400 (includes the old $100 withdrawal already subtracted)
        // Available for additional withdrawal: $400 + $100 (add back old) - $200 (new) = $300 >= 0 ✓
        when(bucketDataAccess.calculateBalance(1L))
                .thenReturn(new BigDecimal("400.00"));
        when(transactionDataAccess.update(any(Transaction.class)))
                .thenReturn(updatedTransaction);
        when(responseBuilder.build(any(), any(), any()))
                .thenReturn(mock(PutTransactionResponse.class));

        // When
        useCase.execute(request, testUser);

        // Then - No exception, update executed
        verify(transactionDataAccess).update(any(Transaction.class));
    }

    @Test
    @DisplayName("Should reject increasing WITHDRAWAL when insufficient balance")
    void shouldRejectIncreasingWithdrawalWhenInsufficientBalance() {
        // Given - Trying to increase withdrawal from $300 to $500
        PutTransactionRequest request = new PutTransactionRequest(
                1L,
                new BigDecimal("500.00"),  // New amount (increase by $200)
                LocalDate.now(),
                null,
                1L,
                "Updated withdrawal"
        );

        Transaction existingTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("300.00"))  // Old amount
                .transactionDate(LocalDate.now())
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        Bucket bucket = Bucket.builder()
                .id(1L)
                .userId(1L)
                .name("Savings")
                .type(BucketType.PERPETUAL_ASSET)
                .status(BucketStatus.ACTIVE)
                .build();

        when(transactionDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(existingTransaction));
        when(bucketDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(bucket));
        // Current balance: $100 (includes the old $300 withdrawal already subtracted)
        // Available: $100 + $300 (add back old) - $500 (new) = -$100 < 0 ✗
        when(bucketDataAccess.calculateBalance(1L))
                .thenReturn(new BigDecimal("100.00"));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Insufficient balance");

        verify(transactionDataAccess, never()).update(any());
    }

    @Test
    @DisplayName("Should allow decreasing WITHDRAWAL amount")
    void shouldAllowDecreasingWithdrawalAmount() {
        // Given - Decreasing withdrawal from $200 to $100
        PutTransactionRequest request = new PutTransactionRequest(
                1L,
                new BigDecimal("100.00"),  // New amount (decrease)
                LocalDate.now(),
                null,
                1L,
                "Updated withdrawal"
        );

        Transaction existingTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("200.00"))  // Old amount
                .transactionDate(LocalDate.now())
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        Bucket bucket = Bucket.builder()
                .id(1L)
                .userId(1L)
                .name("Savings")
                .type(BucketType.PERPETUAL_ASSET)
                .status(BucketStatus.ACTIVE)
                .build();

        Transaction updatedTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100.00"))
                .transactionDate(LocalDate.now())
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(transactionDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(existingTransaction));
        when(bucketDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(bucket));
        // Decreasing withdrawal always safe - returns money to bucket
        when(bucketDataAccess.calculateBalance(1L))
                .thenReturn(new BigDecimal("50.00"));
        when(transactionDataAccess.update(any(Transaction.class)))
                .thenReturn(updatedTransaction);
        when(responseBuilder.build(any(), any(), any()))
                .thenReturn(mock(PutTransactionResponse.class));

        // When
        useCase.execute(request, testUser);

        // Then - No exception, update executed
        verify(transactionDataAccess).update(any(Transaction.class));
    }
}
