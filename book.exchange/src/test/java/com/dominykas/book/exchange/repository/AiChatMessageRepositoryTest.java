package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.AiChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AiChatMessageRepositoryTest {

    @Autowired
    private AiChatMessageRepository repository;

    @Test
    void findAllByUserIdOrderByCreatedAtAscIdAsc_ShouldReturnOrderedMessages() {
        AiChatMessage later = new AiChatMessage();
        later.setUserId(1L);
        later.setRole("assistant");
        later.setText("second");
        later.setCreatedAt(LocalDateTime.now().plusMinutes(1));

        AiChatMessage earlier = new AiChatMessage();
        earlier.setUserId(1L);
        earlier.setRole("user");
        earlier.setText("first");
        earlier.setCreatedAt(LocalDateTime.now());

        repository.save(later);
        repository.save(earlier);

        List<AiChatMessage> result =
                repository.findAllByUserIdOrderByCreatedAtAscIdAsc(1L);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getText()).isEqualTo("first");
        assertThat(result.get(1).getText()).isEqualTo("second");
    }

    @Test
    void deleteAllByUserId_ShouldDeleteOnlyUserMessages() {
        AiChatMessage user1 = new AiChatMessage();
        user1.setUserId(1L);
        user1.setRole("user");
        user1.setText("hello");
        user1.setCreatedAt(LocalDateTime.now());

        AiChatMessage user2 = new AiChatMessage();
        user2.setUserId(2L);
        user2.setRole("user");
        user2.setText("other");
        user2.setCreatedAt(LocalDateTime.now());

        repository.save(user1);
        repository.save(user2);

        repository.deleteAllByUserId(1L);

        assertThat(repository.findAllByUserIdOrderByCreatedAtAscIdAsc(1L)).isEmpty();
        assertThat(repository.findAllByUserIdOrderByCreatedAtAscIdAsc(2L)).hasSize(1);
    }
}