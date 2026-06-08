## 👨‍💻 Thông tin Tác giả
* **Sinh viên thực hiện:** Nguyễn Anh Kiệt
* **Mã số viên (MSSV):** 65131563
* **Môn học:** Đồ án Phát triển Ứng dụng Web
* **Giảng viên hướng dẫn:** ThS. Mai Cường Thọ

---

# 📱 PhoneStore - Hệ Thống Quản Lý & Bán Thiết Bị Di Động Trực Tuyến

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Web_Application-red.svg?style=for-the-badge" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Java_21-blue.svg?style=for-the-badge&logo=openjdk" alt="Language">
  <img src="https://img.shields.io/badge/Framework-Spring_Boot_3.x-green.svg?style=for-the-badge&logo=springboot" alt="Framework">
  <img src="https://img.shields.io/badge/Backend-MySQL-yellow.svg?style=for-the-badge&logo=mysql" alt="Backend">
</p>

**PhoneStore** là một ứng dụng Web thương mại điện tử chuyên kinh doanh các thiết bị di động thông minh được xây dựng trên nền tảng Java Spring Boot. Dự án áp dụng chặt chẽ kiến trúc phân tầng MVC kết hợp với giải pháp quản lý dữ liệu tối ưu nhằm đem lại trải nghiệm mua sắm mượt mà cho khách hàng và hệ thống xử lý nghiệp vụ phê duyệt, hoàn kho an toàn cho Quản trị viên.

## 🎬 1. Video Demo Vận Hành
👉 **[📺 BẤM VÀO ĐÂY ĐỂ XEM VIDEO DEMO DỰ ÁN PHONESTORE](https://youtu.be/A9G4l7xakRw)**

---

## 🚀 Các Tính Năng Cốt Lõi & Kiến Trúc Đáp Ứng Thang Điểm

### 1. Kiến trúc Lưu trữ Phân tầng Tối ưu
Hệ thống giải quyết bài toán hiệu năng và tốc độ phản hồi thông qua việc phân tách luồng dữ liệu rõ ràng:
* **Tầng lưu trữ bền vững (Persistent Storage):** Toàn bộ danh mục dữ liệu cốt lõi như sản phẩm điện thoại (`products`), người dùng (`user`), hãng sản xuất (`categories`), đơn hàng (`orders`) và chi tiết đơn hàng (`order_details`) được lưu trữ đồng bộ trong CSDL quan hệ **MySQL** qua cơ chế ánh xạ tự động `Spring Data JPA (Hibernate)`.
* **Tầng lưu trữ tạm thời trong bộ nhớ (In-Memory Session):** Logic Giỏ hàng được duy trì hoàn toàn trên bộ nhớ RAM máy chủ thông qua **`HttpSession`**. Giải pháp này giải phóng băng thông tối đa cho Database, triệt tiêu các truy vấn I/O ghi/xóa liên tục xuống ổ cứng khi người dùng thao tác chọn mua.

### 2. Phân hệ Khách hàng (Client View)
* **Hiển thị & Phân trang dữ liệu:** Kết xuất danh sách điện thoại trực quan dưới dạng lưới, hỗ trợ chia nhỏ dữ liệu theo từng trang giúp tăng tốc độ tải tài nguyên.
* **Tìm kiếm & Lọc đồng bộ nâng cao:** Tích hợp bộ lọc tìm kiếm mờ (Fuzzy Search) theo tên máy, lọc riêng theo hãng sản xuất và sắp xếp theo giá (tăng/giảm dần). Xử lý JavaScript đồng bộ URL đảm bảo các tiêu chí lọc kết hợp mượt mà mà không làm mất trạng thái của nhau.
* **Xác thực bảo mật người dùng:** Sử dụng Spring Security để quản lý luồng đăng ký/đăng nhập hệ thống. Mật khẩu thành viên được mã hóa tự động bằng thuật toán băm một chiều **`BCrypt`** trước khi ghi vào Database để bảo mật an toàn.
* **Tương tác Giỏ hàng & Đặt hàng:** Thêm/Sửa/Xóa số lượng sản phẩm trong giỏ hàng. Hệ thống tích hợp thuật toán **Kiểm soát tồn kho thực tế (Inventory Safety)**: Tự động đối chiếu số lượng đặt mua trong Session với kho dữ liệu và chặn thao tác xuất thông báo lỗi nếu vượt ngưỡng.

### 3. Phân hệ Quản trị viên (Admin Management)
* **Quản trị danh mục kho hàng (CRUD):** Cung cấp biểu mẫu trực quan cho phép Admin thêm mới dòng máy, chỉnh sửa thông số/giá bán và gỡ bỏ sản phẩm.
* **Tải lên hình ảnh vật lý:** Xử lý luồng tệp tin đa phần (`MultipartFile`), cho phép Admin upload trực tiếp file ảnh đại diện của điện thoại từ máy tính cá nhân lưu vào bộ nhớ vật lý của Server.
* **Bắt lỗi ràng buộc dữ liệu thông minh:** Sử dụng cơ chế Foreign Key Constraint chặn đứng hành động xóa các sản phẩm đã phát sinh lịch sử giao dịch, hiển thị thông báo lỗi trực quan giúp bảo toàn tính toàn vẹn dữ liệu.
* **Xử lý đơn hàng & Logic hoàn kho tự động:** Quản lý danh sách hóa đơn đổ về theo thời gian thực (Real-time). Khi Admin thực hiện hành động "Hủy đơn" (`CANCELLED`), hệ thống tự động kích hoạt vòng lặp xử lý hoàn kho (Rollback logic) cộng trả lại chính xác số lượng máy vào bảng `products`, tránh thất thoát dữ liệu ảo.

---

## 🛠️ Công Nghệ Sử Dụng
* **IDE:** Eclipse
* **Ngôn ngữ:** Java (JDK 21)
* **Framework:** Spring Boot 3.x (Spring MVC, Spring Security, Spring Data JPA)
* **Database:** MySQL Server (XAMPP Control Panel)
* **Template Engine:** HTML5, CSS3, Thymeleaf, Bootstrap 5, JavaScript

---

## 📸 Hình Ảnh Minh Họa Giao Diện

### A. Quy Trình Mua Sắm & Tương Tác (Khách Hàng)

#### 1. Duyệt sản phẩm tại Trang chủ & Tìm kiếm theo danh mục hãng
<p align="center">
  <img width="48%" alt="Hình 8: Trang chủ"  src="https://github.com/user-attachments/assets/72be618d-3ab1-4ca4-9af4-6ee6e3be5e52" 
 />
  <img width="48%" alt="Hình 10: Chức năng tìm kiếm theo danh mục" src="https://github.com/user-attachments/assets/9c3408ff-4780-4492-a0a9-ae573edc8287" />

</p>

#### 2. Hệ thống Xác thực Bảo mật Thành viên (Đăng nhập / Đăng ký)
<p align="center">
  <img width="48%" alt="Hình 12: Trang đăng nhập" src="https://github.com/user-attachments/assets/76c6e02c-5ac6-4768-922f-60d4309e3856" 
 />
  <img width="48%" alt="Hình 13: Trang đăng ký" src="https://github.com/user-attachments/assets/73202912-1b81-4c98-9a34-fd97dec64fb8" />

</p>

#### 3. Quản lý tương tác Giỏ hàng & Đặt hàng thành công
<p align="center">
  <img width="48%" alt="Hình 14: Trang giỏ hàng" src="https://github.com/user-attachments/assets/a4486f54-c212-4bf1-ab42-4b5c6cbdf1fb" 
 />
  <img width="48%" alt="Hình 15: Thông báo đặt hàng thành công" src="https://github.com/user-attachments/assets/7eaeefa1-219a-44bf-985f-d3a394783fa4"
/>
</p>

---

### B. Phân Hệ Quản Trị Kho Hàng & Xử Lý Nghiệp Vụ (Admin)

#### 1. Trang quản trị tổng quan danh sách sản phẩm điện thoại
<p align="center">
  <img width="60%" alt="Hình 16: Trang admin" src="https://github.com/user-attachments/assets/14b8b2a2-558d-4189-afd1-451744e6ae68" 
 />
</p>

#### 2. Biểu mẫu nghiệp vụ Thêm và Cập nhật thông tin dữ liệu (CRUD)
<p align="center">
  <img width="48%" alt="Hình 17: Trang thêm điện thoại" src="https://github.com/user-attachments/assets/e24af851-70cb-4caf-9ea1-796c2944b0bc" 
 />
  <img width="48%" alt="Hình 18: Trang cập nhật điện thoại" src="https://github.com/user-attachments/assets/62b0eb89-473e-4dda-8aac-7f204f461e88" 
 />
</p>

#### 3. Quản lý danh sách Đơn đặt hàng & Biểu mẫu xem chi tiết đơn hàng
<p align="center">
  <img width="48%" alt="Hình 21: Trang quản lý đơn hàng"  src="https://github.com/user-attachments/assets/f263e474-21f7-4d6c-928b-0e7edfb05f67" 
 />
  <img width="48%" alt="Hình 22: Trang thông tin đơn hàng" src="https://github.com/user-attachments/assets/a844f511-b4a9-4516-81fe-06893bfefaee" 
 />
</p>
