package com.dominykas.book.exchange.entity;

import com.dominykas.book.exchange.entity.enums.NoticeStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "notices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time_posted")
    private LocalDate timePosted;

    @Column(columnDefinition = "TEXT", name = "wish_in_return")
    private String wishInReturn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id")
    private User poster;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publication_id", nullable = false, unique = true)
    private Publication publication;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NoticeStatus status;
}