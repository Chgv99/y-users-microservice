package com.chgvcode.y.users.auth.service;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.chgvcode.y.users.auth.dto.AccessTokenDto;
import com.chgvcode.y.users.config.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Service
public class JwtService implements IJwtService {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${application.security.jwt.expiration}")
    private int EXPIRATION_MS;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Override
    public AccessTokenDto generateToken(String uuid, String username, Role role, Instant createdAt) {
        String token = generate(uuid, username, role, createdAt);
        Long seconds = getExpirationSeconds(token);
        return new AccessTokenDto(token, seconds);
    }

    private String generate(String uuid, String username, Role role, Instant createdAt) {
        return Jwts.builder()
                .subject(uuid)
                .claim("username", username)
                .claim("role", role)
                .claim("createdAt", createdAt.toEpochMilli())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getSignInKey())
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
 
    @Override
    public String extractUsername(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("username", String.class);
    }

    @Override
    public long getExpirationSeconds(String token) {
        final Claims claims = extractAllClaims(token);
        long expirationMilis = claims.getExpiration().getTime();
        long issuedMilis = claims.getIssuedAt().getTime();
        return (expirationMilis - issuedMilis) / 1000;
    }

    private Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    private SecretKey getSignInKey() {
        return key;
    }
}
