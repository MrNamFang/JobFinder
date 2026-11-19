package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.activity.JobDetailActivity;
import com.example.jobfinder.adapter.JobAdapter;
import com.example.jobfinder.model.Job;
import com.example.jobfinder.model.SavedJob;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SavedFragment extends Fragment {

    private RecyclerView recyclerViewSaved;
    private TextView tvEmptySaved, tvSavedCount;
    private JobAdapter jobAdapter;
    private final List<Job> savedJobsAsJobList = new ArrayList<>();

    private FirebaseFirestore db;
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
        currentUid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        recyclerViewSaved = view.findViewById(R.id.recyclerViewSaved);
        tvEmptySaved = view.findViewById(R.id.tvEmptySaved);
        tvSavedCount = view.findViewById(R.id.tvSavedCount);
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
        loadSavedJobs();
    }

    private void loadSavedJobs() {
        if (currentUid == null) return;

        db.collection("savedJobs")
                .whereEqualTo("userId", currentUid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded()) return;

                    List<String> jobIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        SavedJob savedJob = doc.toObject(SavedJob.class);
                        if (savedJob.getJobId() != null) {
                            jobIds.add(savedJob.getJobId());
                        }
                    }

                    if (jobIds.isEmpty()) {
                        showJobs(new ArrayList<>());
                        return;
                    }

                    fetchJobs(jobIds);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Lỗi tải danh sách đã lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchJobs(List<String> jobIds) {
        List<Job> result = new ArrayList<>();
        int[] remaining = {jobIds.size()};

        for (String jobId : jobIds) {
            db.collection("jobs").document(jobId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();
                            if (doc != null && doc.exists()) {
                                Job job = doc.toObject(Job.class);
                                if (job != null && !job.isClosed()) {
                                    job.setId(doc.getId());
                                    result.add(job);
                                }
                            }
                        }

                        remaining[0]--;
                        if (remaining[0] == 0 && isAdded()) {
                            Collections.sort(result, (a, b) -> Long.compare(b.getPostedDate(), a.getPostedDate()));
                            showJobs(result);
                        }
                    });
        }
    }

    private void showJobs(List<Job> jobs) {
        savedJobsAsJobList.clear();
        savedJobsAsJobList.addAll(jobs);
        jobAdapter.updateData(savedJobsAsJobList);

        tvEmptySaved.setVisibility(savedJobsAsJobList.isEmpty() ? View.VISIBLE : View.GONE);
        tvSavedCount.setText(savedJobsAsJobList.isEmpty()
                ? "Những tin bạn muốn xem lại sau"
                : savedJobsAsJobList.size() + " tin đã lưu");
    }
}