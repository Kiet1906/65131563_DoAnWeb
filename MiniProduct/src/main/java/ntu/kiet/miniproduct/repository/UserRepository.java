package ntu.kiet.miniproduct.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ntu.kiet.miniproduct.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Tìm kiếm người dùng dựa vào Username để đăng nhập
    Optional<User> findByUsername(String username);
}