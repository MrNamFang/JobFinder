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

public class AdminUsersActivity extends AppCompatActivity {

    private RecyclerView rvUsers;
    private TextView tvUserCount;
    private List<AdminUser> userList;
    private AdminUserAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_users);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvUsers = findViewById(R.id.rvUsers);
        tvUserCount = findViewById(R.id.tvUserCount);

        db = FirebaseFirestore.getInstance();
        userList = new ArrayList<>();
        adapter = new AdminUserAdapter(userList);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        loadUsers();
    }

    private void loadUsers() {

        db.collection("users")
                .get()
                .addOnSuccessListener(result -> {

                    userList.clear();

                    for (var document : result) {

                        String id = document.getId();

                        // Ưu tiên đọc "fullName", nếu không có thì đọc "name"
                        String name = document.getString("fullName");
                        if (name == null) {
                            name = document.getString("name");
                        }

                        String email = document.getString("email");
                        String role = document.getString("role");

                        // Kiểm tra trạng thái khóa qua cả boolean "blocked" và chuỗi "status" == "banned"
                        Boolean blockedValue = document.getBoolean("blocked");
                        String statusValue = document.getString("status");

                        boolean blocked = (blockedValue != null && blockedValue)
                                || "banned".equalsIgnoreCase(statusValue);

                        if (name == null || name.trim().isEmpty()) {
                            name = "Người dùng (" + id.substring(0, Math.min(6, id.length())) + ")";
                        }

                        if (email == null) {
                            email = "Chưa có email";
                        }

                        if (role == null) {
                            role = "seeker";
                        }

                        userList.add(
                                new AdminUser(
                                        id,
                                        name,
                                        email,
                                        role,
                                        blocked
                                )
                        );
                    }

                    tvUserCount.setText("Tổng số tài khoản: " + userList.size());
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    tvUserCount.setText("Lỗi tải tài khoản");
                    Toast.makeText(
                            AdminUsersActivity.this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}
