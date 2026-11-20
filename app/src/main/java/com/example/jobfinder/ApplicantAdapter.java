package com.example.jobfinder;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.model.Application;

import java.util.List;

public class ApplicantAdapter extends RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder> {

    public interface OnApplicantClickListener {
        void onApplicantClick(Application application);
    }

    private final List<Application> applicantList;
    private final OnApplicantClickListener listener;

    public ApplicantAdapter(List<Application> applicantList, OnApplicantClickListener listener) {
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
        Application app = applicantList.get(position);

        // Xen kẽ màu thẻ: xanh ngọc, cam, trắng (giống Trang chủ)
        int[] cardBgs = {R.drawable.bg_card_mint, R.drawable.bg_card_peach, R.drawable.bg_card};
        holder.itemView.setBackgroundResource(cardBgs[position % 3]);

        String name = app.getFullName();
        holder.tvName.setText(name);
        holder.tvInitial.setText(name == null || name.trim().isEmpty()
                ? "?" : name.trim().substring(0, 1).toUpperCase());
        holder.tvJobTitle.setText("Ứng tuyển: " + app.getJobTitle());
        holder.tvEmail.setText(app.getEmail());

        String status = Application.normalize(app.getStatus());
        holder.tvStatus.setText(statusLabel(status));
        holder.tvStatus.setTextColor(statusTextColor(status));

        // Chip bo tròn, màu nền đổi theo trạng thái
        GradientDrawable chip = new GradientDrawable();
        chip.setCornerRadius(12f * holder.itemView.getResources().getDisplayMetrics().density);
        chip.setColor(statusBgColor(status));
        holder.tvStatus.setBackground(chip);

        holder.itemView.setOnClickListener(v -> listener.onApplicantClick(app));
    }

    @Override
    public int getItemCount() {
        return applicantList.size();
    }

    public void updateApplicant(Application updated) {
        for (int i = 0; i < applicantList.size(); i++) {
            if (applicantList.get(i).getId() != null && applicantList.get(i).getId().equals(updated.getId())) {
                applicantList.set(i, updated);
                notifyItemChanged(i);
                break;
            }
        }
    }

    private String statusLabel(String status) {
        switch (status) {
            case Application.STATUS_REVIEWED: return "Đã xem";
            case Application.STATUS_ACCEPTED: return "Chấp nhận";
            case Application.STATUS_REJECTED: return "Từ chối";
            default: return "Chờ duyệt";
        }
    }

    private int statusTextColor(String status) {
        switch (status) {
            case Application.STATUS_REVIEWED: return 0xFF1E3A8A; // xanh dương đậm
            case Application.STATUS_ACCEPTED: return 0xFF065F46; // xanh lá đậm
            case Application.STATUS_REJECTED: return 0xFF991B1B; // đỏ đậm
            default: return 0xFF9A3412; // cam đậm
        }
    }

    private int statusBgColor(String status) {
        switch (status) {
            case Application.STATUS_REVIEWED: return 0xFFDBEAFE; // nền xanh dương nhạt
            case Application.STATUS_ACCEPTED: return 0xFFD1FAE5; // nền xanh lá nhạt
            case Application.STATUS_REJECTED: return 0xFFFEE2E2; // nền đỏ nhạt
            default: return 0xFFFFEDD5; // nền cam nhạt
        }
    }

    public static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvJobTitle, tvEmail, tvStatus;

        public ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitial = itemView.findViewById(R.id.tvApplicantInitial);
            tvName = itemView.findViewById(R.id.tvApplicantName);
            tvJobTitle = itemView.findViewById(R.id.tvApplicantJobTitle);
            tvEmail = itemView.findViewById(R.id.tvApplicantEmail);
            tvStatus = itemView.findViewById(R.id.tvApplicantStatus);
        }
    }
}
