package com.example.irr_project;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.app.ProgressDialog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.irr_project.database.DatabaseHelper;
import com.example.irr_project.utils.EmailSender;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPasswordActivity";
    private EditText editTextResetEmail, editTextVerificationCode, editTextNewPassword;
    private Button buttonSendVerification, buttonResetPassword;
    private LinearLayout layoutVerification;
    private DatabaseHelper dbHelper;
    private String verificationCode;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize UI components
        editTextResetEmail = findViewById(R.id.editTextResetEmail);
        editTextVerificationCode = findViewById(R.id.editTextVerificationCode);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSendVerification = findViewById(R.id.buttonSendVerification);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
        layoutVerification = findViewById(R.id.layoutVerification);

        dbHelper = new DatabaseHelper(this);

        // Send verification code button click handler
        buttonSendVerification.setOnClickListener(v -> {
            userEmail = editTextResetEmail.getText().toString().trim();
            if (validateEmail(userEmail)) {
                if (isEmailRegistered(userEmail)) {
                    sendVerificationCode(userEmail);
                    layoutVerification.setVisibility(View.VISIBLE);
                    buttonSendVerification.setText("Gửi lại mã");
                } else {
                    Toast.makeText(this, "Email không tồn tại trong hệ thống", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Reset password button click handler
        buttonResetPassword.setOnClickListener(v -> {
            String enteredCode = editTextVerificationCode.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();

            if (validateVerificationCode(enteredCode) && validateNewPassword(newPassword)) {
                if (resetPassword(userEmail, newPassword)) {
                    Toast.makeText(this, "Đặt lại mật khẩu thành công", Toast.LENGTH_SHORT).show();
                    finish(); // Return to login screen
                } else {
                    Toast.makeText(this, "Không thể đặt lại mật khẩu. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            editTextResetEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextResetEmail.setError("Vui lòng nhập email hợp lệ");
            return false;
        }
        return true;
    }

    private boolean isEmailRegistered(String email) {
        return dbHelper.isEmailRegistered(email);
    }

    private void sendVerificationCode(String email) {
        // Generate a random 6-digit verification code
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        verificationCode = String.valueOf(code);
        userEmail = email;

        // Show loading indicator
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang gửi mã xác nhận...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        EmailSender.sendVerificationEmail(email, verificationCode, new EmailSender.EmailCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Mã xác nhận đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();

                    // Show verification fields
                    layoutVerification.setVisibility(View.VISIBLE);
                    buttonSendVerification.setText("Gửi lại mã");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this,
                            "Không thể gửi email: " + error, Toast.LENGTH_SHORT).show();

                    // Fallback for testing - show code in dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
                    builder.setTitle("Mã xác nhận (Dự phòng)");
                    builder.setMessage("Không thể gửi email. Mã xác nhận của bạn là: " + verificationCode);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                    // Still show verification fields
                    layoutVerification.setVisibility(View.VISIBLE);
                    buttonSendVerification.setText("Gửi lại mã");
                });
            }
        });

        Log.d(TAG, "Verification code for " + email + ": " + verificationCode);
    }

    private boolean validateVerificationCode(String code) {
        if (code.isEmpty()) {
            editTextVerificationCode.setError("Vui lòng nhập mã xác nhận");
            return false;
        }
        if (!code.equals(verificationCode)) {
            editTextVerificationCode.setError("Mã xác nhận không chính xác");
            return false;
        }
        return true;
    }

    private boolean validateNewPassword(String password) {
        if (password.isEmpty()) {
            editTextNewPassword.setError("Vui lòng nhập mật khẩu mới");
            return false;
        }
        if (password.length() < 6) {
            editTextNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private boolean resetPassword(String email, String newPassword) {
        return dbHelper.updatePassword(email, newPassword);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}