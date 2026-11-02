package com.example.jobfinder.model;

import java.io.Serializable;

public class Application implements Serializable {

    // Giá trị status lưu trong Firestore (thống nhất với bên nhà tuyển dụng)
    public static final String STATUS_PENDING  = "Pending";
    public static final String STATUS_REVIEWED = "Reviewed";
    public static final String STATUS_ACCEPTED = "Accepted";
    public static final String STATUS_REJECTED = "Rejected";

    private String id;
    private String jobId;
    private String jobTitle;
    private String companyName;
    private String employerId;
    private String userId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String education;
    private String experience;
    private String skills;
    private String status; // "Pending", "Reviewed", "Accepted", "Rejected"
    private long appliedDate;

    public Application() {
    }

    public Application(String jobId, String jobTitle, String companyName, String employerId,
                       String userId, String applicantName, String applicantEmail, String applicantPhone,
                       String education, String experience, String skills,
                       String status, long appliedDate) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.employerId = employerId;
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

    /** Chuẩn hóa về giá trị tiếng Anh, vẫn hiểu dữ liệu cũ tiếng Việt đã lưu. */
    public static String normalize(String s) {
        if (s == null) return STATUS_PENDING;
        switch (s) {
            case "Reviewed":
            case "Đã xem":
                return STATUS_REVIEWED;
            case "Accepted":
            case "Được duyệt":
                return STATUS_ACCEPTED;
            case "Rejected":
            case "Từ chối":
                return STATUS_REJECTED;
            default:
                return STATUS_PENDING;
        }
    }

    /** Nhãn tiếng Việt để hiển thị. */
    public static String label(String s) {
        switch (normalize(s)) {
            case STATUS_REVIEWED:
                return "Đã xem";
            case STATUS_ACCEPTED:
                return "Được duyệt";
            case STATUS_REJECTED:
                return "Từ chối";
            default:
                return "Đang chờ";
        }
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

    public String getEmployerId() {
        return employerId;
    }

    public void setEmployerId(String employerId) {
        this.employerId = employerId;
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