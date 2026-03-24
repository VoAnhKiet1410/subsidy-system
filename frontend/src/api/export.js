import http from './http'

/**
 * API xuất báo cáo Excel
 */
export const exportApi = {
  /**
   * Download file Excel từ endpoint
   * @param {string} type - beneficiaries | programs | applications | payments
   */
  async download(type) {
    const res = await http.get(`/export/${type}`, {
      responseType: 'blob',
      // Tắt unwrap ApiResponse cho response này (vì trả binary)
      transformResponse: [(data) => data],
    })

    // Lấy filename từ Content-Disposition header
    const disposition = res.headers['content-disposition']
    let filename = `${type}_report.xlsx`
    if (disposition) {
      const match = disposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
      if (match && match[1]) {
        filename = match[1].replace(/['"]/g, '')
      }
    }

    // Tạo link download
    const blob = new Blob([res.data], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }
}
