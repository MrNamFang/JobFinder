package com.example.jobfinder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.model.Application;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.AppViewHolder> {

    public interface OnApplicationLongClickListener {
        void onLongClick(Application application);
    }

    private List<Application> applicationList;
    private OnApplicationLongClickListener longClickListener;

    public ApplicationAdapter(List<Application> applicationList, OnApplicationLongClickListener longClickListener) {
        this.applicationList = applicationList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_application, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        Application app = applicationList.get(position);

        holder.tvAppJobTitle.setText(app.getJobTitle());
        holder.tvAppCompanyName.setText(app.getCompanyName());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateStr = sdf.format(app.getAppliedDate());
        holder.tvAppDate.setText("Ngày ứng tuyển: " + dateStr);

        holder.tvAppStatus.setText(app.getStatus());

        switch (app.getStatus()) {
            case "Được duyệt":
                holder.tvAppStatus.setTextColor(0xFF4CAF50);
                holder.tvAppStatus.setBackgroundColor(0xFFE8F5E9);
                break;
            case "Từ chối":
                holder.tvAppStatus.setTextColor(0xFFF44336);
                holder.tvAppStatus.setBackgroundColor(0xFFFFEBEE);
                break;
            default: // "Đang chờ"
                holder.tvAppStatus.setTextColor(0xFFFF9800);
                holder.tvAppStatus.setBackgroundColor(0xFFFFF3E0);
                break;
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(app);
            }
            return true; // trả về true để báo đã xử lý xong sự kiện long click
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public void updateData(List<Application> newList) {
        this.applicationList = newList;
        notifyDataSetChanged();
    }

    static class AppViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppJobTitle, tvAppCompanyName, tvAppDate, tvAppStatus;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppJobTitle = itemView.findViewById(R.id.tvAppJobTitle);
            tvAppCompanyName = itemView.findViewById(R.id.tvAppCompanyName);
            tvAppDate = itemView.findViewById(R.id.tvAppDate);
            tvAppStatus = itemView.findViewById(R.id.tvAppStatus);
        }
    }
}