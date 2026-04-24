package com.dominykas.book.exchange.configuration;

import com.dominykas.book.exchange.configuration.auth.ApplicationConfiguration;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationConfigurationTest {

    private UserRepository userRepository;
    private ApplicationConfiguration config;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        config = new ApplicationConfiguration(userRepository);
    }

    @Test
    void userDetailsService_WhenUserExists_ShouldReturnUser() {
        User user = User.builder()
                .email("test@mail.com")
                .password("pass")
                .build();

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        UserDetailsService service = config.userDetailsService();

        assertThat(service.loadUserByUsername("test@mail.com")).isEqualTo(user);
    }

    @Test
    void userDetailsService_WhenUserNotFound_ShouldThrow() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        UserDetailsService service = config.userDetailsService();

        assertThatThrownBy(() -> service.loadUserByUsername("missing@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void authenticationProvider_ShouldReturnDaoAuthenticationProvider() {
        AuthenticationProvider provider = config.authenticationProvider();

        assertThat(provider).isInstanceOf(DaoAuthenticationProvider.class);

        assertThat(provider).isInstanceOf(DaoAuthenticationProvider.class);
    }

    @Test
    void passwordEncoder_ShouldBeBCrypt() {
        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isNotNull();
        assertThat(encoder.encode("test")).isNotEqualTo("test"); // BCrypt hashes
    }

    @Test
    void authenticationManager_ShouldReturnManager() throws Exception {
        AuthenticationConfiguration configuration = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);

        when(configuration.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = config.authenticationManager(configuration);

        assertThat(result).isEqualTo(manager);
    }
}