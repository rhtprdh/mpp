package com.soms.order.cleint;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "notification-service", path = "/notify")
public interface NotificationClient {

    @PostMapping("/order-confirmation")
    String orderConfirmation(
            @RequestParam Long orderId,
            @RequestParam String email
    );
}