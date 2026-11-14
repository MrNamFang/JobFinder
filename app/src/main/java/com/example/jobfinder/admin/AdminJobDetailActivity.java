package com.example.jobfinder.admin;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminJobDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStatus;
    private TextView tvRejectReason;
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
        tvRejectReason = findViewById(R.id.tvRejectReason);
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

        updateStatusView(status, null);

        // Nạp lý do từ chối nếu có từ Firestore
        if (jobId != null && !jobId.isEmpty()) {
            db.collection("jobs").document(jobId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rejectReason = documentSnapshot.getString("rejectReason");
                            String latestStatus = documentSnapshot.getString("adminStatus");
                            if (latestStatus == null) latestStatus = documentSnapshot.getString("status");
                            updateStatusView(latestStatus, rejectReason);
                        }
                    });
        }

        // Nút duyệt bài
        btnApprove.setOnClickListener(v -> {
            updateJobStatus("approved", "", "Đã duyệt bài đăng thành công!");
        });

        // Nút từ chối bài (Hiện Popup nhập lý do)
        btnReject.setOnClickListener(v -> {
            showRejectDialog();
        });
    }

    private void showRejectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Từ chối bài tuyển dụng");
        builder.setMessage("Vui lòng nhập lý do từ chối để thông báo cho Nhà tuyển dụng:");

        final EditText input = new EditText(this);
        input.setHint("VD: Thông tin mức lương chưa rõ ràng, vi phạm tiêu chuẩn cộng đồng...");
        input.setPadding(30, 20, 30, 20);
        builder.setView(input);

        builder.setPositiveButton("Xác nhận từ chối", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (reason.isEmpty()) {
                reason = "Bài đăng chưa đạt tiêu chuẩn kiểm duyệt của hệ thống.";
            }
            updateJobStatus("rejected", reason, "Đã từ chối bài đăng!");
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void updateStatusView(String status, String rejectReason) {
        if ("approved".equalsIgnoreCase(status)) {
            tvStatus.setText("Trạng thái: Đã duyệt (Xanh)");
            tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            tvStatus.setBackgroundColor(Color.parseColor("#C8E6C9"));
            tvRejectReason.setVisibility(View.GONE);
        } else if ("rejected".equalsIgnoreCase(status)) {
            tvStatus.setText("Trạng thái: Bị từ chối (Đỏ)");
            tvStatus.setTextColor(Color.parseColor("#C62828"));
            tvStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));

            if (rejectReason != null && !rejectReason.isEmpty()) {
                tvRejectReason.setVisibility(View.VISIBLE);
                tvRejectReason.setText("Lý do từ chối: " + rejectReason);
            } else {
                tvRejectReason.setVisibility(View.GONE);
            }
        } else {
            tvStatus.setText("Trạng thái: Chờ duyệt (Vàng)");
            tvStatus.setTextColor(Color.parseColor("#E65100"));
            tvStatus.setBackgroundColor(Color.parseColor("#FFE0B2"));
            tvRejectReason.setVisibility(View.GONE);
        }
    }

    private void updateJobStatus(String newStatus, String rejectReason, String toastMessage) {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã bài đăng", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("adminStatus", newStatus);
        updates.put("status", newStatus);
        if (rejectReason != null) {
            updates.put("rejectReason", rejectReason);
        }

        db.collection("jobs")
                .document(jobId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    updateStatusView(newStatus, rejectReason);
                    Toast.makeText(AdminJobDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminJobDetailActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
