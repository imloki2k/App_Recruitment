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
        DatabaseHelper.InternshipDetailsDTO dto = dbHelper.getInternshipDetailsById(internshipId);
        if (dto != null) {
            textViewDescription.setText("Description: " + (dto.description != null ? dto.description : "N/A"));
            textViewRequirements.setText("Requirements: " + (dto.requirements != null ? dto.requirements : "N/A"));
            textViewStipend.setText("Stipend: " + (dto.stipend != null ? dto.stipend : "N/A"));
            textViewDeadline.setText("Deadline: " + (dto.deadline != null ? dto.deadline : "N/A"));
        }
    }

    private void checkApplicationStatus() {
        int userId = getSharedPreferences("session", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            textViewStatus.setText("Status: Unknown");
            return;
        }
        boolean applied = dbHelper.hasApplication(userId, internshipId);
        if (applied) {
            textViewStatus.setText("Status: Pending");
            buttonApply.setEnabled(false);
        } else {
            textViewStatus.setText("Status: Not Applied");
            buttonApply.setEnabled(true);
        }
    }

    private void applyForInternship(String resume) {
        int userId = getSharedPreferences("session", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Please login again", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.hasApplication(userId, internshipId)) {
            Toast.makeText(this, "You have already applied", Toast.LENGTH_SHORT).show();
            return;
        }
        long appRow = dbHelper.insertApplication(userId, internshipId, resume, "Pending");
        if (appRow != -1) {
            textViewStatus.setText("Status: Pending");
            buttonApply.setEnabled(false);
            // Notify recruiter
            Integer companyId = dbHelper.getCompanyIdForInternship(internshipId);
            if (companyId != null) {
                dbHelper.createNotification(companyId, "New application received", "ApplicationUpdate");
            }
            Toast.makeText(this, "Application submitted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to submit application", Toast.LENGTH_SHORT).show();
        }
    }

    // Placeholder methods to fetch additional details (to be implemented in DatabaseHelper)
    private String getDescription(int id) { return null; }
    private String getRequirements(int id) { return null; }
    private String getStipend(int id) { return null; }
    private String getDeadline(int id) { return null; }

    // Placeholder method to get internship by ID
    private Internship getInternshipById(int id) { return null; }
}