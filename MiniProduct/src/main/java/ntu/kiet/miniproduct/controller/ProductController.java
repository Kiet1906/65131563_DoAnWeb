package ntu.kiet.miniproduct.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ntu.kiet.miniproduct.entity.Category;
import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.repository.CategoryRepository;
import ntu.kiet.miniproduct.repository.ProductRepository;

@Controller
public class ProductController {

    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;

    // Tiêm (Inject) cả 2 Repository vào trong Controller để xử lý đồng thời Sản phẩm và Hãng
    public ProductController(ProductRepository repo, CategoryRepository categoryRepo) {
        this.repo = repo;
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * TRANG CHỦ: Nhận từ khóa tìm kiếm (keyword), hướng sắp xếp (sortDir) và mã danh mục hãng (categoryId).
     */
    @GetMapping("/")
    public String viewHomePage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {
        // Chuyển hướng xử lý vào trang số 1
        return findPaginated(1, keyword, sortDir, categoryId, model);
    }

    /**
     * HÀM XỬ LÝ CHÍNH: Phân trang + Tìm kiếm mờ + Sắp xếp A-Z/Z-A + Lọc theo Hãng (Category)
     */
    @GetMapping("/page/{pageNo}")
    public String findPaginated(
            @PathVariable int pageNo, 
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {
        
        int pageSize = 5; // Số sản phẩm trên 1 trang

        // 1. Tạo quy tắc Sắp xếp: Nếu sortDir là "desc" thì xếp tên từ Z->A, ngược lại mặc định A->Z
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by("name").descending() : Sort.by("name").ascending();
        
        // 2. Gộp Trang hiện tại, Kích thước trang và Quy tắc sắp xếp thành 1 đối tượng Pageable
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        
        // Giữ nguyên thuật toán tạo chuỗi tìm kiếm mờ (Fuzzy Pattern) xuất sắc của bạn
        String finalPattern = "%";
        if (keyword != null && !keyword.trim().isEmpty()) {
            StringBuilder fuzzyPattern = new StringBuilder("%");
            for (char c : keyword.trim().toCharArray()) {
                if (c != ' ') {
                    fuzzyPattern.append(c).append("%");
                } else {
                    fuzzyPattern.append("%");
                }
            }
            finalPattern = fuzzyPattern.toString().replaceAll("%+", "%");
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("keyword", "");
        }

        // 3. Gọi hàm Repository mới: Lọc kết hợp cả Hãng điện thoại (CategoryId) và Tên tìm kiếm mờ
        Page<Product> page = repo.searchByCategoryAndNameFuzzy(categoryId, finalPattern, pageable);

        // Lấy danh sách toàn bộ Hãng điện thoại để kết xuất ra thanh Menu bên trái giao diện
        List<Category> listCategories = categoryRepo.findAll();

        // 4. Trả các dữ liệu hiển thị về giao diện HTML
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", page.getContent());
        model.addAttribute("sortDir", sortDir); 
        
        // Đẩy thêm dữ liệu Hãng lên View phục vụ chức năng hiển thị và bôi đậm Menu
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("selectedCategoryId", categoryId);

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
     * ĐÃ CẬP NHẬT: Điều hướng mở biểu mẫu thêm mới sản phẩm trống kèm danh sách hãng lựa chọn.
     */
    @GetMapping("/showNewProductForm")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        List<Category> listCategories = categoryRepo.findAll(); // Lấy list hãng điện thoại
        
        model.addAttribute("product", product);
        model.addAttribute("listCategories", listCategories); // Gửi sang form để nạp vào select dropdown
        return "form";
    }
    
    /**
     * ĐÃ CẬP NHẬT: Tải thông tin sản phẩm hiện tại kèm danh sách hãng để thực hiện chỉnh sửa.
     */
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mã sản phẩm không hợp lệ: " + id));
        List<Category> listCategories = categoryRepo.findAll(); // Lấy list hãng điện thoại
        
        model.addAttribute("product", product);
        model.addAttribute("listCategories", listCategories); // Gửi sang form để nạp vào select dropdown
        return "form";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}