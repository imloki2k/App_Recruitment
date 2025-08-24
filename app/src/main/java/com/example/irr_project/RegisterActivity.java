package com.example.irr_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextName, editTextUniversityOrCompany;
    private RadioGroup radioGroupRole;
    private Button buttonRegister;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // Set custom title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Đăng ký tài khoản"); // or "Register" for RegisterActivity
        }
        // Initialize UI components
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextUniversityOrCompany = findViewById(R.id.editTextUniversityOrCompany);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase(); // Ensure database is created

        // Set up register button click listener
        buttonRegister.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String name = editTextName.getText().toString().trim();
            String universityOrCompany = editTextUniversityOrCompany.getText().toString().trim();
            int selectedRoleId = radioGroupRole.getCheckedRadioButtonId();
            RadioButton selectedRole = findViewById(selectedRoleId);

            // Validate inputs
            if (!validateInputs(email, password, name, universityOrCompany, selectedRole)) {
                return;
            }

            // Insert user into database
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(dbHelper.COL_EMAIL, email);
            values.put(dbHelper.COL_PASSWORD, password); // In production, hash password
            values.put(dbHelper.COL_ROLE, selectedRole.getText().toString().toLowerCase());
            values.put(dbHelper.COL_NAME, name);
            if (selectedRole.getText().toString().equalsIgnoreCase("student")) {
                values.put(dbHelper.COL_UNIVERSITY, universityOrCompany.isEmpty() ? null : universityOrCompany);
                values.putNull(dbHelper.COL_COMPANY);
            } else {
                values.put(dbHelper.COL_COMPANY, universityOrCompany.isEmpty() ? null : universityOrCompany);
                values.putNull(dbHelper.COL_UNIVERSITY);
            }

            long result = db.insert(dbHelper.TABLE_USERS, null, values);
            db.close();
            if (result != -1) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                finish(); // Return to LoginActivity
            } else {
                Toast.makeText(this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String email, String password, String name, String universityOrCompany, RadioButton role) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            return false;
        }
        if (name.isEmpty()) {
            editTextName.setError("Enter your name");
            return false;
        }
        if (universityOrCompany.isEmpty()) {
            editTextUniversityOrCompany.setError("Enter university or company");
            return false;
        }
        if (role == null) {
            Toast.makeText(this, "Please select a role", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}