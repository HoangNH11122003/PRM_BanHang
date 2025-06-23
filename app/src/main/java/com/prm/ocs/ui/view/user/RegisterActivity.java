package com.prm.ocs.ui.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.view.base.UserView;

import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements UserView {
    private EditText etUsername, etEmail, etPhone, etPassword;
    private Button btnRegister;
    private TextView tvBackToLogin;
    private UserController userController;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        userController = new UserController((UserView) this);

        tvBackToLogin.setOnClickListener(v -> {
            finish();
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInputs(username, email, phone, password)) {
            return;
        }

        userController.checkUserExists(username, email, phone);
    }

    private boolean validateInputs(String username, String email, String phone, String password) {
        if (username.isEmpty()) {
            etUsername.setError("Username is required");
            etUsername.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etPhone.setError("Phone number is required");
            etPhone.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.PHONE.matcher(phone).matches() || phone.length() < 10) {
            etPhone.setError("Please enter a valid phone number (at least 10 digits)");
            etPhone.requestFocus();
            return false;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            etPassword.setError("Password must be at least 8 characters, contain both uppercase and lowercase letters");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void displayUsers(List<User> users) {
    }

    @Override
    public void displayUserDetails(User user) {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (user != null) {
            if (user.getUsername().equals(username)) {
                etUsername.setError("Username already exists");
                etUsername.requestFocus();
                return;
            }
            if (user.getEmail().equals(email)) {
                etEmail.setError("Email already exists");
                etEmail.requestFocus();
                return;
            }
            if (user.getPhone().equals(phone)) {
                etPhone.setError("Phone number already exists");
                etPhone.requestFocus();
                return;
            }
        }

        String password = etPassword.getText().toString().trim();
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setPassword(password);
        newUser.setRole(2);
        newUser.setVerified(false);

        userController.insertUser(newUser);
    }

    @Override
    public void onLoginSuccess(User user) {
        Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
        finish();
    }
}