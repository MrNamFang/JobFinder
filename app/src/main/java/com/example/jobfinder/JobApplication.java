package com.example.jobfinder;

import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

/**
 * Model đại diện cho 1 đơn ứng tuyển, tương ứng 1 document trong
 * Firestore collection "applications".
 *
 * Lưu ý: bên ứng viên ghi các field applicantName / applicantEmail / applicantPhone,
 * nên dùng @PropertyName để map vào fullName / email / phone ở đây.
 */
public class JobApplication implements Serializable {

    private String id;
    private String jobId;
    private String employerId;
    private String jobTitle;
    private String companyName;
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String coverLetter;
    private String education;
    private String experience;
    private String skills;
    private long appliedDate;
    private String status; // "Pending" / "Reviewed" / "Accepted" / "Rejected"

    public JobApplication() {
    }

    public JobApplication(String jobId, String employerId, String jobTitle, String userId,
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
        this.status = "Pending";
    }

    // ----- Getters & Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getEmployerId() { return employerId; }
    public void setEmployerId(String employerId) { this.employerId = employerId; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    @PropertyName("applicantName")
    public String getFullName() { return fullName; }
    @PropertyName("applicantName")
    public void setFullName(String fullName) { this.fullName = fullName; }

    @PropertyName("applicantEmail")
    public String getEmail() { return email; }
    @PropertyName("applicantEmail")
    public void setEmail(String email) { this.email = email; }

    @PropertyName("applicantPhone")
    public String getPhone() { return phone; }
    @PropertyName("applicantPhone")
    public void setPhone(String phone) { this.phone = phone; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public long getAppliedDate() { return appliedDate; }
    public void setAppliedDate(long appliedDate) { this.appliedDate = appliedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}