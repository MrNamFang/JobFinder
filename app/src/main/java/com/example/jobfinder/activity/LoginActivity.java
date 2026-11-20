package com.example.jobfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.EmployerMainActivity;
import com.example.jobfinder.MainActivity;
import com.example.jobfinder.R;
import com.example.jobfinder.admin.AdminDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister, tvForgotPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        btnLogin.setOnClickListener(v -> loginUser());

        tvForgotPassword.setOnClickListener(v -> resetPassword());

        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Nếu đã đăng nhập từ trước, kiểm tra role rồi vào đúng màn hình
        if (mAuth.getCurrentUser() != null) {
            goToMain();
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    goToMain();
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Đăng nhập thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // Gửi email đặt lại mật khẩu qua Firebase
    private void resetPassword() {
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Nhập email hợp lệ để nhận link đặt lại mật khẩu");
            etEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        Toast.makeText(this,
                                "Đã gửi link đặt lại mật khẩu tới " + email + ". Kiểm tra cả mục Spam nhé.",
                                Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không gửi được: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // Đọc role và trạng thái tài khoản trong Firestore rồi chuyển đến màn hình tương ứng
    private void goToMain() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String status = doc.getString("status");
                        Boolean blocked = doc.getBoolean("blocked");
                        if ("banned".equalsIgnoreCase(status) || (blocked != null && blocked)) {
                            mAuth.signOut();
                            btnLogin.setEnabled(true);
                            new androidx.appcompat.app.AlertDialog.Builder(this)
                                    .setTitle("Tài khoản bị khóa")
                                    .setMessage("Tài khoản của bạn đã bị khóa bởi Quản trị viên. Vui lòng liên hệ hỗ trợ.")
                                    .setPositiveButton("Đóng", null)
                                    .show();
                            return;
                        }
                    }

                    String role = doc.getString("role");
                    Intent intent;

                    if ("admin".equals(role)) {
                        intent = new Intent(this, AdminDashboardActivity.class);
                    } else if ("employer".equals(role)) {
                        intent = new Intent(this, EmployerMainActivity.class);
                    } else {
                        intent = new Intent(this, MainActivity.class);
                    }

                    startActivity(intent);
                    finish(); // bấm back không quay lại màn login
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this, "Không lấy được thông tin tài khoản: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}