package com.example.irr_project;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import com.example.irr_project.InternshipListingsActivity.Internship;

public class InternshipDetailsActivity extends AppCompatActivity {

    private TextView textViewTitle, textViewCompany, textViewDescription, textViewRequirements, textViewStipend, textViewDeadline, textViewStatus;
    private EditText editTextResume;
    private Button buttonApply;
    private DatabaseHelper dbHelper;
    private Internship internship;
    private int internshipId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_details);

        // Initialize UI components
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewCompany = findViewById(R.id.textViewCompany);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewRequirements = findViewById(R.id.textViewRequirements);
        textViewStipend = findViewById(R.id.textViewStipend);
        textViewDeadline = findViewById(R.id.textViewDeadline);
        textViewStatus = findViewById(R.id.textViewStatus);
        editTextResume = findViewById(R.id.editTextResume);
        buttonApply = findViewById(R.id.buttonApply);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        // Get internship data from intent
        internshipId = getIntent().getIntExtra("internshipId", -1);
        if (internshipId != -1) {
            internship = dbHelper.getInternshipById(internshipId);
            if (internship != null) {
                displayInternshipDetails();
                checkApplicationStatus();
            } else {
                Toast.makeText(this, "Internship not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Set up apply button
        buttonApply.setOnClickListener(v -> {
            String resume = editTextResume.getText().toString().trim();
            if (resume.isEmpty()) {
                editTextResume.setError("Please enter your resume");
                return;
            }
            applyForInternship(resume);
        });
    }

    private void displayInternshipDetails() {
        textViewTitle.setText(internship.getTitle());
        textViewCompany.setText("Company: " + internship.getCompany());
        textViewDescription.setText("Description: " + getDescription(internshipId)); // Placeholder
        textViewRequirements.setText("Requirements: " + getRequirements(internshipId)); // Placeholder
        textViewStipend.setText("Stipend: " + getStipend(internshipId)); // Placeholder
        textViewDeadline.setText("Deadline: " + getDeadline(internshipId)); // Placeholder
    }

    private void checkApplicationStatus() {
        // Placeholder: Check if user has applied (requires user ID)
        textViewStatus.setText("Status: Not Applied");
        // Add logic to query applications table with user ID
    }

    private void applyForInternship(String resume) {
        // Placeholder: Insert into applications table (requires user ID)
        Toast.makeText(this, "Application submitted with resume: " + resume, Toast.LENGTH_SHORT).show();
        textViewStatus.setText("Status: Pending");
        buttonApply.setEnabled(false); // Disable after applying
    }

    // Placeholder methods to fetch additional details (to be implemented in DatabaseHelper)
    private String getDescription(int id) { return "Sample description"; }
    private String getRequirements(int id) { return "Sample requirements"; }
    private String getStipend(int id) { return "Sample stipend"; }
    private String getDeadline(int id) { return "Sample deadline"; }

    // Placeholder method to get internship by ID
    private Internship getInternshipById(int id) {
        // Implement query to get full internship details
        return new Internship("Sample Title", "Sample Company", "Sample Location", "Sample Duration", "Sample Field", "Sample Date");
    }
}