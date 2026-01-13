import { Navigate, useLocation } from 'react-router-dom'
import { useAppSelector } from '@/store/hooks'
import { selectIsAuthenticated, selectAuthLoading } from '@/features/auth/authSlice'

/**
 * Route guard component that redirects unauthenticated users to login
 * Preserves the intended destination for redirect after login
 */
export default function ProtectedRoute({ children }) {
  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const isLoading = useAppSelector(selectAuthLoading)
  const location = useLocation()

  // Show loading state while checking auth
  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-[100dvh] bg-background">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
    )
  }

  // Redirect to login if not authenticated
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  return children
}
