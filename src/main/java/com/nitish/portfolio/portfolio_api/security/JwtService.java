package com.nitish.portfolio.portfolio_api.security;


import java.nio.charset.StandardCharsets;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpirationMs;

    // 1) Generate token using username
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(username)       // subject = who this token belongs to (admin username)
                .setIssuedAt(now)           // when token was created
                .setExpiration(expiryDate)  // when token will expire
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // sign with secret
                .compact();                 // build final token string
    }

    // 2) Extract username from token
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 3) Validate token (not expired + properly signed)
    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // ----------------- helpers -----------------

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
    /*
      Robust key handling:
      1) Try to decode app.jwt.secret as Base64 (recommended secure form).
      2) If that fails, fall back to using the raw UTF-8 bytes of the secret string.
      3) This avoids double-encoding bugs and common malformed-key issues.
    */
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            // fallback to raw bytes
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            return Keys.hmacShaKeyFor(keyBytes);
        }
    }
}
