package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.configuration.ai.MIProperties;
import com.dominykas.book.exchange.dto.embedDTO.EmbedRequest;
import com.dominykas.book.exchange.dto.embedDTO.EmbedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final WebClient mlWebClient;
    private final MIProperties props;

    public List<Double> embed(String text, String modelKey) {
        EmbedResponse res = mlWebClient.post()
                .uri("/embed")
                .bodyValue(new EmbedRequest(text, modelKey))
                .retrieve()
                .bodyToMono(EmbedResponse.class)
                .timeout(Duration.ofSeconds(props.timeoutSeconds()))
                .block();

        if (res == null || res.getVector() == null || res.getVector().isEmpty()) {
            throw new IllegalStateException("ML service returned empty vector");
        }
        return res.getVector();
    }
}
