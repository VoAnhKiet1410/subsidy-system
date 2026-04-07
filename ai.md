Chào bạn, tôi đang phát triển một hệ thống web quản lý trợ cấp xã hội. Hiện tại, tôi cần triển khai luồng nộp hồ sơ và tính năng "AI xét duyệt hồ sơ". Giao diện AI đánh giá đã hoàn thiện và database đã có sẵn bảng danh_gia_ai.

Dưới đây là yêu cầu mới và luồng nghiệp vụ (workflow). Hãy giúp tôi thiết kế lại Database và đề xuất cấu trúc logic xử lý.

1. Yêu cầu cập nhật Database (Lưu trữ Form và Giấy tờ linh hoạt):

Thêm Form mẫu: Mỗi Chương trình hỗ trợ (Programs) cần thêm một thuộc tính lưu trữ file Form mẫu (ví dụ: template_form_url hoặc image chứa ảnh/file mẫu của hộ nghèo).

Giấy tờ yêu cầu (attachments): Mỗi chương trình hỗ trợ sẽ yêu cầu người dùng nộp một bộ tài liệu khác nhau. Cần thiết kế DB sao cho cán bộ (Admin) có thể cấu hình danh sách các loại giấy tờ bắt buộc cho từng chương trình.

2. Luồng nghiệp vụ (Workflow) từ góc độ người dùng và hệ thống:

Bước 1: Người dùng đăng ký hồ sơ -> Chọn Chương trình hỗ trợ muốn tham gia.

Bước 2: Hệ thống hiển thị Form mẫu của chương trình đó để người dùng tải về điền.

Bước 3: Người dùng nộp lại Form đã điền kèm theo các tài liệu minh chứng. Hệ thống phải validate (bắt buộc) người dùng nộp đủ các loại giấy tờ mà cán bộ đã cấu hình cho chương trình này.

Bước 4 (AI Xét duyệt): Khi hồ sơ nộp thành công đầy đủ giấy tờ, AI sẽ bắt đầu quét thông tin (OCR/Vision) trên Form mà người dùng đã điền.

Bước 5 (Đối chiếu & Đánh giá): AI đối chiếu thông tin quét được từ Form với các giấy tờ chính thống đính kèm (như CCCD, Sổ hộ nghèo). Dựa trên kết quả khớp thông tin, AI sẽ tính toán độ tin cậy, chấm điểm (Score) và lưu vào bảng danh_gia_ai.

3. Yêu cầu cụ thể dành cho bạn:

Viết thêm hoặc sửa model để cập nhật DB giải quyết vấn đề "mỗi chương trình có form riêng và danh sách giấy tờ yêu cầu khác nhau".

Gợi ý cấu trúc các lớp Service (logic code) để xử lý luồng nộp hồ sơ này, đặc biệt là đoạn kiểm tra người dùng đã nộp đủ giấy tờ chưa trước khi gọi sang module AI.
(Lưu ý: Tạm thời chưa cần viết code cho phần Detect gian lận hay Check trùng CCCD, chỉ tập trung giải quyết luồng nộp chuẩn và đánh giá AI cơ bản này).