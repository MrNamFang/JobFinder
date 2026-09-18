package com.example.jobfinder.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;

import java.util.List;

public class AdminReportAdapter
        extends RecyclerView.Adapter<AdminReportAdapter.ReportViewHolder> {

    private List<AdminReport> reportList;

    public AdminReportAdapter(List<AdminReport> reportList) {
        this.reportList = reportList;
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

        holder.tvReporter.setText(
                "Người gửi: " + report.getUserName()
        );

        holder.tvReason.setText(
                "Email: " + report.getUserEmail()
        );

        holder.tvContent.setText(
                "Nội dung: " + report.getContent()
        );

        holder.tvReportStatus.setText(
                "Trạng thái: " + report.getStatus()
        );
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

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvReporter = itemView.findViewById(R.id.tvReporter);
            tvReason = itemView.findViewById(R.id.tvReason);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvReportStatus = itemView.findViewById(R.id.tvReportStatus);
        }
    }
}