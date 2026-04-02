package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.settingsDTO.SettingsRequestDTO;
import com.dominykas.book.exchange.dto.settingsDTO.SettingsResponseDTO;
import com.dominykas.book.exchange.entity.Settings;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.mapper.SettingsMapper;
import com.dominykas.book.exchange.repository.SettingsRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SettingsService {

    private final SettingsRepository settingsRepository;
    private final UserRepository userRepository;

    public SettingsResponseDTO getByUserId(Long userId) {
        Settings settings = settingsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        return SettingsMapper.toDto(settings);
    }

    public SettingsResponseDTO updateByUserId(Long userId, SettingsRequestDTO dto) {
        Settings settings = settingsRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        SettingsMapper.updateEntity(settings, dto);
        settingsRepository.save(settings);

        return SettingsMapper.toDto(settings);
    }

    public SettingsResponseDTO createForUser(Long userId, SettingsRequestDTO dto) {
        if (settingsRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Settings already exist for this user");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Settings settings = SettingsMapper.fromDto(dto);
        settings.setUser(user);

        settings = settingsRepository.save(settings);
        return SettingsMapper.toDto(settings);
    }
}
