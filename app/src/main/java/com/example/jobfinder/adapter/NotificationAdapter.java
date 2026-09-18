package com.example.jobfinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.model.Notification;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {

    public interface OnNotificationClickListener {
        void onClick(Notification notification);
    }

    private List<Notification> notificationList;
    private OnNotificationClickListener listener;

    public NotificationAdapter(List<Notification> notificationList, OnNotificationClickListener listener) {
        this.notificationList = notificationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        Notification notif = notificationList.get(position);

        holder.tvNotifTitle.setText(notif.getTitle());
        holder.tvNotifMessage.setText(notif.getMessage());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvNotifTime.setText(sdf.format(notif.getCreatedAt()));

        // Chấm xanh chỉ hiện khi chưa đọc
        holder.viewUnreadDot.setVisibility(notif.isRead() ? View.INVISIBLE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(notif);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateData(List<Notification> newList) {
        this.notificationList = newList;
        notifyDataSetChanged();
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotifTitle, tvNotifMessage, tvNotifTime;
        View viewUnreadDot;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotifTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvNotifMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvNotifTime = itemView.findViewById(R.id.tvNotifTime);
            viewUnreadDot = itemView.findViewById(R.id.viewUnreadDot);
        }
    }
}