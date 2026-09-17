package com.shop.shopmanagement.service;

import com.shop.shopmanagement.entity.Product;
import com.shop.shopmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAll() {
        return repo.findAll();
    }

    public Optional<Product> getById(Long id) {
        return repo.findById(id);
    }

    public void save(Product p) {
        repo.save(p);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<Product> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return repo.findAll();
        }
        return repo.findByNameContainingIgnoreCase(name.trim());
    }

    public List<Product> getByCategory(Long categoryId) {
        return repo.findByCategory_Id(categoryId);
    }

    public List<Product> searchByCategoryAndName(Long categoryId, String name) {
        if (categoryId == null || categoryId == 0) {
            return searchByName(name);
        }
        if (name == null || name.trim().isEmpty()) {
            return getByCategory(categoryId);
        }
        return repo.findByCategory_IdAndNameContainingIgnoreCase(categoryId, name.trim());
    }

    public List<Product> getLowStock(int threshold) {
        return repo.findByQuantityLessThan(threshold);
    }
}
