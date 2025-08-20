package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.example.irr_project.database.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private static final String KEY_USER_ROLE = "logged_user_role";

    private DatabaseHelper dbHelper;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        dbHelper = new DatabaseHelper(this);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        int userId = sharedPreferences.getInt(KEY_USER_ID, -1);
        String userRole = sharedPreferences.getString(KEY_USER_ROLE, null);

        if (userId == -1 || userRole == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        checkNotifications(userId, userRole);
    }

    private void checkNotifications(int userId, String userRole) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<String> notifications = dbHelper.getUnreadNotifications(userId);
            runOnUiThread(() -> {
                if (!notifications.isEmpty()) {
                    String message = "Bạn có " + notifications.size() + " thông báo mới: " + notifications.get(0);
                    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                            .setAction("Xem", v -> {
                                dbHelper.markNotificationsAsRead(userId);
                                Intent intent = new Intent(this, userRole.equals("student") ? MyApplicationsActivity.class : RecruiterDashboardActivity.class);
                                startActivity(intent);
                            })
                            .show();
                }
            });
        });
    }
}