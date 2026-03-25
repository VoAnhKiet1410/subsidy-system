#!/bin/bash
echo "============================================"
echo "  IMPORT DỮ LIỆU MẪU - Hệ Thống Trợ Cấp"
echo "============================================"

# Tìm mongorestore
MONGORESTORE="mongorestore"

echo "[1/2] Đang xóa database cũ (nếu có)..."
$MONGORESTORE --drop --db trocap dump/trocap --quiet

if [ $? -ne 0 ]; then
    echo ""
    echo "[LỖI] Import thất bại!"
    echo "Kiểm tra: MongoDB đang chạy? mongorestore đã cài chưa?"
    echo "Cài MongoDB Database Tools: https://www.mongodb.com/try/download/database-tools"
    exit 1
fi

echo "[2/2] Import hoàn tất!"
echo ""
echo "============================================"
echo "  Dữ liệu đã được nhập thành công!"
echo "  - Users: admin / officer1 / accountant1 / citizen1"
echo "  - Password mặc định: 123456"
echo "============================================"
