package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.jobfinder.R;
import com.example.jobfinder.activity.FeedbackActivity;
import com.example.jobfinder.activity.LoginActivity;
import com.example.jobfinder.activity.MyApplicationsActivity;
import com.example.jobfinder.activity.NotificationsActivity;
import com.example.jobfinder.model.User;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private static final String NOT_UPDATED = "Chưa cập nhật";

    private TextView tvAvatar, tvProfileName, tvProfileEmail, tvProfilePhone;
    private TextView tvProgressPercent, tvProgressHint;
    private LinearProgressIndicator progressProfile;
    private EditText etEducation, etExperience, etSkills;
    private Button btnSaveCv, btnLogout, btnMyApplications, btnNotifications, btnFeedback;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUid;
    private String currentName = "", currentPhone = "";

    public ProfileFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUid = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        tvAvatar = view.findViewById(R.id.tvAvatar);
        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        tvProgressPercent = view.findViewById(R.id.tvProgressPercent);
        tvProgressHint = view.findViewById(R.id.tvProgressHint);
        progressProfile = view.findViewById(R.id.progressProfile);
        etEducation = view.findViewById(R.id.etEducation);
        etExperience = view.findViewById(R.id.etExperience);
        etSkills = view.findViewById(R.id.etSkills);
        btnSaveCv = view.findViewById(R.id.btnSaveCv);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnMyApplications = view.findViewById(R.id.btnMyApplications);
        btnNotifications = view.findViewById(R.id.btnNotifications);
        btnFeedback = view.findViewById(R.id.btnFeedback);

        btnSaveCv.setOnClickListener(v -> saveCvInfo());
        btnMyApplications.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MyApplicationsActivity.class)));
        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationsActivity.class)));
        btnFeedback.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FeedbackActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        loadUserProfile();
    }

    private void loadUserProfile() {
        if (currentUid == null) return;

        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            currentName = safe(user.getFullName());
                            currentPhone = safe(user.getPhone());

                            tvProfileName.setText(currentName.isEmpty() ? NOT_UPDATED : currentName);
                            tvProfileEmail.setText(safe(user.getEmail()));
                            tvProfilePhone.setText(currentPhone.isEmpty() ? NOT_UPDATED : currentPhone);
                            tvAvatar.setText(currentName.isEmpty() || NOT_UPDATED.equals(currentName)
                                    ? "?" : currentName.substring(0, 1).toUpperCase());

                            etEducation.setText(user.getEducation());
                            etExperience.setText(user.getExperience());
                            etSkills.setText(user.getSkills());

                            updateProgress();
                        }
                    } else {
                        createMissingUserDoc();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void createMissingUserDoc() {
        String email = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getEmail() : "";
        User newUser = new User(currentUid, NOT_UPDATED, email, NOT_UPDATED, "seeker");

        db.collection("users").document(currentUid)
                .set(newUser)
                .addOnSuccessListener(unused -> loadUserProfile())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi khởi tạo hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveCvInfo() {
        if (currentUid == null) return;

        String education = etEducation.getText().toString().trim();
        String experience = etExperience.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();

        db.collection("users").document(currentUid)
                .update("education", education, "experience", experience, "skills", skills)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Đã lưu hồ sơ CV!", Toast.LENGTH_SHORT).show();
                    updateProgress();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi lưu hồ sơ: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    // 5 mục, mỗi mục 20%: tên, SĐT, học vấn, kinh nghiệm, kỹ năng
    private void updateProgress() {
        int filled = 0;
        if (isFilled(currentName)) filled++;
        if (isFilled(currentPhone)) filled++;
        if (isFilled(etEducation.getText().toString())) filled++;
        if (isFilled(etExperience.getText().toString())) filled++;
        if (isFilled(etSkills.getText().toString())) filled++;

        int percent = filled * 20;
        progressProfile.setProgressCompat(percent, true);
        tvProgressPercent.setText(percent + "%");
        tvProgressHint.setText(percent >= 100
                ? "Hồ sơ đã hoàn chỉnh, sẵn sàng ứng tuyển!"
                : "Điền đủ học vấn, kinh nghiệm và kỹ năng để nhà tuyển dụng chú ý hơn.");
    }

    private boolean isFilled(String s) {
        return s != null && !s.trim().isEmpty() && !NOT_UPDATED.equals(s.trim());
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}