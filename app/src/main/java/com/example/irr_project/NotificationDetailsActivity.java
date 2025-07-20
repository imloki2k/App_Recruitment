package com.example.irr_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class NotificationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_notification_details);

        TextView textViewNotifications = findViewById(R.id.textViewNotifications);
        ArrayList<String> notifications = getIntent().getStringArrayListExtra("notifications");

        if (notifications != null) {
            StringBuilder message = new StringBuilder("Thông báo:\n");
            for (String notification : notifications) {
                message.append("- ").append(notification).append("\n");
            }
            textViewNotifications.setText(message.toString());
        } else {
            textViewNotifications.setText("Không có thông báo nào.");
        }
    }
}