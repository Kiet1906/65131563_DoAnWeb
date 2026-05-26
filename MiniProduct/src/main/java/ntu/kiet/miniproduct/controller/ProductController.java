package ntu.kiet.miniproduct.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.repository.ProductRepository;

@Controller
public class ProductController {

    private final ProductRepository repo;

    public ProductController(ProductRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * TRANG CHỦ: Nhận từ khóa tìm kiếm (keyword) và hướng sắp xếp (sortDir).
     * Mặc định nếu không ai bấm gì, sortDir sẽ là "asc" (Sắp xếp A-Z).
     */
    @GetMapping("/")
    public String viewHomePage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
            Model model) {
        // Chuyển hướng xử lý vào trang số 1
        return findPaginated(1, keyword, sortDir, model);
    }

    /**
     * HÀM XỬ LÝ CHÍNH: Phân trang + Tìm kiếm mờ + Sắp xếp A-Z/Z-A
     * Đã dọn sạch thuộc tính trùng lặp ở @PathVariable để xóa Cảnh báo IDE
     */
    @GetMapping("/page/{pageNo}")
    public String findPaginated(
            @PathVariable int pageNo, 
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
            Model model) {
        
        int pageSize = 5; // Số sản phẩm trên 1 trang

        // 1. Tạo quy tắc Sắp xếp: Nếu sortDir là "desc" thì xếp tên từ Z->A, ngược lại mặc định A->Z
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
        
        // 2. Gộp Trang hiện tại, Kích thước trang và Quy tắc sắp xếp thành 1 đối tượng Pageable
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        
        Page<Product> page;
        
        // 3. Xử lý Logic Tìm kiếm
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Thuật toán tạo chuỗi tìm kiếm mờ (ví dụ: m tôm -> %m%t%ô%m%)
            StringBuilder fuzzyPattern = new StringBuilder("%");
            for (char c : keyword.trim().toCharArray()) {
                if (c != ' ') {
                    fuzzyPattern.append(c).append("%");
                } else {
                    fuzzyPattern.append("%");
                }
            }
            String finalPattern = fuzzyPattern.toString().replaceAll("%+", "%");
            
            // Gọi tầng DB xử lý (Đã bao gồm cả sắp xếp trong biến pageable)
            page = repo.searchByNameFuzzy(finalPattern, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            // Nếu ô tìm kiếm bỏ trống -> Lấy toàn bộ theo sắp xếp
            page = repo.findAll(pageable);
            model.addAttribute("keyword", "");
        }

        // 4. Trả dữ liệu hiển thị về giao diện HTML
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", page.getContent());
        model.addAttribute("sortDir", sortDir); // Gửi biến này về để Select box giữ nguyên lựa chọn A-Z hoặc Z-A

        return "index";
    }

    /**
     * Xử lý lưu sản phẩm (Thêm mới hoặc Cập nhật) và Upload Ảnh
     */
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product, 
                              @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            product.setImage(fileName);
            repo.save(product);

            String uploadDir = "product-images/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ioe) {
                throw new IOException("Không thể lưu file ảnh: " + fileName, ioe);
            }
        } else {
            // Cập nhật thông tin nhưng giữ nguyên ảnh cũ nếu không up ảnh mới
            if (product.getId() != null) {
                Product existingProduct = repo.findById(product.getId()).orElse(null);
                if (existingProduct != null) {
                    product.setImage(existingProduct.getImage());
                }
            }
            repo.save(product);
        }

        return "redirect:/";
    }

    /**
     * Điều hướng mở biểu mẫu thêm mới sản phẩm trống.
     */
    @GetMapping("/showNewProductForm")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        return "form";
    }
    
    /**
     * Tải thông tin sản phẩm hiện tại và điều hướng mở biểu mẫu chỉnh sửa.
     * Đã dọn sạch thuộc tính trùng lặp ở @PathVariable để xóa Cảnh báo IDE
     */
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mã sản phẩm không hợp lệ: " + id));
        model.addAttribute("product", product);
        return "form";
    }
    
    /**
     * Thực hiện xóa bỏ sản phẩm ra khỏi hệ thống dựa vào mã ID.
     * Đã dọn sạch thuộc tính trùng lặp ở @PathVariable để xóa Cảnh báo IDE
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}