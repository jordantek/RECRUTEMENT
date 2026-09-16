package com.tpc.tpcgestpaie.localapp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtils {
     @Value("${jwt.secret}")
     private String jwtSecret;

     @Value("${jwt.expiration}")
     private int jwtExpirationTime;

     public String generateJwtToken(String username) {
         Map<String,Object>clains = new HashMap<>();
          return createToken(clains, username);
     }

     private String createToken(Map<String, Object> clains, String subject) {

         return Jwts.builder()
                 .setClaims(clains)
                 .setSubject(subject)
                 .setIssuedAt(new Date(System.currentTimeMillis()))
                 .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationTime))
                 .signWith(getSignKey(),SignatureAlgorithm.HS512)
                 .compact();
     }

    private Key getSignKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.getJcaName());
    }
    //**Authentification**
    public Boolean validateJwtToken(String authToken , UserDetails userDetails) {
        String username = extractUsername(authToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(authToken));
    }

    private boolean isTokenExpired(String authToken) {
         return extractExpiration(authToken).before(new Date());
    }


    public String extractUsername(String authToken) {
        return extractClaim(authToken, Claims::getSubject);
     }

     private Date extractExpiration(String authToken) {
         return extractClaim(authToken, Claims::getExpiration);
     }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) { // Function
         final Claims claims = extractAllClaims(token);
         return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                    .setSigningKey(getSignKey())
                    .parseClaimsJws(token)
                    .getBody();
    }

    public String getUsernameFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return extractUsername(token);
     }
}
