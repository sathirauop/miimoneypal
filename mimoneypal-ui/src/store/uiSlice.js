import { createSlice } from '@reduxjs/toolkit'

const initialState = {
  // Drawer states
  isAddTransactionOpen: false,
  isFilterDrawerOpen: false,

  // Active filters
  transactionFilters: {
    type: null, // INCOME, EXPENSE, INVESTMENT, WITHDRAWAL
    categoryId: null,
    bucketId: null,
    startDate: null,
    endDate: null
  },

  // Dashboard view
  selectedMonth: new Date().getMonth() + 1,
  selectedYear: new Date().getFullYear()
}

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    // Drawer actions
    openAddTransaction: (state) => {
      state.isAddTransactionOpen = true
    },
    closeAddTransaction: (state) => {
      state.isAddTransactionOpen = false
    },
    openFilterDrawer: (state) => {
      state.isFilterDrawerOpen = true
    },
    closeFilterDrawer: (state) => {
      state.isFilterDrawerOpen = false
    },

    // Filter actions
    setTransactionFilters: (state, action) => {
      state.transactionFilters = { ...state.transactionFilters, ...action.payload }
    },
    clearTransactionFilters: (state) => {
      state.transactionFilters = initialState.transactionFilters
    },

    // Dashboard view actions
    setSelectedMonth: (state, action) => {
      state.selectedMonth = action.payload
    },
    setSelectedYear: (state, action) => {
      state.selectedYear = action.payload
    },
    navigateMonth: (state, action) => {
      const direction = action.payload // 1 for next, -1 for previous
      let newMonth = state.selectedMonth + direction
      let newYear = state.selectedYear

      if (newMonth > 12) {
        newMonth = 1
        newYear += 1
      } else if (newMonth < 1) {
        newMonth = 12
        newYear -= 1
      }

      state.selectedMonth = newMonth
      state.selectedYear = newYear
    }
  }
})

export const {
  openAddTransaction,
  closeAddTransaction,
  openFilterDrawer,
  closeFilterDrawer,
  setTransactionFilters,
  clearTransactionFilters,
  setSelectedMonth,
  setSelectedYear,
  navigateMonth
} = uiSlice.actions

export default uiSlice.reducer

// Selectors
export const selectTransactionFilters = (state) => state.ui.transactionFilters
export const selectSelectedMonth = (state) => state.ui.selectedMonth
export const selectSelectedYear = (state) => state.ui.selectedYear
export const selectIsAddTransactionOpen = (state) => state.ui.isAddTransactionOpen
