package com.ecommerce.infrastructure.dto;

import com.ecommerce.domain.model.Role;
import com.ecommerce.domain.model.User;

import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        String firstName,
        String lastName,
        String street,
        String city,
        String postalCode,
        List<String> roles
) {
    public static UserResponse fromEntity(User user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getStreet(),
                user.getCity(),
                user.getPostalCode(),
                roleNames
        );
    }
}
