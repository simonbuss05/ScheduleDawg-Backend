package com.simon.scheduledawg.syllabus;

public class ExtractedAssignment {

   private String title;
   private String dueDate;

    public ExtractedAssignment() {

    }

    public ExtractedAssignment(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDueDate() {
        return dueDate;
    }
    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

}
