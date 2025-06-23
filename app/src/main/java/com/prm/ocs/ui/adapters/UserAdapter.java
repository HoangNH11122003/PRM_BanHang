package com.prm.ocs.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prm.ocs.R;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.view.user.AdminUserListActivity;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private final List<User> users; // Danh sách người dùng hiển thị

    // Constructor nhận danh sách ban đầu
    public UserAdapter(Context context, List<User> initialUsers) {
        this.context = context;
        this.users = initialUsers; // Gán danh sách ban đầu
    }

    // Phương thức cập nhật danh sách
    public void setUsers(List<User> newUsers) {
        this.users.clear(); // Xóa dữ liệu cũ
        if (newUsers != null) {
            this.users.addAll(newUsers); // Thêm dữ liệu mới
        }
        notifyDataSetChanged(); // Thông báo thay đổi
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_card, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);

        holder.usernameTextView.setText(user.getUsername());
        holder.emailTextView.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        holder.phoneTextView.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
        holder.roleTextView.setText("Role: " + getRoleString(user.getRole()));
        holder.verifiedTextView.setText("Verified: " + (user.isVerified() ? "Yes" : "No"));

        holder.usernameTextView.setOnClickListener(v -> {
            // Nên dùng interface callback thay vì ép kiểu Activity trực tiếp
            // Ví dụ: if (context instanceof UserAdapterListener) { ((UserAdapterListener) context).onUserMoreClicked(user.getUserId()); }
            // Tạm thời giữ nguyên để giống code gốc:
            if (context instanceof AdminUserListActivity) {
                ((AdminUserListActivity) context).showUserDetail(user.getUserId());
            }
        });

        holder.moreButton.setOnClickListener(v -> {
            // Nên dùng interface callback thay vì ép kiểu Activity trực tiếp
            // Ví dụ: if (context instanceof UserAdapterListener) { ((UserAdapterListener) context).onUserMoreClicked(user.getUserId()); }
            // Tạm thời giữ nguyên để giống code gốc:
            if (context instanceof AdminUserListActivity) {
                ((AdminUserListActivity) context).showUserDetail(user.getUserId());
            }
        });
    }

    // Helper function cho Role
    private String getRoleString(int role) {
        switch (role) {
            case 0:
                return "Admin";
            case 1:
                return "Staff";
            case 2:
                return "Customer";
            default:
                return "Unknown";
        }
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0; // Kiểm tra null
    }

    // Interface (nên tạo để thay thế việc ép kiểu Activity)
    // public interface UserAdapterListener {
    //     void onUserMoreClicked(UUID userId);
    // }

    // ViewHolder giữ nguyên
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView, emailTextView, phoneTextView, roleTextView, verifiedTextView;
        ImageButton moreButton;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.user_username);
            emailTextView = itemView.findViewById(R.id.user_email);
            phoneTextView = itemView.findViewById(R.id.user_phone);
            roleTextView = itemView.findViewById(R.id.user_role);
            verifiedTextView = itemView.findViewById(R.id.user_verified);
            moreButton = itemView.findViewById(R.id.user_more_button);
        }
    }
}