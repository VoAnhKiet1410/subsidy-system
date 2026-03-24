<template>
  <div class="space-y-5">

    <!-- HERO SEARCH -->
    <div class="bg-gradient-to-br from-[#1a56db] via-[#1e40af] to-[#312e81] rounded-3xl px-7 py-7 text-white relative overflow-hidden">
      <div class="absolute -right-6 -top-6 w-44 h-44 bg-white/5 rounded-full"></div>
      <div class="absolute right-16 -bottom-8 w-28 h-28 bg-white/5 rounded-full"></div>
      <div class="relative z-10">
        <p class="text-white/60 text-xs font-bold uppercase tracking-widest mb-2">Danh sách chương trình</p>
        <h1 class="text-2xl font-black mb-1">Chương trình Trợ cấp <span class="text-yellow-300">đang mở</span></h1>
        <p class="text-white/70 text-sm mb-5">Tìm chương trình phù hợp và nộp hồ sơ ngay hôm nay.</p>
        <!-- Search bar -->
        <div class="relative">
          <span class="material-symbols-outlined absolute left-4 top-1/2 -translate-y-1/2 text-slate-400">search</span>
          <input v-model="search" placeholder="Tìm kiếm chương trình..."
            class="w-full pl-11 pr-4 py-3 rounded-2xl bg-white text-slate-800 text-sm font-medium placeholder-slate-400 outline-none shadow-lg focus:ring-2 focus:ring-yellow-300/50" />
        </div>
      </div>
    </div>

    <!-- FILTER TABS -->
    <div class="flex gap-2 overflow-x-auto pb-1 -mx-1 px-1">
      <button v-for="cat in categories" :key="cat.key" @click="activeCategory = cat.key"
        :class="['flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-bold whitespace-nowrap transition-all flex-shrink-0',
          activeCategory === cat.key
            ? 'bg-primary text-white shadow-md shadow-primary/20'
            : 'bg-white text-slate-500 border border-slate-200 hover:border-primary/30 hover:text-primary']">
        <span class="material-symbols-outlined text-base leading-none" style="font-variation-settings:'FILL' 1;">{{ cat.icon }}</span>
        {{ cat.label }}
        <span :class="['text-[10px] font-black px-1.5 py-0.5 rounded-full',
          activeCategory === cat.key ? 'bg-white/20 text-white' : 'bg-slate-100 text-slate-500']">
          {{ cat.count }}
        </span>
      </button>
    </div>

    <!-- STATS ROW -->
    <div class="grid grid-cols-3 gap-3">
      <div class="bg-white rounded-2xl border border-slate-200/80 p-4 text-center shadow-sm">
        <p class="text-2xl font-black text-primary">{{ filteredPrograms.length }}</p>
        <p class="text-[10px] text-slate-400 font-semibold mt-0.5">Chương trình phù hợp</p>
      </div>
      <div class="bg-white rounded-2xl border border-slate-200/80 p-4 text-center shadow-sm">
        <p class="text-2xl font-black text-emerald-600">{{ programs.filter(p=>p.status==='OPEN').length }}</p>
        <p class="text-[10px] text-slate-400 font-semibold mt-0.5">Đang nhận hồ sơ</p>
      </div>
      <div class="bg-white rounded-2xl border border-slate-200/80 p-4 text-center shadow-sm">
        <p class="text-2xl font-black text-amber-600">{{ programs.filter(p=>p.status==='SOON').length }}</p>
        <p class="text-[10px] text-slate-400 font-semibold mt-0.5">Sắp mở</p>
      </div>
    </div>

    <!-- PROGRAMS GRID -->
    <div v-if="filteredPrograms.length" class="grid grid-cols-1 sm:grid-cols-2 gap-4">
      <div v-for="p in filteredPrograms" :key="p.id"
        class="bg-white rounded-2xl border border-slate-200/80 shadow-sm overflow-hidden hover:shadow-md hover:border-primary/25 transition-all group">

        <!-- Card top accent -->
        <div :class="['h-1.5', p.status === 'OPEN' ? 'bg-gradient-to-r from-primary to-indigo-400' : p.status === 'SOON' ? 'bg-gradient-to-r from-amber-400 to-orange-400' : 'bg-slate-200']"></div>

        <div class="p-5">
          <!-- Header -->
          <div class="flex items-start gap-3 mb-3">
            <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0', p.iconBg]">
              <span class="material-symbols-outlined text-xl" :class="p.iconColor" style="font-variation-settings:'FILL' 1;">{{ p.icon }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <div class="flex items-start justify-between gap-2">
                <h3 class="font-bold text-slate-800 text-sm leading-snug">{{ p.ten }}</h3>
                <span :class="['px-2 py-0.5 rounded-full text-[10px] font-black flex-shrink-0', statusStyle(p.status).badge]">
                  {{ statusStyle(p.status).label }}
                </span>
              </div>
              <p class="text-[11px] text-slate-400 mt-0.5">{{ p.danh_muc }}</p>
            </div>
          </div>

          <!-- Description -->
          <p class="text-xs text-slate-500 leading-relaxed mb-4 line-clamp-2">{{ p.mo_ta }}</p>

          <!-- Meta info -->
          <div class="grid grid-cols-2 gap-2 mb-4">
            <div class="bg-slate-50 rounded-xl p-2.5">
              <p class="text-[9px] text-slate-400 font-bold uppercase tracking-wider">Mức hỗ trợ</p>
              <p class="text-xs font-black text-primary mt-0.5">{{ p.muc_ho_tro }}</p>
            </div>
            <div class="bg-slate-50 rounded-xl p-2.5">
              <p class="text-[9px] text-slate-400 font-bold uppercase tracking-wider">Hạn nộp</p>
              <p class="text-xs font-black text-slate-700 mt-0.5">{{ p.han_nop }}</p>
            </div>
          </div>

          <!-- Deadline progress -->
          <div v-if="p.status === 'OPEN'" class="mb-4">
            <div class="flex items-center justify-between text-[10px] text-slate-400 mb-1">
              <span>Còn lại {{ p.con_lai }} ngày</span>
              <span>{{ p.so_ho_so }} hồ sơ đã nộp</span>
            </div>
            <div class="h-1.5 bg-slate-100 rounded-full overflow-hidden">
              <div :class="['h-full rounded-full transition-all', p.con_lai < 30 ? 'bg-red-400' : 'bg-primary']"
                :style="{ width: Math.min((1 - p.con_lai/365)*100, 100) + '%' }"></div>
            </div>
          </div>

          <!-- Actions -->
          <div class="flex gap-2">
            <button @click="selected = p"
              class="flex-1 flex items-center justify-center gap-1.5 py-2.5 border border-slate-200 text-slate-600 text-xs font-semibold rounded-xl hover:bg-slate-50 transition-all">
              <span class="material-symbols-outlined text-sm">info</span>Chi tiết
            </button>
            <router-link v-if="p.status === 'OPEN'" :to="'/portal/nop-ho-so?program=' + p.id"
              class="flex-1 flex items-center justify-center gap-1.5 py-2.5 bg-primary text-white text-xs font-bold rounded-xl hover:opacity-90 shadow-sm shadow-primary/20 transition-all">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">edit_document</span>Nộp hồ sơ
            </router-link>
            <div v-else
              class="flex-1 flex items-center justify-center py-2.5 bg-slate-100 text-slate-400 text-xs font-semibold rounded-xl cursor-not-allowed">
              {{ p.status === 'SOON' ? 'Chưa mở' : 'Đã kết thúc' }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- EMPTY STATE -->
    <div v-else class="bg-white rounded-2xl border border-slate-200 p-14 text-center">
      <span class="material-symbols-outlined text-5xl text-slate-200 block mb-3">search_off</span>
      <p class="font-bold text-slate-500">Không tìm thấy chương trình phù hợp</p>
      <p class="text-sm text-slate-400 mt-1">Thử thay đổi bộ lọc hoặc từ khoá tìm kiếm</p>
      <button @click="search=''; activeCategory='all'" class="mt-4 px-4 py-2 bg-primary/10 text-primary text-sm font-bold rounded-xl hover:bg-primary/20 transition-all">
        Xoá bộ lọc
      </button>
    </div>

    <!-- DETAIL MODAL -->
    <Transition name="fade">
      <div v-if="selected" class="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm flex items-end sm:items-center justify-center p-0 sm:p-4" @click.self="selected=null">
        <div class="bg-white w-full sm:max-w-lg rounded-t-3xl sm:rounded-3xl overflow-hidden shadow-2xl max-h-[90vh] flex flex-col">
          <!-- Modal top accent -->
          <div :class="['h-1.5', selected.status === 'OPEN' ? 'bg-gradient-to-r from-primary to-indigo-400' : 'bg-amber-400']"></div>

          <!-- Modal header -->
          <div class="flex items-center gap-4 px-6 py-5 border-b border-slate-100">
            <div :class="['w-14 h-14 rounded-2xl flex items-center justify-center flex-shrink-0', selected.iconBg]">
              <span class="material-symbols-outlined text-2xl" :class="selected.iconColor" style="font-variation-settings:'FILL' 1;">{{ selected.icon }}</span>
            </div>
            <div class="flex-1 min-w-0">
              <h2 class="font-black text-slate-800 leading-snug">{{ selected.ten }}</h2>
              <span :class="['inline-flex px-2.5 py-1 rounded-full text-[10px] font-black mt-1', statusStyle(selected.status).badge]">
                {{ statusStyle(selected.status).label }}
              </span>
            </div>
            <button @click="selected=null" class="w-9 h-9 rounded-xl hover:bg-slate-100 flex items-center justify-center flex-shrink-0">
              <span class="material-symbols-outlined text-slate-400">close</span>
            </button>
          </div>

          <!-- Modal body -->
          <div class="overflow-y-auto flex-1 px-6 py-5 space-y-4">
            <p class="text-sm text-slate-600 leading-relaxed">{{ selected.mo_ta }}</p>

            <div class="grid grid-cols-2 gap-3">
              <div class="bg-primary/5 rounded-2xl p-4">
                <p class="text-[10px] text-primary/70 font-bold uppercase tracking-wider mb-1">Mức hỗ trợ</p>
                <p class="font-black text-primary text-lg">{{ selected.muc_ho_tro }}</p>
              </div>
              <div class="bg-slate-50 rounded-2xl p-4">
                <p class="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-1">Hạn nộp hồ sơ</p>
                <p class="font-black text-slate-700">{{ selected.han_nop }}</p>
              </div>
            </div>

            <div class="bg-slate-50 rounded-2xl p-4 space-y-2">
              <p class="text-xs font-bold text-slate-500 uppercase tracking-wider mb-2">Điều kiện tham gia</p>
              <div v-for="req in selected.yeu_cau" :key="req" class="flex items-start gap-2">
                <span class="material-symbols-outlined text-primary text-sm mt-0.5" style="font-variation-settings:'FILL' 1;">check_circle</span>
                <span class="text-sm text-slate-600">{{ req }}</span>
              </div>
            </div>

            <div class="bg-amber-50 border border-amber-100 rounded-2xl p-4">
              <p class="text-xs font-bold text-amber-700 mb-2 flex items-center gap-1.5">
                <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">attach_file</span>
                Tài liệu cần chuẩn bị
              </p>
              <div v-for="doc in selected.tai_lieu" :key="doc" class="flex items-center gap-2 text-sm text-amber-800 mb-1">
                <span class="w-1.5 h-1.5 bg-amber-400 rounded-full flex-shrink-0"></span>
                {{ doc }}
              </div>
            </div>
          </div>

          <!-- Modal footer -->
          <div class="px-6 py-4 border-t border-slate-100 flex gap-3">
            <button @click="selected=null" class="flex-1 py-3 border border-slate-200 text-slate-600 text-sm font-semibold rounded-2xl hover:bg-slate-50">
              Đóng
            </button>
            <router-link v-if="selected.status==='OPEN'" :to="'/portal/nop-ho-so?program='+selected.id" @click="selected=null"
              class="flex-1 flex items-center justify-center gap-2 py-3 bg-primary text-white text-sm font-bold rounded-2xl hover:opacity-90 shadow-md shadow-primary/20">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">edit_document</span>
              Nộp hồ sơ ngay
            </router-link>
          </div>
        </div>
      </div>
    </Transition>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { programsApi } from '../../api/programs'

const search   = ref('')
const activeCategory = ref('all')
const selected = ref(null)
const loading  = ref(false)

const fallbackPrograms = [
  {
    id: 1, ten: 'Hỗ trợ người cao tuổi 2026', danh_muc: 'Người cao tuổi',
    icon: 'elderly', iconBg: 'bg-blue-50', iconColor: 'text-blue-500',
    status: 'OPEN', han_nop: '31/12/2026', muc_ho_tro: '500.000đ/tháng',
    con_lai: 280, so_ho_so: 124,
    mo_ta: 'Chương trình hỗ trợ tài chính hàng tháng dành cho người cao tuổi từ 60 tuổi trở lên có hoàn cảnh kinh tế khó khăn, không có nguồn thu nhập ổn định.',
    yeu_cau: ['Từ 60 tuổi trở lên', 'Hộ nghèo hoặc cận nghèo', 'Không có lương hưu hoặc trợ cấp khác', 'Có hộ khẩu thường trú tại địa phương'],
    tai_lieu: ['CCCD / CMND còn hiệu lực', 'Sổ hộ khẩu hoặc giấy tạm trú', 'Giấy xác nhận hộ nghèo/cận nghèo', 'Ảnh 3x4 (2 ảnh)'],
  },
  {
    id: 2, ten: 'Hỗ trợ người khuyết tật', danh_muc: 'Người khuyết tật',
    icon: 'accessible', iconBg: 'bg-purple-50', iconColor: 'text-purple-500',
    status: 'OPEN', han_nop: '30/06/2026', muc_ho_tro: '750.000đ/tháng',
    con_lai: 98, so_ho_so: 87,
    mo_ta: 'Hỗ trợ chi phí phục hồi chức năng, mua sắm dụng cụ trợ giúp và hòa nhập cộng đồng cho người khuyết tật có mức độ nặng và đặc biệt nặng.',
    yeu_cau: ['Có giấy xác nhận khuyết tật mức nặng/đặc biệt nặng', 'Không được hưởng chế độ khuyết tật khác', 'Có hộ khẩu thường trú'],
    tai_lieu: ['CCCD / CMND', 'Giấy xác nhận mức độ khuyết tật', 'Sổ hộ khẩu', 'Giấy khám sức khoẻ'],
  },
  {
    id: 3, ten: 'Quỹ học bổng trẻ em nghèo', danh_muc: 'Trẻ em',
    icon: 'school', iconBg: 'bg-emerald-50', iconColor: 'text-emerald-500',
    status: 'OPEN', han_nop: '31/08/2026', muc_ho_tro: '1.200.000đ/năm',
    con_lai: 160, so_ho_so: 56,
    mo_ta: 'Học bổng hỗ trợ học phí và chi phí sinh hoạt cho trẻ em có hoàn cảnh đặc biệt khó khăn đang học phổ thông, giúp các em không phải nghỉ học vì lý do kinh tế.',
    yeu_cau: ['Đang học từ lớp 1 đến lớp 12', 'Hộ nghèo hoặc cận nghèo', 'Kết quả học tập từ trung bình trở lên', 'Không vi phạm kỷ luật nhà trường'],
    tai_lieu: ['Học bạ năm gần nhất', 'Sổ hộ khẩu', 'Giấy xác nhận hộ nghèo', 'Đơn xin học bổng có xác nhận của trường'],
  },
  {
    id: 4, ten: 'Trợ cấp thai sản khó khăn', danh_muc: 'Phụ nữ & Trẻ em',
    icon: 'pregnant_woman', iconBg: 'bg-pink-50', iconColor: 'text-pink-500',
    status: 'SOON', han_nop: '01/07/2026', muc_ho_tro: '2.000.000đ/lần',
    con_lai: 0, so_ho_so: 0,
    mo_ta: 'Hỗ trợ một lần cho phụ nữ sinh con trong hoàn cảnh kinh tế khó khăn, bao gồm chi phí sinh và chăm sóc trẻ sơ sinh trong 6 tháng đầu đời.',
    yeu_cau: ['Phụ nữ sinh con trong năm 2026', 'Hộ nghèo hoặc cận nghèo', 'Không được hưởng chế độ thai sản BHXH'],
    tai_lieu: ['Giấy chứng sinh', 'CCCD của mẹ', 'Sổ hộ khẩu', 'Giấy xác nhận hộ nghèo'],
  },
  {
    id: 5, ten: 'Hỗ trợ nhà ở cho hộ nghèo', danh_muc: 'Nhà ở',
    icon: 'home', iconBg: 'bg-orange-50', iconColor: 'text-orange-500',
    status: 'CLOSED', han_nop: '31/03/2026', muc_ho_tro: '40.000.000đ/hộ',
    con_lai: 0, so_ho_so: 203,
    mo_ta: 'Hỗ trợ kinh phí xây dựng và sửa chữa nhà ở cho hộ nghèo, hộ cận nghèo không có khả năng tự cải thiện điều kiện nhà ở.',
    yeu_cau: ['Hộ nghèo có nhà tạm bợ, dột nát', 'Chưa được hỗ trợ nhà ở lần nào', 'Có đất ở hợp pháp'],
    tai_lieu: ['Đơn xin hỗ trợ', 'Giấy tờ đất ở', 'Sổ hộ khẩu', 'Biên bản kiểm tra hiện trạng nhà'],
  },
  {
    id: 6, ten: 'Vay vốn tạo việc làm', danh_muc: 'Việc làm',
    icon: 'work', iconBg: 'bg-teal-50', iconColor: 'text-teal-500',
    status: 'OPEN', han_nop: '30/09/2026', muc_ho_tro: 'Vay đến 50 tr.',
    con_lai: 190, so_ho_so: 41,
    mo_ta: 'Cho vay ưu đãi lãi suất thấp để hỗ trợ người nghèo, người khuyết tật tự tạo việc làm, phát triển kinh tế hộ gia đình.',
    yeu_cau: ['Hộ nghèo, cận nghèo hoặc người khuyết tật', 'Có phương án sản xuất kinh doanh khả thi', 'Không có nợ xấu tại tổ chức tín dụng'],
    tai_lieu: ['Phương án sản xuất kinh doanh', 'CCCD', 'Sổ hộ khẩu', 'Xác nhận hộ nghèo/khuyết tật'],
  },
]

const programs = ref(fallbackPrograms)

onMounted(async () => {
  loading.value = true
  try {
    const res = await programsApi.getAll()
    const list = res.data.content || res.data || []
    if (list.length) {
      programs.value = list.map(p => ({
        id: p.id,
        ten: p.tenChuongTrinh || p.ten || '',
        danh_muc: p.danhMuc || 'Trợ cấp',
        icon: 'volunteer_activism',
        iconBg: 'bg-primary/10',
        iconColor: 'text-primary',
        status: p.trangThai === 'OPEN' || p.trangThai === 'ACTIVE' ? 'OPEN' : p.trangThai === 'UPCOMING' || p.trangThai === 'SOON' ? 'SOON' : 'CLOSED',
        han_nop: p.ngayKetThuc ? new Date(p.ngayKetThuc).toLocaleDateString('vi-VN') : '—',
        muc_ho_tro: p.mucHoTro || p.nganSach ? (p.nganSach.toLocaleString() + 'đ') : 'Theo quy định',
        con_lai: p.ngayKetThuc ? Math.max(0, Math.ceil((new Date(p.ngayKetThuc) - Date.now()) / 86400000)) : 0,
        so_ho_so: p.soHoSo || 0,
        mo_ta: p.moTa || '',
        yeu_cau: p.yeuCau || ['Là công dân Việt Nam', 'Nằm trong đối tượng trợ cấp'],
        tai_lieu: p.taiLieu || ['CCCD / CMND', 'Đơn xin trợ cấp', 'Sổ hộ khẩu'],
      }))
    }
  } catch {
    // Giữ fallback data
  } finally {
    loading.value = false
  }
})

const categories = computed(() => [
  { key:'all',       label:'Tất cả',       icon:'apps',              count: programs.value.length },
  { key:'OPEN',      label:'Đang mở',      icon:'check_circle',      count: programs.value.filter(p=>p.status==='OPEN').length },
  { key:'SOON',      label:'Sắp mở',       icon:'schedule',          count: programs.value.filter(p=>p.status==='SOON').length },
  { key:'CLOSED',    label:'Đã kết thúc',  icon:'cancel',            count: programs.value.filter(p=>p.status==='CLOSED').length },
])

const filteredPrograms = computed(() => {
  let list = programs.value
  if (activeCategory.value !== 'all') list = list.filter(p => p.status === activeCategory.value)
  if (search.value.trim()) {
    const q = search.value.toLowerCase()
    list = list.filter(p => p.ten.toLowerCase().includes(q) || p.danh_muc.toLowerCase().includes(q) || p.mo_ta.toLowerCase().includes(q))
  }
  return list
})

const STATUS_STYLES = {
  OPEN:   { badge: 'bg-emerald-100 text-emerald-700', label: 'Đang mở' },
  SOON:   { badge: 'bg-amber-100 text-amber-700',     label: 'Sắp mở' },
  CLOSED: { badge: 'bg-slate-100 text-slate-500',     label: 'Đã kết thúc' },
}
const statusStyle = (s) => STATUS_STYLES[s] || STATUS_STYLES.CLOSED
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
.line-clamp-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; line-clamp: 2; }
</style>
