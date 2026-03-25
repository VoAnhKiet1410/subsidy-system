import http from './http'

export const applicationsApi = {
  // ho_so_ho_tro — CRUD
  getAll:    (page = 0, size = 20) => http.get('/applications', { params: { page, size } }),
  getById:   (id) => http.get(`/applications/${id}`),
  getStats:  ()   => http.get('/applications/stats'),
  create:    (data) => http.post('/applications', data),
  update:    (id, data) => http.put(`/applications/${id}`, data),
  delete:    (id) => http.delete(`/applications/${id}`),
  submit:    (id) => http.patch(`/applications/${id}/submit`),
  approve:   (id) => http.patch(`/applications/${id}/approve`),
  reject:    (id, reason) => http.patch(`/applications/${id}/reject`, { lyDoTuChoi: reason }),

  // tai_lieu_dinh_kem
  getDocuments:    (hoSoId) => http.get(`/applications/${hoSoId}/attachments`),
  uploadDocument:  (hoSoId, formData) => http.post(`/applications/${hoSoId}/attachments`, formData, { headers: { 'Content-Type': 'multipart/form-data' } }),
  deleteDocument:  (docId) => http.delete(`/attachments/${docId}`),

  // danh_gia_ai
  getAiReview:     (hoSoId) => http.get(`/applications/${hoSoId}/ai-review`),
  triggerAiReview: (hoSoId) => http.post(`/applications/${hoSoId}/ai-review`),

  // Portal — hồ sơ của user
  getMyStats:        () => http.get('/applications/stats/my'),
  getMyApplications: (page = 0, size = 20) => http.get('/applications/my', { params: { page, size } }),

  // chi_tra
  getPaymentsByApplication: (id) => http.get(`/payments/application/${id}`),
  createPayment:     (data) => http.post('/payments', data),
  updatePaymentStatus: (id, data) => http.patch(`/payments/${id}/status`, data),
}

// Backward-compatible alias
export const beneficiariesApi = applicationsApi
