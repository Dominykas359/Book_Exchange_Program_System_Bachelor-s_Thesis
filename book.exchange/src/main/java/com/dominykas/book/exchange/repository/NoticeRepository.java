package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Optional<Notice> findByPublicationId(Long publicationId);

    List<Notice> findAllByPosterId(Long posterId);
}