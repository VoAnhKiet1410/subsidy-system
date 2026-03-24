import { reactive } from 'vue'

let toastId = 0

export function useUI() {
  return uiStore
}

export const uiStore = reactive({
  // --- TOASTS ---
  toasts: [],

  toast: {
    _add(type, message, duration = 3500) {
      const id = ++toastId
      uiStore.toasts.push({ id, type, message })
      setTimeout(() => uiStore._removeToast(id), duration)
    },
    success(msg, duration) { this._add('success', msg, duration) },
    error(msg, duration)   { this._add('error',   msg, duration ?? 5000) },
    warning(msg, duration) { this._add('warning', msg, duration) },
    info(msg, duration)    { this._add('info',    msg, duration) },
  },

  _removeToast(id) {
    const i = this.toasts.findIndex(t => t.id === id)
    if (i !== -1) this.toasts.splice(i, 1)
  },

  // Shortcut methods — dùng trực tiếp: ui.showSuccess('msg')
  showSuccess(msg, dur) { this.toast.success(msg, dur) },
  showError(msg, dur)   { this.toast.error(msg, dur) },
  showWarning(msg, dur) { this.toast.warning(msg, dur) },
  showInfo(msg, dur)    { this.toast.info(msg, dur) },

  // --- CONFIRM DIALOG ---
  confirmState: {
    visible: false,
    title: '',
    message: '',
    confirmLabel: 'Xác nhận',
    cancelLabel: 'Hủy',
    variant: 'danger',   // 'danger' | 'warning' | 'info'
    resolve: null,
  },

  confirm({ title = 'Xác nhận', message = '', confirmLabel = 'Xác nhận', cancelLabel = 'Hủy', variant = 'danger' } = {}) {
    return new Promise((resolve) => {
      this.confirmState.visible = true
      this.confirmState.title = title
      this.confirmState.message = message
      this.confirmState.confirmLabel = confirmLabel
      this.confirmState.cancelLabel = cancelLabel
      this.confirmState.variant = variant
      this.confirmState.resolve = resolve
    })
  },

  _resolveConfirm(value) {
    this.confirmState.visible = false
    if (this.confirmState.resolve) {
      this.confirmState.resolve(value)
      this.confirmState.resolve = null
    }
  },
})
