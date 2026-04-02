package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.userDTO.AuthenticationResponseDTO;
import com.dominykas.book.exchange.dto.userDTO.LoginRequestDTO;
import com.dominykas.book.exchange.dto.userDTO.UserRequestDTO;
import com.dominykas.book.exchange.entity.Settings;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.SettingsRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final SettingsRepository settingsRepository;

    @Transactional
    public AuthenticationResponseDTO register(UserRequestDTO request) {

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        user = userRepository.save(user);

        Settings settings = new Settings();
        settings.setUser(user);
        settings.setTheme("light");
        settings.setLanguage("en");
        settings.setEmailNotifications(true);
        settings.setPreferredModelKey("bert");
        settings.setDefaultSearchLimit(10);
        settings.setDefaultMinScore(0.50);

        settingsRepository.save(settings);

        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .token(jwtToken)
                .build();
    }
}