<template>
  <div class="animate-pulse">
    <!-- Card variant -->
    <div v-if="variant === 'card'" class="bg-white rounded-2xl border border-slate-200 p-6 space-y-4">
      <div class="flex items-center gap-3">
        <div class="w-11 h-11 rounded-2xl bg-slate-100"></div>
        <div class="flex-1 space-y-2">
          <div class="w-24 h-3 bg-slate-100 rounded"></div>
          <div class="w-16 h-5 bg-slate-100 rounded"></div>
        </div>
      </div>
    </div>

    <!-- Row variant -->
    <div v-else-if="variant === 'row'" class="flex items-center gap-4 p-4">
      <div class="w-9 h-9 rounded-xl bg-slate-100 flex-shrink-0"></div>
      <div class="flex-1 space-y-2">
        <div class="w-40 h-3.5 bg-slate-100 rounded"></div>
        <div class="w-28 h-2.5 bg-slate-50 rounded"></div>
      </div>
      <div class="w-16 h-5 bg-slate-100 rounded-full"></div>
    </div>

    <!-- Text variant -->
    <div v-else-if="variant === 'text'" class="space-y-2">
      <div class="w-full h-3.5 bg-slate-100 rounded"></div>
      <div class="w-3/4 h-3.5 bg-slate-100 rounded"></div>
      <div class="w-1/2 h-3.5 bg-slate-50 rounded"></div>
    </div>

    <!-- Default: simple line -->
    <div v-else class="w-full h-4 bg-slate-100 rounded"></div>
  </div>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'default', validator: v => ['default', 'card', 'row', 'text'].includes(v) },
})
</script>
