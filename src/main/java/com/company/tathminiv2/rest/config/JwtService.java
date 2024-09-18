package com.company.tathminiv2.rest.config;


import com.company.tathminiv2.entity.TathminiUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {
    private static  final String SECRET_KEY="bWFuYXNzZXNjaGVnZW1haW5hc2Nob2xhcmx5d3JpdGVz";
    public String extractUsername(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims= extractAllClaims(token);
        return  claimsResolver.apply(claims);
    }
    public  String generateToken(TathminiUser tathminiUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", tathminiUser.getEmail());
        return generateToken(claims,tathminiUser);
    }
    public String generateToken(Map<String,Object> extraClaims,TathminiUser tathminiUser){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(tathminiUser.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 60*24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean isTokenValid(String token, TathminiUser tathminiUser) {
        final String username= extractUsername(token);
        return(username.equals(tathminiUser.getUsername()))&& !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    Claims extractAllClaims(String token) {

        return Jwts

                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] KeyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(KeyBytes);
    }
}