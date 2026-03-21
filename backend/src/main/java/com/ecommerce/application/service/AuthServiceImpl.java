package com.ecommerce.application.service;

import com.ecommerce.domain.model.Role;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.RoleRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.domain.service.AuthService;
import com.ecommerce.infrastructure.dto.LoginRequest;
import com.ecommerce.infrastructure.dto.RegisterRequest;
import com.ecommerce.infrastructure.dto.UpdateProfileRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Map<String, Map<String, String>> MESSAGES = Map.of(
            "fr", Map.of(
                    "INVALID_CREDENTIALS", "Identifiants invalides",
                    "USERNAME_TAKEN", "Ce nom d'utilisateur est déjà pris",
                    "EMAIL_TAKEN", "Cet email est déjà utilisé",
                    "USER_NOT_FOUND", "Utilisateur non trouvé"
            ),
            "en", Map.of(
                    "INVALID_CREDENTIALS", "Invalid credentials",
                    "USERNAME_TAKEN", "This username is already taken",
                    "EMAIL_TAKEN", "This email is already in use",
                    "USER_NOT_FOUND", "User not found"
            )
    );

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User authenticate(LoginRequest request, String lang) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException(msg(lang, "INVALID_CREDENTIALS")));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException(msg(lang, "INVALID_CREDENTIALS"));
        }

        return user;
    }

    @Override
    public User register(RegisterRequest request, String lang) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(msg(lang, "USERNAME_TAKEN"));
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(msg(lang, "EMAIL_TAKEN"));
        }

        User user = new User(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.street(),
                request.city(),
                request.postalCode()
        );

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Role USER not found"));
        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    @Override
    public User updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setStreet(request.street());
        user.setCity(request.city());
        user.setPostalCode(request.postalCode());

        return userRepository.save(user);
    }

    private String msg(String lang, String key) {
        return MESSAGES.getOrDefault(lang, MESSAGES.get("fr")).getOrDefault(key, key);
    }
}
