<template>
  <div class="space-y-6 max-w-2xl mx-auto">

    <!-- HEADER -->
    <div>
      <button @click="$router.back()" class="flex items-center gap-1 text-slate-400 hover:text-slate-600 text-sm mb-3">
        <span class="material-symbols-outlined text-sm">arrow_back</span>Quay lại
      </button>
      <h1 class="text-2xl font-black text-slate-800">Nộp hồ sơ hỗ trợ</h1>
    </div>

    <!-- PROGRESS STEPS -->
    <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5">
      <div class="flex items-center">
        <div v-for="(step, i) in steps" :key="i" class="flex items-center flex-1">
          <div class="flex flex-col items-center flex-1">
            <div :class="['w-9 h-9 rounded-full flex items-center justify-center font-black text-sm transition-all',
              currentStep > i ? 'bg-primary text-white shadow-md shadow-primary/30'
              : currentStep === i ? 'bg-primary text-white ring-4 ring-primary/20'
              : 'bg-slate-100 text-slate-400']">
              <span v-if="currentStep > i" class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">check</span>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <p :class="['text-[10px] font-bold mt-1.5 text-center leading-tight', currentStep >= i ? 'text-primary' : 'text-slate-400']">{{ step }}</p>
          </div>
          <div v-if="i < steps.length-1" :class="['h-0.5 flex-1 mx-1 -mt-5 rounded-full', currentStep > i ? 'bg-primary' : 'bg-slate-100']"></div>
        </div>
      </div>
    </div>

    <!-- STEP 0: CHON CHUONG TRINH -->
    <div v-if="currentStep === 0" class="space-y-3">
      <h2 class="font-black text-slate-800">Chọn chương trình trợ cấp</h2>

      <!-- Loading -->
      <div v-if="loadingPrograms" class="space-y-3">
        <div v-for="i in 3" :key="i" class="bg-white rounded-2xl border border-slate-200 p-5 animate-pulse">
          <div class="flex items-start gap-4">
            <div class="w-12 h-12 rounded-2xl bg-slate-100"></div>
            <div class="flex-1 space-y-2">
              <div class="w-48 h-4 bg-slate-100 rounded"></div>
              <div class="w-full h-3 bg-slate-50 rounded"></div>
              <div class="w-24 h-3 bg-slate-50 rounded"></div>
            </div>
          </div>
        </div>
      </div>

      <div v-for="p in programs" :key="p.id"
        @click="form.chuong_trinh_id = p.id"
        :class="['bg-white rounded-2xl border-2 p-5 cursor-pointer transition-all',
          form.chuong_trinh_id === p.id ? 'border-primary bg-primary/5' : 'border-slate-200 hover:border-primary/30']">
        <div class="flex items-start gap-4">
          <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0', form.chuong_trinh_id === p.id ? 'bg-primary text-white' : 'bg-slate-100 text-slate-400']">
            <span class="material-symbols-outlined" style="font-variation-settings:'FILL' 1;">volunteer_activism</span>
          </div>
          <div class="flex-1">
            <p class="font-bold text-slate-800">{{ p.ten }}</p>
            <p class="text-sm text-slate-500 mt-1">{{ p.mo_ta }}</p>
            <p class="text-xs text-emerald-600 font-bold mt-2">Hạn nộp: {{ p.han_nop }}</p>
          </div>
        </div>
      </div>

      <p v-if="formErrors.chuong_trinh_id" class="text-xs text-red-500 font-semibold flex items-center gap-1">
        <span class="material-symbols-outlined text-sm">error</span>{{ formErrors.chuong_trinh_id }}
      </p>

      <h2 class="font-black text-slate-800 mt-6 pt-4 border-t border-slate-100">Chọn nhóm đối tượng thụ hưởng</h2>
      <select v-model="form.doi_tuong_id" class="w-full px-4 py-3 bg-white border border-slate-200 rounded-xl text-sm focus:outline-none focus:ring-2 focus:ring-primary/20 cursor-pointer">
        <option :value="null">-- Chọn nhóm đối tượng --</option>
        <option v-for="g in beneficiaryGroups" :key="g.id" :value="g.id">{{ g.tenDoiTuong || g.ten_doi_tuong || g.name }}</option>
      </select>
    </div>

    <!-- STEP 1: THONG TIN CA NHAN -->
    <div v-if="currentStep === 1" class="space-y-4">
      <h2 class="font-black text-slate-800">Thông tin cá nhân</h2>
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-bold text-slate-600 mb-1.5">Họ và tên</label>
            <input v-model="form.ho_ten" readonly
              class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-500 cursor-not-allowed focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-bold text-slate-600 mb-1.5">Số CCCD</label>
            <input v-model="form.so_cccd" readonly
              class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-500 cursor-not-allowed focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-bold text-slate-600 mb-1.5">Ngày sinh</label>
            <input v-model="form.ngay_sinh" type="date" readonly
              class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-500 cursor-not-allowed focus:outline-none" />
          </div>
          <div>
            <label class="block text-sm font-bold text-slate-600 mb-1.5">Số điện thoại</label>
            <input v-model="form.so_dien_thoai" readonly
              class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-500 cursor-not-allowed focus:outline-none" />
          </div>
        </div>
        <div>
          <label class="block text-sm font-bold text-slate-600 mb-1.5">Địa chỉ</label>
          <input v-model="form.dia_chi" readonly
            class="w-full px-4 py-3 bg-slate-100 border border-slate-200 rounded-xl text-sm text-slate-500 cursor-not-allowed focus:outline-none" />
        </div>
        <div>
          <label class="block text-sm font-bold text-slate-600 mb-1.5">Lý do xin trợ cấp</label>
          <textarea v-model="form.ly_do" rows="4" class="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm focus:bg-white focus:outline-none focus:ring-2 focus:ring-primary/20 resize-none"></textarea>
        </div>
      </div>
    </div>

    <!-- STEP 2: TAI LIEU -->
    <div v-if="currentStep === 2" class="space-y-4">
      <h2 class="font-black text-slate-800">Tài liệu đính kèm</h2>
      <div class="bg-amber-50 border border-amber-200 rounded-2xl p-4 text-sm text-amber-800">
        <p class="font-bold mb-2">Cần tải lên:</p>
        <ul class="space-y-1 list-disc list-inside">
          <li>CCCD / Giấy chứng minh <span class="text-red-500 font-bold">*</span></li>
          <li>Sổ hộ khẩu / Giấy tạm trú <span class="text-red-500 font-bold">*</span></li>
          <li>Giấy xác nhận thu nhập <span class="text-red-500 font-bold">*</span></li>
          <li>Giấy tờ khác (nếu có)</li>
        </ul>
      </div>
      <div @click="fileInput?.click()" @dragover.prevent @drop.prevent="onDrop"
        class="bg-white rounded-2xl border-2 border-dashed border-primary/30 p-8 text-center cursor-pointer hover:border-primary hover:bg-primary/5 transition-all">
        <span class="material-symbols-outlined text-4xl text-primary/60 block mb-3">cloud_upload</span>
        <p class="font-bold text-slate-700 mb-1">Bấm để chọn file hoặc kéo thả vào đây</p>
        <p class="text-xs text-slate-400">Hỗ trợ: PDF, JPG, PNG | Tối đa 10MB/file</p>
        <input ref="fileInput" type="file" multiple accept=".pdf,.jpg,.jpeg,.png" class="hidden" @change="onFileChange" />
      </div>

      <!-- File list -->
      <div v-if="uploadedFiles.length" class="space-y-2">
        <div v-for="(f, i) in uploadedFiles" :key="i"
          class="bg-white rounded-xl border border-slate-200 p-3 flex items-center gap-3">
          <!-- File icon -->
          <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0',
            f.type?.includes('pdf') ? 'bg-red-50' : 'bg-blue-50']">
            <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;"
              :class="f.type?.includes('pdf') ? 'text-red-400' : 'text-blue-400'">
              {{ f.type?.includes('pdf') ? 'picture_as_pdf' : 'image' }}
            </span>
          </div>
          <div class="flex-1 min-w-0">
            <p class="text-sm font-semibold text-slate-700 truncate">{{ f.name }}</p>
            <div class="flex items-center gap-2 mt-1">
              <select v-model="f.doc_type" class="px-2 py-1 bg-slate-50 border border-slate-200 rounded-lg text-xs focus:outline-none flex-1">
                <option value="">-- Chọn loại tài liệu --</option>
                <option value="cccd">CCCD / CMT</option>
                <option value="hokh">Sổ hộ khẩu</option>
                <option value="tnthu">Giấy xác nhận thu nhập</option>
                <option value="other">Tài liệu khác</option>
              </select>
              <span class="text-[10px] text-slate-400 flex-shrink-0">{{ formatFileSize(f.size) }}</span>
            </div>
            <!-- Error: file too large -->
            <p v-if="f.size > MAX_FILE_SIZE" class="text-[11px] text-red-500 mt-1 font-semibold">⚠ File quá 10MB</p>
          </div>
          <button @click="uploadedFiles.splice(i,1)" class="p-1.5 hover:bg-red-50 rounded-lg text-slate-400 hover:text-red-500">
            <span class="material-symbols-outlined text-sm">delete</span>
          </button>
        </div>
      </div>

      <p v-if="formErrors.tai_lieu" class="text-xs text-red-500 font-semibold flex items-center gap-1">
        <span class="material-symbols-outlined text-sm">error</span>{{ formErrors.tai_lieu }}
      </p>
    </div>

    <!-- STEP 3: XAC NHAN -->
    <div v-if="currentStep === 3" class="space-y-4">
      <h2 class="font-black text-slate-800">Xác nhận và nộp hồ sơ</h2>
      <div class="bg-white rounded-2xl border border-slate-200/80 shadow-sm p-6 space-y-3">
        <div class="flex justify-between py-2 border-b border-slate-50 text-sm"><span class="text-slate-400">Chương trình</span><span class="font-bold text-slate-700">{{ selectedProgram?.ten }}</span></div>
        <div class="flex justify-between py-2 border-b border-slate-50 text-sm"><span class="text-slate-400">Họ và tên</span><span class="font-bold text-slate-700">{{ form.ho_ten }}</span></div>
        <div class="flex justify-between py-2 border-b border-slate-50 text-sm"><span class="text-slate-400">Số CCCD</span><span class="font-bold text-slate-700">{{ form.so_cccd }}</span></div>
        <div class="flex justify-between py-2 border-b border-slate-50 text-sm"><span class="text-slate-400">Số điện thoại</span><span class="font-bold text-slate-700">{{ form.so_dien_thoai }}</span></div>
        <div class="flex justify-between py-2 border-b border-slate-50 text-sm"><span class="text-slate-400">Địa chỉ</span><span class="font-bold text-slate-700">{{ form.dia_chi }}</span></div>
        <div class="flex justify-between py-2 text-sm"><span class="text-slate-400">Tài liệu</span><span class="font-bold text-emerald-600">{{ uploadedFiles.length }} file</span></div>
      </div>
      <label class="flex items-start gap-3 bg-white rounded-2xl border border-slate-200 p-4 cursor-pointer">
        <input type="checkbox" v-model="form.dong_y" class="mt-0.5 accent-primary" />
        <span class="text-sm text-slate-600">Tôi cam kết các thông tin khai báo là chính xác và chịu trách nhiệm trước pháp luật.</span>
      </label>
    </div>

    <!-- SUCCESS -->
    <div v-if="submitted" class="bg-white rounded-3xl border border-slate-200/80 shadow-sm p-10 text-center">
      <span class="material-symbols-outlined text-6xl text-emerald-500 block mb-4" style="font-variation-settings:'FILL' 1;">check_circle</span>
      <h2 class="text-2xl font-black text-slate-800 mb-2">Nộp hồ sơ thành công!</h2>
      <p class="text-xl font-black text-primary mb-2">#HS-{{ newAppId }}</p>
      <p class="text-sm text-slate-400 mb-6">Chúng tôi sẽ xem xét và thông báo qua SMS khi có kết quả.</p>
      <div class="flex gap-3 justify-center">
        <router-link to="/portal/ho-so-cua-toi" class="px-5 py-2.5 bg-primary text-white text-sm font-bold rounded-xl">Theo dõi hồ sơ</router-link>
        <router-link to="/portal" class="px-5 py-2.5 border border-slate-200 text-slate-600 text-sm rounded-xl">Trang chủ</router-link>
      </div>
    </div>

    <!-- BUTTONS -->
    <div v-if="!submitted" class="flex gap-3">
      <button v-if="currentStep > 0" @click="currentStep--" class="flex items-center gap-2 px-5 py-3 border border-slate-200 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-50">
        <span class="material-symbols-outlined text-sm">arrow_back</span>Quay lại
      </button>
      <button @click="next" :disabled="submitting"
        :class="['flex items-center gap-2 px-6 py-3 text-sm font-bold rounded-xl flex-1 sm:flex-none sm:ml-auto justify-center transition-all',
          submitting ? 'bg-slate-200 text-slate-400' : 'bg-primary text-white hover:opacity-90 shadow-md shadow-primary/20']">
        <span v-if="submitting" class="material-symbols-outlined text-sm animate-spin">progress_activity</span>
        {{ currentStep < 3 ? 'Tiếp theo' : (submitting ? 'Đang nộp...' : 'Nộp hồ sơ') }}
        <span v-if="!submitting" class="material-symbols-outlined text-sm">{{ currentStep < 3 ? 'arrow_forward' : 'send' }}</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUI } from '../../stores/ui'
import { authStore } from '../../stores/auth'
import { programsApi } from '../../api/programs'
import { applicationsApi } from '../../api/applications'
import { useFormValidation, validators } from '../../composables/useFormValidation'
import http from '../../api/http'

const route = useRoute()
const ui = useUI()
const currentStep = ref(0)
const submitted   = ref(false)
const submitting  = ref(false)
const newAppId    = ref(null)
const fileInput   = ref(null)
const loadingPrograms = ref(false)

const MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB

const user = authStore.user || {}

// Ensure date is in YYYY-MM-DD format for <input type="date">
let sNgaySinh = user.ngay_sinh || user.ngaySinh || ''
if (sNgaySinh && sNgaySinh.includes('/')) {
  // if it's DD/MM/YYYY, convert to YYYY-MM-DD
  const parts = sNgaySinh.split('/')
  if (parts.length === 3) sNgaySinh = `${parts[2]}-${parts[1]}-${parts[0]}`
}

const form = ref({
  chuong_trinh_id: null,
  doi_tuong_id: null,
  ho_ten: user.fullName || user.ho_va_ten || user.tenDayDu || '',
  so_cccd: user.cccd || user.so_cccd || user.soCccd || '',
  ngay_sinh: sNgaySinh,
  so_dien_thoai: user.phone || user.so_dien_thoai || user.soDienThoai || '',
  dia_chi: user.address || user.dia_chi || user.diaChi || '',
  ly_do: '',
  dong_y: false,
})

const uploadedFiles = ref([])
const beneficiaryGroups = ref([])

onMounted(async () => {
  if (route.query.program) {
    form.value.chuong_trinh_id = route.query.program  // ObjectId string, không dùng Number()
  }
  await loadPrograms()
  try {
    const r = await http.get('/beneficiary-groups', { params: { size: 1000 } }).catch(() => ({ data: [] }))
    const list = r.data?.content || r.data || []
    if (list.length > 0) {
      beneficiaryGroups.value = list
    }
  } catch(e) {
    
  }
})

// Validation rules
const rules = {
  ho_ten:        [validators.required('Vui lòng nhập họ và tên')],
  so_cccd:       [validators.required('Vui lòng nhập số CCCD'), validators.cccd()],
  so_dien_thoai: [validators.required('Vui lòng nhập số điện thoại'), validators.phone()],
  dia_chi:       [validators.required('Vui lòng nhập địa chỉ')],
}

const { errors: formErrors, validate, validateField: vField } = useFormValidation(rules)

// Load programs từ API
const programs = ref([])

async function loadPrograms() {
  loadingPrograms.value = true
  try {
    const res = await http.get('/programs', { params: { trangThai: 'ACTIVE', size: 100 } })
    const list = res.data?.content || res.data || []
    programs.value = list.map(p => ({
      id: p.id,
      ten: p.tenChuongTrinh || p.ten_chuong_trinh || p.ten,
      han_nop: (p.ngayKetThuc || p.ngay_ket_thuc) ? new Date(p.ngayKetThuc || p.ngay_ket_thuc).toLocaleDateString('vi-VN') : '—',
      mo_ta: p.moTa || p.mo_ta || '',
    }))
  } catch (err) {
    console.error(err)
    ui.showError('Không thể tải danh sách chương trình. Vui lòng thử lại.')
  } finally {
    loadingPrograms.value = false
  }
}


const selectedProgram = computed(() => programs.value.find(p => p.id === form.value.chuong_trinh_id))

// File handlers
function onFileChange(e) {
  addFiles(e.target.files)
  e.target.value = '' // reset để có thể chọn lại cùng file
}
function onDrop(e) { addFiles(e.dataTransfer?.files) }

function addFiles(fileList) {
  if (!fileList) return
  const rejected = []
  Array.from(fileList).forEach(f => {
    if (f.size > MAX_FILE_SIZE) {
      rejected.push(f.name)
    }
    // Vẫn thêm vào list để user thấy lỗi, nhưng sẽ chặn ở submit
    uploadedFiles.value.push({ name: f.name, type: f.type, size: f.size, doc_type: '', file: f })
  })
  if (rejected.length) {
    ui.showWarning(`${rejected.length} file vượt quá 10MB`)
  }
}

function formatFileSize(bytes) {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(0) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

// Step validation
function validateStep() {
  if (currentStep.value === 0) {
    if (!form.value.chuong_trinh_id || !form.value.doi_tuong_id) {
      formErrors.value = { chuong_trinh_id: 'Vui lòng chọn cả chương trình và nhóm đối tượng' }
      return false
    }
    return true
  }
  if (currentStep.value === 1) {
    return validate(form.value)
  }
  if (currentStep.value === 2) {
    // Kiểm tra có file quá lớn không
    if (uploadedFiles.value.some(f => f.size > MAX_FILE_SIZE)) {
      formErrors.value = { tai_lieu: 'Vui lòng xoá file vượt quá 10MB' }
      return false
    }
    return true
  }
  if (currentStep.value === 3) {
    if (!form.value.dong_y) {
      ui.showWarning('Vui lòng đánh dấu cam kết trước khi nộp')
      return false
    }
    return true
  }
  return true
}

async function next() {
  formErrors.value = {}
  if (!validateStep()) return

  if (currentStep.value < 3) {
    currentStep.value++
    return
  }

  // Submit
  submitting.value = true
  try {
    const payloadJson = {
      chuongTrinhId: form.value.chuong_trinh_id,
      doiTuongId: form.value.doi_tuong_id,
      moTa: form.value.ly_do || ''
    }

    // Bước 1: Tạo hồ sơ (trạng thái DRAFT)
    const res = await applicationsApi.create(payloadJson)
    const appId = res.data?.id || res.data

    if (!appId) throw new Error('Không lấy được ID hồ sơ vừa tạo')

    // Bước 2: Upload tài liệu đính kèm (nếu có)
    const validFiles = uploadedFiles.value.filter(f => f.file && f.size <= MAX_FILE_SIZE)
    if (validFiles.length > 0) {
      const uploadResults = await Promise.allSettled(
        validFiles.map(f => {
          const formData = new FormData()
          formData.append('file', f.file)
          return applicationsApi.uploadDocument(appId, formData)
        })
      )
      const failed = uploadResults.filter(r => r.status === 'rejected').length
      if (failed > 0) {
        ui.showWarning(`${failed}/${validFiles.length} tài liệu upload thất bại — hồ sơ vẫn được ghi nhận.`)
      }
    }

    // Bước 3: Submit hồ sơ (DRAFT → SUBMITTED)
    await applicationsApi.submit(appId)

    newAppId.value = appId
    submitted.value = true
    ui.showSuccess('Nộp hồ sơ thành công! Hồ sơ đang chờ xét duyệt.')
  } catch (err) {
    const msg = err?.response?.data?.message || 'Không thể nộp hồ sơ. Vui lòng thử lại.'
    ui.showError(msg)
  } finally {
    submitting.value = false
  }
}
</script>
