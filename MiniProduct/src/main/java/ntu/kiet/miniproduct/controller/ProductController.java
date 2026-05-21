package ntu.kiet.miniproduct.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

// Import thêm thư viện phân trang và SẮP XẾP của Spring Data
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    // Trang chủ mặc định trỏ về Trang số 1
    @GetMapping("/")
    public String viewHomePage(Model model) {
        return findPaginated(1, model); 
    }

    // Hàm xử lý phân trang kèm SẮP XẾP TÊN từ A-Z
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo, Model model) {
        int pageSize = 5; // Số sản phẩm trên 1 trang
        
        // CẬP NHẬT: Thêm Sort.by("name").ascending() để xếp theo tên từ A -> Z
        Page<Product> page = repo.findAll(PageRequest.of(pageNo - 1, pageSize, Sort.by("name").ascending()));
        List<Product> listProducts = page.getContent();
        
        // Gửi các thông số phân trang sang giao diện HTML
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", listProducts);
        
        return "index";
    }

    // Hàm xử lý hiển thị form thêm mới sản phẩm
    @GetMapping("/new")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product); 
        return "form"; 
    }

    // Nút Lưu
    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product,
                              @RequestParam("imageFile") MultipartFile multipartFile) throws IOException {
        
        if (!multipartFile.isEmpty()) {
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

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Integer id, Model model) {
        Product product = repo.findById(id).get(); 
        model.addAttribute("product", product); 
        return "form"; 
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        repo.deleteById(id); 
        return "redirect:/"; 
    }
}