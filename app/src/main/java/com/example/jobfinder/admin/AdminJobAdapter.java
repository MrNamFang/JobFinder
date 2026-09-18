package com.example.jobfinder.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AdminJobAdapter
        extends RecyclerView.Adapter<AdminJobAdapter.JobViewHolder> {

    private List<AdminJob> jobList;

    private FirebaseFirestore db;

    private OnJobClickListener listener;

    public interface OnJobClickListener {
        void onJobClick(AdminJob job);
    }

    public AdminJobAdapter(
            List<AdminJob> jobList,
            OnJobClickListener listener) {

        this.jobList = jobList;
        this.listener = listener;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_admin_job,
                        parent,
                        false
                );

        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull JobViewHolder holder,
            int position) {

        AdminJob job = jobList.get(position);

        holder.tvJobTitle.setText(
                job.getTitle()
        );

        holder.tvJobCompany.setText(
                "Công ty: " + job.getCompany()
        );

        holder.tvJobLocation.setText(
                "Địa điểm: " + job.getLocation()
        );

        holder.tvJobStatus.setText(
                "Loại: " + job.getJobType()
        );

        // Bấm vào bài đăng để xem chi tiết
        holder.itemView.setOnClickListener(v -> {

            if (listener != null) {
                listener.onJobClick(job);
            }
        });

        holder.btnDeleteJob.setOnClickListener(v -> {

            db.collection("jobs")
                    .document(job.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {

                        int currentPosition =
                                holder.getAdapterPosition();

                        if (currentPosition !=
                                RecyclerView.NO_POSITION) {

                            jobList.remove(currentPosition);

                            notifyItemRemoved(
                                    currentPosition
                            );
                        }

                        Toast.makeText(
                                holder.itemView.getContext(),
                                "Đã xóa bài đăng",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(
                                holder.itemView.getContext(),
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvJobTitle;
        TextView tvJobCompany;
        TextView tvJobLocation;
        TextView tvJobStatus;

        Button btnDeleteJob;

        public JobViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvJobTitle =
                    itemView.findViewById(
                            R.id.tvJobTitle
                    );

            tvJobCompany =
                    itemView.findViewById(
                            R.id.tvJobCompany
                    );

            tvJobLocation =
                    itemView.findViewById(
                            R.id.tvJobLocation
                    );

            tvJobStatus =
                    itemView.findViewById(
                            R.id.tvJobStatus
                    );

            btnDeleteJob =
                    itemView.findViewById(
                            R.id.btnDeleteJob
                    );
        }
    }
}