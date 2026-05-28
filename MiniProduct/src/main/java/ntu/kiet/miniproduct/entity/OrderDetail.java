package ntu.kiet.miniproduct.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Nhiều dòng chi tiết nằm trong cùng 1 Đơn hàng
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Nhiều dòng chi tiết có thể chứa cùng 1 sản phẩm Điện thoại giống nhau
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private double price; // Lưu giá sản phẩm tại thời điểm mua (đề phòng sau này hãng đổi giá)
    private int quantity; // Số lượng mua của sản phẩm này

    // Constructor mặc định (Bắt buộc)
    public OrderDetail() {
    }

    // Constructor có tham số để tiện khởi tạo dữ liệu sau này
    public OrderDetail(Order order, Product product, double price, int quantity) {
        this.order = order;
        this.product = product;
        this.price = price;
        this.quantity = quantity;
    }

    // --- GETTER VÀ SETTER ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}