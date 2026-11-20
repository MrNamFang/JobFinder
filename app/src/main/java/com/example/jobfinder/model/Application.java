package com.example.jobfinder.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

@IgnoreExtraProperties
public class Application implements Serializable {

    // Trạng thái đơn ứng tuyển chuẩn thống nhất cho cả Employer và Seeker
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

    private String fullName;
    private String email;
    private String phone;
    private String coverLetter;
    private String education;
    private String experience;
    private String skills;

    private String status = STATUS_PENDING;
    private long appliedDate;

    public Application() {
        this.status = STATUS_PENDING;
    }

    public Application(String jobId, String jobTitle, String companyName, String employerId,
                       String userId, String fullName, String email, String phone,
                       String education, String experience, String skills,
                       String status, long appliedDate) {
        this.jobId = jobId;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.employerId = employerId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.education = education;
        this.experience = experience;
        this.skills = skills;
        this.status = status != null ? status : STATUS_PENDING;
        this.appliedDate = appliedDate;
    }

    public Application(String jobId, String employerId, String jobTitle, String userId,
                       String fullName, String email, String phone, String coverLetter,
                       long appliedDate) {
        this.jobId = jobId;
        this.employerId = employerId;
        this.jobTitle = jobTitle;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.coverLetter = coverLetter;
        this.appliedDate = appliedDate;
        this.status = STATUS_PENDING;
    }

    /** Chuẩn hóa trạng thái đơn ứng tuyển. */
    public static String normalize(String s) {
        if (s == null) return STATUS_PENDING;
        switch (s.trim()) {
            case "Reviewed":
            case "Đã xem":
                return STATUS_REVIEWED;
            case "Accepted":
            case "Được duyệt":
            case "Chấp nhận":
                return STATUS_ACCEPTED;
            case "Rejected":
            case "Từ chối":
                return STATUS_REJECTED;
            default:
                return STATUS_PENDING;
        }
    }

    /** Nhãn hiển thị tiếng Việt trên giao diện. */
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

    @PropertyName("companyName")
    public String getCompanyName() {
        return companyName;
    }

    @PropertyName("companyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @PropertyName("company")
    public void setCompany(String company) {
        if (this.companyName == null || this.companyName.isEmpty()) {
            this.companyName = company;
        }
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

    @PropertyName("fullName")
    public String getFullName() {
        return fullName;
    }

    @PropertyName("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Tương thích alias "applicantName" nếu Firestore lưu theo tên cũ
    @PropertyName("applicantName")
    public String getApplicantName() {
        return fullName;
    }

    @PropertyName("applicantName")
    public void setApplicantName(String applicantName) {
        if (this.fullName == null || this.fullName.isEmpty()) {
            this.fullName = applicantName;
        }
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("applicantEmail")
    public String getApplicantEmail() {
        return email;
    }

    @PropertyName("applicantEmail")
    public void setApplicantEmail(String applicantEmail) {
        if (this.email == null || this.email.isEmpty()) {
            this.email = applicantEmail;
        }
    }

    @PropertyName("phone")
    public String getPhone() {
        return phone;
    }

    @PropertyName("phone")
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @PropertyName("applicantPhone")
    public String getApplicantPhone() {
        return phone;
    }

    @PropertyName("applicantPhone")
    public void setApplicantPhone(String applicantPhone) {
        if (this.phone == null || this.phone.isEmpty()) {
            this.phone = applicantPhone;
        }
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
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
