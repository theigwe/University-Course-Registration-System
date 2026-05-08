package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.AdminLoginRequest;
import com.unilag.course_registration_system.dto.response.AdminTokenResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("login")
    public Response<AdminTokenResponse> login(@RequestBody AdminLoginRequest request) {
        return adminService.login(request);
    }
}
