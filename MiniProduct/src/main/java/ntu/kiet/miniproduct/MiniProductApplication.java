package ntu.kiet.miniproduct;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ntu.kiet.miniproduct.entity.User;
import ntu.kiet.miniproduct.repository.UserRepository;

@SpringBootApplication
public class MiniProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniProductApplication.class, args);
    }

    // Code tự động chạy khi bật Server: Tạo tài khoản admin nếu chưa có
    @Bean
    CommandLineRunner initDatabase(UserRepository userRepo) {
        return args -> {
            if (userRepo.findByUsername("admin").isEmpty()) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                // Mã hóa mật khẩu "123456" thành chuỗi bảo mật băm
                String hashPassword = encoder.encode("123456");
                
                User admin = new User("admin", hashPassword, "ROLE_ADMIN");
                userRepo.save(admin);
                System.out.println("==> Đã tạo tài khoản admin mẫu thành công trong Database!");
            }
        };
    }
}