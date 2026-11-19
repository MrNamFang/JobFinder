package com.example.jobfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Tab "Dashboard" của Employer: hiển thị 3 con số tổng quan bằng Aggregation Query
 * (count() -- Firestore chỉ trả về SỐ LƯỢNG document khớp điều kiện, không tải nội
 * dung về máy, nên rất nhẹ dù có hàng ngàn document).
 */
public class EmployerDashboardFragment extends Fragment {

    private TextView tvGreeting, tvCompanyName, tvTotalJobs, tvTotalApplicants, tvPendingApplicants;
    private Button btnPostNewJob;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private String employerId;

    // Đăng ký theo dõi realtime collection "applications" -- lưu lại reference
    // này để có thể "hủy đăng ký" (remove()) khi Fragment không còn hiển thị,
    // tránh rò rỉ bộ nhớ (memory leak) và code chạy ngầm không cần thiết.
    private ListenerRegistration applicationsListener;

    // true = lần đầu tiên listener chạy (đây là dữ liệu CŨ đã có sẵn, không
    // phải "mới nộp"), false = từ lần thứ 2 trở đi mới là thay đổi thật sự.
    private boolean isFirstLoad = true;

    // Launcher xin quyền hiện thông báo (chỉ thật sự cần trên Android 13+,
    // nhưng gọi launcher này trên bản Android cũ hơn cũng không lỗi gì).
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // registerForActivityResult PHẢI được gọi sớm (onCreate/onAttach),
        // không được gọi trong onClickListener hay onViewCreated muộn hơn --
        // quy tắc giống hệt bài học ActivityResultLauncher ở EditJobActivity.
        notificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    // Không cần làm gì đặc biệt ở đây -- dù user đồng ý hay từ chối,
                    // listener Firestore vẫn hoạt động bình thường, chỉ là thông báo
                    // sẽ không hiện lên nếu user từ chối quyền.
                    if (!isGranted) {
                        Toast.makeText(getContext(),
                                "Bạn đã tắt quyền thông báo, sẽ không nhận được cảnh báo ứng viên mới",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_employer_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvCompanyName = view.findViewById(R.id.tvCompanyName);
        tvTotalJobs = view.findViewById(R.id.tvTotalJobs);
        tvTotalApplicants = view.findViewById(R.id.tvTotalApplicants);
        tvPendingApplicants = view.findViewById(R.id.tvPendingApplicants);
        btnPostNewJob = view.findViewById(R.id.btnPostNewJob);
        progressBar = view.findViewById(R.id.progressBarDashboard);

        db = FirebaseFirestore.getInstance();
        employerId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        // TODO: thay bằng tên công ty thật lấy từ collection "users"/"employers" ở bước làm Profile sau
        tvCompanyName.setText(FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getEmail()
                : "");

        btnPostNewJob.setOnClickListener(v -> {
            // TODO: PostJobActivity sẽ làm ở bước tiếp theo
            startActivity(new Intent(getContext(), PostJobActivity.class));
        });

        // Tạo channel (an toàn khi gọi nhiều lần) và xin quyền hiện thông báo
        NotificationHelper.createNotificationChannel(requireContext());
        checkAndRequestNotificationPermission();

        loadDashboardStats();
        listenForNewApplicants();
    }

    /**
     * Từ Android 13 (API 33) trở lên, phải xin quyền POST_NOTIFICATIONS.
     * Trên bản Android cũ hơn, quyền này tự động được cấp, không cần hỏi.
     */
    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    /**
     * Gắn Realtime Listener vào collection "applications" của employer này.
     * Mỗi khi có document mới (ADDED), hiện 1 thông báo local.
     */
    private void listenForNewApplicants() {
        if (employerId == null) return;

        isFirstLoad = true;

        applicationsListener = db.collection("applications")
                .whereEqualTo("employerId", employerId)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    if (isFirstLoad) {
                        // Lần gọi đầu tiên trả về dữ liệu CŨ đã có sẵn -- bỏ qua,
                        // không hiện thông báo cho những đơn đã nộp từ trước.
                        isFirstLoad = false;
                        return;
                    }

                    for (DocumentChange change : snapshots.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            String applicantName = change.getDocument().getString("fullName");
                            String jobTitle = change.getDocument().getString("jobTitle");

                            NotificationHelper.showNotification(
                                    requireContext(),
                                    "Có ứng viên mới!",
                                    applicantName + " vừa ứng tuyển vị trí " + jobTitle,
                                    (int) System.currentTimeMillis() // id ngẫu nhiên để mỗi thông báo tách biệt
                            );

                            // Có ứng viên mới thì số liệu Dashboard cũng đổi theo -- tải lại luôn
                            loadDashboardStats();
                        }
                    }
                });
    }

    private void loadDashboardStats() {
        if (employerId == null) {
            Toast.makeText(getContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        // 1) Đếm tổng số tin employer này đã đăng
        db.collection("jobs")
                .whereEqualTo("employerId", employerId)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot ->
                        tvTotalJobs.setText(String.valueOf(snapshot.getCount())))
                .addOnFailureListener(e -> showError(e));

        // 2) Đếm tổng số ứng viên đã nộp vào các tin của employer này
        db.collection("applications")
                .whereEqualTo("employerId", employerId)
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot ->
                        tvTotalApplicants.setText(String.valueOf(snapshot.getCount())))
                .addOnFailureListener(e -> showError(e));

        // 3) Đếm số ứng viên đang ở trạng thái "Pending" (chưa duyệt)
        db.collection("applications")
                .whereEqualTo("employerId", employerId)
                .whereEqualTo("status", "Pending")
                .count()
                .get(AggregateSource.SERVER)
                .addOnSuccessListener(snapshot -> {
                    tvPendingApplicants.setText(String.valueOf(snapshot.getCount()));
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> showError(e));
    }

    private void showError(Exception e) {
        progressBar.setVisibility(View.GONE);
        if (getContext() != null) {
            Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại số liệu mỗi khi quay lại Dashboard (vd sau khi đăng tin mới)
        loadDashboardStats();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Hủy đăng ký lắng nghe khi màn hình này không còn hiển thị -- nếu quên
        // bước này, listener vẫn tiếp tục chạy ngầm mãi mãi dù Fragment đã đóng,
        // gây tốn tài nguyên và có thể crash khi cố truy cập View đã bị hủy.
        if (applicationsListener != null) {
            applicationsListener.remove();
        }
    }
}