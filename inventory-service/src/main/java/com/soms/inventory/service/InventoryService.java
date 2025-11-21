package com.soms.inventory.service;

import com.soms.inventory.model.InventoryItem;
import com.soms.inventory.repository.InventoryRepository;
import com.soms.inventory.client.ProductClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository repo;
    private final ProductClient productClient;

    public InventoryService(InventoryRepository repo, ProductClient productClient) {
        this.repo = repo;
        this.productClient = productClient;
    }

    public List<InventoryItem> getByProduct(Long productId) {
        return repo.findByProductId(productId);
    }

    public Optional<InventoryItem> getByProductAndWarehouse(Long productId, String warehouseId) {
        return repo.findByProductIdAndWarehouseId(productId, warehouseId);
    }

    public InventoryItem create(InventoryItem item) {
        // optional: verify product exists
        Map<String,Object> prod = productClient.getProduct(item.getProductId());
        // If fallback returns exists=false, you may validate; for now we permit creation
        return repo.save(item);
    }

    @Transactional
    public boolean reserve(Long productId, String warehouseId, int qty) {
        Optional<InventoryItem> opt = repo.findByProductIdAndWarehouseId(productId, warehouseId);
        if (opt.isEmpty()) return false;
        InventoryItem it = opt.get();
        if (it.getQuantity() < qty) return false;
        it.setQuantity(it.getQuantity() - qty);
        repo.save(it);
        return true;
    }

    @Transactional
    public InventoryItem adjust(Long productId, String warehouseId, int qty) {
        InventoryItem it = repo.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseGet(() -> InventoryItem.builder()
                        .productId(productId)
                        .warehouseId(warehouseId)
                        .quantity(0)
                        .build());
        it.setQuantity(qty);
        return repo.save(it);
    }
}
