<template>
  <aside class="w-64 h-screen sticky top-0 left-0 bg-surface-container-low flex flex-col font-body antialiased tracking-tight border-r border-outline-variant/10 overflow-y-auto">

    <!-- Logo -->
    <div class="px-5 pt-6 pb-4">
      <div class="flex items-center gap-3">
        <div class="w-9 h-9 bg-primary rounded-xl flex items-center justify-center shadow-md shadow-primary/30">
          <span class="material-symbols-outlined text-white text-lg" style="font-variation-settings:'FILL' 1;">account_balance</span>
        </div>
        <div>
          <h1 class="text-sm font-extrabold text-primary tracking-tight leading-tight">Quản lý Trợ Cấp</h1>
          <p class="text-[9px] text-on-surface-variant uppercase tracking-widest font-bold">Cổng hỗ trợ xã hội</p>
        </div>
      </div>
    </div>

    <!-- Nav Groups -->
    <nav class="flex-1 px-3 pb-3 space-y-5">

      <!-- Nhóm: Tổng quan -->
      <div>
        <p class="px-3 mb-1 text-[9px] font-black uppercase tracking-widest text-on-surface-variant/50">Tổng quan</p>
        <div class="space-y-0.5">
          <NavItem v-for="item in group('overview')" :key="item.path" :item="item" :active="isActive(item.path)" />
        </div>
      </div>

      <!-- Nhóm: Nghiệp vụ -->
      <div>
        <p class="px-3 mb-1 text-[9px] font-black uppercase tracking-widest text-on-surface-variant/50">Nghiệp vụ</p>
        <div class="space-y-0.5">
          <NavItem v-for="item in group('operations')" :key="item.path" :item="item" :active="isActive(item.path)" />
        </div>
      </div>

      <!-- Nhóm: Tài chính (ẩn với CITIZEN) -->
      <div v-if="showFinanceGroup">
        <p class="px-3 mb-1 text-[9px] font-black uppercase tracking-widest text-on-surface-variant/50">Tài chính</p>
        <div class="space-y-0.5">
          <NavItem v-for="item in group('finance')" :key="item.path" :item="item" :active="isActive(item.path)" />
        </div>
      </div>

      <!-- Nhóm: Quản trị (ADMIN only) -->
      <div v-if="authStore.isAdmin">
        <p class="px-3 mb-1 text-[9px] font-black uppercase tracking-widest text-on-surface-variant/50">Quản trị</p>
        <div class="space-y-0.5">
          <NavItem v-for="item in group('admin')" :key="item.path" :item="item" :active="isActive(item.path)" />
        </div>
      </div>

    </nav>

    <!-- Footer: user info + actions -->
    <div class="border-t border-outline-variant/15 px-3 py-3 space-y-0.5">
      <!-- Thông báo -->
      <NavItem :item="notifItem" :active="isActive('/thong-bao')" />
      <!-- Hồ sơ cá nhân -->
      <NavItem :item="profileItem" :active="isActive('/ho-so-ca-nhan')" />
      <!-- Cài đặt (ADMIN) -->
      <NavItem v-if="authStore.isAdmin" :item="settingsItem" :active="isActive('/cai-dat')" />

      <!-- User card -->
      <div v-if="authStore.user"
        class="mt-2 flex items-center gap-3 px-3 py-2.5 rounded-xl bg-white/60 border border-outline-variant/10">
        <div :class="['w-8 h-8 rounded-lg flex items-center justify-center text-xs font-black flex-shrink-0', roleBgColor]">
          {{ userInitials }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-xs font-bold text-on-surface truncate">{{ authStore.user.fullName || authStore.user.username }}</p>
          <p class="text-[10px] text-on-surface-variant truncate">{{ roleLabel }}</p>
        </div>
        <button @click="handleLogout"
          class="w-7 h-7 rounded-lg flex items-center justify-center text-on-surface-variant hover:text-red-500 hover:bg-red-50 transition-colors flex-shrink-0"
          title="Đăng xuất">
          <span class="material-symbols-outlined text-base">logout</span>
        </button>
      </div>
    </div>

  </aside>
</template>

<script setup>
import { computed, h, resolveComponent } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { authStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()

// ─── Inline NavItem component ───────────────────────────────────────────────
const NavItem = {
  props: ['item', 'active'],
  setup(props) {
    return () => h(RouterLink, {
      to: props.item.path,
      class: [
        'flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium transition-all duration-150',
        props.active
          ? 'bg-white text-primary shadow-sm font-semibold'
          : 'text-on-surface-variant hover:bg-white/60 hover:text-on-surface'
      ].join(' ')
    }, () => [
      h('span', {
        class: 'material-symbols-outlined text-[19px] flex-shrink-0',
        style: props.active ? "font-variation-settings:'FILL' 1;" : ''
      }, props.item.icon),
      h('span', { class: 'truncate flex-1' }, props.item.label),
      props.item.badge
        ? h('span', { class: 'px-1.5 py-0.5 bg-primary text-white text-[9px] font-black rounded-full flex-shrink-0' }, props.item.badge)
        : null
    ])
  }
}

// ─── Nav định nghĩa ─────────────────────────────────────────────────────────
const allNavItems = [
  // overview
  { path: '/', icon: 'dashboard', label: 'Bảng điều khiển', group: 'overview', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] },

  // operations
  { path: '/chuong-trinh', icon: 'assignment', label: authStore.isCitizen ? 'Chương trình trợ cấp' : 'Quản lý chương trình', group: 'operations', roles: ['ADMIN', 'CITIZEN'] },
  { path: '/ho-so', icon: 'folder_shared', label: authStore.isCitizen ? 'Hồ sơ của tôi' : 'Quản lý hồ sơ', group: 'operations', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT', 'CITIZEN'] },
  { path: '/xet-duyet-ai', icon: 'psychology', label: 'Xét duyệt AI', group: 'operations', roles: ['ADMIN', 'OFFICER'] },
  { path: '/doi-tuong', icon: 'groups', label: 'Đối tượng', group: 'operations', roles: ['ADMIN', 'OFFICER'] },

  // finance
  { path: '/ngan-sach', icon: 'account_balance_wallet', label: 'Nguồn quỹ & Chi trả', group: 'finance', roles: ['ADMIN', 'ACCOUNTANT'] },
  { path: '/bao-cao', icon: 'analytics', label: 'Báo cáo', group: 'finance', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT'] },

  // admin
  { path: '/nguoi-dung', icon: 'manage_accounts', label: 'Quản lý người dùng', group: 'admin', roles: ['ADMIN'] },
]

function group(g) {
  return allNavItems.filter(it => it.group === g && it.roles.includes(authStore.role))
}

const showFinanceGroup = computed(() =>
  allNavItems.some(it => it.group === 'finance' && it.roles.includes(authStore.role))
)

// fixed footer items
const notifItem   = { path: '/thong-bao',    icon: 'notifications',  label: 'Thông báo' }
const profileItem = { path: '/ho-so-ca-nhan', icon: 'account_circle', label: 'Hồ sơ cá nhân' }
const settingsItem = { path: '/cai-dat',      icon: 'settings',       label: 'Cài đặt hệ thống' }

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}

const userInitials = computed(() => {
  const n = authStore.user?.fullName || authStore.user?.username || ''
  return n.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase() || '?'
})

const roleLabels = { ADMIN: 'Quản trị viên', OFFICER: 'Cán bộ xét duyệt', ACCOUNTANT: 'Cán bộ tài chính', CITIZEN: 'Người dân' }
const roleLabel  = computed(() => roleLabels[authStore.role] || authStore.role || '')

const roleBgColors = { ADMIN: 'bg-red-100 text-red-700', OFFICER: 'bg-amber-100 text-amber-700', ACCOUNTANT: 'bg-blue-100 text-blue-700', CITIZEN: 'bg-emerald-100 text-emerald-700' }
const roleBgColor  = computed(() => roleBgColors[authStore.role] || 'bg-slate-100 text-slate-600')
</script>
