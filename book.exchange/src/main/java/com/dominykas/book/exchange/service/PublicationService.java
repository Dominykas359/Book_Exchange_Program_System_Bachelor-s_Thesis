package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationSearchResultDTO;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.mapper.PublicationMapper;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.PublicationEmbeddingRepository;
import com.dominykas.book.exchange.repository.PublicationSearchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final EmbeddingService embeddingService;
    private final PublicationEmbeddingRepository embeddingRepository;
    private final PublicationSearchRepository publicationSearchRepository;

    @Transactional
    public PublicationResponseDTO createPublication(PublicationRequestDTO publicationRequestDTO) {

        Publication publication = PublicationMapper.fromDto(publicationRequestDTO);
        publication = publicationRepository.save(publication);

        try {
            String text = buildEmbeddingText(publication);

            for (String modelKey : List.of("bert", "distilbert", "roberta")) {
                List<Double> vec = embeddingService.embed(text, modelKey);
                embeddingRepository.updateEmbedding(publication.getId(), modelKey, vec);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return PublicationMapper.toDto(publication);
    }

    public List<PublicationSearchResultDTO> searchByDescription(String query, String modelKey, int limit, double minScore) {
        if (query == null || query.trim().isEmpty()) return List.of();

        List<Double> qVec = embeddingService.embed(query.trim(), modelKey);

        return publicationSearchRepository.searchByEmbedding(qVec, modelKey, limit, minScore)
                .stream()
                .map(row -> new PublicationSearchResultDTO(
                        PublicationMapper.toDto(row.publication()),
                        row.score()
                ))
                .collect(Collectors.toList());
    }

    private String buildEmbeddingText(Publication p) {
        // Keep consistent and deterministic
        return String.join("\n",
                safe(p.getTitle()),
                safe(p.getAuthor()),
                safe(p.getLanguage()),
                safe(p.getDescription())
        );
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
