import { useAppSelector, useAppDispatch } from '@/store/hooks'
import { selectSelectedMonth, selectSelectedYear, navigateMonth } from '@/store/uiSlice'
import { formatCurrency } from '@/lib/utils'

/**
 * Dashboard page - Monthly cash flow overview
 * Shows opening balance, income, expenses, investments, and closing balance
 */
export default function Dashboard() {
  const dispatch = useAppDispatch()
  const selectedMonth = useAppSelector(selectSelectedMonth)
  const selectedYear = useAppSelector(selectSelectedYear)

  const monthName = new Date(selectedYear, selectedMonth - 1).toLocaleString('default', {
    month: 'long',
    year: 'numeric'
  })

  // Placeholder data - will be replaced with TanStack Query
  const dashboardData = {
    openingBalance: 0,
    totalIncome: 0,
    totalExpenses: 0,
    totalInvestments: 0,
    totalWithdrawals: 0,
    closingBalance: 0,
    usableAmount: 0
  }

  return (
    <div className="p-4 space-y-6">
      {/* Month Navigation */}
      <div className="flex items-center justify-between">
        <button
          onClick={() => dispatch(navigateMonth(-1))}
          className="p-2 rounded-lg hover:bg-border-light transition-colors"
          aria-label="Previous month"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
        </button>
        <h1 className="text-xl font-semibold text-text-primary">{monthName}</h1>
        <button
          onClick={() => dispatch(navigateMonth(1))}
          className="p-2 rounded-lg hover:bg-border-light transition-colors"
          aria-label="Next month"
        >
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          </svg>
        </button>
      </div>

      {/* Usable Amount Card */}
      <div className="bg-primary rounded-2xl p-6 text-white">
        <p className="text-sm opacity-80">Usable Amount</p>
        <p className="text-3xl font-bold mt-1">{formatCurrency(dashboardData.usableAmount)}</p>
        <p className="text-xs mt-2 opacity-70">
          Available for spending or investing
        </p>
      </div>

      {/* Cash Flow Summary */}
      <div className="bg-surface rounded-2xl p-4 space-y-4 border border-border">
        <h2 className="font-semibold text-text-primary">Monthly Summary</h2>

        <div className="space-y-3">
          {/* Opening Balance */}
          <div className="flex justify-between items-center py-2 border-b border-border-light">
            <span className="text-text-secondary">Opening Balance</span>
            <span className="font-medium">{formatCurrency(dashboardData.openingBalance)}</span>
          </div>

          {/* Income */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 rounded-full bg-income"></div>
              <span className="text-text-secondary">Income</span>
            </div>
            <span className="font-medium text-income">+{formatCurrency(dashboardData.totalIncome)}</span>
          </div>

          {/* Expenses */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 rounded-full bg-expense"></div>
              <span className="text-text-secondary">Expenses</span>
            </div>
            <span className="font-medium text-expense">-{formatCurrency(dashboardData.totalExpenses)}</span>
          </div>

          {/* Investments */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 rounded-full bg-investment"></div>
              <span className="text-text-secondary">Investments</span>
            </div>
            <span className="font-medium text-investment">-{formatCurrency(dashboardData.totalInvestments)}</span>
          </div>

          {/* Withdrawals */}
          <div className="flex justify-between items-center">
            <div className="flex items-center gap-2">
              <div className="w-2 h-2 rounded-full bg-withdrawal"></div>
              <span className="text-text-secondary">Withdrawals</span>
            </div>
            <span className="font-medium text-withdrawal">+{formatCurrency(dashboardData.totalWithdrawals)}</span>
          </div>

          {/* Closing Balance */}
          <div className="flex justify-between items-center pt-2 border-t border-border-light">
            <span className="text-text-secondary">Closing Balance</span>
            <span className="font-bold text-lg">{formatCurrency(dashboardData.closingBalance)}</span>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-2 gap-3">
        <button className="flex items-center justify-center gap-2 p-4 bg-income/10 text-income rounded-xl font-medium hover:bg-income/20 transition-colors">
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Add Income
        </button>
        <button className="flex items-center justify-center gap-2 p-4 bg-expense/10 text-expense rounded-xl font-medium hover:bg-expense/20 transition-colors">
          <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
          </svg>
          Add Expense
        </button>
      </div>
    </div>
  )
}
