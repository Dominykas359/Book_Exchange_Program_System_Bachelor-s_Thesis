package com.dominykas.book.exchange.configuration;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class VectorDbInitializer {

    private final JdbcTemplate jdbcTemplate;

    public VectorDbInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initVectorSupport() {
        // 1) Enable pgvector extension (server must have it installed)
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector;");

        // 2) Add embedding column (E5 base = 768 dims)
        jdbcTemplate.execute("""
            ALTER TABLE publications
            ADD COLUMN IF NOT EXISTS embedding vector(768);
        """);

        // 3) ANN index for cosine similarity
        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS publications_embedding_hnsw
            ON publications USING hnsw (embedding vector_cosine_ops);
        """);
    }
}
