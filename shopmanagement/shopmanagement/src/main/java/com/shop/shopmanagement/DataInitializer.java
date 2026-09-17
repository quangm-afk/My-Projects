package com.shop.shopmanagement;

import com.shop.shopmanagement.entity.Category;
import com.shop.shopmanagement.entity.Product;
import com.shop.shopmanagement.entity.User;
import com.shop.shopmanagement.service.CategoryService;
import com.shop.shopmanagement.service.ProductService;
import com.shop.shopmanagement.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final CategoryService categoryService;
    private final ProductService productService;

    public DataInitializer(UserService userService, CategoryService categoryService, ProductService productService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize Users if database is empty
        if (userService.getAllUsers().isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password("admin123")
                    .fullName("Quản trị viên")
                    .role("ADMIN")
                    .build();
            userService.register(admin);

            User user = User.builder()
                    .username("user")
                    .password("user123")
                    .fullName("Nguyễn Văn Khách")
                    .role("USER")
                    .build();
            userService.register(user);
        }

        // 2. Initialize Categories if empty
        if (categoryService.getAll().isEmpty()) {
            Category ao = Category.builder().name("Áo").build();
            Category quan = Category.builder().name("Quần").build();
            Category hoodie = Category.builder().name("Hoodie").build();
            Category jacket = Category.builder().name("Jacket").build();

            ao = categoryService.save(ao);
            quan = categoryService.save(quan);
            hoodie = categoryService.save(hoodie);
            jacket = categoryService.save(jacket);

            // 3. Initialize Products if empty
            if (productService.getAll().isEmpty()) {
                Product p1 = Product.builder()
                        .name("Áo thun Cotton Premium")
                        .price(180000)
                        .quantity(25)
                        .size("M, L, XL")
                        .color("Trắng, Đen, Xanh")
                        .imageUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500&auto=format&fit=crop&q=60")
                        .category(ao)
                        .build();

                Product p2 = Product.builder()
                        .name("Quần Jeans Slimfit Nam")
                        .price(350000)
                        .quantity(3) // Low stock to demonstrate statistics
                        .size("30, 31, 32")
                        .color("Xanh Sáng, Xanh Tối")
                        .imageUrl("https://images.unsplash.com/photo-1542272604-787c3835535d?w=500&auto=format&fit=crop&q=60")
                        .category(quan)
                        .build();

                Product p3 = Product.builder()
                        .name("Hoodie Classic Unisex")
                        .price(420000)
                        .quantity(15)
                        .size("S, M, L, XL")
                        .color("Xám, Đen, Hồng")
                        .imageUrl("https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=500&auto=format&fit=crop&q=60")
                        .category(hoodie)
                        .build();

                Product p4 = Product.builder()
                        .name("Jacket Kaki Bomber")
                        .price(550000)
                        .quantity(8)
                        .size("M, L, XL")
                        .color("Vàng Rêu, Đen")
                        .imageUrl("https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500&auto=format&fit=crop&q=60")
                        .category(jacket)
                        .build();

                Product p5 = Product.builder()
                        .name("Áo Sơ mi Oxford Trắng")
                        .price(290000)
                        .quantity(2) // Low stock
                        .size("M, L")
                        .color("Trắng")
                        .imageUrl("https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500&auto=format&fit=crop&q=60")
                        .category(ao)
                        .build();

                productService.save(p1);
                productService.save(p2);
                productService.save(p3);
                productService.save(p4);
                productService.save(p5);
            }
        }
    }
}
