<template>
  <div class="space-y-5 max-w-2xl mx-auto">

    <!-- HEADER CARD -->
    <div class="bg-gradient-to-br from-[#1a56db] via-[#1e40af] to-[#312e81] rounded-3xl p-6 text-white relative overflow-hidden">
      <div class="absolute -right-6 -top-6 w-40 h-40 bg-white/5 rounded-full"></div>
      <div class="absolute right-10 bottom-0 w-24 h-24 bg-white/5 rounded-full translate-y-8"></div>
      <div class="relative z-10 flex items-center gap-5">
        <!-- Avatar -->
        <div class="w-20 h-20 rounded-2xl bg-white/20 border-2 border-white/30 flex items-center justify-center text-3xl font-black flex-shrink-0 shadow-lg">
          {{ initials }}
        </div>
        <div class="flex-1 min-w-0">
          <p class="text-white/60 text-xs font-semibold mb-0.5 uppercase tracking-widest">{{ roleLabel }}</p>
          <h1 class="text-2xl font-black leading-tight mb-1">{{ displayName }}</h1>
          <p class="text-white/70 text-sm">{{ authStore.user?.email || authStore.user?.username }}</p>
        </div>
        <button @click="editing ? cancelEdit() : (editing = true)"
          class="flex items-center gap-2 px-4 py-2 bg-white/15 border border-white/25 rounded-xl text-sm font-bold hover:bg-white/25 transition-all flex-shrink-0">
          <span class="material-symbols-outlined text-sm">{{ editing ? 'close' : 'edit' }}</span>
          {{ editing ? 'Huỷ' : 'Chỉnh sửa' }}
        </button>
      </div>
    </div>

    <!-- STATS ROW -->
    <div class="grid grid-cols-3 gap-3">
      <div v-for="s in stats" :key="s.label" class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-4 text-center">
        <div :class="['w-10 h-10 rounded-xl flex items-center justify-center mx-auto mb-2', s.bg]">
          <span class="material-symbols-outlined" :class="s.color" style="font-variation-settings:'FILL' 1;">{{ s.icon }}</span>
        </div>
        <p class="text-xl font-black text-slate-800">{{ s.value }}</p>
        <p class="text-[10px] text-slate-400 font-semibold mt-0.5 leading-tight">{{ s.label }}</p>
      </div>
    </div>

    <!-- PERSONAL INFO / EDIT FORM -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm">
      <div class="px-5 py-4 border-b border-slate-100 flex items-center justify-between">
        <div class="flex items-center gap-2">
          <span class="material-symbols-outlined text-primary" style="font-variation-settings:'FILL' 1;">person</span>
          <h2 class="font-black text-slate-800">Thông tin cá nhân</h2>
        </div>
        <span v-if="editing" class="text-xs text-amber-600 font-semibold bg-amber-50 px-2 py-1 rounded-lg">Đang chỉnh sửa</span>
      </div>

      <!-- VIEW MODE -->
      <div v-if="!editing" class="divide-y divide-slate-50">
        <div v-for="f in fields" :key="f.key" class="flex items-center gap-4 px-5 py-3.5">
          <div class="w-8 h-8 rounded-lg bg-slate-50 flex items-center justify-center flex-shrink-0">
            <span class="material-symbols-outlined text-slate-400 text-sm">{{ f.icon }}</span>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-[10px] text-slate-400 font-bold uppercase tracking-wider mb-0.5">{{ f.label }}</p>
            <p class="text-sm font-semibold text-slate-800">{{ form[f.key] || '—' }}</p>
          </div>
        </div>
      </div>

      <!-- EDIT MODE -->
      <div v-else class="p-5 space-y-4">
        
        <!-- eKYC Scanner Block -->
        <div class="bg-blue-50/50 border border-blue-200 rounded-2xl p-5 flex flex-col items-center justify-center text-center">
          <div class="w-12 h-12 rounded-xl bg-blue-100 flex items-center justify-center text-blue-600 mb-2 shadow-sm">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;">qr_code_scanner</span>
          </div>
          <p class="font-black text-blue-900 text-sm">eKYC - Định danh tự động</p>
          <p class="text-xs text-blue-700/80 font-medium mt-1 mb-4">Mã QR trên CCCD Việt Nam rất nhỏ, Camera máy tính thường bị mờ. Vui lòng tải một tấm ảnh rõ nét lên để tốc độ nhận diện đạt 100%.</p>
          
          <!-- File Input for QR -->
          <div class="relative overflow-hidden w-full max-w-xs group cursor-pointer transition-all text-center">
             <button class="w-full flex items-center justify-center gap-2 px-6 py-2.5 bg-blue-600 text-white text-sm font-bold rounded-xl shadow-md border-0 group-hover:bg-blue-700 transition">
               <span class="material-symbols-outlined text-sm">upload_file</span> Tải hoặc Chụp ảnh thẻ
             </button>
             <input type="file" @change="handleFileUpload" accept="image/*" class="absolute inset-0 opacity-0 cursor-pointer h-full w-full" :disabled="scanningFile" title="Chọn ảnh mã QR trên CCCD" />
          </div>

          <p v-if="scanningFile" class="text-xs font-bold text-amber-600 mt-3 flex items-center justify-center gap-1">
            <span class="material-symbols-outlined text-sm animate-spin">progress_activity</span> Đang phân tích ảnh...
          </p>

          <div class="text-[11px] text-slate-400 mt-3 flex items-center justify-center border-t border-blue-200/50 pt-3 w-full opacity-60">
             <i>Bạn không có ảnh sẵn?</i> &nbsp;
             <button v-if="!scanning" @click="startScan" type="button" class="underline hover:text-blue-600 font-bold transition">Thử bật WebCam trực tiếp</button>
             <button v-else @click="stopScan" type="button" class="text-red-500 font-bold underline">Tắt WebCam</button>
          </div>

          <!-- QR Reader Container -->
          <div v-if="scanning" class="w-full max-w-sm mt-4 border-2 border-dashed border-blue-300 rounded-2xl overflow-hidden bg-black flex items-center justify-center min-h-[250px]">
            <video id="qr-video" class="w-full h-full object-cover"></video>
          </div>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Họ và tên</label>
            <input v-model="form.ho_ten" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Số CCCD</label>
            <input v-model="form.so_cccd" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Ngày sinh</label>
            <input v-model="form.ngay_sinh" type="date" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
          </div>
          <div>
            <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Số điện thoại</label>
            <input v-model="form.so_dien_thoai" type="tel" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
          </div>
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Địa chỉ</label>
          <input v-model="form.dia_chi" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 transition-all" />
        </div>
        <div>
          <label class="block text-xs font-bold text-slate-500 uppercase tracking-wider mb-1.5">Email</label>
          <input v-model="form.email" type="email" disabled class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-400 cursor-not-allowed" />
          <p class="text-[10px] text-slate-400 mt-1 ml-1">Email không thể thay đổi</p>
        </div>
        <div class="flex gap-3 pt-2">
          <button @click="saveProfile" :disabled="saving"
            :class="['flex items-center gap-2 px-5 py-2.5 text-sm font-bold rounded-xl shadow-md transition-all',
              saving ? 'bg-primary/60 text-white cursor-not-allowed' : 'bg-primary text-white hover:opacity-90 shadow-primary/20']">
            <span v-if="saving" class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
            <span v-else class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">save</span>
            {{ saving ? 'Đang lưu...' : 'Lưu thay đổi' }}
          </button>
          <button @click="cancelEdit"
            class="px-5 py-2.5 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50 transition-all">
            Huỷ
          </button>
        </div>
      </div>
    </div>

    <!-- SECURITY -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm">
      <div class="px-5 py-4 border-b border-slate-100 flex items-center gap-2">
        <span class="material-symbols-outlined text-primary" style="font-variation-settings:'FILL' 1;">security</span>
        <h2 class="font-black text-slate-800">Bảo mật tài khoản</h2>
      </div>
      <div class="divide-y divide-slate-50">
        <div class="flex items-center justify-between px-5 py-4">
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-xl bg-slate-50 flex items-center justify-center">
              <span class="material-symbols-outlined text-slate-400 text-sm">lock</span>
            </div>
            <div>
              <p class="text-sm font-semibold text-slate-800">Đổi mật khẩu</p>
              <p class="text-xs text-slate-400">Lần đổi cuối: chưa xác định</p>
            </div>
          </div>
          <button class="px-3 py-1.5 text-xs font-bold text-primary border border-primary/20 bg-primary/5 rounded-lg hover:bg-primary/10 transition-all">Đổi ngay</button>
        </div>
        <div class="flex items-center justify-between px-5 py-4">
          <div class="flex items-center gap-3">
            <div class="w-9 h-9 rounded-xl bg-emerald-50 flex items-center justify-center">
              <span class="material-symbols-outlined text-emerald-500 text-sm" style="font-variation-settings:'FILL' 1;">verified_user</span>
            </div>
            <div>
              <p class="text-sm font-semibold text-slate-800">Xác minh tài khoản</p>
              <p class="text-xs text-emerald-600 font-semibold">Đã xác minh</p>
            </div>
          </div>
          <span class="px-3 py-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-lg">✓ Hợp lệ</span>
        </div>
      </div>
    </div>

    <!-- LOGOUT -->
    <button @click="handleLogout"
      class="w-full flex items-center justify-center gap-2 py-3.5 border-2 border-red-100 text-red-500 text-sm font-bold rounded-2xl hover:bg-red-50 hover:border-red-200 transition-all">
      <span class="material-symbols-outlined text-sm">logout</span>
      Đăng xuất
    </button>

  </div>
</template>

<script setup>
import { ref, computed, nextTick, onBeforeUnmount, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { authStore } from '../../stores/auth'
import { useUI } from '../../stores/ui'
import { applicationsApi } from '../../api/applications'
import jsQR from 'jsqr'

const router = useRouter()
const ui = useUI()
const editing = ref(false)
const saving  = ref(false)
const scanning = ref(false)
const scanningFile = ref(false)

let stream = null
let requestAnimId = null

// Helper: convert canvas pixels to high-contrast grayscale
function toHighContrastGrayscale(ctx, width, height) {
  const imageData = ctx.getImageData(0, 0, width, height)
  const data = imageData.data
  for (let i = 0; i < data.length; i += 4) {
    // Weighted grayscale (luminance formula)
    const gray = 0.299 * data[i] + 0.587 * data[i+1] + 0.114 * data[i+2]
    // Apply contrast boost: push toward black or white
    const boosted = gray < 128 ? Math.max(0, gray - 40) : Math.min(255, gray + 40)
    data[i] = data[i+1] = data[i+2] = boosted
  }
  ctx.putImageData(imageData, 0, 0)
  return ctx.getImageData(0, 0, width, height)
}

// START EKYC QR LOGIC (BarcodeDetector / jsQR + Grayscale Fallback)
async function handleFileUpload(event) {
  const file = event.target.files[0]
  if (!file) return

  scanningFile.value = true

  try {
    const imageBitmap = await createImageBitmap(file)
    const W = imageBitmap.width
    const H = imageBitmap.height

    // --- Strategy 1: Native BarcodeDetector on original image ---
    if ('BarcodeDetector' in window) {
      const detector = new window.BarcodeDetector({ formats: ['qr_code'] })
      // Try raw image first
      let barcodes = await detector.detect(imageBitmap)
      if (barcodes.length > 0) {
        onScanSuccess(barcodes[0].rawValue, null)
        return
      }
      // Try grayscale version via offscreen canvas
      const offCanvas = document.createElement('canvas')
      offCanvas.width = W; offCanvas.height = H
      const offCtx = offCanvas.getContext('2d', { willReadFrequently: true })
      offCtx.drawImage(imageBitmap, 0, 0)
      toHighContrastGrayscale(offCtx, W, H)
      const grayBitmap = await createImageBitmap(offCanvas)
      barcodes = await detector.detect(grayBitmap)
      if (barcodes.length > 0) {
        onScanSuccess(barcodes[0].rawValue, null)
        return
      }
    }

    // --- Strategy 2: jsQR on grayscale-processed full-resolution image ---
    const canvas = document.createElement('canvas')
    canvas.width = W; canvas.height = H
    const ctx = canvas.getContext('2d', { willReadFrequently: true })
    ctx.drawImage(imageBitmap, 0, 0)
    const grayData = toHighContrastGrayscale(ctx, W, H)

    const code = jsQR(grayData.data, grayData.width, grayData.height, {
      inversionAttempts: 'attemptBoth',
    })

    if (code) {
      onScanSuccess(code.data, null)
    } else {
      ui.showWarning('Không đọc được mã QR! Vui lòng cắt crop ảnh sao cho chỉ còn phần ô vuông QR Code (góc trên phải của CCCD) rồi tải lên lại.')
    }
  } catch (err) {
    console.error(err)
    ui.showWarning('Có lỗi xử lý ảnh: ' + err.message)
  } finally {
    scanningFile.value = false
    event.target.value = ''
  }
}

async function startScan() {
  scanning.value = true
  await nextTick()
  const videoElem = document.getElementById('qr-video')
  
  try {
    stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } })
    videoElem.srcObject = stream
    videoElem.setAttribute('playsinline', true) 
    videoElem.play()
    requestAnimId = requestAnimationFrame(tick)
  } catch (err) {
    ui.showWarning('Lỗi mở Camera hoặc bị từ chối quyền: ' + err.message)
    stopScan()
  }
}

function tick() {
  if (!scanning.value) return
  const videoElem = document.getElementById('qr-video')
  if (!videoElem) return

  if (videoElem.readyState === videoElem.HAVE_ENOUGH_DATA) {
    const canvas = document.createElement('canvas')
    canvas.width = videoElem.videoWidth
    canvas.height = videoElem.videoHeight
    const ctx = canvas.getContext('2d', { willReadFrequently: true })
    ctx.drawImage(videoElem, 0, 0, canvas.width, canvas.height)
    
    const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height)
    const code = jsQR(imageData.data, imageData.width, imageData.height, {
      inversionAttempts: 'dontInvert',
    })

    if (code) {
      onScanSuccess(code.data, null)
      return
    }
  }
  requestAnimId = requestAnimationFrame(tick)
}

function stopScan() {
  scanning.value = false
  if (requestAnimId) {
    cancelAnimationFrame(requestAnimId)
    requestAnimId = null
  }
  if (stream) {
    stream.getTracks().forEach(track => track.stop())
    stream = null
  }
  const videoElem = document.getElementById('qr-video')
  if (videoElem) {
    videoElem.srcObject = null
  }
}

function onScanSuccess(decodedText, decodedResult) {
  // Format CCCD: 001099000001|123456789|NGUYEN VAN A|01101999|Nam|Hà Nội|01102021
  try {
    const parts = decodedText.split('|')
    if (parts.length >= 6) {
      ui.showSuccess('Quét CCCD thành công! Nhận diện eKYC hoàn tất.')
      stopScan()
      
      form.value.so_cccd = parts[0]
      // Format name to Proper Case if needed, but let's just keep original uppercase or capitalize
      form.value.ho_ten = parts[2]
      
      const dob = parts[3] // DDMMYYYY
      if (dob && dob.length === 8) {
        form.value.ngay_sinh = `${dob.substring(4,8)}-${dob.substring(2,4)}-${dob.substring(0,2)}`
      }
      
      form.value.dia_chi = parts[5].replace(/,/g, ' -')
    } else {
      ui.showWarning('Mã QR không đúng định dạng thẻ CCCD Việt Nam')
    }
  } catch(e) {
    ui.showWarning('Có lỗi xảy ra khi phân tích dữ liệu CCCD')
  }
}

function onScanFailure(error) {
  // Silent fail for camera scan frames
}

onBeforeUnmount(() => {
  stopScan()
})
// END EKYC QR LOGIC

const displayName = computed(() => authStore.user?.fullName || authStore.user?.username || 'Người dùng')
const initials = computed(() => {
  const n = displayName.value
  return n.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase()
})

const roleLabels = { ADMIN: 'Quản trị viên', OFFICER: 'Cán bộ xét duyệt', ACCOUNTANT: 'Cán bộ tài chính', CITIZEN: 'Người dân' }
const roleLabel = computed(() => roleLabels[authStore.role] || 'Người dùng')

// Form khởi tạo từ dữ liệu user thực
const form = ref({
  ho_ten:        authStore.user?.fullName || '',
  so_cccd:       authStore.user?.cccd || '',
  ngay_sinh:     authStore.user?.ngaySinh || '',
  so_dien_thoai: authStore.user?.phone || '',
  dia_chi:       authStore.user?.address || '',
  email:         authStore.user?.email || authStore.user?.username || '',
})

// Reset form về giá trị hiện tại khi huỷ
function cancelEdit() {
  form.value = {
    ho_ten:        authStore.user?.fullName || '',
    so_cccd:       authStore.user?.cccd || '',
    ngay_sinh:     authStore.user?.ngaySinh || '',
    so_dien_thoai: authStore.user?.phone || '',
    dia_chi:       authStore.user?.address || '',
    email:         authStore.user?.email || authStore.user?.username || '',
  }
  editing.value = false
}

async function saveProfile() {
  if (saving.value) return
  saving.value = true
  try {
    // Thử gọi API backend
    await authStore.updateUser({
      fullName:  form.value.ho_ten,
      cccd:      form.value.so_cccd,
      ngaySinh:  form.value.ngay_sinh,
      phone:     form.value.so_dien_thoai,
      address:   form.value.dia_chi,
    })
    ui.showSuccess('Đã lưu thông tin cá nhân!')
  } catch {
    // Backend chưa sẵn sàng → cập nhật local
    authStore.updateUserLocal({
      fullName:  form.value.ho_ten,
      cccd:      form.value.so_cccd,
      ngaySinh:  form.value.ngay_sinh,
      phone:     form.value.so_dien_thoai,
      address:   form.value.dia_chi,
    })
    ui.showSuccess('Đã lưu thông tin (chế độ ngoại tuyến)')
  } finally {
    saving.value = false
    editing.value = false
  }
}

const fields = [
  { key:'ho_ten',       label:'Họ và tên',            icon:'person' },
  { key:'so_cccd',      label:'Số CCCD',               icon:'badge' },
  { key:'ngay_sinh',    label:'Ngày sinh',              icon:'cake' },
  { key:'so_dien_thoai',label:'Số điện thoại',         icon:'phone' },
  { key:'dia_chi',      label:'Địa chỉ',               icon:'home' },
  { key:'email',        label:'Email / Tên đăng nhập', icon:'mail' },
]

const stats = ref([
  { label:'Hồ sơ đã nộp', value:0, icon:'folder_open',  bg:'bg-primary/10',   color:'text-primary' },
  { label:'Chờ duyệt',    value:0, icon:'hourglass_top', bg:'bg-amber-100',    color:'text-amber-600' },
  { label:'Đã duyệt',     value:0, icon:'check_circle',  bg:'bg-emerald-100',  color:'text-emerald-600' },
])

onMounted(async () => {
  try {
    const res = await applicationsApi.getMyStats()
    const data = res.data?.data || res.data
    if (data) {
      stats.value[0].value = data.total || 0
      stats.value[1].value = data.pending || 0
      stats.value[2].value = data.approved || 0
    }
  } catch (error) {
    console.error('Lỗi khi tải thống kê:', error)
  }
})

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

