package com.unilag.course_registration_system.service.impl;

import com.unilag.course_registration_system.dto.request.EnableSemesterRequest;
import com.unilag.course_registration_system.dto.response.ActiveSemesterResponse;
import com.unilag.course_registration_system.dto.response.Response;
import com.unilag.course_registration_system.entity.ActiveSemester;
import com.unilag.course_registration_system.repository.ActiveSemesterRepository;
import com.unilag.course_registration_system.service.ActiveSemesterService;
import com.unilag.course_registration_system.utils.ResponseCodes;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ActiveSemesterServiceImpl implements ActiveSemesterService {

    private final ActiveSemesterRepository activeSemesterRepository;

    @Override
    @Transactional
    public Response<ActiveSemesterResponse> enableSemester(EnableSemesterRequest request) {
        if (request.getSemesterName() == null ||
                (!request.getSemesterName().equals("FIRST") && !request.getSemesterName().equals("SECOND"))) {
            return new Response<>(ResponseCodes.VALIDATION_FAILED_CODE, "Semester must be FIRST or SECOND");
        }
        if (request.getAcademicSession() == null || request.getAcademicSession().isBlank()) {
            return new Response<>(ResponseCodes.VALIDATION_FAILED_CODE, "Academic session is required");
        }

        activeSemesterRepository.deactivateAll();

        ActiveSemester semester = new ActiveSemester();
        semester.setSemesterName(request.getSemesterName());
        semester.setAcademicSession(request.getAcademicSession());
        semester.setActive(true);
        activeSemesterRepository.save(semester);

        return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE, "Registration period activated",
                new ActiveSemesterResponse(semester.getSemesterName(), semester.getAcademicSession(), true));
    }

    @Override
    public Response<ActiveSemesterResponse> getActiveSemester() {
        Optional<ActiveSemester> active = activeSemesterRepository.findByActiveTrue();
        if (active.isEmpty()) {
            return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE, "No active registration period",
                    new ActiveSemesterResponse(null, null, false));
        }
        ActiveSemester s = active.get();
        return new Response<>(ResponseCodes.GENERAL_SUCCESS_CODE, "Active registration period retrieved",
                new ActiveSemesterResponse(s.getSemesterName(), s.getAcademicSession(), true));
    }
}
