import http from './http'

export const dashboardApi = {
  getStats: () => http.get('/dashboard/stats'),
  getBeneficiaryCounts: () => http.get('/dashboard/beneficiary-counts'),
}
