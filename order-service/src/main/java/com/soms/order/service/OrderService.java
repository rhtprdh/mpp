package com.soms.order.service;


import org.springframework.stereotype.Service;

import com.soms.order.cleint.InventoryClient;
import com.soms.order.cleint.NotificationClient;
import com.soms.order.cleint.ProductClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import java.util.Map;

@Service
public class OrderService {

    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final NotificationClient notificationClient;

    public OrderService(
            ProductClient productClient,
            InventoryClient inventoryClient,
            NotificationClient notificationClient) {
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
        this.notificationClient = notificationClient;
    }

    public String createOrder(Long productId, Integer qty, String email) {

        // 1. Get Product details
        Map<String, Object> product = productClient.getProduct(productId);

        if (product == null || product.get("id") == null) {
            return "Product not found";
        }

        // 2. Reserve Stock
        boolean reserved = inventoryClient.reserve(productId, "WH1", qty);
        if (!reserved) {
            return "Insufficient stock";
        }

        // 3. Send Notification
        String result = notificationClient.orderConfirmation(1001L, email);

        return "Order created successfully. Notification: " + result;
    }
    
    
//    Resilience4J Circuit Breakers
    @Retry(name = "productRetry", fallbackMethod = "productFallback")
    @CircuitBreaker(name = "productService", fallbackMethod = "productFallback")
    public Map<String, Object> safeGetProduct(Long productId) {
        return productClient.getProduct(productId);
    }

    public Map<String, Object> productFallback(Long productId, Throwable ex) {
        System.out.println("PRODUCT FALLBACK TRIGGERED: " + ex.getMessage());
        return Map.of("id", null, "message", "product service unavailable");
    }
}