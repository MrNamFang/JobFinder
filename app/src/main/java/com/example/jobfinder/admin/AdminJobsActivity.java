package com.example.jobfinder.admin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminJobsActivity extends AppCompatActivity {

    private RecyclerView rvJobs;
    private TextView tvJobCount;
    private Button btnJobFilterAll, btnJobFilterPending, btnJobFilterApproved, btnJobFilterRejected;

    private List<AdminJob> allJobList;
    private List<AdminJob> filteredJobList;
    private AdminJobAdapter adapter;

    private FirebaseFirestore db;
    private String currentStatusFilter = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_jobs);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvJobs = findViewById(R.id.rvJobs);
        tvJobCount = findViewById(R.id.tvJobCount);

        btnJobFilterAll = findViewById(R.id.btnJobFilterAll);
        btnJobFilterPending = findViewById(R.id.btnJobFilterPending);
        btnJobFilterApproved = findViewById(R.id.btnJobFilterApproved);
        btnJobFilterRejected = findViewById(R.id.btnJobFilterRejected);

        db = FirebaseFirestore.getInstance();
        allJobList = new ArrayList<>();
        filteredJobList = new ArrayList<>();

        adapter = new AdminJobAdapter(
                filteredJobList,
                job -> {
                    Intent intent = new Intent(AdminJobsActivity.this, AdminJobDetailActivity.class);
                    intent.putExtra("jobId", job.getId());
                    intent.putExtra("title", job.getTitle());
                    intent.putExtra("company", job.getCompany());
                    intent.putExtra("description", job.getDescription());
                    intent.putExtra("jobType", job.getJobType());
                    intent.putExtra("location", job.getLocation());
                    intent.putExtra("salary", job.getSalary());
                    intent.putExtra("requirements", job.getRequirements());
                    intent.putExtra("status", job.getStatus());
                    intent.putExtra("postedDate", job.getPostedDate());
                    startActivity(intent);
                }
        );

        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        rvJobs.setAdapter(adapter);

        btnJobFilterAll.setOnClickListener(v -> setFilter("all"));
        btnJobFilterPending.setOnClickListener(v -> setFilter("pending"));
        btnJobFilterApproved.setOnClickListener(v -> setFilter("approved"));
        btnJobFilterRejected.setOnClickListener(v -> setFilter("rejected"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    private void setFilter(String filter) {
        this.currentStatusFilter = filter;
        updateFilterButtonStyles();
        applyFilter();
    }

    private void updateFilterButtonStyles() {
        int activeColor = Color.parseColor("#388E3C");
        int inactiveColor = Color.parseColor("#757575");

        btnJobFilterAll.setBackgroundTintList(ColorStateList.valueOf("all".equals(currentStatusFilter) ? activeColor : inactiveColor));
        btnJobFilterPending.setBackgroundTintList(ColorStateList.valueOf("pending".equals(currentStatusFilter) ? activeColor : inactiveColor));
        btnJobFilterApproved.setBackgroundTintList(ColorStateList.valueOf("approved".equals(currentStatusFilter) ? activeColor : inactiveColor));
        btnJobFilterRejected.setBackgroundTintList(ColorStateList.valueOf("rejected".equals(currentStatusFilter) ? activeColor : inactiveColor));
    }

    private void applyFilter() {
        filteredJobList.clear();

        for (AdminJob job : allJobList) {
            boolean matches = true;
            String status = job.getStatus();
            if (status == null) status = "pending";

            if ("pending".equals(currentStatusFilter)) {
                matches = "pending".equalsIgnoreCase(status);
            } else if ("approved".equals(currentStatusFilter)) {
                matches = "approved".equalsIgnoreCase(status);
            } else if ("rejected".equals(currentStatusFilter)) {
                matches = "rejected".equalsIgnoreCase(status);
            }

            if (matches) {
                filteredJobList.add(job);
            }
        }

        tvJobCount.setText("Hiển thị: " + filteredJobList.size() + " / " + allJobList.size() + " bài tuyển dụng");
        adapter.notifyDataSetChanged();
    }

    private void loadJobs() {
        db.collection("jobs")
                .get()
                .addOnSuccessListener(result -> {
                    allJobList.clear();

                    for (var document : result) {
                        String id = document.getId();
                        String title = document.getString("title");

                        String company = document.getString("companyName");
                        if (company == null) company = document.getString("company");

                        String description = document.getString("description");
                        String jobType = document.getString("jobType");

                        String location = document.getString("location");
                        if (location == null) location = document.getString("address");
                        if (location == null) location = document.getString("province");

                        String salary = document.getString("salary");
                        if (salary == null) salary = document.getString("salaryDisplayText");

                        String requirements = document.getString("requirements");

                        String status = document.getString("adminStatus");
                        if (status == null) status = document.getString("status");
                        if (status == null) status = "pending";

                        Long postedDateValue = document.getLong("postedDate");
                        long postedDate = postedDateValue != null ? postedDateValue : 0;

                        if (title == null) title = "Chưa có tiêu đề";
                        if (company == null) company = "Chưa có công ty";
                        if (description == null) description = "Chưa có mô tả";
                        if (jobType == null) jobType = "Toàn thời gian";
                        if (location == null) location = "Chưa có địa điểm";
                        if (salary == null) salary = "Thỏa thuận";
                        if (requirements == null) requirements = "Chưa có yêu cầu";

                        allJobList.add(new AdminJob(
                                id,
                                title,
                                company,
                                description,
                                jobType,
                                location,
                                salary,
                                requirements,
                                status,
                                postedDate
                        ));
                    }

                    applyFilter();
                })
                .addOnFailureListener(e -> {
                    tvJobCount.setText("Không thể tải bài đăng");
                    Toast.makeText(AdminJobsActivity.this, "Lỗi tải bài: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
