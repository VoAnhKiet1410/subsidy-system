<template>
  <span :class="['inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[10px] font-black leading-none', badgeClass]">
    <span v-if="icon" class="material-symbols-outlined" style="font-size:12px;font-variation-settings:'FILL' 1;">{{ icon }}</span>
    {{ label }}
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  status: { type: String, required: true },
  type: { type: String, default: 'application' }, // application | program
})

const STATUS_MAP = {
  application: {
    PENDING:   { label: 'Chờ duyệt',       badge: 'bg-amber-100 text-amber-700',     icon: 'hourglass_top' },
    REVIEWING: { label: 'Đang xét duyệt',  badge: 'bg-blue-100 text-blue-700',       icon: 'manage_search' },
    APPROVED:  { label: 'Đã phê duyệt',    badge: 'bg-emerald-100 text-emerald-700', icon: 'check_circle' },
    REJECTED:  { label: 'Từ chối',          badge: 'bg-red-100 text-red-600',         icon: 'cancel' },
    PAID:      { label: 'Đã chi trả',       badge: 'bg-purple-100 text-purple-700',   icon: 'payments' },
  },
  program: {
    OPEN:   { label: 'Đang mở',       badge: 'bg-emerald-100 text-emerald-700', icon: 'check_circle' },
    SOON:   { label: 'Sắp mở',        badge: 'bg-amber-100 text-amber-700',     icon: 'schedule' },
    CLOSED: { label: 'Đã kết thúc',   badge: 'bg-slate-100 text-slate-500',     icon: 'cancel' },
    ACTIVE: { label: 'Đang hoạt động', badge: 'bg-emerald-100 text-emerald-700', icon: 'check_circle' },
  },
}

const config = computed(() => {
  const map = STATUS_MAP[props.type] || STATUS_MAP.application
  return map[props.status] || { label: props.status, badge: 'bg-slate-100 text-slate-600', icon: null }
})

const label = computed(() => config.value.label)
const badgeClass = computed(() => config.value.badge)
const icon = computed(() => config.value.icon)
</script>
