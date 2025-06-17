package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private RadioGroup roleRadioGroup;
    private Button loginButton;
    private TextView signUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view từ XML
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        loginButton = findViewById(R.id.loginButton);
        signUpTextView = findViewById(R.id.goToSignUp);

        // Xử lý nút login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            int selectedId = roleRadioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRole = findViewById(selectedId);
            String role = selectedRole.getText().toString();

            // Giả lập đăng nhập thành công
            Toast.makeText(this, "Logged in as " + role, Toast.LENGTH_SHORT).show();
        });

        // Chuyển sang màn đăng ký
        signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
