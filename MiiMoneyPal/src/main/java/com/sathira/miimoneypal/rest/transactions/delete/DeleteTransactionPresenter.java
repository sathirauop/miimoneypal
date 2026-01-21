package com.sathira.miimoneypal.rest.transactions.delete;

import org.springframework.stereotype.Component;

/**
 * Presenter for DELETE transaction response.
 * Builds confirmation message for successful deletion.
 */
@Component
public class DeleteTransactionPresenter implements DeleteTransactionResponseBuilder {

    @Override
    public DeleteTransactionResponse build(Long transactionId) {
        return new DeleteTransactionResponse(
                "Transaction deleted successfully",
                transactionId
        );
    }
}
