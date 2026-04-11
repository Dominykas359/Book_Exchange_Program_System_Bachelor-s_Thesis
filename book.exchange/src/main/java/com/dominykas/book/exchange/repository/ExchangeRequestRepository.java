package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.ExchangeRequest;
import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    List<ExchangeRequest> findAllByUserIdOrRequestedFromUserIdOrderByRequestedTimeDescIdDesc(Long userId, Long requestedFromUserId);

    List<ExchangeRequest> findAllByNoticeIdOrderByRequestedTimeDescIdDesc(Long noticeId);

    List<ExchangeRequest> findAllByStatusOrderByRequestedTimeDescIdDesc(ExchangeRequestStatus status);

    boolean existsByUserIdAndNoticeIdAndStatus(Long userId, Long noticeId, ExchangeRequestStatus status);
}