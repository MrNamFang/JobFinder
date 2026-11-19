package com.example.jobfinder;

import java.io.Serializable;

/**
 * Model đại diện cho 1 tin tuyển dụng.
 * Sau này khi nối Firestore, mỗi object Job tương ứng với 1 document
 * trong collection "jobs". Bây giờ dùng tạm dưới dạng object Java thường
 * (mock data) để build UI trước.
 *
 * implements Serializable: cho phép object này được "đóng gói" gửi qua
 * Intent giữa các Activity (ví dụ mở màn Sửa tin và truyền job cần sửa sang).
 */
public class Job implements Serializable {

    private String id;          // ID định danh (Firestore document ID sau này; giờ tự đặt tạm)
    private String employerId;  // uid của employer đã đăng tin này
    private String status;      // "Open" (đang tuyển) hoặc "Closed" (đã đóng)
    private String title;
    private String company;
    private String location;
    private String salary;
    private String jobType;
    private String description;
    private String imageUrl;
    private long postedDate;

    // Constructor rỗng - bắt buộc phải có để Firestore map dữ liệu tự động sau này
    public Job() {
    }

    public Job(String title, String company, String location, String salary,
               String jobType, String description, String imageUrl, long postedDate) {
        this.title = title;
        this.company = company;
        this.location = location;
        this.salary = salary;
        this.jobType = jobType;
        this.description = description;
        this.imageUrl = imageUrl;
        this.postedDate = postedDate;
        this.status = "Open"; // mặc định tin mới đăng luôn ở trạng thái "đang tuyển"
    }

    // ----- Getters & Setters -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmployerId() { return employerId; }
    public void setEmployerId(String employerId) { this.employerId = employerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getPostedDate() { return postedDate; }
    public void setPostedDate(long postedDate) { this.postedDate = postedDate; }
}