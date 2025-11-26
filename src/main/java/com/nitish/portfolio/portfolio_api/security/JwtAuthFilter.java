package com.nitish.portfolio.portfolio_api.security;

import com.nitish.portfolio.portfolio_api.repository.AdminRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.List;


import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        // --- DEBUG: print incoming header ---
        System.out.println("[JwtAuthFilter] Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No token → treat as no authentication
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        String username = null;
        try {
            // Try to extract username, but guard for parsing exceptions
            username = jwtService.extractUsername(token);
            System.out.println("[JwtAuthFilter] extracted username from token: " + username);
        } catch (io.jsonwebtoken.JwtException je) {
            // token malformed / signature invalid / expired etc.
            System.err.println("[JwtAuthFilter] JWT parse error: " + je.getClass().getSimpleName() + " - " + je.getMessage());
            je.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or malformed token: " + je.getMessage());
            return;
        } catch (Exception ex) {
            System.err.println("[JwtAuthFilter] unexpected error extracting username: " + ex.getMessage());
            ex.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token handling failed");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            boolean isValid;
            try {
                isValid = jwtService.isTokenValid(token, username);
            } catch (Exception e) {
                System.err.println("[JwtAuthFilter] error while validating token: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token validation error: " + e.getMessage());
                return;
            }

            System.out.println("[JwtAuthFilter] token valid? " + isValid);

            if (!isValid) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid or expired token");
                return;
            }

            // Token valid → authenticate user. Use empty authority list (not null).
            //update
//            UsernamePasswordAuthenticationToken authToken =
//                    new UsernamePasswordAuthenticationToken(username, null, java.util.Collections.emptyList());
            var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);



            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }


}
