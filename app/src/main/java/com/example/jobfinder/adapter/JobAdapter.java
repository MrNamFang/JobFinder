package com.example.jobfinder.adapter;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.model.Job;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    public interface OnJobClickListener {
        void onJobClick(Job job);
    }

    private List<Job> jobList;
    private OnJobClickListener listener;

    public JobAdapter(List<Job> jobList, OnJobClickListener listener) {
        this.jobList = jobList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);

        holder.tvJobTitle.setText(job.getTitle());
        holder.tvCompanyName.setText(job.getCompanyName());
        holder.tvLocation.setText(job.getLocation());
        holder.tvJobType.setText(job.getJobType());
        holder.tvSalary.setText(job.getSalary());

        // Chữ cái đầu của tên công ty làm ảnh đại diện
        String company = job.getCompanyName();
        holder.tvCompanyInitial.setText(
                TextUtils.isEmpty(company) ? "?" : company.trim().substring(0, 1).toUpperCase());

        // Thời gian đăng, ví dụ "2 ngày trước"
        if (job.getPostedDate() > 0) {
            holder.tvPosted.setText(DateUtils.getRelativeTimeSpanString(
                    job.getPostedDate(), System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));
        } else {
            holder.tvPosted.setText("");
        }

        // Xen kẽ màu thẻ: xanh ngọc, cam, trắng
        int[] cardBgs = {R.drawable.bg_card_mint, R.drawable.bg_card_peach, R.drawable.bg_card};
        holder.itemView.setBackgroundResource(cardBgs[position % 3]);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJobClick(job);
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public void updateData(List<Job> newList) {
        this.jobList = newList;
        notifyDataSetChanged();
    }

    static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvLocation, tvJobType, tvSalary,
                tvCompanyInitial, tvPosted;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tvJobTitle);
            tvCompanyName = itemView.findViewById(R.id.tvCompanyName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvJobType = itemView.findViewById(R.id.tvJobType);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvCompanyInitial = itemView.findViewById(R.id.tvCompanyInitial);
            tvPosted = itemView.findViewById(R.id.tvPosted);
        }
    }
}