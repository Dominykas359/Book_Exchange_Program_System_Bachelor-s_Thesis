package com.dominykas.book.exchange.configuration;

import com.dominykas.book.exchange.configuration.auth.JwtAuthenticationProvider;
import com.dominykas.book.exchange.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetails userDetails;

    private JwtAuthenticationProvider provider;

    @BeforeEach
    void setup() {
        provider = new JwtAuthenticationProvider(jwtService, userDetailsService);
    }

    @Test
    void authenticate_WhenUsernameIsNull_ShouldReturnNull() {
        when(jwtService.extractUsername("token")).thenReturn(null);

        Authentication result = provider.authenticate("token", request);

        assertThat(result).isNull();
        verify(jwtService).extractUsername("token");
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void authenticate_WhenTokenInvalid_ShouldReturnNull() {
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token", userDetails)).thenReturn(false);

        Authentication result = provider.authenticate("token", request);

        assertThat(result).isNull();
        verify(jwtService).isTokenValid("token", userDetails);
    }

    @Test
    void authenticate_WhenTokenValid_ShouldReturnAuthentication() {
        when(jwtService.extractUsername("token")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(userDetails);
        when(jwtService.isTokenValid("token", userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        Authentication result = provider.authenticate("token", request);

        assertThat(result).isNotNull();
        assertThat(result.getPrincipal()).isEqualTo(userDetails);
        assertThat(result.isAuthenticated()).isTrue();

        verify(userDetailsService).loadUserByUsername("user");
        verify(jwtService).isTokenValid("token", userDetails);
    }
}