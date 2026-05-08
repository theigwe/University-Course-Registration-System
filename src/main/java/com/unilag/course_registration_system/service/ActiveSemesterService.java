package com.unilag.course_registration_system.service;

import com.unilag.course_registration_system.dto.request.EnableSemesterRequest;
import com.unilag.course_registration_system.dto.response.ActiveSemesterResponse;
import com.unilag.course_registration_system.dto.response.Response;

public interface ActiveSemesterService {
    Response<ActiveSemesterResponse> enableSemester(EnableSemesterRequest request);
    Response<ActiveSemesterResponse> getActiveSemester();
}
