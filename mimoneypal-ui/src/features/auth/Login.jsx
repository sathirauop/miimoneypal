import { useState } from 'react'
import { Navigate } from 'react-router-dom'
import { useAuth } from './useAuth'

/**
 * Login page component
 * Handles both login and registration
 */
export default function Login() {
  const { login, register, isAuthenticated, isLoading, error, clearError } = useAuth()
  const [isRegisterMode, setIsRegisterMode] = useState(false)
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    name: ''
  })

  // Redirect if already authenticated
  if (isAuthenticated) {
    return <Navigate to="/" replace />
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (isRegisterMode) {
      await register(formData)
    } else {
      await login({ email: formData.email, password: formData.password })
    }
  }

  const handleChange = (e) => {
    clearError()
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }))
  }

  const toggleMode = () => {
    clearError()
    setIsRegisterMode((prev) => !prev)
  }

  return (
    <div className="min-h-[100dvh] flex flex-col items-center justify-center px-4 bg-background">
      <div className="w-full max-w-sm">
        {/* Logo/Brand */}
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-primary">MiiMoneyPal</h1>
          <p className="text-text-secondary mt-2">Control the Flow</p>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4">
          {isRegisterMode && (
            <div>
              <label htmlFor="name" className="block text-sm font-medium text-text-primary mb-1">
                Name
              </label>
              <input
                type="text"
                id="name"
                name="name"
                value={formData.name}
                onChange={handleChange}
                className="w-full px-4 py-3 rounded-lg border border-border bg-surface focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                placeholder="Your name"
                required={isRegisterMode}
              />
            </div>
          )}

          <div>
            <label htmlFor="email" className="block text-sm font-medium text-text-primary mb-1">
              Email
            </label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="w-full px-4 py-3 rounded-lg border border-border bg-surface focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="you@example.com"
              required
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-text-primary mb-1">
              Password
            </label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="w-full px-4 py-3 rounded-lg border border-border bg-surface focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
              placeholder="••••••••"
              required
            />
          </div>

          {error && (
            <div className="p-3 rounded-lg bg-danger/10 text-danger text-sm">
              {error}
            </div>
          )}

          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-3 px-4 bg-primary text-white font-medium rounded-lg hover:bg-primary-dark focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isLoading ? 'Loading...' : isRegisterMode ? 'Create Account' : 'Sign In'}
          </button>
        </form>

        {/* Toggle mode */}
        <p className="text-center mt-6 text-text-secondary">
          {isRegisterMode ? 'Already have an account?' : "Don't have an account?"}{' '}
          <button
            type="button"
            onClick={toggleMode}
            className="text-primary font-medium hover:underline"
          >
            {isRegisterMode ? 'Sign In' : 'Create Account'}
          </button>
        </p>
      </div>
    </div>
  )
}
