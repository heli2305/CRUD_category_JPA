# Dự án JPA - Quản lý Danh mục & Video (Jakarta EE + Hibernate + SQL Server)

## Công nghệ sử dụng

- **Java:** 22
- **Web:** Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0
- **ORM:** Hibernate 6.5.2 (JPA)
- **Database:** Microsoft SQL Server (`ShoppingServiceMVC`)
- **Bảo mật & Tiện ích:** BCrypt (`jbcrypt`), Jakarta Mail (OTP), Lombok

## Hướng dẫn cài đặt & chạy dự án

### 1. Khởi tạo Database

Mở SQL Server Management Studio (SSMS) và chạy file `database.sql` để tạo database `ShoppingServiceMVC` cùng các bảng `User`, `categories`, `Videos`.

- Tài khoản Admin mặc định: `admin` / `123`

### 2. Cấu hình gửi Email OTP (tùy chọn khi test Đăng ký / Quên mật khẩu)

Cấu hình biến môi trường hệ thống trước khi chạy Tomcat:

- `MAIL_USERNAME`: Email Gmail dùng để gửi OTP
- `MAIL_PASSWORD`: App Password của Gmail
- `MAIL_FROM`: Email hiển thị người gửi

### 3. Thư mục lưu file Upload (Multipart)

- Ảnh Category, ảnh Poster và file Video upload được lưu tại `D:\upload` (cấu hình trong `vn.iotstar.util.Constant.DIR`).
- Ứng dụng tự tạo thư mục nếu chưa tồn tại.

### 4. Chạy ứng dụng

- Deploy project lên **Apache Tomcat 10.1+** (Jakarta EE).
- Truy cập: `http://localhost:8080/JPA/home`

## Các chức năng chính

1. **Xác thực & Phân quyền:** Đăng ký kích hoạt OTP qua email, Đăng nhập (BCrypt + Cookie Remember Me), Đăng xuất, Quên mật khẩu qua OTP, phân quyền Admin / Manager / User.
2. **Quản lý Danh mục (Admin):** CRUD Category bằng JPA (`/admin/categories`).
3. **Quản lý & Xem Video:**
   - Trang chủ `/home`: Hiển thị 10 video mới nhất.
   - Trang `/video`: Hiển thị danh sách video của user đang đăng nhập, phân trang 6 video/trang, hỗ trợ Thêm/Sửa/Xóa.
   - Trang `/video/detail`: Xem chi tiết và phát video trực tiếp khi bấm vào tiêu đề video.
   - Trang `/admin/videos`: Admin quản lý toàn bộ video trong hệ thống.
   - Upload ảnh Poster và file Video bằng `@MultipartConfig`.
