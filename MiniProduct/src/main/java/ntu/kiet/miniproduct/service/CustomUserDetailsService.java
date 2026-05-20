package ntu.kiet.miniproduct.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import ntu.kiet.miniproduct.entity.User;
import ntu.kiet.miniproduct.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user trong DB
        User user = userRepo.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng: " + username));

        // Chuyển đổi dữ liệu User của mình thành User chuẩn của Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Đây là mật khẩu đã mã hóa băm
                .authorities(user.getRole())   // Cấp quyền
                .build();
    }
}