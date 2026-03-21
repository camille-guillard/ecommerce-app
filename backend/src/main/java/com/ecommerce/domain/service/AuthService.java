package com.ecommerce.domain.service;

import com.ecommerce.domain.model.User;
import com.ecommerce.infrastructure.dto.LoginRequest;
import com.ecommerce.infrastructure.dto.RegisterRequest;
import com.ecommerce.infrastructure.dto.UpdateProfileRequest;

public interface AuthService {

    User authenticate(LoginRequest request, String lang);

    User register(RegisterRequest request, String lang);

    User updateProfile(Long userId, UpdateProfileRequest request);
}
