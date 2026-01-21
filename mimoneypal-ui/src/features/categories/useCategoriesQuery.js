import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { queryKeys } from '@/lib/queryClient';
import { categoriesApi } from './api';

/**
 * Hook to list categories with optional filters.
 * @param {Object} [filters={}] - Filter options
 * @param {string} [filters.type] - Filter by "INCOME" or "EXPENSE"
 * @param {boolean} [filters.includeArchived=false] - Include archived categories
 * @returns {Object} TanStack Query result with categories data
 */
export function useCategoriesList(filters = {}) {
  return useQuery({
    queryKey: queryKeys.categories.list(filters.type),
    queryFn: () => categoriesApi.list(filters),
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
}

/**
 * Hook to get a single category by ID.
 * @param {number} id - Category ID
 * @returns {Object} TanStack Query result with category data
 */
export function useCategory(id) {
  return useQuery({
    queryKey: queryKeys.categories.detail(id),
    queryFn: () => categoriesApi.getById(id),
    enabled: !!id,
    staleTime: 1000 * 60 * 5, // 5 minutes
  });
}

/**
 * Hook to create a new category.
 * Invalidates category list queries on success.
 * @returns {Object} TanStack Mutation result
 */
export function useCreateCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: categoriesApi.create,
    onSuccess: () => {
      // Invalidate all category list queries
      queryClient.invalidateQueries({ queryKey: queryKeys.categories.all });
    },
  });
}

/**
 * Hook to update an existing category.
 * Invalidates category queries on success.
 * @returns {Object} TanStack Mutation result
 */
export function useUpdateCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ id, ...data }) => categoriesApi.update(id, data),
    onSuccess: (_, variables) => {
      // Invalidate all category queries
      queryClient.invalidateQueries({ queryKey: queryKeys.categories.all });
      // Invalidate specific category detail
      queryClient.invalidateQueries({
        queryKey: queryKeys.categories.detail(variables.id)
      });
    },
  });
}

/**
 * Hook to delete a category.
 * Invalidates category and transaction queries on success.
 * @returns {Object} TanStack Mutation result
 */
export function useDeleteCategory() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: categoriesApi.delete,
    onSuccess: () => {
      // Invalidate all category queries
      queryClient.invalidateQueries({ queryKey: queryKeys.categories.all });
      // Also invalidate transactions as they reference categories
      queryClient.invalidateQueries({ queryKey: queryKeys.transactions.all });
    },
  });
}
