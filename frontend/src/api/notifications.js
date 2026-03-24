import http from './http'

export const notificationsApi = {
  // thong_bao
  getAll: (page = 0, size = 20) => http.get('/notifications', { params: { page, size } }),
  getUnreadCount: () => http.get('/notifications/unread-count'),
  markRead: (id) => http.patch(`/notifications/${id}/read`),
  markAllRead: () => http.patch('/notifications/read-all'),
  delete: (id) => http.delete(`/notifications/${id}`),
}
