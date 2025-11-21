package com.soms.notification.service;


import org.springframework.stereotype.Service;

import com.soms.notification.dto.EmailRequest;
import com.soms.notification.dto.SMSRequest;

@Service
public class NotificationService {

    public String sendEmail(EmailRequest req) {
        System.out.println("Sending EMAIL → " + req.getTo());
        // TODO: Integrate with real email provider (SendGrid / SMTP / MailGun)
        return "Email sent successfully to " + req.getTo();
    }

    public String sendSMS(SMSRequest req) {
        System.out.println("Sending SMS → " + req.getPhone());
        // TODO: Integrate with Twilio
        return "SMS sent to " + req.getPhone();
    }

    public String orderConfirmation(Long orderId, String email) {
        System.out.println("Sending Order Confirmation for Order " + orderId);
        return "Order confirmation sent to " + email;
    }
}