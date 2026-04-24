package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationSearchResultDTO;
import com.dominykas.book.exchange.service.PublicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublicationControllerTest {

    @Mock
    private PublicationService publicationService;

    @InjectMocks
    private PublicationController controller;

    @Test
    void createPublication_ShouldReturnResponse() {
        PublicationRequestDTO request = new PublicationRequestDTO();
        PublicationResponseDTO response = new PublicationResponseDTO();

        when(publicationService.createPublication(request)).thenReturn(response);

        PublicationResponseDTO result = controller.createPublication(request);

        assertThat(result).isSameAs(response);
        verify(publicationService).createPublication(request);
    }

    @Test
    void search_ShouldReturnResults() {
        List<PublicationSearchResultDTO> response = List.of(new PublicationSearchResultDTO());

        when(publicationService.searchByDescription("book", "roberta", 10, 0.45))
                .thenReturn(response);

        List<PublicationSearchResultDTO> result =
                controller.search("book", "roberta", 10, 0.45);

        assertThat(result).isSameAs(response);
        verify(publicationService).searchByDescription("book", "roberta", 10, 0.45);
    }
}