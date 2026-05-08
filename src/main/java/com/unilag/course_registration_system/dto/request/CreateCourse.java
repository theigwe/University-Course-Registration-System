package com.unilag.course_registration_system.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class CreateCourse {
    private String courseCode;
    private String courseTitle;
    private int creditUnit;
    private List<String> prerequisite;
    private int availableSlots;
    private Long departmentId;
    private String level;
    private String semester;
}
