package com.quickbite.buildingblocks.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final int jwtExpirationInMs = 86400000; // 24 hours

    public JwtTokenProvider(@Value("${app.jwt.secret}") String jwtSecret) {
        // Decode the secret string from application.yml consistently across all
        // services
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date expiryDate = new Date(new Date().getTime() + jwtExpirationInMs);

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return (List<String>) claims.get("roles");
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(authToken);
            System.out.println("--- [JWT PROVIDER] Token validation SUCCESSFUL!");
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException ex) {
            System.err.println("=== [JWT PROVIDER ERROR] Token is EXPIRED: " + ex.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException ex) {
            System.err.println(
                    "=== [JWT PROVIDER ERROR] Invalid JWT SIGNATURE (Secret key mismatch): " + ex.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException ex) {
            System.err.println("=== [JWT PROVIDER ERROR] Malformed JWT token: " + ex.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException ex) {
            System.err.println("=== [JWT PROVIDER ERROR] Unsupported JWT token: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("=== [JWT PROVIDER ERROR] JWT claims string is empty or null: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("=== [JWT PROVIDER ERROR] Unexpected exception: " + ex.getClass().getName() + " - "
                    + ex.getMessage());
        }
        return false;
    }
}