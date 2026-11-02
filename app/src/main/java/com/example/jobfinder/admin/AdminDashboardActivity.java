package com.example.jobfinder.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminDashboardActivity extends AppCompatActivity {

    private TextView tvTotalUsers;
    private TextView tvTotalJobs;
    private TextView tvTotalApplications;
    private TextView tvTotalRecruiters;
    private TextView tvPendingJobs;
    private TextView tvPendingReports;

    private View cardPendingJobs;
    private View cardPendingReports;
    private Button btnLogoutAdmin;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_dashboard);

        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvTotalJobs = findViewById(R.id.tvTotalJobs);
        tvTotalApplications = findViewById(R.id.tvTotalApplications);
        tvTotalRecruiters = findViewById(R.id.tvTotalRecruiters);
        tvPendingJobs = findViewById(R.id.tvPendingJobs);
        tvPendingReports = findViewById(R.id.tvPendingReports);

        cardPendingJobs = findViewById(R.id.cardPendingJobs);
        cardPendingReports = findViewById(R.id.cardPendingReports);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);

        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnManageJobs = findViewById(R.id.btnManageJobs);
        Button btnManageReports = findViewById(R.id.btnManageReports);

        db = FirebaseFirestore.getInstance();

        btnManageUsers.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminUsersActivity.class));
        });

        btnManageJobs.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminJobsActivity.class));
        });

        btnManageReports.setOnClickListener(v -> {
            startActivity(new Intent(AdminDashboardActivity.this, AdminReportsActivity.class));
        });

        if (cardPendingJobs != null) {
            cardPendingJobs.setOnClickListener(v -> {
                startActivity(new Intent(AdminDashboardActivity.this, AdminJobsActivity.class));
            });
        }

        if (cardPendingReports != null) {
            cardPendingReports.setOnClickListener(v -> {
                startActivity(new Intent(AdminDashboardActivity.this, AdminReportsActivity.class));
            });
        }

        if (btnLogoutAdmin != null) {
            btnLogoutAdmin.setOnClickListener(v -> {
                com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Đã đăng xuất tài khoản Admin!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, com.example.jobfinder.activity.LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTotalUsers();
        loadTotalJobs();
        loadTotalApplications();
        loadTotalRecruiters();
        loadPendingActions();
    }

    private void loadTotalUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(result -> {
                    if (tvTotalUsers != null) {
                        tvTotalUsers.setText(String.valueOf(result.size()));
                    }
                });
    }

    private void loadTotalJobs() {
        db.collection("jobs")
                .get()
                .addOnSuccessListener(result -> {
                    if (tvTotalJobs != null) {
                        tvTotalJobs.setText(String.valueOf(result.size()));
                    }
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
                    if (tvTotalRecruiters != null) {
                        tvTotalRecruiters.setText(String.valueOf(result.size()));
                    }
                });
    }

    private void loadPendingActions() {
        // Đếm bài đăng chờ duyệt
        db.collection("jobs")
                .get()
                .addOnSuccessListener(result -> {
                    int pendingCount = 0;
                    for (var doc : result) {
                        String status = doc.getString("adminStatus");
                        if (status == null) status = doc.getString("status");
                        if (status == null || "pending".equalsIgnoreCase(status)) {
                            pendingCount++;
                        }
                    }
                    if (tvPendingJobs != null) {
                        tvPendingJobs.setText(String.valueOf(pendingCount));
                    }
                });

        // Đếm khiếu nại chưa xử lý
        db.collection("feedbacks")
                .get()
                .addOnSuccessListener(result -> {
                    int pendingReports = 0;
                    for (var doc : result) {
                        String status = doc.getString("status");
                        if (status == null || !"resolved".equalsIgnoreCase(status)) {
                            pendingReports++;
                        }
                    }
                    if (tvPendingReports != null) {
                        tvPendingReports.setText(String.valueOf(pendingReports));
                    }
                })
                .addOnFailureListener(e -> {
                    if (tvPendingReports != null) {
                        tvPendingReports.setText("0");
                    }
                });
    }
}
