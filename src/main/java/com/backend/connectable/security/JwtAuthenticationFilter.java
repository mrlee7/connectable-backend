package com.backend.connectable.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = AuthorizationExtractor.extract(request);
        String path = request.getRequestURI();
        if (!Objects.isNull(token)) {
            verifyTokenAccordingToPath(token, path);
        }
        filterChain.doFilter(request, response);
    }

    private void verifyTokenAccordingToPath(String token, String path) {
        if (path.startsWith("/admin")) {
            jwtProvider.verifyAdmin(token);
            return;
        }
        jwtProvider.verify(token);
        Authentication authentication = jwtProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
