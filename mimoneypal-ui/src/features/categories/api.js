import axios from '@/lib/axios';

const CATEGORIES_URL = '/categories';

/**
 * Category API service for CRUD operations.
 * All requests require authentication (JWT token handled by axios interceptor).
 */
export const categoriesApi = {
  /**
   * Create a new category.
   * @param {Object} data - Category creation data
   * @param {string} data.name - Category name (required, max 100 chars)
   * @param {string} data.type - Category type: "INCOME" or "EXPENSE" (required)
   * @param {string} [data.color] - Color code or name (optional, max 20 chars)
   * @param {string} [data.icon] - Icon identifier (optional, max 50 chars)
   * @returns {Promise<Object>} Created category
   */
  create: async (data) => {
    const response = await axios.post(CATEGORIES_URL, data);
    return response.data;
  },

  /**
   * Get a single category by ID.
   * @param {number} id - Category ID
   * @returns {Promise<Object>} Category details
   */
  getById: async (id) => {
    const response = await axios.get(`${CATEGORIES_URL}/${id}`);
    return response.data;
  },

  /**
   * List categories with optional filters.
   * @param {Object} [params] - Query parameters
   * @param {string} [params.type] - Filter by type: "INCOME" or "EXPENSE"
   * @param {boolean} [params.includeArchived=false] - Include archived categories
   * @returns {Promise<Object>} List response with categories array and total count
   */
  list: async (params = {}) => {
    const response = await axios.get(CATEGORIES_URL, {
      params: {
        type: params.type,
        include_archived: params.includeArchived
      }
    });
    return response.data;
  },

  /**
   * Update an existing category.
   * @param {number} id - Category ID
   * @param {Object} data - Category update data
   * @param {string} data.name - Updated category name (required, max 100 chars)
   * @param {string} [data.color] - Updated color (optional, max 20 chars)
   * @param {string} [data.icon] - Updated icon (optional, max 50 chars)
   * @returns {Promise<Object>} Updated category
   */
  update: async (id, data) => {
    const response = await axios.put(`${CATEGORIES_URL}/${id}`, {
      id,
      ...data
    });
    return response.data;
  },

  /**
   * Delete a category.
   * - If category has transactions: soft delete (archive)
   * - If category has no transactions: hard delete
   * @param {number} id - Category ID
   * @returns {Promise<Object>} Deletion response with deletionType ("ARCHIVED" or "DELETED")
   */
  delete: async (id) => {
    const response = await axios.delete(`${CATEGORIES_URL}/${id}`);
    return response.data;
  }
};
