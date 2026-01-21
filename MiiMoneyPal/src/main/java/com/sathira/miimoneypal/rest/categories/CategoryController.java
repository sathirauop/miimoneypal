package com.sathira.miimoneypal.rest.categories;

import com.sathira.miimoneypal.constants.EndPoints;
import com.sathira.miimoneypal.records.category.CategoryType;
import com.sathira.miimoneypal.rest.categories.delete.DeleteCategoryRequest;
import com.sathira.miimoneypal.rest.categories.delete.DeleteCategoryResponse;
import com.sathira.miimoneypal.rest.categories.delete.DeleteCategoryUseCase;
import com.sathira.miimoneypal.rest.categories.get.GetCategoryRequest;
import com.sathira.miimoneypal.rest.categories.get.GetCategoryResponse;
import com.sathira.miimoneypal.rest.categories.get.GetCategoryUseCase;
import com.sathira.miimoneypal.rest.categories.list.ListCategoriesRequest;
import com.sathira.miimoneypal.rest.categories.list.ListCategoriesResponse;
import com.sathira.miimoneypal.rest.categories.list.ListCategoriesUseCase;
import com.sathira.miimoneypal.rest.categories.post.PostCategoryRequest;
import com.sathira.miimoneypal.rest.categories.post.PostCategoryResponse;
import com.sathira.miimoneypal.rest.categories.post.PostCategoryUseCase;
import com.sathira.miimoneypal.rest.categories.put.PutCategoryRequest;
import com.sathira.miimoneypal.rest.categories.put.PutCategoryResponse;
import com.sathira.miimoneypal.rest.categories.put.PutCategoryUseCase;
import com.sathira.miimoneypal.security.AppUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for category management operations.
 * Provides CRUD endpoints for income and expense categories.
 *
 * All endpoints require authentication and enforce user ownership.
 */
@RestController
@RequestMapping(EndPoints.CATEGORIES)
@RequiredArgsConstructor
public class CategoryController {

    private final PostCategoryUseCase postCategoryUseCase;
    private final GetCategoryUseCase getCategoryUseCase;
    private final ListCategoriesUseCase listCategoriesUseCase;
    private final PutCategoryUseCase putCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;

    /**
     * Create a new category.
     * POST /api/categories
     *
     * @param request The category creation request
     * @param user The authenticated user
     * @return 201 Created with the created category
     */
    @PostMapping
    public ResponseEntity<PostCategoryResponse> createCategory(
            @Valid @RequestBody PostCategoryRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        PostCategoryResponse response = postCategoryUseCase.execute(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get a single category by ID.
     * GET /api/categories/{id}
     *
     * @param id The category ID
     * @param user The authenticated user
     * @return 200 OK with the category, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<GetCategoryResponse> getCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {
        GetCategoryResponse response = getCategoryUseCase.execute(
                new GetCategoryRequest(id),
                user
        );
        return ResponseEntity.ok(response);
    }

    /**
     * List all categories for the authenticated user.
     * GET /api/categories
     *
     * Supports optional query parameters:
     * - type: Filter by INCOME or EXPENSE
     * - includeArchived: Include archived categories (default: false)
     *
     * @param type Optional category type filter
     * @param includeArchived Whether to include archived categories
     * @param user The authenticated user
     * @return 200 OK with the list of categories
     */
    @GetMapping
    public ResponseEntity<ListCategoriesResponse> listCategories(
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) Boolean includeArchived,
            @AuthenticationPrincipal AppUser user
    ) {
        ListCategoriesResponse response = listCategoriesUseCase.execute(
                new ListCategoriesRequest(type, includeArchived),
                user
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing category.
     * PUT /api/categories/{id}
     *
     * Note: Category type cannot be changed after creation.
     *
     * @param id The category ID (must match request body ID)
     * @param request The category update request
     * @param user The authenticated user
     * @return 200 OK with the updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<PutCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody PutCategoryRequest request,
            @AuthenticationPrincipal AppUser user
    ) {
        // Validate that path ID matches request body ID
        if (!id.equals(request.id())) {
            throw new IllegalArgumentException("Path ID does not match request body ID");
        }

        PutCategoryResponse response = putCategoryUseCase.execute(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a category.
     * DELETE /api/categories/{id}
     *
     * Business Rules:
     * - If category has transactions: soft delete (archive)
     * - If category has no transactions: hard delete
     * - System categories cannot be deleted
     *
     * @param id The category ID
     * @param user The authenticated user
     * @return 200 OK with deletion details (ARCHIVED or DELETED)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteCategoryResponse> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal AppUser user
    ) {
        DeleteCategoryResponse response = deleteCategoryUseCase.execute(
                new DeleteCategoryRequest(id),
                user
        );
        return ResponseEntity.ok(response);
    }
}
