package com.example.jobfinder.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.adapter.NotificationAdapter;
import com.example.jobfinder.model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private android.widget.TextView tvEmptyNotifications;
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

        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        tvEmptyNotifications = findViewById(R.id.tvEmptyNotifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(notificationList, this::markAsRead);
        recyclerViewNotifications.setAdapter(notificationAdapter);

        loadNotifications();
    }

    private void loadNotifications() {
        if (currentUid == null) return;

        db.collection("notifications")
                .whereEqualTo("userId", currentUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notificationList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Notification notif = doc.toObject(Notification.class);
                        notif.setId(doc.getId());
                        notificationList.add(notif);
                    }
                    notificationAdapter.updateData(notificationList);
                    tvEmptyNotifications.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải thông báo: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void markAsRead(Notification notification) {
        if (notification.isRead()) return; // đã đọc rồi thì thôi

        db.collection("notifications").document(notification.getId())
                .update("isRead", true)
                .addOnSuccessListener(unused -> {
                    notification.setRead(true);
                    notificationAdapter.notifyDataSetChanged();
                });
    }
}