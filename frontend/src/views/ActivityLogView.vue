<template>
  <div class="min-h-screen bg-slate-50/60">
    <!-- Header -->
    <div class="bg-white border-b border-slate-200/80 px-8 py-5 flex items-center justify-between sticky top-0 z-30">
      <div>
        <h2 class="text-xl font-black text-slate-800 tracking-tight">Nhật ký hoạt động</h2>
        <p class="text-xs text-slate-400 mt-0.5">Theo dõi tất cả hoạt động trong hệ thống</p>
      </div>
      <div class="flex items-center gap-3">
        <select v-model="filterType" class="px-3 py-2 bg-slate-100 border-0 rounded-xl text-sm font-semibold text-slate-600 focus:outline-none focus:ring-2 focus:ring-primary/20">
          <option value="all">Tất cả</option>
          <option value="application">Hồ sơ</option>
          <option value="program">Chương trình</option>
          <option value="user">Người dùng</option>
          <option value="payment">Chi trả</option>
          <option value="system">Hệ thống</option>
        </select>
        <button @click="loadLogs" :disabled="loading"
          class="flex items-center gap-2 px-4 py-2 bg-slate-100 text-slate-600 text-sm font-semibold rounded-xl hover:bg-slate-200 transition-colors">
          <span :class="['material-symbols-outlined text-sm', loading ? 'animate-spin' : '']">refresh</span>
          Làm mới
        </button>
      </div>
    </div>

    <div class="p-8">
      <!-- Timeline -->
      <div class="max-w-4xl mx-auto">
        <!-- Skeleton -->
        <div v-if="loading" class="space-y-4">
          <div v-for="i in 6" :key="i" class="flex gap-4 animate-pulse">
            <div class="w-10 h-10 rounded-xl bg-slate-100 flex-shrink-0"></div>
            <div class="flex-1 bg-white rounded-2xl border border-slate-200 p-5 space-y-2">
              <div class="w-48 h-3.5 bg-slate-100 rounded"></div>
              <div class="w-full h-3 bg-slate-50 rounded"></div>
              <div class="w-24 h-2.5 bg-slate-50 rounded"></div>
            </div>
          </div>
        </div>

        <!-- Log entries -->
        <div v-else class="relative">
          <div class="absolute left-5 top-0 bottom-0 w-px bg-slate-200"></div>

          <div v-for="log in filteredLogs" :key="log.id" class="relative flex gap-4 mb-4">
            <!-- Icon -->
            <div :class="['w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 z-10', log.iconBg]">
              <span class="material-symbols-outlined text-sm" style="font-variation-settings:'FILL' 1;">{{ log.icon }}</span>
            </div>

            <!-- Content -->
            <div class="flex-1 bg-white rounded-2xl border border-slate-200/80 shadow-sm p-5 hover:shadow-md transition-shadow">
              <div class="flex items-start justify-between gap-3">
                <div class="flex-1">
                  <p class="font-bold text-slate-800 text-sm">{{ log.title }}</p>
                  <p class="text-xs text-slate-500 mt-1 leading-relaxed">{{ log.description }}</p>
                </div>
                <span :class="['px-2 py-0.5 rounded-full text-[10px] font-bold flex-shrink-0', typeClass(log.type)]">
                  {{ typeLabel(log.type) }}
                </span>
              </div>
              <div class="flex items-center gap-3 mt-3 text-[11px] text-slate-400">
                <span class="flex items-center gap-1">
                  <span class="material-symbols-outlined" style="font-size:13px;">schedule</span>
                  {{ log.time }}
                </span>
                <span v-if="log.user" class="flex items-center gap-1">
                  <span class="material-symbols-outlined" style="font-size:13px;">person</span>
                  {{ log.user }}
                </span>
                <span v-if="log.ip" class="flex items-center gap-1">
                  <span class="material-symbols-outlined" style="font-size:13px;">computer</span>
                  {{ log.ip }}
                </span>
              </div>
            </div>
          </div>

          <!-- Empty state -->
          <div v-if="!filteredLogs.length" class="text-center py-16">
            <span class="material-symbols-outlined text-5xl text-slate-200 block mb-3">history</span>
            <p class="font-bold text-slate-400">Không có hoạt động nào</p>
            <p class="text-xs text-slate-300 mt-1">Thay đổi bộ lọc để xem thêm</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import http from '../api/http'

const loading = ref(false)
const filterType = ref('all')

const logs = ref([])

const actionIconMap = {
  LOGIN:    { type: 'system',      icon: 'login',         iconBg: 'bg-slate-100 text-slate-600' },
  LOGOUT:   { type: 'system',      icon: 'logout',        iconBg: 'bg-slate-100 text-slate-600' },
  CREATE:   { type: 'program',     icon: 'add_circle',    iconBg: 'bg-amber-100 text-amber-600' },
  REVIEW:   { type: 'application', icon: 'manage_search', iconBg: 'bg-blue-100 text-blue-600' },
  APPROVE:  { type: 'application', icon: 'check_circle',  iconBg: 'bg-emerald-100 text-emerald-600' },
  REJECT:   { type: 'application', icon: 'cancel',        iconBg: 'bg-red-100 text-red-600' },
  DISBURSE: { type: 'payment',     icon: 'payments',      iconBg: 'bg-purple-100 text-purple-600' },
  UPDATE:   { type: 'system',      icon: 'edit',          iconBg: 'bg-teal-100 text-teal-600' },
  DELETE:   { type: 'system',      icon: 'delete',        iconBg: 'bg-red-100 text-red-600' },
}
const defaultActionIcon = { type: 'system', icon: 'history', iconBg: 'bg-slate-100 text-slate-600' }

const filteredLogs = computed(() => {
  if (filterType.value === 'all') return logs.value
  return logs.value.filter(l => l.type === filterType.value)
})

function typeLabel(t) {
  return { application: 'Hồ sơ', program: 'Chương trình', user: 'Người dùng', payment: 'Chi trả', system: 'Hệ thống' }[t] || t
}
function typeClass(t) {
  return {
    application: 'bg-blue-100 text-blue-700',
    program: 'bg-amber-100 text-amber-700',
    user: 'bg-teal-100 text-teal-700',
    payment: 'bg-purple-100 text-purple-700',
    system: 'bg-slate-100 text-slate-600',
  }[t] || 'bg-slate-100 text-slate-600'
}

function timeAgo(ts) {
  if (!ts) return ''
  const diff = Date.now() - new Date(ts).getTime()
  const m = Math.floor(diff / 60000)
  if (m < 1) return 'Vừa xong'
  if (m < 60) return `${m} phút trước`
  const h = Math.floor(m / 60)
  if (h < 24) return `${h} giờ trước`
  const d = Math.floor(h / 24)
  if (d === 1) return 'Hôm qua'
  return new Date(ts).toLocaleDateString('vi-VN')
}

async function loadLogs() {
  loading.value = true
  try {
    const res = await http.get('/audit-logs', { params: { page: 0, size: 50 } })
    const list = res.data?.content || res.data || []
    logs.value = list.map(l => {
      const info = actionIconMap[l.action] || defaultActionIcon
      return {
        id: l.id,
        type: info.type,
        icon: info.icon,
        iconBg: info.iconBg,
        title: `${l.action} — ${l.entityType || 'System'}`,
        description: l.details || `${l.action} bởi ${l.username || 'system'}`,
        time: l.createdAt ? timeAgo(l.createdAt) : '—',
        user: l.username || 'system',
        ip: l.ipAddress || null,
      }
    })
  } catch (e) {
    console.error('Lỗi tải nhật ký:', e)
    logs.value = []
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)
</script>
