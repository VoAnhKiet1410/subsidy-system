<template>
  <div class="space-y-5">
    <div class="flex items-center justify-between">
      <h1 class="text-2xl font-black text-slate-800">Hồ sơ của tôi</h1>
      <router-link to="/portal/nop-ho-so" class="flex items-center gap-2 px-4 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20">
        <span class="material-symbols-outlined text-sm">add</span>Nộp mới
      </router-link>
    </div>

    <!-- Filter tabs -->
    <div class="flex gap-2 overflow-x-auto pb-1">
      <button v-for="t in tabs" :key="t.key" @click="activeTab=t.key"
        :class="['px-4 py-2 rounded-xl text-sm font-bold whitespace-nowrap transition-colors',
          activeTab===t.key ? 'bg-primary text-white shadow-sm' : 'bg-white text-slate-500 border border-slate-200 hover:bg-slate-50']">
        {{ t.label }} <span class="ml-1 text-xs">({{ t.count }})</span>
      </button>
    </div>

    <!-- List -->
    <div class="space-y-3">
      <div v-if="!filtered.length" class="bg-white rounded-2xl border border-slate-200 p-12 text-center">
        <span class="material-symbols-outlined text-4xl text-slate-200 block mb-2">folder_open</span>
        <p class="text-slate-400 font-semibold">Chưa có hồ sơ nào</p>
        <router-link to="/portal/nop-ho-so" class="text-primary text-sm font-bold">Nộp hồ sơ ngay</router-link>
      </div>

      <div v-for="app in filtered" :key="app.id"
        @click="$router.push('/portal/ho-so-cua-toi/'+app.id)"
        class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 cursor-pointer hover:border-primary/30 hover:shadow-md transition-all">
        <div class="flex items-start gap-4">
          <!-- Status icon -->
          <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0', stStyle(app.trang_thai).iconBg]">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;" :class="stStyle(app.trang_thai).iconColor">{{ stStyle(app.trang_thai).icon }}</span>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between gap-2 mb-1">
              <p class="font-bold text-slate-800">{{ app.ten_chuong_trinh }}</p>
              <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black flex-shrink-0', stStyle(app.trang_thai).badge]">{{ stStyle(app.trang_thai).label }}</span>
            </div>
            <p class="text-xs text-slate-400 mb-3">#HS-{{ app.id }} · Nộp {{ formatDate(app.ngay_nop) }}</p>
            <!-- Timeline dots -->
            <div class="flex items-center gap-2">
              <div v-for="(st, i) in timelineSteps" :key="i" class="flex items-center gap-1.5">
                <div :class="['w-5 h-5 rounded-full flex items-center justify-center',
                  app.step > i ? 'bg-primary' : app.step === i ? 'bg-primary ring-2 ring-primary/30' : 'bg-slate-100']">
                  <span class="material-symbols-outlined text-white" style="font-size:11px; font-variation-settings:'FILL' 1;">{{ app.step > i ? 'check' : st.icon }}</span>
                </div>
                <div v-if="i < timelineSteps.length-1" :class="['h-0.5 w-4 rounded-full', app.step > i ? 'bg-primary' : 'bg-slate-100']"></div>
              </div>
              <p class="text-xs text-slate-400 ml-1">{{ timelineSteps[Math.min(app.step, timelineSteps.length-1)].label }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { applicationsApi } from '../../api/applications'

const activeTab = ref('all')
const loading = ref(false)

const timelineSteps = [
  { icon:'upload_file',   label:'Đã nộp' },
  { icon:'manage_search', label:'Đang xét' },
  { icon:'smart_toy',     label:'AI đánh giá' },
  { icon:'check_circle',  label:'Kết quả' },
]

const apps = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const [resApps, resProgs] = await Promise.all([
      applicationsApi.getMyApplications(),
      http.get('/programs', { params: { size: 500 } }).catch(() => ({ data: [] }))
    ])
    
    const list = resApps.data?.content || resApps.data || []
    const progsList = resProgs.data?.content || resProgs.data || []
    const progMap = Object.fromEntries(progsList.map(p => [p.id, p.tenChuongTrinh || p.name || '—']))

    apps.value = list.map(a => ({
      ...a,
      id: a.id,
      ten_chuong_trinh: progMap[a.chuongTrinhId] || '—',
      trang_thai: a.trangThai || 'PENDING',
      ngay_nop: a.ngayNopHoSo || a.createdAt,
      step: a.trangThai === 'PENDING' ? 1 
          : a.trangThai === 'UNDER_REVIEW' ? 2 
          : ['APPROVED', 'REJECTED', 'PAID'].includes(a.trangThai) ? 4 : 1
    }))
  } catch {
    apps.value = []
  } finally {
    loading.value = false
  }
})

const COUNTS = computed(() => ({
  all:      apps.value.length,
  PENDING:  apps.value.filter(a=>a.trang_thai==='PENDING').length,
  APPROVED: apps.value.filter(a=>a.trang_thai==='APPROVED').length,
  REJECTED: apps.value.filter(a=>a.trang_thai==='REJECTED').length,
}))

const tabs = computed(() => [
  { key:'all',      label:'Tất cả',    count:COUNTS.value.all },
  { key:'PENDING',  label:'Chờ duyệt', count:COUNTS.value.PENDING },
  { key:'APPROVED', label:'Đã duyệt',  count:COUNTS.value.APPROVED },
  { key:'REJECTED', label:'Từ chối',  count:COUNTS.value.REJECTED },
])

const filtered = computed(() => activeTab.value === 'all' ? apps.value : apps.value.filter(a => a.trang_thai === activeTab.value))

const STATUS = {
  PENDING:  { badge:'bg-amber-100 text-amber-700',    icon:'hourglass_top', iconBg:'bg-amber-50',   iconColor:'text-amber-500', label:'Chờ duyệt' },
  APPROVED: { badge:'bg-emerald-100 text-emerald-700',icon:'check_circle',  iconBg:'bg-emerald-50', iconColor:'text-emerald-500',label:'Đã duyệt' },
  REJECTED: { badge:'bg-red-100 text-red-600',        icon:'cancel',        iconBg:'bg-red-50',     iconColor:'text-red-500',   label:'Từ chối' },
}
const stStyle = s => STATUS[s] || STATUS.PENDING
function formatDate(d) { return d ? new Date(d).toLocaleDateString('vi-VN') : '' }
</script>
