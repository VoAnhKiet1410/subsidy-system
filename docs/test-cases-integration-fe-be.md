# Bảng Test Case — Kiểm thử tích hợp Frontend ↔ Backend
## Hệ thống Quản lý Trợ cấp Xã hội

**QA Lead:** Senior QA  
**Công nghệ:** Vue.js + Spring Boot + MongoDB + JWT  
**Ngày tạo:** 24/03/2026  
**Phương pháp:** Integration Testing thực tế (FE gọi BE qua Network, kiểm tra Request/Response/UI)

---

## Quy ước đọc bảng

- **✅ Network Check**: kiểm tra trên DevTools > Network tab
- **Request**: body mà FE gửi (kiểm tra tab Payload)
- **Response**: body mà BE trả (kiểm tra tab Response)
- **UI Check**: giao diện FE phải hiển thị đúng
- **Loại**: Positive / Negative / Security / Empty State / UX / Boundary

---

## MODULE 1: AUTHENTICATION (10 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check (DevTools) | UI Check (FE phải hiển thị) | Loại |
|---|---|---|---|---|---|---|
| INT-AUTH-01 | Đăng ký thành công | BE đang chạy, chưa có user "testqa" | 1. Mở `/login` → tab Đăng ký 2. Nhập: username=testqa, password=123456, email=qa@test.com, fullName=QA Test 3. Nhấn Đăng ký | **URL:** `POST /api/auth/register` **Status:** `200` **Request:** `{"username":"testqa","password":"123456","email":"qa@test.com","fullName":"QA Test"}` **Response:** `{"success":true,"data":{"id":"...","username":"testqa","roles":["CITIZEN"]}}` | ✅ Toast "Đăng ký thành công" ✅ Chuyển về form Đăng nhập ✅ Form đăng ký reset | Positive |
| INT-AUTH-02 | Đăng ký trùng username | User "testqa" đã tồn tại | 1. Nhập lại username=testqa 2. Nhấn Đăng ký | **Status:** `400` **Response:** `{"success":false,"message":"Tên đăng nhập đã tồn tại"}` | ✅ Toast lỗi "Tên đăng nhập đã tồn tại" ✅ Form KHÔNG bị reset ✅ KHÔNG redirect | Negative |
| INT-AUTH-03 | Đăng ký trùng email | User với email qa@test.com đã tồn tại | 1. Nhập username mới, email=qa@test.com 2. Nhấn Đăng ký | **Status:** `400` **Response:** `{"success":false,"message":"Email đã tồn tại"}` | ✅ Toast lỗi "Email đã tồn tại" | Negative |
| INT-AUTH-04 | Login thành công (ADMIN) | Có user admin trong DB | 1. Nhập username=admin, password đúng 2. Nhấn Đăng nhập | **URL:** `POST /api/auth/login` **Status:** `200` **Request:** `{"username":"admin","password":"..."}` **Response:** `{"success":true,"data":{"accessToken":"eyJ...","refreshToken":"uuid...","username":"admin","fullName":"...","roles":["ADMIN"]}}` | ✅ Redirect sang `/` (Dashboard) ✅ Sidebar hiện đầy đủ menu admin ✅ Header hiện tên user ✅ `localStorage.token` = accessToken ✅ `localStorage.user` có roles | Positive |
| INT-AUTH-05 | Login thành công (CITIZEN) | Có user citizen | 1. Login với citizen 2. Quan sát routing | **Status:** `200` **Response:** roles=["CITIZEN"] | ✅ Redirect sang `/portal` (KHÔNG phải `/dashboard`) ✅ Sidebar admin KHÔNG hiện ✅ Chỉ hiện Portal layout | Positive/Routing |
| INT-AUTH-06 | Login sai password | Có user admin | 1. Nhập username=admin, password=sai 2. Nhấn Đăng nhập | **Status:** `401` **Response:** `{"success":false,"message":"Sai tên đăng nhập hoặc mật khẩu"}` | ✅ Toast lỗi hiện message từ BE ✅ KHÔNG redirect ✅ Form giữ username, xóa password | Negative |
| INT-AUTH-07 | Login sai username | Không có user "ghost" | 1. Nhập username=ghost 2. Nhấn Đăng nhập | **Status:** `401` **Response message:** "Sai tên đăng nhập hoặc mật khẩu" (cùng message, không leak) | ✅ Cùng message lỗi với sai password ✅ Không tiết lộ user tồn tại hay không | Security |
| INT-AUTH-08 | JWT Token tự động gắn | Đã login thành công | 1. Mở DevTools → Network 2. Truy cập Dashboard 3. Xem requests | **Mọi request** phải có header: `Authorization: Bearer eyJhbGci...` Token khớp với `localStorage.getItem('token')` | ✅ Dashboard load data thành công ✅ Không bị 401 | Security |
| INT-AUTH-09 | Refresh token tự động | Token hết hạn, refreshToken còn hạn | 1. Sửa token trong localStorage thành invalid 2. Truy cập trang cần auth | **Request 1:** API bất kỳ → `401` **Request 2 (tự động):** `POST /api/auth/refresh` body: `{"refreshToken":"..."}` **Response:** `{"accessToken":"mới","refreshToken":"mới"}` **Request 3 (retry):** API ban đầu → `200` | ✅ User KHÔNG bị logout ✅ Trang load bình thường ✅ Token mới được lưu vào localStorage | Positive |
| INT-AUTH-10 | Refresh token hết hạn → logout | Cả accessToken và refreshToken không hợp lệ | 1. Sửa cả 2 token thành invalid 2. Truy cập trang auth | **Request:** API → 401 **Refresh:** `POST /api/auth/refresh` → `401 "Refresh token không hợp lệ"` | ✅ Tự redirect `/login` ✅ localStorage bị xóa sạch (token, user, refreshToken) ✅ Hiện trang đăng nhập | Security |

---

## MODULE 2: USER PROFILE (3 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-USER-01 | Cập nhật hồ sơ | Login bất kỳ role, vào trang Profile | 1. Sửa fullName, phone 2. Nhấn Lưu | **URL:** `PUT /api/auth/profile` **Status:** `200` **Request:** `{"fullName":"Tên mới","phone":"0901111222","address":"HCM"}` **Response:** `{"success":true,"data":{user object đã update}}` Header: `Authorization: Bearer ...` | ✅ Toast "Cập nhật thành công" ✅ Tên mới hiện trên header ✅ localStorage.user được cập nhật | Positive |
| INT-USER-02 | Đổi mật khẩu thành công | Login, vào Profile | 1. Nhập MK cũ đúng, MK mới 2. Nhấn Đổi | **URL:** `PUT /api/auth/change-password` **Status:** `200` **Request:** `{"currentPassword":"123456","newPassword":"654321"}` | ✅ Toast "Đổi mật khẩu thành công" ✅ Logout → login lại bằng MK mới → thành công | Positive |
| INT-USER-03 | Đổi MK — sai MK cũ | Login, vào Profile | 1. Nhập MK cũ SAI 2. Nhấn Đổi | **Status:** `400` **Response:** `{"success":false,"message":"Mật khẩu hiện tại không đúng"}` | ✅ Toast lỗi "Mật khẩu hiện tại không đúng" ✅ Form không reset | Negative |

---

## MODULE 3: HỒ SƠ HỖ TRỢ (12 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-APP-01 | Tạo hồ sơ mới | Login CITIZEN, có chương trình ACTIVE | 1. Portal → Nộp hồ sơ 2. Chọn chương trình, đối tượng 3. Nhấn Nộp | **URL:** `POST /api/applications` **Status:** `200` **Request:** `{"chuongTrinhTroCapId":"...","doiTuongHuongTroCapId":"...","ghiChu":"..."}` (camelCase) **Response:** `{"success":true,"data":{"id":"...","trangThai":"DRAFT"}}` | ✅ Toast thành công ✅ Redirect sang danh sách hồ sơ ✅ Hồ sơ mới hiện ở đầu danh sách với badge "Nháp" | Positive |
| INT-APP-02 | DS hồ sơ (OFFICER) | Login OFFICER, có ≥3 hồ sơ | 1. Vào menu Hồ sơ | **URL:** `GET /api/applications?page=0&size=20` **Status:** `200` **Response:** `{"success":true,"data":{"content":[...],"totalElements":3,"totalPages":1}}` | ✅ Bảng render 3 dòng ✅ Cột: Mã, Người nộp, Chương trình, Trạng thái, Ngày nộp ✅ Phân trang hiện "1/1" ✅ Trạng thái có badge màu tương ứng | Positive |
| INT-APP-03 | Hồ sơ của tôi (CITIZEN) | Login CITIZEN, có 2 hồ sơ của mình + hồ sơ người khác | 1. Portal → Hồ sơ của tôi | **URL:** `GET /api/applications/my?page=0&size=20` **Status:** `200` **Response:** content chỉ chứa hồ sơ của CITIZEN đang login | ✅ Chỉ hiện 2 hồ sơ (không hiện hồ sơ người khác) ✅ Không có cột "Người nộp" (vì đều là mình) | Security |
| INT-APP-04 | DS hồ sơ rỗng | Login user mới, chưa có hồ sơ | 1. Vào danh sách hồ sơ | **Status:** `200` **Response:** `{"content":[],"totalElements":0}` | ✅ Hiện empty state (icon + "Chưa có hồ sơ nào") ✅ KHÔNG hiện bảng trống ✅ KHÔNG hiện lỗi ✅ Có nút "Tạo hồ sơ mới" | Empty State |
| INT-APP-05 | Chi tiết hồ sơ | Có hồ sơ id=abc123 | 1. Click vào 1 hồ sơ | **URL:** `GET /api/applications/abc123` **Status:** `200` | ✅ Hiện đầy đủ: thông tin, trạng thái, tài liệu, timeline ✅ Nút action phụ thuộc role + trạng thái | Positive |
| INT-APP-06 | Thống kê hồ sơ | Login ADMIN | 1. Vào Dashboard | **URL:** `GET /api/applications/stats` **Status:** `200` **Response:** `{"data":{"total":10,"DRAFT":2,"SUBMITTED":3,"UNDER_REVIEW":1,"APPROVED":3,"REJECTED":1,"PAID":0}}` | ✅ Card thống kê hiện đúng số liệu ✅ Tổng = 10, từng trạng thái khớp | Positive |
| INT-APP-07 | Submit hồ sơ (DRAFT→SUBMITTED) | CITIZEN, hồ sơ DRAFT | 1. Mở chi tiết hồ sơ DRAFT 2. Nhấn "Nộp hồ sơ" | **URL:** `PATCH /api/applications/{id}/submit` **Status:** `200` **Response data:** `trangThai: "SUBMITTED"` | ✅ Badge đổi từ "Nháp" → "Đã nộp" ✅ Nút "Nộp" biến mất ✅ Toast thành công | Positive |
| INT-APP-08 | Approve hồ sơ | OFFICER, hồ sơ UNDER_REVIEW | 1. Mở hồ sơ UNDER_REVIEW 2. Nhấn Phê duyệt | **URL:** `PATCH /api/applications/{id}/approve` **Method phải là PATCH** (không phải PUT) **Status:** `200` | ✅ Badge "Đã duyệt" màu xanh ✅ Nút Approve/Reject biến mất ✅ Thông báo tự tạo cho CITIZEN | Positive |
| INT-APP-09 | Reject hồ sơ có lý do | OFFICER, hồ sơ UNDER_REVIEW | 1. Nhấn Từ chối 2. Modal mở → nhập "Thiếu CMND" 3. Xác nhận | **URL:** `PATCH /api/applications/{id}/reject` **Request:** `{"lyDoTuChoi":"Thiếu CMND"}` (camelCase, KHÔNG phải snake_case) **Status:** `200` | ✅ Badge "Từ chối" màu đỏ ✅ Lý do từ chối hiện trên chi tiết ✅ Modal đóng lại | Positive |
| INT-APP-10 | Reject thiếu lý do | OFFICER | 1. Nhấn Từ chối 2. Để trống lý do 3. Nhấn xác nhận | **Status:** `400` **Response:** `"Lý do từ chối không được để trống"` (nếu FE ko validate) Hoặc FE validate client-side: nút xác nhận bị disabled | ✅ Modal KHÔNG đóng ✅ Hiện validation error "Vui lòng nhập lý do" ✅ KHÔNG gửi request nếu FE validate | Negative |
| INT-APP-11 | Loading state | Bất kỳ | 1. DevTools → Network → Throttling: Slow 3G 2. Mở danh sách hồ sơ | Trong lúc chờ response (1-5 giây): | ✅ Hiện spinner hoặc skeleton ✅ KHÔNG hiện bảng rỗng ✅ KHÔNG hiện lỗi ✅ Sau khi response → loading biến mất, data render | UX |
| INT-APP-12 | CITIZEN truy cập admin route | Login CITIZEN | 1. Gõ URL trực tiếp: `/ho-so` 2. Enter | Không có API call (router guard chặn trước) | ✅ Redirect sang `/portal` ✅ KHÔNG hiện trang admin ✅ URL thay đổi thành `/portal` | Security |

---

## MODULE 4: TÀI LIỆU ĐÍNH KÈM (4 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-DOC-01 | Upload file PDF | Có hồ sơ, login owner | 1. Mở chi tiết hồ sơ 2. Chọn file CMND.pdf 3. Nhấn Upload | **URL:** `POST /api/applications/{id}/attachments` **Content-Type:** `multipart/form-data` (KHÔNG phải application/json) **Status:** `200` Header: `Authorization: Bearer ...` (vẫn phải có) | ✅ File mới xuất hiện trong danh sách tài liệu ✅ Hiện tên file, loại, ngày upload ✅ Toast thành công | Positive |
| INT-DOC-02 | Upload file sai định dạng | Có hồ sơ | 1. Chọn file virus.exe 2. Nhấn Upload | **Status:** `400` **Response:** `"Loại file không hợp lệ"` (hoặc FE validate trước: chỉ accept .pdf,.png,.jpg,.jpeg) | ✅ Toast lỗi "Chỉ chấp nhận PDF, PNG, JPG" ✅ File KHÔNG được upload | Negative |
| INT-DOC-03 | DS tài liệu | Hồ sơ có 2 tài liệu | 1. Mở chi tiết hồ sơ | **URL:** `GET /api/applications/{id}/attachments` **Status:** `200` **Response:** `[{tenTaiLieu, duongDanFile, loaiFile, ketQuaOcr}, ...]` | ✅ Hiện 2 file với icon loại file ✅ Có nút Download, Xóa ✅ Hiện kết quả OCR nếu có | Positive |
| INT-DOC-04 | Xóa tài liệu | Có tài liệu, login owner | 1. Nhấn Xóa bên cạnh file 2. Xác nhận dialog | **URL:** `DELETE /api/attachments/{docId}` **Status:** `200` | ✅ File biến mất khỏi danh sách (không reload trang) ✅ Toast "Đã xóa" ✅ Confirm dialog trước khi xóa | Positive |

---

## MODULE 5: CHI TRẢ / PAYMENT (5 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-PAY-01 | Tạo chi trả | Login ACCOUNTANT, hồ sơ APPROVED | 1. Vào Ngân sách 2. Tạo chi trả: chọn hồ sơ, nhập số tiền 3. Nhấn Tạo | **URL:** `POST /api/payments` **Status:** `200` **Request:** `{"hoSoHoTroId":"...","soTien":2000000,"phuongThuc":"CHUYEN_KHOAN"}` **Response:** `{"data":{"trangThai":"PENDING"}}` | ✅ Giao dịch mới hiện trong bảng ✅ Badge "Đang chờ" ✅ Ngân sách còn lại giảm tương ứng | Positive |
| INT-PAY-02 | Tạo chi trả — vượt ngân sách | Login ACCOUNTANT | 1. Nhập số tiền > ngân sách còn lại | **Status:** `400` **Response:** `"Ngân sách không đủ..."` | ✅ Toast lỗi hiện ngân sách còn lại ✅ KHÔNG tạo giao dịch | Negative |
| INT-PAY-03 | Cập nhật trạng thái chi trả | Có giao dịch PENDING | 1. Click cập nhật → chọn SUCCESS | **URL:** `PATCH /api/payments/{id}/status` **Method: PATCH** (không phải PUT) **Request:** `{"trangThai":"SUCCESS"}` **Status:** `200` | ✅ Badge đổi "Thành công" màu xanh ✅ Hồ sơ liên quan chuyển trạng thái PAID | Positive |
| INT-PAY-04 | DS chi trả phân trang | Login ACCOUNTANT, ≥25 giao dịch | 1. Vào trang chi trả | **URL:** `GET /api/payments?page=0&size=20` **Response:** `totalElements:25, totalPages:2` | ✅ Bảng hiện 20 dòng ✅ Phân trang hiện "1/2" ✅ Click trang 2 → load 5 dòng tiếp | Positive |
| INT-PAY-05 | CITIZEN truy cập payments API | Login CITIZEN | 1. Console: `fetch('/api/payments', {headers:{Authorization:'Bearer '+localStorage.token}})` | **Status:** `403 Forbidden` | ✅ FE không hiện menu chi trả cho CITIZEN ✅ API trả 403 nếu gọi trực tiếp | Security |

---

## MODULE 6: THÔNG BÁO (5 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-NTF-01 | DS thông báo | Login, có ≥3 thông báo (2 chưa đọc) | 1. Vào trang Thông báo | **URL:** `GET /api/notifications?page=0&size=20` **Status:** `200` **Response:** content có 3 thông báo, mỗi cái có `{tieuDe, noiDung, daDoc, createdAt}` | ✅ 3 thông báo render ✅ 2 chưa đọc hiện **in đậm** / background khác ✅ 1 đã đọc hiện màu nhạt hơn | Positive |
| INT-NTF-02 | Badge số chưa đọc | Login, 2 thông báo chưa đọc | 1. Quan sát sidebar/header | **URL:** `GET /api/notifications/unread-count` **Status:** `200` **Response:** `{"data":2}` | ✅ Badge đỏ hiện số "2" bên cạnh icon 🔔 ✅ Sau khi đọc hết → badge biến mất hoặc = 0 | Positive |
| INT-NTF-03 | Đánh dấu đã đọc | Có thông báo chưa đọc | 1. Click vào 1 thông báo chưa đọc | **URL:** `PATCH /api/notifications/{id}/read` **Method: PATCH** **Status:** `200` **Response:** `{"data":{"daDoc":true}}` | ✅ Thông báo đổi style (bỏ in đậm) ✅ Badge giảm 1 ✅ KHÔNG reload trang | Positive |
| INT-NTF-04 | Đọc tất cả | Có ≥2 thông báo chưa đọc | 1. Nhấn "Đánh dấu tất cả đã đọc" | **URL:** `PATCH /api/notifications/read-all` **Status:** `200` | ✅ Tất cả thông báo đổi style đã đọc ✅ Badge = 0 hoặc biến mất ✅ Toast "Đã đánh dấu tất cả" | Positive |
| INT-NTF-05 | Không có thông báo | Login user mới | 1. Vào trang thông báo | **Status:** `200` **Response:** `{"content":[],"totalElements":0}` | ✅ Hiện empty state: icon + "Chưa có thông báo nào" ✅ KHÔNG hiện trang trống ✅ KHÔNG hiện spinner vô hạn | Empty State |

---

## MODULE 7: ROLE-BASED ACCESS (5 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-RBAC-01 | ADMIN sidebar | Login ADMIN | 1. Kiểm tra sidebar menu | Không cần Network check (UI routing) | ✅ Hiện: Dashboard, Hồ sơ, Xét duyệt AI, Ngân sách, Báo cáo, Người dùng, Cài đặt, Đối tượng, Chương trình, Thông báo | Positive |
| INT-RBAC-02 | OFFICER sidebar | Login OFFICER | 1. Kiểm tra sidebar | — | ✅ Hiện: Dashboard, Hồ sơ, Xét duyệt AI, Báo cáo, Đối tượng, Nhật ký, Thông báo ✅ KHÔNG hiện: Ngân sách, Người dùng, Cài đặt | Positive |
| INT-RBAC-03 | ACCOUNTANT sidebar | Login ACCOUNTANT | 1. Kiểm tra sidebar | — | ✅ Hiện: Dashboard, Hồ sơ, Ngân sách, Báo cáo, Thông báo ✅ KHÔNG hiện: Xét duyệt AI, Người dùng, Cài đặt | Positive |
| INT-RBAC-04 | CITIZEN → Portal | Login CITIZEN | 1. Đăng nhập thành công | Không gọi admin API | ✅ Redirect `/portal` (không phải `/`) ✅ Layout Portal (không phải Admin) ✅ Menu: Trang chủ, Chương trình, Nộp hồ sơ, Hồ sơ của tôi, Thông báo, Cá nhân | Routing |
| INT-RBAC-05 | URL bypass: CITIZEN → /nguoi-dung | Login CITIZEN | 1. Gõ URL: `/nguoi-dung` (ADMIN only) 2. Enter | Không có API call `/users` | ✅ Redirect sang `/portal` ✅ URL đổi thành `/portal` ✅ KHÔNG hiện trang Quản lý User | Security |

---

## MODULE 8: ERROR HANDLING & UX (5 test cases)

| ID | Chức năng | Điều kiện tiên quyết | Bước thực hiện | Network Check | UI Check | Loại |
|---|---|---|---|---|---|---|
| INT-ERR-01 | Backend down | Tắt backend (Ctrl+C) | 1. Login hoặc gọi API bất kỳ | **Status:** Network Error (ERR_CONNECTION_REFUSED) **Không có response** | ✅ Toast "Không thể kết nối server" ✅ KHÔNG hiện trang trắng ✅ KHÔNG hiện stack trace | Negative |
| INT-ERR-02 | Request timeout | Backend delay > 15s hoặc DevTools → Offline | 1. Gọi API | **Status:** timeout (axios timeout: 15000ms) | ✅ Toast "Kết nối quá thời gian" ✅ Loading spinner biến mất ✅ Có nút "Thử lại" | Negative |
| INT-ERR-03 | 401 → Auto logout | Token hết hạn + refreshToken invalid | 1. Xóa refreshToken khỏi localStorage 2. Sửa token thành "invalid" 3. Mở trang bất kỳ | **Request 1:** API → `401` **Request 2:** `/api/auth/refresh` → `401` **Sau đó:** redirect | ✅ Redirect `/login` tự động ✅ localStorage sạch (token, user, refreshToken đều null) ✅ Không hiện trang trắng giữa chừng | Security |
| INT-ERR-04 | 403 Forbidden | Login OFFICER, gọi ADMIN API | 1. Console: `fetch('/api/users', {headers:{Authorization:'Bearer '+localStorage.token}}).then(r=>r.json()).then(console.log)` | **Status:** `403` **Response:** `{"success":false,"message":"..."}` | ✅ Nếu gọi từ UI, hiện "Bạn không có quyền" ✅ KHÔNG redirect login (vì đã auth, chỉ thiếu quyền) | Security |
| INT-ERR-05 | Loading consistency | Bất kỳ page | 1. DevTools → Network → Throttle Slow 3G 2. Navigate giữa các trang | Requests mất 2-5 giây | ✅ MỌI trang đều hiện loading khi đang fetch ✅ KHÔNG có trang nào hiện data cũ rồi "nhảy" sang data mới ✅ KHÔNG có flash of empty content | UX |

---

## TỔNG KẾT

| Module | Số TC | Positive | Negative | Security | Empty State | UX |
|---|---|---|---|---|---|---|
| Auth | 10 | 4 | 2 | 4 | 0 | 0 |
| User Profile | 3 | 2 | 1 | 0 | 0 | 0 |
| Hồ sơ | 12 | 6 | 1 | 2 | 1 | 2 |
| Tài liệu | 4 | 3 | 1 | 0 | 0 | 0 |
| Payments | 5 | 3 | 1 | 1 | 0 | 0 |
| Thông báo | 5 | 3 | 0 | 0 | 1 | 1 |
| RBAC | 5 | 3 | 0 | 2 | 0 | 0 |
| Error/UX | 5 | 0 | 2 | 2 | 0 | 1 |
| **TỔNG** | **49** | **24** | **8** | **11** | **2** | **4** |

---

## PHỤ LỤC A: CÁC LỖI FE-BE THƯỜNG GẶP

### 🔴 Nhóm 1: Sai Endpoint (HTTP 404)

| # | Lỗi | Mô tả | Cách phát hiện |
|---|---|---|---|
| 1 | FE gọi sai path | VD: FE `/disbursements` vs BE `/payments` | Network tab → 404 + URL |
| 2 | Path cũ sau refactor | VD: `/applications/mine` vs `/applications/my` | Grep `@RequestMapping` ở BE, đối chiếu FE |
| 3 | Thiếu prefix `/api` | FE gọi `/auth/login` thay vì `/api/auth/login` | Kiểm tra `baseURL` trong axios config |

### 🟠 Nhóm 2: Sai HTTP Method (HTTP 405)

| # | Lỗi | Mô tả | Cách phát hiện |
|---|---|---|---|
| 4 | PUT vs PATCH | FE `PUT /approve` nhưng BE `@PatchMapping` | Network tab → Method column |
| 5 | GET vs POST | FE dùng GET có body (browsers drop GET body) | Request payload trống |

### 🟡 Nhóm 3: Sai Format Request/Response

| # | Lỗi | Mô tả | Cách phát hiện |
|---|---|---|---|
| 6 | snake_case vs camelCase | FE `ly_do_tu_choi` vs BE `lyDoTuChoi` | Network Payload tab |
| 7 | Không unwrap ApiResponse | FE expect `res.data.name` nhưng thực tế `res.data.data.name` | Console.log response |
| 8 | JSON thay vì FormData | Upload file nhưng gửi JSON | Content-Type header |
| 9 | Pagination format | FE expect `items` vs BE trả `content` | Response tab |

### 🔵 Nhóm 4: Auth & Security

| # | Lỗi | Mô tả | Cách phát hiện |
|---|---|---|---|
| 10 | Thiếu Bearer token | Interceptor chưa gắn token | Request Headers |
| 11 | Refresh path sai | `/refresh-token` vs `/refresh` | 401 liên tục |
| 12 | CORS blocked | BE chưa allow FE origin | Console: `CORS policy` |
| 13 | Role format khác | FE check `ADMIN` vs BE dùng `ROLE_ADMIN` | Login response + SecurityConfig |

### ⚫ Nhóm 5: State & UX

| # | Lỗi | Mô tả | Cách phát hiện |
|---|---|---|---|
| 14 | Stale data | Approve → list vẫn hiện "Chờ" | Quan sát UI sau action |
| 15 | No loading | Click → chờ 3s → mới có phản hồi | Slow 3G test |
| 16 | No error toast | API 500 → trang trắng | Tắt backend |

---

## PHỤ LỤC B: KIỂM TRA NHANH BẰNG DEVTOOLS

### Bước 1: Mở DevTools
```
F12 → Tab Network → Filter: Fetch/XHR
```

### Bước 2: Checklist cho mỗi API call

| STT | Kiểm tra | Nơi xem | Giá trị đúng |
|---|---|---|---|
| 1 | URL | Headers → Request URL | `http://localhost:5173/api/...` (proxy) |
| 2 | Method | Headers → Request Method | `GET/POST/PUT/PATCH/DELETE` |
| 3 | Status | Cột Status | `200`, `201`, `400`, `401`, `403` |
| 4 | Auth header | Headers → Authorization | `Bearer eyJhbG...` |
| 5 | Content-Type | Headers | `application/json` hoặc `multipart/form-data` |
| 6 | Request body | Tab Payload | JSON đúng field, đúng case |
| 7 | Response body | Tab Response | `{"success":true,"data":{...}}` |
| 8 | Timing | Tab Timing | < 1s (CRUD), < 5s (upload) |

### Bước 3: Quick Test Script (Console)

```javascript
// ═══ Paste vào DevTools Console để test nhanh ═══

// 1. Kiểm tra auth state
console.table({
  token: localStorage.getItem('token') ? '✅' : '❌',
  user: JSON.parse(localStorage.getItem('user'))?.username || '❌',
  roles: JSON.parse(localStorage.getItem('user'))?.roles?.join(', ') || '❌',
  refreshToken: localStorage.getItem('refreshToken') ? '✅' : '❌'
})

// 2. Test từng endpoint
const h = { 'Authorization': 'Bearer ' + localStorage.getItem('token') }

// Auth
fetch('/api/auth/register', {method:'POST', headers:{...h,'Content-Type':'application/json'}, body: JSON.stringify({username:'qtest'+Date.now(),password:'123456',email:'q'+Date.now()+'@t.com'})}).then(r=>r.json()).then(d=>console.log('Register:', d.success ? '✅' : '❌', d))

// Applications
fetch('/api/applications?page=0&size=5', {headers: h}).then(r => r.json()).then(d => console.log('Apps:', d.success ? '✅' : '❌', 'count:', d.data?.totalElements))

// Stats
fetch('/api/applications/stats', {headers: h}).then(r => r.json()).then(d => console.log('Stats:', d.success ? '✅' : '❌', d.data))

// Notifications
fetch('/api/notifications/unread-count', {headers: h}).then(r => r.json()).then(d => console.log('Unread:', d.success ? '✅' : '❌', d.data))

// Payments
fetch('/api/payments?page=0&size=5', {headers: h}).then(r => r.json()).then(d => console.log('Payments:', d.success ? '✅' : '❌', d))

// Programs
fetch('/api/programs', {headers: h}).then(r => r.json()).then(d => console.log('Programs:', d.success ? '✅' : '❌', d))

// Funding sources
fetch('/api/funding-sources', {headers: h}).then(r => r.json()).then(d => console.log('Funds:', d.success ? '✅' : '❌', d))

// Beneficiaries
fetch('/api/beneficiaries', {headers: h}).then(r => r.json()).then(d => console.log('Beneficiaries:', d.success ? '✅' : '❌', d))

// 3. Test endpoint không tồn tại (expect 404)
fetch('/api/wrong', {headers: h}).then(r => console.log('Wrong path:', r.status === 404 ? '✅ 404' : '❌ ' + r.status))

// 4. Test không có token (expect 401)
fetch('/api/applications').then(r => console.log('No-auth:', r.status === 401 ? '✅ 401' : '❌ ' + r.status))
```

---

## PHỤ LỤC C: ĐỐI CHIẾU REQUEST/RESPONSE FE ↔ BE

### Ma trận đối chiếu toàn bộ 29 endpoints

| # | FE File | Method | FE Path | BE Controller | BE Annotation | Status |
|---|---|---|---|---|---|---|
| 1 | auth.js | POST | /auth/login | AuthController | @PostMapping("/login") | ✅ |
| 2 | auth.js | POST | /auth/register | AuthController | @PostMapping("/register") | ✅ |
| 3 | auth.js | POST | /auth/refresh | AuthController | @PostMapping("/refresh") | ✅ |
| 4 | auth.js | PUT | /auth/profile | AuthController | @PutMapping("/profile") | ✅ |
| 5 | auth.js | PUT | /auth/change-password | AuthController | @PutMapping("/change-password") | ✅ |
| 6 | applications.js | GET | /applications | HoSoController | @GetMapping | ✅ |
| 7 | applications.js | GET | /applications/my | HoSoController | @GetMapping("/my") | ✅ |
| 8 | applications.js | GET | /applications/stats | HoSoController | @GetMapping("/stats") | ✅ |
| 9 | applications.js | GET | /applications/{id} | HoSoController | @GetMapping("/{id}") | ✅ |
| 10 | applications.js | POST | /applications | HoSoController | @PostMapping | ✅ |
| 11 | applications.js | PUT | /applications/{id} | HoSoController | @PutMapping("/{id}") | ✅ |
| 12 | applications.js | DELETE | /applications/{id} | HoSoController | @DeleteMapping("/{id}") | ✅ |
| 13 | applications.js | PATCH | /applications/{id}/submit | HoSoController | @PatchMapping("/{id}/submit") | ✅ |
| 14 | applications.js | PATCH | /applications/{id}/approve | HoSoController | @PatchMapping("/{id}/approve") | ✅ |
| 15 | applications.js | PATCH | /applications/{id}/reject | HoSoController | @PatchMapping("/{id}/reject") | ✅ |
| 16 | applications.js | GET | /applications/{id}/attachments | TaiLieuController | @GetMapping | ✅ |
| 17 | applications.js | POST | /applications/{id}/attachments | TaiLieuController | @PostMapping(multipart) | ✅ |
| 18 | applications.js | DELETE | /attachments/{id} | TaiLieuController | @DeleteMapping | ✅ |
| 19 | transactions.js | GET | /payments | ChiTraController | @GetMapping | ✅ |
| 20 | transactions.js | GET | /payments/{id} | ChiTraController | @GetMapping("/{id}") | ✅ |
| 21 | transactions.js | POST | /payments | ChiTraController | @PostMapping | ✅ |
| 22 | transactions.js | PATCH | /payments/{id}/status | ChiTraController | @PatchMapping("/{id}/status") | ✅ |
| 23 | programs.js | GET | /programs | ChuongTrinhController | @GetMapping | ✅ |
| 24 | programs.js | GET | /funding-sources | NguonQuyController | @GetMapping | ✅ |
| 25 | programs.js | GET | /beneficiaries | DoiTuongController | @GetMapping | ✅ |
| 26 | notifications.js | GET | /notifications | ThongBaoController | @GetMapping | ✅ |
| 27 | notifications.js | GET | /notifications/unread-count | ThongBaoController | @GetMapping("/unread-count") | ✅ |
| 28 | notifications.js | PATCH | /notifications/{id}/read | ThongBaoController | @PatchMapping("/{id}/read") | ✅ |
| 29 | notifications.js | PATCH | /notifications/read-all | ThongBaoController | @PatchMapping("/read-all") | ✅ |

**Kết quả: 29/29 endpoints đã khớp ✅**
