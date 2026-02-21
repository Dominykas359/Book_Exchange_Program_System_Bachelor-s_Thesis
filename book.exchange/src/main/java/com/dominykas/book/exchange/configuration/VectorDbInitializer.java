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
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector;");

        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS publication_embeddings (
              publication_id BIGINT NOT NULL REFERENCES publications(id) ON DELETE CASCADE,
              model_key VARCHAR(32) NOT NULL,
              embedding vector(768) NOT NULL,
              PRIMARY KEY (publication_id, model_key)
            );
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS pub_emb_bert_hnsw
            ON publication_embeddings USING hnsw (embedding vector_cosine_ops)
            WHERE model_key = 'bert';
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS pub_emb_distilbert_hnsw
            ON publication_embeddings USING hnsw (embedding vector_cosine_ops)
            WHERE model_key = 'distilbert';
        """);

        jdbcTemplate.execute("""
            CREATE INDEX IF NOT EXISTS pub_emb_roberta_hnsw
            ON publication_embeddings USING hnsw (embedding vector_cosine_ops)
            WHERE model_key = 'roberta';
        """);
    }
}
