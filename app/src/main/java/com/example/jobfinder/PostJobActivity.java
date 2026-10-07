package com.example.jobfinder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Màn hình Employer đăng tin tuyển dụng mới.
 * Lưu 1 document mới vào Firestore collection "jobs", tự gắn employerId
 * và postedDate để các màn khác (Home/Search của job seeker, Dashboard
 * của employer) dùng để lọc/sắp xếp.
 */
public class PostJobActivity extends AppCompatActivity {

    private EditText etJobTitle, etCompany, etLocation, etSalary, etDescription;
    private Spinner spinnerJobType;
    private Button btnSubmitJob;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private final String[] jobTypes = {"Full-time", "Part-time", "Internship", "Remote"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        etJobTitle = findViewById(R.id.etJobTitle);
        etCompany = findViewById(R.id.etCompany);
        etLocation = findViewById(R.id.etLocation);
        etSalary = findViewById(R.id.etSalary);
        etDescription = findViewById(R.id.etDescription);
        spinnerJobType = findViewById(R.id.spinnerJobType);
        btnSubmitJob = findViewById(R.id.btnSubmitJob);
        progressBar = findViewById(R.id.progressBarPostJob);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Đổ dữ liệu cho Spinner chọn loại hình công việc
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, jobTypes);
        spinnerJobType.setAdapter(adapter);

        btnSubmitJob.setOnClickListener(v -> submitJob());
    }

    private void submitJob() {
        String title = etJobTitle.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String salary = etSalary.getText().toString().trim();
        String jobType = spinnerJobType.getSelectedItem() != null
                ? spinnerJobType.getSelectedItem().toString() : "Full-time";
        String description = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Vui lòng điền Vị trí, Công ty, Địa điểm", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để đăng tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Job job = new Job(title, company, location, salary, jobType, description,
                "", System.currentTimeMillis());
        job.setEmployerId(currentUser.getUid());

        setLoading(true);

        db.collection("jobs")
                .add(job)
                .addOnSuccessListener(documentReference -> {
                    setLoading(false);
                    Toast.makeText(PostJobActivity.this, "Đăng tin thành công!", Toast.LENGTH_SHORT).show();
                    finish(); // quay lại Dashboard, onResume() ở đó sẽ tự cập nhật số liệu
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(PostJobActivity.this, "Lỗi đăng tin: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmitJob.setEnabled(!loading);
    }
}