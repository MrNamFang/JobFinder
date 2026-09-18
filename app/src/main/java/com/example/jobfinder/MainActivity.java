package com.example.jobfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.admin.AdminDashboardActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mở Admin Dashboard
        Intent intent = new Intent(
                MainActivity.this,
                AdminDashboardActivity.class
        );

        startActivity(intent);

        // Đóng MainActivity
        finish();
    }
}