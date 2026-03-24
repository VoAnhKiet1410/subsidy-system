import http from './http'

export const transactionsApi = {
  // chi_tra_tru_cap
  getAll: (page = 0, size = 20) => http.get('/payments', { params: { page, size } }),
  getById: (id) => http.get(`/payments/${id}`),
  create: (data) => http.post('/payments', data),
  updateStatus: (id, status) => http.patch(`/payments/${id}/status`, { trangThai: status }),
}
