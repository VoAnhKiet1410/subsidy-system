import http from './http'

export const usersApi = {
  getAll: () => http.get('/users'),
  getById: (id) => http.get(`/users/${id}`),
  update: (id, data) => http.put(`/users/${id}`, data),
  delete: (id) => http.delete(`/users/${id}`),
}
