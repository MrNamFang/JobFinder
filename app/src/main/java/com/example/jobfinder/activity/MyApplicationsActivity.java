package com.example.jobfinder.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.adapter.ApplicationAdapter;
import com.example.jobfinder.model.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewApplications;
    private TextView tvEmptyApplications;
    private ApplicationAdapter applicationAdapter;
    private List<Application> applicationList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        recyclerViewApplications = findViewById(R.id.recyclerViewApplications);
        tvEmptyApplications = findViewById(R.id.tvEmptyApplications);
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(this));

        applicationAdapter = new ApplicationAdapter(applicationList, this::showCancelDialog);
        recyclerViewApplications.setAdapter(applicationAdapter);

        loadApplications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load lại để thấy trạng thái mới nếu nhà tuyển dụng vừa duyệt/từ chối
        loadApplications();
    }

    private void loadApplications() {
        if (currentUid == null) return;

        db.collection("applications")
                .whereEqualTo("userId", currentUid)
                .orderBy("appliedDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    applicationList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Application app = doc.toObject(Application.class);
                        app.setId(doc.getId());
                        applicationList.add(app);
                    }
                    applicationAdapter.updateData(applicationList);
                    tvEmptyApplications.setVisibility(applicationList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void showCancelDialog(Application application) {
        // Chỉ cho phép hủy khi đang ở trạng thái "Đang chờ" (Pending)
        if (!Application.STATUS_PENDING.equals(Application.normalize(application.getStatus()))) {
            Toast.makeText(this, "Chỉ có thể hủy đơn đang ở trạng thái \"Đang chờ\"", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Hủy ứng tuyển")
                .setMessage("Bạn có chắc muốn hủy đơn ứng tuyển vào \"" + application.getJobTitle() + "\" không?")
                .setPositiveButton("Hủy đơn", (dialog, which) -> cancelApplication(application))
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelApplication(Application application) {
        db.collection("applications").document(application.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã hủy đơn ứng tuyển", Toast.LENGTH_SHORT).show();
                    loadApplications(); // load lại danh sách
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi hủy đơn: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}