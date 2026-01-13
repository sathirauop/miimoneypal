import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Provider } from 'react-redux'
import { QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'

import { store } from '@/store'
import { queryClient } from '@/lib/queryClient'
import { Layout, ProtectedRoute } from '@/components/Layout'

// Feature pages
import Login from '@/features/auth/Login'
import { Dashboard } from '@/features/dashboard'
import { Transactions } from '@/features/transactions'
import { Buckets } from '@/features/buckets'
import { Settings } from '@/features/settings'

/**
 * Root App component
 * Sets up providers and routing
 */
function App() {
  return (
    <Provider store={store}>
      <QueryClientProvider client={queryClient}>
        <BrowserRouter>
          <Routes>
            {/* Public routes */}
            <Route path="/login" element={<Login />} />

            {/* Protected routes with Layout */}
            <Route
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route path="/" element={<Dashboard />} />
              <Route path="/transactions" element={<Transactions />} />
              <Route path="/buckets" element={<Buckets />} />
              <Route path="/settings" element={<Settings />} />
            </Route>
          </Routes>
        </BrowserRouter>

        {/* React Query DevTools - only in development */}
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>
    </Provider>
  )
}

export default App
