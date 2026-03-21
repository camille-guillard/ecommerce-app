package com.ecommerce.application.service;

import com.ecommerce.application.service.AuthServiceImpl;
import com.ecommerce.domain.model.Role;
import com.ecommerce.domain.model.User;
import com.ecommerce.domain.repository.RoleRepository;
import com.ecommerce.domain.repository.UserRepository;
import com.ecommerce.infrastructure.dto.LoginRequest;
import com.ecommerce.infrastructure.dto.RegisterRequest;
import com.ecommerce.infrastructure.dto.UpdateProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void authenticate_validCredentials_returnsUser() {
        User user = new User("user001", "$2a$hash", "user@test.com", "Alice", "Martin", "12 rue de la Paix", "Paris", "75001");
        user.setId(1L);
        when(userRepository.findByUsername("user001")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("user001", "$2a$hash")).thenReturn(true);

        User result = authService.authenticate(new LoginRequest("user001", "user001"), "fr");

        assertThat(result.getUsername()).isEqualTo("user001");
    }

    @Test
    void register_newUser_createsAndReturnsUser() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("$2a$encoded");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role("USER")));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = authService.register(new RegisterRequest("newuser", "password", "new@test.com", "Bob", "Dupont", "2 rue test", "Lyon", "69001"), "fr");

        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getPasswordHash()).isEqualTo("$2a$encoded");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_duplicateUsername_throwsException() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("existing", "pass", "e@t.com", null, null, null, null, null), "fr"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void register_duplicateEmail_throwsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("newuser", "pass", "existing@test.com", null, null, null, null, null), "fr"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateProfile_existingUser_updatesFields() {
        User user = new User("user001", "$2a$hash", "old@test.com", "Alice", "Martin", "old street", "old city", "00000");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.updateProfile(1L, new UpdateProfileRequest("new@test.com", "Bob", "Dupont", "new street", "new city", "11111"));

        assertThat(result.getEmail()).isEqualTo("new@test.com");
        assertThat(result.getFirstName()).isEqualTo("Bob");
    }

    @Test
    void updateProfile_updatesAllFields() {
        User user = new User("user001", "$2a$hash", "old@test.com", "Alice", "Martin", "old street", "old city", "00000");
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authService.updateProfile(1L, new UpdateProfileRequest("updated@test.com", "NewFirst", "NewLast", "123 New St", "NewCity", "99999"));

        assertThat(result.getEmail()).isEqualTo("updated@test.com");
        assertThat(result.getFirstName()).isEqualTo("NewFirst");
        assertThat(result.getLastName()).isEqualTo("NewLast");
        assertThat(result.getStreet()).isEqualTo("123 New St");
        assertThat(result.getCity()).isEqualTo("NewCity");
        assertThat(result.getPostalCode()).isEqualTo("99999");
        verify(userRepository).save(user);
    }

    @Test
    void updateProfile_userNotFound_throwsException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateProfile(999L, new UpdateProfileRequest("e@t.com", null, null, null, null, null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void register_assignsUserRole() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("$2a$encoded");
        Role userRole = new Role("USER");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = authService.register(new RegisterRequest("newuser", "password", "new@test.com", "Bob", "Dupont", "street", "city", "69001"), "fr");

        assertThat(result.getRoles()).contains(userRole);
        assertThat(result.getRoles()).hasSize(1);
    }

    @Test
    void register_roleNotFound_throwsIllegalState() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("$2a$encoded");
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.register(new RegisterRequest("newuser", "password", "new@test.com", null, null, null, null, null), "fr"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Role USER not found");
    }

    @Test
    void authenticate_englishLang_returnsEnglishMessage() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("unknown", "pass"), "en"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    // ========== MUTATION-KILLING TESTS ==========

    @Test
    void authenticate_frenchLang_returnsFrenchMessage() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("unknown", "pass"), "fr"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Identifiants invalides");
    }

    @Test
    void authenticate_unknownLang_fallsBackToFrench() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("unknown", "pass"), "de"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Identifiants invalides");
    }

    @Test
    void register_englishLang_duplicateUsername_returnsEnglishMessage() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("existing", "pass", "e@t.com", null, null, null, null, null), "en"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This username is already taken");
    }

    @Test
    void register_englishLang_duplicateEmail_returnsEnglishMessage() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(new RegisterRequest("newuser", "pass", "existing@test.com", null, null, null, null, null), "en"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This email is already in use");
    }

    @Test
    void authenticate_wrongPassword_frenchLang_returnsFrenchMessage() {
        User user = new User("user001", "$2a$hash", "user@test.com", "Alice", "Martin", null, null, null);
        when(userRepository.findByUsername("user001")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("user001", "wrong"), "fr"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Identifiants invalides");
    }

    @Test
    void authenticate_wrongPassword_englishLang_returnsEnglishMessage() {
        User user = new User("user001", "$2a$hash", "user@test.com", "Alice", "Martin", null, null, null);
        when(userRepository.findByUsername("user001")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "$2a$hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.authenticate(new LoginRequest("user001", "wrong"), "en"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void authenticate_validCredentials_returnsCorrectUser() {
        User user = new User("testuser", "$2a$hash", "test@test.com", "Test", "User", null, null, null);
        user.setId(42L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "$2a$hash")).thenReturn(true);

        User result = authService.authenticate(new LoginRequest("testuser", "pass"), "fr");

        assertThat(result).isSameAs(user);
        assertThat(result.getId()).isEqualTo(42L);
    }

}
