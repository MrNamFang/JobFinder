package com.example.jobfinder.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers;
    private TextView tvTotalJobs;
    private TextView tvTotalApplications;
    private TextView tvTotalRecruiters;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalJobs = findViewById(R.id.tvTotalJobs);
        tvTotalApplications = findViewById(R.id.tvTotalApplications);
        tvTotalRecruiters = findViewById(R.id.tvTotalRecruiters);

        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnManageJobs = findViewById(R.id.btnManageJobs);
        Button btnManageReports = findViewById(R.id.btnManageReports);

        db = FirebaseFirestore.getInstance();

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminUsersActivity.class);
            startActivity(intent);
        });

        btnManageJobs.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminJobsActivity.class);
            startActivity(intent);
        });

        btnManageReports.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, AdminReportsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTotalUsers();
        loadTotalJobs();
        loadTotalApplications();
        loadTotalRecruiters();
    }

    private void loadTotalUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(result -> {
                    tvTotalUsers.setText(String.valueOf(result.size()));
                });
    }

    private void loadTotalJobs() {
        db.collection("jobs")
                .get()
                .addOnSuccessListener(result -> {
                    tvTotalJobs.setText(String.valueOf(result.size()));
                });
    }

    private void loadTotalApplications() {
        db.collection("applications")
                .get()
                .addOnSuccessListener(result -> {
                    if (tvTotalApplications != null) {
                        tvTotalApplications.setText(String.valueOf(result.size()));
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvTotalApplications != null) {
                        tvTotalApplications.setText("0");
                    }
                });
    }

    private void loadTotalRecruiters() {
        db.collection("users")
                .whereEqualTo("role", "recruiter")
                .get()
                .addOnSuccessListener(result -> {
                    tvTotalRecruiters.setText(String.valueOf(result.size()));
                });
    }
}
