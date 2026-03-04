package com.project.Multi_Tenant_SaaS_Backend.security.JWT;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public long ACCESS_TOKEN_VALID_TIME_MILLIS() {
        return 15 * 60 * 1000L; // 15 minutes
    }

    public long REFRESH_TOKEN_VALID_TIME_MILLIS() {
        return 24 * 60 * 60 * 1000L; // 24 Hours
    }

    public String generateAccessToken(UserPrincipal user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type", "ACCESS")
                .claim("userId", user.getUserId())
                .claim("role", user.getRole().name())
                .claim("companyId", user.getCompanyId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALID_TIME_MILLIS()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(final String email){
        return generateToken(email , REFRESH_TOKEN_VALID_TIME_MILLIS());
    }

    public String generateToken(final String email ,final long expirationTime){
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "REFRESH")
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}