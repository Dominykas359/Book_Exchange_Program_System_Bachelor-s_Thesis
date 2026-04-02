package com.dominykas.book.exchange.dto.settingsDTO;

import lombok.Data;

@Data
public class SettingsRequestDTO {

    private String theme;
    private String language;
    private Boolean emailNotifications;
    private String preferredModelKey;
    private Integer defaultSearchLimit;
    private Double defaultMinScore;
}
