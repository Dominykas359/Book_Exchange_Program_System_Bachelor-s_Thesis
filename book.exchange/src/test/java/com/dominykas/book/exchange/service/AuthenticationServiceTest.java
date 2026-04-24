package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.userDTO.AuthenticationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.LoginRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.entity.Settings;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.SettingsRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SettingsRepository settingsRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_ShouldCreateUserSettingsAndReturnToken() {
        UserRequestDTO request = new UserRequestDTO();
        request.setEmail("test@gmail.com");
        request.setPassword("password");
        request.setFirstName("John");
        request.setLastName("Doe");

        User savedUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");

        AuthenticationResponseDTO result = authenticationService.register(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getToken()).isEqualTo("jwt-token");

        verify(userRepository).save(any(User.class));
        verify(settingsRepository).save(any(Settings.class));
        verify(jwtService).generateToken(savedUser);
    }

    @Test
    void login_ShouldAuthenticateAndReturnToken() {
        LoginRequestDTO request = new LoginRequestDTO("test@gmail.com", "password");

        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .build();

        when(userRepository.findByEmail("test@gmail.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthenticationResponseDTO result = authenticationService.login(request);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@gmail.com");
        assertThat(result.getToken()).isEqualTo("jwt-token");

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByEmail("test@gmail.com");
        verify(jwtService).generateToken(user);
    }
}