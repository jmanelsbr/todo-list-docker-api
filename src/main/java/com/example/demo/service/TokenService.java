package com.example.demo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secretKey;

    @Value("${api.security.token.expiration-ms}")
   private long expiration;

    public String generateToken(UserDetails userDetails) {
     String accessToken = Jwts.builder().
               subject(userDetails.getUsername()).
               expiration(new Date(System.currentTimeMillis() + expiration)).
               signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256).compact();
    return   accessToken;
    }

    public String getSubject(String token) {

        Claims claims = (Claims) Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parse(token)
                .getPayload();



        return claims.getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {

        try {
            Claims claims = (Claims) Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parse(token)
                    .getPayload();


            return (claims.getSubject().equals(userDetails.getUsername())) && claims.getExpiration().after(new Date());
        }  catch (JwtException e) {
            return false;
        }

    }
}
