<template>
  <div class="auth-root">
    <!-- ===== LEFT PANEL – Branding ===== -->
    <div class="auth-left">
      <div class="auth-left-inner">
        <div class="auth-brand">
          <div class="auth-logo">
            <span class="material-symbols-outlined">volunteer_activism</span>
          </div>
          <div>
            <h1 class="auth-brand-title">Hệ thống Trợ cấp</h1>
            <p class="auth-brand-sub">Quản lý Hỗ trợ Xã hội</p>
          </div>
        </div>

        <div class="auth-hero">
          <h2 class="auth-hero-title">Nền tảng quản lý trợ cấp xã hội toàn diện</h2>
          <p class="auth-hero-desc">Kết nối dữ liệu, minh bạch ngân sách, hỗ trợ đúng người — đúng lúc.</p>
        </div>

        <div class="auth-stats">
          <div class="auth-stat">
            <span class="auth-stat-value">24K+</span>
            <span class="auth-stat-label">Đối tượng</span>
          </div>
          <div class="auth-stat-divider"></div>
          <div class="auth-stat">
            <span class="auth-stat-value">4.2 Tỷ</span>
            <span class="auth-stat-label">Đã giải ngân</span>
          </div>
          <div class="auth-stat-divider"></div>
          <div class="auth-stat">
            <span class="auth-stat-value">88%</span>
            <span class="auth-stat-label">Phê duyệt</span>
          </div>
        </div>
      </div>

      <!-- Decorative circles -->
      <div class="auth-deco auth-deco-1"></div>
      <div class="auth-deco auth-deco-2"></div>
      <div class="auth-deco auth-deco-3"></div>
    </div>

    <!-- ===== RIGHT PANEL – Form ===== -->
    <div class="auth-right">
      <div class="auth-form-wrap">
        <!-- Tab switcher -->
        <div class="auth-tabs">
          <button
            @click="switchMode('login')"
            :class="['auth-tab', mode === 'login' ? 'auth-tab--active' : '']"
          >
            <span class="material-symbols-outlined">login</span>
            Đăng nhập
          </button>
          <button
            @click="switchMode('register')"
            :class="['auth-tab', mode === 'register' ? 'auth-tab--active' : '']"
          >
            <span class="material-symbols-outlined">person_add</span>
            Đăng ký
          </button>
        </div>

        <!-- Form title -->
        <div class="auth-form-header">
          <h2 class="auth-form-title">
            {{ mode === 'login' ? 'Chào mừng trở lại' : 'Tạo tài khoản mới' }}
          </h2>
          <p class="auth-form-desc">
            {{ mode === 'login'
              ? 'Đăng nhập để tiếp tục quản lý hệ thống'
              : 'Điền thông tin để tạo tài khoản của bạn' }}
          </p>
        </div>

        <!-- ===== LOGIN FORM ===== -->
        <form v-if="mode === 'login'" @submit.prevent="handleLogin" class="auth-form">
          <div class="form-group">
            <label class="form-label">Tên đăng nhập</label>
            <div class="form-input-wrap">
              <span class="material-symbols-outlined form-icon">person</span>
              <input v-model="loginForm.username" type="text" placeholder="Nhập tên đăng nhập" required class="form-input" />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Mật khẩu</label>
            <div class="form-input-wrap">
              <span class="material-symbols-outlined form-icon">lock</span>
              <input v-model="loginForm.password" :type="showPass ? 'text' : 'password'" placeholder="Nhập mật khẩu" required class="form-input" />
              <button type="button" @click="showPass = !showPass" class="form-eye">
                <span class="material-symbols-outlined">{{ showPass ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
          </div>

          <div v-if="error" class="form-error">
            <span class="material-symbols-outlined">error</span>
            {{ error }}
          </div>

          <button type="submit" :disabled="loading" class="btn-primary">
            <span v-if="loading" class="material-symbols-outlined btn-spin">progress_activity</span>
            <span>{{ loading ? 'Đang đăng nhập...' : 'Đăng nhập' }}</span>
            <span v-if="!loading" class="material-symbols-outlined">arrow_forward</span>
          </button>
        </form>

        <!-- ===== REGISTER FORM ===== -->
        <form v-else @submit.prevent="handleRegister" class="auth-form">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">Tên đăng nhập *</label>
              <div class="form-input-wrap">
                <span class="material-symbols-outlined form-icon">person</span>
                <input v-model="regForm.username" type="text" placeholder="username" required class="form-input" />
              </div>
            </div>
            <div class="form-group">
              <label class="form-label">Họ và tên *</label>
              <div class="form-input-wrap">
                <span class="material-symbols-outlined form-icon">badge</span>
                <input v-model="regForm.fullName" type="text" placeholder="Nguyễn Văn A" required class="form-input" />
              </div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Email *</label>
            <div class="form-input-wrap">
              <span class="material-symbols-outlined form-icon">mail</span>
              <input v-model="regForm.email" type="email" placeholder="email@example.com" required class="form-input" />
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Mật khẩu *</label>
            <div class="form-input-wrap">
              <span class="material-symbols-outlined form-icon">lock</span>
              <input v-model="regForm.password" :type="showPass ? 'text' : 'password'" placeholder="Tối thiểu 6 ký tự" required class="form-input" />
              <button type="button" @click="showPass = !showPass" class="form-eye">
                <span class="material-symbols-outlined">{{ showPass ? 'visibility_off' : 'visibility' }}</span>
              </button>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Vai trò</label>
            <div class="form-input-wrap">
              <span class="material-symbols-outlined form-icon">manage_accounts</span>
              <input type="text" value="👁️ Người xem (Viewer)" disabled class="form-input bg-surface-container/50 cursor-not-allowed" />
            </div>
            <p class="text-[11px] text-on-surface-variant mt-1">Tài khoản mới có quyền Người xem. Liên hệ quản trị viên để nâng cấp.</p>
          </div>

          <div v-if="error" class="form-error">
            <span class="material-symbols-outlined">error</span>
            {{ error }}
          </div>
          <div v-if="successMsg" class="form-success">
            <span class="material-symbols-outlined">check_circle</span>
            {{ successMsg }}
          </div>

          <button type="submit" :disabled="loading" class="btn-primary">
            <span v-if="loading" class="material-symbols-outlined btn-spin">progress_activity</span>
            <span>{{ loading ? 'Đang tạo tài khoản...' : 'Tạo tài khoản' }}</span>
            <span v-if="!loading" class="material-symbols-outlined">person_add</span>
          </button>
        </form>

        <p class="auth-footer">© {{ new Date().getFullYear() }} Hệ thống Quản lý Trợ cấp Xã hội</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { authStore } from '../stores/auth'
import { authApi } from '../api/auth'

const router = useRouter()
const mode = ref('login')
const showPass = ref(false)
const loading = ref(false)
const error = ref('')
const successMsg = ref('')

const loginForm = ref({ username: '', password: '' })
const regForm = ref({ username: '', fullName: '', email: '', password: '', role: 'CITIZEN' })

function switchMode(m) {
  mode.value = m
  error.value = ''
  successMsg.value = ''
  showPass.value = false
}

watch(mode, () => { error.value = ''; successMsg.value = '' })

async function handleLogin() {
  loading.value = true
  error.value = ''
  try {
    await authStore.login(loginForm.value.username, loginForm.value.password)
    // CITIZEN → portal, còn lại → dashboard
    if (authStore.role === 'CITIZEN') {
      router.push('/portal')
    } else {
      router.push('/')
    }
  } catch (e) {
    error.value = e.response?.data?.message || 'Tên đăng nhập hoặc mật khẩu không đúng.'
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  loading.value = true
  error.value = ''
  successMsg.value = ''
  try {
    await authApi.register(regForm.value)
    successMsg.value = `Tài khoản "${regForm.value.username}" đã được tạo thành công!`
    regForm.value = { username: '', fullName: '', email: '', password: '', role: 'CITIZEN' }
    setTimeout(() => { switchMode('login') }, 2000)
  } catch (e) {
    error.value = e.response?.data?.message || 'Tên đăng nhập hoặc email đã tồn tại.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ===== ROOT ===== */
.auth-root {
  display: flex;
  min-height: 100vh;
  background: #f7f9fb;
}

/* ===== LEFT PANEL ===== */
.auth-left {
  position: relative;
  flex: 1;
  display: none;
  background: linear-gradient(135deg, #0050cb 0%, #003fa4 60%, #001849 100%);
  overflow: hidden;
  padding: 3rem;
}

@media (min-width: 1024px) {
  .auth-left { display: flex; align-items: center; }
}

.auth-left-inner {
  position: relative;
  z-index: 10;
  display: flex;
  flex-direction: column;
  gap: 3rem;
  width: 100%;
  max-width: 440px;
}

/* Brand */
.auth-brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.auth-logo {
  width: 56px;
  height: 56px;
  background: rgba(255,255,255,0.15);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid rgba(255,255,255,0.25);
  backdrop-filter: blur(8px);
}

.auth-logo .material-symbols-outlined {
  color: white;
  font-size: 28px;
}

.auth-brand-title {
  color: white;
  font-size: 1.25rem;
  font-weight: 800;
  letter-spacing: -0.02em;
  line-height: 1.2;
}

.auth-brand-sub {
  color: rgba(255,255,255,0.6);
  font-size: 0.75rem;
  font-weight: 500;
  margin-top: 2px;
}

/* Hero */
.auth-hero-title {
  color: white;
  font-size: 2.25rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  line-height: 1.15;
  margin-bottom: 1rem;
}

.auth-hero-desc {
  color: rgba(255,255,255,0.7);
  font-size: 1rem;
  line-height: 1.7;
}

/* Stats */
.auth-stats {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 16px;
  padding: 1.25rem 1.5rem;
  backdrop-filter: blur(8px);
}

.auth-stat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.auth-stat-value {
  color: white;
  font-size: 1.375rem;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.auth-stat-label {
  color: rgba(255,255,255,0.55);
  font-size: 0.7rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.auth-stat-divider {
  width: 1px;
  height: 36px;
  background: rgba(255,255,255,0.15);
}

/* Decorative circles */
.auth-deco {
  position: absolute;
  border-radius: 50%;
  opacity: 0.12;
  background: white;
}

.auth-deco-1 {
  width: 400px;
  height: 400px;
  bottom: -150px;
  right: -100px;
}

.auth-deco-2 {
  width: 200px;
  height: 200px;
  top: -60px;
  right: 60px;
  opacity: 0.06;
}

.auth-deco-3 {
  width: 120px;
  height: 120px;
  top: 40%;
  left: -30px;
  opacity: 0.08;
}

/* ===== RIGHT PANEL ===== */
.auth-right {
  flex: none;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 1.5rem;
  overflow-y: auto;
}

@media (min-width: 1024px) {
  .auth-right {
    width: 480px;
    flex: none;
    border-left: 1px solid #eceef0;
  }
}

.auth-form-wrap {
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 0;
}

/* Tabs */
.auth-tabs {
  display: flex;
  gap: 4px;
  background: #eceef0;
  border-radius: 14px;
  padding: 4px;
  margin-bottom: 2rem;
}

.auth-tab {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 10px 16px;
  border-radius: 10px;
  font-size: 0.8125rem;
  font-weight: 600;
  color: #424656;
  background: transparent;
  border: none;
  cursor: pointer;
  transition: all 0.2s ease;
}

.auth-tab .material-symbols-outlined {
  font-size: 18px;
}

.auth-tab:hover {
  background: rgba(255,255,255,0.6);
  color: #191c1e;
}

.auth-tab--active {
  background: white;
  color: #0050cb;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

/* Form header */
.auth-form-header {
  margin-bottom: 1.75rem;
}

.auth-form-title {
  font-size: 1.625rem;
  font-weight: 800;
  letter-spacing: -0.025em;
  color: #191c1e;
  line-height: 1.2;
}

.auth-form-desc {
  color: #424656;
  font-size: 0.875rem;
  margin-top: 6px;
  line-height: 1.6;
}

/* Form */
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 1.125rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.875rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: #424656;
}

.form-input-wrap {
  position: relative;
  display: flex;
  align-items: center;
}

.form-icon {
  position: absolute;
  left: 12px;
  font-size: 18px;
  color: #727687;
  pointer-events: none;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 11px 12px 11px 40px;
  background: #f2f4f6;
  border: 1.5px solid transparent;
  border-radius: 12px;
  font-size: 0.875rem;
  color: #191c1e;
  font-family: inherit;
  transition: all 0.2s ease;
  outline: none;
}

.form-input::placeholder {
  color: #9da3b6;
}

.form-input:focus {
  background: white;
  border-color: #0050cb;
  box-shadow: 0 0 0 3px rgba(0, 80, 203, 0.1);
}

.form-select {
  cursor: pointer;
  appearance: none;
  padding-right: 36px;
}

.form-eye {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  color: #727687;
  display: flex;
  align-items: center;
  padding: 4px;
  border-radius: 6px;
  transition: color 0.2s;
}

.form-eye:hover {
  color: #0050cb;
}

.form-eye .material-symbols-outlined {
  font-size: 20px;
}

/* Error / Success */
.form-error {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8125rem;
  color: #ba1a1a;
  background: #fff0f0;
  border: 1px solid #ffdad6;
  padding: 10px 14px;
  border-radius: 10px;
}

.form-error .material-symbols-outlined { font-size: 18px; }

.form-success {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.8125rem;
  color: #1a6938;
  background: #f0fff5;
  border: 1px solid #b8f0cb;
  padding: 10px 14px;
  border-radius: 10px;
}

.form-success .material-symbols-outlined { font-size: 18px; }

/* CTA Button */
.btn-primary {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 13px 20px;
  background: #0050cb;
  color: white;
  font-family: inherit;
  font-size: 0.9375rem;
  font-weight: 700;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(0, 80, 203, 0.35);
  transition: all 0.2s ease;
  margin-top: 8px;
}

.btn-primary:hover:not(:disabled) {
  background: #003fa4;
  box-shadow: 0 6px 20px rgba(0, 80, 203, 0.45);
  transform: translateY(-1px);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(0, 80, 203, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary .material-symbols-outlined {
  font-size: 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.btn-spin {
  animation: spin 0.8s linear infinite;
}

/* Footer */
.auth-footer {
  text-align: center;
  font-size: 0.75rem;
  color: #9da3b6;
  margin-top: 2rem;
}
</style>
