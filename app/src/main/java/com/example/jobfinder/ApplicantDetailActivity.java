package com.example.jobfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Xem chi tiết hồ sơ 1 ứng viên + cập nhật trạng thái ứng tuyển.
 * Nhận object JobApplication qua Intent, trả lại object đã cập nhật
 * cho ApplicantsListActivity qua setResult().
 */
public class ApplicantDetailActivity extends Activity {

    private JobApplication application;
    private Spinner spinnerStatus;
    private Button btnUpdateStatus;
    private FirebaseFirestore db;

    private final String[] statusLabels = {"Chờ duyệt", "Đã xem", "Chấp nhận", "Từ chối"};
    private final String[] statusValues = {"Pending", "Reviewed", "Accepted", "Rejected"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_detail);

        // Nút quay lại góc trên bên trái
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        db = FirebaseFirestore.getInstance();

        application = (JobApplication) getIntent().getSerializableExtra("application");
        if (application == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu ứng viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvAvatar = findViewById(R.id.tvAvatar);
        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvJobTitle = findViewById(R.id.tvDetailJobTitle);
        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvPhone = findViewById(R.id.tvDetailPhone);
        TextView tvAppliedDate = findViewById(R.id.tvDetailAppliedDate);

        TextView tvEducation = findViewById(R.id.tvDetailEducation);
        TextView tvExperience = findViewById(R.id.tvDetailExperience);
        TextView tvSkills = findViewById(R.id.tvDetailSkills);
        View layoutCoverLetter = findViewById(R.id.layoutCoverLetter);
        TextView tvCoverLetter = findViewById(R.id.tvDetailCoverLetter);

        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        // Đổ dữ liệu thông tin cá nhân
        String fullName = valueOrDash(application.getFullName());
        tvName.setText(fullName);
        tvAvatar.setText(fullName.length() > 0 && !fullName.equals("—")
                ? fullName.substring(0, 1).toUpperCase() : "U");

        tvJobTitle.setText("Ứng tuyển: " + valueOrDash(application.getJobTitle()));
        tvEmail.setText(valueOrDash(application.getEmail()));
        tvPhone.setText(valueOrDash(application.getPhone()));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        tvAppliedDate.setText("Nộp lúc: " + sdf.format(new Date(application.getAppliedDate())));

        // Đổ dữ liệu hồ sơ năng lực (Học vấn, Kinh nghiệm, Kỹ năng)
        tvEducation.setText(valueOrDash(application.getEducation()));
        tvExperience.setText(valueOrDash(application.getExperience()));
        tvSkills.setText(valueOrDash(application.getSkills()));

        // Thư giới thiệu (hiện nếu ứng viên có điền)
        String cover = application.getCoverLetter();
        if (cover != null && !cover.trim().isEmpty()) {
            layoutCoverLetter.setVisibility(View.VISIBLE);
            tvCoverLetter.setText(fixVietnameseEncoding(cover.trim()));
        } else {
            layoutCoverLetter.setVisibility(View.GONE);
        }

        // Cấu hình Spinner trạng thái
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, statusLabels);
        spinnerStatus.setAdapter(adapter);

        int currentIndex = indexOfStatus(application.getStatus());
        spinnerStatus.setSelection(currentIndex);

        btnUpdateStatus.setOnClickListener(v -> updateStatus());
    }

    private String valueOrDash(String s) {
        if (s == null || s.trim().isEmpty()) return "Chưa cập nhật";
        return fixVietnameseEncoding(s.trim());
    }

    /**
     * Sửa các ký tự '?' bị lỗi mã hóa tiếng Việt khi lưu từ trước
     */
    private String fixVietnameseEncoding(String text) {
        if (text == null) return "";
        return text.replace("D?i h?c Th? D?u M?t", "Đại học Thủ Dầu Một")
                .replace("1 nam l?p tr nh Android", "1 năm lập trình Android")
                .replace("l?p tr nh", "lập trình")
                .replace("D?i h?c", "Đại học")
                .replace("Th? D?u M?t", "Thủ Dầu Một");
    }

    private int indexOfStatus(String statusValue) {
        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i].equals(statusValue)) return i;
        }
        return 0;
    }

    private void updateStatus() {
        int selectedIndex = spinnerStatus.getSelectedItemPosition();
        String newStatus = statusValues[selectedIndex];

        application.setStatus(newStatus);
        btnUpdateStatus.setEnabled(false);

        db.collection("applications").document(application.getId())
                .update("status", newStatus)
                .addOnSuccessListener(unused -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedApplication", application);
                    setResult(Activity.RESULT_OK, resultIntent);

                    Toast.makeText(this, "Đã cập nhật trạng thái: " + statusLabels[selectedIndex],
                            Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnUpdateStatus.setEnabled(true);
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}