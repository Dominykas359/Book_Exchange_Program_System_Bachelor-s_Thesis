package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Publication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PublicationRepositoryTest {

    @Autowired
    private PublicationRepository repository;

    @Test
    void save_ShouldPersistPublication() {
        Publication publication = new Publication();
        publication.setTitle("Book");
        publication.setAuthor("Author");
        publication.setReleaseYear(LocalDate.of(2020, 1, 1));
        publication.setLanguage("en");
        publication.setDescription("Description");
        publication.setPageCount(300);
        publication.setCover("hard");
        publication.setColored(false);
        publication.setEditionNumber(1);

        Publication saved = repository.save(publication);

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId())).isPresent();
    }
}