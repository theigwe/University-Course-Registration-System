package com.unilag.course_registration_system.session;

import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.entity.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX        = "Bearer ";
    private final JWTService   jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {
        log.debug("Request URL: {}", request.getRequestURI());

        String token = extractToken(request);

        if (!StringUtils.hasText(token)) {
            // No token → continue the chain unauthenticated
            // Public endpoints are whitelisted in SecurityConfig and will pass through.
            // Protected endpoints will be caught by AuthorizationFilter below.
            filterChain.doFilter(request, response);
            return;  // ← must return after calling doFilter
        }

        try {
            Claims claims = jwtService.validateAndGetClaims(token);
            setAuthentication(claims, request);
            filterChain.doFilter(request, response);  // ← must call this too
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT validation failed for {}: {}", request.getRequestURI(), ex.getMessage());
            sendUnauthorizedError(response, "Invalid or expired token");
            // ← do NOT call filterChain.doFilter() here — response is already written
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /** Pulls the raw JWT string from "Authorization: Bearer <token>". */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Builds a fully-authenticated token from JWT claims and stores it in
     * the SecurityContext. Admin tokens carry role=ADMIN; all others are ROLE_STUDENT.
     */
    private void setAuthentication(Claims claims, HttpServletRequest request) {
        String role = claims.get("role", String.class);

        Object principal;
        SimpleGrantedAuthority authority;

        if ("ADMIN".equals(role)) {
            principal = claims.getSubject();
            authority = new SimpleGrantedAuthority("ROLE_ADMIN");
            log.debug("Authenticated admin: {}", principal);
        } else {
            principal = Student.builder()
                    .studentId(claims.get("studentId", String.class))
                    .email(claims.get("email",     String.class))
                    .firstName(claims.get("firstName", String.class))
                    .build();
            authority = new SimpleGrantedAuthority("ROLE_STUDENT");
            log.debug("Authenticated student: {}", claims.get("studentId", String.class));
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, List.of(authority));
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /** Writes a JSON 401 response and halts the filter chain. */
    private void sendUnauthorizedError(HttpServletResponse response, String message)
            throws IOException {
        log.debug("Unauthorized error: {}", message);
        Response<Void> body = new Response<>(401, "Unauthorized");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
