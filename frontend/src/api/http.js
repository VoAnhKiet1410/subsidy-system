import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: { 'Content-Type': 'application/json' },
})

// Gắn JWT token tự động vào mọi request
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// ─── Response interceptor: unwrap ApiResponse + refresh token ───────────────
let isRefreshing = false
let refreshQueue = []
let isRedirecting = false // Tránh redirect loop khi nhiều request 401 cùng lúc

function processQueue(error, token = null) {
  refreshQueue.forEach(cb => cb(error, token))
  refreshQueue = []
}

// Force logout: xóa auth state + redirect login
function forceLogout() {
  if (isRedirecting) return // Tránh loop
  isRedirecting = true

  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('refreshToken')

  // Nếu đã ở /login rồi thì không redirect nữa
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  } else {
    isRedirecting = false
  }
}

http.interceptors.response.use(
  (res) => {
    // Unwrap ApiResponse wrapper: { success, message, data } → trả về data
    if (res.data && typeof res.data === 'object' && 'success' in res.data) {
      res.data = res.data.data
    }
    return res
  },
  async (err) => {
    const originalRequest = err.config

    // 401 - Token hết hạn → thử refresh
    if (err.response?.status === 401 && !originalRequest._retry) {
      const refreshToken = localStorage.getItem('refreshToken')

      // Nếu có refreshToken → thử refresh
      if (refreshToken) {
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            refreshQueue.push((error, token) => {
              if (error) return reject(error)
              originalRequest.headers.Authorization = `Bearer ${token}`
              resolve(http(originalRequest))
            })
          })
        }

        originalRequest._retry = true
        isRefreshing = true

        try {
          const res = await axios.post('/api/auth/refresh', { refreshToken })
          const data = res.data?.data || res.data
          const newToken = data.accessToken || data.token

          if (newToken) {
            localStorage.setItem('token', newToken)
            if (data.refreshToken) {
              localStorage.setItem('refreshToken', data.refreshToken)
            }
            if (data.username) {
              localStorage.setItem('user', JSON.stringify(data))
            }

            http.defaults.headers.common.Authorization = `Bearer ${newToken}`
            processQueue(null, newToken)

            originalRequest.headers.Authorization = `Bearer ${newToken}`
            return http(originalRequest)
          }
        } catch (refreshErr) {
          processQueue(refreshErr, null)
          forceLogout()
          return Promise.reject(refreshErr)
        } finally {
          isRefreshing = false
        }
      }

      // Không có refreshToken → logout luôn
      forceLogout()
    }

    return Promise.reject(err)
  }
)

export default http

