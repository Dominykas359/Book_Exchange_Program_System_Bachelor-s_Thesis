package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationSearchResultDTO;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.repository.PublicationEmbeddingRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
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
class PublicationServiceTest {

    @Mock private PublicationRepository publicationRepository;
    @Mock private EmbeddingService embeddingService;
    @Mock private PublicationEmbeddingRepository embeddingRepository;
    @Mock private PublicationSearchRepository publicationSearchRepository;

    @InjectMocks
    private PublicationService publicationService;

    @Test
    void createPublication_ShouldSavePublicationAndEmbeddings() {
        PublicationRequestDTO request = new PublicationRequestDTO();
        request.setTitle("Book");
        request.setAuthor("Author");
        request.setLanguage("en");
        request.setDescription("Description");

        Publication saved = new Publication();
        saved.setId(1L);
        saved.setTitle("Book");
        saved.setAuthor("Author");
        saved.setLanguage("en");
        saved.setDescription("Description");

        when(publicationRepository.save(any(Publication.class))).thenReturn(saved);
        when(embeddingService.embed(anyString(), anyString())).thenReturn(List.of(0.1, 0.2, 0.3));

        PublicationResponseDTO result = publicationService.createPublication(request);

        assertThat(result).isNotNull();

        verify(publicationRepository).save(any(Publication.class));
        verify(embeddingRepository, times(3)).updateEmbedding(eq(1L), anyString(), anyList());
    }

    @Test
    void searchByDescription_WhenQueryBlank_ShouldReturnEmptyList() {
        List<PublicationSearchResultDTO> result =
                publicationService.searchByDescription("   ", "bert", 10, 0.45);

        assertThat(result).isEmpty();
        verifyNoInteractions(embeddingService);
        verifyNoInteractions(publicationSearchRepository);
    }
}