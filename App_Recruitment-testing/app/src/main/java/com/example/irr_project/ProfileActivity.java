package com.example.irr_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewName, textViewEmail, textViewRole, textViewUniversity, textViewCompany;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        textViewRole = findViewById(R.id.textViewRole);
        textViewUniversity = findViewById(R.id.textViewUniversity);
        textViewCompany = findViewById(R.id.textViewCompany);
        dbHelper = new DatabaseHelper(this);

        // Lấy thông tin người dùng (giả lập)
        loadUserProfile();
    }

    private void loadUserProfile() {
        String email = getCurrentEmail(); // Thay bằng logic thực tế
        String role = dbHelper.getUserRole(email, "pass123"); // Thay password thực tế
        String name = "Nguyen Van A"; // Lấy từ database
        String university = "Hanoi University"; // Lấy từ database
        String company = null; // Lấy từ database, null nếu là student

        textViewName.setText("Name: " + name);
        textViewEmail.setText("Email: " + email);
        textViewRole.setText("Role: " + role);
        textViewUniversity.setText("University: " + (university != null ? university : "N/A"));
        textViewCompany.setText("Company: " + (company != null ? company : "N/A"));
    }

    // Phương thức giả lập để lấy email hiện tại
    private String getCurrentEmail() {
        return "student1@example.com"; // Thay bằng logic thực tế
    }
}