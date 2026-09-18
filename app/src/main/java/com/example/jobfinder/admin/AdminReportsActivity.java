package com.example.jobfinder.admin;

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

public class AdminReportsActivity extends AppCompatActivity {

    private RecyclerView rvReports;
    private TextView tvReportCount;

    private List<AdminReport> reportList;
    private AdminReportAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_reports);

        // Nút quay lại
        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Ánh xạ giao diện
        rvReports = findViewById(R.id.rvReports);
        tvReportCount = findViewById(R.id.tvReportCount);

        // Firebase
        db = FirebaseFirestore.getInstance();

        // Danh sách feedback
        reportList = new ArrayList<>();

        // Adapter
        adapter = new AdminReportAdapter(reportList);

        // RecyclerView
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(adapter);

        // Tải feedback
        loadReports();
    }

    private void loadReports() {

        db.collection("feedbacks")
                .get()
                .addOnSuccessListener(result -> {

                    reportList.clear();

                    // Kiểm tra số document lấy được
                    Toast.makeText(
                            AdminReportsActivity.this,
                            "Tìm thấy " + result.size() + " feedbacks",
                            Toast.LENGTH_SHORT
                    ).show();

                    for (var document : result) {

                        android.util.Log.d(
                                "FEEDBACK_DEBUG",
                                "DOCUMENT = " + document.getData()
                        );

                        String id = document.getId();

                        String userName = document.getString("userName");
                        String userEmail = document.getString("userEmail");
                        String userId = document.getString("userId");

                        String content = document.getString("content");


                        String status = document.getString("status");
                        String adminReply = document.getString("adminReply");

                        // Không bắt buộc phải đọc createdAt
                        // nên tạm thời bỏ qua để tránh lỗi kiểu dữ liệu
                        long createdAt = 0;

                        if (userName == null) {
                            userName = "Chưa có tên";
                        }

                        if (userEmail == null) {
                            userEmail = "Chưa có email";
                        }

                        if (userId == null) {
                            userId = "";
                        }

                        if (content == null) {
                            content = "Không có nội dung";
                        }

                        if (status == null) {
                            status = "Chưa xử lý";
                        }

                        if (adminReply == null) {
                            adminReply = "";
                        }

                        AdminReport report = new AdminReport(
                                id,
                                userName,
                                userEmail,
                                userId,
                                content,
                                status,
                                adminReply,
                                createdAt
                        );

                        reportList.add(report);
                    }

                    tvReportCount.setText(
                            "Tổng số feedback: " + reportList.size()
                    );

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    // Hiện lỗi Firebase cụ thể
                    tvReportCount.setText("Lỗi tải feedback");

                    Toast.makeText(
                            AdminReportsActivity.this,
                            "LỖI FIREBASE:\n" + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}