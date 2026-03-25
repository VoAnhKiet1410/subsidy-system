# Dữ liệu MongoDB - Hệ Thống Trợ Cấp

Thư mục này chứa dữ liệu thật được export từ MongoDB để tiện chia sẻ và triển khai.

## Cấu trúc

```
database/
├── dump/trocap/          # MongoDB dump (file .bson + .json metadata)
│   ├── users.*           # 5 tài khoản người dùng
│   ├── beneficiaries.*   # 20 đối tượng hưởng trợ cấp
│   ├── programs.*        # Các chương trình trợ cấp
│   ├── applications.*    # Hồ sơ đề nghị
│   ├── transactions.*    # Lịch sử chi trả
│   ├── nguon_quy.*       # Nguồn quỹ
│   ├── beneficiary_groups.*
│   ├── audit_logs.*
│   └── counters.*        # Bộ đếm mã hồ sơ tuần tự (HS-0001...)
├── import-data.bat       # Script import cho Windows
└── import-data.sh        # Script import cho Linux/Mac
```

## Cách import

### Windows
```bat
cd database
import-data.bat
```

### Linux / Mac
```bash
cd database
chmod +x import-data.sh
./import-data.sh
```

### Thủ công (nếu script không chạy được)
```bash
mongorestore --drop --db trocap database/dump/trocap
```

## Tài khoản mặc định

| Username | Password | Vai trò |
|---|---|---|
| `admin` | `123456` | Quản trị viên |
| `officer1` | `123456` | Cán bộ xét duyệt |
| `accountant1` | `123456` | Kế toán |
| `citizen1` | `123456` | Công dân |

## Lưu ý

- MongoDB phải đang chạy trên `localhost:27017`
- Cần cài **MongoDB Database Tools** (chứa `mongorestore`): https://www.mongodb.com/try/download/database-tools
- Lệnh `--drop` sẽ **xóa data cũ** trong database `trocap` trước khi import
