package ntu.kiet.miniproduct.controller;

import ntu.kiet.miniproduct.entity.Order;
import ntu.kiet.miniproduct.entity.OrderDetail;
import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.repository.OrderRepository;
import ntu.kiet.miniproduct.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo; // 1. Bổ sung repo để tương tác với kho hàng

    // 2. Inject cả 2 Repository vào Constructor
    public OrderController(OrderRepository orderRepo, ProductRepository productRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @GetMapping
    public String listOrders(Model model) {
        List<Order> listOrders = orderRepo.findAllByOrderByOrderDateDesc();
        model.addAttribute("listOrders", listOrders);
        return "orders"; 
    }

    @GetMapping("/{id}")
    public String viewOrderDetails(@PathVariable Integer id, Model model) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mã đơn hàng không hợp lệ: " + id));
        model.addAttribute("order", order);
        return "order-details"; 
    }

    @PostMapping("/{id}/status")
    public String updateOrderStatus(@PathVariable Integer id, @RequestParam String status, RedirectAttributes ra) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mã đơn hàng không hợp lệ: " + id));
        
        // 🔴 LOGIC QUAN TRỌNG: HOÀN TRẢ KHO KHI HỦY ĐƠN
        // Nếu Admin bấm Hủy đơn VÀ đơn này trước đó chưa bị hủy
        if ("CANCELLED".equals(status) && !"CANCELLED".equals(order.getStatus())) {
            
            // Duyệt qua từng chi tiết máy trong hóa đơn
            for (OrderDetail detail : order.getOrderDetails()) {
                Product product = detail.getProduct();
                
                // Lấy số lượng đang có trong kho + số lượng khách đã đặt
                product.setQuantity(product.getQuantity() + detail.getQuantity());
                
                // Lưu lại vào database
                productRepo.save(product);
            }
        }
        
        // Cập nhật trạng thái mới cho hóa đơn và lưu lại
        order.setStatus(status);
        orderRepo.save(order);
        
        ra.addFlashAttribute("message", "Cập nhật trạng thái đơn hàng thành công!");
        return "redirect:/admin/orders/" + id;
    }
}