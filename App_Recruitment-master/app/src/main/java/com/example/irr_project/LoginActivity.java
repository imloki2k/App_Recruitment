package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase(); // Ensure database is created

        // Set up login button click listener
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate inputs
            if (!validateInputs(email, password)) {
                return;
            }

            // Check credentials and get user info from database
            DatabaseHelper.AuthUser auth = dbHelper.getAuthUser(email, password);
            if (auth != null) {
                // Persist session
                getSharedPreferences("session", MODE_PRIVATE)
                        .edit()
                        .putInt("user_id", auth.userId)
                        .putString("role", auth.role)
                        .putString("name", auth.name)
                        .apply();

                Toast.makeText(this, "Login successful as " + auth.role, Toast.LENGTH_SHORT).show();
                // Navigate to appropriate dashboard
                Intent intent;
                if (auth.role.equals("student")) {
                    intent = new Intent(LoginActivity.this, InternshipListingsActivity.class);
                } else {
                    intent = new Intent(LoginActivity.this, RecruiterDashboardActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            return false;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}