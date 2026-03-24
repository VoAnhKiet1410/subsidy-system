import { ref, computed } from 'vue'

/**
 * Composable form validation cho các form trong hệ thống.
 *
 * Cách dùng:
 *   const { errors, validate, validateField, isValid, clearErrors } = useFormValidation(rules)
 *   if (validate(formData)) { // submit }
 *
 * Rules format:
 *   { fieldName: [ { test: (value) => bool, message: 'Error msg' } ] }
 */
export function useFormValidation(rules = {}) {
  const errors = ref({})

  function validateField(field, value) {
    const fieldRules = rules[field]
    if (!fieldRules) return true
    for (const rule of fieldRules) {
      if (!rule.test(value)) {
        errors.value[field] = rule.message
        return false
      }
    }
    delete errors.value[field]
    return true
  }

  function validate(formData) {
    errors.value = {}
    let valid = true
    for (const field of Object.keys(rules)) {
      if (!validateField(field, formData[field])) valid = false
    }
    return valid
  }

  function clearErrors() { errors.value = {} }
  function clearField(field) { delete errors.value[field] }

  const isValid = computed(() => Object.keys(errors.value).length === 0)

  return { errors, validate, validateField, isValid, clearErrors, clearField }
}

// ======= Validators phổ biến =======
export const validators = {
  required: (msg = 'Trường này là bắt buộc') => ({
    test: v => v !== null && v !== undefined && String(v).trim() !== '',
    message: msg,
  }),
  minLength: (n, msg) => ({
    test: v => String(v || '').length >= n,
    message: msg || `Tối thiểu ${n} ký tự`,
  }),
  maxLength: (n, msg) => ({
    test: v => String(v || '').length <= n,
    message: msg || `Tối đa ${n} ký tự`,
  }),
  phone: (msg = 'Số điện thoại không hợp lệ') => ({
    test: v => /^(0[0-9]{9,10})$/.test(String(v || '').replace(/\s/g, '')),
    message: msg,
  }),
  cccd: (msg = 'Số CCCD phải 12 chữ số') => ({
    test: v => /^[0-9]{12}$/.test(String(v || '').replace(/\s/g, '')),
    message: msg,
  }),
  email: (msg = 'Email không hợp lệ') => ({
    test: v => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(String(v || '')),
    message: msg,
  }),
}
