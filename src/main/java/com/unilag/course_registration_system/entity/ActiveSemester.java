package com.unilag.course_registration_system.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "active_semesters")
public class ActiveSemester extends BaseEntity {
    private String semesterName;    // FIRST or SECOND
    private String academicSession; // e.g. 2024/2025
    private boolean active;
}
