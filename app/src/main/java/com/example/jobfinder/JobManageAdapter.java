package com.example.jobfinder;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter cho màn "Quản lý tin tuyển dụng" của Employer.
 *
 * Mỗi dòng có: chip trạng thái (Đang tuyển/Đã đóng), nút Toggle trạng thái,
 * nút Sửa, nút Xóa. Adapter không tự quyết định phải làm gì khi bấm các nút
 * này -- chỉ "báo" lại cho Activity qua interface OnJobActionListener.
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

        holder.tvTitle.setText(job.getTitle());
        holder.tvCompany.setText(job.getCompany());
        holder.tvLocation.setText(job.getLocation());

        boolean isOpen = job.getStatus() == null || job.getStatus().equals("Open");

        // Chip trạng thái: chữ + màu nền đổi theo Open/Closed
        holder.tvStatus.setText(isOpen ? "Đang tuyển" : "Đã đóng");
        GradientDrawable chip = new GradientDrawable();
        chip.setCornerRadius(20f);
        chip.setColor(isOpen ? Color.parseColor("#2E7D32") : Color.parseColor("#757575"));
        holder.tvStatus.setBackground(chip);

        // Nút toggle: chữ đổi theo trạng thái NGƯỢC LẠI với hiện tại, vì nó
        // thể hiện HÀNH ĐỘNG sẽ xảy ra khi bấm, không phải trạng thái hiện tại
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
            if (jobList.get(i).getId().equals(updatedJob.getId())) {
                jobList.set(i, updatedJob);
                notifyItemChanged(i);
                break;
            }
        }
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCompany, tvLocation, tvStatus;
        Button btnEdit, btnDelete, btnToggleStatus;

        JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvManageJobTitle);
            tvCompany = itemView.findViewById(R.id.tvManageJobCompany);
            tvLocation = itemView.findViewById(R.id.tvManageJobLocation);
            tvStatus = itemView.findViewById(R.id.tvJobStatus);
            btnEdit = itemView.findViewById(R.id.btnEditJob);
            btnDelete = itemView.findViewById(R.id.btnDeleteJob);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
        }
    }
}