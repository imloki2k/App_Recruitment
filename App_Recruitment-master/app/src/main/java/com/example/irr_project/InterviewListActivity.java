package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class InterviewListActivity extends AppCompatActivity {
    private ListView listView;
    private DatabaseHelper dbHelper;
    private List<DatabaseHelper.InterviewDetailDTO> interviewList;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listView = new ListView(this);
        setContentView(listView);

        currentUserId = getSharedPreferences("session", MODE_PRIVATE).getInt("user_id", -1);
        dbHelper = new DatabaseHelper(this);
        
        loadInterviews();
        
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DatabaseHelper.InterviewDetailDTO interview = interviewList.get(position);
            showInterviewOptionsDialog(interview);
        });
    }

    private void loadInterviews() {
        interviewList = dbHelper.getInterviewDetailsForUser(currentUserId);
        List<String> display = new ArrayList<>();
        
        for (DatabaseHelper.InterviewDetailDTO interview : interviewList) {
            String statusColor = getStatusColor(interview.status);
            String displayText = String.format("%s [%s] - %s\n%s - %s", 
                interview.internshipTitle,
                statusColor,
                interview.status,
                interview.time,
                currentUserId == interview.studentId ? interview.companyName : interview.studentName
            );
            display.add(displayText);
        }
        
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display));
    }

    private String getStatusColor(String status) {
        switch (status) {
            case "Proposed": return "🟡";
            case "Confirmed": return "🟢";
            case "Declined": return "🔴";
            case "Completed": return "🔵";
            default: return "⚪";
        }
    }

    private void showInterviewOptionsDialog(DatabaseHelper.InterviewDetailDTO interview) {
        if (!dbHelper.canUserUpdateInterview(interview.id, currentUserId)) {
            Toast.makeText(this, "Cannot update this interview", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] options = getAvailableStatusOptions(interview.status, currentUserId == interview.studentId);
        
        new AlertDialog.Builder(this)
            .setTitle("Update Interview Status")
            .setItems(options, (dialog, which) -> {
                String newStatus = options[which];
                updateInterviewStatus(interview.id, newStatus);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private String[] getAvailableStatusOptions(String currentStatus, boolean isStudent) {
        List<String> options = new ArrayList<>();
        
        switch (currentStatus) {
            case "Proposed":
                if (isStudent) {
                    options.add("Confirm");
                    options.add("Decline");
                } else {
                    options.add("Cancel");
                }
                break;
            case "Confirmed":
                if (!isStudent) {
                    options.add("Mark as Completed");
                }
                break;
        }
        
        return options.toArray(new String[0]);
    }

    private void updateInterviewStatus(int interviewId, String action) {
        String newStatus;
        switch (action) {
            case "Confirm": newStatus = "Confirmed"; break;
            case "Decline": newStatus = "Declined"; break;
            case "Cancel": newStatus = "Declined"; break;
            case "Mark as Completed": newStatus = "Completed"; break;
            default: return;
        }

        boolean success = dbHelper.updateInterviewStatusWithNotification(interviewId, newStatus, currentUserId);
        if (success) {
            Toast.makeText(this, "Status updated to " + newStatus, Toast.LENGTH_SHORT).show();
            loadInterviews(); // Refresh the list
        } else {
            Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
        }
    }
}
