package com.example.jobfinder.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

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

        btnBack.setOnClickListener(v -> {
            finish();
        });

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
                    intent.putExtra("postedDate", job.getPostedDate());

                    startActivity(intent);
                }
        );

        rvJobs.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvJobs.setAdapter(adapter);

        loadJobs();
    }

    private void loadJobs() {

        db.collection("jobs")
                .get()
                .addOnSuccessListener(result -> {

                    jobList.clear();

                    for (var document : result) {

                        String id = document.getId();

                        String title =
                                document.getString("title");

                        String company =
                                document.getString("companyName");

                        String description =
                                document.getString("description");

                        String jobType =
                                document.getString("jobType");

                        String location =
                                document.getString("rental");

                        String salary =
                                document.getString("salary");

                        String requirements =
                                document.getString("requirements");

                        Long postedDateValue =
                                document.getLong("postedDate");

                        long postedDate =
                                postedDateValue != null
                                        ? postedDateValue
                                        : 0;

                        if (title == null) {
                            title = "Chưa có tên công việc";
                        }

                        if (company == null) {
                            company = "Chưa có công ty";
                        }

                        if (description == null) {
                            description = "Chưa có mô tả";
                        }

                        if (jobType == null) {
                            jobType = "Chưa xác định";
                        }

                        if (location == null) {
                            location = "Chưa có địa điểm";
                        }

                        if (salary == null) {
                            salary = "Chưa có mức lương";
                        }

                        if (requirements == null) {
                            requirements = "Chưa có yêu cầu";
                        }

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
                                        postedDate
                                )
                        );
                    }

                    tvJobCount.setText(
                            "Tổng số bài đăng: "
                                    + jobList.size()
                    );

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {

                    tvJobCount.setText(
                            "Không thể tải bài đăng"
                    );

                    Toast.makeText(
                            AdminJobsActivity.this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}