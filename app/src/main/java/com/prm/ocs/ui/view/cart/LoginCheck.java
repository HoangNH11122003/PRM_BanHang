package com.prm.ocs.ui.view.cart;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.SeedData;
import com.prm.ocs.ui.manager.SessionManager;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.view.admin.AdminDashboardActivity;
import com.prm.ocs.ui.view.base.UserView;
import com.prm.ocs.ui.view.user.ForgotPasswordActivity;
import com.prm.ocs.ui.view.user.RegisterActivity;
import com.prm.ocs.ui.view.user.UserHomepageActivity;

import java.util.List;

public class LoginCheck extends AppCompatActivity implements UserView {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private UserController userController;
    private SessionManager sessionManager;

    private TextView tvRegister , tvForgotPassword;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_check);
        SeedData seedData = new SeedData(this);
        seedData.seedDatabase();
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            if(sessionManager.getRole() == 0) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, UserHomepageActivity.class));
            }
            finish();
            return;
        }

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginCheck.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginCheck.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        userController = new UserController((UserView) this);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty()) {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
                return;
            }

            userController.loadUserDetailsByUsername(username);
        });
    }

    @Override
    public void displayUsers(List<User> users) {
    }

    @Override
    public void displayUserDetails(User user) {
        if (user != null && user.getPassword().equals(etPassword.getText().toString())) {
            sessionManager.saveUserSession(user.getUserId().toString(), user.getUsername(), user.getRole());
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();

            if(user.getRole() == 0) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            } else {
                startActivity(new Intent(this, UserHomepageActivity.class));
            }

            finish();
        } else {
            Toast.makeText(this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSuccess(User user) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        sessionManager.clearSession();
    }
}