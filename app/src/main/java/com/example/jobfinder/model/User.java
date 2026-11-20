package com.example.jobfinder.model;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {
    private String uid;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;

    // Thông tin CV dành cho Ứng viên (Seeker)
    private String education;   // Học vấn
    private String experience;  // Kinh nghiệm làm việc
    private String skills;      // Kỹ năng

    // Thông tin công ty dành cho Nhà tuyển dụng (Employer) - Gom từ bảng employers cũ
    private String companyName;
    private String companyAddress;
    private String companyDescription;

    private String role; // "seeker" | "employer" | "admin"
    private String status = "active"; // "active" | "banned"
    private boolean blocked = false;  // Giữ alias tương thích dữ liệu cũ
    private long createdAt;

    public User() {
    }

    public User(String uid, String fullName, String email, String phone, String role) {
        this.uid = uid;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = "active";
        this.blocked = false;
        this.createdAt = System.currentTimeMillis();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // Alias cho trường "name" nếu trên Firestore có doc cũ dùng "name"
    @PropertyName("name")
    public String getName() {
        return fullName;
    }

    @PropertyName("name")
    public void setName(String name) {
        if (this.fullName == null || this.fullName.isEmpty()) {
            this.fullName = name;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
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

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyDescription() {
        return companyDescription;
    }

    public void setCompanyDescription(String companyDescription) {
        this.companyDescription = companyDescription;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        if ("banned".equalsIgnoreCase(status)) {
            this.blocked = true;
        } else if ("active".equalsIgnoreCase(status)) {
            this.blocked = false;
        }
    }

    public boolean isBlocked() {
        return blocked || "banned".equalsIgnoreCase(status);
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
        if (blocked) {
            this.status = "banned";
        }
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
