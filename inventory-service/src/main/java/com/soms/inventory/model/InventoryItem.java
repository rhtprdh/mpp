package com.soms.inventory.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventory_items", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id","warehouse_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;        // product-service uses Long ids

    @Column(name = "warehouse_id", nullable = false)
    private String warehouseId;    // simple string (e.g., "WH1")

    @Column(nullable = false)
    private Integer quantity;
}