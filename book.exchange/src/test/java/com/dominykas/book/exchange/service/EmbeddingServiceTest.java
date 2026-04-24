package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.configuration.ai.MIProperties;
import com.dominykas.book.exchange.dto.embedDTO.EmbedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmbeddingServiceTest {

    @Mock private WebClient webClient;
    @Mock private MIProperties props;
    @Mock private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock private WebClient.RequestBodySpec requestBodySpec;
    @Mock private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    @Test
    void embed_ShouldReturnVector() {
        EmbedResponse response = new EmbedResponse();
        response.setVector(List.of(0.1, 0.2, 0.3));

        when(props.timeoutSeconds()).thenReturn(5);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/embed")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmbedResponse.class)).thenReturn(Mono.just(response));

        EmbeddingService service = new EmbeddingService(webClient, props);

        List<Double> result = service.embed("text", "bert");

        assertThat(result).containsExactly(0.1, 0.2, 0.3);
    }

    @Test
    void embed_WhenVectorEmpty_ShouldThrowException() {
        EmbedResponse response = new EmbedResponse();
        response.setVector(List.of());

        when(props.timeoutSeconds()).thenReturn(5);
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/embed")).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmbedResponse.class)).thenReturn(Mono.just(response));

        EmbeddingService service = new EmbeddingService(webClient, props);

        assertThatThrownBy(() -> service.embed("text", "bert"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("empty vector");
    }
}