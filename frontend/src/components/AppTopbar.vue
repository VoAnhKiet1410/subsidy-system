<template>
  <header class="sticky top-0 w-full z-40 bg-white/95 backdrop-blur-sm flex justify-between items-center px-8 h-14 border-b border-slate-100">
    <!-- Left: Search + Nav -->
    <div class="flex items-center gap-6 flex-1">
      <div class="relative max-w-md w-full">
        <span class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-secondary/60 text-xl">search</span>
        <input
          v-model="searchQuery"
          @keydown.enter="handleSearch"
          type="text"
          class="w-full bg-[#f0f4f8] border-none rounded-full py-2.5 pl-12 pr-4 text-sm focus:ring-2 focus:ring-primary/20 outline-none placeholder:text-secondary/60 transition-all"
          placeholder="Tìm kiếm cấu hình hoặc người dùng"
        />
      </div>
      <nav class="flex items-center gap-8 ml-4">
        <router-link
          v-for="link in topNavLinks"
          :key="link.path"
          :to="link.path"
          class="relative h-14 flex items-center"
        >
          <span
            :class="[
              'font-semibold text-[15px] transition-colors',
              isTopNavActive(link.path) ? 'text-primary' : 'text-secondary hover:text-primary'
            ]"
          >{{ link.label }}</span>
          <div
            v-if="isTopNavActive(link.path)"
            class="absolute bottom-0 left-0 w-full h-[3px] bg-primary rounded-t-full"
          ></div>
        </router-link>
      </nav>
    </div>

    <!-- Right: Actions -->
    <div class="flex items-center gap-4">
      <div class="h-6 w-[1px] bg-slate-200 mr-2"></div>

      <!-- AI Support -->
      <button
        @click="goToAiReview"
        class="flex items-center gap-2 px-4 py-2 bg-[#f0f7ff] text-primary rounded-xl font-bold hover:bg-primary/10 transition-colors"
      >
        <span class="material-symbols-outlined text-[20px]" style="font-variation-settings: 'FILL' 1;">auto_awesome</span>
        <span class="text-[15px]">Hỗ trợ AI</span>
      </button>

      <!-- Notifications -->
      <div class="relative" data-notif-panel>
        <button @click="toggleNotifications" class="relative p-2 text-secondary hover:text-primary transition-colors">
          <span class="material-symbols-outlined text-2xl" style="font-variation-settings: 'FILL' 1;">notifications</span>
          <span v-if="unreadCount > 0" class="absolute top-1.5 right-1.5 min-w-[16px] h-4 bg-red-500 border-2 border-white rounded-full text-white text-[9px] font-bold flex items-center justify-center px-0.5">
            {{ unreadCount > 9 ? '9+' : unreadCount }}
          </span>
        </button>

        <!-- Notification dropdown -->
        <div v-if="showNotifications" class="scale-in absolute right-0 top-12 w-96 bg-white rounded-2xl shadow-2xl border border-outline-variant/20 z-50 overflow-hidden">
          <div class="px-4 py-3 border-b border-outline-variant/10 flex justify-between items-center">
            <div>
              <h4 class="font-bold text-sm">Thông báo</h4>
              <p class="text-[11px] text-on-surface-variant">{{ unreadCount > 0 ? `${unreadCount} chưa đọc` : 'Đã đọc hết' }}</p>
            </div>
            <button v-if="unreadCount > 0" @click="markAllRead" class="text-xs text-primary font-semibold hover:underline">Đánh dấu đã đọc</button>
          </div>
          <div class="max-h-80 overflow-y-auto">
            <div v-if="notifications.length === 0" class="p-8 text-center">
              <span class="material-symbols-outlined text-3xl text-primary/20 block mb-2">notifications_off</span>
              <p class="text-sm text-on-surface-variant">Không có thông báo</p>
            </div>
            <div
              v-for="n in notifications"
              :key="n.id"
              @click="handleNotificationClick(n)"
              class="px-4 py-3 hover:bg-surface-container-low transition-colors border-b border-outline-variant/10 last:border-0 cursor-pointer"
              :class="!n.read ? 'bg-blue-50/40' : ''"
            >
              <div class="flex items-start gap-3">
                <span :class="['material-symbols-outlined text-lg mt-0.5', iconClassFor(n.type)]" style="font-variation-settings: 'FILL' 1;">{{ iconFor(n.type) }}</span>
                <div class="flex-1 min-w-0">
                  <p class="text-sm font-semibold text-on-surface leading-tight">{{ n.title }}</p>
                  <p class="text-[12px] text-on-surface-variant mt-0.5 leading-relaxed line-clamp-2">{{ n.message }}</p>
                  <p class="text-[11px] text-on-surface-variant/70 mt-1">{{ timeAgo(n.createdAt) }}</p>
                </div>
                <span v-if="!n.read" class="w-2 h-2 bg-primary rounded-full shrink-0 mt-1"></span>
              </div>
            </div>
          </div>
          <div class="px-4 py-3 border-t border-outline-variant/10 bg-surface-container-low/50">
            <button @click="goToAllNotifications" class="w-full text-center text-sm font-semibold text-primary hover:underline py-1">
              Xem tất cả thông báo →
            </button>
          </div>
        </div>
      </div>

      <!-- Help -->
      <button @click="goToSettings" class="p-2 text-secondary hover:text-primary transition-colors">
        <span class="material-symbols-outlined text-2xl" style="font-variation-settings: 'FILL' 1;">help</span>
      </button>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { notificationsApi } from '../api/notifications'
import { authStore } from '../stores/auth'

const route = useRoute()
const router = useRouter()

// --- Search ---
const searchQuery = ref('')

function handleSearch() {
  const q = searchQuery.value.trim()
  if (q) {
    router.push({ path: '/ho-so', query: { q } })
  }
}

// --- Top nav (lọc theo role) ---
const allTopNavLinks = [
  { path: '/bao-cao', label: 'Báo cáo', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT'] },
  { path: '/chuong-trinh', label: 'Chương trình', roles: ['ADMIN', 'CITIZEN'] },
  { path: '/ho-so', label: 'Người dùng', roles: ['ADMIN', 'OFFICER', 'ACCOUNTANT'] },
]
const topNavLinks = computed(() =>
  allTopNavLinks.filter(l => l.roles.includes(authStore.role))
)

function isTopNavActive(path) {
  return route.path === path
}

// --- Nút AI ---
function goToAiReview() {
  router.push('/xet-duyet-ai')
}

// --- Nút Help ---
function goToSettings() {
  router.push('/cai-dat')
}

// --- Notifications (API-driven) ---
const showNotifications = ref(false)
const unreadCount = ref(0)
const notifications = ref([])
let pollTimer = null

const typeIcon = { SUCCESS: 'check_circle', WARNING: 'warning', ERROR: 'error', INFO: 'info' }
const typeClass = { SUCCESS: 'text-green-600', WARNING: 'text-amber-600', ERROR: 'text-red-600', INFO: 'text-blue-500' }

function iconFor(type) { return typeIcon[type] || 'notifications' }
function iconClassFor(type) { return typeClass[type] || 'text-primary' }

function timeAgo(ts) {
  if (!ts) return ''
  const diff = Date.now() - new Date(ts).getTime()
  const m = Math.floor(diff / 60000)
  if (m < 1) return 'Vừa xong'
  if (m < 60) return `${m} phút trước`
  const h = Math.floor(m / 60)
  if (h < 24) return `${h} giờ trước`
  return new Date(ts).toLocaleDateString('vi-VN')
}

async function loadNotifications() {
  try {
    const [listRes, countRes] = await Promise.all([
      notificationsApi.getAll(0, 5),
      notificationsApi.getUnreadCount(),
    ])
    notifications.value = listRes.data.content || []
    unreadCount.value = countRes.data.count || 0
  } catch (e) { /* ignore silent */ }
}

function toggleNotifications() {
  showNotifications.value = !showNotifications.value
  if (showNotifications.value) loadNotifications()
}

async function handleNotificationClick(n) {
  if (!n.read) {
    await notificationsApi.markRead(n.id)
    n.read = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
  showNotifications.value = false
  if (n.link) router.push(n.link)
}

async function markAllRead() {
  await notificationsApi.markAllRead()
  notifications.value.forEach(n => n.read = true)
  unreadCount.value = 0
}

function goToAllNotifications() {
  showNotifications.value = false
  router.push('/thong-bao')
}

// Đóng dropdown khi click ngoài
function handleOutsideClick(e) {
  if (!e.target.closest('[data-notif-panel]')) {
    showNotifications.value = false
  }
}

onMounted(() => {
  loadNotifications()
  pollTimer = setInterval(loadNotifications, 60000)
  document.addEventListener('click', handleOutsideClick)
})
onUnmounted(() => {
  clearInterval(pollTimer)
  document.removeEventListener('click', handleOutsideClick)
})
</script>

