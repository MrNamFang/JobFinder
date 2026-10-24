package com.example.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Màn hình chính THẬT SỰ của Employer, thay thế cho EmployerDashboardHostActivity
 * (chỉ là hàng tạm để test) -- đây là điểm vào chính sau khi đăng nhập.
 *
 * Cách tổ chức: tab "Dashboard" hiển thị EmployerDashboardFragment ngay
 * trong màn này (vì đây vốn đã là Fragment). 3 tab còn lại
 * (ManageJobsActivity, ApplicantsListActivity, EmployerProfileActivity)
 * được viết dưới dạng Activity riêng từ đầu, nên bấm vào sẽ MỞ Activity
 * đó ra (startActivity), thay vì cố nhồi vào chung 1 Fragment -- cách này
 * tránh phải viết lại toàn bộ 3 màn đó thành Fragment.
 */
public class EmployerMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_main);

        bottomNav = findViewById(R.id.bottomNavEmployer);

        ensureSignedIn();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_dashboard) {
                showDashboardFragment();
                return true;
            } else if (id == R.id.nav_manage_jobs) {
                startActivity(new Intent(this, ManageJobsActivity.class));
                return true;
            } else if (id == R.id.nav_applicants) {
                startActivity(new Intent(this, ApplicantsListActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EmployerProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void showDashboardFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof EmployerDashboardFragment) {
            return; // đã đang hiện Dashboard rồi thì khỏi vẽ lại cho tốn công
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new EmployerDashboardFragment())
                .commit();
    }

    /**
     * TẠM THỜI: tự đăng nhập ẩn danh nếu chưa có ai đăng nhập, để test được
     * các chức năng cần currentUser trước khi ghép code LoginActivity thật
     * của ĐẠT. XÓA HÀM NÀY khi đã merge xong (mục 6 trong HANDOFF.md).
     */
    private void ensureSignedIn() {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            showDashboardFragment();
            return;
        }

        auth.signInAnonymously()
                .addOnSuccessListener(result -> {
                    Toast.makeText(this, "Đã đăng nhập ẩn danh để test", Toast.LENGTH_SHORT).show();
                    showDashboardFragment();
                })
                .addOnFailureListener(e -> Toast.makeText(this,
                        "Lỗi đăng nhập ẩn danh: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Mỗi khi quay lại màn này (ví dụ bấm Back từ ManageJobsActivity),
        // đưa Bottom Nav về đúng tab "Dashboard" -- vì 3 tab kia là Activity
        // riêng đã bị đóng lại, không còn "đang mở" nữa.
        if (bottomNav.getSelectedItemId() != R.id.nav_dashboard) {
            bottomNav.setSelectedItemId(R.id.nav_dashboard);
        }
    }
}