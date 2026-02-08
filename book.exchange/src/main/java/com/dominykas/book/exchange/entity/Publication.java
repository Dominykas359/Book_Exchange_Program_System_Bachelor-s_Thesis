package com.dominykas.book.exchange.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

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

    @NonNull
    private String title;

    @NonNull
    private String author;

    @NonNull
    @Column(name = "release_year")
    private LocalDate releaseYear;

    @NonNull
    private String language;

    @NonNull
    @Lob
    private String description;

    @Column(name = "page_count")
    private Integer pageCount;

    private String cover;

    private Boolean colored;

    @Column(name = "edition_number")
    private Integer editionNumber;

}
