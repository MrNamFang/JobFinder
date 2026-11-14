package com.example.jobfinder;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployerProfileActivity extends AppCompatActivity {

    private EditText etCompanyName, etDescription, etAddress, etPhone;
    private TextView tvEmailReadonly;
    private Button btnSaveProfile;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etCompanyName = findViewById(R.id.etCompanyName);
        etDescription = findViewById(R.id.etDescription);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        tvEmailReadonly = findViewById(R.id.tvEmailReadonly);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        uid = currentUser != null ? currentUser.getUid() : null;
        userEmail = currentUser != null ? currentUser.getEmail() : "";
        tvEmailReadonly.setText(userEmail);

        loadProfile();

        btnSaveProfile.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        if (uid == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("employers").document(uid).get()
                .addOnSuccessListener(this::handleProfileLoaded)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void handleProfileLoaded(DocumentSnapshot doc) {
        if (doc.exists()) {
            etCompanyName.setText(doc.getString("companyName"));
            etDescription.setText(doc.getString("description"));
            etAddress.setText(doc.getString("address"));
            etPhone.setText(doc.getString("phone"));
        }
    }

    private void saveProfile() {
        String companyName = etCompanyName.getText().toString().trim();

        if (TextUtils.isEmpty(companyName)) {
            Toast.makeText(this, "Vui lòng nhập tên công ty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uid == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        EmployerProfile profile = new EmployerProfile(
                uid,
                companyName,
                etDescription.getText().toString().trim(),
                etAddress.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                userEmail
        );

        btnSaveProfile.setEnabled(false);

        db.collection("employers").document(uid)
                .set(profile)
                .addOnSuccessListener(unused -> {
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, "Đã lưu thông tin công ty!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnSaveProfile.setEnabled(true);
                    Toast.makeText(this, "Lỗi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}