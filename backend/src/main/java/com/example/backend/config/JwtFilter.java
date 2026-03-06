package com.example.backend.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.backend.service.JwtService;
import com.example.backend.service.TokenBlacklistService;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String email = null;

        // 1. Try to read access token from HttpOnly cookie
        if (request.getCookies() != null) {
            for (javax.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2. Fallback: read from Authorization header (for backward compatibility)
        if (token == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
        }

        if (token != null) {
            try {
                email = jwtService.extractEmail(token);
            } catch (Exception e) {
                // Invalid token, skip authentication
            }
        }

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Check if the token has been blacklisted (logged out)
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            UserDetails userDetails = context.getBean(UserDetailsService.class).loadUserByUsername(email);
            
            if(jwtService.validateToken(token)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
