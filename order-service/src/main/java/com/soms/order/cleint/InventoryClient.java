package com.soms.order.cleint;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "inventory-service", path = "/inventory")
public interface InventoryClient {

    @PostMapping("/reserve")
    Boolean reserve(
            @RequestParam Long productId,
            @RequestParam String warehouseId,
            @RequestParam Integer qty
    );
}