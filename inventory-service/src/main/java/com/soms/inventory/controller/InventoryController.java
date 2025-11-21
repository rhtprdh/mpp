package com.soms.inventory.controller;
import com.soms.inventory.model.InventoryItem;
import com.soms.inventory.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryItem>> byProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getByProduct(productId));
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<InventoryItem> byProductAndWarehouse(@PathVariable Long productId, @PathVariable String warehouseId) {
        Optional<InventoryItem> opt = service.getByProductAndWarehouse(productId, warehouseId);
        return opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<InventoryItem> create(@RequestBody InventoryItem item) {
        return ResponseEntity.ok(service.create(item));
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserve(@RequestParam Long productId, @RequestParam String warehouseId, @RequestParam int qty) {
        boolean ok = service.reserve(productId, warehouseId, qty);
        if (!ok) return ResponseEntity.badRequest().body("Insufficient stock or not found");
        return ResponseEntity.ok("Reserved");
    }

    @PutMapping("/adjust")
    public ResponseEntity<InventoryItem> adjust(@RequestParam Long productId, @RequestParam String warehouseId, @RequestParam int qty) {
        return ResponseEntity.ok(service.adjust(productId, warehouseId, qty));
    }
}