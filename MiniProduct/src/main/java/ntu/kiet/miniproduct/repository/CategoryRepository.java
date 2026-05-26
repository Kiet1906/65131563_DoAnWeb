package ntu.kiet.miniproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ntu.kiet.miniproduct.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Kế thừa JpaRepository để sử dụng toàn bộ hàm CRUD cơ bản của danh mục
}