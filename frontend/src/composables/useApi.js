import { ref } from 'vue'
import { useUI } from '../stores/ui'

/**
 * Composable bao bọc API call với loading, error, toast tự động.
 *
 * Cách dùng:
 *   const { data, loading, error, execute } = useApi(programsApi.getAll)
 *   await execute()    // data.value = response data
 *
 *   // Hoặc dùng nhanh:
 *   const { data, loading, run } = useApiCall()
 *   await run(() => programsApi.getAll(), 'Đã tải chương trình')
 */

// Wrapper cho 1 API function cố định
export function useApi(apiFn, options = {}) {
  const {
    immediate = false,
    successMsg = null,
    errorMsg   = 'Có lỗi xảy ra, vui lòng thử lại',
    transform  = null,       // (resData) => transformedData
  } = options

  const data    = ref(null)
  const loading = ref(false)
  const error   = ref(null)

  async function execute(...args) {
    loading.value = true
    error.value   = null
    try {
      const res = await apiFn(...args)
      data.value = transform ? transform(res.data) : res.data
      if (successMsg) {
        const ui = useUI()
        ui.showSuccess(successMsg)
      }
      return data.value
    } catch (err) {
      error.value = err
      const ui = useUI()
      const msg = err.response?.data?.message || errorMsg
      ui.showError(msg)
      throw err
    } finally {
      loading.value = false
    }
  }

  if (immediate) execute()

  return { data, loading, error, execute }
}

// Wrapper linh hoạt — gọi bất kỳ API function nào
export function useApiCall() {
  const data    = ref(null)
  const loading = ref(false)
  const error   = ref(null)

  async function run(apiFn, successMsg = null) {
    loading.value = true
    error.value   = null
    try {
      const res = await apiFn()
      data.value = res.data
      if (successMsg) {
        const ui = useUI()
        ui.showSuccess(successMsg)
      }
      return data.value
    } catch (err) {
      error.value = err
      const ui = useUI()
      ui.showError(err.response?.data?.message || 'Có lỗi xảy ra')
      throw err
    } finally {
      loading.value = false
    }
  }

  return { data, loading, error, run }
}
