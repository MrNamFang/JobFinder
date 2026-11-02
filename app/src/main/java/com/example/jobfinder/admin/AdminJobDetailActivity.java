package com.example.jobfinder.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminJobDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStatus;
    private TextView tvCompany;
    private TextView tvLocation;
    private TextView tvSalary;
    private TextView tvJobType;
    private TextView tvRequirements;
    private TextView tvDescription;

    private Button btnApprove;
    private Button btnReject;
    private Button btnBack;

    private FirebaseFirestore db;
    private String jobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_job_detail);

        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnApprove = findViewById(R.id.btnApprove);
        btnReject = findViewById(R.id.btnReject);

        btnBack.setOnClickListener(v -> finish());

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvCompany = findViewById(R.id.tvDetailCompany);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvSalary = findViewById(R.id.tvDetailSalary);
        tvJobType = findViewById(R.id.tvDetailJobType);
        tvRequirements = findViewById(R.id.tvDetailRequirements);
        tvDescription = findViewById(R.id.tvDetailDescription);

        jobId = getIntent().getStringExtra("jobId");
        String title = getIntent().getStringExtra("title");
        String company = getIntent().getStringExtra("company");
        String location = getIntent().getStringExtra("location");
        String salary = getIntent().getStringExtra("salary");
        String jobType = getIntent().getStringExtra("jobType");
        String requirements = getIntent().getStringExtra("requirements");
        String description = getIntent().getStringExtra("description");
        String status = getIntent().getStringExtra("status");

        tvTitle.setText(title);
        tvCompany.setText("Công ty: " + company);
        tvLocation.setText("Địa điểm: " + location);
        tvSalary.setText("Mức lương: " + salary);
        tvJobType.setText("Loại công việc: " + jobType);
        tvRequirements.setText(requirements);
        tvDescription.setText(description);

        updateStatusView(status);

        // Nút duyệt bài
        btnApprove.setOnClickListener(v -> updateJobStatus("approved", "Đã duyệt bài đăng thành công!"));

        // Nút từ chối bài
        btnReject.setOnClickListener(v -> updateJobStatus("rejected", "Đã từ chối bài đăng!"));
    }

    private void updateStatusView(String status) {
        if ("approved".equalsIgnoreCase(status)) {
            tvStatus.setText("Trạng thái: Đã duyệt (Xanh)");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
        } else if ("rejected".equalsIgnoreCase(status)) {
            tvStatus.setText("Trạng thái: Bị từ chối (Đỏ)");
            tvStatus.setTextColor(Color.parseColor("#C62828"));
            tvStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));
        } else {
            tvStatus.setText("Trạng thái: Chờ duyệt (Vàng)");
            tvStatus.setTextColor(Color.parseColor("#E65100"));
            tvStatus.setBackgroundColor(Color.parseColor("#FFE0B2"));
        }
    }

    private void updateJobStatus(String newStatus, String toastMessage) {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã bài đăng", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("adminStatus", newStatus);
        updates.put("status", newStatus);

        db.collection("jobs")
                .document(jobId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    updateStatusView(newStatus);
                    Toast.makeText(AdminJobDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminJobDetailActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
