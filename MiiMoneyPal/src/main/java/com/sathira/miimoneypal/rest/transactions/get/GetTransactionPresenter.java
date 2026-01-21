package com.sathira.miimoneypal.rest.transactions.get;

import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Presenter for GET transaction response.
 * Transforms domain Transaction and related entities into GetTransactionResponse DTO.
 */
@Component
public class GetTransactionPresenter implements GetTransactionResponseBuilder {

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public GetTransactionResponse build(Transaction transaction, Category category, Bucket bucket) {
        return new GetTransactionResponse(
                transaction.id(),
                transaction.type().name(),
                transaction.amount(),
                formatAmount(transaction.amount()),
                transaction.transactionDate(),
                category != null ? category.id() : null,
                category != null ? category.name() : null,
                category != null ? category.type().name() : null,
                bucket != null ? bucket.id() : null,
                bucket != null ? bucket.name() : null,
                bucket != null ? bucket.type().name() : null,
                transaction.note(),
                transaction.createdAt(),
                transaction.updatedAt()
        );
    }

    /**
     * Format amount as currency string (e.g., "$1,234.56").
     */
    private String formatAmount(java.math.BigDecimal amount) {
        return CURRENCY_FORMATTER.format(amount);
    }
}
