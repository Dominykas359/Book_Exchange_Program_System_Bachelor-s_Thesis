package com.dominykas.book.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "poster_user_id", nullable = false)
    private Long posterUserId;

    @Column(name = "notice_id", nullable = false)
    private Long noticeId;

    @Column(name = "time_exchanged", nullable = false)
    private LocalDate timeExchanged;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "given_publication_id", nullable = false)
    private Publication givenPublication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "received_publication_id", nullable = false)
    private Publication receivedPublication;
}