package com.prm.ocs.ui.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prm.ocs.R;
import com.prm.ocs.controller.UserController;
import com.prm.ocs.data.db.entity.User;
import com.prm.ocs.ui.view.base.UserView;
import com.prm.ocs.ui.view.cart.LoginCheck;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPasswordActivity extends AppCompatActivity implements UserView {
    private EditText etEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnResetPassword;
    private TextView tvBackToLogin;
    private UserController userController;
    private String generatedOtp;
    private User targetUser;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etEmail = findViewById(R.id.etEmail);
        etOtp = findViewById(R.id.etOtp);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        userController = new UserController((UserView) this);

        btnSendOtp.setOnClickListener(v -> sendOtp());

        btnResetPassword.setOnClickListener(v -> resetPassword());

        tvBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginCheck.class);
            startActivity(intent);
            finish();
        });
    }

    private void sendOtp() {
        String email = etEmail.getText().toString().trim();

        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Please enter a valid email address");
            etEmail.requestFocus();
            return;
        }

        userController.loadUsers();
    }

    private void resetPassword() {
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        if (otp.isEmpty()) {
            etOtp.setError("OTP is required");
            etOtp.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            etNewPassword.setError("New password is required");
            etNewPassword.requestFocus();
            return;
        }

        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            etNewPassword.setError("Password must be at least 8 characters, contain both uppercase and lowercase letters");
            etNewPassword.requestFocus();
            return;
        }

        if (!otp.equals(generatedOtp)) {
            etOtp.setError("Invalid OTP");
            etOtp.requestFocus();
            return;
        }

        targetUser.setPassword(newPassword);
        userController.updateUser(targetUser);
    }

    @Override
    public void displayUsers(List<User> users) {
        String email = etEmail.getText().toString().trim();

        for (User user : users) {
            if (user.getEmail().equals(email)) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            etEmail.setError("Email not found");
            etEmail.requestFocus();
            return;
        }

        generatedOtp = String.valueOf(new Random().nextInt(900000) + 100000);

        new Thread(() -> {
            try {
                sendEmail(email, generatedOtp);
                runOnUiThread(() -> {
                    Toast.makeText(this, "OTP sent to your email!", Toast.LENGTH_SHORT).show();
                    btnSendOtp.setVisibility(View.GONE);
                    etOtp.setVisibility(View.VISIBLE);
                    etNewPassword.setVisibility(View.VISIBLE);
                    btnResetPassword.setVisibility(View.VISIBLE);
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to send OTP: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    @Override
    public void displayUserDetails(User user) {
    }

    @Override
    public void onLoginSuccess(User user) {
        Toast.makeText(this, "Password reset successful!", Toast.LENGTH_SHORT).show();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginCheck.class);
            startActivity(intent);
            finish();
        }, 1000);
    }

    private void sendEmail(String recipientEmail, String otp) throws MessagingException {
        final String senderEmail = "minhnqhe176167@fpt.edu.vn";
        final String senderPassword = "isik xlce audj lvww";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Your OTP for Password Reset");
        message.setText("Your OTP is: " + otp + "\n\nPlease use this OTP to reset your password.");

        Transport.send(message);
    }
}