package com.example.irr_project; // HOẶC package com.example.irr_project.model;

public class Application {
    private int applicationId;
    private int studentId;
    private int internshipId;
    private String internshipTitle;
    private String companyName;
    private String resume;
    private String status;

    // Constructor
    public Application(int applicationId, int studentId, int internshipId, String internshipTitle, String companyName, String resume, String status) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.internshipTitle = internshipTitle;
        this.companyName = companyName;
        this.resume = resume;
        this.status = status;
    }

    // Getters
    public int getApplicationId() { return applicationId; }
    public int getStudentId() { return studentId; }
    public int getInternshipId() { return internshipId; }
    public String getInternshipTitle() { return internshipTitle; }
    public String getCompanyName() { return companyName; }
    public String getResume() { return resume; }
    public String getStatus() { return status; }

    // Setter (ví dụ cho status)
    public void setStatus(String status) { this.status = status; }

    public static class Status { // Đổi tên để tránh trùng với tên lớp, hoặc bỏ static nếu không cần thiết từ bên ngoài Application
        public static final String PENDING = "Pending";
        public static final String ACCEPTED = "Accepted";
        public static final String REJECTED = "Rejected";
        public static final String UNDER_REVIEW = "Under Review";
        public static final String WITHDRAWN = "Withdrawn";
    }
}