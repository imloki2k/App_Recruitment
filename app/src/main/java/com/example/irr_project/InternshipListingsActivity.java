package com.example.irr_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_listings);

        // Initialize UI components
        listViewInternships = findViewById(R.id.listViewInternships);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        checkBoxSortByDate = findViewById(R.id.checkBoxSortByDate);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        // Set up ListView
        internshipList = dbHelper.getAllInternships();
        if (internshipList == null) {
            internshipList = new ArrayList<>();
            Toast.makeText(this, "No internships available", Toast.LENGTH_SHORT).show();
        }
        internshipAdapter = new InternshipAdapter(this, R.layout.internship_item, internshipList);
        listViewInternships.setAdapter(internshipAdapter);

        // Set up Spinner for filtering
        String[] fields = {"All", "IT", "Marketing", "Data Science"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fields);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterInternships();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set up CheckBox for sorting
        checkBoxSortByDate.setOnCheckedChangeListener((buttonView, isChecked) -> filterInternships());

        // Set up ListView item click listener
        listViewInternships.setOnItemClickListener((parent, view, position, id) -> {
            Internship internship = internshipList.get(position);
            Intent intent = new Intent(InternshipListingsActivity.this, InternshipDetailsActivity.class);
            intent.putExtra("internshipId", internship.getId()); // Use actual ID
            startActivity(intent);
        });
    }

    private void filterInternships() {
        String selectedField = spinnerFilter.getSelectedItem().toString();
        boolean sortByDate = checkBoxSortByDate.isChecked();

        List<Internship> filteredList = new ArrayList<>();
        for (Internship internship : internshipList) {
            if (selectedField.equals("All") || internship.getField().equals(selectedField)) {
                filteredList.add(internship);
            }
        }

        if (sortByDate) {
            Collections.sort(filteredList, new Comparator<Internship>() {
                @Override
                public int compare(Internship i1, Internship i2) {
                    return i2.getDatePosted().compareTo(i1.getDatePosted()); // Newest first
                }
            });
        }

        internshipAdapter.clear();
        internshipAdapter.addAll(filteredList);
        internshipAdapter.notifyDataSetChanged();
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
                    textViewTitle.setText(internship.getTitle() != null ? internship.getTitle() : "No Title");
                }
                if (textViewCompany != null) {
                    textViewCompany.setText("Company: " + (internship.getCompany() != null ? internship.getCompany() : "Unknown"));
                }
                if (textViewLocation != null) {
                    textViewLocation.setText("Location: " + (internship.getLocation() != null ? internship.getLocation() : "Unknown"));
                }
                if (textViewDuration != null) {
                    textViewDuration.setText("Duration: " + (internship.getDuration() != null ? internship.getDuration() : "Unknown"));
                }
            }

            return convertView;
        }
    }
}