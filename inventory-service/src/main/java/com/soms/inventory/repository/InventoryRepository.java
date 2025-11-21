package com.soms.inventory.repository;

import com.soms.inventory.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByProductId(Long productId);
    Optional<InventoryItem> findByProductIdAndWarehouseId(Long productId, String warehouseId);
}