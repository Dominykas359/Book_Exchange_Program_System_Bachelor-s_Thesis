package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.evaluationDTO.ModelMetricsDTO;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.repository.PublicationSearchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {

    @Mock
    private EmbeddingService embeddingService;

    @Mock
    private PublicationSearchRepository publicationSearchRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    @Test
    void runEvaluation_ShouldReturnMetrics() {
        Publication publication = new Publication();
        publication.setId(1L);

        PublicationSearchRepository.SearchRow row =
                mock(PublicationSearchRepository.SearchRow.class);

        when(row.publication()).thenReturn(publication);

        when(embeddingService.embed(anyString(), anyString()))
                .thenReturn(List.of(0.1, 0.2, 0.3));

        when(publicationSearchRepository.searchTopK(anyList(), anyString(), eq(10)))
                .thenReturn(List.of(row));

        List<ModelMetricsDTO> result = evaluationService.runEvaluation(List.of("bert"));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getModelKey()).isEqualTo("bert");

        verify(embeddingService).embed("book about magic", "bert");
        verify(publicationSearchRepository).searchTopK(anyList(), eq("bert"), eq(10));
    }
}