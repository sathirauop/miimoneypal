import { clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Merge Tailwind CSS classes with proper precedence
 * @param  {...any} inputs - Class names to merge
 * @returns {string} Merged class string
 */
export function cn(...inputs) {
  return twMerge(clsx(inputs))
}

/**
 * Format a number as currency
 * @param {number} amount - Amount to format
 * @param {string} currency - Currency code (default: 'USD')
 * @returns {string} Formatted currency string
 */
export function formatCurrency(amount, currency = 'USD') {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(amount)
}

/**
 * Format a date for display
 * @param {string|Date} date - Date to format
 * @param {object} options - Intl.DateTimeFormat options
 * @returns {string} Formatted date string
 */
export function formatDate(date, options = {}) {
  const defaultOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }
  return new Intl.DateTimeFormat('en-US', { ...defaultOptions, ...options }).format(new Date(date))
}

/**
 * Format a date as relative time (e.g., "2 days ago")
 * @param {string|Date} date - Date to format
 * @returns {string} Relative time string
 */
export function formatRelativeTime(date) {
  const now = new Date()
  const then = new Date(date)
  const diffInSeconds = Math.floor((now - then) / 1000)

  const intervals = [
    { label: 'year', seconds: 31536000 },
    { label: 'month', seconds: 2592000 },
    { label: 'week', seconds: 604800 },
    { label: 'day', seconds: 86400 },
    { label: 'hour', seconds: 3600 },
    { label: 'minute', seconds: 60 }
  ]

  for (const interval of intervals) {
    const count = Math.floor(diffInSeconds / interval.seconds)
    if (count >= 1) {
      return `${count} ${interval.label}${count > 1 ? 's' : ''} ago`
    }
  }

  return 'just now'
}

/**
 * Get transaction type display properties
 * @param {string} type - Transaction type (INCOME, EXPENSE, INVESTMENT, WITHDRAWAL)
 * @returns {object} Display properties (label, color, sign)
 */
export function getTransactionTypeProps(type) {
  const props = {
    INCOME: { label: 'Income', color: 'text-income', sign: '+', bgColor: 'bg-income/10' },
    EXPENSE: { label: 'Expense', color: 'text-expense', sign: '-', bgColor: 'bg-expense/10' },
    INVESTMENT: { label: 'Investment', color: 'text-investment', sign: '-', bgColor: 'bg-investment/10' },
    WITHDRAWAL: { label: 'Withdrawal', color: 'text-withdrawal', sign: '+', bgColor: 'bg-withdrawal/10' }
  }
  return props[type] || { label: type, color: 'text-text-secondary', sign: '', bgColor: 'bg-gray-100' }
}
