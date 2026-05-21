package ntu.kiet.miniproduct.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Khai báo công cụ mã hóa mật khẩu chuẩn BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/product-images/**", "/page/**").permitAll() // Cho phép vào trang chủ và trang login công khai
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Chỉ định đường dẫn trang login tự thiết kế
                .defaultSuccessUrl("/", true) // Đăng nhập xong thì đá về trang chủ
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout") // Sửa dòng này: Đăng xuất xong đá về trang Login kèm tham số
                .permitAll());
        
        return http.build();
    }
}