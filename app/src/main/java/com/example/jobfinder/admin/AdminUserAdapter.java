package com.example.jobfinder.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobfinder.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

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

            holder.tvUserStatus.setText(
                    "Trạng thái: Đã khóa"
            );

            holder.btnBlock.setText(
                    "Mở khóa tài khoản"
            );

        } else {

            holder.tvUserStatus.setText(
                    "Trạng thái: Hoạt động"
            );

            holder.btnBlock.setText(
                    "Khóa tài khoản"
            );
        }

        holder.btnBlock.setOnClickListener(v -> {

            boolean newStatus = !user.isBlocked();

            db.collection("users")
                    .document(user.getId())
                    .update("blocked", newStatus)
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

            tvUserName =
                    itemView.findViewById(R.id.tvUserName);

            tvUserEmail =
                    itemView.findViewById(R.id.tvUserEmail);

            tvUserRole =
                    itemView.findViewById(R.id.tvUserRole);

            tvUserStatus =
                    itemView.findViewById(R.id.tvUserStatus);

            btnBlock =
                    itemView.findViewById(R.id.btnBlock);
        }
    }
}