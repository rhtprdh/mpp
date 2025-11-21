package com.soms.inventory.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

@Component
public class ProductClientFallback implements ProductClient {
    @Override
    public Map<String, Object> getProduct(Long id) {
        // fallback: return null-ish or throw a runtime; we return null fields for graceful handling
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("exists", false);
        return m;
    }
}
