package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RecruiterDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button button = new Button(this);
        button.setText("Notifications");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecruiterDashboardActivity.this, NotificationsActivity.class));
            }
        });

        Button appsButton = new Button(this);
        appsButton.setText("Applications");
        appsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecruiterDashboardActivity.this, RecruiterApplicationsActivity.class));
            }
        });

        Button interviewsButton = new Button(this);
        interviewsButton.setText("Interviews");
        interviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecruiterDashboardActivity.this, InterviewListActivity.class));
            }
        });

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.addView(button);
        layout.addView(appsButton);
        layout.addView(interviewsButton);
        setContentView(layout);
    }
}
