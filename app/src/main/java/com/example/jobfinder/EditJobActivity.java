package com.example.jobfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jobfinder.model.Job;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Màn Sửa tin tuyển dụng của Employer.
 */
public class EditJobActivity extends Activity {

    private EditText etTitle, etCompany, etLocation, etSalary, etDescription;
    private Button btnUpdate;

    private Job job; // job đang sửa, lấy từ Intent
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        db = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etEditJobTitle);
        etCompany = findViewById(R.id.etEditCompany);
        etLocation = findViewById(R.id.etEditLocation);
        etSalary = findViewById(R.id.etEditSalary);
        etDescription = findViewById(R.id.etEditDescription);
        btnUpdate = findViewById(R.id.btnUpdateJob);

        job = (Job) getIntent().getSerializableExtra("job");
        if (job == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu để sửa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etTitle.setText(job.getTitle());
        etCompany.setText(job.getCompanyName());
        etLocation.setText(job.getLocation());
        etSalary.setText(job.getSalary());
        etDescription.setText(job.getDescription());

        btnUpdate.setOnClickListener(v -> updateJob());
    }

    private void updateJob() {
        String title = etTitle.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ Vị trí, Công ty, Địa điểm",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        job.setTitle(title);
        job.setCompanyName(company);
        job.setCompany(company);
        job.setLocation(location);
        job.setSalary(etSalary.getText().toString().trim());
        job.setDescription(etDescription.getText().toString().trim());

        btnUpdate.setEnabled(false);

        db.collection("jobs").document(job.getId())
                .set(job)
                .addOnSuccessListener(unused -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedJob", job);
                    setResult(Activity.RESULT_OK, resultIntent);

                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnUpdate.setEnabled(true);
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
