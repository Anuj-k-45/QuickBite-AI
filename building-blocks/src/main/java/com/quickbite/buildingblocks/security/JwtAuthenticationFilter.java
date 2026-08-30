package com.quickbite.buildingblocks.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        String header = request.getHeader("Authorization");

        System.out.println("--- [JWT FILTER] Incoming Request URI: " + uri);
        if (header != null) {
            System.out.println("--- [JWT FILTER] Authorization Header found (Length: " + header.length() + ")");
            if (header.startsWith("Bearer ")) {
                System.out.println("--- [JWT FILTER] Header starts with 'Bearer '");
            } else {
                System.out.println("--- [JWT FILTER] WARNING: Header does NOT start with 'Bearer '!");
            }
        } else {
            System.out.println("--- [JWT FILTER] WARNING: Authorization Header is MISSING!");
        }

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            try {
                boolean isValid = jwtTokenProvider.validateToken(token);
                System.out.println("--- [JWT FILTER] Token validation result: " + isValid);

                if (isValid) {
                    String phoneNumber = jwtTokenProvider.getUsernameFromToken(token);
                    List<String> roles = jwtTokenProvider.getRolesFromToken(token);

                    System.out.println("--- [JWT FILTER] Token Subject (Phone): " + phoneNumber);
                    System.out.println("--- [JWT FILTER] Token Roles: " + roles);

                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    org.springframework.security.core.userdetails.User principal = new org.springframework.security.core.userdetails.User(
                            phoneNumber, "", authorities);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            principal, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("--- [JWT FILTER] Successfully set Authentication in SecurityContextHolder for: "
                            + phoneNumber);
                } else {
                    System.out.println("--- [JWT FILTER] Token validation failed inside provider.");
                }
            } catch (Exception ex) {
                System.err.println("=== [JWT FILTER ERROR] Exception during token processing: "
                        + ex.getClass().getName() + " - " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}