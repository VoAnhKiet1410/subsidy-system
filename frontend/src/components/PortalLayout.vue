<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">

    <!-- TOP NAV -->
    <nav class="bg-white border-b border-slate-200 sticky top-0 z-40 shadow-sm">
      <div class="max-w-4xl mx-auto px-4 py-3 flex items-center gap-3">
        <!-- Logo -->
        <div class="flex items-center gap-2.5 flex-1">
          <div class="w-8 h-8 rounded-xl bg-primary flex items-center justify-center flex-shrink-0">
            <span class="material-symbols-outlined text-white text-sm" style="font-variation-settings:'FILL' 1;">volunteer_activism</span>
          </div>
          <div>
            <p class="text-sm font-black text-slate-800 leading-tight">Trợ cấp XH</p>
            <p class="text-[10px] text-slate-400 leading-tight hidden sm:block">Cổng đăng ký trực tuyến</p>
          </div>
        </div>

        <!-- Desktop nav links -->
        <div class="hidden md:flex items-center gap-1">
          <router-link v-for="n in navItems" :key="n.to" :to="n.to" :class="navClass(n.to)">
            <span class="material-symbols-outlined text-sm">{{ n.icon }}</span>{{ n.label }}
          </router-link>
        </div>

        <!-- Right actions -->
        <div class="flex items-center gap-1">

          <!-- NOTIFICATION BELL -->
          <div class="relative" ref="notifRef">
            <button @click="toggleNotif"
              class="relative p-2 rounded-xl hover:bg-slate-100 text-slate-500 transition-colors">
              <span class="material-symbols-outlined text-xl leading-none">notifications</span>
              <span v-if="unreadCount > 0"
                class="absolute top-1.5 right-1.5 w-4 h-4 bg-red-500 rounded-full text-white text-[9px] font-black flex items-center justify-center">
                {{ unreadCount > 9 ? '9+' : unreadCount }}
              </span>
            </button>

            <!-- Notification dropdown -->
            <Transition name="dropdown">
              <div v-if="showNotif"
                class="absolute right-0 top-full mt-2 w-80 bg-white rounded-2xl shadow-xl border border-slate-200/80 overflow-hidden z-50">
                <!-- Header -->
                <div class="flex items-center justify-between px-4 py-3 border-b border-slate-100">
                  <h3 class="font-black text-slate-800 text-sm">Thông báo</h3>
                  <button @click="markAllRead" class="text-xs font-semibold text-primary hover:underline">Đọc tất cả</button>
                </div>
                <!-- List -->
                <div class="max-h-72 overflow-y-auto divide-y divide-slate-50">
                  <div v-for="n in notifications" :key="n.id" @click="readNotif(n)"
                    :class="['flex items-start gap-3 px-4 py-3 cursor-pointer hover:bg-slate-50 transition-colors', !n.read && 'bg-primary/3']">
                    <div :class="['w-8 h-8 rounded-xl flex items-center justify-center flex-shrink-0 mt-0.5', n.iconBg]">
                      <span class="material-symbols-outlined text-sm" :class="n.iconColor" style="font-variation-settings:'FILL' 1;">{{ n.icon }}</span>
                    </div>
                    <div class="flex-1 min-w-0">
                      <p :class="['text-sm leading-snug', n.read ? 'text-slate-600' : 'text-slate-800 font-semibold']">{{ n.message }}</p>
                      <p class="text-[10px] text-slate-400 mt-0.5">{{ n.time }}</p>
                    </div>
                    <div v-if="!n.read" class="w-2 h-2 bg-primary rounded-full flex-shrink-0 mt-2"></div>
                  </div>
                </div>
                <!-- Footer -->
                <div class="border-t border-slate-100 px-4 py-2.5 text-center">
                  <router-link to="/portal/thong-bao" @click="showNotif=false"
                    class="text-xs font-bold text-primary hover:underline">Xem tất cả thông báo</router-link>
                </div>
              </div>
            </Transition>
          </div>

          <!-- USER AVATAR DROPDOWN -->
          <div class="relative" ref="userRef">
            <button @click="toggleUser"
              class="flex items-center gap-2 pl-2 pr-3 py-1.5 rounded-xl hover:bg-slate-100 transition-colors">
              <div class="w-7 h-7 rounded-lg bg-primary/10 text-primary flex items-center justify-center text-xs font-black">
                {{ initials }}
              </div>
              <span class="text-sm font-semibold text-slate-700 hidden sm:block">{{ shortName }}</span>
              <span class="material-symbols-outlined text-slate-400 text-sm hidden sm:block">
                {{ showUser ? 'expand_less' : 'expand_more' }}
              </span>
            </button>

            <!-- User dropdown -->
            <Transition name="dropdown">
              <div v-if="showUser"
                class="absolute right-0 top-full mt-2 w-56 bg-white rounded-2xl shadow-xl border border-slate-200/80 overflow-hidden z-50">
                <!-- User info -->
                <div class="px-4 py-3 bg-gradient-to-br from-primary/5 to-indigo-50 border-b border-slate-100">
                  <p class="font-black text-slate-800 text-sm">{{ displayName }}</p>
                  <p class="text-[11px] text-slate-500 mt-0.5 truncate">{{ authStore.user?.email || authStore.user?.username }}</p>
                  <span class="inline-flex items-center mt-1.5 px-2 py-0.5 bg-primary/10 text-primary text-[10px] font-bold rounded-full">
                    {{ roleLabel }}
                  </span>
                </div>
                <!-- Menu items -->
                <div class="py-1">
                  <router-link to="/portal/ca-nhan" @click="showUser=false"
                    class="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 hover:text-slate-800 transition-colors">
                    <span class="material-symbols-outlined text-slate-400 text-sm">person</span>
                    Thông tin cá nhân
                  </router-link>
                  <router-link to="/portal/ho-so-cua-toi" @click="showUser=false"
                    class="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 hover:text-slate-800 transition-colors">
                    <span class="material-symbols-outlined text-slate-400 text-sm">folder_open</span>
                    Hồ sơ của tôi
                  </router-link>
                  <router-link to="/portal/thong-bao" @click="showUser=false"
                    class="flex items-center gap-3 px-4 py-2.5 text-sm text-slate-600 hover:bg-slate-50 hover:text-slate-800 transition-colors">
                    <span class="material-symbols-outlined text-slate-400 text-sm">notifications</span>
                    Thông báo
                    <span v-if="unreadCount" class="ml-auto text-[10px] font-black px-1.5 py-0.5 bg-red-100 text-red-600 rounded-full">{{ unreadCount }}</span>
                  </router-link>
                </div>
                <!-- Divider + Logout -->
                <div class="border-t border-slate-100 py-1">
                  <button @click="handleLogout"
                    class="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-500 hover:bg-red-50 transition-colors">
                    <span class="material-symbols-outlined text-red-400 text-sm">logout</span>
                    Đăng xuất
                  </button>
                </div>
              </div>
            </Transition>
          </div>
        </div>
      </div>
    </nav>

    <main class="flex-1 max-w-4xl w-full mx-auto px-4 py-6 pb-24 md:pb-6">
      <router-view />
    </main>

    <!-- MOBILE BOTTOM NAV -->
    <nav class="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-slate-200 z-40 shadow-lg">
      <div class="grid grid-cols-4 px-2">
        <router-link v-for="n in navItems" :key="n.label" :to="n.to" :class="mobileNavClass(n.to)">
          <span class="material-symbols-outlined text-xl">{{ n.icon }}</span>
          <span class="text-[10px] font-semibold">{{ n.label }}</span>
        </router-link>
      </div>
    </nav>

    <!-- Backdrop -->
    <div v-if="showNotif || showUser" class="fixed inset-0 z-30" @click="closeAll"></div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authStore } from '../stores/auth'

const route  = useRoute()
const router = useRouter()

// ── Dropdowns ────────────────────────────────────────
const showNotif = ref(false)
const showUser  = ref(false)

function toggleNotif() { showNotif.value = !showNotif.value; showUser.value = false }
function toggleUser()  { showUser.value  = !showUser.value;  showNotif.value = false }
function closeAll()    { showNotif.value = false; showUser.value = false }

// ── User info ─────────────────────────────────────────
const displayName = computed(() => authStore.user?.fullName || authStore.user?.username || 'Người dùng')
const initials = computed(() => {
  const n = displayName.value
  return n.split(' ').slice(-2).map(w => w[0]).join('').toUpperCase()
})
const shortName = computed(() => {
  const n = displayName.value
  const parts = n.trim().split(' ')
  return parts.length > 1 ? parts[parts.length - 1] : parts[0]
})
const roleLabels = { ADMIN:'Quản trị viên', OFFICER:'Cán bộ xét duyệt', ACCOUNTANT:'Cán bộ tài chính', CITIZEN:'Người dân' }
const roleLabel = computed(() => roleLabels[authStore.role] || 'Người dùng')

function handleLogout() {
  closeAll()
  authStore.logout()
  router.push('/login')
}

// ── Notifications ─────────────────────────────────────
const notifications = ref([
  { id:1, message:'Hồ sơ #HS-1001 của bạn đã được phê duyệt!',       icon:'check_circle',  iconBg:'bg-emerald-50', iconColor:'text-emerald-500', time:'10 phút trước',  read:false },
  { id:2, message:'Hồ sơ #HS-1002 đang được xét duyệt.',              icon:'manage_search', iconBg:'bg-blue-50',    iconColor:'text-blue-500',    time:'2 giờ trước',    read:false },
  { id:3, message:'Chương trình "Hỗ trợ trẻ em" sắp hết hạn nộp.',   icon:'schedule',      iconBg:'bg-amber-50',   iconColor:'text-amber-500',   time:'Hôm qua',        read:true  },
  { id:4, message:'Tài khoản của bạn đã được xác minh thành công.',   icon:'verified_user', iconBg:'bg-primary/10', iconColor:'text-primary',     time:'2 ngày trước',   read:true  },
])
const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

function readNotif(n) {
  n.read = true
  showNotif.value = false
}
function markAllRead() {
  notifications.value.forEach(n => n.read = true)
}

// ── Nav ───────────────────────────────────────────────
const navItems = [
  { to:'/portal',               label:'Trang chủ',    icon:'home' },
  { to:'/portal/chuong-trinh',  label:'Chương trình', icon:'list_alt' },
  { to:'/portal/ho-so-cua-toi', label:'Hồ sơ',        icon:'folder_open' },
  { to:'/portal/ca-nhan',       label:'Cá nhân',      icon:'person' },
]

function isActive(to) {
  if (to === '/portal') return route.path === '/portal'
  return route.path.startsWith(to)
}
function navClass(to) {
  const base = 'flex items-center gap-1.5 px-3 py-2 rounded-xl text-sm font-semibold transition-colors'
  return isActive(to) ? base + ' bg-primary/10 text-primary' : base + ' text-slate-500 hover:text-slate-800 hover:bg-slate-100'
}
function mobileNavClass(to) {
  const base = 'flex flex-col items-center gap-0.5 py-2.5 px-1 transition-colors'
  return isActive(to) ? base + ' text-primary' : base + ' text-slate-400'
}
</script>

<style scoped>
.dropdown-enter-active { transition: opacity 0.15s ease, transform 0.15s ease; }
.dropdown-leave-active { transition: opacity 0.1s ease, transform 0.1s ease; }
.dropdown-enter-from   { opacity: 0; transform: translateY(-6px) scale(0.97); }
.dropdown-leave-to     { opacity: 0; transform: translateY(-4px) scale(0.98); }
</style>
