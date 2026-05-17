package ntu.kiet.miniproduct.controller;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.repository.ProductRepository;

@Controller
public class ProductController {

    // Gọi Repository (người phiên dịch) vào để dùng
    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    // 1. Hiển thị danh sách sản phẩm (Trang chủ)
    @GetMapping("/")
    public String viewHomePage(Model model) {
        List<Product> listProducts = repo.findAll(); // Lấy tất cả SP từ Database
        model.addAttribute("listProducts", listProducts); // Gửi dữ liệu sang trang HTML
        return "index"; // Mở file index.html
    }

    // 2. Mở trang Form để Thêm sản phẩm mới
    @GetMapping("/new")
    public String showNewProductForm(Model model) {
        Product product = new Product(); // Tạo 1 sản phẩm trống
        model.addAttribute("product", product); // Gửi sang Form để điền
        return "form"; // Mở file form.html
    }

    // 3. Nút Lưu (Khi người dùng bấm Submit trên Form)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product) {
        repo.save(product); // Lưu vào Database (Nếu có ID rồi thì nó sẽ Cập nhật, chưa có thì Thêm mới)
        return "redirect:/"; // Lưu xong thì quay về trang chủ
    }

    // 4. Mở trang Form để Sửa sản phẩm (Dựa vào ID)
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Integer id, Model model) {
        Product product = repo.findById(id).get(); // Tìm sản phẩm theo ID
        model.addAttribute("product", product); // Gửi dữ liệu cũ sang Form
        return "form"; 
    }

    // 5. Xóa sản phẩm (Dựa vào ID)
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        repo.deleteById(id); // Xóa khỏi Database
        return "redirect:/"; // Xóa xong quay về trang chủ
    }
}