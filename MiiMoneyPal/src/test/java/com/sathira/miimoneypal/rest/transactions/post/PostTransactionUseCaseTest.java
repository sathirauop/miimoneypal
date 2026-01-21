package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.exception.BadRequestException;
import com.sathira.miimoneypal.exception.BusinessRuleException;
import com.sathira.miimoneypal.exception.ResourceNotFoundException;
import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.bucket.BucketStatus;
import com.sathira.miimoneypal.records.bucket.BucketType;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.category.CategoryType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PostTransactionUseCase.
 * Tests critical business logic including validation rules.
 */
@ExtendWith(MockitoExtension.class)
class PostTransactionUseCaseTest {

    @Mock
    private PostTransactionDataAccess transactionDataAccess;
    @Mock
    private CategoryDataAccess categoryDataAccess;
    @Mock
    private BucketDataAccess bucketDataAccess;
    @Mock
    private PostTransactionResponseBuilder responseBuilder;

    private PostTransactionUseCase useCase;
    private AppUser testUser;

    @BeforeEach
    void setUp() {
        useCase = new PostTransactionUseCase(
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
    @DisplayName("Should create INCOME transaction successfully")
    void shouldCreateIncomeTransaction() {
        // Given
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.INCOME,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                1L,  // categoryId
                null,  // bucketId
                "Salary"
        );

        Category category = Category.builder()
                .id(1L)
                .userId(1L)
                .name("Salary")
                .type(CategoryType.INCOME)
                .isSystem(false)
                .isArchived(false)
                .build();

        Transaction savedTransaction = Transaction.builder()
                .id(1L)
                .userId(1L)
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("1000.00"))
                .transactionDate(LocalDate.now())
                .categoryId(1L)
                .bucketId(null)
                .note("Salary")
                .createdAt(LocalDateTime.now())
                .build();

        PostTransactionResponse expectedResponse = new PostTransactionResponse(
                1L, "INCOME", new BigDecimal("1000.00"), "$1,000.00",
                LocalDate.now(), 1L, "Salary", null, null, "Salary", LocalDateTime.now()
        );

        when(categoryDataAccess.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(category));
        when(transactionDataAccess.create(any(Transaction.class))).thenReturn(savedTransaction);
        when(responseBuilder.build(savedTransaction, category, null)).thenReturn(expectedResponse);

        // When
        PostTransactionResponse response = useCase.execute(request, testUser);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.type()).isEqualTo("INCOME");
        verify(categoryDataAccess).findByIdAndUserId(1L, 1L);
        verify(transactionDataAccess).create(any(Transaction.class));
    }

    @Test
    @DisplayName("Should reject system-generated transaction type")
    void shouldRejectSystemGeneratedTransactionType() {
        // Given
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.GOAL_COMPLETED,
                new BigDecimal("100.00"),
                LocalDate.now(),
                null,
                1L,
                "Test"
        );

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("GOAL_COMPLETED transactions cannot be created manually");
    }

    @Test
    @DisplayName("Should reject INCOME transaction with bucket instead of category")
    void shouldRejectIncomeWithBucket() {
        // Given - INCOME transaction with both category AND bucket
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.INCOME,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                1L,    // Has category
                1L,    // Also has bucket - wrong!
                "Test"
        );

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("INCOME transactions cannot have a bucket_id");
    }

    @Test
    @DisplayName("Should reject WITHDRAWAL when bucket balance insufficient")
    void shouldRejectWithdrawalWhenInsufficientBalance() {
        // Given
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.WITHDRAWAL,
                new BigDecimal("500.00"),
                LocalDate.now(),
                null,
                1L,  // bucketId
                "Withdrawal"
        );

        Bucket bucket = Bucket.builder()
                .id(1L)
                .userId(1L)
                .name("Emergency Fund")
                .type(BucketType.SAVINGS_GOAL)
                .status(BucketStatus.ACTIVE)
                .build();

        when(bucketDataAccess.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(bucket));
        when(bucketDataAccess.calculateBalance(1L)).thenReturn(new BigDecimal("300.00")); // Less than withdrawal

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Insufficient balance");
    }

    @Test
    @DisplayName("Should reject transaction with archived category")
    void shouldRejectArchivedCategory() {
        // Given
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.EXPENSE,
                new BigDecimal("50.00"),
                LocalDate.now(),
                1L,
                null,
                "Test"
        );

        Category archivedCategory = Category.builder()
                .id(1L)
                .userId(1L)
                .name("Old Category")
                .type(CategoryType.EXPENSE)
                .isSystem(false)
                .isArchived(true)  // Archived!
                .build();

        when(categoryDataAccess.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(archivedCategory));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Cannot use archived category");
    }

    @Test
    @DisplayName("Should reject transaction with category type mismatch")
    void shouldRejectCategoryTypeMismatch() {
        // Given - INCOME transaction with EXPENSE category
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.INCOME,
                new BigDecimal("1000.00"),
                LocalDate.now(),
                1L,
                null,
                "Test"
        );

        Category expenseCategory = Category.builder()
                .id(1L)
                .userId(1L)
                .name("Food")
                .type(CategoryType.EXPENSE)  // Wrong type!
                .isSystem(false)
                .isArchived(false)
                .build();

        when(categoryDataAccess.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(expenseCategory));

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("is type EXPENSE but transaction is type INCOME");
    }

    @Test
    @DisplayName("Should reject transaction when category not found")
    void shouldRejectWhenCategoryNotFound() {
        // Given
        PostTransactionRequest request = new PostTransactionRequest(
                TransactionType.EXPENSE,
                new BigDecimal("50.00"),
                LocalDate.now(),
                999L,  // Non-existent category
                null,
                "Test"
        );

        when(categoryDataAccess.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> useCase.execute(request, testUser))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found or access denied");
    }
}
