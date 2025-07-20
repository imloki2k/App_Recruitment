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

public class SelectStudentActivity extends AppCompatActivity {
    private ListView listViewStudents;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> studentDisplayNames = new ArrayList<>();
    private ArrayList<Integer> studentIds = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_student);

        listViewStudents = findViewById(R.id.listViewStudents);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentDisplayNames);
        listViewStudents.setAdapter(adapter);

        // Xử lý nút back
        findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        loadStudentsFromDatabase();

        listViewStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int studentId = studentIds.get(position);
                String studentDisplayName = studentDisplayNames.get(position);
                Intent intent = new Intent(SelectStudentActivity.this, RecruiterChatActivity.class);
                intent.putExtra("studentId", String.valueOf(studentId));
                intent.putExtra("studentName", studentDisplayName);
                startActivity(intent);
            }
        });
    }

    private void loadStudentsFromDatabase() {
        studentDisplayNames.clear();
        studentIds.clear();
        
        List<DatabaseHelper.Student> students = dbHelper.getAllStudents();
        
        for (DatabaseHelper.Student student : students) {
            String displayName = student.getEmail() + " - " + student.getName() + " (" + student.getUniversity() + ")";
            studentDisplayNames.add(displayName);
            studentIds.add(student.getId());
        }
        
        adapter.notifyDataSetChanged();
    }
} 