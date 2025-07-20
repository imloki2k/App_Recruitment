package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MyApplicationsActivity extends AppCompatActivity implements ApplicationAdapter.OnApplicationActionListener {

    private static final String TAG = "MyApplicationsActivity";
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private static final String KEY_USER_ROLE = "logged_user_role";

    private RecyclerView recyclerViewApplications;
    private ApplicationAdapter applicationAdapter;
    private DatabaseHelper dbHelper;
    private List<Application> applicationList;
    private TextView tvNoApplications;
    private int currentStudentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // Lấy ID sinh viên và vai trò từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        currentStudentId = sharedPreferences.getInt(KEY_USER_ID, -1);
        String userRole = sharedPreferences.getString(KEY_USER_ROLE, null);
        if (currentStudentId == -1 || !"student".equals(userRole)) {
            Toast.makeText(this, "Vui lòng đăng nhập lại với vai trò sinh viên.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Không tìm thấy studentId hoặc vai trò không phải sinh viên. UserId: " + currentStudentId + ", Role: " + userRole);
            finish();
            return;
        }

        // Khởi tạo cơ sở dữ liệu và giao diện
        dbHelper = new DatabaseHelper(this);
        applicationList = new ArrayList<>();
        tvNoApplications = findViewById(R.id.tvNoApplications);
        recyclerViewApplications = findViewById(R.id.recyclerViewMyApplications);
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(this));
        applicationAdapter = new ApplicationAdapter(this, applicationList, this, "student");
        recyclerViewApplications.setAdapter(applicationAdapter);

        // Tải danh sách đơn ứng tuyển
        loadApplications();
    }

    private void loadApplications() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Application> resultApplications = dbHelper.getApplicationsByStudentId(currentStudentId);
            runOnUiThread(() -> {
                applicationList.clear();
                if (resultApplications != null && !resultApplications.isEmpty()) {
                    applicationList.addAll(resultApplications);
                    tvNoApplications.setVisibility(View.GONE);
                    recyclerViewApplications.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Loaded " + resultApplications.size() + " applications for student ID: " + currentStudentId);
                } else {
                    tvNoApplications.setVisibility(View.VISIBLE);
                    recyclerViewApplications.setVisibility(View.GONE);
                    Log.d(TAG, "No applications found for student ID: " + currentStudentId);
                }
                applicationAdapter.notifyDataSetChanged();
            });
        });
    }

    @Override
    public void onWithdrawClick(Application application, int position) {
        String currentStatus = application.getStatus();
        if (currentStatus.equals(Application.Status.WITHDRAWN.toString()) ||
                currentStatus.equals(Application.Status.REJECTED.toString())) {
            Toast.makeText(this, "Không thể rút đơn đã bị từ chối hoặc đã rút.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Cannot withdraw application ID: " + application.getApplicationId() + ", current status: " + currentStatus);
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận rút đơn")
                .setMessage("Bạn có chắc chắn muốn rút đơn ứng tuyển cho vị trí '" + application.getInternshipTitle() + "' không?")
                .setPositiveButton("Rút đơn", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        boolean success = dbHelper.updateApplicationStatus(application.getApplicationId(), Application.Status.WITHDRAWN.toString());
                        runOnUiThread(() -> {
                            if (success) {
                                Toast.makeText(this, "Đã rút đơn ứng tuyển.", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "Successfully withdrew application ID: " + application.getApplicationId());
                                dbHelper.insertNotification(
                                        dbHelper.getCompanyIdFromInternship(application.getInternshipId()),
                                        "Sinh viên đã rút đơn ứng tuyển cho " + application.getInternshipTitle(),
                                        "ApplicationUpdate"
                                );
                                loadApplications();
                            } else {
                                Toast.makeText(this, "Lỗi: Không thể rút đơn.", Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Failed to withdraw application ID: " + application.getApplicationId());
                            }
                        });
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onScheduleInterviewClick(Application application, int position) {
        if (application.getStatus().equals(Application.Status.ACCEPTED.toString())) {
            Intent intent = new Intent(this, InterviewSchedulingActivity.class);
            intent.putExtra("application_id", application.getApplicationId());
            intent.putExtra("student_id", currentStudentId);
            startActivity(intent);
            Log.d(TAG, "Navigating to InterviewSchedulingActivity for application ID: " + application.getApplicationId());
        } else {
            Toast.makeText(this, "Chỉ có thể lập lịch phỏng vấn cho đơn đã được chấp nhận.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Cannot schedule interview for application ID: " + application.getApplicationId() + ", status: " + application.getStatus());
        }
    }

    @Override
    public void onStatusChange(Application application, int position, String newStatus) {
        Toast.makeText(this, "Sinh viên không thể thay đổi trạng thái đơn.", Toast.LENGTH_SHORT).show();
        Log.w(TAG, "Student attempted to change status for application ID: " + application.getApplicationId());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}