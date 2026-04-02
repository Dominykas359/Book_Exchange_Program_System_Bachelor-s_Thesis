package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.settingsDTO.SettingsRequestDTO;
import com.dominykas.book.exchange.dto.settingsDTO.SettingsResponseDTO;
import com.dominykas.book.exchange.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public SettingsResponseDTO get(@PathVariable Long userId) {
        return settingsService.getByUserId(userId);
    }

    @PostMapping
    public SettingsResponseDTO create(
            @PathVariable Long userId,
            @RequestBody SettingsRequestDTO dto
    ) {
        return settingsService.createForUser(userId, dto);
    }

    @PutMapping
    public SettingsResponseDTO update(
            @PathVariable Long userId,
            @RequestBody SettingsRequestDTO dto
    ) {
        return settingsService.updateByUserId(userId, dto);
    }
}
