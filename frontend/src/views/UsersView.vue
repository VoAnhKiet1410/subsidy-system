<template>
  <div class="p-8 space-y-8">
    <div class="flex flex-col md:flex-row md:items-end justify-between gap-4">
      <div>
        <p class="text-xs font-bold uppercase tracking-widest text-primary/60 mb-1">Quản trị hệ thống</p>
        <h2 class="text-3xl font-extrabold tracking-tight text-on-surface">Quản lý Người dùng</h2>
        <p class="text-on-surface-variant mt-1">Quản lý tài khoản và phân quyền trong hệ thống.</p>
      </div>
      <button @click="openCreate"
        class="flex items-center gap-2 px-5 py-3 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 shadow-md shadow-primary/20 whitespace-nowrap">
        <span class="material-symbols-outlined text-sm">person_add</span>Thêm người dùng
      </button>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
      <div v-for="s in userStats" :key="s.label" class="bg-white rounded-2xl border border-slate-200/80 p-5 flex items-center gap-4">
        <div :class="['w-11 h-11 rounded-xl flex items-center justify-center shrink-0', s.iconBg]">
          <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ s.icon }}</span>
        </div>
        <div>
          <p class="text-xs text-slate-400 font-semibold">{{ s.label }}</p>
          <p class="text-2xl font-black text-slate-800">{{ s.value }}</p>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-wrap items-center gap-3">
      <div class="relative flex-1 min-w-[200px]">
        <span class="material-symbols-outlined absolute left-3 top-1/2 -translate-y-1/2 text-slate-400 text-sm">search</span>
        <input v-model="search" type="text" placeholder="Tìm tên, email, username..."
          class="w-full pl-9 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20" />
      </div>
      <div class="flex gap-2">
        <button v-for="rf in roleFilters" :key="rf.val" @click="filterRole = rf.val"
          :class="['px-4 py-2.5 rounded-xl text-sm font-semibold transition-colors', filterRole === rf.val ? 'bg-primary text-white' : 'bg-white border border-slate-200 text-slate-500 hover:bg-slate-50']">
          {{ rf.label }}
        </button>
      </div>
    </div>

    <!-- Table -->
    <div class="bg-white rounded-2xl shadow-sm border border-slate-200/80 overflow-hidden">
      <div v-if="loading" class="p-12 flex justify-center">
        <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
      </div>
      <table v-else class="w-full text-sm">
        <thead>
          <tr class="bg-slate-50/70 border-b border-slate-100">
            <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-wider">Người dùng</th>
            <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-wider">Liên hệ</th>
            <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-wider">Vai trò</th>
            <th class="text-left px-6 py-4 text-xs font-black text-slate-400 uppercase tracking-wider">Ngày tạo</th>
            <th class="px-6 py-4 text-right text-xs font-black text-slate-400 uppercase tracking-wider">Thao tác</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-50">
          <tr v-if="filteredUsers.length === 0">
            <td colspan="5" class="text-center py-16 text-slate-400">
              <span class="material-symbols-outlined text-4xl text-slate-200 block mb-2">group_off</span>
              Không tìm thấy người dùng
            </td>
          </tr>
          <tr v-for="u in filteredUsers" :key="u.id" class="hover:bg-slate-50/50 transition-colors group">
            <td class="px-6 py-4">
              <div class="flex items-center gap-3">
                <div :class="['w-10 h-10 rounded-xl flex items-center justify-center text-sm font-black shrink-0', avatarBg(u.vai_tro)]">
                  {{ initials(u.ho_va_ten || u.ten_dang_nhap) }}
                </div>
                <div>
                  <p class="font-bold text-slate-800">{{ u.ho_va_ten || u.ten_dang_nhap }}</p>
                  <p class="text-xs text-slate-400">@{{ u.ten_dang_nhap }}</p>
                </div>
              </div>
            </td>
            <td class="px-6 py-4">
              <p class="text-slate-700 text-xs">{{ u.email }}</p>
              <p class="text-slate-400 text-xs">{{ u.so_dien_thoai || '—' }}</p>
            </td>
            <td class="px-6 py-4">
              <span :class="['px-3 py-1 rounded-full text-xs font-bold', roleBadge(u.vai_tro)]">{{ roleLabel(u.vai_tro) }}</span>
            </td>
            <td class="px-6 py-4 text-slate-400 text-xs">{{ formatDate(u.created_at) }}</td>
            <td class="px-6 py-4 text-right opacity-0 group-hover:opacity-100 transition-opacity">
              <div class="flex justify-end gap-2">
                <button @click="openEdit(u)" class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 text-slate-600 text-xs font-bold rounded-lg flex items-center gap-1">
                  <span class="material-symbols-outlined text-sm">edit</span>Sửa
                </button>
                <button @click="confirmDel(u)" class="px-3 py-1.5 bg-red-50 hover:bg-red-100 text-red-600 text-xs font-bold rounded-lg flex items-center gap-1">
                  <span class="material-symbols-outlined text-sm">delete</span>
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <Teleport to="body">
      <div v-if="showModal" class="fixed inset-0 z-[100] flex items-center justify-center bg-black/40 backdrop-blur-sm p-4" @click.self="showModal = false">
        <div class="bg-white rounded-3xl shadow-2xl w-full max-w-md overflow-hidden">
          <div class="h-1.5 w-full bg-gradient-to-r from-primary to-blue-400"></div>
          <div class="p-8 space-y-4">
            <h3 class="text-xl font-black text-slate-800">{{ editUser ? 'Sửa người dùng' : 'Thêm người dùng mới' }}</h3>
            <div class="grid grid-cols-2 gap-4">
              <div class="col-span-2">
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Họ và tên <span class="text-red-500">*</span></label>
                <input v-model="mf.ho_va_ten" type="text" placeholder="Nhập họ và tên..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên đăng nhập <span class="text-red-500">*</span></label>
                <input v-model="mf.ten_dang_nhap" type="text" :disabled="!!editUser"
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 disabled:opacity-50" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Vai trò</label>
                <select v-model="mf.vai_tro" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20">
                  <option value="ADMIN">Quản trị viên</option>
                  <option value="OFFICER">Cán bộ xét duyệt</option>
                  <option value="ACCOUNTANT">Cán bộ tài chính</option>
                  <option value="CITIZEN">Người dân</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Email</label>
                <input v-model="mf.email" type="email" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Số điện thoại</label>
                <input v-model="mf.so_dien_thoai" type="tel" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
              <div v-if="!editUser" class="col-span-2">
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mật khẩu <span class="text-red-500">*</span></label>
                <input v-model="mf.mat_khau" type="password" placeholder="Ít nhất 8 ký tự..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
              </div>
            </div>
            <p v-if="modalError" class="text-sm text-red-600 bg-red-50 p-3 rounded-xl flex gap-2 border border-red-200">
              <span class="material-symbols-outlined text-sm">error</span>{{ modalError }}
            </p>
            <div class="flex justify-end gap-3 pt-1">
              <button @click="showModal = false" class="px-5 py-2.5 text-sm font-semibold text-slate-500 hover:bg-slate-100 rounded-xl">Hủy</button>
              <button @click="saveUser" :disabled="modalSaving"
                class="px-6 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 disabled:opacity-60">
                {{ modalSaving ? 'Đang lưu...' : (editUser ? 'Cập nhật' : 'Tạo') }}
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
const users = ref([])
const search = ref('')
const filterRole = ref('ALL')
const showModal = ref(false)
const editUser = ref(null)
const modalSaving = ref(false)
const modalError = ref('')
const mf = ref({ ho_va_ten:'', ten_dang_nhap:'', email:'', so_dien_thoai:'', vai_tro:'CITIZEN', mat_khau:'' })

const roleFilters = [
  { val:'ALL', label:'Tất cả' }, { val:'ADMIN', label:'Admin' },
  { val:'OFFICER', label:'Xét duyệt' }, { val:'ACCOUNTANT', label:'Tài chính' }, { val:'CITIZEN', label:'Người dân' }
]

const filteredUsers = computed(() => users.value.filter(u => {
  const q = search.value.toLowerCase()
  const ms = !q || [u.ho_va_ten, u.ten_dang_nhap, u.email].some(f => f?.toLowerCase().includes(q))
  const mr = filterRole.value === 'ALL' || u.vai_tro === filterRole.value
  return ms && mr
}))

const userStats = computed(() => [
  { label:'Tổng', value: users.value.length, icon:'group', iconBg:'bg-blue-100 text-blue-600' },
  { label:'Xét duyệt', value: users.value.filter(u=>u.vai_tro==='OFFICER').length, icon:'rate_review', iconBg:'bg-amber-100 text-amber-600' },
  { label:'Tài chính', value: users.value.filter(u=>u.vai_tro==='ACCOUNTANT').length, icon:'account_balance', iconBg:'bg-purple-100 text-purple-600' },
  { label:'Người dân', value: users.value.filter(u=>u.vai_tro==='CITIZEN').length, icon:'person', iconBg:'bg-emerald-100 text-emerald-600' },
])

onMounted(async () => {
  try {
    const res = await http.get('/users')
    let list = res.data?.content || res.data || []
    users.value = list.map(u => ({
      id: u.id,
      ho_va_ten: u.fullName || '',
      ten_dang_nhap: u.username || '',
      email: u.email || '',
      so_dien_thoai: u.phone || '',
      vai_tro: (u.roles && u.roles.length > 0) ? u.roles[0] : 'CITIZEN',
      created_at: u.createdAt || ''
    }))
  } catch {
    users.value = [
      { id:1, ten_dang_nhap:'admin', ho_va_ten:'Trần Quản Trị', email:'admin@system.vn', so_dien_thoai:'0901234567', vai_tro:'ADMIN', created_at: new Date(Date.now()-86400000*30).toISOString() },
      { id:2, ten_dang_nhap:'officer1', ho_va_ten:'Lê Cán Bộ', email:'canbo@system.vn', so_dien_thoai:'0911222333', vai_tro:'OFFICER', created_at: new Date(Date.now()-86400000*20).toISOString() },
      { id:3, ten_dang_nhap:'accountant1', ho_va_ten:'Phạm Kế Toán', email:'ketoan@system.vn', so_dien_thoai:'0922333444', vai_tro:'ACCOUNTANT', created_at: new Date(Date.now()-86400000*15).toISOString() },
      { id:4, ten_dang_nhap:'citizen1', ho_va_ten:'Nguyễn Văn A', email:'nva@gmail.com', so_dien_thoai:'0988777666', vai_tro:'CITIZEN', created_at: new Date(Date.now()-86400000*5).toISOString() },
      { id:5, ten_dang_nhap:'citizen2', ho_va_ten:'Trần Thị B', email:'ttb@gmail.com', so_dien_thoai:'0977666555', vai_tro:'CITIZEN', created_at: new Date(Date.now()-86400000*3).toISOString() },
    ]
  } finally { loading.value = false }
})

function openCreate() { editUser.value=null; mf.value={ ho_va_ten:'',ten_dang_nhap:'',email:'',so_dien_thoai:'',vai_tro:'CITIZEN',mat_khau:'' }; modalError.value=''; showModal.value=true }
function openEdit(u) { editUser.value=u; mf.value={...u}; modalError.value=''; showModal.value=true }
function confirmDel(u) { if(confirm(`Xóa người dùng "${u.ho_va_ten || u.ten_dang_nhap}"?`)) { users.value = users.value.filter(x=>x.id!==u.id); ui.showSuccess('Đã xóa!') } }

async function saveUser() {
  if (!mf.value.ten_dang_nhap || !mf.value.ho_va_ten) { modalError.value='Vui lòng nhập đủ thông tin bắt buộc.'; return }
  modalSaving.value=true; modalError.value=''
  try {
    const payload = {
      fullName: mf.value.ho_va_ten,
      username: mf.value.ten_dang_nhap,
      email: mf.value.email,
      phone: mf.value.so_dien_thoai,
      roles: [mf.value.vai_tro]
    }
    if (mf.value.mat_khau) payload.password = mf.value.mat_khau;

    if (editUser.value) {
      await http.put(`/users/${editUser.value.id}`, payload)
      const i = users.value.findIndex(u=>u.id===editUser.value.id)
      if (i!==-1) users.value[i]={...users.value[i],...mf.value}
      ui.showSuccess('Đã cập nhật!')
    } else {
      const res = await http.post('/users', payload)
      users.value.unshift({...mf.value, id: res.data?.id || Date.now(), created_at:new Date().toISOString()})
      ui.showSuccess('Đã tạo tài khoản!')
    }
    showModal.value=false
  } catch(e) { modalError.value = e.response?.data?.message || 'Có lỗi xảy ra.' } finally { modalSaving.value=false }
}

function roleLabel(r) { return { ADMIN:'Quản trị viên', OFFICER:'Cán bộ xét duyệt', ACCOUNTANT:'Cán bộ tài chính', CITIZEN:'Người dân' }[r]||r }
function roleBadge(r) { return { ADMIN:'bg-red-100 text-red-700', OFFICER:'bg-amber-100 text-amber-700', ACCOUNTANT:'bg-blue-100 text-blue-700', CITIZEN:'bg-emerald-100 text-emerald-700' }[r]||'bg-slate-100 text-slate-600' }
function avatarBg(r) { return { ADMIN:'bg-red-100 text-red-700', OFFICER:'bg-amber-100 text-amber-700', ACCOUNTANT:'bg-blue-100 text-blue-700', CITIZEN:'bg-emerald-100 text-emerald-700' }[r]||'bg-slate-100 text-slate-600' }
function initials(n) { return (n||'').split(' ').slice(-2).map(w=>w[0]).join('').toUpperCase()||'?' }
function formatDate(d) { if(!d) return '—'; return new Date(d).toLocaleDateString('vi-VN') }
</script>
