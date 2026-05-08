package com.unilag.course_registration_system.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course extends BaseEntity {
    private String courseCode;
    private String courseTitle;
    private int creditUnit;
    @ElementCollection
    @CollectionTable(name = "course_prerequisites", joinColumns = @JoinColumn(name = "course_code"))
    @Column(name = "prerequisite_code")
    private List<String> prerequisite;
    private int availableSlots;
    private String level;
    private String semester;

    /**
     * Relationship: Many Courses belong to one Department.
     */
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    public Course(String courseCode, String courseTitle, int creditUnit, List<String> prerequisite, int availableSlots, String level, String semester, Department department) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditUnit = creditUnit;
        this.prerequisite = prerequisite;
        this.availableSlots = availableSlots;
        this.level = level;
        this.semester = semester;
        this.department = department;
    }

    @Override
    public String toString() {
        return "Course{courseCode='" + courseCode + "', courseTitle='" + courseTitle + "', semester='" + semester + "'}";
    }
}
