<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.4-6DB33F?logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Vue.js-3.5-4FC08D?logo=vuedotjs&logoColor=white" />
  <img src="https://img.shields.io/badge/MongoDB-7.0-47A248?logo=mongodb&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/License-MIT-blue" />
</p>

# 🏛️ Hệ Thống Quản Lý Trợ Cấp Xã Hội

Hệ thống quản lý trợ cấp & hỗ trợ xã hội dành cho cơ quan nhà nước. Hỗ trợ toàn bộ quy trình từ tiếp nhận hồ sơ, xét duyệt (có AI đánh giá), đến chi trả và báo cáo thống kê.

---

## 📋 Mục Lục

- [Tính Năng](#-tính-năng)
- [Kiến Trúc Hệ Thống](#-kiến-trúc-hệ-thống)
- [Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
- [Yêu Cầu Hệ Thống](#-yêu-cầu-hệ-thống)
- [Hướng Dẫn Cài Đặt](#-hướng-dẫn-cài-đặt)
- [Chạy Dự Án](#-chạy-dự-án)
- [Tài Khoản Demo](#-tài-khoản-demo)
- [API Endpoints](#-api-endpoints)
- [Screenshots](#-screenshots)
- [Công Nghệ Sử Dụng](#-công-nghệ-sử-dụng)

---

## ✨ Tính Năng

### 🔐 Phân quyền & Bảo mật
- Xác thực JWT (Access Token + Refresh Token)
- 4 vai trò: **Admin**, **Officer** (cán bộ), **Accountant** (kế toán), **Citizen** (công dân)
- Phân quyền RBAC trên từng API endpoint

### 📊 Dashboard thông minh
- Thống kê tổng quan: chương trình, hồ sơ, đối tượng, ngân sách
- Biểu đồ trực quan với Chart.js
- Hồ sơ mới nhất & thông báo real-time

### 📋 Quản lý chương trình trợ cấp
- CRUD chương trình với ngân sách, thời hạn
- Liên kết nguồn quỹ
- Theo dõi tiến độ giải ngân

### 📁 Quản lý hồ sơ hỗ trợ
- Tiếp nhận đơn từ công dân
- Upload tài liệu đính kèm (CMND, hộ khẩu, ...)
- Chuyển trạng thái: Nháp → Đã nộp → Đang xét → Duyệt/Từ chối
- Phân trang, lọc nâng cao, tìm kiếm

### 🤖 Đánh giá AI
- Chấm điểm ưu tiên tự động
- Đánh giá độ tin cậy hồ sơ
- Đề xuất duyệt/từ chối kèm nhận xét chi tiết
- Phân tích OCR tài liệu

### 💰 Chi trả & Ngân sách
- Tạo giao dịch chi trả (chuyển khoản/tiền mặt/ATM)
- Quản lý nguồn quỹ với cảnh báo sắp cạn
- Biểu đồ phân bổ ngân sách dạng donut

### 📥 Xuất báo cáo Excel
- Xuất file `.xlsx` chuyên nghiệp với Apache POI
- 4 loại báo cáo: Đối tượng, Chương trình, Hồ sơ, Chi trả
- Styling cao cấp: branding, alternating rows, status badges, auto-filter

### 🔔 Thông báo hệ thống
- Thông báo theo loại: Success, Warning, Error, Info
- Đánh dấu đã đọc / đọc tất cả

### 🌐 Cổng công dân (Portal)
- Giao diện riêng cho công dân đăng ký hỗ trợ
- Theo dõi trạng thái hồ sơ
- Nhận thông báo kết quả

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────┐         ┌─────────────────┐        ┌──────────────┐
│   Vue.js SPA    │◄──API──►│  Spring Boot    │◄──────►│   MongoDB    │
│   (Port 5175)   │         │  (Port 8080)    │        │  (Port 27017)│
├─────────────────┤         ├─────────────────┤        └──────────────┘
│ • Vue Router    │         │ • REST API      │
│ • Axios + JWT   │         │ • JWT Auth      │
│ • TailwindCSS   │         │ • RBAC Security │
│ • Chart.js      │         │ • Apache POI    │
│ • Vite          │         │ • Spring Data   │
└─────────────────┘         └─────────────────┘
```

---

## 📂 Cấu Trúc Dự Án

```
Hệ Thống Trợ Cấp/
├── backend/                          # Java Spring Boot API
│   └── src/main/java/com/trocap/
│       ├── TroCapApplication.java    # Main entry point
│       ├── auth/                     # Đăng nhập, đăng ký, JWT
│       ├── nguoidung/                # Quản lý người dùng
│       ├── chuongtrinh/              # Chương trình trợ cấp
│       ├── doituong/                 # Đối tượng hưởng trợ cấp
│       ├── hoso/                     # Hồ sơ hỗ trợ
│       ├── chitra/                   # Chi trả trợ cấp
│       ├── nguonquy/                 # Nguồn quỹ / ngân sách
│       ├── danhgiaai/                # Đánh giá AI
│       ├── tailieu/                  # Tài liệu đính kèm
│       ├── thongbao/                 # Thông báo
│       ├── dashboard/                # Dashboard stats
│       ├── auditlog/                 # Nhật ký hoạt động
│       ├── export/                   # Xuất báo cáo Excel
│       ├── config/                   # Security, CORS, JWT, DataSeeder
│       └── common/                   # DTOs, Utils chung
│
├── frontend/                         # Vue.js SPA
│   └── src/
│       ├── api/                      # Axios services (auth, programs, ...)
│       ├── views/                    # Các trang chính
│       │   ├── LoginView.vue         # Đăng nhập
│       │   ├── DashboardView.vue     # Trang chủ admin
│       │   ├── ProgramsView.vue      # Chương trình
│       │   ├── BeneficiariesView.vue # Hồ sơ hỗ trợ
│       │   ├── BudgetView.vue        # Ngân sách
│       │   ├── ReportsView.vue       # Báo cáo thống kê
│       │   ├── AiReviewView.vue      # Xét duyệt AI
│       │   ├── UsersView.vue         # Quản lý users
│       │   └── portal/               # Cổng công dân
│       ├── stores/                   # Auth store, UI store
│       ├── router/                   # Vue Router
│       └── components/               # Components tái sử dụng
│
├── docs/                             # Tài liệu test, kết quả
├── start-backend.bat                 # Script khởi chạy backend
└── start-frontend.bat                # Script khởi chạy frontend
```

---

## 💻 Yêu Cầu Hệ Thống

| Thành phần | Phiên bản tối thiểu | Ghi chú |
|------------|---------------------|---------|
| **Java JDK** | 25+ | Cần JDK, không chỉ JRE |
| **Node.js** | 18+ | Cần npm đi kèm |
| **MongoDB** | 6.0+ | Chạy local hoặc Atlas |
| **Git** | 2.x | Clone dự án |

> **Lưu ý:** Không cần cài Maven riêng. Dự án sử dụng **Maven Wrapper** (`mvnw.cmd`) — sẽ tự động tải Maven khi chạy lần đầu.

---

## 🚀 Hướng Dẫn Cài Đặt

### Bước 1: Clone dự án

```bash
git clone <repository-url>
cd "Hệ Thống Trợ Cấp"
```

### Bước 2: Cài đặt MongoDB

**Windows:**
- Tải MongoDB Community Server từ https://www.mongodb.com/try/download/community
- Cài đặt và chạy MongoDB service (mặc định port `27017`)
- Hoặc dùng MongoDB Atlas (cloud): tạo cluster miễn phí tại https://cloud.mongodb.com

**Kiểm tra MongoDB đang chạy:**
```bash
mongosh --eval "db.version()"
```

### Bước 3: Cài đặt dependencies Frontend

```bash
cd frontend
npm install
cd ..
```

### Bước 4: Cấu hình Backend (tùy chọn)

File cấu hình: `backend/src/main/resources/application.properties`

```properties
# MongoDB connection (mặc định: localhost:27017)
spring.data.mongodb.uri=mongodb://localhost:27017/tro_cap_db

# JWT Secret (thay đổi trong production!)
jwt.secret=your-secret-key-here
jwt.expiration=86400000

# Server port
server.port=8080
```

> **Lưu ý:** Mặc định backend sẽ kết nối MongoDB tại `localhost:27017`. Không cần thay đổi nếu chạy local.

---

## ▶️ Chạy Dự Án

### Cách 1: Dùng script (Khuyến nghị — Windows)

**Terminal 1 — Backend:**
```bash
start-backend.bat
```

**Terminal 2 — Frontend:**
```bash
start-frontend.bat
```

### Cách 2: Chạy thủ công

**Terminal 1 — Backend:**
```bash
cd backend
.\mvnw.cmd clean package -DskipTests
java -jar target\tro-cap-backend-1.0.0.jar
```

**Terminal 2 — Frontend:**
```bash
cd frontend
npm run dev
```

### Truy cập hệ thống

| Giao diện | URL | Mô tả |
|-----------|-----|--------|
| 🖥️ **Admin Panel** | http://localhost:5175/login | Dành cho Admin, Officer, Accountant |
| 🌐 **Cổng Công dân** | http://localhost:5175/portal | Dành cho Citizen |
| 📚 **Swagger API Docs** | http://localhost:8080/swagger-ui.html | API Documentation |

---

## 👤 Tài Khoản Demo

Hệ thống tự động tạo 4 tài khoản mặc định khi khởi động (DataSeeder). Password được reset về mặc định mỗi lần restart.

| Username | Password | Vai trò | Quyền hạn |
|----------|----------|---------|-----------|
| `admin` | `123456` | **ADMIN** | Toàn quyền: quản lý users, chương trình, hồ sơ, chi trả, xét duyệt, xuất báo cáo |
| `officer1` | `123456` | **OFFICER** | Xét duyệt hồ sơ, quản lý chương trình, xuất báo cáo |
| `accountant1` | `123456` | **ACCOUNTANT** | Quản lý chi trả, ngân sách, xuất báo cáo |
| `citizen1` | `123456` | **CITIZEN** | Nộp hồ sơ, theo dõi trạng thái, xem thông báo |

### Phân quyền chi tiết

```
ADMIN
├── Quản lý tất cả users
├── CRUD chương trình trợ cấp
├── Xét duyệt / từ chối hồ sơ
├── Tạo chi trả
├── Xuất báo cáo Excel
├── Xem dashboard & audit log
└── Quản lý thông báo

OFFICER
├── Xét duyệt / từ chối hồ sơ
├── Quản lý chương trình
├── Xuất báo cáo Excel
└── Xem dashboard

ACCOUNTANT
├── Tạo / quản lý chi trả
├── Quản lý nguồn quỹ
├── Xuất báo cáo Excel
└── Xem dashboard

CITIZEN
├── Đăng ký hồ sơ hỗ trợ
├── Upload tài liệu
├── Theo dõi trạng thái hồ sơ
└── Xem thông báo cá nhân
```

---

## 🔗 API Endpoints

> **Giải thích cột "Xác thực":**
> - 🔓 **Công khai** = Không cần đăng nhập, ai cũng gọi được
> - 🔑 **Cần đăng nhập** = Phải gửi JWT token trong header `Authorization: Bearer <token>`
> - 🔒 **Chỉ Admin** = Cần đăng nhập VÀ phải có role ADMIN

### Authentication
| Method | Endpoint | Mô tả | Xác thực |
|--------|----------|-------|----------|
| POST | `/api/auth/login` | Đăng nhập (trả về JWT token) | 🔓 Công khai |
| POST | `/api/auth/register` | Đăng ký tài khoản mới | 🔓 Công khai |
| POST | `/api/auth/refresh` | Làm mới token hết hạn | 🔓 Công khai |
| PUT | `/api/auth/profile` | Cập nhật thông tin cá nhân | 🔑 Cần đăng nhập |
| PUT | `/api/auth/change-password` | Đổi mật khẩu | 🔑 Cần đăng nhập |

### Nghiệp vụ
| Method | Endpoint | Mô tả | Xác thực |
|--------|----------|-------|----------|
| GET | `/api/dashboard/stats` | Thống kê dashboard | 🔑 Cần đăng nhập |
| GET | `/api/programs` | Danh sách chương trình | 🔑 Cần đăng nhập |
| GET | `/api/beneficiaries` | Danh sách đối tượng | 🔑 Cần đăng nhập |
| GET | `/api/applications` | Danh sách hồ sơ | 🔑 Cần đăng nhập |
| GET | `/api/payments` | Danh sách chi trả | 🔑 Cần đăng nhập |
| GET | `/api/notifications` | Thông báo | 🔑 Cần đăng nhập |
| GET | `/api/funding-sources` | Nguồn quỹ | 🔑 Cần đăng nhập |
| GET | `/api/users` | Quản lý users | 🔒 Chỉ ADMIN |
| GET | `/api/audit-logs` | Nhật ký hoạt động | 🔒 Chỉ ADMIN |
| GET | `/api/ai-reviews` | Đánh giá AI | 🔑 Cần đăng nhập |

### Xuất báo cáo Excel
| Method | Endpoint | Mô tả | Xác thực |
|--------|----------|-------|----------|
| GET | `/api/export/beneficiaries` | Xuất danh sách đối tượng | 🔒 ADMIN/OFFICER |
| GET | `/api/export/programs` | Xuất chương trình | 🔒 ADMIN/OFFICER |
| GET | `/api/export/applications` | Xuất hồ sơ | 🔒 ADMIN/OFFICER |
| GET | `/api/export/payments` | Xuất chi trả | 🔒 ADMIN/OFFICER |

---

## 🛠️ Công Nghệ Sử Dụng

### Backend
| Công nghệ | Mục đích |
|-----------|----------|
| Java 25 | Ngôn ngữ chính |
| Spring Boot 3.4.4 | Framework backend |
| Spring Security | Bảo mật, JWT |
| Spring Data MongoDB | ORM cho MongoDB |
| Apache POI | Xuất Excel (.xlsx) |
| jjwt 0.12.6 | JWT token |
| Lombok | Giảm boilerplate code |
| Swagger/OpenAPI | API documentation |

### Frontend
| Công nghệ | Mục đích |
|-----------|----------|
| Vue.js 3.5 | Framework frontend |
| Vite 6.2 | Build tool |
| Vue Router 4.5 | Routing SPA |
| Axios 1.13 | HTTP client |
| TailwindCSS 3.4 | CSS framework |
| Chart.js + vue-chartjs | Biểu đồ |
| Google Material Symbols | Icons |

### Database & Infra
| Công nghệ | Mục đích |
|-----------|----------|
| MongoDB 7.0 | NoSQL Database |
| BCrypt | Hash password |

---

## ⚠️ Lưu Ý Quan Trọng

### Đường dẫn tiếng Việt (Windows)
Nếu thư mục dự án chứa ký tự tiếng Việt (VD: `Hệ Thống Trợ Cấp`), **bắt buộc** sử dụng `start-backend.bat` để chạy backend. Lệnh `mvn spring-boot:run` sẽ gây lỗi `ClassNotFoundException` do đường dẫn Unicode.

### Build lỗi
Nếu build backend thất bại:
1. Đảm bảo Java 25+ đã cài: `java --version`
2. Xóa cache: `cd backend && .\mvnw.cmd clean`
3. Kiểm tra MongoDB đang chạy
4. Không cần cài Maven — dự án dùng Maven Wrapper (`mvnw.cmd`)

### Dữ liệu mẫu
Khi khởi động lần đầu, hệ thống tự tạo:
- 4 tài khoản demo (password luôn reset về `123456`)
- 4 chương trình trợ cấp
- 20 đối tượng hưởng trợ cấp
- 5 hồ sơ hỗ trợ
- 5 giao dịch chi trả
- 6 thông báo
- 5 audit logs

---

## 📄 License

MIT License — Sử dụng tự do cho mục đích học tập và nghiên cứu.

---

<p align="center">
  <b>Hệ thống Quản lý Trợ cấp & Hỗ trợ Xã hội</b><br/>
  <sub>Được xây dựng với ❤️ bằng Java Spring Boot + Vue.js + MongoDB</sub>
</p>
