package com.sathira.miimoneypal.rest.categories.list;

/**
 * Summary DTO for a category in list responses.
 * Contains essential category information without timestamps.
 */
public record CategorySummary(
        Long id,
        String name,
        String type,
        String color,
        String icon,
        Boolean isSystem,
        Boolean isArchived
) {
}
