export { useAuth } from './useAuth'
export {
  login,
  register,
  logout,
  fetchCurrentUser,
  clearError,
  selectIsAuthenticated,
  selectCurrentUser,
  selectAuthLoading,
  selectAuthError
} from './authSlice'
export { default as authReducer } from './authSlice'
