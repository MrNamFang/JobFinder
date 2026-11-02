package com.example.jobfinder.admin;

import android.content.Intent;
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

    private List<AdminJob> jobList;
    private AdminJobAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_jobs);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rvJobs = findViewById(R.id.rvJobs);
        tvJobCount = findViewById(R.id.tvJobCount);

        db = FirebaseFirestore.getInstance();

        jobList = new ArrayList<>();

        adapter = new AdminJobAdapter(
                jobList,
                job -> {
                    Intent intent = new Intent(
                            AdminJobsActivity.this,
                            AdminJobDetailActivity.class
                    );

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    private void loadJobs() {

        db.collection("jobs")
                .get()
                .addOnSuccessListener(result -> {

                    jobList.clear();

                    for (var document : result) {

                        String id = document.getId();

                        String title = document.getString("title");

                        String company = document.getString("companyName");
                        if (company == null) {
                            company = document.getString("company");
                        }

                        String description = document.getString("description");
                        String jobType = document.getString("jobType");

                        // Sửa lỗi đọc địa điểm từ "rental" sang các trường location / address / province
                        String location = document.getString("location");
                        if (location == null) {
                            location = document.getString("address");
                        }
                        if (location == null) {
                            location = document.getString("province");
                        }

                        String salary = document.getString("salary");
                        if (salary == null) {
                            salary = document.getString("salaryDisplayText");
                        }

                        String requirements = document.getString("requirements");

                        // Trạng thái duyệt bài từ Admin (pending / approved / rejected)
                        String status = document.getString("adminStatus");
                        if (status == null) {
                            status = document.getString("status");
                        }
                        if (status == null) {
                            status = "pending";
                        }

                        Long postedDateValue = document.getLong("postedDate");
                        long postedDate = postedDateValue != null ? postedDateValue : 0;

                        if (title == null) title = "Chưa có tiêu đề";
                        if (company == null) company = "Chưa có công ty";
                        if (description == null) description = "Chưa có mô tả";
                        if (jobType == null) jobType = "Toàn thời gian";
                        if (location == null) location = "Chưa có địa điểm";
                        if (salary == null) salary = "Thỏa thuận";
                        if (requirements == null) requirements = "Chưa có yêu cầu";

                        jobList.add(
                                new AdminJob(
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
                                )
                        );
                    }

                    tvJobCount.setText("Tổng số bài đăng: " + jobList.size());
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    tvJobCount.setText("Không thể tải bài đăng");
                    Toast.makeText(
                            AdminJobsActivity.this,
                            "Lỗi tải bài: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}
