package com.shop.shopmanagement.service;

import com.shop.shopmanagement.entity.Order;
import com.shop.shopmanagement.entity.OrderItem;
import com.shop.shopmanagement.entity.Product;
import com.shop.shopmanagement.entity.User;
import com.shop.shopmanagement.repository.OrderItemRepository;
import com.shop.shopmanagement.repository.OrderRepository;
import com.shop.shopmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public Order placeOrder(User user, Long productId, int quantity, String address, String phone) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm!"));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Số lượng tồn kho không đủ! Chỉ còn " + product.getQuantity() + " sản phẩm.");
        }

        // Deduct inventory stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        double totalAmount = product.getPrice() * quantity;

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status("PENDING")
                .shippingAddress(address)
                .phoneNumber(phone)
                .totalAmount(totalAmount)
                .orderItems(new ArrayList<>())
                .build();

        Order savedOrder = orderRepository.save(order);

        OrderItem orderItem = OrderItem.builder()
                .order(savedOrder)
                .product(product)
                .quantity(quantity)
                .price(product.getPrice())
                .build();

        orderItemRepository.save(orderItem);
        savedOrder.getOrderItems().add(orderItem);

        return savedOrder;
    }

    @Transactional
    public void updateStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng!"));

        String oldStatus = order.getStatus();
        if ("CANCELLED".equals(status) && !"CANCELLED".equals(oldStatus)) {
            // Restock items since order is cancelled
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        } else if (!"CANCELLED".equals(status) && "CANCELLED".equals(oldStatus)) {
            // Re-deduct if changing from cancelled back to active
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new IllegalArgumentException("Không thể khôi phục đơn hàng! Số lượng tồn kho của '" + product.getName() + "' không đủ.");
                }
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        orderRepository.save(order);
    }
}
