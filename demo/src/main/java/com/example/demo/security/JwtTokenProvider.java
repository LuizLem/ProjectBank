package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret:defaultSecretKeyForDevelopmentOnlyChangeInProduction123456789}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:3600000}")
    private long jwtExpirationMs;

    /**
     * Gera um token JWT para um usuário
     * @param userId o ID do usuário
     * @param email o email do usuário
     * @return o token JWT
     */
    public String generateToken(UUID userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrai o ID do usuário do token
     * @param token o token JWT
     * @return o ID do usuário
     */
    public UUID getUserIdFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return UUID.fromString(claims.getSubject());
        } catch (Exception e) {
            log.error("Erro ao extrair ID do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrai o email do token
     * @param token o token JWT
     * @return o email do usuário
     */
    public String getEmailFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.get("email", String.class);
        } catch (Exception e) {
            log.error("Erro ao extrair email do token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Valida se o token é válido
     * @param token o token JWT
     * @return true se válido, false caso contrário
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtém o tempo de expiração em segundos
     * @return tempo de expiração
     */
    public long getExpirationTimeInSeconds() {
        return jwtExpirationMs / 1000;
    }
}
