package com.dominykas.book.exchange.configuration.auth;

import com.dominykas.book.exchange.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public Authentication authenticate(String jwt, HttpServletRequest request) {
        String username = jwtService.extractUsername(jwt);

        if (username == null) {
            return null;
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(jwt, userDetails)) {
            return null;
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authToken;
    }
}