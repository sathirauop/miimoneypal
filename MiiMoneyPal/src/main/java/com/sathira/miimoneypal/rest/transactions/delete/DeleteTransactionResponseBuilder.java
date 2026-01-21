package com.sathira.miimoneypal.rest.transactions.delete;

/**
 * Interface for building DELETE transaction response.
 * Transforms deletion confirmation into response DTO.
 */
public interface DeleteTransactionResponseBuilder {

    /**
     * Build response confirming successful deletion.
     *
     * @param transactionId the ID of the deleted transaction
     * @return response DTO with confirmation message
     */
    DeleteTransactionResponse build(Long transactionId);
}
