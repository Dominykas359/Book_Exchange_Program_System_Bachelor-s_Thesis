package com.dominykas.book.exchange.repository;

import com.dominykas.book.exchange.entity.Publication;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class PublicationSearchRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<SearchRow> searchByEmbedding(List<Double> queryEmbedding, String modelKey, int limit, double minScore) {
        String qVec = toVectorLiteral(queryEmbedding);

        // cosine distance threshold (because score = 1 - distance)
        double maxDistance = 1.0 - minScore;

        String sql = """
            SELECT p.*,
                   (1 - (e.embedding <=> ?::vector)) AS score
            FROM publication_embeddings e
            JOIN publications p ON p.id = e.publication_id
            WHERE e.model_key = ?
              AND e.embedding IS NOT NULL
              AND (e.embedding <=> ?::vector) <= ?
            ORDER BY (e.embedding <=> ?::vector)
            LIMIT ?;
        """;

        return jdbcTemplate.query(
                sql,
                searchRowMapper(),
                qVec,        // score calc
                modelKey,    // WHERE e.model_key = ?
                qVec,        // distance in WHERE
                maxDistance, // threshold
                qVec,        // ORDER BY
                limit
        );
    }

    public List<SearchRow> searchTopK(List<Double> queryEmbedding, String modelKey, int limit) {
        String qVec = toVectorLiteral(queryEmbedding);

        String sql = """
        SELECT p.*,
               (1 - (e.embedding <=> ?::vector)) AS score
        FROM publication_embeddings e
        JOIN publications p ON p.id = e.publication_id
        WHERE e.model_key = ?
          AND e.embedding IS NOT NULL
        ORDER BY (e.embedding <=> ?::vector)
        LIMIT ?;
    """;

        return jdbcTemplate.query(
                sql,
                searchRowMapper(),
                qVec,
                modelKey,
                qVec,
                limit
        );
    }

    private RowMapper<SearchRow> searchRowMapper() {
        return (rs, rowNum) -> new SearchRow(mapPublication(rs), rs.getDouble("score"));
    }

    private Publication mapPublication(ResultSet rs) throws SQLException {
        Publication p = new Publication();
        p.setId(rs.getLong("id"));
        p.setTitle(rs.getString("title"));
        p.setAuthor(rs.getString("author"));
        p.setReleaseYear(rs.getDate("release_year").toLocalDate());
        p.setLanguage(rs.getString("language"));
        p.setDescription(rs.getString("description"));
        p.setPageCount((Integer) rs.getObject("page_count"));
        p.setCover(rs.getString("cover"));
        p.setColored((Boolean) rs.getObject("colored"));
        p.setEditionNumber((Integer) rs.getObject("edition_number"));
        return p;
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

    public record SearchRow(Publication publication, double score) {}
}