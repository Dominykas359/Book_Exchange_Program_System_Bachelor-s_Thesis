package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.entity.Notice;
import com.dominykas.book.exchange.entity.Publication;
import com.dominykas.book.exchange.entity.User;
import com.dominykas.book.exchange.repository.NoticeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class NoticeSpecificationTest {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private EntityManager em;

    private Notice notice1;
    private Notice notice2;

    @BeforeEach
    void setup() {
        User user = new User();
        em.persist(user);

        Publication pub1 = new Publication();
        pub1.setTitle("Java Book");
        pub1.setAuthor("John Doe");
        pub1.setLanguage("EN");
        pub1.setReleaseYear(LocalDate.of(2020, 1, 1));
        pub1.setPageCount(200);
        pub1.setCover("hard");
        pub1.setColored(true);
        em.persist(pub1);

        Publication pub2 = new Publication();
        pub2.setTitle("Python Guide");
        pub2.setAuthor("Jane Doe");
        pub2.setLanguage("LT");
        pub2.setReleaseYear(LocalDate.of(2010, 1, 1));
        pub2.setPageCount(100);
        pub2.setCover("soft");
        pub2.setColored(false);
        em.persist(pub2);

        notice1 = new Notice();
        notice1.setPoster(user);
        notice1.setPublication(pub1);
        notice1.setTimePosted(LocalDate.of(2023, 1, 1));
        notice1.setStatus(com.dominykas.book.exchange.entity.enums.NoticeStatus.OPEN); // ✅ FIX
        em.persist(notice1);

        notice2 = new Notice();
        notice2.setPoster(user);
        notice2.setPublication(pub2);
        notice2.setTimePosted(LocalDate.of(2022, 1, 1));
        notice2.setStatus(com.dominykas.book.exchange.entity.enums.NoticeStatus.OPEN); // ✅ FIX
        em.persist(notice2);

        em.flush();
    }

    @Test
    void hasPosterId_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.hasPosterId(notice1.getPoster().getId());

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(2);
    }

    @Test
    void titleContains_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.titleContains("java");

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPublication().getTitle()).containsIgnoringCase("java");
    }

    @Test
    void authorContains_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.authorContains("jane");

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void hasLanguage_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.hasLanguage("lt");

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void releaseYearFrom_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.releaseYearFrom(LocalDate.of(2015, 1, 1));

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void releaseYearTo_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.releaseYearTo(LocalDate.of(2015, 1, 1));

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void minPageCount_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.minPageCount(150);

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void maxPageCount_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.maxPageCount(150);

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void hasCover_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.hasCover("hard");

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void hasColored_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.hasColored(true);

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void postedFrom_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.postedFrom(LocalDate.of(2023, 1, 1));

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void postedTo_ShouldFilter() {
        Specification<Notice> spec = NoticeSpecification.postedTo(LocalDate.of(2022, 1, 1));

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(1);
    }

    @Test
    void nullValues_ShouldReturnAll() {
        Specification<Notice> spec = Specification
                .where(NoticeSpecification.titleContains(null))
                .and(NoticeSpecification.authorContains(null))
                .and(NoticeSpecification.hasLanguage(null));

        List<Notice> result = noticeRepository.findAll(spec);

        assertThat(result).hasSize(2);
    }
}