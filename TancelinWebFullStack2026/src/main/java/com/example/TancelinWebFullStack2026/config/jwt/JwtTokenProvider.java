package com.example.TancelinWebFullStack2026.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {


    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSecretKey() {
       byte[] keyBytes = Base64.getDecoder().decode(secret);
       return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateToken(Authentication authentication) {

        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + 1000 * 60 * 60 * 24);

        String roles = authentication.getAuthorities().stream().map(
                grantedAuthority -> grantedAuthority.getAuthority()
        ).collect(Collectors.joining(","));

        String token = Jwts
                .builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
        return token;
    }


    public boolean validateToken(String token) {
      try {
          Jwts
                  .parserBuilder()
                  .setSigningKey(getSecretKey())
                  .build()
                  .parseClaimsJws(token);

          return true;
      }catch (Exception e) {
          throw new JwtException("Invalid JWT");
      }

    }

public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
}


}
