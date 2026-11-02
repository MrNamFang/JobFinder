package com.example.jobfinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.model.Feedback;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private List<Feedback> feedbackList;

    public FeedbackAdapter(List<Feedback> feedbackList) {
        this.feedbackList = feedbackList;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback fb = feedbackList.get(position);

        holder.tvFeedbackContent.setText(fb.getContent());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvFeedbackDate.setText("Ngày gửi: " + sdf.format(fb.getCreatedAt()));

        holder.tvFeedbackStatus.setText(fb.getStatus());

        if ("Đã xử lý".equals(fb.getStatus())) {
            holder.tvFeedbackStatus.setTextColor(0xFF4CAF50);
            holder.tvFeedbackStatus.setBackgroundColor(0xFFE8F5E9);
        } else {
            holder.tvFeedbackStatus.setTextColor(0xFFFF9800);
            holder.tvFeedbackStatus.setBackgroundColor(0xFFFFF3E0);
        }

        if (fb.getAdminReply() != null && !fb.getAdminReply().isEmpty()) {
            holder.tvAdminReply.setText("Phản hồi từ Admin: " + fb.getAdminReply());
            holder.tvAdminReply.setVisibility(View.VISIBLE);
        } else {
            holder.tvAdminReply.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    public void updateData(List<Feedback> newList) {
        this.feedbackList = newList;
        notifyDataSetChanged();
    }

    static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvFeedbackContent, tvFeedbackDate, tvFeedbackStatus, tvAdminReply;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeedbackContent = itemView.findViewById(R.id.tvFeedbackContent);
            tvFeedbackDate = itemView.findViewById(R.id.tvFeedbackDate);
            tvFeedbackStatus = itemView.findViewById(R.id.tvFeedbackStatus);
            tvAdminReply = itemView.findViewById(R.id.tvAdminReply);
        }
    }
}