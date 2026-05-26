package ntu.kiet.miniproduct.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "products") // Tên bảng trong database sẽ là products
public class Product {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự động tăng
    private Integer id;
	
    private String name;
    private double price;
    private int quantity;

    @Column(nullable = true, length = 64)
    private String image;

    // THÊM MỚI: Thiết lập mối quan hệ Nhiều-Một (Many-to-One) đến bảng Category
    @ManyToOne
    @JoinColumn(name = "category_id") // Tạo cột khóa ngoại category_id trong bảng products
    private Category category;

    // BẮT BUỘC: Tạo constructor rỗng (Spring cần cái này)
    public Product() {
    }

    // BẮT BUỘC: Tạo các Getter và Setter để đọc/ghi dữ liệu
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    // THÊM MỚI: Getter và Setter cho đối tượng Category phục vụ liên kết danh mục
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}