package com.sathira.miimoneypal.rest.transactions.delete;

import com.sathira.miimoneypal.models.response.ApiResponse;

/**
 * Response DTO for successful transaction deletion.
 * Returns confirmation message with the deleted transaction ID.
 */
public record DeleteTransactionResponse(
        String message,
        Long deletedTransactionId
) implements ApiResponse {}
