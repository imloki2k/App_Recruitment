package com.example.irr_project;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;

public class InterviewScheduleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_schedule);

        EditText input = findViewById(R.id.inputTimeEditText);
        Button confirm = findViewById(R.id.confirmTimeButton);
        Button decline = findViewById(R.id.declineTimeButton);

        DatabaseHelper db = new DatabaseHelper(this);
        int applicationId = getIntent().getIntExtra("applicationId", -1);
        int studentId = getIntent().getIntExtra("studentId", -1);
        int companyId = getIntent().getIntExtra("companyId", -1);

        confirm.setOnClickListener(v -> {
            String time = input.getText().toString().trim();
            if (time.isEmpty()) {
                Toast.makeText(this, "Enter time", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = db.createInterviewProposal(applicationId, studentId, companyId, time);
            if (id != -1) {
                db.createNotification(studentId, "Interview proposed at " + time, "InterviewInvite");
                db.createNotification(companyId, "Interview proposed at " + time, "InterviewInvite");
                Toast.makeText(this, "Interview proposed successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK); // Indicate success
                finish();
            } else {
                Toast.makeText(this, "Failed to create interview", Toast.LENGTH_SHORT).show();
            }
        });

        decline.setOnClickListener(v -> {
            Toast.makeText(this, "Declined (no meeting created)", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}

