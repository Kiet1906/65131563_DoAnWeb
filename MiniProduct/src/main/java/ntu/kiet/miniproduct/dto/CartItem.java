package ntu.kiet.miniproduct.dto;

import ntu.kiet.miniproduct.entity.Product;

public class CartItem {
    
    // Thay vì khai báo rời rạc, ta nhúng thẳng đối tượng Product vào đây
    private Product product;
    private int quantity; // Số lượng khách chọn mua

    public CartItem() {
    }

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    // Hàm tự động tính toán tổng tiền của riêng dòng sản phẩm này
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    // --- GETTER VÀ SETTER ---
    public Product getProduct() { 
        return product; 
    }
    
    public void setProduct(Product product) { 
        this.product = product; 
    }

    public int getQuantity() { 
        return quantity; 
    }
    
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
    }
}