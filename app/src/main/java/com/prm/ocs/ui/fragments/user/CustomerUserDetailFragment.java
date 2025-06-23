package com.prm.ocs.ui.fragments.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.base.UserView;
import com.prm.ocs.ui.view.cart.LoginCheck;
import com.prm.ocs.ui.view.user.UserHomepageActivity;
import com.prm.ocs.utils.UUIDUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class CustomerUserDetailFragment extends Fragment implements UserView {
    private UserController userController;
    private TextView usernameTextView, emailTextView, phoneTextView, roleTextView, verifiedTextView;
    private ImageView avatarImageView;
    private Button editProfileButton;
    private Button logoutButton;
    private ImageButton backButton;
    private ImageButton homeButton;
    private UUID userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_user_detail, container, false);

        usernameTextView = view.findViewById(R.id.user_username);
        emailTextView = view.findViewById(R.id.user_email);
        phoneTextView = view.findViewById(R.id.user_phone);
        roleTextView = view.findViewById(R.id.user_role);
        verifiedTextView = view.findViewById(R.id.user_verified);
        avatarImageView = view.findViewById(R.id.user_avatar);
        editProfileButton = view.findViewById(R.id.edit_profile_button);
        logoutButton = view.findViewById(R.id.logout_button);
        backButton = view.findViewById(R.id.back_button);
        homeButton = view.findViewById(R.id.home_button);
        userController = new UserController(this);

        SessionManager sessionManager = new SessionManager(requireContext());
        userId = UUID.fromString(sessionManager.getUserId());

        // Xử lý nút Back
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UserHomepageActivity.class);
            startActivity(intent);
        });

        // Xử lý nút Home
        homeButton.setOnClickListener(v -> {
            // Logic xử lý nút Home
            Intent intent = new Intent(requireContext(), UserHomepageActivity.class);
            startActivity(intent);
        });


        // Xử lý nút Logout với dialog xác nhận
        logoutButton.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        editProfileButton.setOnClickListener(v -> {
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, editProfileFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại thông tin user khi quay lại từ EditProfileFragment
        userController.loadUserDetails(userId);
    }
    // Phương thức hiển thị dialog xác nhận logout
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm logout");
        builder.setMessage("Are you sure you want to log out?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Logic đăng xuất: quay về màn hình đăng nhập
                performLogout();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Phương thức thực hiện logout
    private void performLogout() {
        SessionManager sessionManager = new SessionManager(requireContext());
        sessionManager.clearSession();
        Intent intent = new Intent(requireContext(), LoginCheck.class);
        startActivity(intent);
        return;
    }

    @Override
    public void displayUserDetails(User user) {
        usernameTextView.setText("Username: " + user.getUsername());
        emailTextView.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
        phoneTextView.setText("Phone: " + (user.getPhone() != null ? user.getPhone() : "N/A"));
        roleTextView.setText("Role: " + getRoleName(user.getRole()));
        verifiedTextView.setText("Verified: " + (user.isVerified() ? "Yes" : "No"));

        if (user.getImgUrl() != null && !user.getImgUrl().isEmpty()) {
            File imageFile = new File(user.getImgUrl());
            Glide.with(this)
                    .load(imageFile)
                    .error(R.drawable.ic_default_avatar) // Ảnh mặc định nếu không tìm thấy
                    .into(avatarImageView);
        }
    }

    @Override
    public void onLoginSuccess(User user) {

    }

    @Override
    public void displayUsers(List<User> users) {
        // Không dùng trong Fragment này
    }

    private String getRoleName(int role) {
        switch (role) {
            case 0:
                return "Admin";
            case 2:
                return "Customer";
            default:
                return "Unknown";
        }
    }
}