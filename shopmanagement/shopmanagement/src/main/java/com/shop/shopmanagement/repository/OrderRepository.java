package com.shop.shopmanagement.repository;

import com.shop.shopmanagement.entity.Order;
import com.shop.shopmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findAllByOrderByOrderDateDesc();
}
