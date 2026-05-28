package ntu.kiet.miniproduct.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    private String status; // Mặc định: "PENDING" (Chờ xử lý)

    // Quan hệ Nhiều - Một: Nhiều đơn hàng có thể thuộc về 1 người dùng (User)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Quan hệ Một - Nhiều: Một đơn hàng sẽ có nhiều dòng chi tiết đơn hàng (OrderDetail)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    // Constructor mặc định (Bắt buộc)
    public Order() {
        this.orderDate = LocalDateTime.now(); // Tự động lấy thời gian hiện tại khi tạo đơn
        this.status = "PENDING";
    }

    // --- GETTER VÀ SETTER ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<OrderDetail> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }
}