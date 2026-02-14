package com.dominykas.book.exchange.configuration.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(MIProperties.class)
public class MIConfig {

    @Bean
    WebClient mlWebClient(MIProperties props) {
        return WebClient.builder()
                .baseUrl(props.baseUrl())
                .build();
    }
}
