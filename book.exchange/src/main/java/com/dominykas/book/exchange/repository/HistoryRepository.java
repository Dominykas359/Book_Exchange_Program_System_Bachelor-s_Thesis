package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    List<History> findAllByUserIdOrPosterUserIdOrderByTimeExchangedDescIdDesc(Long userId, Long posterUserId);
}