package com.example.jobfinder.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends AppCompatActivity {

    // Danh sách tài khoản
    private RecyclerView rvUsers;

    // Hiển thị tổng số tài khoản
    private TextView tvUserCount;

    // Danh sách User
    private List<AdminUser> userList;

    // Adapter kết nối dữ liệu với RecyclerView
    private AdminUserAdapter adapter;

    // Firebase Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hiển thị giao diện activity_admin_users.xml
        setContentView(R.layout.activity_admin_users);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        // Ánh xạ View
        rvUsers = findViewById(R.id.rvUsers);
        tvUserCount = findViewById(R.id.tvUserCount);

        // Khởi tạo Firebase
        db = FirebaseFirestore.getInstance();

        // Tạo danh sách rỗng
        userList = new ArrayList<>();

        // Tạo Adapter
        adapter = new AdminUserAdapter(userList);

        // RecyclerView hiển thị theo chiều dọc
        rvUsers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Gắn Adapter vào RecyclerView
        rvUsers.setAdapter(adapter);

        // Lấy danh sách tài khoản
        loadUsers();
    }

    private void loadUsers() {

        db.collection("users")
                .get()
                .addOnSuccessListener(result -> {

                    // Xóa dữ liệu cũ
                    userList.clear();

                    // Duyệt từng tài khoản
                    for (var document : result) {

                        String id = document.getId();

                        String name = document.getString("name");

                        String email = document.getString("email");

                        String role = document.getString("role");

                        Boolean blockedValue =
                                document.getBoolean("blocked");

                        boolean blocked =
                                blockedValue != null && blockedValue;

                        // Xử lý trường hợp thiếu dữ liệu
                        if (name == null) {
                            name = "Chưa có tên";
                        }

                        if (email == null) {
                            email = "Chưa có email";
                        }

                        if (role == null) {
                            role = "Chưa xác định";
                        }

                        // Thêm tài khoản vào danh sách
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

                    // Hiển thị tổng số tài khoản
                    tvUserCount.setText(
                            "Tổng số tài khoản: " + userList.size()
                    );

                    // Cập nhật danh sách
                    adapter.notifyDataSetChanged();
                })

                // Nếu Firebase xảy ra lỗi
                .addOnFailureListener(e -> {

                    tvUserCount.setText(
                            "Lỗi tải tài khoản"
                    );

                    android.widget.Toast.makeText(
                            AdminUsersActivity.this,
                            "Lỗi: " + e.getMessage(),
                            android.widget.Toast.LENGTH_LONG
                    ).show();
                });
    }
}