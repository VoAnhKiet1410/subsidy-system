<template>
  <div class="space-y-6">

    <!-- HERO -->
    <div class="bg-gradient-to-br from-[#1a56db] via-[#1e40af] to-[#312e81] rounded-3xl px-7 py-8 text-white relative overflow-hidden">
      <!-- Decoration circles -->
      <div class="absolute -right-6 -top-6 w-44 h-44 bg-white/5 rounded-full"></div>
      <div class="absolute right-12 -bottom-8 w-28 h-28 bg-white/5 rounded-full"></div>
      <div class="absolute left-1/2 bottom-0 w-16 h-16 bg-indigo-400/10 rounded-full -translate-x-10"></div>

      <div class="relative z-10">
        <!-- Greeting badge -->
        <div class="inline-flex items-center gap-2 bg-white/15 backdrop-blur-sm px-3 py-1.5 rounded-full mb-4 border border-white/20">
          <span class="w-2 h-2 bg-emerald-400 rounded-full animate-pulse flex-shrink-0"></span>
          <span class="text-white/90 text-xs font-semibold">Xin chào, {{ displayName }}!</span>
        </div>

        <!-- Main title -->
        <h1 class="text-3xl sm:text-4xl font-black leading-tight mb-3 tracking-tight">
          Hệ thống
          <span class="text-yellow-300">Trợ cấp</span><br>
          Xã hội trực tuyến
        </h1>

        <!-- Subtitle -->
        <p class="text-white/70 text-sm leading-relaxed mb-6 max-w-xs">
          Đăng ký, nộp hồ sơ và theo dõi trạng thái<br class="hidden sm:block"> trợ cấp của bạn mọi lúc, mọi nơi.
        </p>

        <!-- Actions -->
        <div class="flex gap-3 flex-wrap">
          <router-link to="/portal/nop-ho-so"
            class="flex items-center gap-2 px-5 py-2.5 bg-white text-primary text-sm font-black rounded-2xl hover:bg-yellow-50 shadow-lg shadow-black/20 transition-all">
            <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">add_circle</span>
            Nộp hồ sơ mới
          </router-link>
          <router-link to="/portal/ho-so-cua-toi"
            class="flex items-center gap-2 px-5 py-2.5 bg-white/15 text-white text-sm font-bold rounded-2xl hover:bg-white/25 border border-white/25 backdrop-blur-sm transition-all">
            <span class="material-symbols-outlined text-sm">folder_open</span>
            Hồ sơ của tôi
          </router-link>
        </div>
      </div>
    </div>

    <!-- QUICK STATS -->
    <div class="grid grid-cols-3 gap-3">
      <div v-for="s in quickStats" :key="s.label" class="bg-white rounded-2xl p-4 border border-slate-200/80 shadow-sm text-center">
        <div :class="['w-10 h-10 rounded-xl flex items-center justify-center mx-auto mb-2', s.iconBg]">
          <span class="material-symbols-outlined text-lg" style="font-variation-settings:'FILL' 1;" :class="s.iconColor">{{ s.icon }}</span>
        </div>
        <p class="text-2xl font-black text-slate-800">{{ s.value }}</p>
        <p class="text-[10px] text-slate-400 font-semibold mt-0.5 leading-tight">{{ s.label }}</p>
      </div>
    </div>

    <!-- MY RECENT APPLICATIONS -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="font-black text-slate-800">Hồ sơ gần đây</h2>
        <router-link to="/portal/ho-so-cua-toi" class="text-xs font-bold text-primary flex items-center gap-1">
          Xem tất cả<span class="material-symbols-outlined text-sm">chevron_right</span>
        </router-link>
      </div>
      <div class="space-y-3">
        <div v-for="app in recentApps" :key="app.id"
          @click="$router.push('/portal/ho-so-cua-toi/'+app.id)"
          class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-4 cursor-pointer hover:border-primary/30 hover:shadow-md transition-all">
          <div class="flex items-start gap-3">
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0', stStyle(app.trang_thai).iconBg]">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;" :class="stStyle(app.trang_thai).iconColor">{{ stStyle(app.trang_thai).icon }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-center justify-between gap-2">
                <p class="font-bold text-slate-800 text-sm truncate">{{ app.ten_chuong_trinh }}</p>
                <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black flex-shrink-0', stStyle(app.trang_thai).badge]">{{ stStyle(app.trang_thai).label }}</span>
              </div>
              <p class="text-xs text-slate-400 mt-0.5">{{ app.maHoSo || '#HS-' + (app.id?.substring(0,8) || '—') }} · Nộp {{ formatDate(app.ngay_nop) }}</p>
              <!-- Mini timeline -->
              <div class="flex items-center gap-1 mt-2">
                <div v-for="(step, i) in app.steps" :key="i"
                  :class="['h-1.5 flex-1 rounded-full', step <= app.step_done ? 'bg-primary' : 'bg-slate-100']"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- OPEN PROGRAMS PREVIEW -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="font-black text-slate-800">Chương trình đang mở</h2>
        <router-link to="/portal/chuong-trinh" class="text-xs font-bold text-primary flex items-center gap-1">
          Xem tất cả<span class="material-symbols-outlined text-sm">chevron_right</span>
        </router-link>
      </div>
      <div class="grid grid-cols-1 sm:grid-cols-2 gap-3">
        <div v-for="p in openPrograms" :key="p.id"
          class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 hover:border-primary/30 hover:shadow-md transition-all">
          <div class="flex items-start gap-3 mb-3">
            <div class="w-11 h-11 rounded-2xl bg-primary/10 flex items-center justify-center flex-shrink-0">
              <span class="material-symbols-outlined text-primary" style="font-variation-settings:'FILL' 1;">volunteer_activism</span>
            </div>
            <div>
              <p class="font-bold text-slate-800 text-sm leading-snug">{{ p.ten }}</p>
              <p class="text-xs text-slate-400 mt-0.5">Hạn: {{ p.han_nop }}</p>
            </div>
          </div>
          <p class="text-xs text-slate-500 mb-4 leading-relaxed">{{ p.mo_ta }}</p>
          <router-link :to="'/portal/nop-ho-so?program='+p.id"
            class="flex items-center justify-center gap-2 w-full py-2.5 bg-primary text-white text-xs font-bold rounded-xl hover:opacity-90">
            <span class="material-symbols-outlined text-sm">edit_document</span>Nộp hồ sơ
          </router-link>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { authStore } from '../../stores/auth'
import { applicationsApi } from '../../api/applications'
import { programsApi } from '../../api/programs'
import http from '../../api/http'

const displayName = computed(() => {
  const u = authStore.user
  if (!u) return 'bạn'
  return u.fullName || u.username || 'bạn'
})

const loading = ref(false)

// Stats
const quickStats = ref([
  { label:'Hồ sơ đã nộp', value:0, icon:'folder_open',  iconBg:'bg-primary/10', iconColor:'text-primary' },
  { label:'Chờ duyệt',    value:0, icon:'hourglass_top', iconBg:'bg-amber-100',  iconColor:'text-amber-600' },
  { label:'Đã phê duyệt', value:0, icon:'check_circle',  iconBg:'bg-emerald-100',iconColor:'text-emerald-600' },
])

const recentApps = ref([])
const openPrograms = ref([])


onMounted(async () => {
  loading.value = true
  try {
    // Lấy toàn bộ chương trình trước để join dữ liệu
    const pRes = await http.get('/programs', { params: { size: 1000 } }).catch(() => ({ data: { content: [] } }))
    const allProgs = pRes.data?.content || pRes.data || []

    const [appsRes, statsRes] = await Promise.allSettled([
      applicationsApi.getMyApplications(0, 3),
      applicationsApi.getMyStats(),
    ])

    // Hồ sơ gần đây
    if (appsRes.status === 'fulfilled') {
      const raw = appsRes.value.data
      const list = raw?.content || (Array.isArray(raw) ? raw : [])
      recentApps.value = list.slice(0, 3).map(a => {
        const prog = allProgs.find(p => p.id === a.chuongTrinhId)
        const status = a.trangThai || a.trang_thai || 'SUBMITTED'
        return {
          ...a,
          id: a.id,
          maHoSo: a.maHoSo || a.ma_ho_so,
          trang_thai: status,
          ten_chuong_trinh: prog ? prog.tenChuongTrinh : '—',
          ngay_nop: a.ngayNopHoSo || a.ngay_nop_ho_so || a.createdAt || Date.now(),
          steps: [1, 2, 3, 4],
          step_done: ['APPROVED','PAID'].includes(status) ? 4 : status === 'UNDER_REVIEW' ? 2 : 1
        }
      })
    } else {
      recentApps.value = []
    }

    // Stats
    if (statsRes.status === 'fulfilled' && statsRes.value.data) {
      const s = statsRes.value.data?.data || statsRes.value.data
      if (s) {
        quickStats.value[0].value = s.total || 0
        quickStats.value[1].value = s.pending || 0
        quickStats.value[2].value = s.approved || 0
      }
    } else {
      quickStats.value[0].value = recentApps.value.length
      quickStats.value[1].value = recentApps.value.filter(a => a.trang_thai === 'PENDING' || a.trang_thai === 'SUBMITTED' || a.trang_thai === 'UNDER_REVIEW').length
      quickStats.value[2].value = recentApps.value.filter(a => a.trang_thai === 'APPROVED' || a.trang_thai === 'PAID').length
    }

    // Chương trình đang mở
    const openProgsOrigin = allProgs.filter(p => p.trangThai === 'ACTIVE' || p.trang_thai === 'ACTIVE' || p.trangThai === 'OPEN')
    if (openProgsOrigin.length) {
      openPrograms.value = openProgsOrigin.slice(0, 4).map(p => ({
        id: p.id,
        ten: p.tenChuongTrinh || p.ten_chuong_trinh || p.ten,
        han_nop: p.ngayKetThuc || p.ngay_ket_thuc ? new Date(p.ngayKetThuc || p.ngay_ket_thuc).toLocaleDateString('vi-VN') : '—',
        mo_ta: p.moTa || p.mo_ta || '',
      }))
    } else {
      openPrograms.value = []
    }
  } catch (err) {
    // Fallback toàn bộ
    console.error(err)
    recentApps.value = []
    openPrograms.value = []
    quickStats.value[0].value = 0
    quickStats.value[1].value = 0
    quickStats.value[2].value = 0
  } finally {
    loading.value = false
  }
})

const STATUS = {
  DRAFT:        { badge:'bg-slate-100 text-slate-600',     icon:'draft',         iconBg:'bg-slate-100',   iconColor:'text-slate-500',  label:'Bản nháp' },
  SUBMITTED:    { badge:'bg-amber-100 text-amber-700',     icon:'hourglass_top', iconBg:'bg-amber-100',   iconColor:'text-amber-600',  label:'Chờ duyệt' },
  PENDING:      { badge:'bg-amber-100 text-amber-700',     icon:'hourglass_top', iconBg:'bg-amber-100',   iconColor:'text-amber-600',  label:'Chờ duyệt' },
  UNDER_REVIEW: { badge:'bg-blue-100 text-blue-700',       icon:'manage_search', iconBg:'bg-blue-100',    iconColor:'text-blue-600',   label:'Đang xét duyệt' },
  APPROVED:     { badge:'bg-emerald-100 text-emerald-700', icon:'check_circle',  iconBg:'bg-emerald-100', iconColor:'text-emerald-600',label:'Đã duyệt' },
  REJECTED:     { badge:'bg-red-100 text-red-600',         icon:'cancel',        iconBg:'bg-red-100',     iconColor:'text-red-500',    label:'Từ chối' },
  PAID:         { badge:'bg-teal-100 text-teal-700',       icon:'payments',      iconBg:'bg-teal-100',    iconColor:'text-teal-600',   label:'Đã chi trả' },
}
const stStyle = (s) => STATUS[s] || STATUS.SUBMITTED

function formatDate(d) { if(!d) return '—'; return new Date(d).toLocaleDateString('vi-VN') }
</script>
