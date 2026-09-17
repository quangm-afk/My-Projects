package com.shop.shopmanagement.controller;

import com.shop.shopmanagement.entity.Product;
import com.shop.shopmanagement.entity.User;
import com.shop.shopmanagement.service.CategoryService;
import com.shop.shopmanagement.service.OrderService;
import com.shop.shopmanagement.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public ProductController(ProductService productService, CategoryService categoryService, OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String index(@RequestParam(required = false) Long categoryId,
                        @RequestParam(required = false) String search,
                        HttpSession modelSession,
                        Model model) {
        model.addAttribute("products", productService.searchByCategoryAndName(categoryId, search));
        model.addAttribute("categories", categoryService.getAll());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("search", search);
        model.addAttribute("currentUser", modelSession.getAttribute("user"));
        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm!"));
            model.addAttribute("product", product);
            model.addAttribute("currentUser", session.getAttribute("user"));
            return "detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        }
    }

    @PostMapping("/order/place")
    public String placeOrder(@RequestParam Long productId,
                             @RequestParam int quantity,
                             @RequestParam String address,
                             @RequestParam String phone,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để đặt hàng!");
            return "redirect:/login";
        }

        try {
            orderService.placeOrder(user, productId, quantity, address, phone);
            redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công! Đơn hàng đang được chờ xử lý.");
            return "redirect:/orders";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/product/" + productId;
        }
    }

    @GetMapping("/orders")
    public String customerOrders(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem lịch sử đơn hàng!");
            return "redirect:/login";
        }

        model.addAttribute("orders", orderService.getOrdersByUser(user));
        model.addAttribute("currentUser", user);
        return "orders";
    }
}
