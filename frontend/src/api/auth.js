import http from './http'

export const authApi = {
  login:          (data) => http.post('/auth/login', data),
  register:       (data) => http.post('/auth/register', data),
  refreshToken:   (refreshToken) => http.post('/auth/refresh', { refreshToken }),
  updateProfile:  (data) => http.put('/auth/profile', data),
  changePassword: (data) => http.put('/auth/change-password', data),
}
