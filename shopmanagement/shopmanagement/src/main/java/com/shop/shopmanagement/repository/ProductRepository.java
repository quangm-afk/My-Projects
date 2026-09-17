package com.shop.shopmanagement.repository;

import com.shop.shopmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategory_Id(Long categoryId);
    List<Product> findByCategory_IdAndNameContainingIgnoreCase(Long categoryId, String name);
    List<Product> findByQuantityLessThan(int threshold);
}
