package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;

public class EditInternshipActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextLocation, editTextDuration, editTextField, editTextDescription,
            editTextRequirements, editTextStipend, editTextDeadline;
    private DatabaseHelper dbHelper;
    private int internshipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_internship);

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

        // Get internship ID from intent
        internshipId = getIntent().getIntExtra("internshipId", -1);
        if (internshipId == -1) {
            Toast.makeText(this, "Invalid internship", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load internship data
        loadInternshipData();

        // Set up Update button
        Button buttonUpdateInternship = findViewById(R.id.buttonUpdateInternship);
        buttonUpdateInternship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInternship();
            }
        });
    }

    private void loadInternshipData() {
        Internship internship = dbHelper.getInternshipById(internshipId);
        if (internship != null) {
            editTextTitle.setText(internship.getTitle());
            editTextLocation.setText(internship.getLocation());
            editTextDuration.setText(internship.getDuration());
            editTextField.setText(internship.getField());
            editTextDescription.setText(internship.getDescription());
            editTextRequirements.setText(internship.getRequirements());
            editTextStipend.setText(internship.getStipend());
            editTextDeadline.setText(internship.getDeadline());
        } else {
            Toast.makeText(this, "Internship not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateInternship() {
        String title = editTextTitle.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String duration = editTextDuration.getText().toString().trim();
        String field = editTextField.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String requirements = editTextRequirements.getText().toString().trim();
        String stipend = editTextStipend.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || duration.isEmpty() || field.isEmpty() ||
                description.isEmpty() || requirements.isEmpty() || stipend.isEmpty() || deadline.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isUpdated = dbHelper.updateInternship(internshipId, title, location, duration, field,
                description, requirements, stipend, deadline);

        if (isUpdated) {
            Toast.makeText(this, "Internship updated successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditInternshipActivity.this, RecruiterDashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Failed to update internship", Toast.LENGTH_SHORT).show();
        }
    }
}