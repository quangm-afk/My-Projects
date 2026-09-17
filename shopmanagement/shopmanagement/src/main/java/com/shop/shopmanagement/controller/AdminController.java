package com.shop.shopmanagement.controller;

import com.shop.shopmanagement.entity.Category;
import com.shop.shopmanagement.entity.Product;
import com.shop.shopmanagement.entity.User;
import com.shop.shopmanagement.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StatsService statsService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final UserService userService;

    public AdminController(StatsService statsService, ProductService productService, CategoryService categoryService, OrderService orderService, UserService userService) {
        this.statsService = statsService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
        this.userService = userService;
    }

    private boolean checkAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập tài khoản quản trị.");
            return false;
        }
        if (!"ADMIN".equals(user.getRole())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập trang quản trị.");
            return false;
        }
        return true;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        model.addAttribute("totalProducts", statsService.getTotalProducts());
        model.addAttribute("lowStockCount", statsService.getLowStockProductsCount());
        model.addAttribute("totalOrders", statsService.getTotalOrders());
        model.addAttribute("totalRevenue", statsService.getTotalRevenue());
        model.addAttribute("lowStockProducts", statsService.getLowStockProducts());
        model.addAttribute("currentUser", session.getAttribute("user"));
        return "admin/dashboard";
    }

    // --- PRODUCT MANAGEMENT ---

    @GetMapping("/products")
    public String productsPage(@RequestParam(required = false) String search,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        model.addAttribute("products", productService.searchByName(search));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("search", search);
        model.addAttribute("currentUser", session.getAttribute("user"));
        return "admin/products";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product,
                              @RequestParam Long categoryId,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            Category category = categoryService.getById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục!"));
            product.setCategory(category);
            productService.save(product);
            redirectAttributes.addFlashAttribute("success", "Lưu sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa sản phẩm! Có thể sản phẩm này đã nằm trong đơn hàng.");
        }
        return "redirect:/admin/products";
    }

    // --- CATEGORY MANAGEMENT ---

    @GetMapping("/categories")
    public String categoriesPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("currentUser", session.getAttribute("user"));
        return "admin/categories";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Lưu danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Tên danh mục đã tồn tại hoặc không hợp lệ!");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa danh mục thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục này! Vẫn còn sản phẩm thuộc danh mục này.");
        }
        return "redirect:/admin/categories";
    }

    // --- ORDER MANAGEMENT ---

    @GetMapping("/orders")
    public String ordersPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("currentUser", session.getAttribute("user"));
        return "admin/orders";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam Long orderId,
                                    @RequestParam String status,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            orderService.updateStatus(orderId, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders";
    }

    // --- USER MANAGEMENT ---

    @GetMapping("/users")
    public String usersPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("currentUser", session.getAttribute("user"));
        return "admin/users";
    }

    @GetMapping("/users/toggle-lock/{id}")
    public String toggleUserLock(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            User currentAdmin = (User) session.getAttribute("user");
            if (currentAdmin.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("error", "Bạn không thể tự khóa tài khoản của chính mình!");
                return "redirect:/admin/users";
            }
            userService.toggleLock(id);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái tài khoản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/change-role")
    public String changeUserRole(@RequestParam Long userId,
                                 @RequestParam String role,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (!checkAdmin(session, redirectAttributes)) {
            return "redirect:/login";
        }
        try {
            User currentAdmin = (User) session.getAttribute("user");
            if (currentAdmin.getId().equals(userId)) {
                redirectAttributes.addFlashAttribute("error", "Bạn không thể tự tước quyền quản trị của mình!");
                return "redirect:/admin/users";
            }
            userService.changeRole(userId, role);
            redirectAttributes.addFlashAttribute("success", "Cập nhật vai trò người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
