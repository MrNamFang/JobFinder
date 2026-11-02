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
import com.google.firebase.firestore.QuerySnapshot;

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

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvReports = findViewById(R.id.rvReports);
        tvReportCount = findViewById(R.id.tvReportCount);

        db = FirebaseFirestore.getInstance();
        reportList = new ArrayList<>();
        adapter = new AdminReportAdapter(reportList);

        rvReports.setLayoutManager(new LinearLayoutManager(this));
        rvReports.setAdapter(adapter);

        loadReports();
    }

    private void loadReports() {
        // Thử tải từ collection "reports" trước, nếu rỗng thì tải từ "feedbacks"
        db.collection("reports")
                .get()
                .addOnSuccessListener(result -> {
                    if (result != null && !result.isEmpty()) {
                        adapter.setCollectionName("reports");
                        parseDocs(result);
                    } else {
                        loadFromFeedbacks();
                    }
                })
                .addOnFailureListener(e -> loadFromFeedbacks());
    }

    private void loadFromFeedbacks() {
        db.collection("feedbacks")
                .get()
                .addOnSuccessListener(result -> {
                    adapter.setCollectionName("feedbacks");
                    parseDocs(result);
                })
                .addOnFailureListener(e -> {
                    tvReportCount.setText("Lỗi tải báo cáo / feedback");
                    Toast.makeText(
                            AdminReportsActivity.this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void parseDocs(QuerySnapshot result) {
        reportList.clear();

        for (var document : result) {
            String id = document.getId();

            String userName = document.getString("userName");
            if (userName == null) userName = document.getString("reporterName");

            String userEmail = document.getString("userEmail");
            if (userEmail == null) userEmail = document.getString("reporterEmail");

            String userId = document.getString("userId");
            if (userId == null) userId = document.getString("reporterId");

            String content = document.getString("content");
            if (content == null) content = document.getString("description");
            if (content == null) content = document.getString("reason");

            String status = document.getString("status");
            String adminReply = document.getString("adminReply");

            long createdAt = 0;
            Long createdLong = document.getLong("createdAt");
            if (createdLong != null) {
                createdAt = createdLong;
            }

            if (userName == null) userName = "Người dùng ẩn danh";
            if (userEmail == null) userEmail = "Không có email";
            if (userId == null) userId = "";
            if (content == null) content = "Không có nội dung chi tiết";
            if (status == null) status = "pending";
            if (adminReply == null) adminReply = "";

            reportList.add(new AdminReport(
                    id,
                    userName,
                    userEmail,
                    userId,
                    content,
                    status,
                    adminReply,
                    createdAt
            ));
        }

        tvReportCount.setText("Tổng số báo cáo / khiếu nại: " + reportList.size());
        adapter.notifyDataSetChanged();
    }
}
