package com.prm.ocs.ui.fragments.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.base.UserView;

import java.util.List;
import java.util.UUID;

public class ChangePasswordFragment extends Fragment implements UserView {
    private UserController userController;
    private TextInputEditText oldPasswordEditText, newPasswordEditText, confirmPasswordEditText;
    private Button saveButton, cancelButton;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        oldPasswordEditText = view.findViewById(R.id.old_password_edit_text);
        newPasswordEditText = view.findViewById(R.id.new_password_edit_text);
        confirmPasswordEditText = view.findViewById(R.id.confirm_password_edit_text);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);

        userController = new UserController(this);

        // Lấy userId từ SessionManager
        SessionManager sessionManager = new SessionManager(requireContext());
        String userIdStr = sessionManager.getUserId();
        if (userIdStr == null || userIdStr.isEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
            return view;
        }
        UUID userId = UUID.fromString(userIdStr);

        // Tải thông tin người dùng
        userController.loadUserDetails(userId);

        // Vô hiệu hóa nút Save cho đến khi currentUser được tải
        saveButton.setEnabled(false);
        saveButton.setOnClickListener(v -> changePassword());
        cancelButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void changePassword() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String oldPassword = oldPasswordEditText.getText().toString().trim();
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validation
        if (oldPassword.isEmpty()) {
            oldPasswordEditText.setError("Old password cannot be empty");
            return;
        }

        if (newPassword.isEmpty()) {
            newPasswordEditText.setError("New password cannot be empty");
            return;
        }

        if (newPassword.length() < 6) {
            newPasswordEditText.setError("New password must be at least 6 characters");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Passwords do not match");
            return;
        }

        // Gọi controller để đổi mật khẩu
        userController.changePassword(currentUser.getUserId(), oldPassword, newPassword, success -> {
            if (success) {
                Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(requireContext(), "Old password is incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void displayUsers(List<User> users) {
        // Không dùng trong fragment này
    }

    @Override
    public void displayUserDetails(User user) {
        if (user != null) {
            currentUser = user;
            saveButton.setEnabled(true); // Kích hoạt nút Save khi có dữ liệu
        } else {
            Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack(); // Quay lại nếu không tải được
        }
    }

    @Override
    public void onLoginSuccess(User user) {
        // Không dùng trong fragment này
    }
}