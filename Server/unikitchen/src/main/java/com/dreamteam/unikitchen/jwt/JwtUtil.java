package com.dreamteam.unikitchen.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;

    // Constructor to initialize the signing key using the secret key from application properties
    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(decodedKey); // Create a secure HMAC key for signing tokens
    }

    // Generates a JWT token for the given username and userId
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username) // Set the username as the subject of the token
                .setIssuedAt(new Date()) // Set the token's issue time to the current date and time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // Set expiration time (7 days)
                .signWith(key) // Sign the token with the HMAC key
                .compact(); // Build and return the token as a compact string
    }

    // Validates the token and retrieves the subject (username) if valid
    public String validateTokenAndGetSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key) // Set the signing key for validation
                    .build()
                    .parseClaimsJws(token) // Parse and validate the token
                    .getBody()
                    .getSubject(); // Return the subject (username) from the token
        } catch (ExpiredJwtException e) {
            // Token is expired
            throw new JwtException("Token has expired", e);
        } catch (JwtException e) {
            // Other JWT-specific exceptions
            throw new JwtException("Invalid token", e);
        } catch (Exception e) {
            throw new RuntimeException("Token validation failed", e);
        }
    }

}
