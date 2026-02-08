package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.entity.Publication;

public class PublicationMapper {

    public static Publication fromDto(PublicationRequestDTO publicationRequestDTO) {
        Publication publication = new Publication();

        publication.setTitle(publicationRequestDTO.getTitle());
        publication.setAuthor(publicationRequestDTO.getAuthor());
        publication.setReleaseYear(publicationRequestDTO.getReleaseYear());
        publication.setLanguage(publicationRequestDTO.getLanguage());
        publication.setDescription(publicationRequestDTO.getDescription());
        publication.setPageCount(publicationRequestDTO.getPageCount());
        publication.setCover(publicationRequestDTO.getCover());
        publication.setColored(publicationRequestDTO.getColored());
        publication.setEditionNumber(publicationRequestDTO.getEditionNumber());

        return publication;
    }

    public static PublicationResponseDTO toDto(Publication publication) {
        PublicationResponseDTO publicationResponseDTO = new PublicationResponseDTO();

        publicationResponseDTO.setId(publication.getId());
        publicationResponseDTO.setTitle(publication.getTitle());
        publicationResponseDTO.setAuthor(publication.getAuthor());
        publicationResponseDTO.setReleaseYear(publication.getReleaseYear());
        publicationResponseDTO.setLanguage(publication.getLanguage());
        publicationResponseDTO.setDescription(publication.getDescription());
        publicationResponseDTO.setPageCount(publication.getPageCount());
        publicationResponseDTO.setCover(publication.getCover());
        publicationResponseDTO.setColored(publication.getColored());
        publicationResponseDTO.setEditionNumber(publication.getEditionNumber());

        return publicationResponseDTO;
    }
}
