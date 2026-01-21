/**
 * Categories feature exports.
 * Provides API and hooks for category management.
 */

export { categoriesApi } from './api';
export {
  useCategoriesList,
  useCategory,
  useCreateCategory,
  useUpdateCategory,
  useDeleteCategory
} from './useCategoriesQuery';
