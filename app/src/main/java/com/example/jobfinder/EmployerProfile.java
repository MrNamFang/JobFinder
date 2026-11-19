package com.example.jobfinder;

import java.io.Serializable;

/**
 * Model đại diện cho hồ sơ 1 nhà tuyển dụng (Employer).
 * Sau này tương ứng 1 document trong Firestore collection "employers",
 * với document ID chính là uid của user (Firebase Auth) -- cách này giúp
 * tra cứu nhanh: chỉ cần biết uid là lấy thẳng được profile, không cần query.
 */
public class EmployerProfile implements Serializable {

    private String uid;
    private String companyName;
    private String description;
    private String address;
    private String phone;
    private String email;

    public EmployerProfile() {
    }

    public EmployerProfile(String uid, String companyName, String description,
                           String address, String phone, String email) {
        this.uid = uid;
        this.companyName = companyName;
        this.description = description;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}