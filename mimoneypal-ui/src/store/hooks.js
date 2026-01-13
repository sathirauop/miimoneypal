import { useDispatch, useSelector } from 'react-redux'

/**
 * Typed dispatch hook
 * @returns {import('@reduxjs/toolkit').Dispatch}
 */
export const useAppDispatch = useDispatch

/**
 * Typed selector hook
 * @template T
 * @param {(state: import('./index').RootState) => T} selector
 * @returns {T}
 */
export const useAppSelector = useSelector
