package com.dominykas.book.exchange.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class PublicationEmbeddingRepository {

    private final JdbcTemplate jdbcTemplate;

    public void updateEmbedding(Long publicationId, List<Double> embedding) {
        String vec = toVectorLiteral(embedding);
        jdbcTemplate.update(
                "UPDATE publications SET embedding = ?::vector WHERE id = ?",
                vec, publicationId
        );
    }

    private static String toVectorLiteral(List<Double> v) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < v.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format(Locale.US, "%.10f", v.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
}
