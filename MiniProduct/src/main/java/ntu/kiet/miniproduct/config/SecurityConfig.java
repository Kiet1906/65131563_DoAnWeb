package ntu.kiet.miniproduct.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. Cho phép TẤT CẢ mọi người (Khách & User) truy cập các trang công khai và tài nguyên tĩnh
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/product-images/**", "/page/**", "/cart/**").permitAll()
                
                // 2. CHỈ ADMIN (ROLE_ADMIN) mới có quyền truy cập các đường dẫn Quản trị và CRUD
                // Đã bổ sung chặn đường dẫn "/admin/**"
                .requestMatchers("/showNewProductForm", "/save", "/edit/**", "/delete/**", "/admin/**").hasRole("ADMIN")
                
                // 3. Các yêu cầu còn lại (nếu có) bắt buộc phải đăng nhập
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")               // Trang đăng nhập tự chế
                .defaultSuccessUrl("/", true)      // Đăng nhập thành công thì về trang chủ
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Đăng xuất xong đá về trang login kèm thông báo
                .permitAll()
            );
        
        return http.build();
    }
}