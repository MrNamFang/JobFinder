package com.example.jobfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobfinder.model.Application;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Xem chi tiết hồ sơ 1 ứng viên + cập nhật trạng thái ứng tuyển.
 * Nhận object Application qua Intent, trả lại object đã cập nhật
 * cho ApplicantsListActivity qua setResult().
 */
public class ApplicantDetailActivity extends Activity {

    private Application application;
    private Spinner spinnerStatus;
    private Button btnUpdateStatus;
    private FirebaseFirestore db;

    private final String[] statusLabels = {"Chờ duyệt", "Đã xem", "Chấp nhận", "Từ chối"};
    private final String[] statusValues = {
            Application.STATUS_PENDING,
            Application.STATUS_REVIEWED,
            Application.STATUS_ACCEPTED,
            Application.STATUS_REJECTED
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_detail);

        db = FirebaseFirestore.getInstance();

        application = (Application) getIntent().getSerializableExtra("application");
        if (application == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu ứng viên", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tvDetailName);
        TextView tvJobTitle = findViewById(R.id.tvDetailJobTitle);
        TextView tvEmail = findViewById(R.id.tvDetailEmail);
        TextView tvPhone = findViewById(R.id.tvDetailPhone);
        TextView tvAppliedDate = findViewById(R.id.tvDetailAppliedDate);
        TextView tvCoverLetter = findViewById(R.id.tvDetailCoverLetter);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        tvName.setText(application.getFullName());
        tvJobTitle.setText("Ứng tuyển: " + application.getJobTitle());
        tvEmail.setText("📧 " + application.getEmail());
        tvPhone.setText("📱 " + application.getPhone());

        // Hồ sơ CV do ứng viên điền trong Profile
        tvCoverLetter.setText(
                "Học vấn: " + valueOrDash(application.getEducation()) + "\n\n"
                        + "Kinh nghiệm: " + valueOrDash(application.getExperience()) + "\n\n"
                        + "Kỹ năng: " + valueOrDash(application.getSkills()));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        tvAppliedDate.setText("Nộp lúc: " + sdf.format(new Date(application.getAppliedDate())));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, statusLabels);
        spinnerStatus.setAdapter(adapter);

        // Đặt Spinner về đúng trạng thái hiện tại của ứng viên này khi mở màn
        int currentIndex = indexOfStatus(Application.normalize(application.getStatus()));
        spinnerStatus.setSelection(currentIndex);

        btnUpdateStatus.setOnClickListener(v -> updateStatus());
    }

    private String valueOrDash(String s) {
        return (s == null || s.trim().isEmpty()) ? "—" : s;
    }

    private int indexOfStatus(String statusValue) {
        for (int i = 0; i < statusValues.length; i++) {
            if (statusValues[i].equals(statusValue)) return i;
        }
        return 0; // mặc định "Pending" nếu không khớp giá trị nào
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
