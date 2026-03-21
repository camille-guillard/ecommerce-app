package com.ecommerce.infrastructure.controller;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.service.AuthService;
import com.ecommerce.infrastructure.config.JwtUtil;
import com.ecommerce.infrastructure.dto.AuthResponse;
import com.ecommerce.infrastructure.dto.LoginRequest;
import com.ecommerce.infrastructure.dto.RegisterRequest;
import com.ecommerce.infrastructure.dto.UpdateProfileRequest;
import com.ecommerce.infrastructure.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                    @RequestHeader(value = "Accept-Language", defaultValue = "fr") String lang) {
        try {
            User user = authService.authenticate(request, extractLang(lang));
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(new AuthResponse(token, UserResponse.fromEntity(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request,
                                       @RequestHeader(value = "Accept-Language", defaultValue = "fr") String lang) {
        try {
            User user = authService.register(request, extractLang(lang));
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, UserResponse.fromEntity(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(Authentication authentication, @Valid @RequestBody UpdateProfileRequest request) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = (User) authentication.getPrincipal();
        User updated = authService.updateProfile(user.getId(), request);
        return ResponseEntity.ok(UserResponse.fromEntity(updated));
    }

    private String extractLang(String acceptLanguage) {
        if (acceptLanguage != null && acceptLanguage.toLowerCase().startsWith("en")) {
            return "en";
        }
        return "fr";
    }
}
