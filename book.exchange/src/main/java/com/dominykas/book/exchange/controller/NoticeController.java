package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public NoticeResponseDTO createNotice(@RequestBody NoticeRequestDTO noticeRequestDTO) {
        return noticeService.createNotice(noticeRequestDTO);
    }

    @GetMapping
    public List<NoticeResponseDTO> getAllNotices() {
        return noticeService.getAllNotices();
    }

    @GetMapping("/{id}")
    public NoticeResponseDTO getNoticeById(@PathVariable Long id) {
        return noticeService.getNoticeById(id);
    }

    @GetMapping("/publication/{publicationId}")
    public NoticeResponseDTO getNoticeByPublicationId(@PathVariable Long publicationId) {
        return noticeService.getNoticeByPublicationId(publicationId);
    }

    @GetMapping("/poster/{posterId}")
    public List<NoticeResponseDTO> getNoticesByPosterId(@PathVariable Long posterId) {
        return noticeService.getNoticesByPosterId(posterId);
    }
}