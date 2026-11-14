package com.example.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.activity.FeedbackActivity;
import com.example.jobfinder.activity.LoginActivity;
import com.example.jobfinder.activity.NotificationsActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployerAccountActivity extends AppCompatActivity {

    private TextView tvAccountAvatar, tvAccountName, tvAccountEmail;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_account);

        BottomNavigationView nav = findViewById(R.id.bottomNavEmployer);
        EmployerNav.setup(this, nav, R.id.nav_profile);

        tvAccountAvatar = findViewById(R.id.tvAccountAvatar);
        tvAccountName = findViewById(R.id.tvAccountName);
        tvAccountEmail = findViewById(R.id.tvAccountEmail);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        uid = user != null ? user.getUid() : null;
        tvAccountEmail.setText(user != null && user.getEmail() != null ? user.getEmail() : "");

        findViewById(R.id.rowCompany).setOnClickListener(v ->
                startActivity(new Intent(this, EmployerProfileActivity.class)));
        findViewById(R.id.rowNotifications).setOnClickListener(v ->
                startActivity(new Intent(this, NotificationsActivity.class)));
        findViewById(R.id.rowFeedback).setOnClickListener(v ->
                startActivity(new Intent(this, FeedbackActivity.class)));
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCompanyName();
    }

    private void loadCompanyName() {
        if (uid == null) return;

        db.collection("employers").document(uid).get()
                .addOnSuccessListener(doc -> {
                    String name = doc.exists() ? doc.getString("companyName") : null;
                    if (name != null && !name.trim().isEmpty()) {
                        tvAccountName.setText(name.trim());
                        tvAccountAvatar.setText(name.trim().substring(0, 1).toUpperCase());
                    }
                });
    }

    private void logout() {
        auth.signOut();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}