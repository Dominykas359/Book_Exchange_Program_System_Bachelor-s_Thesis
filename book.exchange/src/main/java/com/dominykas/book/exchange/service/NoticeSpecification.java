package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.entity.Notice;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class NoticeSpecification {

    public static Specification<Notice> hasPosterId(Long posterId) {
        return (root, query, cb) ->
                posterId == null ? null : cb.equal(root.get("poster").get("id"), posterId);
    }

    public static Specification<Notice> titleContains(String title) {
        return (root, query, cb) ->
                title == null || title.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.join("publication", JoinType.INNER).get("title")),
                        "%" + title.toLowerCase() + "%"
                );
    }

    public static Specification<Notice> authorContains(String author) {
        return (root, query, cb) ->
                author == null || author.isBlank()
                        ? null
                        : cb.like(
                        cb.lower(root.join("publication", JoinType.INNER).get("author")),
                        "%" + author.toLowerCase() + "%"
                );
    }

    public static Specification<Notice> hasLanguage(String language) {
        return (root, query, cb) ->
                language == null || language.isBlank()
                        ? null
                        : cb.equal(
                        cb.lower(root.join("publication", JoinType.INNER).get("language")),
                        language.toLowerCase()
                );
    }

    public static Specification<Notice> releaseYearFrom(LocalDate releaseYearFrom) {
        return (root, query, cb) ->
                releaseYearFrom == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.join("publication", JoinType.INNER).get("releaseYear"),
                        releaseYearFrom
                );
    }

    public static Specification<Notice> releaseYearTo(LocalDate releaseYearTo) {
        return (root, query, cb) ->
                releaseYearTo == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.join("publication", JoinType.INNER).get("releaseYear"),
                        releaseYearTo
                );
    }

    public static Specification<Notice> minPageCount(Integer minPageCount) {
        return (root, query, cb) ->
                minPageCount == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.join("publication", JoinType.INNER).get("pageCount"),
                        minPageCount
                );
    }

    public static Specification<Notice> maxPageCount(Integer maxPageCount) {
        return (root, query, cb) ->
                maxPageCount == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.join("publication", JoinType.INNER).get("pageCount"),
                        maxPageCount
                );
    }

    public static Specification<Notice> hasCover(String cover) {
        return (root, query, cb) ->
                cover == null || cover.isBlank()
                        ? null
                        : cb.equal(
                        cb.lower(root.join("publication", JoinType.INNER).get("cover")),
                        cover.toLowerCase()
                );
    }

    public static Specification<Notice> hasColored(Boolean colored) {
        return (root, query, cb) ->
                colored == null
                        ? null
                        : cb.equal(
                        root.join("publication", JoinType.INNER).get("colored"),
                        colored
                );
    }

    public static Specification<Notice> postedFrom(LocalDate postedFrom) {
        return (root, query, cb) ->
                postedFrom == null ? null : cb.greaterThanOrEqualTo(root.get("timePosted"), postedFrom);
    }

    public static Specification<Notice> postedTo(LocalDate postedTo) {
        return (root, query, cb) ->
                postedTo == null ? null : cb.lessThanOrEqualTo(root.get("timePosted"), postedTo);
    }
}