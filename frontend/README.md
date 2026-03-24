# 🏛️ Hệ Thống Quản Lý Trợ Cấp Xã Hội

> Ứng dụng web quản lý hồ sơ trợ cấp xã hội, tích hợp AI đánh giá tự động, hỗ trợ OCR tài liệu và quản lý ngân sách hỗ trợ.

![Vue.js](https://img.shields.io/badge/Vue.js-3.5-4FC08D?logo=vuedotjs&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-6.2-646CFF?logo=vite&logoColor=white)
![TailwindCSS](https://img.shields.io/badge/Tailwind-3.4-06B6D4?logo=tailwindcss&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-4.5-FF6384?logo=chartdotjs&logoColor=white)

---

## 📋 Mục Lục

- [Giới Thiệu](#-giới-thiệu)
- [Tính Năng](#-tính-năng)
- [Tech Stack](#-tech-stack)
- [Yêu Cầu Hệ Thống](#-yêu-cầu-hệ-thống)
- [Cài Đặt & Khởi Chạy](#-cài-đặt--khởi-chạy)
- [Tài Khoản Demo](#-tài-khoản-demo)
- [Cấu Trúc Dự Án](#-cấu-trúc-dự-án)
- [Các Trang Chính](#-các-trang-chính)
- [Cấu Hình](#-cấu-hình)
- [Tác Giả](#-tác-giả)

---

## 🎯 Giới Thiệu

**Hệ Thống Quản Lý Trợ Cấp Xã Hội** là ứng dụng web frontend được xây dựng bằng Vue.js 3, phục vụ cho việc:

- **Cán bộ xã/phường**: Quản lý hồ sơ, xét duyệt bằng AI, quản lý ngân sách
- **Người dân**: Tra cứu chương trình, nộp hồ sơ online, theo dõi tiến độ

Hệ thống chia thành 2 giao diện:
1. **Admin Panel** — Dành cho cán bộ quản lý (ADMIN, REVIEWER, FINANCE)
2. **Portal** — Dành cho người dân (VIEWER)

---

## ✨ Tính Năng

### 🔐 Xác Thực & Phân Quyền
- Đăng nhập / Đăng ký với validation realtime
- 4 vai trò: **ADMIN**, **REVIEWER**, **FINANCE**, **VIEWER**
- Route guard tự động chuyển hướng theo vai trò
- JWT token tự động refresh

### 📊 Dashboard (Admin)
- Thống kê tổng quan (hồ sơ, ngân sách, chương trình)
- Biểu đồ Doughnut phân bổ trạng thái hồ sơ
- Biểu đồ Line xu hướng 6 tháng
- Danh sách hồ sơ gần đây

### 📁 Quản Lý Hồ Sơ
- Bảng dữ liệu với tìm kiếm, lọc đa tiêu chí, phân trang
- ⭐ Hồ sơ chưa duyệt (PENDING) ưu tiên lên đầu với sao vàng
- Chi tiết hồ sơ: timeline xử lý, tài liệu, kết quả AI
- Bộ lọc theo: trạng thái, chương trình, đối tượng, điểm AI, ngày nộp

### 🤖 AI Review Engine
- Phân tích AI tự động với điểm ưu tiên + độ tin cậy
- Điểm tin cậy tương thích với điểm AI (dao động ±8%)
- OCR tài liệu: trích xuất tự động thông tin từ CCCD, hộ khẩu
- Duyệt thủ công: modal phê duyệt/từ chối với ghi chú
- Yêu cầu bổ sung: chọn loại tài liệu cần bổ sung
- Chạy AI hàng loạt: progress bar realtime

### 💰 Quản Lý Ngân Sách
- Tổng quan nguồn quỹ, đã chi, còn lại
- Danh sách giao dịch chi trả
- Biểu đồ phân bổ ngân sách

### 🌐 Portal (Người Dân)
- Tra cứu chương trình trợ cấp đang mở
- Nộp hồ sơ online: validation, upload file, kéo thả
- Theo dõi tiến độ xử lý hồ sơ
- Nhận thông báo trạng thái
- Chỉnh sửa thông tin cá nhân

### 🔔 Khác
- Hệ thống thông báo với dropdown + trang chi tiết
- Nhật ký hoạt động hệ thống (Activity Log)
- Cài đặt & phân quyền
- Skeleton loading, toast notification
- Responsive design

---

## 🛠️ Tech Stack

| Công nghệ | Phiên bản | Mục đích |
|---|---|---|
| [Vue.js 3](https://vuejs.org/) | 3.5 | Framework SPA (Composition API) |
| [Vue Router](https://router.vuejs.org/) | 4.5 | Điều hướng SPA + Route guard |
| [Pinia](https://pinia.vuejs.org/) | — | State management (authStore, uiStore) |
| [Vite](https://vitejs.dev/) | 6.2 | Build tool + Dev server |
| [Tailwind CSS](https://tailwindcss.com/) | 3.4 | Utility-first CSS framework |
| [Axios](https://axios-http.com/) | 1.13 | HTTP client + Interceptors |
| [Chart.js](https://www.chartjs.org/) | 4.5 | Biểu đồ thống kê |
| [vue-chartjs](https://vue-chartjs.org/) | 5.3 | Vue wrapper cho Chart.js |
| [Material Symbols](https://fonts.google.com/icons) | — | Icon system |

---

## 💻 Yêu Cầu Hệ Thống

- **Node.js** >= 18.x
- **npm** >= 9.x (hoặc **yarn** >= 1.22)
- **Trình duyệt**: Chrome 90+, Firefox 90+, Edge 90+, Safari 15+

---

## 🚀 Cài Đặt & Khởi Chạy

### 1. Clone Repository

```bash
git clone https://github.com/VoAnhKiet1410/subsidy-system.git
cd subsidy-system
```

### 2. Cài Đặt Dependencies

```bash
npm install
```

### 3. Chạy Development Server

```bash
npm run dev
```

Ứng dụng sẽ khởi chạy tại: **http://localhost:5175**

### 4. Build Production

```bash
npm run build
```

File build sẽ được tạo trong thư mục `dist/`.

### 5. Preview Production Build

```bash
npm run preview
```

---

## 👤 Tài Khoản Demo

Ứng dụng có hệ thống mock authentication. Sử dụng các tài khoản sau để đăng nhập:

| Vai trò | Username | Password | Mô tả |
|---|---|---|---|
| **Admin** | `admin` | `Admin123!` | Toàn quyền quản lý hệ thống |
| **Reviewer** | `reviewer` | `Admin123!` | Xét duyệt hồ sơ + AI Review |
| **Finance** | `finance` | `Admin123!` | Quản lý ngân sách + chi trả |
| **Viewer** | `viewer` | `Admin123!` | Người dân — Portal |

> **Lưu ý**: Mỗi vai trò sẽ thấy giao diện khác nhau tùy theo phân quyền.

---

## 📂 Cấu Trúc Dự Án

```
frontend/
├── index.html                  # Entry HTML
├── package.json                # Dependencies & scripts
├── vite.config.js              # Vite config (proxy, port)
├── tailwind.config.js          # Tailwind CSS config
├── postcss.config.js           # PostCSS config
└── src/
    ├── main.js                 # Vue app bootstrap
    ├── App.vue                 # Root component
    ├── style.css               # Global styles + Tailwind
    │
    ├── api/                    # API modules
    │   ├── http.js             # Axios instance + interceptors
    │   ├── applications.js     # CRUD hồ sơ
    │   ├── auth.js             # Đăng nhập / đăng ký
    │   ├── beneficiaries.js    # Alias → applications.js
    │   ├── dashboard.js        # Thống kê dashboard
    │   ├── mockDb.js           # Dữ liệu mock fallback
    │   ├── notifications.js    # Thông báo
    │   ├── programs.js         # Chương trình trợ cấp
    │   ├── transactions.js     # Giao dịch chi trả
    │   └── users.js            # Quản lý người dùng
    │
    ├── components/             # Reusable components
    │   ├── AppLayout.vue       # Layout admin (sidebar + topbar)
    │   ├── AppSidebar.vue      # Sidebar navigation
    │   ├── AppTopbar.vue       # Top bar (search, notification, user)
    │   ├── ConfirmModal.vue    # Modal xác nhận hành động
    │   ├── PortalLayout.vue    # Layout portal (header + footer)
    │   ├── SkeletonLoader.vue  # Skeleton loading (card/row/text)
    │   ├── StatusBadge.vue     # Badge trạng thái tự động
    │   └── ToastContainer.vue  # Toast notification system
    │
    ├── composables/            # Vue composables
    │   ├── useApi.js           # Auto loading/error/toast wrapper
    │   └── useFormValidation.js # Field + form validation
    │
    ├── router/
    │   └── index.js            # Routes + Auth guard + Role guard
    │
    ├── stores/                 # Pinia stores
    │   ├── auth.js             # Authentication state + JWT
    │   └── ui.js               # UI state (sidebar, toast, modal)
    │
    └── views/                  # Page components
        ├── LoginView.vue           # Đăng nhập / Đăng ký
        ├── DashboardView.vue       # Dashboard thống kê
        ├── BeneficiariesView.vue   # Quản lý hồ sơ
        ├── ApplicationDetailView.vue # Chi tiết hồ sơ
        ├── AiReviewView.vue        # AI Review Engine
        ├── ProgramsView.vue        # Quản lý chương trình
        ├── BudgetView.vue          # Quản lý ngân sách
        ├── DoiTuongView.vue        # Đối tượng hưởng trợ cấp
        ├── UsersView.vue           # Quản lý người dùng
        ├── ReportsView.vue         # Báo cáo hệ thống
        ├── ProfileView.vue         # Hồ sơ cá nhân
        ├── SettingsView.vue        # Cài đặt hệ thống
        ├── NotificationsView.vue   # Thông báo
        ├── ActivityLogView.vue     # Nhật ký hoạt động
        │
        └── portal/                 # Portal (Người dân)
            ├── PortalHomeView.vue      # Trang chủ portal
            ├── PortalProgramsView.vue  # Tra cứu chương trình
            ├── PortalApplyView.vue     # Nộp hồ sơ online
            ├── PortalMyAppsView.vue    # Hồ sơ của tôi
            ├── PortalAppDetailView.vue # Chi tiết hồ sơ
            ├── PortalNotifyView.vue    # Thông báo
            └── PortalProfileView.vue   # Thông tin cá nhân
```

---

## ⚙️ Cấu Hình

### Vite Dev Server

File `vite.config.js`:

```js
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5175,           // Port dev server
    strictPort: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // Backend API
        changeOrigin: true,
      }
    }
  }
})
```

### API Proxy

Mọi request đến `/api/*` sẽ được proxy sang `http://localhost:8080` (backend). Nếu backend chưa chạy, ứng dụng sẽ sử dụng **dữ liệu mock fallback** từ `mockDb.js`.

### Biến Môi Trường

Tạo file `.env` tại thư mục gốc (tùy chọn):

```env
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 🗺️ Routing & Phân Quyền

| Route | Component | Vai trò | Mô tả |
|---|---|---|---|
| `/login` | LoginView | Public | Đăng nhập / Đăng ký |
| `/` | DashboardView | ALL | Dashboard thống kê |
| `/ho-so` | BeneficiariesView | ALL | Quản lý hồ sơ |
| `/ho-so/:id` | ApplicationDetailView | ALL | Chi tiết hồ sơ |
| `/xet-duyet-ai` | AiReviewView | ADMIN, REVIEWER | AI Review |
| `/ngan-sach` | BudgetView | ADMIN, FINANCE | Ngân sách |
| `/bao-cao` | ReportsView | ADMIN, REVIEWER, FINANCE | Báo cáo |
| `/nguoi-dung` | UsersView | ADMIN | Quản lý user |
| `/cai-dat` | SettingsView | ADMIN | Cài đặt |
| `/portal/*` | PortalLayout | VIEWER | Portal người dân |

---

## 📸 Screenshots

> *Sẽ được cập nhật sau*

---

## 👨‍💻 Tác Giả

**Võ Anh Kiệt**

- GitHub: [@VoAnhKiet1410](https://github.com/VoAnhKiet1410)

---

## 📄 License

Dự án này phục vụ mục đích học tập và nghiên cứu.
