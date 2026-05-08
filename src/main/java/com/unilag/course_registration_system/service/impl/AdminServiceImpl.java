package com.unilag.course_registration_system.service.impl;

import com.unilag.course_registration_system.dto.request.AdminLoginRequest;
import com.unilag.course_registration_system.dto.response.AdminTokenResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.exception.NotFoundException;
import com.unilag.course_registration_system.service.AdminService;
import com.unilag.course_registration_system.session.JWTService;
import com.unilag.course_registration_system.utils.ResponseCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final JWTService jwtService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public Response<AdminTokenResponse> login(AdminLoginRequest request) {
        if (!adminUsername.equals(request.getUsername()) || !adminPassword.equals(request.getPassword())) {
            throw new NotFoundException("Invalid admin credentials");
        }

        String token   = jwtService.generateAdminToken(request.getUsername());
        var expiresAt  = jwtService.getExpiration(token);

        AdminTokenResponse body = AdminTokenResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(request.getUsername())
                .expiresAt(expiresAt)
                .build();

        return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE, "Admin login successful", body);
    }
}
