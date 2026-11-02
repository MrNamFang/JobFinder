package com.example.jobfinder.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobfinder.R;
import com.example.jobfinder.activity.LoginActivity;
import com.example.jobfinder.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.jobfinder.activity.MyApplicationsActivity;
import com.example.jobfinder.activity.NotificationsActivity;
import com.example.jobfinder.activity.FeedbackActivity;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileEmail, tvProfilePhone;
    private EditText etEducation, etExperience, etSkills;
    private Button btnSaveCv, btnLogout, btnMyApplications, btnNotifications, btnFeedback;    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUid;

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

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail);
        tvProfilePhone = view.findViewById(R.id.tvProfilePhone);
        etEducation = view.findViewById(R.id.etEducation);
        etExperience = view.findViewById(R.id.etExperience);
        etSkills = view.findViewById(R.id.etSkills);
        btnSaveCv = view.findViewById(R.id.btnSaveCv);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnMyApplications = view.findViewById(R.id.btnMyApplications);
        btnMyApplications = view.findViewById(R.id.btnMyApplications);
        btnNotifications = view.findViewById(R.id.btnNotifications);
        btnFeedback = view.findViewById(R.id.btnFeedback);
        loadUserProfile();

        btnSaveCv.setOnClickListener(v -> saveCvInfo());
        btnMyApplications.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MyApplicationsActivity.class)));
        btnMyApplications.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), MyApplicationsActivity.class)));

        btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationsActivity.class)));
        btnFeedback.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), FeedbackActivity.class)));
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.putExtra("logout", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
        });
    }

    private void loadUserProfile() {
        if (currentUid == null) return;

        db.collection("users").document(currentUid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        User user = documentSnapshot.toObject(User.class);
                        if (user != null) {
                            tvProfileName.setText("Họ tên: " + user.getFullName());
                            tvProfileEmail.setText("Email: " + user.getEmail());
                            tvProfilePhone.setText("SĐT: " + user.getPhone());

                            etEducation.setText(user.getEducation());
                            etExperience.setText(user.getExperience());
                            etSkills.setText(user.getSkills());
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
        User newUser = new User(currentUid, "Chưa cập nhật", email, "Chưa cập nhật", "seeker");

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
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(), "Đã lưu hồ sơ CV!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi lưu hồ sơ: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
