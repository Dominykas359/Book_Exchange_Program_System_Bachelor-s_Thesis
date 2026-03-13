package com.dominykas.book.exchange.configuration.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = extractJwt(request);

        log.info("Auth header present: {}, requestURI: {}", jwt != null, request.getRequestURI());

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            authenticate(jwt, request);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }

    private void authenticate(String jwt, HttpServletRequest request) {
        Authentication authentication = jwtAuthenticationProvider.authenticate(jwt, request);

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}