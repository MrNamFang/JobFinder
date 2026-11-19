package com.example.jobfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobfinder.activity.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class EmployerMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bottomNav = findViewById(R.id.bottomNavEmployer);
        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> insets);

        EmployerNav.setup(this, bottomNav, R.id.nav_dashboard);

        if (savedInstanceState == null) {
            showDashboardFragment();
        }
    }

    private void showDashboardFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, new EmployerDashboardFragment())
                .commit();
    }
}