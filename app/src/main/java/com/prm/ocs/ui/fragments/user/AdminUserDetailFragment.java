package com.prm.ocs.ui.fragments.user;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.view.base.UserView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public class AdminUserDetailFragment extends Fragment implements UserView {
    private static final String TAG = "AdminUserDetailFragment";
    private static final String ARG_USER_ID = "user_id";
    private UserController userController;
    private EditText usernameEditText, emailEditText, phoneEditText, roleEditText;
    private CheckBox verifiedEditText;
    private ImageView userImageView;
    private Button saveButton, deleteButton;
    private User currentUser;
    private String imagePath;
    private Uri photoUri;
    private ProgressBar loadingIndicator;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    public static AdminUserDetailFragment newInstance(UUID userId) {
        AdminUserDetailFragment fragment = new AdminUserDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                    userImageView.setImageBitmap(bitmap);
                    imagePath = saveImageToInternalStorage(bitmap, "user_" + System.currentTimeMillis() + ".jpg");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), photoUri);
                    userImageView.setImageBitmap(bitmap);
                    imagePath = saveImageToInternalStorage(bitmap, "user_" + System.currentTimeMillis() + ".jpg");
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user_detail, container, false);

        usernameEditText = view.findViewById(R.id.user_username);
        emailEditText = view.findViewById(R.id.user_email);
        phoneEditText = view.findViewById(R.id.user_phone);
        roleEditText = view.findViewById(R.id.user_role);
        verifiedEditText = view.findViewById(R.id.user_verified);
        userImageView = view.findViewById(R.id.user_image);
        saveButton = view.findViewById(R.id.save_button);
        deleteButton = view.findViewById(R.id.delete_button);

        userController = new UserController(this);
        if (getArguments() != null && getArguments().containsKey(ARG_USER_ID)) {
            UUID userId = UUID.fromString(getArguments().getString(ARG_USER_ID));
            userController.loadUserDetails(userId);
        } else {
            Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }

        userImageView.setOnClickListener(v -> showImageSelectionDialog());
        saveButton.setOnClickListener(v -> attemptUpdateUser());
        deleteButton.setOnClickListener(v -> showDeleteConfirmation());

        return view;
    }

    @Override
    public void displayUserDetails(User user) {
        if (getView() == null) return;
        currentUser = user;
        if (user != null) {
            usernameEditText.setText(user.getUsername() != null ? user.getUsername() : "");
            emailEditText.setText(user.getEmail() != null ? user.getEmail() : "");
            phoneEditText.setText(user.getPhone() != null ? user.getPhone() : "");
            roleEditText.setText(String.valueOf(user.getRole()));
            verifiedEditText.setText(String.valueOf(user.isVerified()));

            if (user.getImgUrl() != null && !user.getImgUrl().isEmpty()) {
                File imageFile = new File(user.getImgUrl());
                Glide.with(this)
                        .load(imageFile)
                        .circleCrop()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(userImageView);
            } else {
                Glide.with(this)
                        .load(R.drawable.ic_launcher_background)
                        .circleCrop()
                        .into(userImageView);
            }
        } else {
            Toast.makeText(getContext(), "User details not found.", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void showImageSelectionDialog() {
        boolean hasCamera = requireContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        String[] options = hasCamera ?
                new String[]{"Choose from Gallery", "Take a Photo", "Enter Image URL"} :
                new String[]{"Choose from Gallery", "Enter Image URL"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Select Image Source")
                .setItems(options, (dialog, which) -> {
                    if (options[which].equals("Choose from Gallery")) {
                        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        pickImageLauncher.launch(pickIntent);
                    } else if (options[which].equals("Take a Photo")) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = createImageFile();
                        if (photoFile != null) {
                            photoUri = FileProvider.getUriForFile(requireContext(),
                                    "com.prm.ocs.fileprovider", photoFile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                            takePictureLauncher.launch(takePictureIntent);
                        }
                    } else if (options[which].equals("Enter Image URL")) {
                        showUrlInputDialog();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private File createImageFile() {
        try {
            String imageFileName = "user_" + System.currentTimeMillis();
            File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            return image;
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error creating file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void showUrlInputDialog() {
        EditText urlInput = new EditText(getContext());
        urlInput.setHint("Enter image URL");
        new AlertDialog.Builder(requireContext())
                .setTitle("Enter Image URL")
                .setView(urlInput)
                .setPositiveButton("Load", (dialog, which) -> {
                    String url = urlInput.getText().toString().trim();
                    if (!url.isEmpty()) {
                        loadImageFromUrl(url);
                    } else {
                        Toast.makeText(getContext(), "Please enter a valid URL", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadImageFromUrl(String url) {
        new Thread(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                input.close();

                requireActivity().runOnUiThread(() -> {
                    userImageView.setImageBitmap(bitmap);
                    imagePath = saveImageToInternalStorage(bitmap, "user_" + System.currentTimeMillis() + ".jpg");
                });
            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error loading image from URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private String saveImageToInternalStorage(Bitmap bitmap, String fileName) {
        try {
            File directory = new File(requireContext().getFilesDir(), "avatars");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void attemptUpdateUser() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "Cannot update, user data not loaded.", Toast.LENGTH_SHORT).show();
            return;
        }

        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String roleStr = roleEditText.getText().toString().trim();
        String verifiedStr = verifiedEditText.isChecked() ? "true" : "false";

        if (username.isEmpty()) {
            usernameEditText.setError("Username is required!");
            usernameEditText.requestFocus();
            return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid Email is required!");
            emailEditText.requestFocus();
            return;
        }
        String phoneRegex = "^(\\+84|0)[1-9][0-9]{8}$";
        if (phone.isEmpty() || !phone.matches(phoneRegex)) {
            phoneEditText.setError("Valid VN Phone is required (0xxxxxxxxx or +84xxxxxxxxx)!");
            phoneEditText.requestFocus();
            return;
        }
        final int role;
        try {
            int tempRole = Integer.parseInt(roleStr);
            if (tempRole < 0 || tempRole > 2) throw new NumberFormatException();
            role = tempRole;
        } catch (NumberFormatException e) {
            roleEditText.setError("Role must be 0, 1, or 2!");
            roleEditText.requestFocus();
            return;
        }
        boolean isVerified;
        if (verifiedStr.equalsIgnoreCase("true")) {
            isVerified = true;
        } else if (verifiedStr.equalsIgnoreCase("false")) {
            isVerified = false;
        } else {
            verifiedEditText.setError("Verified must be 'true' or 'false'!");
            verifiedEditText.requestFocus();
            return;
        }

        showLoading(true);

        userController.checkUserExistsForUpdate(username, email, phone, currentUser.getUserId(), new UserController.CheckUserExistsCallback() {
            @Override
            public void onResult(String conflictingField) {
                if (getView() == null) return;
                showLoading(false);

                if (conflictingField != null) {
                    switch (conflictingField) {
                        case "username":
                            usernameEditText.setError("Username already exists for another user!");
                            usernameEditText.requestFocus();
                            break;
                        case "email":
                            emailEditText.setError("Email already registered by another user!");
                            emailEditText.requestFocus();
                            break;
                        case "phone":
                            phoneEditText.setError("Phone number already used by another user!");
                            phoneEditText.requestFocus();
                            break;
                    }
                } else {
                    Log.d(TAG, "No duplicates found for update. Proceeding to update user.");
                    proceedWithUserUpdate(username, email, phone, role, isVerified);
                }
            }

            @Override
            public void onError(String message) {
                if (getView() == null) return;
                showLoading(false);
                Log.e(TAG, "Error checking user existence for update: " + message);
                Toast.makeText(getContext(), "Error checking user: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithUserUpdate(String username, String email, String phone, int role, boolean isVerified) {
        try {
            if (imagePath != null && currentUser.getImgUrl() != null && !currentUser.getImgUrl().isEmpty()) {
                if (!imagePath.equals(currentUser.getImgUrl())) {
                    File oldImageFile = new File(currentUser.getImgUrl());
                    if (oldImageFile.exists()) {
                        boolean deleted = oldImageFile.delete();
                        Log.d(TAG, "Old image deleted: " + deleted);
                    }
                } else {
                    imagePath = null;
                }
            }

            currentUser.setUsername(username);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);
            currentUser.setRole(role);
            currentUser.setVerified(isVerified);
            if (imagePath != null) {
                currentUser.setImgUrl(imagePath);
            }

            Log.d(TAG, "Updating user: " + currentUser.getUsername());
            userController.updateUser(currentUser);

        } catch (Exception e) {
            Log.e(TAG, "Error preparing user data for update", e);
            showLoading(false);
            Toast.makeText(getContext(), "Error preparing user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (saveButton != null) {
            saveButton.setEnabled(!isLoading);
        }
        if (deleteButton != null) {
            deleteButton.setEnabled(!isLoading);
        }
    }

    private void showDeleteConfirmation() {
        if (currentUser == null) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete user '" + currentUser.getUsername() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteUser();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void deleteUser() {
        if (currentUser != null) {
            showLoading(true);
            Log.d(TAG, "Deleting user: " + currentUser.getUsername());
            if (currentUser.getImgUrl() != null && !currentUser.getImgUrl().isEmpty()) {
                File imageFile = new File(currentUser.getImgUrl());
                if (imageFile.exists()) {
                    userController.deleteUser(currentUser);
                    boolean deleted = imageFile.delete();
                    Log.d(TAG, "Image file deleted on user delete: " + deleted);
                } else {
                    userController.deleteUser(currentUser);
                }
            } else {
                userController.deleteUser(currentUser);
            }

        } else {
            Toast.makeText(getContext(), "Cannot delete, user data not available.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSuccess(User user) {
        if (getView() == null) return;
        showLoading(false);
        if (currentUser != null && user == null) {
            Log.d(TAG, "User delete successful.");
            Toast.makeText(getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "User operation successful (Insert/Update).");
            Toast.makeText(getContext(), "Operation Successful!", Toast.LENGTH_SHORT).show();
        }
        getParentFragmentManager().popBackStack();
    }

    @Override
    public void displayUsers(List<User> users) {
    }
}