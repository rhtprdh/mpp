package com.soms.notification.controller;



import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.soms.notification.dto.EmailRequest;
import com.soms.notification.dto.SMSRequest;
import com.soms.notification.service.NotificationService;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @PostMapping("/email")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest req) {
        return ResponseEntity.ok(service.sendEmail(req));
    }

    @PostMapping("/sms")
    public ResponseEntity<?> sendSMS(@Valid @RequestBody SMSRequest req) {
        return ResponseEntity.ok(service.sendSMS(req));
    }

    @PostMapping("/order-confirmation")
    public ResponseEntity<?> orderConfirm(
            @RequestParam Long orderId,
            @RequestParam String email
    ) {
        return ResponseEntity.ok(service.orderConfirmation(orderId, email));
    }
}