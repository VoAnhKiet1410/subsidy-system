# KẾT QUẢ CHẠY INTEGRATION TEST THỰC TẾ
## Ngày: 24/03/2026 | Build: Clean rebuild + jar

---

## Kết quả: 19/19 PASS ✅

| # | Test ID | Chức năng | Endpoint | Status | Kết quả | Chi tiết |
|---|---|---|---|---|---|---|
| 1 | AUTH-01 | Register | `POST /api/auth/register` | `200` | ✅ PASS | user=int_1774328825052 |
| 2 | AUTH-04 | Login | `POST /api/auth/login` | `200` | ✅ PASS | tokenLen=216 |
| 3 | AUTH-JWT | JWT roles claim | — | — | ✅ PASS | roles=CITIZEN trong JWT payload |
| 4 | APP-02 | Danh sách hồ sơ | `GET /api/applications` | `200` | ✅ PASS | totalElements=0 (user mới, chưa có hồ sơ) |
| 5 | APP-06 | Thống kê hồ sơ | `GET /api/applications/stats` | `200` | ✅ PASS | total=5, DRAFT=0, SUBMITTED=0 |
| 6 | PROG-01 | Danh sách chương trình | `GET /api/programs` | `200` | ✅ PASS | count=4 |
| 7 | BEN-01 | Danh sách đối tượng | `GET /api/beneficiaries` | `200` | ✅ PASS | count=20 |
| 8 | PAY-04 | Payments (CITIZEN) | `GET /api/payments` | `403` | ✅ PASS (RBAC) | CITIZEN bị chặn truy cập payments → đúng thiết kế |
| 9 | NTF-01 | Danh sách thông báo | `GET /api/notifications` | `200` | ✅ PASS | count=0 (user mới) |
| 10 | NTF-02 | Đếm chưa đọc | `GET /api/notifications/unread-count` | `200` | ✅ PASS | count=0 |
| 11 | NTF-04 | Đọc tất cả | `PATCH /api/notifications/read-all` | `200` | ✅ PASS | "Đánh dấu tất cả đã đọc" |
| 12 | USER-01 | Cập nhật profile | `PUT /api/auth/profile` | `200` | ✅ PASS | name=Updated Name |
| 13 | USER-03 | Đổi MK sai MK cũ | `PUT /api/auth/change-password` | `400` | ✅ PASS | BE reject đúng |
| 14 | AUTH-06 | Login sai password | `POST /api/auth/login` | `401` | ✅ PASS | Trả 401 đúng |
| 15 | AUTH-10 | Không có token | `GET /api/applications` | `401` | ✅ PASS | Trả 401 đúng |
| 16 | AUTH-02 | Register trùng username | `POST /api/auth/register` | `400` | ✅ PASS | Trả 400 đúng |
| 17 | FUND-01 | Nguồn quỹ | `GET /api/funding-sources` | `200` | ✅ PASS | Response received |
| 18 | USERS-01 | Users (CITIZEN) | `GET /api/users` | `403` | ✅ PASS (RBAC) | CITIZEN bị chặn → đúng thiết kế |
| 19 | PROXY-01 | Vite proxy FE→BE | `POST /api/auth/login` via :5175 | `200` | ✅ PASS | Proxy forward thành công |

---

## Phân tích

### Auth & JWT
- ✅ Register, Login, JWT generation hoạt động
- ✅ JWT payload có `roles` claim → RBAC hoạt động
- ✅ Token 216 chars (HS512, bao gồm roles)
- ✅ Profile update + Password change validation

### RBAC
- ✅ CITIZEN truy cập payments → `403 Forbidden` (đúng)
- ✅ CITIZEN truy cập users → `403 Forbidden` (đúng)
- ✅ CITIZEN truy cập applications, programs, beneficiaries → `200 OK`

### Data Layer
- ✅ MongoDB connected, seed data có sẵn (5 hồ sơ, 4 chương trình, 20 đối tượng)
- ✅ Pagination hoạt động (page=0&size=5)

### Proxy (FE → BE)
- ✅ Vite dev server proxy `/api` → `localhost:8080` hoạt động

### Mới thêm (endpoints tạo trong phiên này)
- ✅ `GET /api/applications/stats` — trả đúng object thống kê
- ✅ `PATCH /api/notifications/read-all` — trả thành công
- ✅ `PUT /api/auth/profile` — cập nhật profile thành công
- ✅ `PUT /api/auth/change-password` — validation mật khẩu cũ hoạt động

---

## Lưu ý
1. **Seed password không phải `123456`**: Dữ liệu trong DB đã bị thay đổi, admin password = `admin123`. DataSeeder ghi `123456` nhưng seed đã skip vì DB đã có dữ liệu.
2. **Vietnamese path issue**: Java 25 không xử lý tốt đường dẫn tiếng Việt khi chạy `mvn spring-boot:run`. Workaround: build jar rồi chạy bằng short path.
