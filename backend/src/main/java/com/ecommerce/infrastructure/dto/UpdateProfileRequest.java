package com.ecommerce.infrastructure.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank @Email String email,
        String firstName,
        String lastName,
        String street,
        String city,
        String postalCode
) {
}
