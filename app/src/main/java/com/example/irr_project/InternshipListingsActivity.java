package com.example.irr_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import com.example.irr_project.Internship;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InternshipListingsActivity extends AppCompatActivity {

    private ListView listViewInternships;
    private InternshipAdapter internshipAdapter;
    private DatabaseHelper dbHelper;
    private Spinner spinnerFilter;
    private CheckBox checkBoxSortByDate;
    private List<Internship> internshipList;
    private static final String TAG = "InternshipListings";
    private int currentUserId; // Thêm để lưu ID người dùng hiện tại
    private int currentRole;   // Thêm để lưu vai trò (0: Student, 1: Recruiter)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_listings);

        // Initialize UI components
        listViewInternships = findViewById(R.id.listViewInternships);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        checkBoxSortByDate = findViewById(R.id.checkBoxSortByDate);
        Button scheduleInterviewButton = findViewById(R.id.buttonScheduleInterview); // Thêm nút

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        // Lấy currentUserId và currentRole (giả định từ Intent hoặc session)
        currentUserId = getIntent().getIntExtra("userId", -1); // Thay bằng logic thực tế
        currentRole = getIntent().getIntExtra("role", -1);     // Thay bằng logic thực tế
        if (currentUserId == -1 || currentRole == -1) {
            Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load internships
        internshipList = dbHelper.getAllInternships();
        if (internshipList == null || internshipList.isEmpty()) {
            internshipList = new ArrayList<>();
            Toast.makeText(this, "No internships available", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Loaded " + internshipList.size() + " internships");
            for (Internship i : internshipList) {
                Log.d(TAG, "Internship: " + i.getTitle() + ", Field: " + i.getField());
            }
        }
        internshipAdapter = new InternshipAdapter(this, R.layout.internship_item, new ArrayList<>(internshipList));
        listViewInternships.setAdapter(internshipAdapter);

        // Set up Spinner for filtering
        String[] fields = {"All", "IT", "Marketing", "Data Science"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fields);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateInternshipList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set up CheckBox for sorting
        checkBoxSortByDate.setOnCheckedChangeListener((buttonView, isChecked) -> updateInternshipList());

        // Set up ListView item click listener
        listViewInternships.setOnItemClickListener((parent, view, position, id) -> {
            Internship internship = internshipAdapter.getItem(position);
            if (internship != null) {
                Intent intent = new Intent(InternshipListingsActivity.this, InternshipDetailsActivity.class);
                intent.putExtra("internshipId", internship.getId());
                startActivity(intent);
            }
        });

        // Set up Schedule Interview button
        scheduleInterviewButton.setOnClickListener(v -> {
            if (internshipAdapter.getCount() > 0) {
                Internship selectedInternship = internshipAdapter.getItem(0); // Giả định chọn internship đầu tiên
                Intent intent = new Intent(InternshipListingsActivity.this, InterviewSchedulingActivity.class);
                intent.putExtra("userId", currentUserId);
                intent.putExtra("role", currentRole);
                intent.putExtra("internshipId", selectedInternship.getId());
                if (currentRole == 0) {
                    // Cần lấy interviewId từ database (giả định logic đơn giản)
                    int interviewId = getInterviewIdForUser(selectedInternship.getId());
                    if (interviewId != -1) intent.putExtra("interviewId", interviewId);
                } else if (currentRole == 1) {
                    // Cần lấy applicationId từ database (giả định logic đơn giản)
                    int applicationId = getApplicationIdForUser(selectedInternship.getId());
                    if (applicationId != -1) intent.putExtra("applicationId", applicationId);
                }
                startActivity(intent);
            } else {
                Toast.makeText(this, "No internships to schedule", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInternshipList() {
        List<Internship> filteredList = new ArrayList<>(internshipList); // Copy original list
        String selectedField = spinnerFilter.getSelectedItem().toString();
        boolean sortByDate = checkBoxSortByDate.isChecked();

        // Filter by field
        if (!selectedField.equals("All")) {
            filteredList.removeIf(internship -> !internship.getField().equals(selectedField));
        }

        // Sort by date if checked
        if (sortByDate) {
            Collections.sort(filteredList, new Comparator<Internship>() {
                @Override
                public int compare(Internship i1, Internship i2) {
                    if (i1.getDatePosted() == null || i2.getDatePosted() == null) return 0;
                    return i2.getDatePosted().compareTo(i1.getDatePosted()); // Newest first
                }
            });
        }

        // Update adapter
        internshipAdapter.clear();
        internshipAdapter.addAll(filteredList);
        internshipAdapter.notifyDataSetChanged();
        Log.d(TAG, "Updated list with " + filteredList.size() + " items, Field: " + selectedField + ", Sort: " + sortByDate);
    }

    // Placeholder methods to get interviewId or applicationId
    private int getInterviewIdForUser(int internshipId) {
        return dbHelper.getInterviewIdForUser(currentUserId, internshipId);
    }

    private int getApplicationIdForUser(int internshipId) {
        return dbHelper.getApplicationIdForUser(currentUserId, internshipId);
    }

    // Custom ArrayAdapter for ListView
    public static class InternshipAdapter extends ArrayAdapter<Internship> {
        private List<Internship> internshipList;
        private Context context;

        public InternshipAdapter(Context context, int resource, List<Internship> internshipList) {
            super(context, resource, internshipList);
            this.context = context;
            this.internshipList = internshipList != null ? new ArrayList<>(internshipList) : new ArrayList<>();
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

                if (textViewTitle != null) textViewTitle.setText(internship.getTitle() != null ? internship.getTitle() : "No Title");
                if (textViewCompany != null) textViewCompany.setText("Company: " + (internship.getCompany() != null ? internship.getCompany() : "Unknown"));
                if (textViewLocation != null) textViewLocation.setText("Location: " + (internship.getLocation() != null ? internship.getLocation() : "Unknown"));
                if (textViewDuration != null) textViewDuration.setText("Duration: " + (internship.getDuration() != null ? internship.getDuration() : "Unknown"));
            }

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}