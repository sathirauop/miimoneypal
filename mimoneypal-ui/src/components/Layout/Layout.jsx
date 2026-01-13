import { Outlet } from 'react-router-dom'
import BottomNav from './BottomNav'

/**
 * Main layout component with bottom navigation
 * Uses mobile-first design with fixed bottom nav
 */
export default function Layout() {
  return (
    <div className="flex flex-col h-[100dvh] bg-background">
      {/* Main content area - scrollable with bottom padding for nav */}
      <main className="flex-1 overflow-y-auto pb-20 scrollbar-hide">
        <Outlet />
      </main>

      {/* Fixed bottom navigation */}
      <BottomNav />
    </div>
  )
}
