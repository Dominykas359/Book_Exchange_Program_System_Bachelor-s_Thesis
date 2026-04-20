package com.dominykas.book.exchange.entity;

import com.dominykas.book.exchange.entity.enums.ExchangeRequestStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "exchange_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requested_time", nullable = false)
    private LocalDate requestedTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_from_user_id", nullable = false)
    private User requestedFromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id", nullable = false)
    private Notice notice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "given_publication_id", nullable = false)
    private Publication givenPublication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_publication_id", nullable = false)
    private Publication receivedPublication;

    @Column(name = "requester_address", nullable = false, columnDefinition = "TEXT")
    private String requesterAddress;

    @Column(name = "requested_from_user_address", columnDefinition = "TEXT")
    private String requestedFromUserAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeRequestStatus status;
}