package com.example.jobfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageJobsActivity extends AppCompatActivity implements JobManageAdapter.OnJobActionListener {

    private RecyclerView rvManageJobs;
    private TextView tvEmpty;
    private Button btnAddNewJob;
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

        db = FirebaseFirestore.getInstance();
        employerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        rvManageJobs = findViewById(R.id.rvManageJobs);
        tvEmpty = findViewById(R.id.tvEmptyManageJobs);
        btnAddNewJob = findViewById(R.id.btnAddNewJob);
        progressBar = findViewById(R.id.progressBarManageJobs);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavEmployer);
        EmployerNav.setup(this, bottomNav, R.id.nav_manage_jobs);

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
        rvManageJobs.setLayoutManager(new LinearLayoutManager(this));
        rvManageJobs.setAdapter(adapter);

        btnAddNewJob.setOnClickListener(v ->
                startActivity(new Intent(this, PostJobActivity.class)));
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

        Job job = new Job(
                text(doc, "title"),
                text(doc, "company"),
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
        boolean isCurrentlyOpen = job.getStatus() == null || job.getStatus().equals("Open");
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