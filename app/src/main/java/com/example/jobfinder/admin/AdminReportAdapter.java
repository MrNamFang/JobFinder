package com.example.jobfinder.admin;

import android.graphics.Color;
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

public class AdminReportAdapter
        extends RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder> {

    private List<AdminReport> reportList;
    private FirebaseFirestore db;
    private String collectionName = "feedbacks";

    public AdminReportAdapter(List<AdminReport> reportList) {
        this.reportList = reportList;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_report, parent, false);

        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ReportViewHolder holder,
            int position) {

        AdminReport report = reportList.get(position);

        holder.tvReporter.setText("Người gửi: " + report.getUserName());
        holder.tvReason.setText("Email: " + report.getUserEmail());
        holder.tvContent.setText("Nội dung: " + report.getContent());

        String status = report.getStatus();
        if ("resolved".equalsIgnoreCase(status) || "Đã xử lý".equalsIgnoreCase(status)) {
            holder.tvReportStatus.setText("Trạng thái: Đã xử lý (Xanh)");
            holder.tvReportStatus.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            holder.tvReportStatus.setText("Trạng thái: Chưa xử lý (Chờ giải quyết)");
            holder.tvReportStatus.setTextColor(Color.parseColor("#E65100"));
        }

        holder.btnResolveReport.setOnClickListener(v -> {
            db.collection(collectionName)
                    .document(report.getId())
                    .update("status", "resolved")
                    .addOnSuccessListener(aVoid -> {
                        report.setStatus("resolved");
                        notifyItemChanged(position);
                        Toast.makeText(holder.itemView.getContext(), "Đã đánh dấu xử lý xong báo cáo", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        holder.btnDeleteReport.setOnClickListener(v -> {
            db.collection(collectionName)
                    .document(report.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        int currentPos = holder.getAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            reportList.remove(currentPos);
                            notifyItemRemoved(currentPos);
                        }
                        Toast.makeText(holder.itemView.getContext(), "Đã xóa báo cáo", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(holder.itemView.getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public static class ReportViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvReporter;
        TextView tvReason;
        TextView tvContent;
        TextView tvReportStatus;
        Button btnResolveReport;
        Button btnDeleteReport;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReporter = itemView.findViewById(R.id.tvReporter);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReportStatus = itemView.findViewById(R.id.tvReportStatus);
            btnResolveReport = itemView.findViewById(R.id.btnResolveReport);
            btnDeleteReport = itemView.findViewById(R.id.btnDeleteReport);
        }
    }
}
