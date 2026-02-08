package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.mapper.PublicationMapper;
import com.dominykas.book.exchange.repository.PublicationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;

    @Transactional
    public PublicationResponseDTO createPublication(PublicationRequestDTO publicationRequestDTO) {

        Publication publication = PublicationMapper.fromDto(publicationRequestDTO);
        publication = publicationRepository.save(publication);
        return PublicationMapper.toDto(publication);
    }
}
