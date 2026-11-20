package com.example.jobfinder.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

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
    private String imageUrl;

    // Trạng thái tuyển dụng của Employer: "Open" (mở tuyển) | "Closed" (đóng tuyển)
    private String status = "Open";

    // Trạng thái kiểm duyệt của Admin: "pending" (chờ duyệt) | "approved" (đã duyệt) | "rejected" (từ chối)
    private String adminStatus = "pending";

    // Lý do từ chối nếu Admin từ chối bài
    private String rejectReason;

    private long postedDate;

    public Job() {
        this.status = "Open";
        this.adminStatus = "approved"; // Mặc định tin hợp lệ để hiển thị liền
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
        this.status = "Open";
        this.adminStatus = "approved";
    }

    public Job(String title, String company, String location, String salary,
               String jobType, String description, String imageUrl, long postedDate) {
        this.title = title;
        this.companyName = company;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.description = description;
        this.imageUrl = imageUrl;
        this.postedDate = postedDate;
        this.status = "Open";
        this.adminStatus = "approved";
    }

    public Job(String title, String company, String location, String salary,
               String jobType, String description, String requirements, long postedDate, String status) {
        this.title = title;
        this.companyName = company;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.description = description;
        this.requirements = requirements;
        this.postedDate = postedDate;
        this.status = status != null ? status : "Open";
        this.adminStatus = "approved";
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

    @PropertyName("companyName")
    public String getCompanyName() {
        return companyName;
    }

    @PropertyName("companyName")
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    // Alias hỗ trợ tương thích với trường "company" cũ trên Firestore
    @PropertyName("company")
    public String getCompany() {
        return companyName;
    }

    @PropertyName("company")
    public void setCompany(String company) {
        if (this.companyName == null || this.companyName.isEmpty()) {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdminStatus() {
        return adminStatus;
    }

    public void setAdminStatus(String adminStatus) {
        this.adminStatus = adminStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public boolean isClosed() {
        return "Closed".equalsIgnoreCase(status);
    }

    public boolean isOpen() {
        return status == null || "Open".equalsIgnoreCase(status);
    }

    public boolean isApproved() {
        return adminStatus == null || "approved".equalsIgnoreCase(adminStatus);
    }

    public long getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(long postedDate) {
        this.postedDate = postedDate;
    }
}
