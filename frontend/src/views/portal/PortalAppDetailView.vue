<template>
  <div class="space-y-5">

    <!-- BACK BUTTON -->
    <button @click="$router.push('/portal/ho-so-cua-toi')"
      class="flex items-center gap-1.5 text-sm text-slate-500 font-semibold hover:text-primary transition-colors">
      <span class="material-symbols-outlined text-sm">arrow_back</span>Quay lại danh sách
    </button>

    <!-- HEADER -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <div :class="['h-1.5', statusColor]"></div>
      <div class="p-5">
        <div class="flex items-start gap-4">
          <div :class="['w-14 h-14 rounded-2xl flex items-center justify-center flex-shrink-0', statusIconBg]">
            <span class="material-symbols-outlined text-2xl" :class="statusIconColor" style="font-variation-settings:'FILL' 1;">{{ statusIcon }}</span>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between gap-3 mb-1">
              <h1 class="text-xl font-black text-slate-800">{{ app.ten_chuong_trinh }}</h1>
              <span :class="['px-3 py-1 rounded-full text-xs font-black flex-shrink-0', statusBadge]">{{ statusLabel }}</span>
            </div>
            <p class="text-sm text-slate-400">#HS-{{ app.id }} · Nộp ngày {{ formatDate(app.ngay_nop) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- TIMELINE -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
      <h2 class="font-black text-slate-800 mb-4 flex items-center gap-2">
        <span class="material-symbols-outlined text-primary text-sm" style="font-variation-settings:'FILL' 1;">timeline</span>
        Tiến trình xử lý
      </h2>
      <div class="relative">
        <div v-for="(step, i) in timeline" :key="i" class="flex gap-4 mb-0" :class="i < timeline.length - 1 ? 'pb-6' : ''">
          <!-- Line -->
          <div class="flex flex-col items-center">
            <div :class="['w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 z-10',
              step.done ? 'bg-primary' : step.active ? 'bg-primary ring-4 ring-primary/20' : 'bg-slate-100']">
              <span class="material-symbols-outlined text-sm" :class="step.done || step.active ? 'text-white' : 'text-slate-400'"
                style="font-variation-settings:'FILL' 1;">{{ step.done ? 'check' : step.icon }}</span>
            </div>
            <div v-if="i < timeline.length - 1" :class="['w-0.5 flex-1 mt-1', step.done ? 'bg-primary' : 'bg-slate-100']"></div>
          </div>
          <!-- Content -->
          <div class="flex-1 min-w-0 pt-1">
            <p :class="['text-sm font-bold', step.done || step.active ? 'text-slate-800' : 'text-slate-400']">{{ step.label }}</p>
            <p class="text-xs text-slate-400 mt-0.5">{{ step.date || '—' }}</p>
            <p v-if="step.note" class="text-xs text-slate-500 mt-1 bg-slate-50 rounded-xl px-3 py-2">{{ step.note }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- PERSONAL INFO -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm">
      <div class="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
        <span class="material-symbols-outlined text-primary text-sm" style="font-variation-settings:'FILL' 1;">person</span>
        <h2 class="font-black text-slate-800">Thông tin người nộp</h2>
      </div>
      <div class="grid grid-cols-2 gap-4 p-5">
        <div v-for="f in infoFields" :key="f.label">
          <p class="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-0.5">{{ f.label }}</p>
          <p class="text-sm font-semibold text-slate-800">{{ f.value || '—' }}</p>
        </div>
      </div>
    </div>

    <!-- DOCUMENTS -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm">
      <div class="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
        <span class="material-symbols-outlined text-primary text-sm" style="font-variation-settings:'FILL' 1;">attach_file</span>
        <h2 class="font-black text-slate-800">Tài liệu đính kèm</h2>
      </div>
      <div class="divide-y divide-slate-50">
        <div v-for="doc in app.tai_lieu" :key="doc.id" class="flex items-center gap-3 px-5 py-3">
          <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0',
            doc.loai === 'PDF' ? 'bg-red-50' : 'bg-blue-50']">
            <span class="material-symbols-outlined text-sm"
              :class="doc.loai === 'PDF' ? 'text-red-500' : 'text-blue-500'"
              style="font-variation-settings:'FILL' 1;">
              {{ doc.loai === 'PDF' ? 'picture_as_pdf' : 'image' }}
            </span>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-semibold text-slate-800 truncate">{{ doc.ten }}</p>
            <p class="text-[11px] text-slate-400">{{ doc.loai }} · Tải lên {{ doc.ngay }}</p>
          </div>
          <span class="material-symbols-outlined text-slate-300 text-sm">download</span>
        </div>
      </div>
    </div>

    <!-- AI REVIEW -->
    <div v-if="app.ai_review" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm">
      <div class="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
        <span class="material-symbols-outlined text-primary text-sm" style="font-variation-settings:'FILL' 1;">smart_toy</span>
        <h2 class="font-black text-slate-800">Kết quả AI đánh giá</h2>
      </div>
      <div class="p-5 space-y-4">
        <div class="grid grid-cols-2 gap-3">
          <div class="bg-primary/5 rounded-2xl p-4 text-center">
            <p class="text-[10px] text-primary/60 font-bold uppercase tracking-wider mb-1">Điểm ưu tiên</p>
            <p class="text-3xl font-black text-primary">{{ app.ai_review.diem }}</p>
          </div>
          <div class="bg-emerald-50 rounded-2xl p-4 text-center">
            <p class="text-[10px] text-emerald-600/60 font-bold uppercase tracking-wider mb-1">Độ tin cậy AI</p>
            <p class="text-3xl font-black text-emerald-600">{{ app.ai_review.tin_cay }}%</p>
          </div>
        </div>
        <div class="bg-slate-50 rounded-2xl p-4">
          <p class="text-xs font-bold text-slate-500 mb-2">Nhận xét AI</p>
          <p class="text-sm text-slate-600 leading-relaxed">{{ app.ai_review.nhan_xet }}</p>
        </div>
      </div>
    </div>

    <!-- REJECTION REASON -->
    <div v-if="app.trang_thai === 'REJECTED' && app.ly_do_tu_choi"
      class="bg-red-50 border border-red-100 rounded-2xl p-5">
      <div class="flex items-center gap-2 mb-2">
        <span class="material-symbols-outlined text-red-500 text-sm" style="font-variation-settings:'FILL' 1;">warning</span>
        <h2 class="font-black text-red-700">Lý do từ chối</h2>
      </div>
      <p class="text-sm text-red-600 leading-relaxed">{{ app.ly_do_tu_choi }}</p>
    </div>

    <!-- ACTIONS -->
    <div class="flex gap-3">
      <router-link v-if="app.trang_thai === 'REJECTED'" to="/portal/nop-ho-so"
        class="flex-1 flex items-center justify-center gap-2 py-3 bg-primary text-white text-sm font-bold rounded-2xl hover:opacity-90 shadow-md shadow-primary/20">
        <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">refresh</span>
        Nộp lại hồ sơ
      </router-link>
      <button @click="$router.push('/portal/ho-so-cua-toi')"
        class="flex-1 py-3 border border-slate-200 text-slate-600 text-sm font-semibold rounded-2xl hover:bg-slate-50">
        Quay lại
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { applicationsApi } from '../../api/applications'

const route = useRoute()
const loading = ref(true)

const fallback = {
  id: 1001,
  ten_chuong_trinh: 'Hỗ trợ người cao tuổi 2026',
  trang_thai: 'APPROVED',
  ngay_nop: '2026-01-10',
  ly_do_tu_choi: null,
  ho_ten: 'Lê Ngọc Thảo', so_cccd: '079123456789',
  ngay_sinh: '01/01/1990', so_dien_thoai: '0901234567',
  dia_chi: '123 Đường Lê Lợi, TP. Đà Nẵng',
  ly_do: 'Người cao tuổi không có nguồn thu nhập ổn định',
  tai_lieu: [
    { id: 1, ten: 'chung_minh_nhan_dan.jpg', loai: 'IMAGE', ngay: '10/01/2026' },
    { id: 2, ten: 'giay_xac_nhan_ho_ngheo.pdf', loai: 'PDF', ngay: '10/01/2026' },
  ],
  ai_review: { diem: 85.5, tin_cay: 92, nhan_xet: 'Trùng khớp dữ liệu. CCCD hợp lệ. OCR thành công. Hộ nghèo xác nhận đúng. Đề xuất ưu tiên.' },
}

const app = ref(fallback)

onMounted(async () => {
  loading.value = true
  try {
    const res = await applicationsApi.getById(route.params.id)
    app.value = res.data || fallback
  } catch {
    app.value = { ...fallback, id: Number(route.params.id) || fallback.id }
  } finally {
    loading.value = false
  }
})

const infoFields = computed(() => [
  { label: 'Họ và tên', value: app.value.ho_ten },
  { label: 'Số CCCD', value: app.value.so_cccd },
  { label: 'Ngày sinh', value: app.value.ngay_sinh },
  { label: 'Số điện thoại', value: app.value.so_dien_thoai },
  { label: 'Địa chỉ', value: app.value.dia_chi },
  { label: 'Lý do xin trợ cấp', value: app.value.ly_do },
])

const ST = {
  PENDING:   { label:'Chờ duyệt',      badge:'bg-amber-100 text-amber-700',    icon:'hourglass_top', iconBg:'bg-amber-50',   iconColor:'text-amber-500',  color:'bg-amber-400' },
  REVIEWING: { label:'Đang xét duyệt', badge:'bg-blue-100 text-blue-700',      icon:'manage_search', iconBg:'bg-blue-50',    iconColor:'text-blue-500',   color:'bg-blue-400' },
  APPROVED:  { label:'Đã phê duyệt',   badge:'bg-emerald-100 text-emerald-700',icon:'check_circle',  iconBg:'bg-emerald-50', iconColor:'text-emerald-500',color:'bg-emerald-400' },
  REJECTED:  { label:'Từ chối',         badge:'bg-red-100 text-red-600',        icon:'cancel',        iconBg:'bg-red-50',     iconColor:'text-red-500',    color:'bg-red-400' },
}
const stCfg = computed(() => ST[app.value.trang_thai] || ST.PENDING)
const statusLabel     = computed(() => stCfg.value.label)
const statusBadge     = computed(() => stCfg.value.badge)
const statusIcon      = computed(() => stCfg.value.icon)
const statusIconBg    = computed(() => stCfg.value.iconBg)
const statusIconColor = computed(() => stCfg.value.iconColor)
const statusColor     = computed(() => stCfg.value.color)

const timeline = computed(() => {
  const s = app.value.trang_thai
  const step = s === 'APPROVED' ? 4 : s === 'REJECTED' ? 3 : s === 'REVIEWING' ? 2 : 1
  return [
    { icon:'upload_file',   label:'Nộp hồ sơ',             date: formatDate(app.value.ngay_nop), done: step >= 1, active: step === 1 },
    { icon:'manage_search', label:'Tiếp nhận & xét duyệt', date: step >= 2 ? '12/01/2026' : null, done: step >= 2, active: step === 2 },
    { icon:'smart_toy',     label:'AI đánh giá',            date: step >= 3 ? '14/01/2026' : null, done: step >= 3, active: step === 3, note: step >= 3 && app.value.ai_review ? `Điểm ưu tiên: ${app.value.ai_review.diem}` : null },
    { icon:'check_circle',  label: s === 'REJECTED' ? 'Từ chối' : 'Phê duyệt', date: step >= 4 || s === 'REJECTED' ? '15/01/2026' : null, done: step >= 4 || s === 'REJECTED', active: false },
  ]
})

function formatDate(d) { return d ? new Date(d).toLocaleDateString('vi-VN') : '—' }
</script>
