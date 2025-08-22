package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class RecruiterApplicationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = new ListView(this);
        setContentView(listView);

        int companyId = getSharedPreferences("session", MODE_PRIVATE).getInt("user_id", -1);
        DatabaseHelper db = new DatabaseHelper(this);
        List<DatabaseHelper.ApplicationDTO> apps = db.getApplicationsForCompany(companyId);

        List<String> display = new ArrayList<>();
        for (DatabaseHelper.ApplicationDTO a : apps) {
            display.add("#" + a.applicationId + " - " + a.internshipTitle + " - " + a.studentName + " - " + a.status);
        }
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, display));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            DatabaseHelper.ApplicationDTO a = apps.get(position);
            Intent intent = new Intent(this, InterviewScheduleActivity.class);
            intent.putExtra("applicationId", a.applicationId);
            intent.putExtra("studentId", a.studentId);
            intent.putExtra("companyId", companyId);
            startActivity(intent);
        });
    }
}

