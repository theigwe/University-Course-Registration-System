package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.RegistrationRequest;
import com.unilag.course_registration_system.dto.response.RegistrationStatusResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.model.CourseModel;
import com.unilag.course_registration_system.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("registrations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class RegistrationController{
    private final RegistrationService registrationService;

    @PostMapping
    public Response<Void> courseRegistration(@RequestBody RegistrationRequest request){
        return registrationService.courseRegistration(request);
    }

    @GetMapping("status")
    public Response<RegistrationStatusResponse> checkRegistrationStatus(){
        return registrationService.checkRegistrationStatus();
    }

    @GetMapping("courses")
    public Response<List<CourseModel>> getRegisteredCourses(){
        return registrationService.getRegisteredCourses();
    }
}
