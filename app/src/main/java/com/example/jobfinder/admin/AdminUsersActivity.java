package com.example.jobfinder.admin;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText etSearchUser;
    private Button btnFilterAll, btnFilterSeeker, btnFilterEmployer, btnFilterBlocked;

    private List<AdminUser> allUserList;
    private List<AdminUser> filteredUserList;
    private AdminUserAdapter adapter;
    private FirebaseFirestore db;

    private String currentRoleFilter = "all";
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_users);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvUsers = findViewById(R.id.rvUsers);
        tvUserCount = findViewById(R.id.tvUserCount);
        etSearchUser = findViewById(R.id.etSearchUser);

        btnFilterAll = findViewById(R.id.btnFilterAll);
        btnFilterSeeker = findViewById(R.id.btnFilterSeeker);
        btnFilterEmployer = findViewById(R.id.btnFilterEmployer);
        btnFilterBlocked = findViewById(R.id.btnFilterBlocked);

        db = FirebaseFirestore.getInstance();
        allUserList = new ArrayList<>();
        filteredUserList = new ArrayList<>();

        adapter = new AdminUserAdapter(filteredUserList);
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(adapter);

        // Lắng nghe tìm kiếm theo thời gian thực
        if (etSearchUser != null) {
            etSearchUser.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    currentSearchQuery = s.toString().trim().toLowerCase();
                    applyFilter();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        // Bắt sự kiện các nút lọc vai trò
        btnFilterAll.setOnClickListener(v -> setFilter("all"));
        btnFilterSeeker.setOnClickListener(v -> setFilter("seeker"));
        btnFilterEmployer.setOnClickListener(v -> setFilter("employer"));
        btnFilterBlocked.setOnClickListener(v -> setFilter("blocked"));

        loadUsers();
    }

    private void setFilter(String filter) {
        this.currentRoleFilter = filter;
        updateFilterButtonStyles();
        applyFilter();
    }

    private void updateFilterButtonStyles() {
        int activeColor = Color.parseColor("#1976D2");
        int inactiveColor = Color.parseColor("#757575");

        btnFilterAll.setBackgroundTintList(ColorStateList.valueOf("all".equals(currentRoleFilter) ? activeColor : inactiveColor));
        btnFilterSeeker.setBackgroundTintList(ColorStateList.valueOf("seeker".equals(currentRoleFilter) ? activeColor : inactiveColor));
        btnFilterEmployer.setBackgroundTintList(ColorStateList.valueOf("employer".equals(currentRoleFilter) ? activeColor : inactiveColor));
        btnFilterBlocked.setBackgroundTintList(ColorStateList.valueOf("blocked".equals(currentRoleFilter) ? activeColor : inactiveColor));
    }

    private void applyFilter() {
        filteredUserList.clear();

        for (AdminUser user : allUserList) {
            boolean matchesSearch = currentSearchQuery.isEmpty()
                    || user.getName().toLowerCase().contains(currentSearchQuery)
                    || user.getEmail().toLowerCase().contains(currentSearchQuery);

            boolean matchesRole = true;
            if ("seeker".equals(currentRoleFilter)) {
                matchesRole = "seeker".equalsIgnoreCase(user.getRole()) || "candidate".equalsIgnoreCase(user.getRole());
            } else if ("employer".equals(currentRoleFilter)) {
                matchesRole = "employer".equalsIgnoreCase(user.getRole()) || "recruiter".equalsIgnoreCase(user.getRole());
            } else if ("blocked".equals(currentRoleFilter)) {
                matchesRole = user.isBlocked();
            }

            if (matchesSearch && matchesRole) {
                filteredUserList.add(user);
            }
        }

        tvUserCount.setText("Hiển thị: " + filteredUserList.size() + " / " + allUserList.size() + " tài khoản");
        adapter.notifyDataSetChanged();
    }

    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(result -> {
                    allUserList.clear();

                    for (var document : result) {
                        String id = document.getId();

                        String name = document.getString("fullName");
                        if (name == null) {
                            name = document.getString("name");
                        }

                        String email = document.getString("email");
                        String role = document.getString("role");

                        Boolean blockedValue = document.getBoolean("blocked");
                        String statusValue = document.getString("status");

                        boolean blocked = (blockedValue != null && blockedValue)
                                || "banned".equalsIgnoreCase(statusValue);

                        if (name == null || name.trim().isEmpty()) {
                            name = "Người dùng (" + id.substring(0, Math.min(6, id.length())) + ")";
                        }

                        if (email == null) email = "Chưa có email";
                        if (role == null) role = "seeker";

                        allUserList.add(new AdminUser(id, name, email, role, blocked));
                    }

                    applyFilter();
                })
                .addOnFailureListener(e -> {
                    tvUserCount.setText("Lỗi tải tài khoản");
                    Toast.makeText(AdminUsersActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
