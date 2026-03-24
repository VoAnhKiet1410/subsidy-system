/**
 * Format mã hồ sơ: chỉ lấy 8 ký tự cuối của MongoDB ObjectId
 * Ví dụ: "69c244c0b3833677763c165c" → "#HS-763c165c"
 * @param {string} id - MongoDB ObjectId string (24 chars)
 * @returns {string} mã hồ sơ dễ đọc
 */
export function formatMaHoSo(id) {
  if (!id) return '#HS-???'
  // Nếu ID ngắn (<= 8 ký tự) thì dùng nguyên
  if (id.length <= 8) return `#HS-${id}`
  // Lấy 8 ký tự cuối của ObjectId
  return `#HS-${id.slice(-8).toUpperCase()}`
}
