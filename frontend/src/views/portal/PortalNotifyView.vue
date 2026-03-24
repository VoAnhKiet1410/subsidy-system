<template>
  <div class="space-y-5">

    <!-- HEADER -->
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-black text-slate-800">Thông báo</h1>
      <button v-if="unreadCount > 0" @click="markAllRead"
        class="text-xs font-bold text-primary hover:underline">Đánh dấu tất cả đã đọc</button>
    </div>

    <!-- FILTER TABS -->
    <div class="flex gap-2 overflow-x-auto pb-1">
      <button v-for="t in tabs" :key="t.key" @click="activeTab = t.key"
        :class="['px-4 py-2 rounded-xl text-sm font-bold whitespace-nowrap transition-colors',
          activeTab === t.key
            ? 'bg-primary text-white shadow-sm'
            : 'bg-white text-slate-500 border border-slate-200 hover:bg-slate-50']">
        {{ t.label }}
        <span v-if="t.count" :class="['ml-1 text-[10px] font-black px-1.5 py-0.5 rounded-full',
          activeTab === t.key ? 'bg-white/20' : 'bg-red-100 text-red-600']">{{ t.count }}</span>
      </button>
    </div>

    <!-- NOTIFICATIONS LIST -->
    <div class="space-y-2">
      <div v-if="!filtered.length" class="bg-white rounded-2xl border border-slate-200 p-14 text-center">
        <span class="material-symbols-outlined text-5xl text-slate-200 block mb-3">notifications_off</span>
        <p class="font-bold text-slate-500">Không có thông báo</p>
        <p class="text-sm text-slate-400 mt-1">Bạn sẽ nhận thông báo khi có cập nhật về hồ sơ</p>
      </div>

      <TransitionGroup name="list" tag="div" class="space-y-2">
        <div v-for="n in filtered" :key="n.id" @click="readNotif(n)"
          :class="['bg-white rounded-2xl border shadow-sm p-4 cursor-pointer hover:shadow-md transition-all',
            n.read ? 'border-slate-200/80' : 'border-primary/20 bg-primary/[0.02]']">
          <div class="flex items-start gap-3">
            <!-- Icon -->
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0', n.iconBg]">
              <span class="material-symbols-outlined text-sm" :class="n.iconColor" style="font-variation-settings:'FILL' 1;">{{ n.icon }}</span>
            </div>
            <!-- Content -->
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between gap-2 mb-0.5">
                <p :class="['text-sm leading-snug', n.read ? 'text-slate-600' : 'text-slate-800 font-bold']">{{ n.title }}</p>
                <div v-if="!n.read" class="w-2.5 h-2.5 bg-primary rounded-full flex-shrink-0 mt-1.5"></div>
              </div>
              <p class="text-xs text-slate-500 leading-relaxed mb-1.5">{{ n.content }}</p>
              <div class="flex items-center gap-3">
                <p class="text-[10px] text-slate-400 font-semibold">{{ n.time }}</p>
                <span v-if="n.link" class="text-[10px] font-bold text-primary">Xem chi tiết →</span>
              </div>
            </div>
          </div>
        </div>
      </TransitionGroup>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { notificationsApi } from '../../api/notifications'

const activeTab = ref('all')
const loading = ref(false)

const fallbackNotifs = [
  {
    id: 1, type: 'approved',
    title: 'Hồ sơ #HS-1001 đã được phê duyệt!',
    content: 'Hồ sơ xin hỗ trợ người cao tuổi của bạn đã được phê duyệt. Tiền trợ cấp sẽ được chuyển khoản trong vòng 7 ngày làm việc.',
    icon: 'check_circle', iconBg: 'bg-emerald-50', iconColor: 'text-emerald-500',
    time: '10 phút trước', read: false, link: '/portal/ho-so-cua-toi/1001',
  },
  {
    id: 2, type: 'review',
    title: 'Hồ sơ #HS-1002 đang được xét duyệt',
    content: 'Hồ sơ xin hỗ trợ người khuyết tật đã qua vòng AI đánh giá và đang chờ cán bộ xét duyệt.',
    icon: 'manage_search', iconBg: 'bg-blue-50', iconColor: 'text-blue-500',
    time: '2 giờ trước', read: false, link: '/portal/ho-so-cua-toi/1002',
  },
  {
    id: 3, type: 'warning',
    title: 'Chương trình "Quỹ học bổng trẻ em nghèo" sắp hết hạn',
    content: 'Chỉ còn 15 ngày để nộp hồ sơ. Nếu bạn đủ điều kiện, hãy nộp ngay.',
    icon: 'schedule', iconBg: 'bg-amber-50', iconColor: 'text-amber-500',
    time: 'Hôm qua', read: true, link: '/portal/chuong-trinh',
  },
  {
    id: 4, type: 'system',
    title: 'Tài khoản đã được xác minh',
    content: 'Tài khoản của bạn đã được xác minh thành công. Bạn có thể nộp hồ sơ cho tất cả chương trình.',
    icon: 'verified_user', iconBg: 'bg-primary/10', iconColor: 'text-primary',
    time: '2 ngày trước', read: true, link: null,
  },
  {
    id: 5, type: 'rejected',
    title: 'Hồ sơ #HS-1003 bị từ chối',
    content: 'Rất tiếc, hồ sơ hỗ trợ trẻ em có hoàn cảnh khó khăn không đáp ứng đủ điều kiện. Bạn có thể bổ sung tài liệu và nộp lại.',
    icon: 'cancel', iconBg: 'bg-red-50', iconColor: 'text-red-500',
    time: '5 ngày trước', read: true, link: '/portal/ho-so-cua-toi/1003',
  },
  {
    id: 6, type: 'payment',
    title: 'Đã nhận trợ cấp tháng 01/2026',
    content: 'Số tiền 500.000đ đã được chuyển vào tài khoản ngân hàng của bạn. Vui lòng kiểm tra.',
    icon: 'payments', iconBg: 'bg-emerald-50', iconColor: 'text-emerald-500',
    time: '20/01/2026', read: true, link: null,
  },
]

const notifications = ref(fallbackNotifs)

onMounted(async () => {
  loading.value = true
  try {
    const res = await notificationsApi.getAll()
    const list = res.data.content || res.data || []
    if (list.length) {
      notifications.value = list.map(n => ({
        id: n.id,
        type: n.loai || 'system',
        title: n.tieu_de || n.title || '',
        content: n.noi_dung || n.content || '',
        icon: mapIcon(n.loai),
        iconBg: mapIconBg(n.loai),
        iconColor: mapIconColor(n.loai),
        time: n.ngay_tao ? timeAgo(n.ngay_tao) : '',
        read: !!n.da_doc,
        link: n.link || null,
      }))
    }
  } catch {
    // Giữ fallback
  } finally {
    loading.value = false
  }
})

const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

const tabs = computed(() => [
  { key: 'all',    label: 'Tất cả' },
  { key: 'unread', label: 'Chưa đọc', count: unreadCount.value || null },
  { key: 'read',   label: 'Đã đọc' },
])

const filtered = computed(() => {
  if (activeTab.value === 'unread') return notifications.value.filter(n => !n.read)
  if (activeTab.value === 'read')   return notifications.value.filter(n => n.read)
  return notifications.value
})

async function readNotif(n) {
  if (n.read) return
  n.read = true
  try { await notificationsApi.markRead(n.id) } catch { /* silent */ }
}

async function markAllRead() {
  notifications.value.forEach(n => n.read = true)
  try {
    const ids = notifications.value.map(n => n.id)
    await Promise.allSettled(ids.map(id => notificationsApi.markRead(id)))
  } catch { /* silent */ }
}

// Helpers
function mapIcon(type) {
  const m = { approved:'check_circle', review:'manage_search', rejected:'cancel', payment:'payments', warning:'schedule' }
  return m[type] || 'notifications'
}
function mapIconBg(type) {
  const m = { approved:'bg-emerald-50', review:'bg-blue-50', rejected:'bg-red-50', payment:'bg-emerald-50', warning:'bg-amber-50' }
  return m[type] || 'bg-primary/10'
}
function mapIconColor(type) {
  const m = { approved:'text-emerald-500', review:'text-blue-500', rejected:'text-red-500', payment:'text-emerald-500', warning:'text-amber-500' }
  return m[type] || 'text-primary'
}
function timeAgo(dateStr) {
  const diff = Date.now() - new Date(dateStr).getTime()
  const mins = Math.floor(diff / 60000)
  if (mins < 60) return `${mins} phút trước`
  const hrs = Math.floor(mins / 60)
  if (hrs < 24) return `${hrs} giờ trước`
  const days = Math.floor(hrs / 24)
  if (days < 7) return `${days} ngày trước`
  return new Date(dateStr).toLocaleDateString('vi-VN')
}
</script>

<style scoped>
.list-enter-active, .list-leave-active { transition: all 0.3s ease; }
.list-enter-from { opacity: 0; transform: translateY(-10px); }
.list-leave-to   { opacity: 0; transform: translateX(30px); }
</style>
