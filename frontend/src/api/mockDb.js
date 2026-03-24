// Mock Database mô phỏng 11 tables cho Frontend UI Redesign

const now = new Date().toISOString()
const past1 = new Date(Date.now() - 24 * 3600 * 1000).toISOString()
const past5 = new Date(Date.now() - 5 * 24 * 3600 * 1000).toISOString()

export const mockDb = {
  // 1. nguoi_dung
  nguoi_dung: [
    { id: 1, ten_dang_nhap: 'admin', mat_khau: '123456', email: 'admin@system.vn', so_dien_thoai: '0901234567', dia_chi: 'Hà Nội', created_at: past5 },
    { id: 2, ten_dang_nhap: 'officer1', mat_khau: '123456', email: 'rev1@system.vn', so_dien_thoai: '0911222333', dia_chi: 'Hà Nội', created_at: past5 },
    { id: 3, ten_dang_nhap: 'accountant1', mat_khau: '123456', email: 'fin1@system.vn', so_dien_thoai: '0922333444', dia_chi: 'Hồ Chí Minh', created_at: past5 },
    { id: 4, ten_dang_nhap: 'citizen1', ho_va_ten: 'Nguyễn Văn A', mat_khau: '123456', email: 'nva@gmail.com', so_dien_thoai: '0988777666', dia_chi: 'Đà Nẵng', created_at: past5 },
    { id: 5, ten_dang_nhap: 'citizen2', ho_va_ten: 'Trần Thị B', mat_khau: '123456', email: 'ttb@gmail.com', so_dien_thoai: '0977666555', dia_chi: 'Cần Thơ', created_at: past5 }
  ],

  // 2. vai_tro
  vai_tro: [
    { id: 1, ma_vai_tro: 'ADMIN', ten_vai_tro: 'Quản trị viên', mo_ta: 'Toàn quyền' },
    { id: 2, ma_vai_tro: 'OFFICER', ten_vai_tro: 'Cán bộ xét duyệt', mo_ta: 'Xét duyệt hồ sơ' },
    { id: 3, ma_vai_tro: 'ACCOUNTANT', ten_vai_tro: 'Kế toán', mo_ta: 'Quản lý tài chính' },
    { id: 4, ma_vai_tro: 'CITIZEN', ten_vai_tro: 'Người xem', mo_ta: 'Người dân muốn nhận trợ cấp' }
  ],

  // 3. nguoi_dung_vai_tro
  nguoi_dung_vai_tro: [
    { nguoi_dung_id: 1, vai_tro_id: 1 },
    { nguoi_dung_id: 2, vai_tro_id: 2 },
    { nguoi_dung_id: 3, vai_tro_id: 3 },
    { nguoi_dung_id: 4, vai_tro_id: 4 },
    { nguoi_dung_id: 5, vai_tro_id: 4 }
  ],

  // 4. nguon_quy
  nguon_quy: [
    { id: 1, ten_nguon_quy: 'Quỹ BTXH Tỉnh 2026', tong_ngan_sach: 50000000000, ngan_sach_con_lai: 45000000000, created_at: past5, updated_at: past5 },
    { id: 2, ten_nguon_quy: 'Quỹ Cứu Trợ Khẩn Cấp', tong_ngan_sach: 10000000000, ngan_sach_con_lai: 2000000000, created_at: past5, updated_at: past5 }
  ],

  // 5. chuong_trinh_tru_cap
  chuong_trinh_tru_cap: [
    { id: 1, ten_chuong_trinh: 'Hỗ trợ Nông dân mất mùa Q1', nguon_quy_id: 2, mo_ta: 'Hỗ trợ bà con miền Trung...', ngay_bat_dau: past5, ngay_ket_thuc: '2026-12-31', trang_thai: 'ACTIVE', created_at: past5 },
    { id: 2, ten_chuong_trinh: 'Trợ cấp người khuyết tật định kỳ', nguon_quy_id: 1, mo_ta: 'Chi trả hàng tháng', ngay_bat_dau: past5, ngay_ket_thuc: '2026-12-31', trang_thai: 'ACTIVE', created_at: past5 }
  ],

  // 6. doi_tuong_huong_tru_cap
  doi_tuong_huong_tru_cap: [
    { id: 1, ten_doi_tuong: 'Thu nhập thấp', mo_ta: 'Chuẩn nghèo quốc gia', created_at: past5 },
    { id: 2, ten_doi_tuong: 'Khuyết tật', mo_ta: 'Khuyết tật bẩm sinh/thương tật', created_at: past5 },
    { id: 3, ten_doi_tuong: 'Thiên tai', mo_ta: 'Chịu ảnh hưởng trực tiếp của lũ lụt', created_at: past5 }
  ],

  // 7. ho_so_ho_tro
  ho_so_ho_tro: [
    { id: 1, nguoi_dung_id: 4, doi_tuong_huong_tru_cap_id: 3, chuong_trinh_tru_cap_id: 1, ngay_nop_ho_so: past1, trang_thai: 'PENDING', ly_do_tu_choi: null, created_at: past1 },
    { id: 2, nguoi_dung_id: 5, doi_tuong_huong_tru_cap_id: 2, chuong_trinh_tru_cap_id: 2, ngay_nop_ho_so: past5, trang_thai: 'APPROVED', ly_do_tu_choi: null, created_at: past5 }
  ],

  // 8. danh_gia_ai
  danh_gia_ai: [
    { id: 1, ho_so_ho_tro_id: 1, diem_uu_tien: 85.5, do_tin_cay_ai: 92.0, nhan_xet_ai: 'Trùng khớp dữ liệu thiên tai. Báo cáo ảnh ngập lụt thực tế rõ nét. Tuyển duyệt ưu tiên.', created_at: past1 },
    { id: 2, ho_so_ho_tro_id: 2, diem_uu_tien: 90.0, do_tin_cay_ai: 98.5, nhan_xet_ai: 'Xác thực chuẩn nghèo hợp lệ qua CMND. OCR thành công 100%.', created_at: past5 }
  ],

  // 9. chi_tra_tru_cap
  chi_tra_tru_cap: [
    { id: 1, ho_so_ho_tro_id: 2, so_tien: 2000000, ngay_chi_tra: now, phuong_thuc: 'CHUYEN_KHOAN', trang_thai: 'PROCESSING', created_at: now }
  ],

  // 10. tai_lieu_dinh_kem
  tai_lieu_dinh_kem: [
    { id: 1, ho_so_ho_tro_id: 1, ten_tai_lieu: 'chung_minh_nhan_dan.jpg', duong_dan_file: '/uploads/cmnd_1.jpg', loai_file: 'IMAGE', ket_qua_ocr: 'Nguyễn Văn A\nSinh: 1980\nQuê quán: Đà Nẵng', created_at: past1 },
    { id: 2, ho_so_ho_tro_id: 1, ten_tai_lieu: 'giay_xac_nhan_thiet_hai.pdf', duong_dan_file: '/uploads/gxnth_1.pdf', loai_file: 'PDF', ket_qua_ocr: 'Thiệt hại 5ha cây trồng...', created_at: past1 }
  ],

  // 11. thong_bao
  thong_bao: [
    { id: 1, nguoi_dung_id: 4, tieu_de: 'Hồ sơ đã được tiếp nhận', noi_dung: 'Hồ sơ xin trợ cấp mất mùa của bạn đã vào hệ thống.', da_doc: false, created_at: past1 },
    { id: 2, nguoi_dung_id: 5, tieu_de: 'Hồ sơ đã phê duyệt', noi_dung: 'Xin chúc mừng, hồ sơ khuyết tật của bạn đã được duyệt. Tiền sẽ sớm được chuyển khoản.', da_doc: true, created_at: past5 }
  ]
}
