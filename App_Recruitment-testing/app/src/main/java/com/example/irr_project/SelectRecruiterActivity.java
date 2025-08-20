package com.example.irr_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class SelectRecruiterActivity extends AppCompatActivity {
    private ListView listViewRecruiters;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recruiterDisplayNames = new ArrayList<>();
    private ArrayList<Integer> recruiterIds = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recruiter);
        getSupportActionBar().hide();
        listViewRecruiters = findViewById(R.id.listViewRecruiters);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recruiterDisplayNames);
        listViewRecruiters.setAdapter(adapter);

        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        loadRecruitersFromDatabase();

        listViewRecruiters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int recruiterId = recruiterIds.get(position);
                String recruiterDisplayName = recruiterDisplayNames.get(position);
                Intent intent = new Intent(SelectRecruiterActivity.this, ChatActivity.class);
                intent.putExtra("recruiterId", String.valueOf(recruiterId));
                intent.putExtra("recruiterName", recruiterDisplayName);
                startActivity(intent);
            }
        });

        // Xử lý nút back
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadRecruitersFromDatabase() {
        recruiterDisplayNames.clear();
        recruiterIds.clear();
        
        List<DatabaseHelper.Recruiter> recruiters = dbHelper.getAllRecruiters();
        
        for (DatabaseHelper.Recruiter recruiter : recruiters) {
            String displayName = recruiter.getEmail() + " - " + recruiter.getName() + " (" + recruiter.getCompany() + ")";
            recruiterDisplayNames.add(displayName);
            recruiterIds.add(recruiter.getId());
        }
        
        adapter.notifyDataSetChanged();
    }
} 