package com.example.jobfinder.model;

import java.io.Serializable;

public class SavedJob implements Serializable {
    private String id;
    private String userId;
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String location;
    private String salary;
    private String jobType;
    private long savedDate;

    public SavedJob() {
    }

    public SavedJob(String userId, String jobId, String jobTitle, String companyName,
                    String location, String salary, String jobType, long savedDate) {
        this.userId = userId;
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.savedDate = savedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public long getSavedDate() {
        return savedDate;
    }

    public void setSavedDate(long savedDate) {
        this.savedDate = savedDate;
    }
}