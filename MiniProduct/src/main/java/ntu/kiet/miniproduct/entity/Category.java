package ntu.kiet.miniproduct.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories") // Tên bảng trong database sẽ là categories
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID tự động tăng
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // Tên danh mục (Ví dụ: iPhone, Samsung, Oppo, Xiaomi...)

    // Quan hệ Một - Nhiều: Một danh mục có nhiều sản phẩm
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    // Getter và Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}