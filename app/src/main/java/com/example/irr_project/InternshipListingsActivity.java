package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class InternshipListingsActivity extends AppCompatActivity {

    private static final String TAG = "InternshipListingsActivity";
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private RecyclerView recyclerViewInternships;
    private InternshipAdapter internshipAdapter;
    private DatabaseHelper dbHelper;
    private List<Internship> internshipList;
    private Spinner spinnerFilter;
    private CheckBox checkBoxSortByDate;
    private int currentStudentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_internship_listings);

            // Khởi tạo giao diện
            recyclerViewInternships = findViewById(R.id.recyclerViewInternships);
            spinnerFilter = findViewById(R.id.spinnerFilter);
            checkBoxSortByDate = findViewById(R.id.checkBoxSortByDate);
            findViewById(R.id.buttonApplications).setOnClickListener(v -> goToMyApplications());

            if (recyclerViewInternships == null || spinnerFilter == null || checkBoxSortByDate == null) {
                Log.e(TAG, "Không tìm thấy một hoặc nhiều thành phần giao diện");
                Toast.makeText(this, "Lỗi giao diện: Không tìm thấy thành phần", Toast.LENGTH_LONG).show();
                return;
            }

            // Lấy userId từ SharedPreferences
            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
            currentStudentId = prefs.getInt(KEY_USER_ID, -1);
            if (currentStudentId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Khởi tạo DatabaseHelper
            dbHelper = new DatabaseHelper(this);
            internshipList = new ArrayList<>();
            recyclerViewInternships.setLayoutManager(new LinearLayoutManager(this));
            internshipAdapter = new InternshipAdapter(this, internshipList, internship -> {
                Intent intent = new Intent(this, InternshipDetailsActivity.class);
                intent.putExtra("internshipId", internship.getId());
                intent.putExtra("userId", currentStudentId);
                startActivity(intent);
            });
            recyclerViewInternships.setAdapter(internshipAdapter);

            // Thiết lập Spinner
            String[] fields = {"Tất cả", "IT", "Marketing", "Data Science"};
            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fields);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerFilter.setAdapter(spinnerAdapter);

            // Xử lý sự kiện chọn Spinner
            spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filterInternships();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            // Xử lý sự kiện CheckBox sắp xếp
            checkBoxSortByDate.setOnCheckedChangeListener((buttonView, isChecked) -> filterInternships());

            // Tải danh sách thực tập
            filterInternships();
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khởi tạo InternshipListingsActivity: ", e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void filterInternships() {
        try {
            String selectedField = spinnerFilter.getSelectedItem() != null ? spinnerFilter.getSelectedItem().toString() : "Tất cả";
            boolean sortByDate = checkBoxSortByDate.isChecked();
            String field = selectedField.equals("Tất cả") ? null : selectedField;

            Executors.newSingleThreadExecutor().execute(() -> {
                List<Internship> resultInternships = dbHelper.getAllInternships(field, sortByDate);
                runOnUiThread(() -> {
                    try {
                        internshipList.clear();
                        if (resultInternships != null && !resultInternships.isEmpty()) {
                            internshipList.addAll(resultInternships);
                        } else {
                            Toast.makeText(this, "Không có thực tập nào.", Toast.LENGTH_SHORT).show();
                        }
                        internshipAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi khi tải danh sách thực tập: ", e);
                        Toast.makeText(this, "Lỗi tải danh sách thực tập: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lọc thực tập: ", e);
            Toast.makeText(this, "Lỗi lọc thực tập: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void goToMyApplications() {
        try {
            Intent intent = new Intent(this, MyApplicationsActivity.class);
            intent.putExtra("userId", currentStudentId);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi chuyển đến MyApplicationsActivity: ", e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}