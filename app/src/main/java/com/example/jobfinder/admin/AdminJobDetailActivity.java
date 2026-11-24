package com.example.jobfinder.admin;

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
        com.google.android.material.dialog.MaterialAlertDialogBuilder builder =
                new com.google.android.material.dialog.MaterialAlertDialogBuilder(this);
        builder.setTitle("Từ chối bài tuyển dụng");
        builder.setMessage("Vui lòng nhập lý do từ chối để thông báo cho Nhà tuyển dụng:");

        final EditText input = new EditText(this);
        input.setHint("VD: Thông tin mức lương chưa rõ ràng, vi phạm tiêu chuẩn cộng đồng...");
        input.setTextColor(0xFF2B1B12);
        input.setHintTextColor(0xFF7A5A48);
        input.setTextSize(15f);
        input.setBackgroundResource(android.R.drawable.editbox_background);
        input.setPadding(40, 30, 40, 30);

        android.widget.FrameLayout container = new android.widget.FrameLayout(this);
        android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.leftMargin = 50;
        params.rightMargin = 50;
        params.topMargin = 10;
        params.bottomMargin = 10;
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);

        builder.setPositiveButton("Xác nhận từ chối", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            if (reason.isEmpty()) {
                reason = "Bài đăng chưa đạt tiêu chuẩn kiểm duyệt của hệ thống.";
            }
            updateJobStatus("rejected", reason, "Đã từ chối bài đăng!");
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(0);
        }
        dialog.show();
    }

    private void updateStatusView(String status, String rejectReason) {
        if ("approved".equalsIgnoreCase(status)) {
            tvStatus.setText("Trạng thái: Đã duyệt");
            tvStatus.setTextColor(0xFF0B5C55);
            tvStatus.setBackgroundResource(R.drawable.bg_status_accepted);
            tvRejectReason.setVisibility(View.GONE);
        } else if ("rejected".equalsIgnoreCase(status)) {
            tvStatus.setText("Trạng thái: Bị từ chối");
            tvStatus.setTextColor(0xFFB3261E);
            tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);

            if (rejectReason != null && !rejectReason.isEmpty()) {
                tvRejectReason.setVisibility(View.VISIBLE);
                tvRejectReason.setText("Lý do từ chối: " + rejectReason);
            } else {
                tvRejectReason.setVisibility(View.GONE);
            }
        } else {
            tvStatus.setText("Trạng thái: Chờ duyệt");
            tvStatus.setTextColor(0xFF9A4B00);
            tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
            tvRejectReason.setVisibility(View.GONE);
        }
    }

    //hàm cập nhâ lên firebase
    private void updateJobStatus(String newStatus, String rejectReason, String toastMessage) {
        if (jobId == null || jobId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy mã bài đăng", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("adminStatus", newStatus);
        if ("rejected".equalsIgnoreCase(newStatus)) {
            updates.put("status", "Closed");//đóng bài
        } else if ("approved".equalsIgnoreCase(newStatus)) {
            updates.put("status", "Open");//mở bài cho ứng viên thấy
        }
        if (rejectReason != null) {
            updates.put("rejectReason", rejectReason);//lưu lý do
        }

        db.collection("jobs")
                .document(jobId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    updateStatusView(newStatus, rejectReason);
                    Toast.makeText(AdminJobDetailActivity.this, toastMessage, Toast.LENGTH_SHORT).show();

                    // Gửi thông báo cho Nhà tuyển dụng sở hữu bài đăng này
                    db.collection("jobs").document(jobId).get().addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String empId = doc.getString("employerId");
                            String jTitle = doc.getString("title");
                            if (empId != null && !empId.isEmpty()) {
                                if ("approved".equalsIgnoreCase(newStatus)) {
                                    com.example.jobfinder.NotificationHelper.sendInAppNotification(
                                            empId,
                                            "Tin tuyển dụng đã được duyệt",
                                            "Bài đăng '" + jTitle + "' đã được Admin phê duyệt và xuất hiện trên Trang chủ."
                                    );
                                } else if ("rejected".equalsIgnoreCase(newStatus)) {
                                    com.example.jobfinder.NotificationHelper.sendInAppNotification(
                                            empId,
                                            "Tin tuyển dụng bị từ chối",
                                            "Bài đăng '" + jTitle + "' bị từ chối kiểm duyệt. Lý do: " + rejectReason
                                    );
                                }
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AdminJobDetailActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}