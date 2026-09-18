package com.example.jobfinder.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jobfinder.R;
import com.example.jobfinder.model.Application;
import com.example.jobfinder.model.Job;
import com.example.jobfinder.model.SavedJob;
import com.example.jobfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class JobDetailActivity extends AppCompatActivity {

    public static final String EXTRA_JOB = "extra_job";

    private TextView tvDetailTitle, tvDetailCompany, tvDetailSalary,
            tvDetailLocation, tvDetailJobType, tvDetailDescription, tvDetailRequirements;
    private Button btnSaveJob, btnApply;

    private Job currentJob;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUid;

    private String existingSavedJobDocId = null; // null = chưa lưu, có giá trị = đã lưu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        tvDetailTitle = findViewById(R.id.tvDetailTitle);
        tvDetailCompany = findViewById(R.id.tvDetailCompany);
        tvDetailSalary = findViewById(R.id.tvDetailSalary);
        tvDetailLocation = findViewById(R.id.tvDetailLocation);
        tvDetailJobType = findViewById(R.id.tvDetailJobType);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);
        tvDetailRequirements = findViewById(R.id.tvDetailRequirements);
        btnSaveJob = findViewById(R.id.btnSaveJob);
        btnApply = findViewById(R.id.btnApply);

        currentJob = (Job) getIntent().getSerializableExtra(EXTRA_JOB);

        if (currentJob != null) {
            displayJobInfo(currentJob);
            checkIfJobSaved();
        } else {
            Toast.makeText(this, "Không tìm thấy dữ liệu công việc", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnSaveJob.setOnClickListener(v -> toggleSaveJob());

        btnApply.setOnClickListener(v -> confirmApply());
    }

    private void displayJobInfo(Job job) {
        tvDetailTitle.setText(job.getTitle());
        tvDetailCompany.setText(job.getCompanyName());
        tvDetailSalary.setText(job.getSalary());
        tvDetailLocation.setText(job.getLocation());
        tvDetailJobType.setText(job.getJobType());
        tvDetailDescription.setText(job.getDescription());
        tvDetailRequirements.setText(job.getRequirements());
    }

    // ============ PHẦN LƯU TIN (SAVED) ============

    private void checkIfJobSaved() {
        if (currentUid == null) return;

        db.collection("savedJobs")
                .whereEqualTo("userId", currentUid)
                .whereEqualTo("jobId", currentJob.getId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        existingSavedJobDocId = doc.getId();
                        btnSaveJob.setText("Đã lưu ✓");
                    } else {
                        existingSavedJobDocId = null;
                        btnSaveJob.setText("Lưu tin");
                    }
                });
    }

    private void toggleSaveJob() {
        if (currentUid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSaveJob.setEnabled(false);

        if (existingSavedJobDocId != null) {
            // Đã lưu rồi -> bỏ lưu
            db.collection("savedJobs").document(existingSavedJobDocId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        btnSaveJob.setEnabled(true);
                        existingSavedJobDocId = null;
                        btnSaveJob.setText("Lưu tin");
                        Toast.makeText(this, "Đã bỏ lưu tin", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        btnSaveJob.setEnabled(true);
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Chưa lưu -> thêm mới
            SavedJob savedJob = new SavedJob(
                    currentUid,
                    currentJob.getId(),
                    currentJob.getTitle(),
                    currentJob.getCompanyName(),
                    currentJob.getLocation(),
                    currentJob.getSalary(),
                    currentJob.getJobType(),
                    System.currentTimeMillis()
            );

            db.collection("savedJobs")
                    .add(savedJob)
                    .addOnSuccessListener(documentReference -> {
                        btnSaveJob.setEnabled(true);
                        existingSavedJobDocId = documentReference.getId();
                        btnSaveJob.setText("Đã lưu ✓");
                        Toast.makeText(this, "Đã lưu tin thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        btnSaveJob.setEnabled(true);
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    // ============ PHẦN ỨNG TUYỂN (APPLY) ============

    private void confirmApply() {
        if (currentUid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận ứng tuyển")
                .setMessage("Bạn có chắc muốn ứng tuyển vào vị trí \"" + currentJob.getTitle() + "\" không?\n\nHồ sơ CV (Học vấn, Kinh nghiệm, Kỹ năng) trong mục Profile sẽ được gửi kèm.")
                .setPositiveButton("Ứng tuyển", (dialog, which) -> checkAlreadyAppliedThenApply())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void checkAlreadyAppliedThenApply() {
        db.collection("applications")
                .whereEqualTo("userId", currentUid)
                .whereEqualTo("jobId", currentJob.getId())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        Toast.makeText(this, "Bạn đã ứng tuyển công việc này rồi", Toast.LENGTH_SHORT).show();
                    } else {
                        submitApplication();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi kiểm tra: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void submitApplication() {
        btnApply.setEnabled(false);

        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user == null) {
                        btnApply.setEnabled(true);
                        Toast.makeText(this, "Không tìm thấy thông tin hồ sơ", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Application application = new Application(
                            currentJob.getId(),
                            currentJob.getTitle(),
                            currentJob.getCompanyName(),
                            currentUid,
                            user.getFullName(),
                            user.getEmail(),
                            user.getPhone(),
                            user.getEducation(),
                            user.getExperience(),
                            user.getSkills(),
                            "Đang chờ",
                            System.currentTimeMillis()
                    );

                    db.collection("applications")
                            .add(application)
                            .addOnSuccessListener(documentReference -> {
                                btnApply.setEnabled(true);
                                Toast.makeText(this, "Ứng tuyển thành công!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                btnApply.setEnabled(true);
                                Toast.makeText(this, "Lỗi ứng tuyển: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnApply.setEnabled(true);
                    Toast.makeText(this, "Lỗi tải hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}