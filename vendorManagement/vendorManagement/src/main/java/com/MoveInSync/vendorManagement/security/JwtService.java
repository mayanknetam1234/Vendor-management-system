package com.MoveInSync.vendorManagement.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.io.Decoders;

@Service
@PropertySource("classpath:env.properties")
public class JwtService {

    // Loaded from env.properties
    @Value("${JWT_SECRET}")
    private String secretKey;

    private Key getSignKey() {
        String keyStr = secretKey != null ? secretKey.trim() : "";
        byte[] keyBytes;
        try {
            // If it looks like Base64, decode it; otherwise treat as raw text
            if (keyStr.matches("^[A-Za-z0-9+/=]+$") && keyStr.length() % 4 == 0) {
                keyBytes = Decoders.BASE64.decode(keyStr);
            } else {
                keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            keyBytes = keyStr.getBytes(StandardCharsets.UTF_8);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hrs
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            String extracted = extractUsername(token);
            Date exp = extractAllClaims(token).getExpiration();
            return username.equals(extracted) && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
