package com.sathira.miimoneypal.rest.categories.list;

import com.sathira.miimoneypal.records.category.Category;

import java.util.List;

/**
 * Response builder interface for LIST categories operation.
 * Transforms list of domain Category records to ListCategoriesResponse DTO.
 */
public interface ListCategoriesResponseBuilder {

    /**
     * Build a response DTO from a list of Category domain records.
     * @param categories The list of domain category records
     * @return The response DTO
     */
    ListCategoriesResponse build(List<Category> categories);
}
