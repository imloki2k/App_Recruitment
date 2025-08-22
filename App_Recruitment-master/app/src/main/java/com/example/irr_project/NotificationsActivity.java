package com.example.irr_project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        setContentView(listView);

        int userId = getSharedPreferences("session", MODE_PRIVATE).getInt("user_id", -1);
        DatabaseHelper db = new DatabaseHelper(this);
        List<DatabaseHelper.NotificationDTO> items = db.getNotificationsForUser(userId, false);
        List<String> display = new ArrayList<>();
        for (DatabaseHelper.NotificationDTO n : items) {
            display.add((n.isRead ? "[READ] " : "[NEW] ") + n.type + ": " + n.message);
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DatabaseHelper.NotificationDTO n = items.get(position);
            db.markNotificationAsRead(n.id);
        });
    }
}

