package com.unilag.course_registration_system.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
public class AdminTokenResponse {
    private String token;
    private String tokenType;
    private String username;
    private Instant expiresAt;
}
