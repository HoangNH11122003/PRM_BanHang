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

public class AdminUserAddFragment extends Fragment implements UserView {
    private static final String TAG = "AdminUserAddFragment";
    private UserController userController;
    private EditText usernameEditText, emailEditText, phoneEditText, roleEditText;
    private CheckBox verifiedEditCheck;
    private ImageView userImageView;
    private Button saveButton;
    private String imagePath;
    private Uri photoUri;
    private ProgressBar loadingIndicator;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePictureLauncher;

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
        View view = inflater.inflate(R.layout.fragment_admin_user_add, container, false);

        usernameEditText = view.findViewById(R.id.user_username);
        emailEditText = view.findViewById(R.id.user_email);
        phoneEditText = view.findViewById(R.id.user_phone);
        roleEditText = view.findViewById(R.id.user_role);
        verifiedEditCheck = view.findViewById(R.id.user_verified);
        userImageView = view.findViewById(R.id.user_image);
        saveButton = view.findViewById(R.id.save_button);

        userController = new UserController(this);

        userImageView.setOnClickListener(v -> showImageSelectionDialog());
        saveButton.setOnClickListener(v -> attemptAddUser());

        return view;
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

    private void attemptAddUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String roleStr = roleEditText.getText().toString().trim();
        String verifiedStr = verifiedEditCheck.isChecked() ? "true" : "false";

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
            verifiedEditCheck.setError("Verified must be true or false!");
            return;
        }

        showLoading(true);

        userController.checkUserExistsForInsert(username, email, phone, new UserController.CheckUserExistsCallback() {
            @Override
            public void onResult(String conflictingField) {
                if (getView() == null) return;
                showLoading(false);

                if (conflictingField != null) {
                    switch (conflictingField) {
                        case "username":
                            usernameEditText.setError("Username already exists!");
                            usernameEditText.requestFocus();
                            break;
                        case "email":
                            emailEditText.setError("Email already registered!");
                            emailEditText.requestFocus();
                            break;
                        case "phone":
                            phoneEditText.setError("Phone number already in use!");
                            phoneEditText.requestFocus();
                            break;
                    }
                } else {
                    Log.d(TAG, "No duplicates found. Proceeding to insert user.");
                    proceedWithUserInsertion(username, email, phone, role, isVerified);
                }
            }

            @Override
            public void onError(String message) {
                if (getView() == null) return;
                showLoading(false);
                Log.e(TAG, "Error checking user existence: " + message);
                Toast.makeText(getContext(), "Error checking user: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void proceedWithUserInsertion(String username, String email, String phone, int role, boolean isVerified) {
        try {
            User newUser = new User();
            newUser.setUserId(UUID.randomUUID());
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setPassword("password");
            newUser.setRole(role);
            newUser.setVerified(isVerified);
            newUser.setImgUrl(imagePath);

            Log.d(TAG, "Inserting user: " + newUser.getUsername());
            userController.insertUser(newUser);

        } catch (Exception e) {
            Log.e(TAG, "Error creating user object", e);
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
    }

    @Override
    public void displayUsers(List<User> users) {
    }

    @Override
    public void displayUserDetails(User user) {
    }

    @Override
    public void onLoginSuccess(User user) {
        if (getView() == null) return;
        showLoading(false);
        Log.d(TAG, "User operation successful (Insert/Update). User: " + (user != null ? user.getUsername() : "N/A"));
        Toast.makeText(getContext(), "Operation Successful!", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }
}