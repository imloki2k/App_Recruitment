package com.example.irr_project;

public class Application {
    private int applicationId;
    private int internshipId;
    private int studentId; // Thêm trường student_id
    private String internshipTitle;
    private String companyName;
    private String status;

    public enum Status {
        PENDING("Pending"),
        UNDER_REVIEW("Under Review"),
        ACCEPTED("Accepted"),
        REJECTED("Rejected"),
        WITHDRAWN("Withdrawn");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public Application(int applicationId, int internshipId, int studentId, String internshipTitle, String companyName, String status) {
        this.applicationId = applicationId;
        this.internshipId = internshipId;
        this.studentId = studentId; // Gán student_id
        this.internshipTitle = internshipTitle;
        this.companyName = companyName;
        this.status = status;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public int getInternshipId() {
        return internshipId;
    }

    public int getStudentId() { // Thêm getter cho student_id
        return studentId;
    }

    public String getInternshipTitle() {
        return internshipTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getStatus() {
        return status;
    }
}