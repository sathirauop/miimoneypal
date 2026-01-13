import { useCallback, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAppDispatch, useAppSelector } from '@/store/hooks'
import {
  login as loginAction,
  register as registerAction,
  logout as logoutAction,
  fetchCurrentUser,
  clearError,
  selectIsAuthenticated,
  selectCurrentUser,
  selectAuthLoading,
  selectAuthError
} from './authSlice'

/**
 * Custom hook for authentication operations
 * @returns {object} Auth state and methods
 */
export function useAuth() {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()

  const isAuthenticated = useAppSelector(selectIsAuthenticated)
  const user = useAppSelector(selectCurrentUser)
  const isLoading = useAppSelector(selectAuthLoading)
  const error = useAppSelector(selectAuthError)

  // Fetch current user on mount if token exists
  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token && !user) {
      dispatch(fetchCurrentUser())
    }
  }, [dispatch, user])

  const login = useCallback(
    async (credentials) => {
      const result = await dispatch(loginAction(credentials))
      if (loginAction.fulfilled.match(result)) {
        navigate('/')
        return true
      }
      return false
    },
    [dispatch, navigate]
  )

  const register = useCallback(
    async (userData) => {
      const result = await dispatch(registerAction(userData))
      if (registerAction.fulfilled.match(result)) {
        navigate('/')
        return true
      }
      return false
    },
    [dispatch, navigate]
  )

  const logout = useCallback(() => {
    dispatch(logoutAction())
    navigate('/login')
  }, [dispatch, navigate])

  const clearAuthError = useCallback(() => {
    dispatch(clearError())
  }, [dispatch])

  return {
    // State
    isAuthenticated,
    user,
    isLoading,
    error,

    // Actions
    login,
    register,
    logout,
    clearError: clearAuthError
  }
}
