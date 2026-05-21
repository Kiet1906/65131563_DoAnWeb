package ntu.kiet.miniproduct.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.repository.ProductRepository;

@Controller
public class ProductController {

    // Gọi Repository (người phiên dịch) vào để dùng
    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }
    
    // Mở trang đăng nhập tự thiết kế
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Mở file login.html
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

    // 3. Nút Lưu (Khi người dùng bấm Submit trên Form có chứa file ảnh)
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        // TRƯỜNG HỢP 1: NẾU NGƯỜI DÙNG CÓ CHỌN ẢNH ĐỂ TẢI LÊN
        if (!multipartFile.isEmpty()) {
            // 1. Lấy sạch tên file ảnh (Ví dụ: iphone15.png)
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setImage(fileName); // Lưu tên ảnh này vào thuộc tính của Product
            
            // 2. Lưu thông tin sản phẩm vào Database trước
            repo.save(product);
            
            // 3. Tạo thư mục ảo "product-images" ở ngoài nếu trên máy chưa có
            String uploadDir = "product-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // 4. Tiến hành copy file ảnh từ trình duyệt vào thư mục vừa tạo
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Không thể lưu file ảnh: " + fileName, ioe);
            }
        } 
        // TRƯỜNG HỢP 2: KHÔNG CHỌN ẢNH (Khi bấm Sửa sản phẩm và muốn giữ lại ảnh cũ)
        else {
            if (product.getId() != null) {
                Product existingProduct = repo.findById(product.getId()).orElse(null);
                if (existingProduct != null) {
                    product.setImage(existingProduct.getImage()); // Lấy lại tên ảnh cũ dán vào
                }
            }
            repo.save(product); // Lưu cập nhật vào DB
        }

        return "redirect:/"; // Lưu xong thì tự động quay về trang chủ
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