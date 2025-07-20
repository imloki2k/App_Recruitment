package com.example.irr_project.database;

public class Interview {
    private int interviewId;
    private int applicationId;
    private int studentId;
    private int companyId;
    private String time;
    private String status;

    public Interview(int interviewId, int applicationId, int studentId, int companyId, String time, String status) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.time = time;
        this.status = status;
    }

    // Getters and setters
    public int getInterviewId() { return interviewId; }
    public void setInterviewId(int interviewId) { this.interviewId = interviewId; }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
