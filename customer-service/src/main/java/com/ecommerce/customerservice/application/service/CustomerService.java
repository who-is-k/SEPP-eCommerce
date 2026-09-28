package com.ecommerce.customerservice.application.service;

import com.ecommerce.customerservice.application.dto.request.*;
import com.ecommerce.customerservice.application.dto.response.*;

public interface CustomerService {

    CustomerResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    CustomerResponse getById(Long id);

    CustomerResponse updateProfile(Long id, UpdateProfileRequest request);

    void deactivate(Long id);

    boolean verifyExists(Long id);
}
