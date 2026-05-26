package ntu.kiet.miniproduct;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ntu.kiet.miniproduct.entity.User;
import ntu.kiet.miniproduct.entity.Category;
import ntu.kiet.miniproduct.repository.UserRepository;
import ntu.kiet.miniproduct.repository.CategoryRepository;

@SpringBootApplication
public class MiniProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniProductApplication.class, args);
    }

    // Code tự động chạy khi bật Server: Tạo tài khoản admin và nạp các Hãng điện thoại mẫu
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo, CategoryRepository categoryRepo) {
        return args -> {
            // 1. Tự động tạo tài khoản Admin mẫu nếu chưa tồn tại
            if (userRepo.findByUsername("admin").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String hashPassword = encoder.encode("123456");
                
                User admin = new User("admin", hashPassword, "ROLE_ADMIN");
                userRepo.save(admin);
                System.out.println("==> Đã tạo tài khoản admin mẫu thành công trong Database!");
            }

            // 2. THÊM MỚI: Tự động kiểm tra và nạp dữ liệu 4 Hãng điện thoại mẫu vào Database
            if (categoryRepo.count() < 4) {
                categoryRepo.deleteAll(); // Xóa sạch dữ liệu trống hoặc lỗi cũ nếu có
                categoryRepo.save(new Category("Apple (iPhone)"));
                categoryRepo.save(new Category("Samsung"));
                categoryRepo.save(new Category("Oppo"));
                categoryRepo.save(new Category("Xiaomi"));
                System.out.println("==> Đã ép buộc nạp thành công 4 Hãng điện thoại mẫu vào Database!");
            }
        };
    }
}