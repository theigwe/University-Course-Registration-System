package com.unilag.course_registration_system.service;

import com.unilag.course_registration_system.dto.request.AdminLoginRequest;
import com.unilag.course_registration_system.dto.response.AdminTokenResponse;
import com.unilag.course_registration_system.dto.response.Response;

public interface AdminService {
    Response<AdminTokenResponse> login(AdminLoginRequest request);
}
