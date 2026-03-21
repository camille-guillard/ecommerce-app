package com.ecommerce.infrastructure.config;

import com.ecommerce.infrastructure.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil("ecommerce-demo-secret-key-that-is-at-least-256-bits-long!!", 86400000L);
    }

    @Test
    void generateToken_returnsNonEmptyString() {
        String token = jwtUtil.generateToken("user001");
        assertThat(token).isNotEmpty();
    }

    @Test
    void extractUsername_returnsCorrectUsername() {
        String token = jwtUtil.generateToken("user001");
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("user001");
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        String token = jwtUtil.generateToken("user001");
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_invalidToken_returnsFalse() {
        assertThat(jwtUtil.isTokenValid("invalid.token.here")).isFalse();
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        JwtUtil shortLived = new JwtUtil("ecommerce-demo-secret-key-that-is-at-least-256-bits-long!!", -1000L);
        String token = shortLived.generateToken("user001");
        assertThat(shortLived.isTokenValid(token)).isFalse();
    }
}
