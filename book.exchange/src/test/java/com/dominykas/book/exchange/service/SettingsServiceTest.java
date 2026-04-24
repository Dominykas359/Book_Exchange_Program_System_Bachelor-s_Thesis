package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.settingsDTO.SettingsRequestDTO;
import com.dominykas.book.exchange.dto.settingsDTO.SettingsResponseDTO;
import com.dominykas.book.exchange.entity.Settings;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.SettingsRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @Mock
    private SettingsRepository settingsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SettingsService settingsService;

    @Test
    void getByUserId_ShouldReturnSettings() {
        User user = User.builder().id(1L).build();

        Settings settings = new Settings();
        settings.setId(1L);
        settings.setUser(user);
        settings.setTheme("light");
        settings.setLanguage("en");

        when(settingsRepository.findByUserId(1L)).thenReturn(Optional.of(settings));

        SettingsResponseDTO result = settingsService.getByUserId(1L);

        assertThat(result).isNotNull();

        verify(settingsRepository).findByUserId(1L);
    }

    @Test
    void getByUserId_WhenSettingsMissing_ShouldThrowException() {
        when(settingsRepository.findByUserId(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> settingsService.getByUserId(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Settings not found");
    }

    @Test
    void updateByUserId_ShouldUpdateSettings() {
        SettingsRequestDTO request = new SettingsRequestDTO();
        request.setTheme("dark");
        request.setLanguage("lt");
        request.setEmailNotifications(false);
        request.setPreferredModelKey("roberta");
        request.setDefaultSearchLimit(20);
        request.setDefaultMinScore(0.7);

        User user = User.builder().id(1L).build();

        Settings settings = new Settings();
        settings.setId(1L);
        settings.setUser(user);

        when(settingsRepository.findByUserId(1L)).thenReturn(Optional.of(settings));
        when(settingsRepository.save(settings)).thenReturn(settings);

        SettingsResponseDTO result = settingsService.updateByUserId(1L, request);

        assertThat(result).isNotNull();

        verify(settingsRepository).save(settings);
    }

    @Test
    void createForUser_ShouldCreateSettings() {
        SettingsRequestDTO request = new SettingsRequestDTO();
        request.setTheme("light");
        request.setLanguage("en");
        request.setEmailNotifications(true);
        request.setPreferredModelKey("bert");
        request.setDefaultSearchLimit(10);
        request.setDefaultMinScore(0.5);

        User user = User.builder().id(1L).build();

        Settings saved = new Settings();
        saved.setId(1L);
        saved.setUser(user);
        saved.setTheme("light");
        saved.setLanguage("en");

        when(settingsRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(settingsRepository.save(any(Settings.class))).thenReturn(saved);

        SettingsResponseDTO result = settingsService.createForUser(1L, request);

        assertThat(result).isNotNull();

        verify(settingsRepository).save(any(Settings.class));
    }

    @Test
    void createForUser_WhenSettingsAlreadyExist_ShouldThrowException() {
        when(settingsRepository.findByUserId(1L)).thenReturn(Optional.of(new Settings()));

        assertThatThrownBy(() -> settingsService.createForUser(1L, new SettingsRequestDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Settings already exist");
    }
}