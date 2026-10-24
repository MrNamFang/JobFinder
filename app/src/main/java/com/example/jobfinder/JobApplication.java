package com.example.jobfinder;

import java.io.Serializable;

/**
 * Model đại diện cho 1 đơn ứng tuyển. Sau này tương ứng 1 document trong
 * Firestore collection "applications". Giờ dùng mock data để build UI trước.
 */
public class JobApplication implements Serializable {

    private String id;
    private String jobId;
    private String employerId;
    private String jobTitle;
    private String userId;
    private String fullName;
    private String email;
    private String phone;
    private String coverLetter;
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

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public long getAppliedDate() { return appliedDate; }
    public void setAppliedDate(long appliedDate) { this.appliedDate = appliedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}