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

    public ProductController(ProductRepository repo, CategoryRepository categoryRepo) {
        this.repo = repo;
        this.categoryRepo = categoryRepo;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/")
    public String viewHomePage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {
        return findPaginated(1, keyword, sortDir, categoryId, model);
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(
            @PathVariable int pageNo, 
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {
        
        int pageSize = 6; // Để hiển thị lưới 3 cột cho đẹp

        // FIX LỖI 14: Đổi sắp xếp từ "name" sang "price"
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by("price").descending() : Sort.by("price").ascending();
        
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        
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

        Page<Product> page = repo.searchByCategoryAndNameFuzzy(categoryId, finalPattern, pageable);
        List<Category> listCategories = categoryRepo.findAll();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listProducts", page.getContent());
        model.addAttribute("sortDir", sortDir); 
        model.addAttribute("listCategories", listCategories);
        
        // FIX LỖI 13: Đổi từ selectedCategoryId sang categoryId cho khớp giao diện
        model.addAttribute("categoryId", categoryId);

        return "index";
    }

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

    @GetMapping("/showNewProductForm")
    public String showNewProductForm(Model model) {
        Product product = new Product();
        List<Category> listCategories = categoryRepo.findAll();
        model.addAttribute("product", product);
        model.addAttribute("listCategories", listCategories);
        return "form";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Integer id, Model model) {
        Product product = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mã sản phẩm không hợp lệ: " + id));
        List<Category> listCategories = categoryRepo.findAll();
        model.addAttribute("product", product);
        model.addAttribute("listCategories", listCategories);
        return "form";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        repo.deleteById(id);
        return "redirect:/";
    }
}