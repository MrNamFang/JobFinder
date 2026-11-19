package com.example.jobfinder;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity TẠM THỜI chỉ để "chứa" EmployerDashboardFragment và test được.
 *
 * Fragment không thể tự chạy độc lập như Activity -- luôn cần 1 Activity
 * "host" nó. Sau này khi làm bước nối mạng (gộp Dashboard/ManageJobs/
 * ApplicantsList/Profile vào chung 1 Bottom Navigation), Activity này sẽ
 * được thay bằng EmployerMainActivity thật sự có đầy đủ 4 tab.
 */
public class EmployerDashboardHostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_dashboard_host);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new EmployerDashboardFragment())
                    .commit();
        }
    }
}