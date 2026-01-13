import { formatCurrency } from '@/lib/utils'

/**
 * Buckets page - Investment buckets and savings goals
 * Shows bucket balances and progress towards goals
 */
export default function Buckets() {
  // Placeholder data - will be replaced with TanStack Query
  const buckets = []

  const totalBucketBalance = buckets.reduce((sum, b) => sum + b.balance, 0)

  return (
    <div className="p-4 space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-xl font-semibold text-text-primary">Buckets</h1>
        <p className="text-text-secondary text-sm mt-1">
          Total invested: {formatCurrency(totalBucketBalance)}
        </p>
      </div>

      {/* Bucket List */}
      {buckets.length === 0 ? (
        <div className="text-center py-12">
          <div className="w-16 h-16 mx-auto mb-4 rounded-full bg-border-light flex items-center justify-center">
            <svg className="w-8 h-8 text-text-muted" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
            </svg>
          </div>
          <h3 className="text-lg font-medium text-text-primary mb-1">No buckets yet</h3>
          <p className="text-text-secondary text-sm mb-4">Create a bucket to start saving towards your goals</p>
          <button className="px-4 py-2 bg-primary text-white rounded-lg font-medium hover:bg-primary-dark transition-colors">
            Create Bucket
          </button>
        </div>
      ) : (
        <div className="space-y-4">
          {buckets.map((bucket) => {
            const progress = bucket.targetAmount
              ? Math.min((bucket.balance / bucket.targetAmount) * 100, 100)
              : null

            return (
              <div
                key={bucket.id}
                className="p-4 bg-surface rounded-xl border border-border"
              >
                <div className="flex items-start justify-between mb-3">
                  <div>
                    <h3 className="font-medium text-text-primary">{bucket.name}</h3>
                    <p className="text-sm text-text-secondary capitalize">
                      {bucket.type === 'SAVINGS_GOAL' ? 'Savings Goal' : 'Perpetual Asset'}
                    </p>
                  </div>
                  <p className="font-semibold text-investment">{formatCurrency(bucket.balance)}</p>
                </div>

                {/* Progress bar for savings goals */}
                {progress !== null && (
                  <div>
                    <div className="flex justify-between text-xs text-text-secondary mb-1">
                      <span>{progress.toFixed(0)}% complete</span>
                      <span>Goal: {formatCurrency(bucket.targetAmount)}</span>
                    </div>
                    <div className="h-2 bg-border-light rounded-full overflow-hidden">
                      <div
                        className="h-full bg-primary rounded-full transition-all duration-300"
                        style={{ width: `${progress}%` }}
                      />
                    </div>
                  </div>
                )}
              </div>
            )
          })}
        </div>
      )}

      {/* FAB - Add Bucket */}
      <button className="fixed bottom-24 right-4 w-14 h-14 bg-investment text-white rounded-full shadow-lg flex items-center justify-center hover:bg-investment/90 transition-colors">
        <svg className="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
      </button>
    </div>
  )
}
