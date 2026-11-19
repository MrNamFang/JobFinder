package com.example.jobfinder;

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

        // Xen kẽ màu thẻ: xanh ngọc, cam, trắng (giống Trang chủ)
        int[] cardBgs = {R.drawable.bg_card_mint, R.drawable.bg_card_peach, R.drawable.bg_card};
        holder.itemView.setBackgroundResource(cardBgs[position % 3]);

        String name = app.getFullName();
        holder.tvName.setText(name);
        holder.tvInitial.setText(name == null || name.trim().isEmpty()
                ? "?" : name.trim().substring(0, 1).toUpperCase());
        holder.tvJobTitle.setText("Ứng tuyển: " + app.getJobTitle());
        holder.tvEmail.setText(app.getEmail());

        String status = normalize(app.getStatus());
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

    public void updateApplicant(JobApplication updated) {
        for (int i = 0; i < applicantList.size(); i++) {
            if (applicantList.get(i).getId().equals(updated.getId())) {
                applicantList.set(i, updated);
                notifyItemChanged(i);
                break;
            }
        }
    }

    // Đưa mọi giá trị (kể cả tiếng Việt lưu cũ) về 4 trạng thái chuẩn
    private String normalize(String status) {
        if (status == null) return "Pending";
        switch (status) {
            case "Reviewed":
            case "Đã xem":
                return "Reviewed";
            case "Accepted":
            case "Được duyệt":
            case "Chấp nhận":
                return "Accepted";
            case "Rejected":
            case "Từ chối":
                return "Rejected";
            default:
                return "Pending";
        }
    }

    private String statusLabel(String status) {
        switch (status) {
            case "Reviewed": return "Đã xem";
            case "Accepted": return "Chấp nhận";
            case "Rejected": return "Từ chối";
            default: return "Chờ duyệt";
        }
    }

    private int statusBgColor(String status) {
        switch (status) {
            case "Reviewed": return 0xFFFFFFFF; // trắng, nổi trên nền thẻ màu
            case "Accepted": return 0xFFCDEFD6; // xanh lá nhạt
            case "Rejected": return 0xFFFFDAD6; // đỏ nhạt
            default: return 0xFFFFFFFF;         // trắng
        }
    }

    private int statusTextColor(String status) {
        switch (status) {
            case "Reviewed": return 0xFF0F766E;
            case "Accepted": return 0xFF166534;
            case "Rejected": return 0xFFC62828;
            default: return 0xFF9A4A00;
        }
    }

    static class ApplicantViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvJobTitle, tvEmail, tvStatus, tvInitial;

        ApplicantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvApplicantName);
            tvJobTitle = itemView.findViewById(R.id.tvApplicantJobTitle);
            tvEmail = itemView.findViewById(R.id.tvApplicantEmail);
            tvStatus = itemView.findViewById(R.id.tvApplicantStatus);
            tvInitial = itemView.findViewById(R.id.tvApplicantInitial);
        }
    }
}