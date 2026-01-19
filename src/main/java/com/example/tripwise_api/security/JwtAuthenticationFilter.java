package com.example.tripwise_api.security;

import com.example.tripwise_api.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        // Skip JWT validation for public endpoints
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

        logger.debug("Processing request to: " + requestURI);
        logger.debug("Authorization header present: " + (authorizationHeader != null));

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
                logger.debug("Extracted username from JWT: " + username);
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("JWT token is expired for request to: " + requestURI);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Access token expired", "Please refresh your token");
                return;
            } catch (io.jsonwebtoken.MalformedJwtException | io.jsonwebtoken.security.SignatureException e) {
                logger.warn("Invalid JWT token for request to: " + requestURI);
                sendAuthFailedResponse(response);
                return;
            } catch (Exception e) {
                logger.error("JWT token extraction failed: " + e.getMessage());
                sendAuthFailedResponse(response);
                return;
            }
        } else {
            logger.debug("No Bearer token found in Authorization header for: " + requestURI);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication required", "Please log in to access this resource");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Validate specifically as access token
                if (jwtUtil.validateAccessToken(jwt, username)) {
                    UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Successfully authenticated user: " + username);
                } else {
                    logger.warn("Access token validation failed for user: " + username);
                    sendAuthFailedResponse(response);
                    return;
                }
            } catch (Exception e) {
                logger.error("Error during authentication: " + e.getMessage());
                sendAuthFailedResponse(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String error, String message) throws IOException {
        // 'status' is always HttpServletResponse.SC_UNAUTHORIZED in current usage, but method kept flexible for future use
        response.setStatus(status);
        response.getWriter().write(String.format("{\"error\":\"%s\",\"message\":\"%s\"}", error, message));
        response.getWriter().flush();
    }

    private void sendAuthFailedResponse(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed", "Please log in again");
    }
}
