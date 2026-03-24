<template>
  <div class="p-8 space-y-8">
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <p class="text-xs font-bold uppercase tracking-widest text-primary/60 mb-1">Danh mục</p>
        <h2 class="text-3xl font-extrabold tracking-tight text-on-surface">Đối tượng hưởng trợ cấp</h2>
        <p class="text-on-surface-variant mt-1">Quản lý các nhóm đối tượng được hưởng trợ cấp xã hội.</p>
      </div>
      <button @click="openCreate"
        class="flex items-center gap-2 px-5 py-3 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20 whitespace-nowrap">
        <span class="material-symbols-outlined text-sm">add</span>Thêm đối tượng
      </button>
    </div>

    <!-- Search -->
    <div class="relative max-w-md">
      <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm">search</span>
      <input v-model="search" type="text" placeholder="Tìm kiếm đối tượng..."
        class="w-full pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
    </div>

    <!-- Grid cards -->
    <div v-if="loading" class="flex justify-center py-12">
      <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
    </div>
    <div v-else class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-5">
      <!-- Empty -->
      <div v-if="filteredItems.length === 0" class="col-span-full py-16 text-center bg-white rounded-2xl border border-slate-200">
        <span class="material-symbols-outlined text-5xl text-slate-200 block mb-3">group</span>
        <p class="font-semibold text-slate-600">Không tìm thấy đối tượng nào</p>
      </div>

      <!-- Card -->
      <div v-for="item in filteredItems" :key="item.id"
        class="group bg-white border border-slate-200/80 rounded-2xl p-6 shadow-sm hover:shadow-md hover:border-primary/30 transition-all relative overflow-hidden">
        <div class="absolute top-0 right-0 w-24 h-24 opacity-[0.04]">
          <span class="material-symbols-outlined text-[100px]" style="font-variation-settings:'FILL' 1;">people</span>
        </div>

        <!-- Header -->
        <div class="flex items-start justify-between mb-4">
          <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center', item.color]">
            <span class="material-symbols-outlined text-lg" style="font-variation-settings:'FILL' 1;">{{ item.icon || 'folder_special' }}</span>
          </div>
          <span :class="['px-2.5 py-1 rounded-full text-xs font-bold', item.trang_thai === 'ACTIVE' ? 'bg-emerald-100 text-emerald-700' : 'bg-slate-100 text-slate-500']">
            {{ item.trang_thai === 'ACTIVE' ? 'Đang áp dụng' : 'Tạm ngưng' }}
          </span>
        </div>

        <h3 class="font-black text-slate-800 text-lg mb-1">{{ item.ten_doi_tuong }}</h3>
        <p class="text-sm text-slate-500 leading-relaxed line-clamp-2 mb-4">{{ item.mo_ta || 'Chưa có mô tả.' }}</p>

        <!-- Meta -->
        <div class="flex items-center gap-4 text-xs text-slate-400">
          <span class="flex items-center gap-1.5">
            <span class="material-symbols-outlined text-sm">folder</span>
            {{ item.ho_so_count || 0 }} hồ sơ
          </span>
          <span class="flex items-center gap-1.5">
            <span class="material-symbols-outlined text-sm">calendar_month</span>
            {{ formatDate(item.created_at) }}
          </span>
        </div>

        <!-- Actions on hover -->
        <div class="flex justify-end gap-2 mt-4 pt-4 border-t border-slate-50 opacity-0 group-hover:opacity-100 transition-opacity">
          <button @click="openEdit(item)" class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-bold rounded-lg flex items-center gap-1 transition-colors">
            <span class="material-symbols-outlined text-sm">edit</span>Sửa
          </button>
          <button @click="toggleStatus(item)"
            :class="['px-3 py-1.5 text-xs font-bold rounded-lg flex items-center gap-1 transition-colors', item.trang_thai === 'ACTIVE' ? 'bg-amber-50 hover:bg-amber-100 text-amber-700' : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-700']">
            <span class="material-symbols-outlined text-sm">{{ item.trang_thai === 'ACTIVE' ? 'pause' : 'play_arrow' }}</span>
            {{ item.trang_thai === 'ACTIVE' ? 'Tạm ngưng' : 'Kích hoạt' }}
          </button>
          <button @click="confirmDel(item)" class="px-3 py-1.5 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-bold rounded-lg flex items-center gap-1 transition-colors">
            <span class="material-symbols-outlined text-sm">delete</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showModal = false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-lg overflow-hidden">
          <div class="h-1.5 w-full bg-gradient-to-r from-primary to-blue-400"></div>
          <div class="p-8 space-y-5">
            <h3 class="text-xl font-black text-slate-800">{{ editItem ? 'Chỉnh sửa đối tượng' : 'Thêm đối tượng mới' }}</h3>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên đối tượng <span class="text-red-500">*</span></label>
                <input v-model="mf.ten_doi_tuong" type="text" placeholder="VD: Người cao tuổi, Thiên tai..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mô tả</label>
                <textarea v-model="mf.mo_ta" rows="3" placeholder="Mô tả tiêu chí xác định đối tượng..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Trạng thái</label>
                <div class="flex gap-3">
                  <label v-for="opt in [{val:'ACTIVE', label:'Đang áp dụng'}, {val:'INACTIVE', label:'Tạm ngưng'}]" :key="opt.val"
                    :class="['flex-1 flex items-center gap-3 p-3 border rounded-xl cursor-pointer transition-colors', mf.trang_thai === opt.val ? 'border-primary bg-primary/5' : 'border-slate-200 hover:bg-slate-50']">
                    <input type="radio" v-model="mf.trang_thai" :value="opt.val" class="accent-primary" />
                    <span class="text-sm font-semibold text-slate-700">{{ opt.label }}</span>
                  </label>
                </div>
              </div>
            </div>
            <p v-if="modalError" class="text-sm text-red-600 bg-red-50 p-3 rounded-xl flex gap-2 border border-red-200">
              <span class="material-symbols-outlined text-sm">error</span>{{ modalError }}
            </p>
            <div class="flex justify-end gap-3">
              <button @click="showModal = false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="saveItem" :disabled="saving"
                class="px-6 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 disabled:opacity-60">
                {{ saving ? 'Đang lưu...' : (editItem ? 'Cập nhật' : 'Tạo mới') }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUI } from '../stores/ui'
import http from '../api/http'

const ui = useUI()
const loading = ref(true)
const items = ref([])
const search = ref('')
const showModal = ref(false)
const editItem = ref(null)
const saving = ref(false)
const modalError = ref('')
const mf = ref({ ten_doi_tuong: '', mo_ta: '', trang_thai: 'ACTIVE' })

const colorPalette = ['bg-blue-100 text-blue-600', 'bg-emerald-100 text-emerald-600', 'bg-amber-100 text-amber-600', 'bg-purple-100 text-purple-600', 'bg-red-100 text-red-600', 'bg-teal-100 text-teal-600']
const iconList = ['elderly', 'child_care', 'healing', 'flood', 'agriculture', 'family_restroom']

const filteredItems = computed(() => {
  const q = search.value.toLowerCase()
  return items.value.filter(i => !q || i.ten_doi_tuong?.toLowerCase().includes(q) || i.mo_ta?.toLowerCase().includes(q))
})

onMounted(async () => {
  try {
    const res = await http.get('/doi-tuong')
    items.value = (res.data?.content || res.data || []).map((it, idx) => ({ ...it, color: colorPalette[idx % colorPalette.length], icon: iconList[idx % iconList.length] }))
  } catch {
    items.value = [
      { id:1, ten_doi_tuong:'Người cao tuổi', mo_ta:'Người từ 60 tuổi trở lên không có người chăm sóc', trang_thai:'ACTIVE', ho_so_count:145, created_at: new Date(Date.now()-86400000*60).toISOString(), color: colorPalette[0], icon: iconList[0] },
      { id:2, ten_doi_tuong:'Trẻ em khó khăn', mo_ta:'Trẻ dưới 16 tuổi thuộc hộ nghèo hoặc cận nghèo', trang_thai:'ACTIVE', ho_so_count:89, created_at: new Date(Date.now()-86400000*45).toISOString(), color: colorPalette[1], icon: iconList[1] },
      { id:3, ten_doi_tuong:'Người khuyết tật', mo_ta:'Người bị khuyết tật thể chất hoặc tinh thần', trang_thai:'ACTIVE', ho_so_count:234, created_at: new Date(Date.now()-86400000*30).toISOString(), color: colorPalette[2], icon: iconList[2] },
      { id:4, ten_doi_tuong:'Thiên tai, dịch bệnh', mo_ta:'Hộ dân bị ảnh hưởng bởi thiên tai hoặc dịch bệnh', trang_thai:'ACTIVE', ho_so_count:512, created_at: new Date(Date.now()-86400000*20).toISOString(), color: colorPalette[3], icon: iconList[3] },
      { id:5, ten_doi_tuong:'Nông dân mất mùa', mo_ta:'Hộ sản xuất nông nghiệp bị mất mùa do thiên tai', trang_thai:'ACTIVE', ho_so_count:178, created_at: new Date(Date.now()-86400000*10).toISOString(), color: colorPalette[4], icon: iconList[4] },
      { id:6, ten_doi_tuong:'Hộ nghèo đơn thân', mo_ta:'Hộ gia đình một thành viên thuộc diện hộ nghèo', trang_thai:'INACTIVE', ho_so_count:67, created_at: new Date(Date.now()-86400000*5).toISOString(), color: colorPalette[5], icon: iconList[5] },
    ]
  } finally { loading.value = false }
})

function openCreate() { editItem.value=null; mf.value={ ten_doi_tuong:'', mo_ta:'', trang_thai:'ACTIVE' }; modalError.value=''; showModal.value=true }
function openEdit(item) { editItem.value=item; mf.value={...item}; modalError.value=''; showModal.value=true }

async function toggleStatus(item) {
  const newStatus = item.trang_thai === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  try { await http.put(`/doi-tuong/${item.id}`, { ...item, trang_thai: newStatus }) } catch {}
  item.trang_thai = newStatus
  ui.showSuccess(newStatus === 'ACTIVE' ? 'Đã kích hoạt!' : 'Đã tạm ngưng!')
}

function confirmDel(item) {
  if (confirm(`Xóa đối tượng "${item.ten_doi_tuong}"?`)) {
    items.value = items.value.filter(i => i.id !== item.id)
    ui.showSuccess('Đã xóa!')
  }
}

async function saveItem() {
  if (!mf.value.ten_doi_tuong.trim()) { modalError.value='Vui lòng nhập tên đối tượng.'; return }
  saving.value=true; modalError.value=''
  try {
    const idx = colorPalette[items.value.length % colorPalette.length]
    if (editItem.value) {
      await http.put(`/doi-tuong/${editItem.value.id}`, mf.value)
      const i = items.value.findIndex(x=>x.id===editItem.value.id)
      if (i!==-1) items.value[i]={...items.value[i],...mf.value}
      ui.showSuccess('Đã cập nhật!')
    } else {
      const res = await http.post('/doi-tuong', mf.value)
      items.value.push({ ...mf.value, ...res.data, id:Date.now(), ho_so_count:0, created_at:new Date().toISOString(), color: colorPalette[items.value.length % colorPalette.length], icon: iconList[items.value.length % iconList.length] })
      ui.showSuccess('Đã tạo đối tượng mới!')
    }
    showModal.value=false
  } catch(e) { modalError.value = e.response?.data?.message || 'Có lỗi.' } finally { saving.value=false }
}

function formatDate(d) { if(!d) return '—'; return new Date(d).toLocaleDateString('vi-VN') }
</script>
