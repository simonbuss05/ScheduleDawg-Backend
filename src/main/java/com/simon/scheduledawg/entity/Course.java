// src/main/java/com/simon/scheduledawg/entity/Course.java
package com.simon.scheduledawg.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    private String professor;

    private Integer creditHours;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meeting> meetings = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Assignment> assignments = new ArrayList<>();

    public Course() {

    }

    public Course(String name, String code, String professor, Integer creditHours) {
        this.name = name;
        this.code = code;
        this.professor = professor;
        this.creditHours = creditHours;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getProfessor() {
        return professor;
    }
    public void setProfessor(String professor) {
        this.professor = professor;
    }
    public Integer getCreditHours() {
        return creditHours;
    }
    public void setCreditHours(Integer creditHours) {
        this.creditHours = creditHours;
    }
    public List<Meeting> getMeetings() {
        return meetings;
    }
    public void setMeetings(List<Meeting> meetings) {
        this.meetings = meetings;
    }
    public List<Assignment> getAssignments() {
        return assignments;
    }
    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }
}