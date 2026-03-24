import { createRouter, createWebHistory } from 'vue-router'
import { authStore } from '../stores/auth'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true, title: 'Đăng nhập' }
  },
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { title: 'Bảng điều khiển', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] }
  },
  {
    path: '/ho-so',
    name: 'beneficiaries',
    component: () => import('../views/BeneficiariesView.vue'),
    meta: { title: 'Hồ sơ đối tượng', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] }
  },
  {
    path: '/ho-so/:id',
    name: 'application-detail',
    component: () => import('../views/ApplicationDetailView.vue'),
    meta: { title: 'Chi tiết hồ sơ', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] }
  },
  {
    path: '/xet-duyet-ai',
    name: 'ai-review',
    component: () => import('../views/AiReviewView.vue'),
    meta: { title: 'Xét duyệt AI', roles: ['ADMIN', 'OFFICER'] }
  },
  {
    path: '/ngan-sach',
    name: 'budget',
    component: () => import('../views/BudgetView.vue'),
    meta: { title: 'Nguồn quỹ hỗ trợ', roles: ['ADMIN', 'ACCOUNTANT'] }
  },
  {
    path: '/bao-cao',
    name: 'reports',
    component: () => import('../views/ReportsView.vue'),
    meta: { title: 'Báo cáo hệ thống', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT'] }
  },
  {
    path: '/nguoi-dung',
    name: 'users',
    component: () => import('../views/UsersView.vue'),
    meta: { title: 'Quản lý Người dùng', roles: ['ADMIN'] }
  },
  {
    path: '/doi-tuong',
    name: 'doi-tuong',
    component: () => import('../views/DoiTuongView.vue'),
    meta: { title: 'Đối tượng hưởng trợ cấp', roles: ['ADMIN', 'OFFICER'] }
  },
  {
    path: '/ho-so-ca-nhan',
    name: 'profile',
    component: () => import('../views/ProfileView.vue'),
    meta: { title: 'Hồ sơ cá nhân', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] }
  },
  {
    path: '/nhat-ky',
    name: 'activity-log',
    component: () => import('../views/ActivityLogView.vue'),
    meta: { title: 'Nhật ký hoạt động', roles: ['ADMIN', 'OFFICER'] }
  },
  {
    path: '/cai-dat',
    name: 'settings',
    component: () => import('../views/SettingsView.vue'),
    meta: { title: 'Cài đặt & Phân quyền', roles: ['ADMIN'] }
  },
  {
    path: '/chuong-trinh',
    name: 'programs',
    component: () => import('../views/ProgramsView.vue'),
    meta: { title: 'Quản lý chương trình', roles: ['ADMIN', 'CITIZEN'] }
  },
  {
    path: '/thong-bao',
    name: 'notifications',
    component: () => import('../views/NotificationsView.vue'),
    meta: { title: 'Thông báo', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] }
  },
  {
    path: '/portal',
    component: () => import('../components/PortalLayout.vue'),
    meta: { public: false },
    children: [
      { path: '',                name:'portal-home',    component: () => import('../views/portal/PortalHomeView.vue'),    meta: { title:'Trang chủ' } },
      { path: 'chuong-trinh',    name:'portal-programs',component: () => import('../views/portal/PortalProgramsView.vue'), meta: { title:'Chương trình' } },
      { path: 'nop-ho-so',       name:'portal-apply',   component: () => import('../views/portal/PortalApplyView.vue'),   meta: { title:'Nộp hồ sơ' } },
      { path: 'ho-so-cua-toi',   name:'portal-myapps',  component: () => import('../views/portal/PortalMyAppsView.vue'),  meta: { title:'Hồ sơ của tôi' } },
      { path: 'ho-so-cua-toi/:id',name:'portal-appdetail',component:() => import('../views/portal/PortalAppDetailView.vue'), meta: { title:'Chi tiết hồ sơ' } },
      { path: 'thong-bao',       name:'portal-notify',  component: () => import('../views/portal/PortalNotifyView.vue'),  meta: { title:'Thông báo' } },
      { path: 'ca-nhan',         name:'portal-profile', component: () => import('../views/portal/PortalProfileView.vue'), meta: { title:'Cá nhân' } },
    ]
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Auth + Role guard
router.beforeEach((to) => {
  document.title = `${to.meta.title || 'Trang chủ'} - Hệ thống Trợ cấp`

  // Trang public không cần login
  if (to.meta.public) {
    if (to.name === 'login' && authStore.isLoggedIn) {
      // CITIZEN → portal, còn lại → dashboard
      return authStore.role === 'CITIZEN' ? { name: 'portal-home' } : { name: 'dashboard' }
    }
    return
  }

  // Chưa đăng nhập → redirect login
  if (!authStore.isLoggedIn) {
    return { name: 'login' }
  }

  // CITIZEN truy cập admin routes → redirect sang portal
  const isPortalRoute = to.path.startsWith('/portal')
  if (authStore.role === 'CITIZEN' && !isPortalRoute) {
    return { name: 'portal-home' }
  }

  // Kiểm tra role — nếu route có meta.roles, user phải có role phù hợp
  if (to.meta.roles && !to.meta.roles.includes(authStore.role)) {
    // CITIZEN lạc vào admin → về portal
    if (authStore.role === 'CITIZEN') return { name: 'portal-home' }
    return { name: 'dashboard' }
  }
})

export default router
