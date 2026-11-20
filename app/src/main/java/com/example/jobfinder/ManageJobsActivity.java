package com.example.jobfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.model.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Màn Quản lý tin tuyển dụng của Employer.
 */
public class ManageJobsActivity extends AppCompatActivity
        implements JobManageAdapter.OnJobActionListener {

    private RecyclerView rvJobs;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private JobManageAdapter adapter;
    private final List<Job> jobList = new ArrayList<>();

    private FirebaseFirestore db;
    private String employerId;

    private ActivityResultLauncher<Intent> editJobLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        EmployerNav.setup(this, findViewById(R.id.bottomNavEmployer), R.id.nav_jobs);

        rvJobs = findViewById(R.id.rvManageJobs);
        tvEmpty = findViewById(R.id.tvEmptyJobs);
        progressBar = findViewById(R.id.progressBarManageJobs);

        db = FirebaseFirestore.getInstance();
        employerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        editJobLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Job updatedJob = (Job) result.getData().getSerializableExtra("updatedJob");
                        if (updatedJob != null) {
                            adapter.updateJob(updatedJob);
                        }
                    }
                });

        adapter = new JobManageAdapter(jobList, this);
        rvJobs.setLayoutManager(new LinearLayoutManager(this));
        rvJobs.setAdapter(adapter);

        findViewById(R.id.fabAddJob).setOnClickListener(v ->
                startActivity(new Intent(this, PostJobActivity.class)));

        loadJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadJobs();
    }

    private void loadJobs() {
        if (employerId == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("jobs")
                .whereEqualTo("employerId", employerId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    jobList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        jobList.add(toJob(doc));
                    }
                    Collections.sort(jobList, (a, b) -> Long.compare(b.getPostedDate(), a.getPostedDate()));

                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(jobList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tải tin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private Job toJob(DocumentSnapshot doc) {
        Long posted = doc.getLong("postedDate");

        String company = text(doc, "companyName");
        if (company.isEmpty()) {
            company = text(doc, "company");
        }

        Job job = new Job(
                text(doc, "title"),
                company,
                text(doc, "location"),
                text(doc, "salary"),
                text(doc, "jobType"),
                text(doc, "description"),
                text(doc, "requirements"),
                posted != null ? posted : 0L);

        job.setId(doc.getId());
        job.setEmployerId(text(doc, "employerId"));

        String status = doc.getString("status");
        if (status != null) {
            job.setStatus(status);
        }
        String adminStatus = doc.getString("adminStatus");
        if (adminStatus != null) {
            job.setAdminStatus(adminStatus);
        }
        job.setRejectReason(doc.getString("rejectReason"));
        return job;
    }

    private String text(DocumentSnapshot doc, String field) {
        String value = doc.getString(field);
        return value != null ? value : "";
    }

    @Override
    public void onEditClick(Job job) {
        Intent intent = new Intent(this, EditJobActivity.class);
        intent.putExtra("job", job);
        editJobLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(Job job) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa tin tuyển dụng")
                .setMessage("Bạn có chắc muốn xóa tin \"" + job.getTitle() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) ->
                        db.collection("jobs").document(job.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    adapter.removeJob(job);
                                    tvEmpty.setVisibility(jobList.isEmpty() ? View.VISIBLE : View.GONE);
                                    Toast.makeText(this, "Đã xóa tin", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(this, "Lỗi xóa tin: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onToggleStatusClick(Job job) {
        boolean isCurrentlyOpen = job.isOpen();
        String newStatus = isCurrentlyOpen ? "Closed" : "Open";

        db.collection("jobs").document(job.getId())
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    job.setStatus(newStatus);
                    adapter.updateJob(job);
                    Toast.makeText(this,
                            newStatus.equals("Closed") ? "Đã đóng tin tuyển dụng" : "Đã mở lại tin tuyển dụng",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
