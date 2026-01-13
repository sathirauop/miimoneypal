import { formatCurrency, formatDate, getTransactionTypeProps } from '@/lib/utils'

/**
 * Transactions page - List and manage all transactions
 * Supports filtering by type, category, bucket, and date range
 */
export default function Transactions() {
  // Placeholder data - will be replaced with TanStack Query
  const transactions = []

  return (
    <div className="p-4 space-y-4">
      {/* Header */}
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-text-primary">Transactions</h1>
        <button className="p-2 rounded-lg hover:bg-border-light transition-colors">
          <svg className="w-5 h-5 text-text-secondary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z" />
          </svg>
        </button>
      </div>

      {/* Transaction List */}
      {transactions.length === 0 ? (
        <div className="text-center py-12">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-border-light flex items-center justify-center">
            <svg className="w-8 h-8 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-text-primary mb-1">No transactions yet</h3>
          <p className="text-text-secondary text-sm">Add your first transaction to get started</p>
        </div>
      ) : (
        <div className="space-y-2">
          {transactions.map((transaction) => {
            const typeProps = getTransactionTypeProps(transaction.type)
            return (
              <div
                key={transaction.id}
                className="flex items-center justify-between p-4 bg-surface rounded-xl border border-border"
              >
                <div className="flex items-center gap-3">
                  <div className={`w-10 h-10 rounded-full ${typeProps.bgColor} flex items-center justify-center`}>
                    <span className={`text-lg ${typeProps.color}`}>{typeProps.sign}</span>
                  </div>
                  <div>
                    <p className="font-medium text-text-primary">{transaction.description}</p>
                    <p className="text-sm text-text-secondary">
                      {transaction.category?.name} • {formatDate(transaction.date)}
                    </p>
                  </div>
                </div>
                <p className={`font-semibold ${typeProps.color}`}>
                  {typeProps.sign}{formatCurrency(transaction.amount)}
                </p>
              </div>
            )
          })}
        </div>
      )}

      {/* FAB - Add Transaction */}
      <button className="fixed bottom-24 right-4 w-14 h-14 bg-primary text-white rounded-full shadow-lg flex items-center justify-center hover:bg-primary-dark transition-colors">
        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
      </button>
    </div>
  )
}
