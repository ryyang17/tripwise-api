package com.example.tripwise_api.security;

import com.example.tripwise_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        final String requestURI = request.getRequestURI();
        final String method = request.getMethod();

        // 1) Allow CORS preflight through
        if ("OPTIONS".equals(method)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Skip public endpoints
        if (requestURI.equals("/") ||
            requestURI.equals("/favicon.ico") ||
            requestURI.startsWith("/api/auth/") ||
            requestURI.startsWith("/swagger-ui") ||
            requestURI.startsWith("/v3/api-docs") ||
            requestURI.startsWith("/actuator/") ||
            requestURI.startsWith("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        String username;
        String jwt;

        // 3) Expect a Bearer token
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                // Token expired - client should use refresh
                logger.warn("Access token expired for request to {}", requestURI);
                sendUnauthorized(response, "Access token expired", "Please refresh your token");
                return;
            } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.security.SignatureException e) {
                logger.warn("Invalid JWT token for request to {}", requestURI);
                sendAuthFailedResponse(response);
                return;
            } catch (Exception e) {
                logger.error("JWT extraction failed for request {}: {}", requestURI, e.getMessage());
                sendAuthFailedResponse(response);
                return;
            }
        } else {
            // No token presented
            sendUnauthorized(response, "Authentication required", "Please log in to access this resource");
            return;
        }

        // 4) If username found and no existing Authentication, validate token and set SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateAccessToken(jwt, username)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    logger.warn("Access token validation failed for user: {}", username);
                    sendAuthFailedResponse(response);
                    return;
                }
            } catch (Exception e) {
                logger.error("Error during authentication for user {}: {}", username, e.getMessage());
                sendAuthFailedResponse(response);
                return;
            }
        }

        // 5) Continue filter chain
        filterChain.doFilter(request, response);
    }

//
    private void sendUnauthorized(HttpServletResponse response, String error, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(String.format("{\"error\":\"%s\",\"message\":\"%s\"}", error, message));
        response.getWriter().flush();
    }

    private void sendAuthFailedResponse(HttpServletResponse response) throws IOException {
        sendUnauthorized(response, "Authentication failed", "Please log in again");
    }
}
