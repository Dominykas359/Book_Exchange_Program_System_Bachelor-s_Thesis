package com.dominykas.book.exchange.configuration.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ml")
public record MIProperties(
        String baseUrl,
        int timeoutSeconds
) {}
