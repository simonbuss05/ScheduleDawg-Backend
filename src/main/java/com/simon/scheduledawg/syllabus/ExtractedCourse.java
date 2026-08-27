package com.simon.scheduledawg.syllabus;

public class ExtractedCourse {

    private String name;
    private String code;
    private String professor;
    private Integer creditHours;

    public ExtractedCourse() {

    }

    public ExtractedCourse(String name, String code, String professor, Integer creditHours) {
        this.name = name;
        this.code = code;
        this.professor = professor;
        this.creditHours = creditHours;
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

}
