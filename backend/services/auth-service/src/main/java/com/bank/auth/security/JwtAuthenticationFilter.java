package com.bank.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService
            userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path =
                request.getServletPath();

        /*
         * PUBLIC ENDPOINTS
         */
        if (
                path.startsWith("/api/v1/auth")
                        || path.startsWith("/swagger-ui")
                        || path.startsWith("/v3/api-docs")
        ) {

            filterChain.doFilter(request, response);

            return;
        }

        /*
         * READ AUTH HEADER
         */
        String authHeader =
                request.getHeader("Authorization");

        /*
         * NO TOKEN
         */
        if (
                authHeader == null
                        || !authHeader.startsWith("Bearer ")
        ) {

            filterChain.doFilter(request, response);

            return;
        }

        try {

            /*
             * EXTRACT TOKEN
             */
            String token =
                    authHeader.substring(7);

            /*
             * EXTRACT USERNAME
             */
            String username =
                    jwtUtil.extractUsername(token);

            /*
             * VALIDATE USER
             */
            if (
                    username != null
                            && SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null
            ) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username
                                );

                /*
                 * VALIDATE TOKEN
                 */
                if (
                        jwtUtil.isTokenValid(
                                token,
                                userDetails
                        )
                ) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception ex) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter()
                    .write("Invalid JWT Token");

            return;
        }

        /*
         * CONTINUE REQUEST
         */
        filterChain.doFilter(request, response);
    }
}