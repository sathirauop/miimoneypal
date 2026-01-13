import { QueryClient } from '@tanstack/react-query'

export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 1000 * 60 * 5, // 5 minutes
      gcTime: 1000 * 60 * 30, // 30 minutes (previously cacheTime)
      retry: 1,
      refetchOnWindowFocus: false
    },
    mutations: {
      retry: 0
    }
  }
})

// Query keys factory for type-safe cache invalidation
export const queryKeys = {
  // Dashboard
  dashboard: {
    all: ['dashboard'],
    monthly: (year, month) => ['dashboard', 'monthly', year, month]
  },

  // Transactions
  transactions: {
    all: ['transactions'],
    list: (filters) => ['transactions', 'list', filters],
    detail: (id) => ['transactions', 'detail', id]
  },

  // Buckets
  buckets: {
    all: ['buckets'],
    list: () => ['buckets', 'list'],
    detail: (id) => ['buckets', 'detail', id]
  },

  // Categories
  categories: {
    all: ['categories'],
    list: (type) => ['categories', 'list', type],
    detail: (id) => ['categories', 'detail', id]
  },

  // User
  user: {
    profile: ['user', 'profile'],
    settings: ['user', 'settings']
  }
}
