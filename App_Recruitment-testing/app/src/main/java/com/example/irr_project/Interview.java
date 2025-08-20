package com.example.irr_project;

public class Interview {
    private int interviewId;
    private int applicationId;
    private int studentId;
    private int companyId;
    private String time;
    private String status;
    private String internshipTitle;

    public enum Status {
        PROPOSED("Proposed"),
        CONFIRMED("Confirmed"),
        DECLINED("Declined");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public Interview(int interviewId, int applicationId, int studentId, int companyId, String time, String status, String internshipTitle) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.companyId = companyId;
        this.time = time;
        this.status = status;
        this.internshipTitle = internshipTitle;
    }

    public int getInterviewId() {
        return interviewId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }

    public String getInternshipTitle() {
        return internshipTitle;
    }
}