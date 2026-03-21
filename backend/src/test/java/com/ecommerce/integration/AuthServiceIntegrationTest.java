package com.ecommerce.integration;

import com.ecommerce.domain.model.User;
import com.ecommerce.domain.service.AuthService;
import com.ecommerce.infrastructure.config.JwtUtil;
import com.ecommerce.infrastructure.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void register_assignsUserRole() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "roletest_user",
                                "password", "password",
                                "email", "roletest@test.com",
                                "firstName", "Role",
                                "lastName", "Test",
                                "street", "street",
                                "city", "city",
                                "postalCode", "00000"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.username").value("roletest_user"));
    }

    @Test
    void register_duplicateUsername_throwsWithFrenchMessage() throws Exception {
        // First register a user to ensure the username exists, regardless of test order
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "dup_user_fr",
                                "password", "password",
                                "email", "dup_user_fr@test.com"))))
                .andExpect(status().isCreated());

        // Now try to register with the same username
        mockMvc.perform(post("/api/auth/register")
                        .header("Accept-Language", "fr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "dup_user_fr",
                                "password", "password",
                                "email", "different@test.com"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$", containsString("nom d'utilisateur")));
    }

    @Test
    void register_duplicateEmail_throwsWithFrenchMessage() throws Exception {
        // First register a user to ensure the email exists, regardless of test order
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "dup_email_setup",
                                "password", "password",
                                "email", "dup_email_fr@test.com"))))
                .andExpect(status().isCreated());

        // Now try to register with the same email
        mockMvc.perform(post("/api/auth/register")
                        .header("Accept-Language", "fr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "different_user",
                                "password", "password",
                                "email", "dup_email_fr@test.com"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$", containsString("email")));
    }

    @Test
    void register_duplicateUsername_throwsWithEnglishMessage() throws Exception {
        // First register a user to ensure the username exists
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "dup_user_en",
                                "password", "password",
                                "email", "dup_user_en@test.com"))))
                .andExpect(status().isCreated());

        // Now try with the same username, English locale
        mockMvc.perform(post("/api/auth/register")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "username", "dup_user_en",
                                "password", "password",
                                "email", "en_unique@test.com"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$", containsString("username")));
    }
}
