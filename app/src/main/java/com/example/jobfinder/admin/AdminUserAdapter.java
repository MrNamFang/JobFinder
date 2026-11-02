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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUserAdapter
        extends RecyclerView.Adapter<AdminUserAdapter.UserViewHolder> {

    private List<AdminUser> userList;
    private FirebaseFirestore db;

    public AdminUserAdapter(List<AdminUser> userList) {
        this.userList = userList;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder,
            int position) {

        AdminUser user = userList.get(position);

        holder.tvUserName.setText(user.getName());
        holder.tvUserEmail.setText(user.getEmail());
        holder.tvUserRole.setText("Vai trò: " + user.getRole());

        if (user.isBlocked()) {
            holder.tvUserStatus.setText("Trạng thái: Đã khóa (Banned)");
            holder.tvUserStatus.setTextColor(Color.parseColor("#C62828"));
            holder.btnBlock.setText("Mở khóa tài khoản");
            holder.btnBlock.setBackgroundColor(Color.parseColor("#4CAF50"));
        } else {
            holder.tvUserStatus.setText("Trạng thái: Đang hoạt động");
            holder.tvUserStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.btnBlock.setText("Khóa tài khoản");
            holder.btnBlock.setBackgroundColor(Color.parseColor("#E53935"));
        }

        holder.btnBlock.setOnClickListener(v -> {
            boolean newStatus = !user.isBlocked();

            Map<String, Object> updates = new HashMap<>();
            updates.put("blocked", newStatus);
            updates.put("status", newStatus ? "banned" : "active");

            db.collection("users")
                    .document(user.getId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        userList.set(
                                position,
                                new AdminUser(
                                        user.getId(),
                                        user.getName(),
                                        user.getEmail(),
                                        user.getRole(),
                                        newStatus
                                )
                        );
                        notifyItemChanged(position);
                        Toast.makeText(
                                holder.itemView.getContext(),
                                (newStatus ? "Đã khóa" : "Đã mở khóa") + " tài khoản: " + user.getName(),
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(
                                holder.itemView.getContext(),
                                "Lỗi cập nhật: " + e.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvUserEmail;
        TextView tvUserRole;
        TextView tvUserStatus;
        Button btnBlock;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnBlock = itemView.findViewById(R.id.btnBlock);
        }
    }
}
