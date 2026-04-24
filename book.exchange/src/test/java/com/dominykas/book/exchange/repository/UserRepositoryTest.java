package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_ShouldReturnUser() {
        User user = User.builder()
                .email("test@gmail.com")
                .password("password")
                .firstName("John")
                .lastName("Doe")
                .build();

        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("test@gmail.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@gmail.com");
    }

    @Test
    void findByEmail_WhenMissing_ShouldReturnEmpty() {
        Optional<User> result = userRepository.findByEmail("missing@gmail.com");

        assertThat(result).isEmpty();
    }
}