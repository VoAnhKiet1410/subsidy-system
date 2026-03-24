<template>
  <div class="p-8 space-y-7">

    <!-- HEADER -->
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <p class="text-[11px] font-black uppercase tracking-widest text-primary/60 mb-1">Phân hệ Chương Trình</p>
        <h2 class="text-3xl font-extrabold tracking-tight text-slate-800">Chương trình Trợ cấp</h2>
        <p class="text-slate-400 text-sm mt-1">Quản lý, theo dõi và điều phối các chương trình hỗ trợ xã hội.</p>
      </div>
      <div class="flex gap-3">
        <button @click="exportData" class="flex items-center gap-2 px-4 py-2.5 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50">
          <span class="material-symbols-outlined text-sm">download</span>Xuất
        </button>
        <button @click="openCreate" class="flex items-center gap-2 px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-lg shadow-primary/20">
          <span class="material-symbols-outlined text-sm">add_circle</span>Tạo chương trình
        </button>
      </div>
    </div>

    <!-- STATS -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-5">
      <div v-for="s in stats" :key="s.label" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
        <div :class="['w-10 h-10 rounded-xl flex items-center justify-center mb-3', s.iconBg]">
          <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;" :class="s.iconColor">{{ s.icon }}</span>
        </div>
        <p class="text-2xl font-black text-slate-800">{{ s.value }}</p>
        <p class="text-xs text-slate-400 mt-0.5">{{ s.label }}</p>
      </div>
    </div>

    <!-- FILTERS -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 flex flex-wrap gap-3 items-center">
      <span class="material-symbols-outlined text-slate-400 text-sm">filter_list</span>
      <input v-model="search" placeholder="Tìm chương trình..."
        class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 focus:bg-white w-52" />
      <select v-model="filterStatus" class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20">
        <option value="">Tất cả trạng thái</option>
        <option value="ACTIVE">Đang hoạt động</option>
        <option value="INACTIVE">Tạm dừng</option>
        <option value="ENDED">Đã kết thúc</option>
      </select>
      <select v-model="filterFund" class="px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/20">
        <option value="">Tất cả nguồn quỹ</option>
        <option v-for="f in funds" :key="f.id" :value="f.id">{{ f.ten_nguon_quy }}</option>
      </select>
      <button v-if="search||filterStatus||filterFund" @click="clearFilter" class="text-xs text-slate-400 hover:text-red-500 flex items-center gap-1">
        <span class="material-symbols-outlined text-sm">close</span>Xóa bộ lọc
      </button>
      <span class="ml-auto text-xs text-slate-400">{{ filteredPrograms.length }} / {{ programs.length }} chương trình</span>
    </div>

    <!-- TABLE -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead>
            <tr class="text-[10px] uppercase tracking-widest text-slate-400 bg-slate-50/80 border-b border-slate-100">
              <th class="text-left px-6 py-3 font-black">Chương trình</th>
              <th class="text-left px-4 py-3 font-black">Nguồn quỹ</th>
              <th class="text-center px-4 py-3 font-black">Bắt đầu</th>
              <th class="text-center px-4 py-3 font-black">Kết thúc</th>
              <th class="text-center px-4 py-3 font-black">Trạng thái</th>
              <th class="text-center px-4 py-3 font-black">Hồ sơ</th>
              <th class="text-center px-4 py-3 font-black">Tiến độ</th>
              <th class="text-center px-4 py-3 font-black">Hành động</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-slate-50">
            <tr v-if="!filteredPrograms.length">
              <td colspan="8" class="py-16 text-center text-slate-400">Không có dữ liệu phù hợp</td>
            </tr>
            <tr v-for="p in filteredPrograms" :key="p.id" class="hover:bg-slate-50/70 transition-colors">
              <td class="px-6 py-4 max-w-xs">
                <div class="flex items-center gap-3">
                  <div :class="['w-9 h-9 rounded-xl flex items-center justify-center flex-shrink-0', statusStyle(p.trang_thai).iconBg]">
                    <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;" :class="statusStyle(p.trang_thai).iconColor">volunteer_activism</span>
                  </div>
                  <div>
                    <p class="font-bold text-slate-800 text-sm">{{ p.ten_chuong_trinh }}</p>
                    <p class="text-[10px] text-slate-400 truncate max-w-[180px]">{{ p.mo_ta }}</p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-4">
                <div class="flex items-center gap-2">
                  <span class="material-symbols-outlined text-sm text-primary" style="font-variation-settings:'FILL' 1;">savings</span>
                  <span class="text-sm text-slate-600 truncate max-w-[120px]">{{ getFundName(p.nguon_quy_id) }}</span>
                </div>
              </td>
              <td class="px-4 py-4 text-center text-sm text-slate-500 whitespace-nowrap">{{ formatDate(p.ngay_bat_dau) }}</td>
              <td class="px-4 py-4 text-center text-sm text-slate-500 whitespace-nowrap">{{ formatDate(p.ngay_ket_thuc) }}</td>
              <td class="px-4 py-4 text-center">
                <span :class="['px-2.5 py-1 rounded-full text-[10px] font-black', statusStyle(p.trang_thai).badge]">
                  {{ statusStyle(p.trang_thai).label }}
                </span>
              </td>
              <td class="px-4 py-4 text-center">
                <span class="text-sm font-bold text-slate-700">{{ p.so_ho_so || 0 }}</span>
              </td>
              <td class="px-4 py-4 w-32">
                <div class="space-y-1">
                  <div class="h-1.5 bg-slate-100 rounded-full overflow-hidden">
                    <div class="h-full bg-primary rounded-full" :style="{ width: daysProgress(p) + '%' }"></div>
                  </div>
                  <p class="text-[10px] text-slate-400 text-center">{{ daysLeft(p) }}</p>
                </div>
              </td>
              <td class="px-4 py-4 text-center">
                <div class="flex items-center justify-center gap-1">
                  <button @click="openDetail(p)" class="p-1.5 rounded-lg hover:bg-primary/10 text-slate-400 hover:text-primary transition-colors" title="Chi tiết">
                    <span class="material-symbols-outlined text-sm">open_in_new</span>
                  </button>
                  <button @click="openEdit(p)" class="p-1.5 rounded-lg hover:bg-slate-100 text-slate-400 hover:text-slate-600 transition-colors" title="Chỉnh sửa">
                    <span class="material-symbols-outlined text-sm">edit</span>
                  </button>
                  <button @click="toggleStatus(p)" class="p-1.5 rounded-lg hover:bg-amber-50 text-slate-400 hover:text-amber-600 transition-colors" :title="p.trang_thai==='ACTIVE'?'Tạm dừng':'Kích hoạt'">
                    <span class="material-symbols-outlined text-sm">{{ p.trang_thai === 'ACTIVE' ? 'pause_circle' : 'play_circle' }}</span>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- CREATE / EDIT MODAL -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showModal=false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-xl overflow-hidden">
          <div class="h-1.5 bg-gradient-to-r from-primary to-indigo-400"></div>
          <div class="p-8">
            <div class="flex items-center gap-4 mb-6">
              <div class="w-12 h-12 rounded-2xl bg-primary/10 text-primary flex items-center justify-center">
                <span class="material-symbols-outlined text-2xl" style="font-variation-settings:'FILL' 1;">volunteer_activism</span>
              </div>
              <div>
                <h3 class="text-xl font-black text-slate-800">{{ editProgram ? 'Chỉnh sửa chương trình' : 'Tạo chương trình mới' }}</h3>
                <p class="text-xs text-slate-400">Điền đầy đủ thông tin</p>
              </div>
            </div>
            <div class="grid grid-cols-1 gap-4">
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên chương trình <span class="text-red-500">*</span></label>
                <input v-model="form.ten_chuong_trinh" type="text" placeholder="VD: Hỗ trợ người cao tuổi 2026"
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
                <p v-if="errors.ten" class="text-xs text-red-500 mt-1">{{ errors.ten }}</p>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Nguồn quỹ <span class="text-red-500">*</span></label>
                <select v-model="form.nguon_quy_id" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20">
                  <option value="" disabled>Chọn nguồn quỹ...</option>
                  <option v-for="f in funds" :key="f.id" :value="f.id">
                    {{ f.ten_nguon_quy }} — Còn: {{ formatVnd(f.ngan_sach_con_lai) }}
                  </option>
                </select>
                <p v-if="errors.fund" class="text-xs text-red-500 mt-1">{{ errors.fund }}</p>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mô tả</label>
                <textarea v-model="form.mo_ta" rows="2" placeholder="Mô tả mục tiêu chương trình..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
              </div>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ngày bắt đầu <span class="text-red-500">*</span></label>
                  <input v-model="form.ngay_bat_dau" type="date"
                    class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
                </div>
                <div>
                  <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ngày kết thúc <span class="text-red-500">*</span></label>
                  <input v-model="form.ngay_ket_thuc" type="date"
                    class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
                </div>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Trạng thái</label>
                <select v-model="form.trang_thai" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20">
                  <option value="ACTIVE">Đang hoạt động</option>
                  <option value="INACTIVE">Tạm dừng</option>
                </select>
              </div>
            </div>
            <div class="flex justify-end gap-3 mt-7">
              <button @click="showModal=false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="saveProgram" :disabled="saving"
                class="px-6 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20 disabled:opacity-50">
                {{ saving ? 'Đang lưu...' : (editProgram ? 'Cập nhật' : 'Tạo chương trình') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- DETAIL MODAL -->
    <Teleport to="body">
      <div v-if="detailProgram" class="fixed inset-0 z-[200] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="detailProgram=null">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-2xl overflow-hidden">
          <div :class="['h-1.5', statusStyle(detailProgram.trang_thai).gradient]"></div>
          <div class="p-8">
            <div class="flex items-start justify-between mb-6">
              <div class="flex items-center gap-4">
                <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center', statusStyle(detailProgram.trang_thai).iconBg]">
                  <span class="material-symbols-outlined text-xl" style="font-variation-settings:'FILL' 1;" :class="statusStyle(detailProgram.trang_thai).iconColor">volunteer_activism</span>
                </div>
                <div>
                  <h3 class="text-xl font-black text-slate-800">{{ detailProgram.ten_chuong_trinh }}</h3>
                  <div class="flex items-center gap-2 mt-1">
                    <span :class="['px-2.5 py-0.5 rounded-full text-[10px] font-black', statusStyle(detailProgram.trang_thai).badge]">{{ statusStyle(detailProgram.trang_thai).label }}</span>
                    <span class="text-xs text-slate-400">{{ getFundName(detailProgram.nguon_quy_id) }}</span>
                  </div>
                </div>
              </div>
              <button @click="detailProgram=null" class="p-2 rounded-xl hover:bg-slate-100 text-slate-400">
                <span class="material-symbols-outlined">close</span>
              </button>
            </div>
            <!-- Timeline progress -->
            <div class="bg-slate-50 rounded-2xl p-5 mb-6">
              <div class="flex justify-between text-xs text-slate-400 mb-2">
                <span>{{ formatDate(detailProgram.ngay_bat_dau) }}</span>
                <span class="font-bold text-primary">{{ daysLeft(detailProgram) }}</span>
                <span>{{ formatDate(detailProgram.ngay_ket_thuc) }}</span>
              </div>
              <div class="h-3 bg-white rounded-full overflow-hidden border border-slate-200">
                <div class="h-full bg-primary rounded-full" :style="{ width: daysProgress(detailProgram)+'%' }"></div>
              </div>
            </div>
            <!-- Stats grid -->
            <div class="grid grid-cols-3 gap-4 mb-6">
              <div class="bg-slate-50 rounded-2xl p-4 text-center">
                <p class="text-2xl font-black text-slate-800">{{ detailProgram.so_ho_so || 0 }}</p>
                <p class="text-xs text-slate-400 mt-1">Tổng hồ sơ</p>
              </div>
              <div class="bg-emerald-50 rounded-2xl p-4 text-center">
                <p class="text-2xl font-black text-emerald-700">{{ detailProgram.ho_so_duyet || 0 }}</p>
                <p class="text-xs text-slate-400 mt-1">Đã duyệt</p>
              </div>
              <div class="bg-amber-50 rounded-2xl p-4 text-center">
                <p class="text-2xl font-black text-amber-700">{{ detailProgram.ho_so_cho || 0 }}</p>
                <p class="text-xs text-slate-400 mt-1">Chờ xử lý</p>
              </div>
            </div>
            <!-- Fund info -->
            <div class="bg-blue-50 border border-blue-100 rounded-2xl p-5 mb-6">
              <p class="text-xs font-black uppercase tracking-widest text-blue-400 mb-3">Ngân sách liên quan</p>
              <div class="flex items-center justify-between mb-3">
                <span class="text-sm font-bold text-slate-700">{{ getFundName(detailProgram.nguon_quy_id) }}</span>
                <span class="text-sm font-black text-emerald-600">{{ formatVnd(getFundRemain(detailProgram.nguon_quy_id)) }} còn lại</span>
              </div>
              <div class="h-2 bg-blue-100 rounded-full overflow-hidden">
                <div class="h-full bg-blue-500 rounded-full" :style="{ width: getFundUsedPct(detailProgram.nguon_quy_id)+'%' }"></div>
              </div>
              <p class="text-xs text-slate-400 mt-1">{{ getFundUsedPct(detailProgram.nguon_quy_id) }}% ngân sách quỹ đã sử dụng</p>
            </div>
            <!-- Description -->
            <p class="text-sm text-slate-500 leading-relaxed">{{ detailProgram.mo_ta || 'Không có mô tả.' }}</p>
            <div class="flex justify-end gap-3 mt-6">
              <button @click="openEdit(detailProgram);detailProgram=null"
                class="px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90">Chỉnh sửa</button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useUI } from '../stores/ui'

const ui = useUI()

const search        = ref('')
const filterStatus  = ref('')
const filterFund    = ref('')
const showModal     = ref(false)
const editProgram   = ref(null)
const detailProgram = ref(null)
const saving        = ref(false)
const errors        = ref({})

const defaultForm = { ten_chuong_trinh:'', nguon_quy_id:'', mo_ta:'', ngay_bat_dau:'', ngay_ket_thuc:'', trang_thai:'ACTIVE' }
const form = ref({ ...defaultForm })

const funds = ref([
  { id:1, ten_nguon_quy:'Quỹ BTXH Tỉnh 2026',      ngan_sach_con_lai:1800000000, tong_ngan_sach:5000000000 },
  { id:2, ten_nguon_quy:'Quỹ Hỗ trợ Quốc tế',   ngan_sach_con_lai:1500000000, tong_ngan_sach:2000000000 },
  { id:3, ten_nguon_quy:'Quỹ Từ thiện Cộng đồng', ngan_sach_con_lai:120000000,  tong_ngan_sach:800000000  },
])

const programs = ref([
  { id:1, ten_chuong_trinh:'Hỗ trợ người cao tuổi',      nguon_quy_id:1, mo_ta:'Hỗ trợ hàng tháng cho người trên 60 tuổi không có người chăm sóc', ngay_bat_dau:'2026-01-01', ngay_ket_thuc:'2026-12-31', trang_thai:'ACTIVE',   so_ho_so:145, ho_so_duyet:98,  ho_so_cho:30 },
  { id:2, ten_chuong_trinh:'Hỗ trợ người khuyết tật',  nguon_quy_id:1, mo_ta:'Hỗ trợ chi phí phục hồi chức năng và hòa nhập cộng đồng',  ngay_bat_dau:'2026-01-15', ngay_ket_thuc:'2026-06-30', trang_thai:'ACTIVE',   so_ho_so:73,  ho_so_duyet:50,  ho_so_cho:15 },
  { id:3, ten_chuong_trinh:'Hỗ trợ trẻ em có hoàn cảnh', nguon_quy_id:2, mo_ta:'Chi trả học phí và chi phí sinh hoạt cho trẻ em có hoàn cảnh khó khăn', ngay_bat_dau:'2025-09-01', ngay_ket_thuc:'2026-08-31', trang_thai:'ACTIVE',   so_ho_so:210, ho_so_duyet:180, ho_so_cho:20 },
  { id:4, ten_chuong_trinh:'Hỗ trợ hộ nghèo 2025',       nguon_quy_id:3, mo_ta:'Hỗ trợ lương thực và nhu yếu phẩm cho hộ nghèo',  ngay_bat_dau:'2025-01-01', ngay_ket_thuc:'2025-12-31', trang_thai:'ENDED',    so_ho_so:320, ho_so_duyet:295, ho_so_cho:0  },
  { id:5, ten_chuong_trinh:'Hỗ trợ phụ nữ đơn thân',   nguon_quy_id:1, mo_ta:'Hỗ trợ kinh phí nuôi con cho phụ nữ đơn thân có hoàn cảnh khó khăn', ngay_bat_dau:'2026-03-01', ngay_ket_thuc:'2026-09-30', trang_thai:'INACTIVE', so_ho_so:18,  ho_so_duyet:10,  ho_so_cho:8  },
])

const stats = computed(() => {
  const act  = programs.value.filter(p => p.trang_thai === 'ACTIVE').length
  const tot  = programs.value.reduce((s,p) => s + (p.so_ho_so||0), 0)
  const appr = programs.value.reduce((s,p) => s + (p.ho_so_duyet||0), 0)
  return [
    { label:'Tổng số chương trình', value: programs.value.length, icon:'list_alt',    iconBg:'bg-primary/10',  iconColor:'text-primary' },
    { label:'Đang hoạt động',        value: act,                   icon:'check_circle', iconBg:'bg-emerald-100', iconColor:'text-emerald-600' },
    { label:'Tổng hồ sơ',           value: tot,                   icon:'folder_open',  iconBg:'bg-blue-100',    iconColor:'text-blue-600' },
    { label:'Hồ sơ đã duyệt',       value: appr,                  icon:'task_alt',     iconBg:'bg-amber-100',   iconColor:'text-amber-600' },
  ]
})

const filteredPrograms = computed(() => {
  let list = programs.value
  if (search.value) list = list.filter(p => p.ten_chuong_trinh.toLowerCase().includes(search.value.toLowerCase()))
  if (filterStatus.value) list = list.filter(p => p.trang_thai === filterStatus.value)
  if (filterFund.value)   list = list.filter(p => p.nguon_quy_id === Number(filterFund.value))
  return list
})

function statusStyle(s) {
  return {
    ACTIVE:   { badge:'bg-emerald-100 text-emerald-700', label:'Đang hoạt động', icon:'check_circle', iconBg:'bg-emerald-100', iconColor:'text-emerald-600', gradient:'bg-gradient-to-r from-emerald-400 to-teal-400' },
    INACTIVE: { badge:'bg-amber-100 text-amber-700',     label:'Tạm dừng',       icon:'pause_circle', iconBg:'bg-amber-100',   iconColor:'text-amber-600',   gradient:'bg-gradient-to-r from-amber-400 to-orange-400' },
    ENDED:    { badge:'bg-slate-100 text-slate-500',     label:'Đã kết thúc',    icon:'cancel',       iconBg:'bg-slate-100',   iconColor:'text-slate-500',   gradient:'bg-gradient-to-r from-slate-300 to-slate-400' },
  }[s] || { badge:'bg-slate-100 text-slate-400', label:'N/A', icon:'help', iconBg:'bg-slate-100', iconColor:'text-slate-400', gradient:'bg-slate-200' }
}

function getFundName(id)    { return funds.value.find(f => f.id === id)?.ten_nguon_quy || '—' }
function getFundRemain(id)  { return funds.value.find(f => f.id === id)?.ngan_sach_con_lai || 0 }
function getFundUsedPct(id) {
  const f = funds.value.find(x => x.id === id)
  if (!f) return 0
  return Math.round((1 - f.ngan_sach_con_lai / f.tong_ngan_sach) * 100)
}

function daysProgress(p) {
  const total = new Date(p.ngay_ket_thuc) - new Date(p.ngay_bat_dau)
  const done  = Date.now() - new Date(p.ngay_bat_dau)
  return Math.min(100, Math.max(0, Math.round(done / total * 100)))
}
function daysLeft(p) {
  const diff = new Date(p.ngay_ket_thuc) - Date.now()
  if (diff <= 0) return 'Đã kết thúc'
  const d = Math.ceil(diff / 86400000)
  return `Còn ${d} ngày`
}
function formatDate(d)  { if (!d) return '—'; return new Date(d).toLocaleDateString('vi-VN') }
function formatVnd(v)   {
  if (!v && v !== 0) return '—'
  if (v >= 1e9) return (v / 1e9).toFixed(1) + ' Tỷ'
  if (v >= 1e6) return (v / 1e6).toFixed(0) + 'Triệu'
  return v.toLocaleString('vi-VN') + 'đ'
}

function clearFilter()  { search.value = ''; filterStatus.value = ''; filterFund.value = '' }
async function exportData() {
  try {
    const { exportApi } = await import('../api/export')
    await exportApi.download('programs')
    ui.showSuccess('Đã xuất dữ liệu thành công!')
  } catch (e) {
    ui.showError?.('Xuất dữ liệu thất bại!') || alert('Xuất dữ liệu thất bại: ' + e.message)
  }
}

function openCreate() { editProgram.value = null; form.value = { ...defaultForm }; errors.value = {}; showModal.value = true }
function openEdit(p)  { editProgram.value = p;   form.value = { ...p };           errors.value = {}; showModal.value = true }
function openDetail(p){ detailProgram.value = p }

function toggleStatus(p) {
  p.trang_thai = p.trang_thai === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  ui.showSuccess('Đã cập nhật trạng thái chương trình!')
}

function saveProgram() {
  errors.value = {}
  if (!form.value.ten_chuong_trinh?.trim())  { errors.value.ten  = 'Vui lòng nhập tên chương trình'; return }
  if (!form.value.nguon_quy_id)              { errors.value.fund = 'Vui lòng chọn nguồn quỹ'; return }
  saving.value = true
  setTimeout(() => {
    if (editProgram.value) {
      const idx = programs.value.findIndex(p => p.id === editProgram.value.id)
      if (idx >= 0) Object.assign(programs.value[idx], form.value)
      ui.showSuccess('Cập nhật chương trình thành công!')
    } else {
      programs.value.unshift({ id: Date.now(), ...form.value, nguon_quy_id: Number(form.value.nguon_quy_id), so_ho_so:0, ho_so_duyet:0, ho_so_cho:0 })
      ui.showSuccess('Tạo chương trình thành công!')
    }
    saving.value = false
    showModal.value = false
  }, 600)
}
</script>
