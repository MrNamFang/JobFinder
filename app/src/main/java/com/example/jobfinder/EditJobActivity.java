package com.example.jobfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Màn Sửa tin tuyển dụng.
 *
 * Nhận object "job" cũ từ ManageJobsActivity qua Intent, đổ dữ liệu có sẵn
 * vào các ô nhập liệu. Khi bấm "Cập nhật", KHÔNG tự lưu Firestore ở đây
 * (chưa có Firestore) mà "trả object job đã sửa" lại cho màn gọi nó, thông
 * qua setResult() + finish().
 */
public class EditJobActivity extends Activity {

    private EditText etTitle, etCompany, etLocation, etSalary, etDescription;
    private Button btnUpdate;

    private Job job; // job đang sửa, lấy từ Intent
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_job);

        db = FirebaseFirestore.getInstance();

        etTitle = findViewById(R.id.etEditJobTitle);
        etCompany = findViewById(R.id.etEditCompany);
        etLocation = findViewById(R.id.etEditLocation);
        etSalary = findViewById(R.id.etEditSalary);
        etDescription = findViewById(R.id.etEditDescription);
        btnUpdate = findViewById(R.id.btnUpdateJob);

        job = (Job) getIntent().getSerializableExtra("job");
        if (job == null) {
            Toast.makeText(this, "Không tìm thấy dữ liệu để sửa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Đổ dữ liệu cũ vào các ô -- đây là điểm khác biệt so với PostJobActivity
        // (PostJobActivity bắt đầu với ô trống, còn đây bắt đầu với dữ liệu có sẵn)
        etTitle.setText(job.getTitle());
        etCompany.setText(job.getCompany());
        etLocation.setText(job.getLocation());
        etSalary.setText(job.getSalary());
        etDescription.setText(job.getDescription());

        btnUpdate.setOnClickListener(v -> updateJob());
    }

    private void updateJob() {
        String title = etTitle.getText().toString().trim();
        String company = etCompany.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(company) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ Vị trí, Công ty, Địa điểm",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Cập nhật lại các field của object "job" bằng dữ liệu mới nhập
        job.setTitle(title);
        job.setCompany(company);
        job.setLocation(location);
        job.setSalary(etSalary.getText().toString().trim());
        job.setDescription(etDescription.getText().toString().trim());

        btnUpdate.setEnabled(false); // tránh bấm 2 lần trong lúc chờ Firestore phản hồi

        // set() ghi đè TOÀN BỘ document bằng object "job" hiện tại -- vì mình
        // đã đổ sẵn dữ liệu cũ vào object này lúc mở màn (constructor rỗng +
        // các setter), nên set() ở đây an toàn, không làm mất field nào.
        db.collection("jobs").document(job.getId())
                .set(job)
                .addOnSuccessListener(unused -> {
                    // "Trả kết quả" lại cho Activity đã mở màn này (ManageJobsActivity):
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedJob", job);
                    setResult(Activity.RESULT_OK, resultIntent);

                    Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnUpdate.setEnabled(true);
                    Toast.makeText(this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}