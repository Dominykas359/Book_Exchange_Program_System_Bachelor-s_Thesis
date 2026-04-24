package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.settingsDTO.SettingsRequestDTO;
import com.dominykas.book.exchange.dto.settingsDTO.SettingsResponseDTO;
import com.dominykas.book.exchange.service.SettingsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettingsControllerTest {

    @Mock
    private SettingsService settingsService;

    @InjectMocks
    private SettingsController controller;

    @Test
    void get_ShouldReturnSettings() {
        SettingsResponseDTO response = new SettingsResponseDTO();

        when(settingsService.getByUserId(1L)).thenReturn(response);

        SettingsResponseDTO result = controller.get(1L);

        assertThat(result).isSameAs(response);
        verify(settingsService).getByUserId(1L);
    }

    @Test
    void create_ShouldReturnSettings() {
        SettingsRequestDTO request = new SettingsRequestDTO();
        SettingsResponseDTO response = new SettingsResponseDTO();

        when(settingsService.createForUser(1L, request)).thenReturn(response);

        SettingsResponseDTO result = controller.create(1L, request);

        assertThat(result).isSameAs(response);
        verify(settingsService).createForUser(1L, request);
    }

    @Test
    void update_ShouldReturnSettings() {
        SettingsRequestDTO request = new SettingsRequestDTO();
        SettingsResponseDTO response = new SettingsResponseDTO();

        when(settingsService.updateByUserId(1L, request)).thenReturn(response);

        SettingsResponseDTO result = controller.update(1L, request);

        assertThat(result).isSameAs(response);
        verify(settingsService).updateByUserId(1L, request);
    }
}