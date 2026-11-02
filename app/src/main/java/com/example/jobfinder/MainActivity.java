package com.example.jobfinder;

import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.jobfinder.fragment.HomeFragment;
import com.example.jobfinder.fragment.ProfileFragment;
import com.example.jobfinder.fragment.SavedFragment;
import com.example.jobfinder.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiem tra neu chua dang nhap thi chuyen ve man Login
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, com.example.jobfinder.activity.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định mở Home khi vào app
        if (savedInstanceState == null) {
            replaceFragment(new HomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                replaceFragment(new HomeFragment());
                return true;
            } else if (id == R.id.nav_search) {
                replaceFragment(new SearchFragment());
                return true;
            } else if (id == R.id.nav_saved) {
                replaceFragment(new SavedFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                replaceFragment(new ProfileFragment());
                return true;
            }

            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}
