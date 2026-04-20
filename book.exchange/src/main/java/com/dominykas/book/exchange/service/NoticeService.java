package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.noticeDTO.PageResponseDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeFilterDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import com.dominykas.book.exchange.mapper.NoticeMapper;
import com.dominykas.book.exchange.repository.NoticeRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;

    public NoticeResponseDTO createNotice(NoticeRequestDTO dto) {
        Notice notice = NoticeMapper.fromDto(dto);

        User poster = userRepository.findById(dto.getPosterId())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getPosterId()));

        Publication publication = publicationRepository.findById(dto.getPublicationId())
                .orElseThrow(() -> new RuntimeException("Publication not found: " + dto.getPublicationId()));

        notice.setPoster(poster);
        notice.setPublication(publication);
        notice.setTimePosted(LocalDate.now());

        if (notice.getStatus() == null) {
            notice.setStatus(NoticeStatus.OPEN);
        }

        Notice saved = noticeRepository.save(notice);
        return NoticeMapper.toDto(saved);
    }

    public PageResponseDTO<NoticeResponseDTO> getNotices(
            int page,
            int size,
            String sortBy,
            String sortDir,
            NoticeFilterDTO filter
    ) {
        String mappedSortBy = mapSortBy(sortBy);

        Sort secondarySort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(mappedSortBy).ascending()
                : Sort.by(mappedSortBy).descending();

        Sort sort = Sort.by(Sort.Order.desc("status")).and(secondarySort);

        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<Notice> specification = Specification
                .where(NoticeSpecification.hasPosterId(filter.getPosterId()))
                .and(NoticeSpecification.titleContains(filter.getTitle()))
                .and(NoticeSpecification.authorContains(filter.getAuthor()))
                .and(NoticeSpecification.hasLanguage(filter.getLanguage()))
                .and(NoticeSpecification.releaseYearFrom(filter.getReleaseYearFrom()))
                .and(NoticeSpecification.releaseYearTo(filter.getReleaseYearTo()))
                .and(NoticeSpecification.minPageCount(filter.getMinPageCount()))
                .and(NoticeSpecification.maxPageCount(filter.getMaxPageCount()))
                .and(NoticeSpecification.hasCover(filter.getCover()))
                .and(NoticeSpecification.hasColored(filter.getColored()))
                .and(NoticeSpecification.postedFrom(filter.getPostedFrom()))
                .and(NoticeSpecification.postedTo(filter.getPostedTo()));

        Page<Notice> noticePage = noticeRepository.findAll(specification, pageable);

        List<NoticeResponseDTO> content = noticePage.getContent()
                .stream()
                .map(NoticeMapper::toDto)
                .toList();

        return new PageResponseDTO<>(
                content,
                noticePage.getNumber(),
                noticePage.getSize(),
                noticePage.getTotalElements(),
                noticePage.getTotalPages(),
                noticePage.isFirst(),
                noticePage.isLast()
        );
    }

    public NoticeResponseDTO getNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found: " + id));

        return NoticeMapper.toDto(notice);
    }

    public NoticeResponseDTO getNoticeByPublicationId(Long publicationId) {
        Notice notice = noticeRepository.findByPublicationId(publicationId)
                .orElseThrow(() -> new RuntimeException("Notice not found for publication id: " + publicationId));

        return NoticeMapper.toDto(notice);
    }

    public List<NoticeResponseDTO> getNoticesByPosterId(Long posterId) {
        return noticeRepository.findAllByPosterId(posterId)
                .stream()
                .map(NoticeMapper::toDto)
                .toList();
    }

    private String mapSortBy(String sortBy) {
        return switch (sortBy) {
            case "id" -> "id";
            case "timePosted" -> "timePosted";
            default -> "timePosted";
        };
    }
}