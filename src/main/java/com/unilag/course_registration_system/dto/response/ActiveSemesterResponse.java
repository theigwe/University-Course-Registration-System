package com.unilag.course_registration_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActiveSemesterResponse {
    private String semesterName;
    private String academicSession;
    private boolean active;
}
