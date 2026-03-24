import http from './http'

export const dashboardApi = {
  getStats: () => http.get('/dashboard/stats'),
}
