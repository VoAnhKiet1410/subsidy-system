<template>
  <div class="p-8 space-y-8 max-w-5xl mx-auto">
    <!-- Header -->
    <div>
      <p class="text-xs font-bold uppercase tracking-widest text-primary/60 mb-1">Quản trị hệ thống</p>
      <h2 class="text-3xl font-extrabold tracking-tight text-on-surface">Cài đặt hệ thống</h2>
      <p class="text-on-surface-variant mt-1">Cấu hình hoạt động, bảo mật và thông số kỹ thuật của hệ thống.</p>
    </div>

    <!-- Tab pills -->
    <div class="flex gap-2 bg-slate-100 p-1 rounded-xl w-fit">
      <button v-for="tab in tabs" :key="tab.key" @click="activeTab = tab.key"
        :class="['flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-semibold transition-all',
          activeTab === tab.key ? 'bg-white text-primary shadow-sm' : 'text-slate-500 hover:text-slate-700']">
        <span class="material-symbols-outlined text-sm">{{ tab.icon }}</span>
        {{ tab.label }}
      </button>
    </div>

    <!-- ═══ TAB: Thông tin hệ thống ═══ -->
    <div v-if="activeTab === 'general'" class="space-y-6">
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Thông tin tổ chức</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên hệ thống</label>
            <input v-model="gen.systemName" type="text"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Tên tổ chức</label>
            <input v-model="gen.orgName" type="text"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Email liên hệ</label>
            <input v-model="gen.contactEmail" type="email"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Số điện thoại hỗ trợ</label>
            <input v-model="gen.supportPhone" type="tel"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
          </div>
          <div class="md:col-span-2">
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Địa chỉ tổ chức</label>
            <input v-model="gen.address" type="text"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
          </div>
          <div class="md:col-span-2">
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Mô tả / Giới thiệu</label>
            <textarea v-model="gen.description" rows="3"
              class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
          </div>
        </div>
        <div class="mt-5 flex justify-end">
          <button @click="saveGeneral" :disabled="saving"
            class="px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl hover:opacity-90 disabled:opacity-60 flex items-center gap-2">
            <span v-if="saving" class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
            <span v-else class="material-symbols-outlined text-sm">save</span>
            {{ saving ? 'Đang lưu...' : 'Lưu cài đặt' }}
          </button>
        </div>
      </div>

      <!-- Thông số AI -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Cấu hình AI xét duyệt</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-5">
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ngưỡng điểm ưu tiên tối thiểu</label>
            <div class="flex items-center gap-3">
              <input v-model.number="ai.minScore" type="range" min="0" max="100" class="flex-1 accent-primary" />
              <span class="text-sm font-black text-primary w-8 text-center">{{ ai.minScore }}</span>
            </div>
          </div>
          <div>
            <label class="block text-sm font-semibold text-slate-600 mb-1.5">Ngưỡng độ tin cậy AI (%)</label>
            <div class="flex items-center gap-3">
              <input v-model.number="ai.minConfidence" type="range" min="0" max="100" class="flex-1 accent-primary" />
              <span class="text-sm font-black text-primary w-8 text-center">{{ ai.minConfidence }}</span>
            </div>
          </div>
          <div class="md:col-span-2">
            <label class="flex items-center gap-3 cursor-pointer group">
              <div @click="ai.autoReview = !ai.autoReview"
                :class="['relative w-11 h-6 rounded-full transition-colors flex-shrink-0', ai.autoReview ? 'bg-primary' : 'bg-slate-300']">
                <div :class="['absolute top-0.5 w-5 h-5 bg-white rounded-full shadow-sm transition-transform', ai.autoReview ? 'translate-x-5' : 'translate-x-0.5']"></div>
              </div>
              <div>
                <p class="text-sm font-semibold text-slate-700">Tự động xét duyệt khi điểm AI cao</p>
                <p class="text-xs text-slate-400">Hệ thống tự phê duyệt nếu điểm ≥ {{ ai.minScore }} và độ tin cậy ≥ {{ ai.minConfidence }}%</p>
              </div>
            </label>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ TAB: Bảo mật ═══ -->
    <div v-if="activeTab === 'security'" class="space-y-6">
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Chính sách mật khẩu</h3>
        <div class="space-y-5">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Độ dài tối thiểu</label>
              <input v-model.number="sec.minPasswordLen" type="number" min="6" max="32"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
            </div>
            <div>
              <label class="block text-sm font-semibold text-slate-600 mb-1.5">Phiên đăng nhập (phút)</label>
              <input v-model.number="sec.sessionTimeout" type="number" min="15"
                class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20" />
            </div>
          </div>
          <div class="space-y-3">
            <toggle-row v-for="opt in secOptions" :key="opt.key" :opt="opt" v-model="sec[opt.key]" />
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Nhật ký hoạt động</h3>
        <div class="space-y-3">
          <div v-for="log in logs" :key="log.id"
            class="flex items-center gap-4 py-3 border-b border-slate-50 last:border-0">
            <div :class="['w-8 h-8 rounded-lg flex items-center justify-center flex-shrink-0 text-xs', log.type === 'ERROR' ? 'bg-red-100 text-red-600' : log.type === 'WARN' ? 'bg-amber-100 text-amber-600' : 'bg-blue-100 text-blue-600']">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">
                {{ log.type === 'ERROR' ? 'error' : log.type === 'WARN' ? 'warning' : 'info' }}
              </span>
            </div>
            <div class="flex-1 min-w-0">
              <p class="text-sm text-slate-700 font-medium">{{ log.message }}</p>
              <p class="text-xs text-slate-400">{{ log.actor }} · {{ formatDate(log.timestamp) }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ═══ TAB: Thông báo ═══ -->
    <div v-if="activeTab === 'notifications'" class="space-y-6">
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Kênh gửi thông báo</h3>
        <div class="space-y-4">
          <div v-for="ch in notifChannels" :key="ch.key"
            class="flex items-center justify-between py-4 border-b border-slate-50 last:border-0">
            <div class="flex items-center gap-4">
              <div :class="['w-10 h-10 rounded-xl flex items-center justify-center', ch.iconBg]">
                <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ ch.icon }}</span>
              </div>
              <div>
                <p class="font-semibold text-slate-800 text-sm">{{ ch.label }}</p>
                <p class="text-xs text-slate-400">{{ ch.desc }}</p>
              </div>
            </div>
            <div @click="ch.enabled = !ch.enabled"
              :class="['relative w-11 h-6 rounded-full transition-colors cursor-pointer flex-shrink-0', ch.enabled ? 'bg-primary' : 'bg-slate-300']">
              <div :class="['absolute top-0.5 w-5 h-5 bg-white rounded-full shadow-sm transition-transform', ch.enabled ? 'translate-x-5' : 'translate-x-0.5']"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Sự kiện gửi tự động</h3>
        <div class="space-y-3">
          <label v-for="ev in notifEvents" :key="ev.key"
            class="flex items-center gap-3 py-3 border-b border-slate-50 last:border-0 cursor-pointer">
            <input type="checkbox" v-model="ev.enabled" class="w-4 h-4 rounded accent-primary" />
            <div>
              <p class="text-sm font-semibold text-slate-700">{{ ev.label }}</p>
              <p class="text-xs text-slate-400">{{ ev.desc }}</p>
            </div>
          </label>
        </div>
      </div>
    </div>

    <!-- ═══ TAB: Hệ thống ═══ -->
    <div v-if="activeTab === 'system'" class="space-y-6">
      <!-- Status cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div v-for="s in systemStatus" :key="s.label"
          class="bg-white rounded-2xl border border-slate-200/80 p-6">
          <div class="flex items-center justify-between mb-3">
            <span class="text-xs font-black uppercase tracking-wider text-slate-400">{{ s.label }}</span>
            <span :class="['w-2.5 h-2.5 rounded-full', s.ok ? 'bg-emerald-500' : 'bg-red-500']"></span>
          </div>
          <p class="text-2xl font-black text-slate-800">{{ s.value }}</p>
          <p class="text-xs text-slate-400 mt-1">{{ s.sub }}</p>
        </div>
      </div>

      <!-- Thao tác hệ thống -->
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-7">
        <h3 class="text-sm font-black uppercase tracking-widest text-slate-400 mb-5">Thao tác bảo trì</h3>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <button v-for="action in sysActions" :key="action.label"
            @click="action.fn"
            :class="['flex items-center gap-4 p-4 rounded-xl border text-left transition-colors group', action.danger ? 'border-red-200 hover:bg-red-50' : 'border-slate-200 hover:bg-slate-50']">
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0', action.danger ? 'bg-red-100 text-red-600' : 'bg-slate-100 text-slate-600 group-hover:bg-primary/10 group-hover:text-primary']">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ action.icon }}</span>
            </div>
            <div>
              <p :class="['font-bold text-sm', action.danger ? 'text-red-700' : 'text-slate-800']">{{ action.label }}</p>
              <p class="text-xs text-slate-400 mt-0.5">{{ action.desc }}</p>
            </div>
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUI } from '../stores/ui'
import http from '../api/http'

const ui = useUI()
const saving = ref(false)
const activeTab = ref('general')

const tabs = [
  { key: 'general',       label: 'Tổng quan',   icon: 'tune' },
  { key: 'security',      label: 'Bảo mật',     icon: 'security' },
  { key: 'notifications', label: 'Thông báo',   icon: 'notifications' },
  { key: 'system',        label: 'Hệ thống',    icon: 'developer_board' },
]

// ─── Helper: Load / Save settings to localStorage ───
const SETTINGS_KEY = 'trocap_settings'
const defaultSettings = {
  gen: {
    systemName: '', orgName: '', contactEmail: '', supportPhone: '',
    address: '', description: '',
  },
  ai: { minScore: 0, minConfidence: 0, autoReview: false },
  sec: {
    minPasswordLen: 8, sessionTimeout: 60,
    requireUppercase: true, requireNumber: true, requireSymbol: false,
    twoFactor: false, logAllActions: true,
  },
  notifChannels: [
    { key:'email', enabled:true }, { key:'inapp', enabled:true }, { key:'sms', enabled:false },
  ],
  notifEvents: [
    { key:'submitted', enabled:true }, { key:'approved', enabled:true },
    { key:'rejected', enabled:true }, { key:'payment', enabled:true }, { key:'reminder', enabled:false },
  ],
}

function loadSettings() {
  try {
    const raw = localStorage.getItem(SETTINGS_KEY)
    return raw ? JSON.parse(raw) : null
  } catch { return null }
}
function persistSettings() {
  const data = {
    gen: { ...gen },
    ai: { ...ai },
    sec: { ...sec },
    notifChannels: notifChannels.map(c => ({ key: c.key, enabled: c.enabled })),
    notifEvents: notifEvents.map(e => ({ key: e.key, enabled: e.enabled })),
  }
  localStorage.setItem(SETTINGS_KEY, JSON.stringify(data))
}

const saved = loadSettings()

// General settings — from localStorage or empty
const gen = reactive(saved?.gen || { ...defaultSettings.gen })

// AI config
const ai = reactive(saved?.ai || { ...defaultSettings.ai })

// Security
const sec = reactive(saved?.sec || { ...defaultSettings.sec })
const secOptions = [
  { key: 'requireUppercase', label: 'Yêu cầu chữ hoa', desc: 'Mật khẩu phải chứa ít nhất 1 chữ cái viết hoa' },
  { key: 'requireNumber',    label: 'Yêu cầu số',      desc: 'Mật khẩu phải chứa ít nhất 1 chữ số' },
  { key: 'requireSymbol',    label: 'Yêu cầu ký tự đặc biệt', desc: 'Ví dụ: !@#$%^&*' },
  { key: 'twoFactor',        label: 'Xác thực 2 bước', desc: 'Gửi OTP qua email khi đăng nhập' },
  { key: 'logAllActions',    label: 'Ghi nhật ký toàn bộ hoạt động', desc: 'Lưu log tất cả thao tác người dùng' },
]

// Logs — loaded from API
const logs = ref([])

onMounted(async () => {
  try {
    const res = await http.get('/audit-logs', { params: { page: 0, size: 10 } })
    const list = res.data?.content || res.data || []
    logs.value = list.map(l => ({
      id: l.id,
      type: l.action?.includes('LOGIN_FAILED') ? 'WARN' : (l.action?.includes('ERROR') ? 'ERROR' : 'INFO'),
      message: l.details || `${l.action} bởi ${l.username}`,
      actor: l.username || 'system',
      timestamp: l.createdAt || new Date().toISOString(),
    }))
  } catch (e) {
    console.error('Lỗi tải nhật ký:', e)
    logs.value = []
  }
})

// Notification channels (UI definitions + persisted enabled state)
const channelDefs = [
  { key:'email',   label:'Email',           desc:'Gửi qua SMTP đến địa chỉ email người dùng',       icon:'mail',             iconBg:'bg-blue-100 text-blue-600' },
  { key:'inapp',   label:'Trong ứng dụng',  desc:'Hiển thị trong màn hình Thông báo',               icon:'notifications',    iconBg:'bg-primary/10 text-primary' },
  { key:'sms',     label:'SMS',             desc:'Gửi qua nhà mạng (cần cấu hình API)',             icon:'sms',              iconBg:'bg-emerald-100 text-emerald-600' },
]
const savedChannels = saved?.notifChannels || defaultSettings.notifChannels
const notifChannels = reactive(channelDefs.map(c => ({
  ...c, enabled: savedChannels.find(s => s.key === c.key)?.enabled ?? false,
})))

const eventDefs = [
  { key:'submitted', label:'Hồ sơ được nộp thành công',    desc:'Gửi thông báo xác nhận cho người nộp' },
  { key:'approved',  label:'Hồ sơ được phê duyệt',         desc:'Gửi thông báo cho người nộp và cán bộ' },
  { key:'rejected',  label:'Hồ sơ bị từ chối',             desc:'Gửi thông báo kèm lý do từ chối' },
  { key:'payment',   label:'Đã thực hiện chi trả',          desc:'Thông báo hoàn tất chi trả đến người nhận' },
  { key:'reminder',  label:'Nhắc nộp hồ sơ còn thiếu',     desc:'Nhắc sau 3 ngày nếu hồ sơ thiếu tài liệu' },
]
const savedEvents = saved?.notifEvents || defaultSettings.notifEvents
const notifEvents = reactive(eventDefs.map(e => ({
  ...e, enabled: savedEvents.find(s => s.key === e.key)?.enabled ?? false,
})))

// System status — loaded dynamically
const systemStatus = ref([])

// Load system status on mount (added to existing onMounted)
const loadSystemStatus = async () => {
  try {
    const res = await http.get('/actuator/health').catch(() => null)
    const isUp = res?.data?.status === 'UP'
    systemStatus.value = [
      { label:'Trạng thái API',      value: isUp ? 'Online' : 'Offline',  sub: isUp ? 'Hoạt động bình thường' : 'Không phản hồi', ok: isUp },
      { label:'Cơ sở dữ liệu',      value: isUp ? 'Ổn định' : '—',      sub:'Kết nối MongoDB',   ok: isUp },
      { label:'Dịch vụ AI',         value: isUp ? 'Online' : '—',       sub:'Sẵn sàng',          ok: isUp },
    ]
  } catch {
    systemStatus.value = [
      { label:'Trạng thái API', value:'Offline', sub:'Không thể kết nối', ok:false },
    ]
  }
}
loadSystemStatus()

const sysActions = [
  { icon:'cached',        label:'Xóa cache hệ thống',    desc:'Làm mới bộ nhớ tạm — không ảnh hưởng dữ liệu',     danger:false, fn: () => ui.showSuccess('Đã xóa cache!') },
  { icon:'cloud_download',label:'Sao lưu dữ liệu',       desc:'Tạo bản backup toàn bộ database',                   danger:false, fn: () => ui.showSuccess('Đang sao lưu...') },
  { icon:'analytics',     label:'Tái tính điểm AI',      desc:'Chạy lại AI scoring cho toàn bộ hồ sơ PENDING',     danger:false, fn: () => ui.showSuccess('Đang xử lý...') },
  { icon:'send',          label:'Gửi thông báo hàng loạt', desc:'Gửi thông báo đến tất cả người dùng',             danger:false, fn: () => ui.showSuccess('Đã gửi!') },
  { icon:'restart_alt',   label:'Khởi động lại dịch vụ', desc:'Restart toàn bộ backend service',                   danger:true,  fn: () => confirm('Bạn có chắc muốn restart?') },
  { icon:'delete_forever',label:'Xóa dữ liệu kiểm thử',  desc:'Xóa toàn bộ dữ liệu được tạo trong môi trường dev', danger:true,  fn: () => confirm('Hành động này không thể hoàn tác!') },
]

async function saveGeneral() {
  saving.value = true
  await new Promise(r => setTimeout(r, 400))
  persistSettings()
  saving.value = false
  ui.showSuccess('Đã lưu cài đặt hệ thống!')
}

function formatDate(d) {
  if (!d) return '—'
  const diff = Date.now() - new Date(d).getTime()
  const m = Math.floor(diff / 60000)
  if (m < 1) return 'Vừa xong'
  if (m < 60) return `${m} phút trước`
  const h = Math.floor(m / 60)
  if (h < 24) return `${h} giờ trước`
  return new Date(d).toLocaleDateString('vi-VN')
}
</script>
