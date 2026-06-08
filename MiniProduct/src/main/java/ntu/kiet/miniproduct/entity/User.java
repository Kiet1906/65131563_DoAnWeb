package ntu.kiet.miniproduct.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String role; // Ví dụ: "ROLE_ADMIN" - Bắt buộc giữ để đăng nhập

    // === BỔ SUNG 3 TRƯỜNG TỪ BÁO CÁO ===
    @Column(nullable = false)
    private String fullname;
    
    @Column(nullable = false)
    private String phone;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String address;

    public User() {}

    // Cập nhật lại Constructor
    public User(String username, String password, String role, String fullname, String phone, String address) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullname = fullname;
        this.phone = phone;
        this.address = address;
    }

    // === Getter và Setter cũ ===
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    // === Getter và Setter mới bổ sung ===
    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}