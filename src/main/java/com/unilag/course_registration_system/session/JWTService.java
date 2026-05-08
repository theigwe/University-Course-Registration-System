package com.unilag.course_registration_system.session;

import com.unilag.course_registration_system.entity.Student;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JWTService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Generate a JWT token embedding studentId, email, and firstName as claims.
     */
    public String generateToken(Student student) {
        Map<String, Object> claims = getStringObjectMap(student);

        Instant now = Instant.now();
        Instant expiry = now.plusMillis(jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(student.getStudentId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    private static Map<String, Object> getStringObjectMap(Student student) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("studentId", student.getStudentId());
        claims.put("email",     student.getEmail());
        claims.put("firstName", student.getFirstName());
        claims.put("lastName", student.getLastName());
        claims.put("departmentId", student.getDepartments().getId());
        claims.put("facultyId", student.getDepartments().getFaculties().getId());

        if (student.getLastName() != null) {
            claims.put("lastName", student.getLastName());
        }
        return claims;
    }

    public String generateAdminToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("username", username);

        Instant now    = Instant.now();
        Instant expiry = now.plusMillis(jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validate a token and return its claims.
     * Throws JwtException if invalid or expired.
     */
    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Returns true if the token is valid and not expired.
     */
    public boolean isTokenValid(String token) {
        try {
            validateAndGetClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Instant getIssuedAt(String token) {
        return validateAndGetClaims(token).getIssuedAt().toInstant();
    }

    public Instant getExpiration(String token) {
        return validateAndGetClaims(token).getExpiration().toInstant();
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
