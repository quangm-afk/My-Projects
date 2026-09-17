package com.shop.shopmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private double price;

    private int quantity; // Inventory stock

    private String size; // e.g., "S, M, L, XL"

    private String color; // e.g., "Red, Blue, Black"

    @Column(length = 1000)
    private String imageUrl; // Can store inline SVG, external image link, or uploads

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;
}
