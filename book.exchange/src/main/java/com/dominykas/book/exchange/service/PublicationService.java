package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.mapper.PublicationMapper;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.PublicationEmbeddingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
    private final EmbeddingService embeddingService;
    private final PublicationEmbeddingRepository embeddingRepository;

    @Transactional
    public PublicationResponseDTO createPublication(PublicationRequestDTO publicationRequestDTO) {

        Publication publication = PublicationMapper.fromDto(publicationRequestDTO);
        publication = publicationRepository.save(publication);

        try {
            String text = buildEmbeddingText(publication);
            List<Double> vec = embeddingService.embed(text);
            embeddingRepository.updateEmbedding(publication.getId(), vec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return PublicationMapper.toDto(publication);
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
