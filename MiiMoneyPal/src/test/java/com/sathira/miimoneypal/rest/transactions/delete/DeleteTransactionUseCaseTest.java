package com.sathira.miimoneypal.rest.transactions.delete;

import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.transaction.Transaction;
import com.sathira.miimoneypal.records.transaction.TransactionType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DeleteTransactionUseCase.
 * Focuses on system transaction protection.
 */
@ExtendWith(MockitoExtension.class)
class DeleteTransactionUseCaseTest {

    @Mock
    private DeleteTransactionDataAccess transactionDataAccess;
    @Mock
    private DeleteTransactionResponseBuilder responseBuilder;

    private DeleteTransactionUseCase useCase;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        useCase = new DeleteTransactionUseCase(transactionDataAccess, responseBuilder);

        testUser = AppUser.builder()
                .id(1L)
                .email("test@example.com")
                .passwordHash("hash")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("Should delete user-created transaction successfully")
    void shouldDeleteUserCreatedTransaction() {
        // Given
        DeleteTransactionRequest request = new DeleteTransactionRequest(1L);

        Transaction userTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.EXPENSE)  // User-created type
                .amount(new BigDecimal("50.00"))
                .transactionDate(LocalDate.now())
                .categoryId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        DeleteTransactionResponse expectedResponse = new DeleteTransactionResponse(
                "Transaction deleted successfully",
                1L
        );

        when(transactionDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(userTransaction));
        when(transactionDataAccess.deleteByIdAndUserId(1L, 1L))
                .thenReturn(true);
        when(responseBuilder.build(1L))
                .thenReturn(expectedResponse);

        // When
        DeleteTransactionResponse response = useCase.execute(request, testUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.deletedTransactionId()).isEqualTo(1L);
        assertThat(response.message()).contains("deleted successfully");
        verify(transactionDataAccess).deleteByIdAndUserId(1L, 1L);
    }

    @Test
    @DisplayName("Should prevent deleting system-generated transactions")
    void shouldPreventDeletingSystemGeneratedTransactions() {
        // Given
        DeleteTransactionRequest request = new DeleteTransactionRequest(1L);

        Transaction goalCompletedTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.GOAL_COMPLETED)  // System-generated type
                .amount(new BigDecimal("500.00"))
                .transactionDate(LocalDate.now())
                .bucketId(1L)
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionDataAccess.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(goalCompletedTransaction));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("System-generated transactions cannot be deleted");

        verify(transactionDataAccess, never()).deleteByIdAndUserId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when transaction not found")
    void shouldThrowExceptionWhenTransactionNotFound() {
        // Given
        DeleteTransactionRequest request = new DeleteTransactionRequest(999L);

        when(transactionDataAccess.findByIdAndUserId(999L, 1L))
                .thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Transaction not found or access denied");

        verify(transactionDataAccess, never()).deleteByIdAndUserId(anyLong(), anyLong());
    }
}
