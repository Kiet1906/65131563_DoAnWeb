package ntu.kiet.miniproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ntu.kiet.miniproduct.entity.Product;

// JpaRepository<Tên Entity, Kiểu dữ liệu của ID>
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Để trống! Spring Boot sẽ tự động viết sẵn các lệnh Thêm, Sửa, Xóa cho bạn.
}