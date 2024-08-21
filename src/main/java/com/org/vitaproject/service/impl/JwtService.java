package com.org.vitaproject.service.impl;

import com.org.vitaproject.model.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private String secretKey = "448840d9d664568eaceb4ec0c4a6dcadec254dcf316b3d179749ae1b6f21a710";

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isValid(String token, UserDetails user) {
        return user.getUsername().equals(extractUserName(token)) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build().parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(UserEntity user) {
        String token = Jwts.
                builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10 * 24 * 1000 * 60 * 60))
                .signWith(getSignInKey()).
                compact();
        return token;
    }

    private SecretKey getSignInKey() {
        byte[] keyBets = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBets);
    }
}
