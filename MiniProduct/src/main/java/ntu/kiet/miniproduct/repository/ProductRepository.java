package ntu.kiet.miniproduct.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ntu.kiet.miniproduct.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Tìm kiếm mờ (Fuzzy Search) kết hợp phân trang và sắp xếp.
     * Đối tượng Pageable truyền vào đã tự động chứa thông tin Sắp xếp (A-Z hoặc Z-A)
     * nên câu lệnh SQL này sẽ tự động xếp đúng theo tên mà không cần code thêm SQL.
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:keyword)")
    Page<Product> searchByNameFuzzy(@Param("keyword") String keyword, Pageable pageable);
}