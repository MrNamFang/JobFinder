package com.example.jobfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Màn "Quản lý tin tuyển dụng" của Employer: xem danh sách, Sửa, Xóa.
 *
 * QUAN TRỌNG: hiện tại đang dùng MOCK DATA (dữ liệu giả tự tạo trong code,
 * hàm createMockJobs() bên dưới) thay vì lấy từ Firestore, vì project chưa
 * setup xong Firebase. Khi có Firebase rồi, chỉ cần thay nội dung hàm
 * loadJobs() để gọi Firestore thay vì gọi createMockJobs() -- toàn bộ phần
 * UI, Adapter, xử lý Sửa/Xóa phía dưới GIỮ NGUYÊN không cần đổi gì.
 */
public class ManageJobsActivity extends AppCompatActivity implements JobManageAdapter.OnJobActionListener {

    private RecyclerView rvManageJobs;
    private TextView tvEmpty;
    private Button btnAddNewJob;

    private JobManageAdapter adapter;
    private final List<Job> jobList = new ArrayList<>();

    // "Launcher" chuyên dùng để mở 1 Activity VÀ nhận kết quả trả về từ nó.
    // Đây là cách làm hiện đại, thay thế cho startActivityForResult() kiểu cũ.
    private ActivityResultLauncher<Intent> editJobLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

// Gắn thanh menu dưới, tab "Tin tuyển dụng" đang được chọn
        EmployerNav.setup(this, findViewById(R.id.bottomNavEmployer), R.id.nav_manage_jobs);

        rvManageJobs = findViewById(R.id.rvManageJobs);
        tvEmpty = findViewById(R.id.tvEmptyManageJobs);
        btnAddNewJob = findViewById(R.id.btnAddNewJob);

        // Đăng ký "launcher": định nghĩa TRƯỚC những gì sẽ làm khi EditJobActivity
        // đóng lại và trả kết quả về. Phải đăng ký TRƯỚC khi Activity ở trạng thái
        // STARTED (an toàn nhất là đặt ngay trong onCreate, không đặt trong hàm
        // xử lý sự kiện như onClickListener).
        editJobLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Đoạn code trong đây CHỈ chạy khi EditJobActivity đã đóng và
                    // gọi setResult() + finish() -- không chạy nếu user bấm Back
                    // giữa chừng (resultCode sẽ không phải RESULT_OK).
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Job updatedJob = (Job) result.getData().getSerializableExtra("updatedJob");
                        if (updatedJob != null) {
                            adapter.updateJob(updatedJob);
                        }
                    }
                });

        // "this" ở đây hợp lệ vì class ManageJobsActivity có "implements OnJobActionListener"
        // ở dòng khai báo class phía trên -- nghĩa là chính Activity này đứng ra nhận
        // sự kiện Sửa/Xóa từ Adapter (xem 2 hàm onEditClick/onDeleteClick bên dưới).
        adapter = new JobManageAdapter(jobList, this);
        rvManageJobs.setLayoutManager(new LinearLayoutManager(this));
        rvManageJobs.setAdapter(adapter);

        btnAddNewJob.setOnClickListener(v ->
                startActivity(new Intent(this, PostJobActivity.class)));

        loadJobs();
    }

    private void loadJobs() {
        jobList.clear();
        jobList.addAll(createMockJobs());
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(jobList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /**
     * Dữ liệu giả tạm thời để test UI. Xóa hàm này khi nối Firestore thật.
     */
    private List<Job> createMockJobs() {
        List<Job> mock = new ArrayList<>();

        Job job1 = new Job("Android Developer", "Tech Company Ltd.", "Quận 1, TP.HCM",
                "15-20 triệu", "Full-time", "Phát triển ứng dụng Android bằng Java/Kotlin...",
                "", System.currentTimeMillis());
        job1.setId("mock_1");

        Job job2 = new Job("Backend Developer", "Tech Company Ltd.", "Quận 3, TP.HCM",
                "18-25 triệu", "Full-time", "Xây dựng API, quản lý database...",
                "", System.currentTimeMillis());
        job2.setId("mock_2");

        mock.add(job1);
        mock.add(job2);
        return mock;
    }

    // ----- Triển khai (implement) 2 hàm bắt buộc của interface OnJobActionListener -----

    @Override
    public void onEditClick(Job job) {
        Intent intent = new Intent(this, EditJobActivity.class);
        intent.putExtra("job", job);
        editJobLauncher.launch(intent);
    }

    @Override
    public void onDeleteClick(Job job) {
        // AlertDialog: hộp thoại xác nhận -- tránh xóa nhầm khi lỡ tay bấm nút Xóa.
        new AlertDialog.Builder(this)
                .setTitle("Xóa tin tuyển dụng")
                .setMessage("Bạn có chắc muốn xóa tin \"" + job.getTitle() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // TODO: khi có Firestore, gọi thêm:
                    // db.collection("jobs").document(job.getId()).delete();
                    adapter.removeJob(job);
                    tvEmpty.setVisibility(jobList.isEmpty() ? View.VISIBLE : View.GONE);
                    Toast.makeText(this, "Đã xóa tin", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onToggleStatusClick(Job job) {
        boolean isCurrentlyOpen = job.getStatus() == null || job.getStatus().equals("Open");
        String newStatus = isCurrentlyOpen ? "Closed" : "Open";

        // TODO: khi có Firestore, gọi thêm:
        // db.collection("jobs").document(job.getId()).update("status", newStatus);

        job.setStatus(newStatus);
        adapter.updateJob(job);

        Toast.makeText(this,
                newStatus.equals("Closed") ? "Đã đóng tin tuyển dụng" : "Đã mở lại tin tuyển dụng",
                Toast.LENGTH_SHORT).show();
    }
}