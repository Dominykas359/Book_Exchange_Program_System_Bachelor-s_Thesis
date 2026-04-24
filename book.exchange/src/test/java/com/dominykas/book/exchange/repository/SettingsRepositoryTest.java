package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Settings;
import com.dominykas.book.exchange.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class SettingsRepositoryTest {

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserId_ShouldReturnSettings() {
        User user = User.builder()
                .email("test@gmail.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        user = userRepository.save(user);

        Settings settings = new Settings();
        settings.setUser(user);
        settings.setTheme("light");
        settings.setLanguage("en");
        settings.setEmailNotifications(true);
        settings.setPreferredModelKey("bert");
        settings.setDefaultSearchLimit(10);
        settings.setDefaultMinScore(0.5);

        settingsRepository.save(settings);

        Optional<Settings> result = settingsRepository.findByUserId(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void findByUserId_WhenMissing_ShouldReturnEmpty() {
        Optional<Settings> result = settingsRepository.findByUserId(999L);

        assertThat(result).isEmpty();
    }
}