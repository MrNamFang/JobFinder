package com.example.jobfinder.admin;

public class AdminJob {

    private String id;
    private String title;
    private String company;
    private String description;
    private String jobType;
    private String location;
    private String salary;
    private String requirements;
    private long postedDate;

    public AdminJob() {
    }

    public AdminJob(
            String id,
            String title,
            String company,
            String description,
            String jobType,
            String location,
            String salary,
            String requirements,
            long postedDate) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.jobType = jobType;
        this.location = location;
        this.salary = salary;
        this.requirements = requirements;
        this.postedDate = postedDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getDescription() {
        return description;
    }

    public String getJobType() {
        return jobType;
    }

    public String getLocation() {
        return location;
    }

    public String getSalary() {
        return salary;
    }

    public String getRequirements() {
        return requirements;
    }

    public long getPostedDate() {
        return postedDate;
    }
}