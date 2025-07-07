package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;

public class CreateInternshipActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextLocation, editTextDuration, editTextField, editTextDescription,
            editTextRequirements, editTextStipend, editTextDeadline;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_internship);

        // Initialize UI components
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextDuration = findViewById(R.id.editTextDuration);
        editTextField = findViewById(R.id.editTextField);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextRequirements = findViewById(R.id.editTextRequirements);
        editTextStipend = findViewById(R.id.editTextStipend);
        editTextDeadline = findViewById(R.id.editTextDeadline);

        dbHelper = new DatabaseHelper(this);

        // Set up Save button
        Button buttonSaveInternship = findViewById(R.id.buttonSaveInternship);
        buttonSaveInternship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInternship();
            }
        });
    }

    private void saveInternship() {
        String title = editTextTitle.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String duration = editTextDuration.getText().toString().trim();
        String field = editTextField.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String requirements = editTextRequirements.getText().toString().trim();
        String stipend = editTextStipend.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();

        // Get current recruiter (company) ID from login session (placeholder logic)
        int companyId = getCurrentUserId(); // Implement this method based on your login logic

        if (title.isEmpty() || location.isEmpty() || duration.isEmpty() || field.isEmpty() ||
                description.isEmpty() || requirements.isEmpty() || stipend.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Current date as date posted
        String datePosted = java.time.LocalDate.now().toString();

        // Save to database
        boolean isSaved = dbHelper.addInternship(companyId, title, location, duration, field, description,
                requirements, stipend, deadline, datePosted);

        if (isSaved) {
            Toast.makeText(this, "Internship created successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CreateInternshipActivity.this, RecruiterDashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to create internship", Toast.LENGTH_SHORT).show();
        }
    }

    // Placeholder method to get current user ID (replace with actual logic)
    private int getCurrentUserId() {
        // This should fetch the logged-in recruiter's user ID from shared preferences or intent
        return 2; // Replace with real implementation
    }
}