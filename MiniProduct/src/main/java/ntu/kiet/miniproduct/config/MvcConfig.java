package ntu.kiet.miniproduct.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Chỉ định thư mục lưu ảnh tên là "product-images"
        Path uploadDir = Paths.get("product-images");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/product-images/**").addResourceLocations("file:/" + uploadPath + "/");
    }
}