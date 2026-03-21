package com.ecommerce.infrastructure.dto;

public record AuthResponse(String token, UserResponse user) {
}
