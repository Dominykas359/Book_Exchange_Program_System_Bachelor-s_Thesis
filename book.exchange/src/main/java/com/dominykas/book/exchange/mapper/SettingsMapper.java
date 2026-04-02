package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.settingsDTO.SettingsRequestDTO;
import com.dominykas.book.exchange.dto.settingsDTO.SettingsResponseDTO;
import com.dominykas.book.exchange.entity.Settings;

public class SettingsMapper {

    public static Settings fromDto(SettingsRequestDTO dto) {
        Settings settings = new Settings();

        settings.setTheme(dto.getTheme());
        settings.setLanguage(dto.getLanguage());
        settings.setEmailNotifications(dto.getEmailNotifications());
        settings.setPreferredModelKey(dto.getPreferredModelKey());
        settings.setDefaultSearchLimit(dto.getDefaultSearchLimit());
        settings.setDefaultMinScore(dto.getDefaultMinScore());

        return settings;
    }

    public static SettingsResponseDTO toDto(Settings settings) {
        SettingsResponseDTO dto = new SettingsResponseDTO();

        dto.setId(settings.getId());
        dto.setUserId(settings.getUser().getId());

        dto.setTheme(settings.getTheme());
        dto.setLanguage(settings.getLanguage());
        dto.setEmailNotifications(settings.getEmailNotifications());
        dto.setPreferredModelKey(settings.getPreferredModelKey());
        dto.setDefaultSearchLimit(settings.getDefaultSearchLimit());
        dto.setDefaultMinScore(settings.getDefaultMinScore());

        return dto;
    }

    public static void updateEntity(Settings settings, SettingsRequestDTO dto) {
        settings.setTheme(dto.getTheme());
        settings.setLanguage(dto.getLanguage());
        settings.setEmailNotifications(dto.getEmailNotifications());
        settings.setPreferredModelKey(dto.getPreferredModelKey());
        settings.setDefaultSearchLimit(dto.getDefaultSearchLimit());
        settings.setDefaultMinScore(dto.getDefaultMinScore());
    }
}