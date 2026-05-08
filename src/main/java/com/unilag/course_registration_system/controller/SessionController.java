package com.unilag.course_registration_system.controller;

import com.unilag.course_registration_system.dto.request.CreateSession;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.dto.response.TokenResponse;
import com.unilag.course_registration_system.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    public Response<TokenResponse> createSession(@RequestBody CreateSession request) {
        return sessionService.createSession(request);
    }
}
