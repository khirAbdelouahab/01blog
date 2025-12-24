package com.blogger._blog.service;

import com.blogger._blog.details.UserDataResponse;
import com.blogger._blog.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {
    @Value("${jwt.secret:mySecretKeyThatIsAtLeast256BitsLongForHS256Algorithm}")
    private String secretKey;
    @Value("${jwt.expiration:3600000}")
    private int jwtExpiration; // 24 hours

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("exp", jwtExpiration); // or use as 'sub' below
        claims.put("username", userDetails.getUsername());
        claims.put("email", userDetails.getEmail());
        claims.put("role", userDetails.getRole().name());// sub
        claims.put("sub", String.valueOf(userDetails.getId()));// sub
        claims.put("state", String.valueOf(userDetails.getState()));// sub

        return createToken(claims, userDetails.getUsername());
    }

    public String generateToken(UserDataResponse userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("exp", jwtExpiration); // or use as 'sub' below
        claims.put("username", userDetails.getUsername());
        claims.put("email", userDetails.getEmail());
        claims.put("role", userDetails.getRole());// sub
        claims.put("sub", String.valueOf(userDetails.getId()));// sub
        claims.put("state", String.valueOf(userDetails.getState()));// sub

        return createToken(claims, userDetails.getUsername());
    }

    public String extractState(String token) {
        return extractClaim(token, claims -> claims.get("state", String.class));
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateTokenByUsername(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
