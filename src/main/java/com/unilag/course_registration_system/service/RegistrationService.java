package com.unilag.course_registration_system.service;

import com.unilag.course_registration_system.dto.request.RegistrationRequest;
import com.unilag.course_registration_system.dto.response.RegistrationStatusResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.model.CourseModel;
import java.util.List;

public interface RegistrationService {
    Response<Void> courseRegistration(RegistrationRequest request);

    Response<RegistrationStatusResponse> checkRegistrationStatus();

    Response<List<CourseModel>> getRegisteredCourses();
}
