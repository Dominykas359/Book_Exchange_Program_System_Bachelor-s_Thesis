package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService();

    @Test
    void generateToken_ShouldCreateValidToken() {
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        String token = jwtService.generateToken(user);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("test@gmail.com");
        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }
}