package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.publicationDTO.PublicationRequestDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationResponseDTO;
import com.dominykas.book.exchange.dto.publicationDTO.PublicationSearchResultDTO;
import com.dominykas.book.exchange.service.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping
    public PublicationResponseDTO createPublication(@RequestBody PublicationRequestDTO publicationRequestDTO) {
        return publicationService.createPublication(publicationRequestDTO);
    }

    @GetMapping("/search")
    public List<PublicationSearchResultDTO> search(
            @RequestParam("q") String q,
            @RequestParam(value = "modelKey", defaultValue = "roberta") String modelKey,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "minScore", defaultValue = "0.45") double minScore
    ) {
        return publicationService.searchByDescription(q, modelKey, limit, minScore);
    }
}
