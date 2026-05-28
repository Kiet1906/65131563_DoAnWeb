package ntu.kiet.miniproduct.controller;

import jakarta.servlet.http.HttpSession;
import ntu.kiet.miniproduct.dto.CartItem;
import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.entity.Order;
import ntu.kiet.miniproduct.entity.OrderDetail;
import ntu.kiet.miniproduct.entity.User;
import ntu.kiet.miniproduct.repository.ProductRepository;
import ntu.kiet.miniproduct.repository.OrderRepository;
import ntu.kiet.miniproduct.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepo;
    private final OrderRepository orderRepo; // Thêm Repo để lưu hóa đơn
    private final UserRepository userRepo;   // Thêm Repo để lấy thông tin user đăng nhập

    // Inject các Repository
    public CartController(ProductRepository productRepo, OrderRepository orderRepo, UserRepository userRepo) {
        this.productRepo = productRepo;
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        Product product = productRepo.findById(id).orElse(null);
        if (product == null || product.getQuantity() <= 0) {
            ra.addFlashAttribute("error", "Sản phẩm không hợp lệ hoặc đã hết hàng!");
            return "redirect:/";
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        if (cart.containsKey(id)) {
            CartItem item = cart.get(id);
            if (item.getQuantity() < product.getQuantity()) {
                item.setQuantity(item.getQuantity() + 1);
            } else {
                ra.addFlashAttribute("error", "Không thể thêm vì vượt quá số lượng máy hiện có trong kho!");
                return "redirect:/";
            }
        } else {
            cart.put(id, new CartItem(product, 1));
        }

        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", calculateTotalQuantity(cart));

        ra.addFlashAttribute("success", "Đã thêm " + product.getName() + " vào giỏ hàng thành công!");
        return "redirect:/";
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        double totalAmount = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalAmount", totalAmount);
        return "cart"; 
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("id") Integer id, @RequestParam("quantity") int quantity, HttpSession session, RedirectAttributes ra) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        Product product = productRepo.findById(id).orElse(null);

        if (cart != null && cart.containsKey(id) && product != null) {
            if (quantity <= 0) {
                cart.remove(id); 
            } else if (quantity > product.getQuantity()) {
                ra.addFlashAttribute("error", "Sản phẩm " + product.getName() + " chỉ còn tối đa " + product.getQuantity() + " chiếc trong kho!");
                cart.get(id).setQuantity(product.getQuantity()); 
            } else {
                cart.get(id).setQuantity(quantity);
            }
            session.setAttribute("cart", cart);
            session.setAttribute("cartCount", calculateTotalQuantity(cart));
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Integer id, HttpSession session) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart != null && cart.containsKey(id)) {
            cart.remove(id);
            session.setAttribute("cart", cart);
            session.setAttribute("cartCount", calculateTotalQuantity(cart));
        }
        return "redirect:/cart";
    }

    // 🔴 ĐÂY LÀ HÀM QUAN TRỌNG NHẤT ĐÃ ĐƯỢC CẬP NHẬT ĐỂ LƯU XUỐNG DB
    @PostMapping("/checkout")
    public String checkout(
            @RequestParam("fullname") String fullname,
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            HttpSession session, RedirectAttributes ra, Principal principal) {

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng đang trống rỗng, không thể tiến hành đặt hàng!");
            return "redirect:/cart";
        }

        // 1. Kiểm tra kho hàng real-time
        for (CartItem item : cart.values()) {
            Product dbProduct = productRepo.findById(item.getProduct().getId()).orElse(null);
            if (dbProduct == null || dbProduct.getQuantity() < item.getQuantity()) {
                ra.addFlashAttribute("error", "Đặt hàng thất bại! Sản phẩm " + (dbProduct != null ? dbProduct.getName() : "này") + " đã hết hoặc không đủ số lượng.");
                return "redirect:/cart";
            }
        }

        // 2. Tạo đối tượng hóa đơn (Order)
        Order newOrder = new Order();
        newOrder.setFullname(fullname);
        newOrder.setAddress(address);
        newOrder.setPhone(phone);
        
        // Nếu user đã đăng nhập, gắn tài khoản vào hóa đơn
        if (principal != null) {
            User user = userRepo.findByUsername(principal.getName()).orElse(null);
            newOrder.setUser(user);
        }

        // 3. Xử lý chi tiết hóa đơn (OrderDetail) và Trừ kho
        List<OrderDetail> orderDetailsList = new ArrayList<>();
        double totalPrice = 0;

        for (CartItem item : cart.values()) {
            Product dbProduct = productRepo.findById(item.getProduct().getId()).get();
            
            // Trừ kho
            dbProduct.setQuantity(dbProduct.getQuantity() - item.getQuantity());
            productRepo.save(dbProduct);

            // Tạo chi tiết đơn hàng
            OrderDetail detail = new OrderDetail(newOrder, dbProduct, dbProduct.getPrice(), item.getQuantity());
            orderDetailsList.add(detail);
            
            totalPrice += item.getSubtotal();
        }

        // Gắn danh sách chi tiết vào hóa đơn và cập nhật tổng tiền
        newOrder.setOrderDetails(orderDetailsList);
        newOrder.setTotalPrice(totalPrice);

        // Lưu toàn bộ (nhờ CascadeType.ALL trong entity Order, orderDetails sẽ tự động được lưu theo)
        orderRepo.save(newOrder);

        // 4. Dọn dẹp session
        session.removeAttribute("cart");
        session.removeAttribute("cartCount");

        ra.addFlashAttribute("message", "🎉 Đặt hàng thành công! Đơn hàng của bạn đã được chuyển tới Admin kiểm duyệt.");
        return "redirect:/";
    }

    private int calculateTotalQuantity(Map<Integer, CartItem> cart) {
        return cart.values().stream().mapToInt(CartItem::getQuantity).sum();
    }
}