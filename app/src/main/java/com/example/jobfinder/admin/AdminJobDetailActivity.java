package com.example.jobfinder.admin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;

import com.example.jobfinder.R;

public class AdminJobDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvCompany;
    private TextView tvLocation;
    private TextView tvSalary;
    private TextView tvJobType;
    private TextView tvRequirements;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_job_detail);

        Button btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            finish();
        });

        tvTitle = findViewById(R.id.tvDetailTitle);
        tvCompany = findViewById(R.id.tvDetailCompany);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvSalary = findViewById(R.id.tvDetailSalary);
        tvJobType = findViewById(R.id.tvDetailJobType);
        tvRequirements = findViewById(R.id.tvDetailRequirements);
        tvDescription = findViewById(R.id.tvDetailDescription);

        String title =
                getIntent().getStringExtra("title");

        String company =
                getIntent().getStringExtra("company");

        String location =
                getIntent().getStringExtra("location");

        String salary =
                getIntent().getStringExtra("salary");

        String jobType =
                getIntent().getStringExtra("jobType");

        String requirements =
                getIntent().getStringExtra("requirements");

        String description =
                getIntent().getStringExtra("description");

        tvTitle.setText(title);
        tvCompany.setText("Công ty: " + company);
        tvLocation.setText("Địa điểm: " + location);
        tvSalary.setText("Mức lương: " + salary);
        tvJobType.setText("Loại công việc: " + jobType);
        tvRequirements.setText(requirements);
        tvDescription.setText(description);
    }
}