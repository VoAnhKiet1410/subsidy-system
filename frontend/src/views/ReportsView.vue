<template>
  <div class="p-8 space-y-10">
    <!-- Header -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-6">
      <div>
        <h2 class="text-3xl font-extrabold editorial-tight text-on-surface mb-2">Báo cáo &amp; Thống kê</h2>
        <p class="text-on-surface-variant max-w-xl">Phân tích dữ liệu chương trình trợ cấp, hiệu quả giải ngân và phân bổ ngân sách.</p>
      </div>
      <button @click="exportAllReports" class="flex items-center gap-2 px-5 py-2.5 bg-primary text-white font-semibold rounded-xl hover:opacity-90 transition-opacity text-sm shadow-md">
        <span class="material-symbols-outlined text-xl">download</span>
        {{ exporting ? 'Đang xuất...' : 'Tải Tất Cả Báo Cáo' }}
      </button>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="flex justify-center py-12">
      <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
    </div>

    <template v-else>
      <!-- Main Chart + Summary -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- Chart Area -->
        <div class="lg:col-span-2 bg-surface-container-lowest p-8 rounded-2xl shadow-sm">
          <div class="flex items-center justify-between mb-8">
            <div>
              <h3 class="text-lg font-bold text-on-surface">Giải ngân theo chương trình</h3>
              <p class="text-sm text-on-surface-variant">So sánh ngân sách và chi tiêu thực tế</p>
            </div>
          </div>
          <div class="flex items-end justify-between gap-4 px-2" style="height:280px">
            <div v-if="!chartBars.length" class="w-full flex items-center justify-center text-slate-400 text-sm">Chưa có dữ liệu</div>
            <div v-for="bar in chartBars" :key="bar.label" class="flex-1 flex flex-col items-center gap-1 group relative" style="height:100%; min-width:0">
              <!-- Tooltip -->
              <div class="absolute bottom-10 left-1/2 -translate-x-1/2 bg-slate-800 text-white text-[11px] rounded-lg px-3 py-2 whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none z-10 shadow-lg">
                <div class="flex items-center gap-1.5 mb-0.5"><span class="w-2 h-2 rounded-full bg-primary-fixed-dim inline-block"></span>Ngân sách: {{ formatBudget(bar.budget) }}</div>
                <div class="flex items-center gap-1.5"><span class="w-2 h-2 rounded-full bg-primary inline-block"></span>Đã chi: {{ formatBudget(bar.spent) }}</div>
                <div class="absolute left-1/2 -translate-x-1/2 top-full w-0 h-0" style="border-left:5px solid transparent;border-right:5px solid transparent;border-top:5px solid #1e293b"></div>
              </div>
              <div class="w-full flex items-end gap-1" style="height:calc(100% - 36px)">
                <div class="bg-primary-fixed-dim w-1/2 rounded-t-lg transition-all group-hover:opacity-80" :style="{ height: bar.budgetPct }"></div>
                <div class="bg-primary w-1/2 rounded-t-lg transition-all group-hover:opacity-80" :style="{ height: bar.spentPct }"></div>
              </div>
              <span class="text-[10px] font-bold text-slate-400 text-center leading-tight w-full" style="display:block; overflow:visible; white-space:normal; word-break:break-word">{{ bar.label }}</span>
            </div>
          </div>
          <div class="mt-8 pt-6 border-t border-slate-100 flex items-center justify-center gap-8">
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full bg-primary-fixed-dim"></span>
              <span class="text-xs font-semibold text-on-surface-variant">Ngân sách</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="w-3 h-3 rounded-full bg-primary"></span>
              <span class="text-xs font-semibold text-on-surface-variant">Đã chi</span>
            </div>
          </div>
        </div>

        <!-- Summary Cards -->
        <div class="space-y-6">
          <div class="bg-primary p-7 rounded-2xl text-white shadow-lg shadow-primary/10 relative overflow-hidden">
            <div class="absolute -right-2 -bottom-2 opacity-10">
              <span class="material-symbols-outlined text-[100px]">analytics</span>
            </div>
            <p class="text-sm font-bold opacity-80 mb-1">Tổng ngân sách</p>
            <h3 class="text-3xl font-black">{{ formatBudget(dashStats?.totalBudget) }}</h3>
            <p class="text-xs opacity-60 mt-2">{{ programs.length }} chương trình</p>
          </div>
          <div class="bg-surface-container-lowest p-7 rounded-2xl shadow-sm">
            <p class="text-xs font-bold text-on-surface-variant uppercase tracking-widest mb-1">Đã giải ngân</p>
            <h3 class="text-3xl font-black text-on-surface">{{ formatBudget(dashStats?.totalSpent) }}</h3>
            <p class="text-xs text-on-surface-variant mt-2">{{ usedPct }}% đã sử dụng</p>
          </div>
          <div class="bg-surface-container-lowest p-7 rounded-2xl shadow-sm">
            <p class="text-xs font-bold text-on-surface-variant uppercase tracking-widest mb-1">Tỷ lệ phê duyệt</p>
            <h3 class="text-3xl font-black text-primary">{{ dashStats?.approvalRate || 0 }}%</h3>
            <p class="text-xs text-on-surface-variant mt-2">{{ dashStats?.totalBeneficiaries || 0 }} đối tượng</p>
          </div>
        </div>
      </div>

      <!-- Programs Table -->
      <div class="bg-surface-container-lowest rounded-2xl shadow-sm overflow-hidden">
        <div class="px-8 py-6 border-b border-slate-50">
          <h3 class="text-lg font-bold text-on-surface">Chi tiết Chương trình</h3>
        </div>
        <div v-if="programs.length === 0" class="p-12 text-center text-on-surface-variant">Chưa có chương trình nào</div>
        <div v-else class="overflow-x-auto">
          <table class="w-full text-left min-w-[600px]">
            <thead>
              <tr class="bg-slate-50/50">
                <th class="px-8 py-4 text-[10px] font-black text-on-surface-variant uppercase tracking-widest">Chương trình</th>
                <th class="px-8 py-4 text-[10px] font-black text-on-surface-variant uppercase tracking-widest">Ngân sách</th>
                <th class="px-8 py-4 text-[10px] font-black text-on-surface-variant uppercase tracking-widest">Đã chi</th>
                <th class="px-8 py-4 text-[10px] font-black text-on-surface-variant uppercase tracking-widest">Đối tượng</th>
                <th class="px-8 py-4 text-[10px] font-black text-on-surface-variant uppercase tracking-widest">Trạng thái</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-50">
              <tr v-for="p in programs" :key="p.id" class="hover:bg-slate-50/50 transition-colors">
                <td class="px-8 py-5">
                  <div class="flex items-center gap-3">
                    <span class="material-symbols-outlined text-primary">{{ p.icon || 'assignment' }}</span>
                    <span class="text-sm font-bold text-on-surface">{{ p.name }}</span>
                  </div>
                </td>
                <td class="px-8 py-5 text-sm font-bold">{{ formatBudget(p.budget) }}</td>
                <td class="px-8 py-5 text-sm">{{ formatBudget(p.spent) }}</td>
                <td class="px-8 py-5 text-sm">{{ (p.beneficiaryCount || 0).toLocaleString('vi-VN') }}</td>
                <td class="px-8 py-5">
                  <span :class="['px-2.5 py-1 rounded-lg text-[11px] font-bold', statusClass(p.status)]">{{ statusLabel(p.status) }}</span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Category Distribution -->
      <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-sm">
        <h3 class="text-lg font-bold text-on-surface mb-6">Phân bổ theo danh mục</h3>
        <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div v-for="(pct, cat) in (dashStats?.categoryDistribution || {})" :key="cat" class="bg-surface-container-low p-5 rounded-xl">
            <span class="material-symbols-outlined text-primary text-2xl mb-2">{{ categoryIcons[cat] || 'category' }}</span>
            <p class="text-xs font-bold text-on-surface-variant">{{ categoryLabels[cat] || cat }}</p>
            <p class="text-2xl font-black text-on-surface">{{ pct }}%</p>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { dashboardApi } from '../api/dashboard'
import { programsApi } from '../api/programs'

const loading = ref(true)
const exporting = ref(false)
const dashStats = ref(null)
const programs = ref([])

const categoryIcons = { THU_NHAP_THAP: 'home', KHUYET_TAT: 'accessible', NGUOI_GIA: 'elderly', TRE_EM: 'child_care', DON_THAN: 'person', HIV: 'medical_services', THIEN_TAI: 'storm' }
const categoryLabels = { THU_NHAP_THAP: 'Thu nhập thấp', KHUYET_TAT: 'Khuyết tật', NGUOI_GIA: 'Người già', TRE_EM: 'Trẻ em', DON_THAN: 'Đơn thân', HIV: 'HIV/AIDS', THIEN_TAI: 'Thiên tai' }

onMounted(async () => {
  try {
    const [dashRes, progRes] = await Promise.all([
      dashboardApi.getStats(),
      programsApi.getAll({ size: 1000 }),
    ])
    dashStats.value = dashRes.data

    const beneficiaryCounts = await dashboardApi.getBeneficiaryCounts()
      .then(r => r.data || {}).catch(() => {})
    const rawPrograms = progRes.data?.content || progRes.data || []
    programs.value = rawPrograms.map(p => ({
      id: p.id,
      name: p.tenChuongTrinh || p.ten || '',
      budget: p.nganSach || 0,
      spent: p.daChi || 0,
      status: p.trangThai || '',
      beneficiaryCount: beneficiaryCounts[p.id] || 0,
    }))

    const totalBudget = programs.value.reduce((s, p) => s + p.budget, 0)
    const totalSpent  = programs.value.reduce((s, p) => s + p.spent, 0)
    const d = dashStats.value || {}
    const totalApps   = (d.approvedApplications || 0) + (d.rejectedApplications || 0)
    dashStats.value = {
      ...d,
      totalBudget,
      totalSpent,
      approvalRate: totalApps > 0 ? Math.round((d.approvedApplications || 0) / totalApps * 100) : 0,
      totalBeneficiaries: d.totalBeneficiaries || 0,
    }
  } catch (e) {
    console.error('Lỗi tải báo cáo:', e)
  } finally {
    loading.value = false
  }
})

const usedPct = computed(() => {
  const d = dashStats.value
  if (!d || !d.totalBudget) return 0
  return Math.round(d.totalSpent / d.totalBudget * 100)
})

const chartBars = computed(() => {
  if (!programs.value.length) return []
  const active = programs.value.filter(p => (p.budget || 0) > 0 || (p.spent || 0) > 0)
  if (!active.length) return []

  // Gom nhóm theo từ đầu tiên của tên
  const grouped = {}
  for (const p of active) {
    const key = (p.name || '').split(' ').slice(0, 2).join(' ') || 'Khác'
    if (!grouped[key]) grouped[key] = { budget: 0, spent: 0 }
    grouped[key].budget += p.budget || 0
    grouped[key].spent  += p.spent  || 0
  }

  const entries = Object.entries(grouped)
  const maxVal = Math.max(...entries.map(([, v]) => Math.max(v.budget, v.spent)), 1)
  return entries.map(([label, v]) => ({
    label,
    budgetPct: Math.round(v.budget / maxVal * 100) + '%',
    spentPct:  Math.round(v.spent  / maxVal * 100) + '%',
    budget: v.budget,
    spent:  v.spent,
  }))
})

function formatBudget(val) {
  if (!val) return '0'
  if (val >= 1e9) return (val / 1e9).toFixed(1) + ' Tỷ'
  if (val >= 1e6) return (val / 1e6).toFixed(0) + ' Tr'
  return val.toLocaleString('vi-VN')
}

function statusClass(s) {
  if (s === 'OPEN') return 'bg-emerald-50 text-emerald-700'
  if (s === 'PAUSED') return 'bg-amber-50 text-amber-700'
  return 'bg-slate-100 text-slate-600'
}
function statusLabel(s) {
  if (s === 'OPEN') return 'Đang mở'
  if (s === 'PAUSED') return 'Tạm dừng'
  return 'Đã kết thúc'
}

async function exportAllReports() {
  exporting.value = true
  try {
    const { exportApi } = await import('../api/export')
    const types = ['beneficiaries', 'programs', 'applications', 'payments']
    for (const type of types) {
      await exportApi.download(type)
      // Delay nhỏ giữa mỗi file để browser xử lý download
      await new Promise(r => setTimeout(r, 500))
    }
  } catch (e) {
    alert('Xuất báo cáo thất bại: ' + e.message)
  } finally {
    exporting.value = false
  }
}
</script>
