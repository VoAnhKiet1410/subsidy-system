import http from './http'

export const programsApi = {
  // chuong_trinh_tru_cap
  getAll: (params) => http.get('/programs', { params }),
  getById: (id) => http.get(`/programs/${id}`),
  filter: (status) => http.get('/programs/filter', { params: { status } }),
  create: (data) => http.post('/programs', data),
  update: (id, data) => http.put(`/programs/${id}`, data),
  delete: (id) => http.delete(`/programs/${id}`),

  // nguon_quy
  getFunds: () => http.get('/funding-sources'),
  createFund: (data) => http.post('/funding-sources', data),
  updateFund: (id, data) => http.put(`/funding-sources/${id}`, data),

  // doi_tuong_huong_tru_cap
  getDoiTuong: () => http.get('/beneficiaries'),
}
