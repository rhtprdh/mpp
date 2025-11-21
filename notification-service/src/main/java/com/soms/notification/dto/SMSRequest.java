package com.soms.notification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SMSRequest {
    @NotBlank
    private String phone;

    @NotBlank
    private String message;
}
