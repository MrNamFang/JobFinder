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
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn "Quản lý ứng viên" của Employer: xem danh sách ứng viên đã nộp đơn
 * vào các tin của mình. Bấm vào 1 ứng viên sẽ mở ApplicantDetailActivity
 * để xem hồ sơ đầy đủ và cập nhật trạng thái.
 *
 * ĐÃ NỐI FIRESTORE THẬT. Query collection "applications", lọc theo
 * employerId == uid người đang đăng nhập.
 */
public class ApplicantsListActivity extends AppCompatActivity
        implements ApplicantAdapter.OnApplicantClickListener {

    private RecyclerView rvApplicants;
    private TextView tvEmpty;
    private ProgressBar progressBar;
    private ApplicantAdapter adapter;
    private final List<JobApplication> applicantList = new ArrayList<>();

    private FirebaseFirestore db;
    private String employerId;

    private ActivityResultLauncher<Intent> applicantDetailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants_list);

        rvApplicants = findViewById(R.id.rvApplicants);
        tvEmpty = findViewById(R.id.tvEmptyApplicants);
        // Nếu layout của bạn chưa có id progressBarApplicants, không sao,
        // findViewById() sẽ trả về null và code phía dưới có kiểm tra null.
        progressBar = findViewById(R.id.progressBarApplicants);

        db = FirebaseFirestore.getInstance();
        employerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        applicantDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        JobApplication updated =
                                (JobApplication) result.getData().getSerializableExtra("updatedApplication");
                        if (updated != null) {
                            adapter.updateApplicant(updated);
                        }
                    }
                });

        adapter = new ApplicantAdapter(applicantList, this);
        rvApplicants.setLayoutManager(new LinearLayoutManager(this));
        rvApplicants.setAdapter(adapter);

        loadApplicants();
    }

    private void loadApplicants() {
        if (employerId == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        db.collection("applications")
                .whereEqualTo("employerId", employerId)
                .orderBy("appliedDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    applicantList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        JobApplication app = doc.toObject(JobApplication.class);
                        app.setId(doc.getId());
                        applicantList.add(app);
                    }
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(applicantList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi tải danh sách: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onApplicantClick(JobApplication application) {
        Intent intent = new Intent(this, ApplicantDetailActivity.class);
        intent.putExtra("application", application);
        applicantDetailLauncher.launch(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplicants();
    }
}