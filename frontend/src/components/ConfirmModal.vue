<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div
        v-if="uiStore.confirmState.visible"
        class="fixed inset-0 z-[9998] flex items-center justify-center p-4"
        @click.self="cancel"
      >
        <!-- Backdrop -->
        <div class="absolute inset-0 bg-slate-900/50 backdrop-blur-sm"></div>

        <!-- Dialog box -->
        <div class="relative bg-white rounded-3xl shadow-2xl max-w-md w-full overflow-hidden">
          <!-- Top accent bar -->
          <div class="h-1.5 w-full" :class="accentColor"></div>

          <div class="p-8">
            <!-- Icon + Title -->
            <div class="flex items-center gap-4 mb-4">
              <div :class="['w-12 h-12 rounded-2xl flex items-center justify-center shrink-0', iconBg]">
                <span
                  class="material-symbols-outlined text-2xl"
                  :class="iconColor"
                  style="font-variation-settings: 'FILL' 1;"
                >{{ iconName }}</span>
              </div>
              <h3 class="text-xl font-black text-on-surface leading-tight">
                {{ uiStore.confirmState.title }}
              </h3>
            </div>

            <!-- Message -->
            <p v-if="uiStore.confirmState.message" class="text-sm text-on-surface-variant leading-relaxed ml-16">
              {{ uiStore.confirmState.message }}
            </p>

            <!-- Actions -->
            <div class="flex gap-3 mt-8 justify-end">
              <button
                @click="cancel"
                class="px-5 py-2.5 rounded-xl text-sm font-semibold text-on-surface-variant bg-surface-container hover:bg-surface-container-high transition-all"
              >
                {{ uiStore.confirmState.cancelLabel }}
              </button>
              <button
                @click="ok"
                :class="['px-6 py-2.5 rounded-xl text-sm font-bold text-white transition-all hover:opacity-90 active:scale-95', confirmBtnColor]"
              >
                {{ uiStore.confirmState.confirmLabel }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { computed } from 'vue'
import { uiStore } from '../stores/ui'

const variant = computed(() => uiStore.confirmState.variant)

const accentColor = computed(() => ({
  danger:  'bg-red-500',
  warning: 'bg-amber-400',
  info:    'bg-blue-500',
}[variant.value] || 'bg-red-500'))

const confirmBtnColor = computed(() => ({
  danger:  'bg-red-500 hover:bg-red-600 shadow-lg shadow-red-500/25',
  warning: 'bg-amber-500 hover:bg-amber-600 shadow-lg shadow-amber-500/25',
  info:    'bg-blue-600 hover:bg-blue-700 shadow-lg shadow-blue-500/25',
}[variant.value] || 'bg-red-500'))

const iconBg = computed(() => ({
  danger:  'bg-red-100',
  warning: 'bg-amber-100',
  info:    'bg-blue-100',
}[variant.value] || 'bg-red-100'))

const iconColor = computed(() => ({
  danger:  'text-red-600',
  warning: 'text-amber-600',
  info:    'text-blue-600',
}[variant.value] || 'text-red-600'))

const iconName = computed(() => ({
  danger:  'delete_forever',
  warning: 'warning',
  info:    'help',
}[variant.value] || 'help'))

function ok()     { uiStore._resolveConfirm(true)  }
function cancel() { uiStore._resolveConfirm(false) }
</script>

<style scoped>
.modal-fade-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.4, 0.64, 1);
}
.modal-fade-leave-active {
  transition: all 0.2s ease-in;
}
.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}
.modal-fade-enter-from :deep(.relative.bg-white) {
  transform: scale(0.9) translateY(20px);
}
</style>
