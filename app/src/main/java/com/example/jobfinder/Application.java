package com.example.jobfinder.model;

import java.io.Serializable;

public class Application implements Serializable {
    private String id;
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String userId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String education;
    private String experience;
    private String skills;
    private String status; // "Đang chờ", "Được duyệt", "Từ chối"
    private long appliedDate;

    public Application() {
    }

    public Application(String jobId, String jobTitle, String companyName, String userId,
                       String applicantName, String applicantEmail, String applicantPhone,
                       String education, String experience, String skills,
                       String status, long appliedDate) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.userId = userId;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicantPhone = applicantPhone;
        this.education = education;
        this.experience = experience;
        this.skills = skills;
        this.status = status;
        this.appliedDate = appliedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantEmail() {
        return applicantEmail;
    }

    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(long appliedDate) {
        this.appliedDate = appliedDate;
    }
}