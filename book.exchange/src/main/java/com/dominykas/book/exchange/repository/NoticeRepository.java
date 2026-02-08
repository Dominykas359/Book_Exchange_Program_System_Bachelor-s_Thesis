package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
