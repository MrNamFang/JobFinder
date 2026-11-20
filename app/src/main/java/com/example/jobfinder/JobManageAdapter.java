package com.example.jobfinder;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.model.Job;

import java.util.List;

/**
 * Adapter cho màn "Quản lý tin tuyển dụng" của Employer.
 */
public class JobManageAdapter extends RecyclerView.Adapter<JobManageAdapter.JobViewHolder> {

    public interface OnJobActionListener {
        void onEditClick(Job job);
        void onDeleteClick(Job job);
        void onToggleStatusClick(Job job);
    }

    private final List<Job> jobList;
    private final OnJobActionListener listener;

    public JobManageAdapter(List<Job> jobList, OnJobActionListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_manage, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        // Xen kẽ màu thẻ: xanh ngọc, cam, trắng (giống Trang chủ)
        int[] cardBgs = {R.drawable.bg_card_mint, R.drawable.bg_card_peach, R.drawable.bg_card};
        holder.itemView.setBackgroundResource(cardBgs[position % 3]);

        holder.tvTitle.setText(job.getTitle());
        holder.tvCompany.setText(job.getCompanyName());
        holder.tvLocation.setText(job.getLocation());

        boolean isOpen = job.isOpen();

        // Chip trạng thái: chữ + màu nền đổi theo Open/Closed
        holder.tvStatus.setText(isOpen ? "Đang tuyển" : "Đã đóng");
        holder.tvStatus.setTextColor(isOpen ? 0xFF0F766E : 0xFF7A5A48);

        GradientDrawable chip = new GradientDrawable();
        chip.setCornerRadius(12f * holder.itemView.getResources().getDisplayMetrics().density);
        chip.setColor(isOpen ? 0xFFFFFFFF : 0xFFEADFD6);
        holder.tvStatus.setBackground(chip);

        // Nút toggle
        holder.btnToggleStatus.setText(isOpen ? "Đóng tin" : "Mở lại");

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(job));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(job));
        holder.btnToggleStatus.setOnClickListener(v -> listener.onToggleStatusClick(job));
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void removeJob(Job job) {
        int index = jobList.indexOf(job);
        if (index != -1) {
            jobList.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void updateJob(Job updatedJob) {
        for (int i = 0; i < jobList.size(); i++) {
            if (jobList.get(i).getId() != null && jobList.get(i).getId().equals(updatedJob.getId())) {
                jobList.set(i, updatedJob);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompany, tvLocation, tvStatus;
        Button btnEdit, btnDelete, btnToggleStatus;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompany = itemView.findViewById(R.id.tvJobCompany);
            tvLocation = itemView.findViewById(R.id.tvJobLocation);
            tvStatus = itemView.findViewById(R.id.tvJobStatus);
            btnEdit = itemView.findViewById(R.id.btnEditJob);
            btnDelete = itemView.findViewById(R.id.btnDeleteJob);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }
}
