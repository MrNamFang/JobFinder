package com.example.jobfinder.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Job implements Serializable {
    private String id;
    private String employerId;
    private String title;
    private String companyName;
    private String location;
    private String salary;
    private String jobType;
    private String description;
    private String requirements;
    private String status;
    private long postedDate;

    public Job() {
    }

    public Job(String id, String title, String companyName, String location,
               String salary, String jobType, String description, String requirements, long postedDate) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.description = description;
        this.requirements = requirements;
        this.postedDate = postedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCompany(String company) {
        if (this.companyName == null) {
            this.companyName = company;
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isClosed() {
        return "Closed".equals(status);
    }

    public long getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(long postedDate) {
        this.postedDate = postedDate;
    }
}