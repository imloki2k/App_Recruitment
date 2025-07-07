package com.example.irr_project;

public class Internship {
    private int id;
    private String title, company, location, duration, field, description, requirements, stipend, deadline, datePosted;

    public Internship(int id, String title, String company, String location, String duration, String field,
                      String description, String requirements, String stipend, String deadline, String datePosted) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.location = location;
        this.duration = duration;
        this.field = field;
        this.description = description;
        this.requirements = requirements;
        this.stipend = stipend;
        this.deadline = deadline;
        this.datePosted = datePosted;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public String getDuration() { return duration; }
    public String getField() { return field; }
    public String getDescription() { return description; }
    public String getRequirements() { return requirements; }
    public String getStipend() { return stipend; }
    public String getDeadline() { return deadline; }
    public String getDatePosted() { return datePosted; }

    @Override
    public String toString() {
        return title + " (" + datePosted + ")";
    }
}