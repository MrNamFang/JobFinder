package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jobfinder.R;
import com.example.jobfinder.activity.JobDetailActivity;
import com.example.jobfinder.adapter.JobAdapter;
import com.example.jobfinder.model.Job;
import com.example.jobfinder.model.SavedJob;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {

    private RecyclerView recyclerViewSaved;
    private TextView tvEmptySaved;
    private JobAdapter jobAdapter;
    private List<Job> savedJobsAsJobList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUid;

    public SavedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        recyclerViewSaved = view.findViewById(R.id.recyclerViewSaved);
        tvEmptySaved = view.findViewById(R.id.tvEmptySaved);
        recyclerViewSaved.setLayoutManager(new LinearLayoutManager(getContext()));

        jobAdapter = new JobAdapter(savedJobsAsJobList, job -> {
            Intent intent = new Intent(getActivity(), JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
            startActivity(intent);
        });
        recyclerViewSaved.setAdapter(jobAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load lại mỗi khi quay lại tab Saved (để cập nhật nếu vừa lưu/bỏ lưu ở màn Detail)
        loadSavedJobs();
    }

    private void loadSavedJobs() {
        if (currentUid == null) return;

        db.collection("savedJobs")
                .whereEqualTo("userId", currentUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    savedJobsAsJobList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        SavedJob savedJob = doc.toObject(SavedJob.class);

                        // Convert SavedJob -> Job để dùng chung JobAdapter
                        Job job = new Job();
                        job.setId(savedJob.getJobId());
                        job.setTitle(savedJob.getJobTitle());
                        job.setCompanyName(savedJob.getCompanyName());
                        job.setLocation(savedJob.getLocation());
                        job.setSalary(savedJob.getSalary());
                        job.setJobType(savedJob.getJobType());

                        savedJobsAsJobList.add(job);
                    }

                    jobAdapter.updateData(savedJobsAsJobList);
                    tvEmptySaved.setVisibility(savedJobsAsJobList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải danh sách đã lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}