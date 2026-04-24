package com.dominykas.book.exchange.configuration;

import com.dominykas.book.exchange.configuration.auth.JwtAuthenticationFilter;
import com.dominykas.book.exchange.configuration.auth.JwtAuthenticationProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtAuthenticationProvider provider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        filter = new JwtAuthenticationFilter(provider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoAuthorizationHeader_ShouldSkipAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilterInternal(request, response, filterChain);

        verify(provider, never()).authenticate(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidHeader_ShouldSkipAuthentication() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Invalid");
        when(request.getRequestURI()).thenReturn("/test");

        filter.doFilterInternal(request, response, filterChain);

        verify(provider, never()).authenticate(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ValidHeader_ShouldAuthenticate() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(request.getRequestURI()).thenReturn("/test");

        when(provider.authenticate("token", request)).thenReturn(authentication);

        filter.doFilterInternal(request, response, filterChain);

        verify(provider).authenticate("token", request);
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isEqualTo(authentication);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_AlreadyAuthenticated_ShouldSkipProvider() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(request.getRequestURI()).thenReturn("/test");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filter.doFilterInternal(request, response, filterChain);

        verify(provider, never()).authenticate(any(), any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_ProviderReturnsNull_ShouldNotSetContext() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(request.getRequestURI()).thenReturn("/test");

        when(provider.authenticate("token", request)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}