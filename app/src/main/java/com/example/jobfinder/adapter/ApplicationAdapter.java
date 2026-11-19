package com.example.jobfinder.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.example.jobfinder.model.Application;

import java.text.SimpleDateFormat;
import java.util.Date;
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
        String dateStr = sdf.format(new Date(app.getAppliedDate()));
        holder.tvAppDate.setText("Ngày ứng tuyển: " + dateStr);

        // Nhãn tiếng Việt, dù Firestore lưu "Pending"/"Accepted"...
        holder.tvAppStatus.setText(Application.label(app.getStatus()));

        int textColor;
        int bgColor;
        switch (Application.normalize(app.getStatus())) {
            case Application.STATUS_ACCEPTED:
                textColor = 0xFF166534;
                bgColor = 0xFFCDEFD6;
                break;
            case Application.STATUS_REJECTED:
                textColor = 0xFFC62828;
                bgColor = 0xFFFFDAD6;
                break;
            case Application.STATUS_REVIEWED:
                textColor = 0xFF0F766E;
                bgColor = 0xFFCFF0EA;
                break;
            default: // Pending
                textColor = 0xFF9A4A00;
                bgColor = 0xFFFFE0C2;
                break;
        }
        holder.tvAppStatus.setTextColor(textColor);
        holder.tvAppStatus.setBackground(roundedBg(holder.itemView, bgColor));

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onLongClick(app);
            }
            return true;
        });
    }

    // Nền bo tròn 12dp cho nhãn trạng thái
    private GradientDrawable roundedBg(View view, int color) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(color);
        bg.setCornerRadius(12f * view.getResources().getDisplayMetrics().density);
        return bg;
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