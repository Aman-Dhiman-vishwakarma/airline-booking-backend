package com.airline.booking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Cookie se JWT read karo
        String token = extractTokenFromCookie(request);

        // 2. Agar cookie/token nahi hai
        //    to request ko next filter ki taraf bhej do
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. JWT valid hai ya nahi check karo
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. JWT ke subject se userId nikalo
        Long userId = jwtService.extractUserId(token);

        // 5. Database se UserDetails load karo
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        String.valueOf(userId)
                );

        // 6. Authentication object banao
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // 7. SecurityContext me authentication set karo
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        // DEBUG
        Authentication currentAuthentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        System.out.println("===== SECURITY DEBUG =====");
        System.out.println("URI: "
                + request.getRequestURI());

        System.out.println("Authenticated: "
                + currentAuthentication.isAuthenticated());

        System.out.println("Principal: "
                + currentAuthentication.getName());

        System.out.println("Authorities: "
                + currentAuthentication.getAuthorities());

        System.out.println("==========================");

        // 8. Request ko aage bhejo
        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(
            HttpServletRequest request
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {

            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}