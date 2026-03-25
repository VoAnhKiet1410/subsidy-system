import { reactive } from 'vue'
import { authApi } from '../api/auth'

// ─── Kiểm tra JWT đã hết hạn chưa ─────────────────────────────
function isTokenExpired(token) {
  if (!token) return true
  try {
    const parts = token.split('.')
    if (parts.length !== 3) return true
    const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')))
    // exp là Unix seconds, nhân 1000 để so với Date.now() (milliseconds)
    return payload.exp * 1000 < Date.now()
  } catch {
    return true
  }
}

// ─── Nếu token đã hết hạn khi load app → xóa luôn ─────────────
const savedToken = localStorage.getItem('token')
if (isTokenExpired(savedToken)) {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('refreshToken')
}

export const authStore = reactive({
  token: isTokenExpired(savedToken) ? null : savedToken,
  user: isTokenExpired(savedToken) ? null : JSON.parse(localStorage.getItem('user') || 'null'),

  get isLoggedIn() {
    return !!this.token
  },

  get role() {
    // Hỗ trợ cả single role string lẫn array roles từ backend mới
    const roles = this.user?.roles
    if (Array.isArray(roles) && roles.length > 0) return roles[0]
    return this.user?.role || 'CITIZEN'
  },

  get isAdmin() {
    return this.role === 'ADMIN'
  },

  get isOfficer() {
    return this.role === 'OFFICER'
  },

  get isAccountant() {
    return this.role === 'ACCOUNTANT'
  },

  get isCitizen() {
    return this.role === 'CITIZEN'
  },

  // Backward compat aliases
  get isReviewer() { return this.isOfficer },
  get isFinance() { return this.isAccountant },
  get isViewer() { return this.isCitizen },

  // Kiểm tra quyền theo nhóm chức năng
  get canManageApplications() {
    return this.isAdmin || this.isOfficer
  },

  get canManageTransactions() {
    return this.isAdmin || this.isAccountant
  },

  get canManagePrograms() {
    return this.isAdmin
  },

  get canManageUsers() {
    return this.isAdmin
  },

  get canManageBeneficiaries() {
    return this.isAdmin || this.isOfficer
  },

  async login(username, password) {
    const res = await authApi.login({ username, password })
    // Hỗ trợ cả response cũ (token) lẫn mới (data.accessToken)
    const data = res.data?.data || res.data
    this.token = data.accessToken || data.token
    this.user = data
    localStorage.setItem('token', this.token)
    localStorage.setItem('user', JSON.stringify(this.user))
    // Lưu refresh token nếu có
    if (data.refreshToken) {
      localStorage.setItem('refreshToken', data.refreshToken)
    }
  },

  logout() {
    this.token = null
    this.user = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('refreshToken')
  },

  async updateUser(profileData) {
    const res = await authApi.updateProfile(profileData)
    // Dữ liệu User thực nằm ở res.data.data
    const updatedUser = res.data?.data || res.data
    const updated = { ...this.user, ...updatedUser }
    this.user = updated
    localStorage.setItem('user', JSON.stringify(updated))
    return updated
  },

  updateUserLocal(profileData) {
    const updated = { ...this.user, ...profileData }
    this.user = updated
    localStorage.setItem('user', JSON.stringify(updated))
    return updated
  },
})
