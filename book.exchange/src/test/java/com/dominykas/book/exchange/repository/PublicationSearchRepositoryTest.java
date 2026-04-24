package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Publication;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PublicationSearchRepositoryTest {

    @Test
    void searchTopK_ShouldCallJdbcTemplateQuery() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PublicationSearchRepository repository =
                new PublicationSearchRepository(jdbcTemplate);

        Publication publication = new Publication();
        publication.setId(1L);

        PublicationSearchRepository.SearchRow row =
                new PublicationSearchRepository.SearchRow(publication, 0.9);

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(List.of(row));

        List<PublicationSearchRepository.SearchRow> result =
                repository.searchTopK(List.of(0.1, 0.2), "bert", 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).publication().getId()).isEqualTo(1L);
        assertThat(result.get(0).score()).isEqualTo(0.9);
    }

    @Test
    void searchByEmbedding_ShouldCallJdbcTemplateQuery() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        PublicationSearchRepository repository =
                new PublicationSearchRepository(jdbcTemplate);

        when(jdbcTemplate.query(
                anyString(),
                any(RowMapper.class),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        )).thenReturn(List.of());

        List<PublicationSearchRepository.SearchRow> result =
                repository.searchByEmbedding(List.of(0.1, 0.2), "bert", 10, 0.45);

        assertThat(result).isEmpty();
    }
}