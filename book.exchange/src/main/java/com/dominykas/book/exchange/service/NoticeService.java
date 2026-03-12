package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.mapper.NoticeMapper;
import com.dominykas.book.exchange.repository.NoticeRepository;
import com.dominykas.book.exchange.repository.PublicationRepository;
import com.dominykas.book.exchange.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

        Notice saved = noticeRepository.save(notice);
        return NoticeMapper.toDto(saved);
    }

    public List<NoticeResponseDTO> getAllNotices() {
        return noticeRepository.findAll()
                .stream()
                .map(NoticeMapper::toDto)
                .toList();
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
}