<template>
  <div class="min-h-screen bg-slate-50/60">
    <!-- Header -->
    <div class="bg-white border-b border-slate-200/80 px-8 py-5 flex items-center justify-between sticky top-0 z-30">
      <div>
        <h2 class="text-xl font-black text-slate-800 tracking-tight">Nhật ký hoạt động</h2>
        <p class="text-xs text-slate-400 mt-0.5">Theo dõi tất cả hoạt động trong hệ thống</p>
      </div>
      <div class="flex items-center gap-3">
        <select v-model="filterType" class="px-3 py-2 bg-slate-100 border-0 rounded-xl text-sm font-semibold text-slate-600 focus:outline-none focus:ring-2 focus:ring-primary/20">
          <option value="all">Tất cả</option>
          <option value="application">Hồ sơ</option>
          <option value="program">Chương trình</option>
          <option value="user">Người dùng</option>
          <option value="payment">Chi trả</option>
          <option value="system">Hệ thống</option>
        </select>
        <button @click="loadLogs" :disabled="loading"
          class="flex items-center gap-2 px-4 py-2 bg-slate-100 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-200 transition-colors">
          <span :class="['material-symbols-outlined text-sm', loading ? 'animate-spin' : '']">refresh</span>
          Làm mới
        </button>
      </div>
    </div>

    <div class="p-8">
      <!-- Timeline -->
      <div class="max-w-4xl mx-auto">
        <!-- Skeleton -->
        <div v-if="loading" class="space-y-4">
          <div v-for="i in 6" :key="i" class="flex gap-4 animate-pulse">
            <div class="w-10 h-10 rounded-xl bg-slate-100 flex-shrink-0"></div>
            <div class="flex-1 bg-white rounded-2xl border border-slate-200 p-5 space-y-2">
              <div class="w-48 h-3.5 bg-slate-100 rounded"></div>
              <div class="w-full h-3 bg-slate-50 rounded"></div>
              <div class="w-24 h-2.5 bg-slate-50 rounded"></div>
            </div>
          </div>
        </div>

        <!-- Log entries -->
        <div v-else class="relative">
          <div class="absolute left-5 top-0 bottom-0 w-px bg-slate-200"></div>

          <div v-for="log in filteredLogs" :key="log.id" class="relative flex gap-4 mb-4">
            <!-- Icon -->
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 z-10', log.iconBg]">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ log.icon }}</span>
            </div>

            <!-- Content -->
            <div class="flex-1 bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 hover:shadow-md transition-shadow">
              <div class="flex items-start justify-between gap-3">
                <div class="flex-1">
                  <p class="font-bold text-slate-800 text-sm">{{ log.title }}</p>
                  <p class="text-xs text-slate-500 mt-1 leading-relaxed">{{ log.description }}</p>
                </div>
                <span :class="['px-2 py-0.5 rounded-full text-[10px] font-bold flex-shrink-0', typeClass(log.type)]">
                  {{ typeLabel(log.type) }}
                </span>
              </div>
              <div class="flex items-center gap-3 mt-3 text-[11px] text-slate-400">
                <span class="flex items-center gap-1">
                  <span class="material-symbols-outlined" style="font-size:13px;">schedule</span>
                  {{ log.time }}
                </span>
                <span v-if="log.user" class="flex items-center gap-1">
                  <span class="material-symbols-outlined" style="font-size:13px;">person</span>
                  {{ log.user }}
                </span>
                <span v-if="log.ip" class="flex items-center gap-1">
                  <span class="material-symbols-outlined" style="font-size:13px;">computer</span>
                  {{ log.ip }}
                </span>
              </div>
            </div>
          </div>

          <!-- Empty state -->
          <div v-if="!filteredLogs.length" class="text-center py-16">
            <span class="material-symbols-outlined text-5xl text-slate-200 block mb-3">history</span>
            <p class="font-bold text-slate-400">Không có hoạt động nào</p>
            <p class="text-xs text-slate-300 mt-1">Thay đổi bộ lọc để xem thêm</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const loading = ref(false)
const filterType = ref('all')

const logs = ref([
  { id: 1, type: 'application', icon: 'upload_file', iconBg: 'bg-blue-100 text-blue-600',
    title: 'Hồ sơ #HS-1247 được nộp',
    description: 'Nguyễn Thị Hoa đã nộp hồ sơ xin trợ cấp người cao tuổi — tự động chuyển sang trạng thái chờ duyệt.',
    time: '5 phút trước', user: 'Nguyễn Thị Hoa', ip: '192.168.1.45' },
  { id: 2, type: 'application', icon: 'check_circle', iconBg: 'bg-emerald-100 text-emerald-600',
    title: 'Hồ sơ #HS-1240 được phê duyệt',
    description: 'Cán bộ Lê Minh Trí đã phê duyệt hồ sơ. AI score: 91.2. Chuyển sang giai đoạn chi trả.',
    time: '15 phút trước', user: 'Lê Minh Trí', ip: '10.0.0.12' },
  { id: 3, type: 'payment', icon: 'payments', iconBg: 'bg-purple-100 text-purple-600',
    title: 'Chi trả lô tháng 3/2026 hoàn thành',
    description: '89 hồ sơ đã được chi trả thành công. Tổng giải ngân: 445.000.000đ. 0 lỗi chuyển khoản.',
    time: '1 giờ trước', user: 'Hệ thống', ip: null },
  { id: 4, type: 'system', icon: 'psychology', iconBg: 'bg-indigo-100 text-indigo-600',
    title: 'AI phát hiện 3 hồ sơ nghi gian lận',
    description: 'Module AI fraud detection đã đánh dấu 3 hồ sơ (#HS-1198, #HS-1201, #HS-1205) có điểm tin cậy dưới 40.',
    time: '2 giờ trước', user: 'AI Engine', ip: null },
  { id: 5, type: 'application', icon: 'cancel', iconBg: 'bg-red-100 text-red-600',
    title: 'Hồ sơ #HS-1235 bị từ chối',
    description: 'Lý do: Thiếu giấy xác nhận hộ nghèo. Người nộp được gợi ý bổ sung tài liệu.',
    time: '3 giờ trước', user: 'Trần Văn Bình', ip: '10.0.0.8' },
  { id: 6, type: 'program', icon: 'campaign', iconBg: 'bg-amber-100 text-amber-600',
    title: 'Chương trình "Học bổng trẻ em nghèo" cập nhật',
    description: 'Hạn nộp được gia hạn từ 31/07/2026 → 31/08/2026. Tổng chỉ tiêu tăng từ 150 → 200.',
    time: '5 giờ trước', user: 'Admin', ip: '10.0.0.1' },
  { id: 7, type: 'user', icon: 'person_add', iconBg: 'bg-teal-100 text-teal-600',
    title: 'Tài khoản mới đăng ký',
    description: 'Phạm Thị Lan (email: lan.pt@gmail.com) đã tạo tài khoản mới. Vai trò: Người xem.',
    time: '6 giờ trước', user: 'Phạm Thị Lan', ip: '118.70.45.22' },
  { id: 8, type: 'system', icon: 'backup', iconBg: 'bg-slate-100 text-slate-600',
    title: 'Sao lưu dữ liệu tự động',
    description: 'Backup #B-20260323 hoàn thành. Kích thước: 2.4GB. Lưu trữ: Cloud Storage.',
    time: 'Hôm qua 23:00', user: 'Hệ thống', ip: null },
  { id: 9, type: 'payment', icon: 'account_balance', iconBg: 'bg-purple-100 text-purple-600',
    title: 'Nguồn quỹ được bổ sung',
    description: 'Quỹ BTXH Tỉnh 2026 nhận bổ sung 5 tỷ đồng từ ngân sách Trung ương.',
    time: 'Hôm qua 14:30', user: 'Admin', ip: '10.0.0.1' },
  { id: 10, type: 'application', icon: 'smart_toy', iconBg: 'bg-indigo-100 text-indigo-600',
    title: 'AI Review batch #42 hoàn thành',
    description: '25 hồ sơ đã qua vòng AI review. Trung bình score: 78.5. 3 hồ sơ ưu tiên cao (≥90).',
    time: 'Hôm qua 10:15', user: 'AI Engine', ip: null },
])

const filteredLogs = computed(() => {
  if (filterType.value === 'all') return logs.value
  return logs.value.filter(l => l.type === filterType.value)
})

function typeLabel(t) {
  return { application: 'Hồ sơ', program: 'Chương trình', user: 'Người dùng', payment: 'Chi trả', system: 'Hệ thống' }[t] || t
}
function typeClass(t) {
  return {
    application: 'bg-blue-100 text-blue-700',
    program: 'bg-amber-100 text-amber-700',
    user: 'bg-teal-100 text-teal-700',
    payment: 'bg-purple-100 text-purple-700',
    system: 'bg-slate-100 text-slate-600',
  }[t] || 'bg-slate-100 text-slate-600'
}

async function loadLogs() {
  loading.value = true
  // TODO: gọi API logs
  setTimeout(() => { loading.value = false }, 500)
}

onMounted(() => { /* Data đã sẵn sàng từ fallback */ })
</script>
