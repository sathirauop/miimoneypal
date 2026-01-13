import { useAuth } from '@/features/auth/useAuth'

/**
 * Settings page - User profile and app settings
 */
export default function Settings() {
  const { user, logout } = useAuth()

  const settingsItems = [
    {
      label: 'Categories',
      description: 'Manage income and expense categories',
      icon: (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 7h.01M7 3h5c.512 0 1.024.195 1.414.586l7 7a2 2 0 010 2.828l-7 7a2 2 0 01-2.828 0l-7-7A1.994 1.994 0 013 12V7a4 4 0 014-4z" />
        </svg>
      ),
      onClick: () => {}
    },
    {
      label: 'Currency',
      description: 'Change display currency',
      icon: (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      ),
      onClick: () => {}
    },
    {
      label: 'Export Data',
      description: 'Download your financial data',
      icon: (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
        </svg>
      ),
      onClick: () => {}
    },
    {
      label: 'About',
      description: 'Version and app information',
      icon: (
        <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
      ),
      onClick: () => {}
    }
  ]

  return (
    <div className="p-4 space-y-6">
      {/* Header */}
      <h1 className="text-xl font-semibold text-text-primary">Settings</h1>

      {/* User Profile Card */}
      <div className="bg-surface rounded-xl border border-border p-4">
        <div className="flex items-center gap-4">
          <div className="w-14 h-14 rounded-full bg-primary/10 flex items-center justify-center">
            <span className="text-xl font-semibold text-primary">
              {user?.name?.charAt(0)?.toUpperCase() || 'U'}
            </span>
          </div>
          <div>
            <p className="font-medium text-text-primary">{user?.name || 'User'}</p>
            <p className="text-sm text-text-secondary">{user?.email || 'user@example.com'}</p>
          </div>
        </div>
      </div>

      {/* Settings List */}
      <div className="bg-surface rounded-xl border border-border divide-y divide-border">
        {settingsItems.map((item) => (
          <button
            key={item.label}
            onClick={item.onClick}
            className="w-full flex items-center gap-4 p-4 hover:bg-border-light transition-colors text-left"
          >
            <div className="text-text-secondary">{item.icon}</div>
            <div className="flex-1">
              <p className="font-medium text-text-primary">{item.label}</p>
              <p className="text-sm text-text-secondary">{item.description}</p>
            </div>
            <svg className="w-5 h-5 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        ))}
      </div>

      {/* Logout Button */}
      <button
        onClick={logout}
        className="w-full p-4 bg-danger/10 text-danger rounded-xl font-medium hover:bg-danger/20 transition-colors"
      >
        Sign Out
      </button>

      {/* Version Info */}
      <p className="text-center text-sm text-text-muted">
        MiiMoneyPal v0.1.0
      </p>
    </div>
  )
}
