package com.dominykas.book.exchange.repository;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PublicationEmbeddingRepositoryTest {

    @Test
    void updateEmbedding_ShouldCallJdbcTemplateUpdate() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PublicationEmbeddingRepository repository =
                new PublicationEmbeddingRepository(jdbcTemplate);

        repository.updateEmbedding(1L, "bert", List.of(0.1, 0.2, 0.3));

        verify(jdbcTemplate).update(
                anyString(),
                eq(1L),
                eq("bert"),
                eq("[0.1000000000,0.2000000000,0.3000000000]")
        );
    }
}