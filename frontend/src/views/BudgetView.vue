<template>
  <div class="p-8 space-y-7">

    <!-- HEADER -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <p class="text-[11px] font-black uppercase tracking-widest text-primary/60 mb-1">Phân hệ Tài chính</p>
        <h2 class="text-3xl font-extrabold tracking-tight text-slate-800">Quản lý Nguồn Quỹ</h2>
        <p class="text-slate-400 text-sm mt-1">Theo dõi phân bổ và mức sử dụng ngân sách theo từng quỹ.</p>
      </div>
      <div class="flex gap-3">
        <button @click="exportCsv" class="flex items-center gap-2 px-4 py-2.5 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50">
          <span class="material-symbols-outlined text-sm">download</span>Xuất CSV
        </button>
        <button @click="openAdd" class="flex items-center gap-2 px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-lg shadow-primary/20">
          <span class="material-symbols-outlined text-sm">add_circle</span>Thêm nguồn quỹ
        </button>
      </div>
    </div>

    <!-- STAT CARDS -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-5">
      <div v-for="s in stats" :key="s.label" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
        <div class="flex items-center justify-between mb-3">
          <span :class="['w-9 h-9 rounded-xl flex items-center justify-center', s.iconBg]">
            <span class="material-symbols-outlined text-lg" style="font-variation-settings:'FILL' 1;" :class="s.iconColor">{{ s.icon }}</span>
          </span>
          <span v-if="s.warn" class="text-[10px] font-black text-red-500 bg-red-50 px-2 py-0.5 rounded-full">Cảnh báo</span>
        </div>
        <p class="text-2xl font-black text-slate-800">{{ s.value }}</p>
        <p class="text-xs text-slate-400 mt-0.5">{{ s.label }}</p>
      </div>
    </div>

    <!-- LOW BUDGET ALERTS -->
    <div v-if="lowFunds.length" class="bg-red-50 border border-red-200 rounded-2xl p-5">
      <div class="flex items-center gap-3 mb-3">
        <span class="material-symbols-outlined text-red-500" style="font-variation-settings:'FILL' 1;">warning</span>
        <p class="font-black text-red-700 text-sm">{{ lowFunds.length }} nguồn quỹ sắp cạn ngân sách (&lt;20%)</p>
      </div>
      <div class="flex flex-wrap gap-2">
        <span v-for="f in lowFunds" :key="f.id" class="px-3 py-1.5 bg-red-100 text-red-700 text-xs font-bold rounded-lg">
          {{ f.ten_nguon_quy }} — còn {{ formatPct(f) }}%
        </span>
      </div>
    </div>

    <div class="grid grid-cols-1 xl:grid-cols-3 gap-6">

      <!-- TABLE (left 2/3) -->
      <div class="xl:col-span-2 bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
        <div class="px-6 py-5 border-b border-slate-100 flex items-center justify-between">
          <h3 class="font-black text-slate-800">Danh sách nguồn quỹ</h3>
          <div class="flex items-center gap-2">
            <input v-model="search" placeholder="Tìm kiếm..." class="px-3 py-1.5 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:bg-white w-44" />
          </div>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead>
              <tr class="text-[10px] uppercase tracking-widest text-slate-400 bg-slate-50/80 border-b border-slate-100">
                <th class="text-left px-6 py-3 font-black">Tên nguồn quỹ</th>
                <th class="text-right px-4 py-3 font-black">Tổng ngân sách</th>
                <th class="text-right px-4 py-3 font-black">Còn lại</th>
                <th class="text-center px-4 py-3 font-black w-40">Sử dụng</th>
                <th class="text-center px-4 py-3 font-black">CT</th>
                <th class="text-center px-4 py-3 font-black">Hành động</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-slate-50">
              <tr v-if="!filteredFunds.length">
                <td colspan="6" class="py-16 text-center text-slate-400 text-sm">Không có dữ liệu</td>
              </tr>
              <tr v-for="f in filteredFunds" :key="f.id"
                  :class="['hover:bg-slate-50/70 transition-colors', formatPct(f) < 20 ? 'bg-red-50/40' : '']">
                <td class="px-6 py-4">
                  <div class="flex items-center gap-3">
                    <div :class="['w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0', formatPct(f) < 20 ? 'bg-red-100' : 'bg-primary/10']">
                      <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;" :class="formatPct(f) < 20 ? 'text-red-600' : 'text-primary'">savings</span>
                    </div>
                    <div>
                      <p class="font-bold text-slate-800 text-sm">{{ f.ten_nguon_quy }}</p>
                      <p class="text-[10px] text-slate-400">{{ f.nguon_kinh_phi || 'Ngân sách nhà nước' }}</p>
                    </div>
                  </div>
                </td>
                <td class="px-4 py-4 text-right font-bold text-slate-700 text-sm whitespace-nowrap">{{ formatVnd(f.tong_ngan_sach) }}</td>
                <td class="px-4 py-4 text-right text-sm whitespace-nowrap">
                  <span :class="['font-black', formatPct(f) < 20 ? 'text-red-600' : formatPct(f) < 50 ? 'text-amber-600' : 'text-emerald-600']">{{ formatVnd(f.ngan_sach_con_lai) }}</span>
                </td>
                <td class="px-4 py-4 w-40">
                  <div class="flex items-center gap-2">
                    <div class="flex-1 h-2 bg-slate-100 rounded-full overflow-hidden">
                      <div :class="['h-full rounded-full', usedPct(f) > 80 ? 'bg-red-500' : usedPct(f) > 60 ? 'bg-amber-400' : 'bg-emerald-500']"
                        :style="{ width: usedPct(f) + '%' }"></div>
                    </div>
                    <span class="text-xs font-bold text-slate-500 w-10 text-right">{{ usedPct(f) }}%</span>
                  </div>
                </td>
                <td class="px-4 py-4 text-center">
                  <span class="px-2.5 py-1 bg-blue-100 text-blue-700 text-xs font-black rounded-full">{{ f.so_chuong_trinh || 0 }}</span>
                </td>
                <td class="px-4 py-4 text-center">
                  <div class="flex items-center justify-center gap-1">
                    <button @click="openEdit(f)" class="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-colors">
                      <span class="material-symbols-outlined text-sm">edit</span>
                    </button>
                    <button @click="openDetail(f)" class="p-1.5 rounded-lg hover:bg-primary/10 text-slate-400 hover:text-primary transition-colors">
                      <span class="material-symbols-outlined text-sm">open_in_new</span>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- CHART (right 1/3) -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6">
        <h3 class="font-black text-slate-800 mb-1">Phân bổ ngân sách</h3>
        <p class="text-xs text-slate-400 mb-5">Tỷ lệ ngân sách còn lại</p>
        <!-- Donut SVG chart -->
        <div class="flex justify-center mb-5">
          <div class="relative w-44 h-44">
            <svg viewBox="0 0 100 100" class="-rotate-90 w-full h-full">
              <circle cx="50" cy="50" r="38" fill="none" stroke="#f1f5f9" stroke-width="14"/>
              <circle v-for="(seg, i) in donutSegments" :key="i"
                cx="50" cy="50" r="38" fill="none" :stroke="seg.color" stroke-width="14"
                :stroke-dasharray="`${seg.dash} ${seg.gap}`"
                :stroke-dashoffset="`-${seg.offset}`"
                stroke-linecap="butt"/>
            </svg>
            <div class="absolute inset-0 flex flex-col items-center justify-center">
              <p class="text-xl font-black text-slate-800">{{ funds.length }}</p>
              <p class="text-[10px] text-slate-400">nguồn quỹ</p>
            </div>
          </div>
        </div>
        <!-- Legend -->
        <div class="space-y-3">
          <div v-for="(f, i) in funds" :key="f.id" class="flex items-center gap-3">
            <div class="w-3 h-3 rounded-full flex-shrink-0" :style="{ background: CHART_COLORS[i % CHART_COLORS.length] }"></div>
            <span class="text-xs text-slate-600 flex-1 truncate">{{ f.ten_nguon_quy }}</span>
            <span class="text-xs font-bold text-slate-500">{{ formatPct(f) }}%</span>
          </div>
        </div>
        <!-- Bar budget comparison -->
        <div class="mt-6 pt-5 border-t border-slate-100 space-y-3">
          <p class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-3">Mức sử dụng theo quỹ</p>
          <div v-for="f in funds" :key="'bar'+f.id" class="space-y-1">
            <div class="flex justify-between text-xs">
              <span class="text-slate-600 truncate max-w-[120px]">{{ f.ten_nguon_quy }}</span>
              <span class="font-bold" :class="usedPct(f)>80?'text-red-600':usedPct(f)>60?'text-amber-600':'text-emerald-600'">{{ usedPct(f) }}%</span>
            </div>
            <div class="h-1.5 bg-slate-100 rounded-full overflow-hidden">
              <div :class="['h-full rounded-full', usedPct(f)>80?'bg-red-500':usedPct(f)>60?'bg-amber-400':'bg-emerald-500']"
                :style="{ width: usedPct(f)+'%' }"></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ADD/EDIT MODAL -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showModal=false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-lg overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-primary to-blue-400"></div>
          <div class="p-8">
            <div class="flex items-center gap-4 mb-6">
              <div class="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">savings</span>
              </div>
              <div>
                <h3 class="text-xl font-black text-slate-800">{{ editFund ? 'Chỉnh sửa nguồn quỹ' : 'Thêm nguồn quỹ mới' }}</h3>
                <p class="text-xs text-slate-400">Điền đầy đủ thông tin</p>
              </div>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên nguồn quỹ <span class="text-red-500">*</span></label>
                <input v-model="form.ten_nguon_quy" type="text" placeholder="VD: Quỹ BTXH Tỉnh 2026"
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
                <p v-if="errors.ten" class="text-xs text-red-500 mt-1">{{ errors.ten }}</p>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Nguồn kinh phí</label>
                <select v-model="form.nguon_kinh_phi" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20">
                  <option value="Ngân sách nhà nước">Ngân sách nhà nước</option>
                  <option value="Tài trợ quốc tế">Tài trợ quốc tế</option>
                  <option value="Quyên góp, từ thiện">Quyên góp, từ thiện</option>
                  <option value="Khác">Khác</option>
                </select>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tổng ngân sách (VNĐ) <span class="text-red-500">*</span></label>
                  <input v-model="form.tong_ngan_sach" type="number" min="0" placeholder="0"
                    class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
                  <p v-if="errors.budget" class="text-xs text-red-500 mt-1">{{ errors.budget }}</p>
                </div>
                <div>
                  <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ngân sách còn lại</label>
                  <input v-model="form.ngan_sach_con_lai" type="number" min="0" placeholder="Tự động"
                    class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
                </div>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mô tả</label>
                <textarea v-model="form.mo_ta" rows="3" placeholder="Mô tả về nguồn quỹ..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
              </div>
            </div>
            <div class="flex justify-end gap-3 mt-7">
              <button @click="showModal=false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="saveFund" :disabled="saving" class="px-6 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20 disabled:opacity-50">
                {{ saving ? 'Đang lưu...' : (editFund ? 'Cập nhật' : 'Thêm mới') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- DETAIL DRAWER -->
    <Teleport to="body">
      <div v-if="detailFund" class="fixed inset-0 z-[200] flex" @click.self="detailFund=null">
        <div class="flex-1" @click="detailFund=null"></div>
        <div class="w-96 bg-white shadow-2xl h-full overflow-y-auto flex flex-col">
          <div class="h-1.5 bg-gradient-to-r from-primary to-blue-400 flex-shrink-0"></div>
          <div class="p-7 flex-1">
            <div class="flex items-start justify-between mb-6">
              <div>
                <p class="text-[10px] font-black uppercase tracking-widest text-slate-400 mb-1">Chi tiết nguồn quỹ</p>
                <h3 class="text-xl font-black text-slate-800">{{ detailFund.ten_nguon_quy }}</h3>
                <p class="text-xs text-slate-400 mt-1">{{ detailFund.nguon_kinh_phi }}</p>
              </div>
              <button @click="detailFund=null" class="p-2 rounded-xl hover:bg-slate-100 text-slate-400">
                <span class="material-symbols-outlined">close</span>
              </button>
            </div>
            <!-- Big progress ring -->
            <div class="flex justify-center mb-6">
              <div class="relative w-36 h-36">
                <svg viewBox="0 0 100 100" class="-rotate-90 w-full h-full">
                  <circle cx="50" cy="50" r="40" fill="none" stroke="#f1f5f9" stroke-width="12"/>
                  <circle cx="50" cy="50" r="40" fill="none" :stroke="usedPct(detailFund)>80?'#ef4444':usedPct(detailFund)>60?'#f59e0b':'#10b981'"
                    stroke-width="12" :stroke-dasharray="`${usedPct(detailFund)*2.51} 251`" stroke-linecap="round"/>
                </svg>
                <div class="absolute inset-0 flex flex-col items-center justify-center">
                  <p class="text-2xl font-black" :class="usedPct(detailFund)>80?'text-red-600':usedPct(detailFund)>60?'text-amber-600':'text-emerald-600'">{{ usedPct(detailFund) }}%</p>
                  <p class="text-[10px] text-slate-400">đã dùng</p>
                </div>
              </div>
            </div>
            <!-- Numbers -->
            <div class="grid grid-cols-2 gap-4 mb-6">
              <div class="bg-slate-50 rounded-xl p-4 text-center">
                <p class="text-xs text-slate-400 mb-1">Tổng ngân sách</p>
                <p class="font-black text-slate-800">{{ formatVnd(detailFund.tong_ngan_sach) }}</p>
              </div>
              <div class="bg-emerald-50 rounded-xl p-4 text-center">
                <p class="text-xs text-slate-400 mb-1">Còn lại</p>
                <p class="font-black" :class="formatPct(detailFund)<20?'text-red-600':'text-emerald-700'">{{ formatVnd(detailFund.ngan_sach_con_lai) }}</p>
              </div>
            </div>
            <div class="space-y-3">
              <div class="flex justify-between text-sm py-2 border-b border-slate-50">
                <span class="text-slate-400">Đã sử dụng</span>
                <span class="font-bold text-slate-700">{{ formatVnd(detailFund.tong_ngan_sach - detailFund.ngan_sach_con_lai) }}</span>
              </div>
              <div class="flex justify-between text-sm py-2 border-b border-slate-50">
                <span class="text-slate-400">Chương trình gắn</span>
                <span class="font-bold text-blue-600">{{ detailFund.so_chuong_trinh || 0 }}</span>
              </div>
              <div class="flex justify-between text-sm py-2">
                <span class="text-slate-400">Mô tả</span>
                <span class="font-semibold text-slate-600 text-right max-w-[200px]">{{ detailFund.mo_ta || 'Không có' }}</span>
              </div>
            </div>
          </div>
          <div class="p-5 border-t border-slate-100 flex-shrink-0 flex gap-3">
            <button @click="openEdit(detailFund);detailFund=null" class="flex-1 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90">Chỉnh sửa</button>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUI } from '../stores/ui'
import http from '../api/http'

const ui = useUI()

const CHART_COLORS = ['#6366f1','#10b981','#f59e0b','#3b82f6','#ec4899','#8b5cf6']

const search   = ref('')
const showModal = ref(false)
const editFund  = ref(null)
const detailFund = ref(null)
const saving    = ref(false)
const errors    = ref({})

const defaultForm = { ten_nguon_quy:'', nguon_kinh_phi:'Ngân sách nhà nước', tong_ngan_sach:'', ngan_sach_con_lai:'', mo_ta:'' }
const form = ref({ ...defaultForm })

const funds = ref([
  { id:1, ten_nguon_quy:'Quỹ BTXH Tỉnh 2026',   nguon_kinh_phi:'Ngân sách nhà nước', tong_ngan_sach:5000000000, ngan_sach_con_lai:1800000000, so_chuong_trinh:3, mo_ta:'Ngân sách nhà nước cấp cho chương trình bảo trợ xã hội' },
  { id:2, ten_nguon_quy:'Quỹ Hỗ trợ Quốc tế',   nguon_kinh_phi:'Tài trợ quốc tế',   tong_ngan_sach:2000000000, ngan_sach_con_lai:1500000000, so_chuong_trinh:1, mo_ta:'Nguồn viện trợ từ tổ chức UNICEF' },
  { id:3, ten_nguon_quy:'Quỹ Từ thiện Cộng đồng',nguon_kinh_phi:'Quyên góp, từ thiện',tong_ngan_sach:800000000,  ngan_sach_con_lai:120000000,  so_chuong_trinh:2, mo_ta:'Thu từ các chiến dịch quyên góp' },
])

const stats = computed(() => {
  const total   = funds.value.reduce((s,f) => s + f.tong_ngan_sach, 0)
  const remain  = funds.value.reduce((s,f) => s + f.ngan_sach_con_lai, 0)
  const low     = funds.value.filter(f => formatPct(f) < 20).length
  return [
    { label:'Tổng ngân sách',    value: formatVnd(total),           icon:'account_balance_wallet', iconBg:'bg-primary/10',    iconColor:'text-primary' },
    { label:'Còn lại',           value: formatVnd(remain),          icon:'savings',                iconBg:'bg-emerald-100',   iconColor:'text-emerald-600' },
    { label:'Đã sử dụng',        value: formatVnd(total - remain),  icon:'payments',               iconBg:'bg-amber-100',     iconColor:'text-amber-600' },
    { label:'Sắp cạn ngân sách', value: low + ' quỹ',              icon:'warning',                iconBg:'bg-red-100',       iconColor:'text-red-500', warn: low > 0 },
  ]
})

const lowFunds      = computed(() => funds.value.filter(f => formatPct(f) < 20))
const filteredFunds = computed(() => {
  const q = search.value.toLowerCase()
  return funds.value.filter(f => f.ten_nguon_quy.toLowerCase().includes(q))
})

const CIRCUMFERENCE = 2 * Math.PI * 38
const donutSegments = computed(() => {
  const total = funds.value.reduce((s,f) => s + f.ngan_sach_con_lai, 0)
  let offset = 0
  return funds.value.map((f, i) => {
    const pct  = total > 0 ? f.ngan_sach_con_lai / total : 0
    const dash = pct * CIRCUMFERENCE
    const seg  = { dash, gap: CIRCUMFERENCE - dash, offset, color: CHART_COLORS[i % CHART_COLORS.length] }
    offset += dash
    return seg
  })
})

function usedPct(f) { return Math.round((1 - f.ngan_sach_con_lai / f.tong_ngan_sach) * 100) }
function formatPct(f) { return Math.round((f.ngan_sach_con_lai / f.tong_ngan_sach) * 100) }
function formatVnd(v) {
  if (!v && v !== 0) return '—'
  if (v >= 1e9) return (v / 1e9).toFixed(1) + ' Tỷ'
  if (v >= 1e6) return (v / 1e6).toFixed(0) + 'Tr'
  return v.toLocaleString('vi-VN') + 'đ'
}

function openAdd()    { editFund.value = null; form.value = { ...defaultForm }; errors.value = {}; showModal.value = true }
function openEdit(f)  { editFund.value = f; form.value = { ...f }; errors.value = {}; showModal.value = true }
function openDetail(f){ detailFund.value = f }
async function exportCsv() {
  try {
    const { exportApi } = await import('../api/export')
    await exportApi.download('payments')
    ui.showSuccess('Xuất Excel thành công!')
  } catch (e) {
    ui.showError?.('Xuất dữ liệu thất bại!') || alert('Xuất dữ liệu thất bại: ' + e.message)
  }
}

function saveFund() {
  errors.value = {}
  if (!form.value.ten_nguon_quy?.trim()) { errors.value.ten = 'Vui lòng nhập tên nguồn quỹ'; return }
  if (!form.value.tong_ngan_sach || form.value.tong_ngan_sach <= 0) { errors.value.budget = 'Tổng ngân sách phải > 0'; return }
  saving.value = true
  setTimeout(() => {
    if (editFund.value) {
      const idx = funds.value.findIndex(f => f.id === editFund.value.id)
      if (idx >= 0) Object.assign(funds.value[idx], form.value)
      ui.showSuccess('Cập nhật nguồn quỹ thành công!')
    } else {
      funds.value.push({ id: Date.now(), ...form.value, tong_ngan_sach: Number(form.value.tong_ngan_sach), ngan_sach_con_lai: Number(form.value.ngan_sach_con_lai || form.value.tong_ngan_sach), so_chuong_trinh: 0 })
      ui.showSuccess('Thêm nguồn quỹ thành công!')
    }
    saving.value = false
    showModal.value = false
  }, 600)
}
</script>
