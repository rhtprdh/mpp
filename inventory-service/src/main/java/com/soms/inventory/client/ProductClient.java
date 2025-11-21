package com.soms.inventory.client;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;
import java.util.Map;

@FeignClient(name = "product-service", path = "/", fallback = ProductClientFallback.class)
public interface ProductClient {
    @GetMapping("/products/{id}")
    Map<String, Object> getProduct(@PathVariable("id") Long id);
}
