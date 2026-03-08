package com.dominykas.book.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "publications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String author;

    @Column(name = "release_year")
    private LocalDate releaseYear;

    @Column(columnDefinition = "TEXT")
    private String language;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "page_count")
    private Integer pageCount;

    @Column(columnDefinition = "TEXT")
    private String cover;

    private Boolean colored;

    @Column(name = "edition_number")
    private Integer editionNumber;

}
