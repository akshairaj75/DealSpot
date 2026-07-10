package com.backend.dealspot.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secretKey;

        @Value("${jwt.expiration}")
        private long jwtExpiration;

        public String generateToken(CustomUserPrincipal principal) {
                return Jwts.builder()
                                .setSubject(principal.getEmail())
                                .claim("id", principal.getId())
                                .claim("accountType", principal.getAccountType().name())
                                .claim("role", principal.getRole() == null ? "USER" : principal.getRole().name())
                                .setIssuedAt(new Date())
                                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                                .signWith(getSigningKey(),
                                                SignatureAlgorithm.HS256)
                                .compact();
        }

        private Key getSigningKey() {
                byte[] keyBytes = Decoders.BASE64.decode(secretKey);
                return Keys.hmacShaKeyFor(keyBytes);
        }

        public String extractEmail(String token) {
                return Jwts.parserBuilder()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .getSubject();
        }

        public boolean validateToken(String token, UserDetails userDetails) {
                return extractEmail(token)
                                .equals(userDetails
                                                .getUsername())
                                && !isTokenExpired(token);

        }

        private boolean isTokenExpired(String token) {
                Date expiration = Jwts.parserBuilder()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .getExpiration();
                return expiration.before(new Date());
        }

        public String extractAccountType(String token) {

                return Jwts.parserBuilder()
                                .setSigningKey(getSigningKey())
                                .build()
                                .parseClaimsJws(token)
                                .getBody()
                                .get(
                                                "accountType",
                                                String.class);
        }
}
