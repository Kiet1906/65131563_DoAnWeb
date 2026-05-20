package ntu.kiet.miniproduct.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. Tạo tài khoản đăng nhập (Lưu tạm trong bộ nhớ máy)
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin") // Tên đăng nhập
            .password("{noop}123456") // Mật khẩu (chữ {noop} để báo cho Spring biết là không cần mã hóa mật khẩu lúc test)
            .roles("ADMIN") // Quyền quản trị
            .build();
            
        return new InMemoryUserDetailsManager(admin);
    }

    // 2. Thiết lập quy tắc bảo vệ các đường dẫn (URL)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").permitAll() // Trang chủ ("/") thì AI CŨNG ĐƯỢC XEM (permitAll)
                .anyRequest().authenticated()     // Tất cả các thao tác khác (Thêm, Sửa, Xóa) thì PHẢI ĐĂNG NHẬP (authenticated)
            )
            .formLogin(form -> form.permitAll())  // Tự động sinh ra 1 trang đăng nhập mặc định của Spring
            .logout(logout -> logout.permitAll()); // Cho phép đăng xuất
        
        return http.build();
    }
}