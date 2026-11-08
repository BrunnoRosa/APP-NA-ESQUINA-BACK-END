package com.example.app_na_esquina.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Puxa a chave do application.properties (que vem do .env)
    @Value("${app.jwt.secret}")
    private String secretKey;

    // Puxa o tempo de expiração do application.properties
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    public String extrairUsername(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodasClaims(token);
        return claimsResolver.apply(claims);
    }

    public String gerarToken(UserDetails userDetails) {
        return gerarToken(new HashMap<>(), userDetails);
    }

    public String gerarToken(Map<String, Object> claimsExtras, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(claimsExtras)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Usa a variável dinâmica
                .signWith(getChaveAssinatura(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean tokenValido(String token, UserDetails userDetails) {
        final String username = extrairUsername(token);
        return (username.equals(userDetails.getUsername())) && !tokenExpirado(token);
    }

    private boolean tokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    private Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getChaveAssinatura())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getChaveAssinatura() {
        // Converte a string hexadecimal em bytes do jeito correto
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}