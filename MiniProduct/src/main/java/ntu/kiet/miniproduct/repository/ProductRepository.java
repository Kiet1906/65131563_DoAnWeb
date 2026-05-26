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
* THÊM MỚI: Thuật toán lọc kép kết hợp Tìm kiếm mờ + Lọc theo Hãng sản xuất + Phân trang sắp xếp.
* Nếu categoryId là null, điều kiện (:categoryId IS NULL) sẽ đúng và bỏ qua bộ lọc hãng.
*/
@Query("SELECT p FROM Product p WHERE (:categoryId IS NULL OR p.category.id = :categoryId) AND LOWER(p.name) LIKE LOWER(:keyword)")
Page<Product> searchByCategoryAndNameFuzzy(
@Param("categoryId") Integer categoryId,
@Param("keyword") String keyword,
Pageable pageable);
}