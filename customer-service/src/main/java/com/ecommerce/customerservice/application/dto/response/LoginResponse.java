package com.ecommerce.customerservice.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Long customerId;
    private String fullName;
    private String email;
    private String token;
}
