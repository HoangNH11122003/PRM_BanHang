
package com.prm.ocs.ui.fragments.user;

import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.ui.view.base.UserView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public class EditProfileFragment extends Fragment implements UserView {
    private UserController userController;
    private ImageView avatarImageView;
    private TextInputEditText usernameEditText, emailEditText, phoneEditText;
    private Button saveButton, cancelButton, changeAvatarButton, changePasswordButton;
    private User currentUser;
    private String avatarPath;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    avatarImageView.setImageURI(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        avatarImageView = view.findViewById(R.id.edit_avatar);
        usernameEditText = view.findViewById(R.id.edit_username);
        emailEditText = view.findViewById(R.id.edit_email);
        phoneEditText = view.findViewById(R.id.edit_phone);
        saveButton = view.findViewById(R.id.save_button);
        cancelButton = view.findViewById(R.id.cancel_button);
        changeAvatarButton = view.findViewById(R.id.change_avatar_button);
        changePasswordButton = view.findViewById(R.id.change_password_button);

        userController = new UserController(this);

        SessionManager sessionManager = new SessionManager(requireContext());
        UUID userId = UUID.fromString(sessionManager.getUserId());
        userController.loadUserDetails(userId);

        changeAvatarButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        saveButton.setOnClickListener(v -> saveProfile());

        cancelButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        changePasswordButton.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ChangePasswordFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void displayUserDetails(User user) {
        this.currentUser = user;
        usernameEditText.setText(user.getUsername());
        emailEditText.setText(user.getEmail() != null ? user.getEmail() : "");
        phoneEditText.setText(user.getPhone() != null ? user.getPhone() : "");

        if (user.getImgUrl() != null && !user.getImgUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getImgUrl())
                    .circleCrop()
                    .into(avatarImageView);
        }
        avatarPath = user.getImgUrl();
    }

    @Override
    public void onLoginSuccess(User user) {
        // Không dùng trong Fragment này
    }

    @Override
    public void displayUsers(List<User> users) {
        // Không dùng trong Fragment này
    }

    private void saveProfile() {
        if (currentUser == null) return;

        String newUsername = usernameEditText.getText().toString().trim();
        String newEmail = emailEditText.getText().toString().trim();
        String newPhone = phoneEditText.getText().toString().trim();

        // Validation
        if (!validateInputs(newUsername, newEmail, newPhone)) {
            return;
        }

        // Nếu có ảnh mới, lưu ảnh vào bộ nhớ và lấy đường dẫn
        if (selectedImageUri != null) {
            avatarPath = saveImageToInternalStorage(selectedImageUri);
            if (avatarPath == null) {
                Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedImageUri = null;
        }

        // Cập nhật thông tin user
        currentUser.setUsername(newUsername);
        currentUser.setEmail(newEmail.isEmpty() ? null : newEmail);
        currentUser.setPhone(newPhone.isEmpty() ? null : newPhone);
        currentUser.setImgUrl(avatarPath);

        userController.updateUser(currentUser);

        Glide.with(this).clear(avatarImageView);
        if (avatarPath != null) {
            Glide.with(this)
                    .load(avatarPath)
                    .circleCrop()
                    .into(avatarImageView);
        }

        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }

    private boolean validateInputs(String username, String email, String phone) {
        boolean isValid = true;

        // Validate username
        if (username.isEmpty()) {
            usernameEditText.setError("Username cannot be empty");
            isValid = false;
        } else if (username.length() < 3) {
            usernameEditText.setError("Username must be at least 3 characters");
            isValid = false;
        } else if (!username.matches("[a-zA-Z0-9_]+")) {
            usernameEditText.setError("Username can only contain letters, numbers, and underscores");
            isValid = false;
        }

        // Validate email (nếu có nhập)
        if (!email.isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Invalid email format");
                isValid = false;
            }
        }

        // Validate phone (nếu có nhập)
        if (!phone.isEmpty()) {
            if (!phone.startsWith("0")) {
                phoneEditText.setError("Phone number must start with 0");
                isValid = false;
            }
            else if (!phone.matches("[0-9]+")) {
                phoneEditText.setError("Phone number must contain only digits");
                isValid = false;
            }
        }

        return isValid;
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // Tạo thư mục "avatars" trong internal storage
            File avatarsDir = new File(requireContext().getFilesDir(), "avatars");
            if (!avatarsDir.exists()) {
                avatarsDir.mkdirs();
            }

            // Tạo file ảnh với tên là userId + timestamp để tránh cache
            String fileName = currentUser.getUserId() + "_" + System.currentTimeMillis() + ".jpg";
            File imageFile = new File(avatarsDir, fileName);

            // Xóa file cũ nếu tồn tại (dựa trên userId)
            File[] files = avatarsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().startsWith(currentUser.getUserId() + "_")) {
                        file.delete();
                    }
                }
            }

            // Copy dữ liệu từ URI vào file
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();
            outputStream.close();

            // Trả về đường dẫn tuyệt đối của file
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}