<template>
  <div class="p-8 space-y-8 max-w-5xl mx-auto">
    <!-- Header -->
    <div class="flex items-end justify-between">
      <div>
        <p class="text-xs font-bold uppercase tracking-widest text-primary/60 mb-1">Tài khoản</p>
        <h2 class="text-3xl font-extrabold tracking-tight text-on-surface">Hồ sơ cá nhân</h2>
        <p class="text-on-surface-variant mt-1">Quản lý thông tin tài khoản và bảo mật của bạn.</p>
      </div>
      <button @click="saveProfile" :disabled="saving"
        class="flex items-center gap-2 px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 transition-opacity disabled:opacity-60 shadow-md shadow-primary/20">
        <span v-if="saving" class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
        <span v-else class="material-symbols-outlined text-sm">save</span>
        {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
      </button>
    </div>

    <!-- Profile Hero -->
    <div class="bg-gradient-to-br from-primary to-blue-700 rounded-3xl p-8 relative overflow-hidden">
      <div class="absolute -right-8 -bottom-8 opacity-10">
        <span class="material-symbols-outlined text-[180px]" style="font-variation-settings:'FILL' 1;">account_circle</span>
      </div>
      <div class="relative flex items-center gap-6">
        <!-- Avatar -->
        <div class="relative group">
          <div class="w-24 h-24 rounded-2xl bg-white/20 backdrop-blur-sm border-2 border-white/30 flex items-center justify-center text-4xl font-black text-white cursor-pointer"
            @click="triggerAvatarUpload">
            {{ userInitials }}
          </div>
          <div class="absolute inset-0 bg-black/30 rounded-2xl flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity cursor-pointer"
            @click="triggerAvatarUpload">
            <span class="material-symbols-outlined text-white text-lg">photo_camera</span>
          </div>
          <input ref="avatarInput" type="file" accept="image/*" class="hidden" @change="handleAvatarChange" />
        </div>
        <div>
          <h3 class="text-2xl font-black text-white">{{ authStore.user?.fullName || authStore.user?.username || 'Người dùng' }}</h3>
          <p class="text-white/70 text-sm">{{ authStore.user?.email || '' }}</p>
          <div class="flex items-center gap-3 mt-3">
            <span :class="['px-3 py-1 rounded-full text-xs font-bold', roleBadge]">{{ roleLabel }}</span>
            <span class="flex items-center gap-1.5 text-white/70 text-xs">
              <span class="w-2 h-2 bg-emerald-400 rounded-full"></span>
              Đang hoạt động
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Content Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">

      <!-- Left: Form chỉnh sửa -->
      <div class="lg:col-span-2 space-y-6">

        <!-- Thông tin cơ bản -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200/80 p-6">
          <h3 class="text-xs font-black uppercase tracking-widest text-slate-400 mb-5">Thông tin cơ bản</h3>
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Họ và tên <span class="text-red-500">*</span></label>
              <input v-model="form.fullName" type="text" placeholder="Nhập họ và tên..."
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên đăng nhập</label>
              <input :value="authStore.user?.username" type="text" disabled
                class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-400 cursor-not-allowed" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Email <span class="text-red-500">*</span></label>
              <input v-model="form.email" type="email" placeholder="email@example.com"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Số điện thoại</label>
              <input v-model="form.so_dien_thoai" type="tel" placeholder="VD: 0901234567"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
            </div>
            <div class="md:col-span-2">
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Địa chỉ</label>
              <input v-model="form.dia_chi" type="text" placeholder="Địa chỉ cư trú..."
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
            </div>
          </div>
          <div v-if="updateSuccess" class="mt-4 flex items-center gap-2 text-emerald-700 bg-emerald-50 border border-emerald-200 px-4 py-3 rounded-xl text-sm">
            <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">check_circle</span>
            Cập nhật thành công!
          </div>
          <div v-if="updateError" class="mt-4 flex items-center gap-2 text-red-700 bg-red-50 border border-red-200 px-4 py-3 rounded-xl text-sm">
            <span class="material-symbols-outlined text-sm">error</span> {{ updateError }}
          </div>
        </div>

        <!-- Đổi mật khẩu -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200/80 p-6">
          <div class="flex items-center justify-between mb-5">
            <h3 class="text-xs font-black uppercase tracking-widest text-slate-400">Đổi mật khẩu</h3>
            <button @click="showPasswordForm = !showPasswordForm"
              class="text-xs font-bold text-primary hover:underline">
              {{ showPasswordForm ? 'Ẩn' : 'Thay đổi' }}
            </button>
          </div>
          <div v-if="!showPasswordForm" class="flex items-center gap-3 py-2">
            <div class="flex gap-1">
              <div v-for="i in 10" :key="i" class="w-2 h-2 rounded-full bg-slate-200"></div>
            </div>
            <span class="text-xs text-slate-400">Mật khẩu được bảo mật</span>
          </div>
          <div v-else class="space-y-4">
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mật khẩu hiện tại <span class="text-red-500">*</span></label>
              <div class="relative">
                <input v-model="pwForm.current" :type="showPw.current ? 'text' : 'password'" placeholder="Nhập mật khẩu hiện tại..."
                  class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 pr-11 transition-all" />
                <button @click="showPw.current = !showPw.current" class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                  <span class="material-symbols-outlined text-sm">{{ showPw.current ? 'visibility_off' : 'visibility' }}</span>
                </button>
              </div>
            </div>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mật khẩu mới <span class="text-red-500">*</span></label>
                <div class="relative">
                  <input v-model="pwForm.newPw" :type="showPw.new ? 'text' : 'password'" placeholder="Ít nhất 8 ký tự..."
                    class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 pr-11 transition-all" />
                  <button @click="showPw.new = !showPw.new" class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                    <span class="material-symbols-outlined text-sm">{{ showPw.new ? 'visibility_off' : 'visibility' }}</span>
                  </button>
                </div>
                <!-- Strength indicator -->
                <div v-if="pwForm.newPw" class="mt-2 flex gap-1.5">
                  <div v-for="i in 4" :key="i" :class="['h-1 flex-1 rounded-full transition-colors', i <= pwStrength ? pwStrengthColor : 'bg-slate-200']"></div>
                </div>
              </div>
              <div>
                <label class="block text-sm font-semibold text-slate-600 mb-1.5">Xác nhận mật khẩu <span class="text-red-500">*</span></label>
                <div class="relative">
                  <input v-model="pwForm.confirm" :type="showPw.confirm ? 'text' : 'password'" placeholder="Nhập lại mật khẩu..."
                    class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 pr-11 transition-all"
                    :class="pwForm.confirm && pwForm.confirm !== pwForm.newPw ? 'border-red-300 focus:ring-red-200' : ''" />
                  <button @click="showPw.confirm = !showPw.confirm" class="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600">
                    <span class="material-symbols-outlined text-sm">{{ showPw.confirm ? 'visibility_off' : 'visibility' }}</span>
                  </button>
                </div>
                <p v-if="pwForm.confirm && pwForm.confirm !== pwForm.newPw" class="text-xs text-red-500 mt-1">Mật khẩu không khớp</p>
              </div>
            </div>
            <div v-if="pwError" class="flex items-center gap-2 text-red-700 bg-red-50 border border-red-200 px-4 py-3 rounded-xl text-sm">
              <span class="material-symbols-outlined text-sm">error</span> {{ pwError }}
            </div>
            <div class="flex justify-end">
              <button @click="changePassword" :disabled="savingPw || pwForm.confirm !== pwForm.newPw || !pwForm.current"
                class="px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 disabled:opacity-50 transition-opacity">
                {{ savingPw ? 'Đang cập nhật...' : 'Cập nhật mật khẩu' }}
              </button>
            </div>
          </div>
        </div>

      </div>

      <!-- Right: Stat cards -->
      <div class="space-y-5">
        <!-- Role & Permissions -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200/80 p-6">
          <h3 class="text-xs font-black uppercase tracking-widest text-slate-400 mb-4">Vai trò & Quyền hạn</h3>
          <div class="flex items-center gap-3 mb-4">
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center', roleIconBg]">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ roleIcon }}</span>
            </div>
            <div>
              <p class="font-bold text-slate-800 text-sm">{{ roleLabel }}</p>
              <p class="text-xs text-slate-400">{{ authStore.user?.username }}</p>
            </div>
          </div>
          <div class="space-y-2">
            <div v-for="perm in permissions" :key="perm" class="flex items-center gap-2.5">
              <span class="w-4 h-4 rounded-full bg-emerald-100 text-emerald-600 flex items-center justify-center flex-shrink-0">
                <span class="material-symbols-outlined text-[10px]" style="font-variation-settings:'FILL' 1;">check</span>
              </span>
              <span class="text-xs text-slate-600">{{ perm }}</span>
            </div>
          </div>
        </div>

        <!-- Activity stats -->
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200/80 p-6">
          <h3 class="text-xs font-black uppercase tracking-widest text-slate-400 mb-4">Hoạt động</h3>
          <div class="space-y-3">
            <div v-for="stat in activityStats" :key="stat.label" class="flex items-center justify-between py-2 border-b border-slate-50 last:border-0">
              <span class="text-sm text-slate-500 flex items-center gap-2">
                <span class="material-symbols-outlined text-sm text-slate-400">{{ stat.icon }}</span>
                {{ stat.label }}
              </span>
              <span class="font-bold text-slate-800 text-sm">{{ stat.value }}</span>
            </div>
          </div>
        </div>

        <!-- Danger zone (ADMIN only thấy) -->
        <div v-if="authStore.isAdmin" class="bg-red-50 border border-red-200 rounded-2xl p-6">
          <h3 class="text-xs font-black uppercase tracking-widest text-red-400 mb-3">Khu vực nguy hiểm</h3>
          <p class="text-xs text-red-600 mb-4">Xóa tài khoản sẽ xóa toàn bộ dữ liệu. Hành động này không thể hoàn tác.</p>
          <button class="w-full py-2.5 border border-red-300 text-red-700 text-sm font-bold rounded-xl hover:bg-red-100 transition-colors">
            Xóa tài khoản
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { authStore } from '../stores/auth'
import { useUI } from '../stores/ui'

const ui = useUI()
const saving = ref(false)
const savingPw = ref(false)
const updateSuccess = ref(false)
const updateError = ref('')
const pwError = ref('')
const showPasswordForm = ref(false)
const avatarInput = ref(null)
const showPw = ref({ current: false, new: false, confirm: false })

const form = ref({
  fullName: authStore.user?.fullName || '',
  email: authStore.user?.email || '',
  so_dien_thoai: authStore.user?.so_dien_thoai || '',
  dia_chi: authStore.user?.dia_chi || '',
})

const pwForm = ref({ current: '', newPw: '', confirm: '' })

const userInitials = computed(() => {
  const n = form.value.fullName || authStore.user?.username || ''
  return n.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase() || '?'
})

const roleLabel = computed(() => ({
  ADMIN: 'Quản trị viên', OFFICER: 'Cán bộ xét duyệt', ACCOUNTANT: 'Cán bộ tài chính', CITIZEN: 'Người dân'
}[authStore.role] || authStore.role))

const roleBadge = computed(() => ({
  ADMIN: 'bg-red-400/20 text-red-200', OFFICER: 'bg-amber-400/20 text-amber-100',
  ACCOUNTANT: 'bg-blue-400/20 text-blue-100', CITIZEN: 'bg-emerald-400/20 text-emerald-100'
}[authStore.role] || 'bg-white/20 text-white'))

const roleIcon = computed(() => ({
  ADMIN: 'shield_person', OFFICER: 'rate_review', ACCOUNTANT: 'account_balance', CITIZEN: 'person'
}[authStore.role] || 'person'))

const roleIconBg = computed(() => ({
  ADMIN: 'bg-red-100 text-red-600', OFFICER: 'bg-amber-100 text-amber-600',
  ACCOUNTANT: 'bg-blue-100 text-blue-600', CITIZEN: 'bg-emerald-100 text-emerald-600'
}[authStore.role] || 'bg-slate-100 text-slate-600'))

const permissionMap = {
  ADMIN: ['Xem tất cả dữ liệu', 'Quản lý người dùng', 'Phân quyền hệ thống', 'Xét duyệt hồ sơ', 'Quản lý chương trình', 'Xuất báo cáo'],
  OFFICER: ['Xem hồ sơ được phân công', 'Xét duyệt hồ sơ', 'Xem đánh giá AI', 'Ghi chú xét duyệt'],
  ACCOUNTANT: ['Xem ngân sách', 'Quản lý chi trả', 'Xuất báo cáo tài chính', 'Thanh toán lô'],
  CITIZEN: ['Xem chương trình trợ cấp', 'Nộp đơn đăng ký', 'Xem hồ sơ của mình', 'Nhận thông báo'],
}
const permissions = computed(() => permissionMap[authStore.role] || [])

const activityStats = ref([
  { icon: 'login', label: 'Lần đăng nhập', value: '—' },
  { icon: 'calendar_today', label: 'Ngày tạo tài khoản', value: '—' },
  { icon: 'edit', label: 'Lần cập nhật gần nhất', value: '—' },
])

const pwStrength = computed(() => {
  const p = pwForm.value.newPw
  if (!p) return 0
  let s = 0
  if (p.length >= 8) s++
  if (/[A-Z]/.test(p)) s++
  if (/[0-9]/.test(p)) s++
  if (/[^a-zA-Z0-9]/.test(p)) s++
  return s
})
const pwStrengthColor = computed(() => {
  const c = ['bg-red-400', 'bg-orange-400', 'bg-amber-400', 'bg-emerald-500']
  return c[pwStrength.value - 1] || 'bg-red-400'
})

async function saveProfile() {
  if (!form.value.fullName.trim()) { updateError.value = 'Vui lòng nhập họ và tên.'; return }
  saving.value = true; updateError.value = ''; updateSuccess.value = false
  try {
    // await usersApi.updateProfile(form.value)
    await new Promise(r => setTimeout(r, 800))
    authStore.user = { ...authStore.user, fullName: form.value.fullName, email: form.value.email }
    updateSuccess.value = true
    setTimeout(() => updateSuccess.value = false, 3000)
    ui.showSuccess('Cập nhật thông tin thành công!')
  } catch (e) {
    updateError.value = 'Không thể cập nhật. Vui lòng thử lại.'
  } finally { saving.value = false }
}

async function changePassword() {
  pwError.value = ''
  if (pwForm.value.newPw.length < 8) { pwError.value = 'Mật khẩu mới phải ít nhất 8 ký tự.'; return }
  if (pwForm.value.newPw !== pwForm.value.confirm) { pwError.value = 'Mật khẩu xác nhận không khớp.'; return }
  savingPw.value = true
  try {
    await new Promise(r => setTimeout(r, 800))
    pwForm.value = { current: '', newPw: '', confirm: '' }
    showPasswordForm.value = false
    ui.showSuccess('Đã đổi mật khẩu thành công!')
  } catch { pwError.value = 'Mật khẩu hiện tại không đúng.' } finally { savingPw.value = false }
}

function triggerAvatarUpload() { avatarInput.value?.click() }
function handleAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  ui.showSuccess('Đã cập nhật ảnh đại diện!')
}
</script>
