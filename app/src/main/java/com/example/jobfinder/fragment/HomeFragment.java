package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.jobfinder.activity.NotificationsActivity;
import com.example.jobfinder.adapter.JobAdapter;
import com.example.jobfinder.model.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewJobs;
    private JobAdapter jobAdapter;
    private TextView tvGreeting;
    private final List<Job> jobList = new ArrayList<>();
    private FirebaseFirestore db;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        tvGreeting = view.findViewById(R.id.tvGreeting);

        recyclerViewJobs = view.findViewById(R.id.recyclerViewJobs);
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(getContext()));
        jobAdapter = new JobAdapter(jobList, job -> {
            Intent intent = new Intent(getActivity(), JobDetailActivity.class);
            intent.putExtra(JobDetailActivity.EXTRA_JOB, job);
            startActivity(intent);
        });
        recyclerViewJobs.setAdapter(jobAdapter);

        view.findViewById(R.id.btnNotifications).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationsActivity.class)));

        loadGreeting();
        loadJobsFromFirestore();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (db != null) {
            loadJobsFromFirestore();
        }
    }

    private void loadGreeting() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    String fullName = doc.getString("fullName");
                    if (fullName != null && !fullName.trim().isEmpty() && tvGreeting != null) {
                        String[] parts = fullName.trim().split("\\s+");
                        tvGreeting.setText("Chào " + parts[parts.length - 1] + ",");
                    }
                });
    }

    private void loadJobsFromFirestore() {
        db.collection("jobs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    jobList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Job job = doc.toObject(Job.class);
                        job.setId(doc.getId());
                        if (!job.isClosed()) {
                            jobList.add(job);
                        }
                    }
                    Collections.sort(jobList, (a, b) -> Long.compare(b.getPostedDate(), a.getPostedDate()));
                    jobAdapter.updateData(jobList);

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(getContext(), "Chưa có việc làm nào, đang thêm dữ liệu mẫu...", Toast.LENGTH_SHORT).show();
                        addSampleJobs();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeFragment", "Lỗi load jobs: " + e.getMessage());
                    Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void addSampleJobs() {
        List<Job> sampleJobs = new ArrayList<>();
        sampleJobs.add(new Job(null, "Lập trình viên Android", "Công ty ABC Tech",
                "TP. Hồ Chí Minh", "15-20 triệu", "Full-time",
                "Phát triển ứng dụng Android bằng Java/Kotlin", "Có kinh nghiệm 1 năm trở lên",
                System.currentTimeMillis()));

        sampleJobs.add(new Job(null, "Nhân viên Content Marketing", "Công ty XYZ Media",
                "Hà Nội", "8-12 triệu", "Full-time",
                "Viết bài, lên kế hoạch nội dung mạng xã hội", "Kỹ năng viết tốt, sáng tạo",
                System.currentTimeMillis()));

        sampleJobs.add(new Job(null, "Thực tập sinh Kế toán", "Công ty DEF Solutions",
                "Đà Nẵng", "3-5 triệu", "Part-time",
                "Hỗ trợ nhập liệu, đối chiếu sổ sách", "Sinh viên năm 3-4 ngành Kế toán",
                System.currentTimeMillis()));

        sampleJobs.add(new Job(null, "Nhân viên Sales", "Công ty GHI Group",
                "TP. Hồ Chí Minh", "10-15 triệu + hoa hồng", "Full-time",
                "Tư vấn và chăm sóc khách hàng", "Giao tiếp tốt, chịu được áp lực",
                System.currentTimeMillis()));

        for (Job job : sampleJobs) {
            db.collection("jobs").add(job)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("HomeFragment", "Đã thêm job: " + job.getTitle());
                        loadJobsFromFirestore();
                    })
                    .addOnFailureListener(e ->
                            Log.e("HomeFragment", "Lỗi thêm job mẫu: " + e.getMessage()));
        }
    }
}