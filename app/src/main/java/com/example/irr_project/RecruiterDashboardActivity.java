package com.example.irr_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import com.example.irr_project.Internship;
import java.util.ArrayList;
import java.util.List;

public class RecruiterDashboardActivity extends AppCompatActivity {

    private ListView listViewInternships;
    private DatabaseHelper dbHelper;
    private List<Internship> internshipList;
    private InternshipAdapter internshipAdapter;
    private int companyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_dashboard);

        // Initialize UI components
        listViewInternships = findViewById(R.id.listViewInternships);
        Button buttonCreateInternship = findViewById(R.id.buttonCreateInternship);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        // Get current company ID (placeholder logic)
        companyId = getCurrentUserId(); // Replace with real implementation

        // Load internships
        loadInternships();

        // Set up Create button
        buttonCreateInternship.setOnClickListener(v -> {
            Intent intent = new Intent(RecruiterDashboardActivity.this, CreateInternshipActivity.class);
            startActivity(intent);
        });

        // Set up ListView item click for edit
        listViewInternships.setOnItemClickListener((parent, view, position, id) -> {
            Internship internship = internshipList.get(position);
            Intent intent = new Intent(RecruiterDashboardActivity.this, EditInternshipActivity.class);
            intent.putExtra("internshipId", internship.getId());
            startActivity(intent);
        });
    }

    private void loadInternships() {
        internshipList = dbHelper.getInternshipsByCompanyId(companyId);
        if (internshipList == null || internshipList.isEmpty()) {
            Toast.makeText(this, "No internships available", Toast.LENGTH_SHORT).show();
            internshipList = new ArrayList<>();
        }
        internshipAdapter = new InternshipAdapter(this, R.layout.internship_item, internshipList);
        listViewInternships.setAdapter(internshipAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInternships(); // Refresh list when returning
    }

    // Placeholder method to get current user ID
    private int getCurrentUserId() {
        // This should fetch the logged-in recruiter's user ID from shared preferences or intent
        return 2; // Replace with real implementation
    }

    // Custom ArrayAdapter for ListView
    public static class InternshipAdapter extends ArrayAdapter<Internship> {
        private List<Internship> internshipList;
        private Context context;

        public InternshipAdapter(Context context, int resource, List<Internship> internshipList) {
            super(context, resource, internshipList);
            this.context = context;
            this.internshipList = internshipList != null ? internshipList : new ArrayList<>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.internship_item, parent, false);
            }

            Internship internship = getItem(position);
            if (internship != null) {
                TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
                TextView textViewCompany = convertView.findViewById(R.id.textViewCompany);
                TextView textViewLocation = convertView.findViewById(R.id.textViewLocation);
                TextView textViewDuration = convertView.findViewById(R.id.textViewDuration);

                if (textViewTitle != null) {
                    textViewTitle.setText(internship.getTitle());
                }
                if (textViewCompany != null) {
                    textViewCompany.setText("Company: " + internship.getCompany());
                }
                if (textViewLocation != null) {
                    textViewLocation.setText("Location: " + internship.getLocation());
                }
                if (textViewDuration != null) {
                    textViewDuration.setText("Duration: " + internship.getDuration());
                }
            }

            return convertView;
        }
    }
}