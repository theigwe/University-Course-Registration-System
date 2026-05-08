package com.unilag.course_registration_system.model;

import java.util.List;

public interface CourseModel {
    String getCourseCode();
    String getCourseTitle();
    int getCreditUnit();
    List<String> getPrerequisite();
    int getAvailableSlots();
    String getLevel();
    String getSemester();
}
