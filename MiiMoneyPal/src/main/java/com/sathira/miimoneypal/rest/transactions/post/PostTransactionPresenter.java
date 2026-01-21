package com.sathira.miimoneypal.rest.transactions.post;

import com.sathira.miimoneypal.records.bucket.Bucket;
import com.sathira.miimoneypal.records.category.Category;
import com.sathira.miimoneypal.records.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Presenter for transaction creation response.
 * Transforms domain Transaction and related entities into PostTransactionResponse DTO.
 */
@Component
public class PostTransactionPresenter implements PostTransactionResponseBuilder {

    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public PostTransactionResponse build(Transaction transaction, Category category, Bucket bucket) {
        return new PostTransactionResponse(
                transaction.id(),
                transaction.type().name(),
                transaction.amount(),
                formatAmount(transaction.amount()),
                transaction.transactionDate(),
                category != null ? category.id() : null,
                category != null ? category.name() : null,
                bucket != null ? bucket.id() : null,
                bucket != null ? bucket.name() : null,
                transaction.note(),
                transaction.createdAt()
        );
    }

    /**
     * Format amount as currency string (e.g., "$1,234.56").
     */
    private String formatAmount(java.math.BigDecimal amount) {
        return CURRENCY_FORMATTER.format(amount);
    }
}
