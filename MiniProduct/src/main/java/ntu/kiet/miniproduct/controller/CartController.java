package ntu.kiet.miniproduct.controller;

import jakarta.servlet.http.HttpSession;
import ntu.kiet.miniproduct.dto.CartItem; // Đã cập nhật import chuẩn xác theo gói dto của bạn
import ntu.kiet.miniproduct.entity.Product;
import ntu.kiet.miniproduct.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepo;

    public CartController(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    // 1. Thêm sản phẩm vào giỏ hàng (lưu tạm vào Session trình duyệt)
    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Integer id, HttpSession session, RedirectAttributes ra) {
        Product product = productRepo.findById(id).orElse(null);
        if (product == null || product.getQuantity() <= 0) {
            ra.addFlashAttribute("error", "Sản phẩm không hợp lệ hoặc đã hết hàng!");
            return "redirect:/";
        }

        // Lấy giỏ hàng từ session (Nếu người dùng mới vào web thì tạo mới một HashMap)
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Nếu điện thoại này đã có sẵn trong giỏ, tăng số lượng thêm 1
        if (cart.containsKey(id)) {
            CartItem item = cart.get(id);
            if (item.getQuantity() < product.getQuantity()) {
                item.setQuantity(item.getQuantity() + 1);
            } else {
                ra.addFlashAttribute("error", "Không thể thêm vì vượt quá số lượng máy hiện có trong kho!");
                return "redirect:/";
            }
        } else {
            // Nếu sản phẩm chưa có, tạo dòng mới với số lượng mặc định là 1
            cart.put(id, new CartItem(product, 1));
        }

        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", calculateTotalQuantity(cart));

        ra.addFlashAttribute("success", "Đã thêm " + product.getName() + " vào giỏ hàng thành công!");
        return "redirect:/";
    }

    // 2. Xem chi tiết giao diện trang giỏ hàng
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // Tính tổng tiền toàn bộ hóa đơn
        double totalAmount = cart.values().stream().mapToDouble(CartItem::getSubtotal).sum();

        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalAmount", totalAmount);
        return "cart"; // Trả về trang cart.html
    }

    // 3. Cập nhật số lượng trực tiếp bằng ô nhập số trên giao diện
    @PostMapping("/update")
    public String updateCart(@RequestParam("id") Integer id, @RequestParam("quantity") int quantity, HttpSession session, RedirectAttributes ra) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        Product product = productRepo.findById(id).orElse(null);

        if (cart != null && cart.containsKey(id) && product != null) {
            if (quantity <= 0) {
                cart.remove(id); // Nếu hạ số lượng về 0 hoặc âm, xóa luôn sản phẩm khỏi giỏ
            } else if (quantity > product.getQuantity()) {
                ra.addFlashAttribute("error", "Sản phẩm " + product.getName() + " chỉ còn tối đa " + product.getQuantity() + " chiếc trong kho!");
                cart.get(id).setQuantity(product.getQuantity()); // Ép về số lượng max trong kho
            } else {
                cart.get(id).setQuantity(quantity);
            }
            session.setAttribute("cart", cart);
            session.setAttribute("cartCount", calculateTotalQuantity(cart));
        }
        return "redirect:/cart";
    }

    // 4. Xóa một sản phẩm ra khỏi giỏ thông qua nút Thùng rác
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

    // 5. XỬ LÝ CHẤT LƯỢNG NHẤT: Bấm nút Đặt hàng (Trừ trực tiếp số lượng tồn trong kho Database)
    @PostMapping("/checkout")
    public String checkout(HttpSession session, RedirectAttributes ra) {
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            ra.addFlashAttribute("error", "Giỏ hàng đang trống rỗng, không thể tiến hành đặt hàng!");
            return "redirect:/cart";
        }

        // Vòng lặp kiểm tra kho hàng real-time đề phòng trường hợp có người khác đã mua hết máy trước đó
        for (CartItem item : cart.values()) {
            Product dbProduct = productRepo.findById(item.getProduct().getId()).orElse(null);
            if (dbProduct == null || dbProduct.getQuantity() < item.getQuantity()) {
                ra.addFlashAttribute("error", "Đặt hàng thất bại! Sản phẩm " + (dbProduct != null ? dbProduct.getName() : "này") + " đã bị thay đổi số lượng kho hoặc hết hàng.");
                return "redirect:/cart";
            }
            
            // Thực hiện trừ kho trực tiếp trong Database
            dbProduct.setQuantity(dbProduct.getQuantity() - item.getQuantity());
            productRepo.save(dbProduct); // Ghi đè cập nhật lại bảng products
        }

        // Xóa sạch thông tin giỏ hàng trong Session sau khi thanh toán thành công
        session.removeAttribute("cart");
        session.removeAttribute("cartCount");

        ra.addFlashAttribute("success", "🎉 Chúc mừng! Bạn đã đặt hàng thành công. Hệ thống đã cập nhật giảm trừ số lượng trong kho máy!");
        return "redirect:/";
    }

    // Hàm tiện ích nội bộ để tính tổng số chiếc máy đang có trong giỏ hàng
    private int calculateTotalQuantity(Map<Integer, CartItem> cart) {
        return cart.values().stream().mapToInt(CartItem::getQuantity).sum();
    }
}