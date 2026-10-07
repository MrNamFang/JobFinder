package com.example.jobfinder;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    public interface OnApplicantClickListener {
        void onApplicantClick(JobApplication application);
    }

    private final List<JobApplication> applicantList;
    private final OnApplicantClickListener listener;

    public ApplicantAdapter(List<JobApplication> applicantList, OnApplicantClickListener listener) {
        this.applicantList = applicantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ApplicantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_applicant, parent, false);
        return new ApplicantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicantViewHolder holder, int position) {
        JobApplication app = applicantList.get(position);

        holder.tvName.setText(app.getFullName());
        holder.tvJobTitle.setText("Ứng tuyển: " + app.getJobTitle());
        holder.tvEmail.setText(app.getEmail());
        holder.tvStatus.setText(statusLabel(app.getStatus()));

        // Tạo 1 "chip" bo tròn với màu nền thay đổi tùy theo trạng thái --
        // GradientDrawable ở đây không phải để tạo gradient (dù tên vậy),
        // mà dùng để vẽ 1 hình chữ nhật bo góc với màu nền tùy chỉnh, thay
        // vì phải tạo sẵn nhiều file .xml drawable riêng cho từng màu.
        GradientDrawable chipBackground = new GradientDrawable();
        chipBackground.setCornerRadius(20f);
        chipBackground.setColor(statusColor(app.getStatus()));
        holder.tvStatus.setBackground(chipBackground);

        holder.itemView.setOnClickListener(v -> listener.onApplicantClick(app));
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    public void updateApplicant(JobApplication updated) {
        for (int i = 0; i < applicantList.size(); i++) {
            if (applicantList.get(i).getId().equals(updated.getId())) {
                applicantList.set(i, updated);
                notifyItemChanged(i);
                break;
            }
        }
    }

    private String statusLabel(String status) {
        if (status == null) return "Pending";
        switch (status) {
            case "Reviewed": return "Đã xem";
            case "Accepted": return "Chấp nhận";
            case "Rejected": return "Từ chối";
            default: return "Chờ duyệt";
        }
    }

    private int statusColor(String status) {
        if (status == null) return Color.parseColor("#F57C00"); // cam - Pending
        switch (status) {
            case "Reviewed": return Color.parseColor("#1565C0");  // xanh dương
            case "Accepted": return Color.parseColor("#2E7D32");  // xanh lá
            case "Rejected": return Color.parseColor("#C62828");  // đỏ
            default: return Color.parseColor("#F57C00");          // cam
        }
    }

    static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvJobTitle, tvEmail, tvStatus;

        ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvApplicantName);
            tvJobTitle = itemView.findViewById(R.id.tvApplicantJobTitle);
            tvEmail = itemView.findViewById(R.id.tvApplicantEmail);
            tvStatus = itemView.findViewById(R.id.tvApplicantStatus);
        }
    }
}