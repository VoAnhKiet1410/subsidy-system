<template>
  <div class="p-8 space-y-8 max-w-3xl mx-auto">
    <!-- Header -->
    <div class="flex items-end justify-between">
      <div>
        <h2 class="text-3xl font-extrabold editorial-tight text-on-surface">Thông báo</h2>
        <p class="text-on-surface-variant mt-1">
          <span v-if="unreadCount > 0" class="text-primary font-semibold">{{ unreadCount }} chưa đọc</span>
          <span v-else>Không có thông báo chưa đọc</span>
        </p>
      </div>
      <div class="flex gap-3">
        <button
          v-if="unreadCount > 0"
          @click="markAllRead"
          class="ripple flex items-center gap-2 px-4 py-2 text-sm font-semibold text-primary bg-primary/5 hover:bg-primary/10 rounded-xl transition-colors"
        >
          <span class="material-symbols-outlined text-lg">done_all</span>
          Đánh dấu tất cả đã đọc
        </button>
        <button
          @click="fetchData"
          class="p-2 text-on-surface-variant hover:text-primary hover:bg-surface-container rounded-xl transition-colors"
        >
          <span class="material-symbols-outlined">refresh</span>
        </button>
      </div>
    </div>

    <!-- Filter tabs -->
    <div class="flex gap-2 bg-surface-container-low p-1 rounded-xl w-fit">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        @click="activeTab = tab.key"
        :class="['px-4 py-2 rounded-lg text-sm font-semibold transition-all', activeTab === tab.key ? 'bg-white shadow-sm text-primary' : 'text-on-surface-variant hover:text-on-surface']"
      >
        {{ tab.label }}
        <span v-if="tab.key === 'unread' && unreadCount > 0" class="ml-1.5 px-1.5 py-0.5 bg-primary text-white text-[10px] rounded-full">{{ unreadCount }}</span>
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="space-y-3">
      <div v-for="i in 4" :key="i" class="skeleton h-20 rounded-2xl"></div>
    </div>

    <!-- Empty -->
    <div v-else-if="filteredNotifications.length === 0" class="bg-surface-container-lowest rounded-2xl p-16 text-center">
      <span class="material-symbols-outlined text-6xl text-primary/20 mb-4 block">notifications_off</span>
      <h3 class="text-lg font-bold text-on-surface">Không có thông báo nào</h3>
      <p class="text-sm text-on-surface-variant mt-1">{{ activeTab === 'unread' ? 'Bạn đã đọc hết thông báo!' : 'Chưa có thông báo nào.' }}</p>
    </div>

    <!-- Notification list -->
    <div v-else class="space-y-3">
      <div
        v-for="n in filteredNotifications"
        :key="n.id"
        class="fade-up group relative bg-surface-container-lowest rounded-2xl p-5 shadow-sm hover:shadow-md transition-all cursor-pointer border-l-4"
        :class="[borderClass(n.type), !n.read ? 'bg-blue-50/30' : '']"
        @click="handleClick(n)"
      >
        <div class="flex items-start gap-4">
          <!-- Icon -->
          <div :class="['w-10 h-10 rounded-xl flex items-center justify-center shrink-0', iconBg(n.type)]">
            <span class="material-symbols-outlined text-xl" :class="iconColor(n.type)" style="font-variation-settings: 'FILL' 1;">
              {{ iconName(n.type) }}
            </span>
          </div>

          <!-- Content -->
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between gap-2">
              <h4 class="text-sm font-bold text-on-surface leading-tight">{{ n.title }}</h4>
              <div class="flex items-center gap-2 shrink-0">
                <span v-if="!n.read" class="w-2 h-2 bg-primary rounded-full shrink-0"></span>
                <span class="text-[11px] text-on-surface-variant">{{ timeAgo(n.createdAt) }}</span>
              </div>
            </div>
            <p class="text-sm text-on-surface-variant mt-1 leading-relaxed">{{ n.message }}</p>
            <div class="flex items-center gap-3 mt-3">
              <span class="text-[11px] font-bold text-on-surface-variant/70 uppercase tracking-widest bg-surface-container px-2 py-0.5 rounded-full">
                {{ entityLabel(n.entityType) }}
              </span>
              <span v-if="n.link" class="text-[11px] text-primary font-semibold hover:underline">Xem chi tiết →</span>
            </div>
          </div>

          <!-- Delete button -->
          <button
            @click.stop="deleteNotification(n.id)"
            class="opacity-0 group-hover:opacity-100 p-1.5 rounded-lg text-on-surface-variant hover:text-red-500 hover:bg-red-50 transition-all"
          >
            <span class="material-symbols-outlined text-sm">close</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="flex justify-center gap-2">
      <button
        :disabled="currentPage === 0"
        @click="currentPage--; fetchData()"
        class="px-4 py-2 rounded-xl bg-surface-container text-sm font-semibold disabled:opacity-40"
      >← Trước</button>
      <span class="px-4 py-2 text-sm text-on-surface-variant">Trang {{ currentPage + 1 }}/{{ totalPages }}</span>
      <button
        :disabled="currentPage >= totalPages - 1"
        @click="currentPage++; fetchData()"
        class="px-4 py-2 rounded-xl bg-surface-container text-sm font-semibold disabled:opacity-40"
      >Sau →</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { notificationsApi } from '../api/notifications'

const router = useRouter()
const loading = ref(false)
const notifications = ref([])
const totalElements = ref(0)
const currentPage = ref(0)
const pageSize = 20
const unreadCount = ref(0)
const activeTab = ref('all')

const tabs = [
  { key: 'all', label: 'Tất cả' },
  { key: 'unread', label: 'Chưa đọc' },
  { key: 'SUCCESS', label: 'Thành công' },
  { key: 'WARNING', label: 'Cảnh báo' },
  { key: 'ERROR', label: 'Lỗi' },
]

const filteredNotifications = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  if (activeTab.value === 'unread') return notifications.value.filter(n => !n.read)
  return notifications.value.filter(n => n.type === activeTab.value)
})

const totalPages = computed(() => Math.ceil(totalElements.value / pageSize))

async function fetchData() {
  loading.value = true
  try {
    const [res, countRes] = await Promise.all([
      notificationsApi.getAll(currentPage.value, pageSize),
      notificationsApi.getUnreadCount(),
    ])
    notifications.value = res.data.content || []
    totalElements.value = res.data.totalElements || 0
    unreadCount.value = countRes.data.count || 0
  } catch (e) {
    console.error('Lỗi tải thông báo:', e)
  } finally {
    loading.value = false
  }
}

async function handleClick(n) {
  if (!n.read) {
    await notificationsApi.markRead(n.id)
    n.read = true
    unreadCount.value = Math.max(0, unreadCount.value - 1)
  }
  if (n.link) router.push(n.link)
}

async function markAllRead() {
  await notificationsApi.markAllRead()
  notifications.value.forEach(n => n.read = true)
  unreadCount.value = 0
}

async function deleteNotification(id) {
  await notificationsApi.delete(id)
  notifications.value = notifications.value.filter(n => n.id !== id)
}

// Helpers
function iconName(type) {
  return { SUCCESS: 'check_circle', WARNING: 'warning', ERROR: 'error', INFO: 'info' }[type] || 'notifications'
}
function iconBg(type) {
  return { SUCCESS: 'bg-green-100', WARNING: 'bg-amber-100', ERROR: 'bg-red-100', INFO: 'bg-blue-100' }[type] || 'bg-surface-container'
}
function iconColor(type) {
  return { SUCCESS: 'text-green-600', WARNING: 'text-amber-600', ERROR: 'text-red-600', INFO: 'text-blue-600' }[type] || 'text-primary'
}
function borderClass(type) {
  return { SUCCESS: 'border-green-400', WARNING: 'border-amber-400', ERROR: 'border-red-400', INFO: 'border-blue-400' }[type] || 'border-slate-200'
}
function entityLabel(et) {
  return { Application: 'Hồ sơ', Transaction: 'Giao dịch', Program: 'Chương trình', User: 'Người dùng' }[et] || et || 'Hệ thống'
}
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

onMounted(fetchData)
</script>
