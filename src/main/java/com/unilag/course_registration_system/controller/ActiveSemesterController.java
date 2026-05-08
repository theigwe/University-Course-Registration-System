package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.EnableSemesterRequest;
import com.unilag.course_registration_system.dto.response.ActiveSemesterResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.service.ActiveSemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("active-semester")
public class ActiveSemesterController {

    private final ActiveSemesterService activeSemesterService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("enable")
    public Response<ActiveSemesterResponse> enableSemester(@RequestBody EnableSemesterRequest request) {
        return activeSemesterService.enableSemester(request);
    }

    @GetMapping
    public Response<ActiveSemesterResponse> getActiveSemester() {
        return activeSemesterService.getActiveSemester();
    }
}
