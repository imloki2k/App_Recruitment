package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        dbHelper.getWritableDatabase();

        // Set up login button click listener
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            // Validate inputs
            if (!validateInputs(email, password)) {
                return;
            }

            // Check credentials and get role from database
            String role = dbHelper.getUserRole(email, password);
            if (role != null) {
                // Get userId from database
                int userId = getUserIdFromEmail(email);
                if (userId != -1) {
                    // Save userId to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("userId", userId);
                    editor.apply();

                    Toast.makeText(this, "Login successful as " + role, Toast.LENGTH_SHORT).show();
                    // Navigate to appropriate dashboard with Intent extras
                    Intent intent;
                    int roleInt = "student".equals(role) ? 0 : 1;
                    if ("student".equals(role)) {
                        intent = new Intent(LoginActivity.this, InternshipListingsActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, RecruiterDashboardActivity.class);
                    }
                    intent.putExtra("userId", userId);
                    intent.putExtra("role", roleInt);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                }
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
        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }

    // Method to get userId from email
    private int getUserIdFromEmail(String email) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] columns = {DatabaseHelper.COL_USER_ID};
        String selection = DatabaseHelper.COL_EMAIL + "=?";
        String[] selectionArgs = {email};
        Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
        }
        cursor.close();
        return userId;
    }
}