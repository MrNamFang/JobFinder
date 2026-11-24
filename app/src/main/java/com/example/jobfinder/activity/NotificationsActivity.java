package com.example.jobfinder.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.NotificationHelper;
import com.example.jobfinder.R;
import com.example.jobfinder.adapter.NotificationAdapter;
import com.example.jobfinder.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private TextView tvEmptyNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(notificationList, this::markAsRead);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        if (currentUid == null) return;

        // Truy vấn theo userId (sắp xếp ở client-side để không yêu cầu tạo composite index trên Firestore)
        db.collection("notifications")
                .whereEqualTo("userId", currentUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Notification notif = doc.toObject(Notification.class);
                        notif.setId(doc.getId());
                        notificationList.add(notif);
                    }
                    if (notificationList.isEmpty()) {
                        // Tự động kiểm tra và đồng bộ thông báo từ các hồ sơ đã nộp/bài đã đăng trước đó
                        syncPastNotifications();
                    } else {
                        Collections.sort(notificationList, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
                        notificationAdapter.updateData(notificationList);
                        tvEmptyNotifications.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải thông báo: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void syncPastNotifications() {
        if (currentUid == null) {
            tvEmptyNotifications.setVisibility(View.VISIBLE);
            return;
        }

        // 1. Kiểm tra xem người dùng có đơn ứng tuyển nào trong collection applications không
        db.collection("applications")
                .whereEqualTo("userId", currentUid)
                .get()
                .addOnSuccessListener(apps -> {
                    if (apps != null && !apps.isEmpty()) {
                        for (QueryDocumentSnapshot doc : apps) {
                            String jobTitle = doc.getString("jobTitle");
                            Long appliedDate = doc.getLong("appliedDate");
                            String status = doc.getString("status");
                            String titleText = jobTitle != null ? jobTitle : "công việc";

                            // Tạo thông báo đã ứng tuyển thành công
                            NotificationHelper.sendInAppNotification(
                                    currentUid,
                                    "Ứng tuyển thành công",
                                    "Bạn đã nộp đơn ứng tuyển vào vị trí: " + titleText
                            );

                            // Nếu trạng thái đã được cập nhật khác Pending
                            if (status != null && !status.equalsIgnoreCase("Pending") && !status.equalsIgnoreCase("Đang chờ")) {
                                NotificationHelper.sendInAppNotification(
                                        currentUid,
                                        "Cập nhật hồ sơ ứng tuyển",
                                        "Đơn ứng tuyển vào '" + titleText + "' của bạn đã được cập nhật: " + status
                                );
                            }
                        }
                        loadNotificationsAfterSync();
                    } else {
                        // 2. Nếu không có đơn với tư cách ứng viên, kiểm tra xem có phải nhà tuyển dụng có bài đăng không
                        checkEmployerSync();
                    }
                })
                .addOnFailureListener(e -> tvEmptyNotifications.setVisibility(View.VISIBLE));
    }

    private void checkEmployerSync() {
        db.collection("jobs")
                .whereEqualTo("employerId", currentUid)
                .get()
                .addOnSuccessListener(jobs -> {
                    if (jobs != null && !jobs.isEmpty()) {
                        for (QueryDocumentSnapshot doc : jobs) {
                            String title = doc.getString("title");
                            String adminStatus = doc.getString("adminStatus");
                            String rejectReason = doc.getString("rejectReason");
                            String jTitle = title != null ? title : "";
                            if ("approved".equalsIgnoreCase(adminStatus)) {
                                NotificationHelper.sendInAppNotification(
                                        currentUid,
                                        "Tin tuyển dụng đã được duyệt",
                                        "Bài đăng '" + jTitle + "' đã được Admin phê duyệt và xuất hiện trên Trang chủ."
                                );
                            } else if ("rejected".equalsIgnoreCase(adminStatus)) {
                                NotificationHelper.sendInAppNotification(
                                        currentUid,
                                        "Tin tuyển dụng bị từ chối",
                                        "Bài đăng '" + jTitle + "' bị từ chối kiểm duyệt. Lý do: " + (rejectReason != null ? rejectReason : "Vi phạm quy định")
                                );
                            }
                        }
                        loadNotificationsAfterSync();
                    } else {
                        tvEmptyNotifications.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> tvEmptyNotifications.setVisibility(View.VISIBLE));
    }

    private void loadNotificationsAfterSync() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            db.collection("notifications")
                    .whereEqualTo("userId", currentUid)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        notificationList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Notification notif = doc.toObject(Notification.class);
                            notif.setId(doc.getId());
                            notificationList.add(notif);
                        }
                        Collections.sort(notificationList, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
                        notificationAdapter.updateData(notificationList);
                        tvEmptyNotifications.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
        }, 800);
    }

    private void markAsRead(Notification notification) {
        if (notification.isRead()) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("isRead", true);
        updates.put("read", true);

        db.collection("notifications").document(notification.getId())
                .update(updates)
                .addOnSuccessListener(unused -> {
                    notification.setRead(true);
                    notificationAdapter.notifyDataSetChanged();
                });
    }
}