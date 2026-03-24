<template>
  <!-- Fixed ở góc dưới phải, xếp chồng lên nhau -->
  <Teleport to="body">
    <div class="fixed bottom-6 right-6 z-[9999] flex flex-col gap-3 items-end pointer-events-none">
      <TransitionGroup name="toast">
        <div
          v-for="toast in uiStore.toasts"
          :key="toast.id"
          class="pointer-events-auto flex items-start gap-3 min-w-[280px] max-w-sm px-4 py-4 rounded-2xl shadow-2xl backdrop-blur-sm border"
          :class="styles[toast.type].wrapper"
        >
          <!-- Icon -->
          <div :class="['w-8 h-8 rounded-xl flex items-center justify-center shrink-0', styles[toast.type].iconBg]">
            <span
              class="material-symbols-outlined text-[18px]"
              :class="styles[toast.type].iconColor"
              style="font-variation-settings: 'FILL' 1;"
            >{{ styles[toast.type].icon }}</span>
          </div>

          <!-- Message -->
          <p class="text-sm font-semibold leading-snug pt-1 flex-1" :class="styles[toast.type].text">
            {{ toast.message }}
          </p>

          <!-- Close button -->
          <button
            @click="uiStore._removeToast(toast.id)"
            :class="['p-1 rounded-lg transition-colors opacity-50 hover:opacity-100 mt-0.5', styles[toast.type].close]"
          >
            <span class="material-symbols-outlined text-[14px]">close</span>
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<script setup>
import { uiStore } from '../stores/ui'

const styles = {
  success: {
    wrapper: 'bg-white/95 border-green-200 shadow-green-100/50',
    iconBg:  'bg-green-100',
    iconColor: 'text-green-600',
    icon:  'check_circle',
    text:  'text-green-900',
    close: 'hover:bg-green-100 text-green-700',
  },
  error: {
    wrapper: 'bg-white/95 border-red-200 shadow-red-100/50',
    iconBg:  'bg-red-100',
    iconColor: 'text-red-600',
    icon:  'error',
    text:  'text-red-900',
    close: 'hover:bg-red-100 text-red-700',
  },
  warning: {
    wrapper: 'bg-white/95 border-amber-200 shadow-amber-100/50',
    iconBg:  'bg-amber-100',
    iconColor: 'text-amber-600',
    icon:  'warning',
    text:  'text-amber-900',
    close: 'hover:bg-amber-100 text-amber-700',
  },
  info: {
    wrapper: 'bg-white/95 border-blue-200 shadow-blue-100/50',
    iconBg:  'bg-blue-100',
    iconColor: 'text-blue-600',
    icon:  'info',
    text:  'text-blue-900',
    close: 'hover:bg-blue-100 text-blue-700',
  },
}
</script>

<style scoped>
.toast-enter-active {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.toast-leave-active {
  transition: all 0.25s ease-in;
}
.toast-enter-from {
  opacity: 0;
  transform: translateX(60px) scale(0.9);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(60px) scale(0.9);
}
.toast-move {
  transition: transform 0.3s ease;
}
</style>
