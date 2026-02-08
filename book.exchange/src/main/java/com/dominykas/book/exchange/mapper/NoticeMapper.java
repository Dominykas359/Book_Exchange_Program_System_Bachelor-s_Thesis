package com.dominykas.book.exchange.mapper;

import com.dominykas.book.exchange.dto.noticeDTO.NoticeRequestDTO;
import com.dominykas.book.exchange.dto.noticeDTO.NoticeResponseDTO;
import com.dominykas.book.exchange.entity.Notice;

public class NoticeMapper {

    private NoticeMapper() {}

    public static Notice fromDto(NoticeRequestDTO noticeRequestDTO) {
        Notice notice = new Notice();
        notice.setTimePosted(noticeRequestDTO.getTimePosted());
        return notice;
    }

    public static NoticeResponseDTO toDto(Notice notice) {
        NoticeResponseDTO noticeResponseDTO = new NoticeResponseDTO();
        noticeResponseDTO.setId(notice.getId());
        noticeResponseDTO.setTimePosted(notice.getTimePosted());
        noticeResponseDTO.setPoster(UserMapper.toDto(notice.getPoster()));
        noticeResponseDTO.setPublication(PublicationMapper.toDto(notice.getPublication()));

        return noticeResponseDTO;
    }
}
