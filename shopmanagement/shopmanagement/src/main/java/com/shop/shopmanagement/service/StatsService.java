package com.shop.shopmanagement.service;

import com.shop.shopmanagement.entity.Order;
import com.shop.shopmanagement.entity.Product;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StatsService {

    private final ProductService productService;
    private final OrderService orderService;

    public StatsService(ProductService productService, OrderService orderService) {
        this.productService = productService;
        this.orderService = orderService;
    }

    public long getTotalProducts() {
        return productService.getAll().size();
    }

    public long getLowStockProductsCount() {
        return productService.getLowStock(5).size();
    }

    public List<Product> getLowStockProducts() {
        return productService.getLowStock(5);
    }

    public long getTotalOrders() {
        return orderService.getAllOrders().size();
    }

    public double getTotalRevenue() {
        return orderService.getAllOrders().stream()
                .filter(o -> !"CANCELLED".equals(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }
}
