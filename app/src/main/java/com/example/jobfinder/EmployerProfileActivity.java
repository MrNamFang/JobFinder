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
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

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

        // Ưu tiên đọc từ collection "users" đã được gom gọn
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.getString("companyName") != null) {
                        etCompanyName.setText(doc.getString("companyName"));
                        etDescription.setText(doc.getString("companyDescription"));
                        etAddress.setText(doc.getString("companyAddress"));
                        String phone = doc.getString("phone");
                        if (phone != null) etPhone.setText(phone);
                    } else {
                        // Tương thích ngược: thử đọc từ collection "employers" cũ nếu có
                        db.collection("employers").document(uid).get()
                                .addOnSuccessListener(oldDoc -> {
                                    if (oldDoc.exists()) {
                                        etCompanyName.setText(oldDoc.getString("companyName"));
                                        etDescription.setText(oldDoc.getString("description"));
                                        etAddress.setText(oldDoc.getString("address"));
                                        etPhone.setText(oldDoc.getString("phone"));
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveProfile() {
        String companyName = etCompanyName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(companyName)) {
            Toast.makeText(this, "Vui lòng nhập tên công ty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (uid == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveProfile.setEnabled(false);

        // Lưu thông tin công ty trực tiếp vào document của tài khoản trong "users" (Gom gọn CSDL)
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("companyName", companyName);
        userUpdates.put("companyDescription", description);
        userUpdates.put("companyAddress", address);
        userUpdates.put("phone", phone);

        db.collection("users").document(uid)
                .set(userUpdates, SetOptions.merge())
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
