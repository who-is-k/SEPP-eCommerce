package com.ecommerce.customerservice.application.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String phoneNumber;
    private String street;
    private String city;
    private String postcode;
}
