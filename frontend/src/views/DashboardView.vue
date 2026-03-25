<template>
  <div class="min-h-screen bg-slate-50/60">
    <!-- ── TOP HEADER ── -->
    <div class="bg-white border-b border-slate-200/80 px-8 py-5 flex items-center justify-between sticky top-0 z-30">
      <div>
        <h2 class="text-xl font-black text-slate-800 tracking-tight">
          {{ greeting }}, <span class="text-primary">{{ userName }}</span> 👋
        </h2>
        <p class="text-xs text-slate-400 mt-0.5">{{ todayFormatted }} · Hệ thống Quản lý Trợ cấp Thông minh</p>
      </div>
      <div class="flex items-center gap-3">
        <button @click="refresh" :disabled="loading"
          class="flex items-center gap-2 px-4 py-2 bg-slate-100 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-200 transition-colors disabled:opacity-50">
          <span :class="['material-symbols-outlined text-sm', loading ? 'animate-spin' : '']">refresh</span>
          Làm mới
        </button>

        <!-- Export dropdown -->
        <div class="relative" ref="exportDropdownRef">
          <button @click="showExportMenu = !showExportMenu"
            :disabled="exporting"
            class="flex items-center gap-2 px-4 py-2 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20 disabled:opacity-60">
            <span :class="['material-symbols-outlined text-sm', exporting ? 'animate-spin' : '']">
              {{ exporting ? 'progress_activity' : 'download' }}
            </span>
            {{ exporting ? 'Đang xuất...' : 'Xuất báo cáo' }}
            <span class="material-symbols-outlined text-sm">expand_more</span>
          </button>

          <!-- Dropdown menu -->
          <Transition name="dropdown">
            <div v-if="showExportMenu"
              class="absolute right-0 top-full mt-2 w-64 bg-white rounded-2xl border border-slate-200 shadow-xl shadow-slate-200/50 py-2 z-50">
              <p class="px-4 py-2 text-[10px] uppercase tracking-widest font-bold text-slate-400">Chọn loại báo cáo</p>
              <button v-for="opt in exportOptions" :key="opt.type"
                @click="handleExport(opt.type)"
                class="w-full flex items-center gap-3 px-4 py-3 hover:bg-slate-50 transition-colors text-left group">
                <div :class="['w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0', opt.iconBg]">
                  <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ opt.icon }}</span>
                </div>
                <div>
                  <p class="text-sm font-bold text-slate-700 group-hover:text-slate-900">{{ opt.label }}</p>
                  <p class="text-[11px] text-slate-400">{{ opt.desc }}</p>
                </div>
              </button>
            </div>
          </Transition>
        </div>
      </div>
    </div>

    <div class="p-8 space-y-8">

      <!-- ══ 1. STAT CARDS ══ -->
      <div class="grid grid-cols-2 xl:grid-cols-3 gap-5">
        <div v-for="(s, i) in statCards" :key="i"
          class="relative bg-white rounded-2xl border border-slate-200/80 p-6 shadow-sm hover:shadow-md hover:-translate-y-0.5 transition-all overflow-hidden group cursor-default">
          <div :class="['absolute -right-5 -top-5 w-24 h-24 rounded-full opacity-[0.08] transition-transform duration-500 group-hover:scale-150', s.bg]"></div>
          <div class="relative">
            <div :class="['w-11 h-11 rounded-2xl flex items-center justify-center mb-4', s.iconBg]">
              <span class="material-symbols-outlined text-lg" style="font-variation-settings:'FILL' 1;">{{ s.icon }}</span>
            </div>
            <p class="text-xs font-bold uppercase tracking-widest text-slate-400">{{ s.label }}</p>
            <p class="text-3xl font-black text-slate-800 mt-1 tracking-tight">{{ s.value }}</p>
            <div class="flex items-center gap-1.5 mt-2">
              <span :class="['material-symbols-outlined text-xs', s.trend > 0 ? 'text-emerald-500' : 'text-red-400']">
                {{ s.trend > 0 ? 'trending_up' : 'trending_down' }}
              </span>
              <span :class="['text-xs font-bold', s.trend > 0 ? 'text-emerald-600' : 'text-red-500']">
                {{ Math.abs(s.trend) }}%
              </span>
              <span class="text-xs text-slate-400">so tháng trước</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ══ 2. CẢNH BÁO ══ -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div v-for="alert in alerts" :key="alert.id"
          :class="['flex items-start gap-3 p-4 rounded-2xl border', alert.cls]">
          <span class="material-symbols-outlined text-xl flex-shrink-0 mt-0.5" style="font-variation-settings:'FILL' 1;">{{ alert.icon }}</span>
          <div>
            <p class="font-bold text-sm">{{ alert.title }}</p>
            <p class="text-xs opacity-75 mt-0.5 leading-relaxed">{{ alert.desc }}</p>
          </div>
        </div>
      </div>

      <!-- ══ 3. CHARTS ROW ══ -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <!-- 3a. Biểu đồ CỘT: hồ sơ theo chương trình -->
        <div class="lg:col-span-2 bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
          <div class="flex items-center justify-between mb-4">
            <div>
              <h4 class="font-black text-slate-800">Hồ sơ theo chương trình trợ cấp</h4>
              <p class="text-xs text-slate-400 mt-0.5">Số lượng hồ sơ nộp trong 30 ngày qua</p>
            </div>
          </div>
          <div class="h-64">
            <Bar v-if="programChartData" :data="programChartData" :options="barOptions" />
          </div>
        </div>

        <!-- 3b. Biểu đồ TRÒN: trạng thái hồ sơ -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
          <h4 class="font-black text-slate-800 mb-1">Tỷ lệ trạng thái</h4>
          <p class="text-xs text-slate-400 mb-4">Tổng hồ sơ theo trạng thái xét duyệt</p>
          <div class="h-48 flex items-center justify-center">
            <Doughnut v-if="statusChartData" :data="statusChartData" :options="doughnutOptions" />
          </div>
          <div class="mt-4 space-y-2">
            <div v-for="(item, i) in statusLegend" :key="i" class="flex items-center justify-between">
              <div class="flex items-center gap-2">
                <span class="w-2.5 h-2.5 rounded-full flex-shrink-0" :style="{ background: item.color }"></span>
                <span class="text-xs text-slate-600 font-medium">{{ item.label }}</span>
              </div>
              <span class="text-xs font-black text-slate-800">{{ item.count }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- ══ 3.5. BIỂU ĐỒ ĐƯỜNG: xu hướng 6 tháng ══ -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h4 class="font-black text-slate-800">Xu hướng hồ sơ 6 tháng gần đây</h4>
            <p class="text-xs text-slate-400 mt-0.5">So sánh hồ sơ mới, phê duyệt và từ chối</p>
          </div>
        </div>
        <div class="h-64">
          <Line v-if="trendChartData" :data="trendChartData" :options="lineOptions" />
        </div>
      </div>

      <!-- ══ 4. BIỂU ĐỒ THANH NGANG: ngân sách nguồn quỹ ══ -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <div class="flex items-center justify-between mb-4">
          <div>
            <h4 class="font-black text-slate-800">Ngân sách từng nguồn quỹ</h4>
            <p class="text-xs text-slate-400 mt-0.5">Đã sử dụng so với tổng ngân sách (tỷ đồng)</p>
          </div>
          <router-link to="/ngan-sach" class="text-xs font-bold text-primary hover:underline flex items-center gap-1">
            Xem chi tiết <span class="material-symbols-outlined text-sm">arrow_forward</span>
          </router-link>
        </div>
        <div class="h-48">
          <Bar v-if="fundChartData" :data="fundChartData" :options="fundBarOptions" />
        </div>
      </div>

      <!-- ══ 5. BOTTOM: Hồ sơ mới + Thông báo ══ -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">

        <!-- Hồ sơ mới nộp -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div class="flex items-center justify-between px-6 py-5 border-b border-slate-100">
            <div>
              <h4 class="font-black text-slate-800">Hồ sơ mới nộp</h4>
              <p class="text-xs text-slate-400 mt-0.5">Gần đây nhất</p>
            </div>
            <router-link to="/ho-so" class="text-xs font-bold text-primary hover:underline flex items-center gap-1">
              Tất cả <span class="material-symbols-outlined text-sm">arrow_forward</span>
            </router-link>
          </div>
          <div class="divide-y divide-slate-50">
            <div v-for="hs in recentHoSo" :key="hs.id"
              class="flex items-center gap-4 px-6 py-4 hover:bg-slate-50 transition-colors cursor-pointer"
              @click="$router.push(`/ho-so/${hs.id}`)">
              <div :class="['w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0 text-xs font-black', hs.avatarBg]">
                {{ hs.initials }}
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-bold text-slate-800 text-sm truncate">{{ hs.ten_nguoi_nop }}</p>
                <p class="text-xs text-slate-400 truncate">{{ hs.chuong_trinh }} · {{ hs.ngay_nop }}</p>
              </div>
              <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black flex-shrink-0', hoSoStatusClass(hs.trang_thai)]">
                {{ hoSoStatusLabel(hs.trang_thai) }}
              </span>
            </div>
          </div>
        </div>

        <!-- Thông báo gần đây -->
        <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
          <div class="flex items-center justify-between px-6 py-5 border-b border-slate-100">
            <div>
              <h4 class="font-black text-slate-800">Thông báo gần đây</h4>
              <p class="text-xs text-slate-400 mt-0.5">Hoạt động hệ thống</p>
            </div>
            <router-link to="/thong-bao" class="text-xs font-bold text-primary hover:underline flex items-center gap-1">
              Xem tất cả <span class="material-symbols-outlined text-sm">arrow_forward</span>
            </router-link>
          </div>
          <div class="divide-y divide-slate-50">
            <div v-for="n in recentNotifs" :key="n.id"
              :class="['flex items-start gap-4 px-6 py-4 hover:bg-slate-50 transition-colors cursor-pointer', !n.da_doc ? 'bg-primary/[0.02]' : '']">
              <div :class="['w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0', n.iconBg]">
                <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ n.icon }}</span>
              </div>
              <div class="flex-1 min-w-0">
                <p class="font-semibold text-slate-800 text-sm line-clamp-2">{{ n.tieu_de }}</p>
                <p class="text-xs text-slate-400 mt-0.5">{{ n.time }}</p>
              </div>
              <span v-if="!n.da_doc" class="w-2 h-2 bg-primary rounded-full flex-shrink-0 mt-1.5"></span>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Bar, Doughnut, Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale, LinearScale, BarElement,
  ArcElement, Tooltip, Legend,
  LineElement, PointElement, Filler
} from 'chart.js'
import { authStore } from '../stores/auth'
import { applicationsApi } from '../api/applications'
import { exportApi } from '../api/export'
import http from '../api/http'

ChartJS.register(CategoryScale, LinearScale, BarElement, ArcElement, Tooltip, Legend, LineElement, PointElement, Filler)

const loading = ref(true)
const exporting = ref(false)
const showExportMenu = ref(false)
const exportDropdownRef = ref(null)

// ─── Export Options ───────────────────────────────────────────
const exportOptions = [
  { type: 'beneficiaries', label: 'Đối tượng hưởng trợ cấp', desc: 'Danh sách toàn bộ đối tượng', icon: 'group', iconBg: 'bg-blue-100 text-blue-600' },
  { type: 'programs',      label: 'Chương trình trợ cấp',     desc: 'Ngân sách & tiến độ',         icon: 'campaign', iconBg: 'bg-emerald-100 text-emerald-600' },
  { type: 'applications',  label: 'Hồ sơ hỗ trợ',            desc: 'Tất cả hồ sơ đề nghị',        icon: 'folder_shared', iconBg: 'bg-amber-100 text-amber-600' },
  { type: 'payments',      label: 'Chi trả trợ cấp',         desc: 'Lịch sử giao dịch',           icon: 'payments', iconBg: 'bg-purple-100 text-purple-600' },
]

async function handleExport(type) {
  showExportMenu.value = false
  exporting.value = true
  try {
    await exportApi.download(type)
  } catch (e) {
    alert('Xuất báo cáo thất bại: ' + (e.response?.data?.message || e.message))
  } finally {
    exporting.value = false
  }
}

// Click outside → đóng menu
function handleClickOutside(e) {
  if (exportDropdownRef.value && !exportDropdownRef.value.contains(e.target)) {
    showExportMenu.value = false
  }
}
onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))

// ─── Greeting ─────────────────────────────────────────────────
const now = new Date()
const hour = now.getHours()
const greeting = hour < 12 ? 'Chào buổi sáng' : hour < 18 ? 'Chào buổi chiều' : 'Chào buổi tối'
const userName = computed(() => authStore.user?.fullName || authStore.user?.username || 'bạn')
const todayFormatted = now.toLocaleDateString('vi-VN', { weekday: 'long', day: '2-digit', month: '2-digit', year: 'numeric' })

// ─── Stats ────────────────────────────────────────────────────
const stats = ref({ total: 0, pending: 0, approved: 0, rejected: 0, paid: 0, remaining: 0 })

const statCards = computed(() => [
  { label: 'Tổng hồ sơ nộp',     value: stats.value.total.toLocaleString('vi-VN'),   icon: 'folder_shared',          iconBg: 'bg-blue-100 text-blue-600',      bg: 'bg-blue-500',    trend: 0 },
  { label: 'Chờ xét duyệt',      value: stats.value.pending.toLocaleString('vi-VN'), icon: 'pending_actions',        iconBg: 'bg-amber-100 text-amber-600',    bg: 'bg-amber-500',   trend: 0 },
  { label: 'Đã phê duyệt',       value: stats.value.approved.toLocaleString('vi-VN'),icon: 'check_circle',           iconBg: 'bg-emerald-100 text-emerald-600',bg: 'bg-emerald-500', trend: 0 },
  { label: 'Bị từ chối',         value: stats.value.rejected.toLocaleString('vi-VN'),icon: 'cancel',                 iconBg: 'bg-red-100 text-red-600',        bg: 'bg-red-500',     trend: 0 },
  { label: 'Tổng đã chi trả',    value: formatVnd(stats.value.paid),                 icon: 'payments',               iconBg: 'bg-purple-100 text-purple-600',  bg: 'bg-purple-500',  trend: 0 },
  { label: 'Ngân sách còn lại',  value: formatVnd(stats.value.remaining),            icon: 'account_balance_wallet', iconBg: 'bg-teal-100 text-teal-600',      bg: 'bg-teal-500',    trend: 0 },
])

// ─── Alerts ─── (loaded from API data)
const alerts = ref([])

// ─── Program Bar Chart ────────────────────────────────────────
const programChartData = ref(null)
const barOptions = {
  responsive: true, maintainAspectRatio: false,
  plugins: { legend: { display: false }, tooltip: { callbacks: { label: ctx => `${ctx.parsed.y} hồ sơ` } } },
  scales: {
    x: { grid: { display: false }, ticks: { font: { size: 11, weight: '600' }, color: '#94a3b8' } },
    y: { grid: { color: '#f1f5f9' }, ticks: { font: { size: 11 }, color: '#94a3b8' }, beginAtZero: true },
  },
  borderRadius: 8,
  borderSkipped: false,
}

// ─── Status Doughnut Chart ────────────────────────────────────
const statusChartData = ref(null)
const doughnutOptions = {
  responsive: true, maintainAspectRatio: false, cutout: '72%',
  plugins: {
    legend: { display: false },
    tooltip: { callbacks: { label: ctx => ` ${ctx.label}: ${ctx.parsed} hồ sơ` } }
  },
}
const statusLegend = ref([])

// ─── Trend Line Chart ─────────────────────────────────────────
const trendChartData = ref(null)
const lineOptions = {
  responsive: true, maintainAspectRatio: false,
  plugins: {
    legend: { position: 'bottom', labels: { font: { size: 11, weight: '600' }, color: '#64748b', boxWidth: 12, padding: 16, usePointStyle: true } },
    tooltip: {
      mode: 'index', intersect: false,
      callbacks: { label: ctx => ` ${ctx.dataset.label}: ${ctx.parsed.y} hồ sơ` }
    }
  },
  interaction: { mode: 'index', intersect: false },
  scales: {
    x: { grid: { display: false }, ticks: { font: { size: 11, weight: '600' }, color: '#94a3b8' } },
    y: { grid: { color: '#f1f5f9' }, ticks: { font: { size: 11 }, color: '#94a3b8' }, beginAtZero: true },
  },
  elements: { line: { tension: 0.35 }, point: { radius: 4, hoverRadius: 6 } },
}

// ─── Fund Bar Chart (horizontal) ─────────────────────────────
const fundChartData = ref(null)
const fundBarOptions = {
  responsive: true, maintainAspectRatio: false, indexAxis: 'y',
  plugins: { legend: { position: 'bottom', labels: { font: { size: 11 }, color: '#64748b', boxWidth: 12, padding: 16 } },
    tooltip: { callbacks: { label: ctx => ` ${ctx.dataset.label}: ${ctx.parsed.x.toFixed(1)} Tỷ` } }
  },
  scales: {
    x: { stacked: true, grid: { color: '#f1f5f9' }, ticks: { color: '#94a3b8', font: { size: 11 }, callback: v => v + ' Tỷ' } },
    y: { stacked: true, grid: { display: false }, ticks: { color: '#475569', font: { size: 12, weight: '600' } } },
  },
  borderRadius: 4, borderSkipped: false,
}

// ─── Recent Data ──────────────────────────────────────────────
const recentHoSo = ref([])
const recentNotifs = ref([])

// ─── Load ─────────────────────────────────────────────────────
onMounted(() => loadData())

async function loadData() {
  loading.value = true

  // Thử gọi API cho stats
  try {
    const res = await applicationsApi.getStats()
    const d = res.data || {}
    stats.value = {
      total: d.total ?? 0,
      pending: d.SUBMITTED ?? d.pending ?? 0,
      approved: d.APPROVED ?? d.approved ?? 0,
      rejected: d.REJECTED ?? d.rejected ?? 0,
      paid: d.PAID ?? d.paid ?? 0,
      remaining: d.remaining ?? d.con_lai ?? 0,
    }
  } catch (e) {
    console.error('Lỗi tải thống kê:', e)
    stats.value = { total: 0, pending: 0, approved: 0, rejected: 0, paid: 0, remaining: 0 }
  }

  // Program Bar Chart — lấy từ API /programs
  try {
    const progRes = await http.get('/programs', { params: { size: 100 } })
    const progList = progRes.data?.content || progRes.data || []
    if (progList.length) {
      const progNames = progList.map(p => (p.tenChuongTrinh || p.name || '—').split(' ').slice(0, 3).join(' '))
      const progCounts = progList.map(p => p.soHoSo ?? 0)
      const bgColors = ['#3b82f6', '#6366f1', '#10b981', '#f59e0b', '#ef4444', '#14b8a6', '#8b5cf6', '#06b6d4']
      programChartData.value = {
        labels: progNames,
        datasets: [{ data: progCounts, backgroundColor: bgColors.slice(0, progNames.length), hoverBackgroundColor: bgColors.slice(0, progNames.length) }]
      }
    }
  } catch { /* charts optional */ }

  // Doughnut data — from stats
  const statusData = [stats.value.pending, stats.value.approved, stats.value.rejected, 0]
  const statusColors = ['#f59e0b', '#10b981', '#ef4444', '#6366f1']
  const statusLabels = ['Chờ duyệt', 'Phê duyệt', 'Từ chối', 'Đang xử lý']
  statusChartData.value = {
    labels: statusLabels,
    datasets: [{ data: statusData, backgroundColor: statusColors, borderWidth: 0, hoverOffset: 6 }]
  }
  statusLegend.value = statusLabels.map((l, i) => ({ label: l, color: statusColors[i], count: statusData[i] }))

  // Trend Line Chart — chưa có API trend, hiển thị trống
  trendChartData.value = null

  // Fund horizontal bar — lấy từ API /nguon-quy
  try {
    const fundRes = await http.get('/nguon-quy', { params: { size: 50 } })
    const fundList = fundRes.data?.content || fundRes.data || []
    if (fundList.length) {
      const fundNames = fundList.map(f => f.tenNguonQuy || f.ten_nguon_quy || '—')
      const spentArr = fundList.map(f => (f.daSuDung || f.da_su_dung || 0) / 1e9)
      const remainArr = fundList.map(f => (f.conLai || f.con_lai || 0) / 1e9)
      fundChartData.value = {
        labels: fundNames,
        datasets: [
          { label: 'Đã chi',  data: spentArr,  backgroundColor: '#f59e0b', hoverBackgroundColor: '#d97706' },
          { label: 'Còn lại', data: remainArr, backgroundColor: '#3b82f6', hoverBackgroundColor: '#2563eb' },
        ]
      }
    }
  } catch { /* charts optional */ }

  // Recent hồ sơ — thử từ API
  const bgs = ['bg-blue-100 text-blue-700', 'bg-emerald-100 text-emerald-700', 'bg-amber-100 text-amber-700', 'bg-purple-100 text-purple-700', 'bg-red-100 text-red-700']
  try {
    const [resApps, resUsers, resProgs] = await Promise.all([
      applicationsApi.getAll({ page: 0, size: 5, sort: 'createdAt,desc' }),
      http.get('/users', { params: { size: 1000 } }).catch(()=>({data:[]})),
      http.get('/programs', { params: { size: 500 } }).catch(()=>({data:[]}))
    ])

    const list = resApps.data?.content || resApps.data || []
    const usersList = resUsers.data?.content || resUsers.data || []
    const progsList = resProgs.data?.content || resProgs.data || []

    const userMap = Object.fromEntries(usersList.map(u => [u.id, u.fullName || u.username]))
    const userMapByUsername = Object.fromEntries(usersList.map(u => [u.username, u.fullName || u.username]))
    const progMap = Object.fromEntries(progsList.map(p => [p.id, p.tenChuongTrinh || p.name || '—']))

    if (list.length) {
      recentHoSo.value = list.map((hs, i) => {
        const uName = userMap[hs.nguoiDungId] || userMapByUsername[hs.nguoiDungId] || 'Người nộp #' + (hs.nguoiDungId||'').substring(0,4)
        return {
          id: hs.id,
          ten_nguoi_nop: uName,
          chuong_trinh: progMap[hs.chuongTrinhId] || '—',
          ngay_nop: hs.ngayNopHoSo || hs.createdAt ? new Date(hs.ngayNopHoSo || hs.createdAt).toLocaleDateString('vi-VN') : '—',
          trang_thai: hs.trangThai || 'PENDING',
          initials: (uName || 'X').split(' ').slice(-1)[0][0].toUpperCase(),
          avatarBg: bgs[i % bgs.length],
        }
      })
    } else throw new Error('empty')
  } catch (e) {
    console.error('Lỗi tải hồ sơ gần đây:', e)
    recentHoSo.value = []
  }

  // Recent notifs — from API
  try {
    const notifIconMap = {
      'APPLICATION_APPROVED': { icon: 'check_circle', iconBg: 'bg-emerald-100 text-emerald-600' },
      'APPLICATION_REJECTED': { icon: 'cancel', iconBg: 'bg-red-100 text-red-600' },
      'AI_REVIEW':            { icon: 'psychology', iconBg: 'bg-blue-100 text-blue-600' },
      'PAYMENT':              { icon: 'payments', iconBg: 'bg-purple-100 text-purple-600' },
      'WARNING':              { icon: 'warning', iconBg: 'bg-amber-100 text-amber-600' },
    }
    const defaultIcon = { icon: 'notifications', iconBg: 'bg-slate-100 text-slate-600' }

    const resNotifs = await http.get('/notifications', { params: { page: 0, size: 5 } })
    const notifsList = resNotifs.data?.content || resNotifs.data || []
    recentNotifs.value = notifsList.map(n => {
      const iconInfo = notifIconMap[n.loai] || defaultIcon
      return {
        id: n.id,
        tieu_de: n.tieuDe || n.noiDung || 'Thông báo',
        icon: iconInfo.icon,
        iconBg: iconInfo.iconBg,
        da_doc: n.daDoc || false,
        time: n.createdAt ? timeAgo(n.createdAt) : '—',
      }
    })
  } catch {
    recentNotifs.value = []
  }

  loading.value = false
}

// ─── Helpers ──────────────────────────────────────────────────
function formatVnd(v) {
  if (!v && v !== 0) return '—'
  if (v >= 1e9) return (v / 1e9).toFixed(1) + ' Tỷ'
  if (v >= 1e6) return (v / 1e6).toFixed(0) + ' Tr'
  return v.toLocaleString('vi-VN') + 'đ'
}

function hoSoStatusLabel(s) {
  return { PENDING: 'Chờ duyệt', APPROVED: 'Phê duyệt', REJECTED: 'Từ chối', REVIEWING: 'Đang xem' }[s] || s
}
function hoSoStatusClass(s) {
  return {
    PENDING:   'bg-amber-100 text-amber-700',
    APPROVED:  'bg-emerald-100 text-emerald-700',
    REJECTED:  'bg-red-100 text-red-700',
    REVIEWING: 'bg-blue-100 text-blue-700',
  }[s] || 'bg-slate-100 text-slate-600'
}

function refresh() { loadData() }

function timeAgo(dateStr) {
  const now = Date.now()
  const d = new Date(dateStr).getTime()
  const diff = now - d
  const mins = Math.floor(diff / 60000)
  if (mins < 1) return 'Vừa xong'
  if (mins < 60) return `${mins} phút trước`
  const hours = Math.floor(mins / 60)
  if (hours < 24) return `${hours} giờ trước`
  const days = Math.floor(hours / 24)
  if (days === 1) return 'Hôm qua'
  return `${days} ngày trước`
}
</script>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.2s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.95);
}
</style>
