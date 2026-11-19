package com.example.jobfinder.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.adapter.FeedbackAdapter;
import com.example.jobfinder.model.Feedback;
import com.example.jobfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private EditText etFeedbackContent;
    private Button btnSubmitFeedback;
    private RecyclerView recyclerViewFeedback;
    private TextView tvEmptyFeedback;

    private FeedbackAdapter feedbackAdapter;
    private List<Feedback> feedbackList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String currentUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etFeedbackContent = findViewById(R.id.etFeedbackContent);
        etFeedbackContent = findViewById(R.id.etFeedbackContent);
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback);
        recyclerViewFeedback = findViewById(R.id.recyclerViewFeedback);
        tvEmptyFeedback = findViewById(R.id.tvEmptyFeedback);

        recyclerViewFeedback.setLayoutManager(new LinearLayoutManager(this));
        feedbackAdapter = new FeedbackAdapter(feedbackList);
        recyclerViewFeedback.setAdapter(feedbackAdapter);

        loadMyFeedbacks();

        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String content = etFeedbackContent.getText().toString().trim();

        if (TextUtils.isEmpty(content)) {
            etFeedbackContent.setError("Vui lòng nhập nội dung");
            return;
        }

        if (currentUid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitFeedback.setEnabled(false);

        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    String userName = user != null ? user.getFullName() : "";
                    String userEmail = user != null ? user.getEmail() : "";

                    Feedback feedback = new Feedback(
                            currentUid, userName, userEmail, content,
                            "Chưa xử lý", "", System.currentTimeMillis()
                    );

                    db.collection("feedbacks")
                            .add(feedback)
                            .addOnSuccessListener(documentReference -> {
                                btnSubmitFeedback.setEnabled(true);
                                etFeedbackContent.setText("");
                                Toast.makeText(this, "Đã gửi phản hồi thành công!", Toast.LENGTH_SHORT).show();
                                loadMyFeedbacks();
                            })
                            .addOnFailureListener(e -> {
                                btnSubmitFeedback.setEnabled(true);
                                Toast.makeText(this, "Lỗi gửi phản hồi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnSubmitFeedback.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadMyFeedbacks() {
        if (currentUid == null) return;

        db.collection("feedbacks")
                .whereEqualTo("userId", currentUid)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    feedbackList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Feedback fb = doc.toObject(Feedback.class);
                        fb.setId(doc.getId());
                        feedbackList.add(fb);
                    }
                    feedbackAdapter.updateData(feedbackList);
                    tvEmptyFeedback.setVisibility(feedbackList.isEmpty() ? View.VISIBLE : View.GONE);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi tải danh sách: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}