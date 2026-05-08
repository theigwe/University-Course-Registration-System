package com.unilag.course_registration_system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnableSemesterRequest {
    private String semesterName;    // FIRST or SECOND
    private String academicSession; // e.g. 2024/2025
}
