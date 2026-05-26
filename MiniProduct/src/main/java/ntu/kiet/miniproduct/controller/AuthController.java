package ntu.kiet.miniproduct.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ntu.kiet.miniproduct.entity.User;
import ntu.kiet.miniproduct.repository.UserRepository;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Inject tự động các Bean qua Constructor
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Mở giao diện trang đăng ký
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Trả về file register.html trong templates
    }

    /**
     * Xử lý dữ liệu khi người dùng ấn nút "Đăng Ký"
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        
        // 1. Kiểm tra xem Tên đăng nhập đã bị trùng trong DB chưa
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập này đã tồn tại! Vui lòng chọn tên khác.");
            return "redirect:/register";
        }

        // 2. Tiến hành mã hóa mật khẩu bằng BCrypt trước khi lưu
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 3. Thiết lập quyền mặc định là quyền USER thường
        user.setRole("ROLE_USER");

        // 4. Lưu tài khoản mới vào hệ thống
        userRepository.save(user);

        // Bắn thông báo thành công sang trang Login
        redirectAttributes.addFlashAttribute("message", "Đăng ký tài khoản thành công! Mời bạn đăng nhập.");
        return "redirect:/login";
    }
}