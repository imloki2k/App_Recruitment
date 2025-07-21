package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.irr_project.database.DatabaseHelper;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class RecruiterDashboardActivity extends AppCompatActivity {

    private static final String TAG = "RecruiterDashboardActivity";
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private static final String KEY_USER_ROLE = "logged_user_role";
    private RecyclerView recyclerViewApplications;
    private ApplicationAdapter applicationAdapter;
    private DatabaseHelper dbHelper;
    private List<Application> applicationList;
    private int companyId = -1;
    private TextView notificationBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable StrictMode to avoid hiddenapi issues on BlueStacks
        android.os.StrictMode.setThreadPolicy(new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build());
        getSupportActionBar().hide();
        setContentView(R.layout.activity_recruiter_dashboard);

        // Khởi tạo giao diện
        recyclerViewApplications = findViewById(R.id.recyclerViewApplications);
        notificationBadge = findViewById(R.id.notificationBadge);
        findViewById(R.id.buttonCreateInternship).setOnClickListener(v -> goToCreateInternship());
        findViewById(R.id.buttonLogout).setOnClickListener(v -> logout()); // Thêm nút Đăng xuất
        findViewById(R.id.buttonMessage).setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectStudentActivity.class);
            startActivity(intent);
        });

        if (recyclerViewApplications == null || notificationBadge == null) {
            Log.e(TAG, "Không tìm thấy RecyclerView hoặc notificationBadge");
            Toast.makeText(this, "Lỗi giao diện: Không tìm thấy thành phần", Toast.LENGTH_LONG).show();
            return;
        }

        // Lấy companyId và vai trò từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        companyId = prefs.getInt(KEY_USER_ID, -1);
        String userRole = prefs.getString(KEY_USER_ROLE, null);
        if (companyId == -1 || !"recruiter".equals(userRole)) {
            Toast.makeText(this, "Vui lòng đăng nhập lại với vai trò nhà tuyển dụng.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Không tìm thấy companyId hoặc vai trò không phải recruiter. CompanyId: " + companyId + ", Role: " + userRole);
            finish();
            return;
        }

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        applicationList = new ArrayList<>();
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(this));
        applicationAdapter = new ApplicationAdapter(this, applicationList, new ApplicationAdapter.OnApplicationActionListener() {
            @Override
            public void onWithdrawClick(Application application, int position) {
                Toast.makeText(RecruiterDashboardActivity.this, "Nhà tuyển dụng không thể rút đơn.", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Recruiter attempted to withdraw application ID: " + application.getApplicationId());
            }

            @Override
            public void onScheduleInterviewClick(Application application, int position) {
                if (application.getStatus().equals(Application.Status.ACCEPTED.toString())) {
                    Intent intent = new Intent(RecruiterDashboardActivity.this, InterviewSchedulingActivity.class);
                    intent.putExtra("application_id", application.getApplicationId());
                    intent.putExtra("student_id", application.getStudentId());
                    startActivity(intent);
                    Log.d(TAG, "Navigating to InterviewSchedulingActivity for application ID: " + application.getApplicationId() + ", student ID: " + application.getStudentId());
                } else {
                    Toast.makeText(RecruiterDashboardActivity.this, "Chỉ có thể lập lịch phỏng vấn cho đơn đã được chấp nhận.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Cannot schedule interview for application ID: " + application.getApplicationId() + ", status: " + application.getStatus());
                }
            }

            @Override
            public void onStatusChange(Application application, int position, String newStatus) {
                if (newStatus.equals(Application.Status.WITHDRAWN.toString())) {
                    Toast.makeText(RecruiterDashboardActivity.this, "Nhà tuyển dụng không thể đặt trạng thái 'Withdrawn'.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Recruiter attempted to set 'Withdrawn' status for application ID: " + application.getApplicationId());
                    return;
                }
                if (newStatus.equals(Application.Status.PENDING.toString())) {
                    Toast.makeText(RecruiterDashboardActivity.this, "Nhà tuyển dụng không thể đặt trạng thái 'Pending'.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Recruiter attempted to set 'Pending' status for application ID: " + application.getApplicationId());
                    return;
                }
                if (application.getStatus().equals(Application.Status.WITHDRAWN.toString())) {
                    Toast.makeText(RecruiterDashboardActivity.this, "Không thể thay đổi trạng thái đơn đã rút.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Cannot change status of withdrawn application ID: " + application.getApplicationId());
                    return;
                }
                Executors.newSingleThreadExecutor().execute(() -> {
                    boolean success = dbHelper.updateApplicationStatus(application.getApplicationId(), newStatus);
                    runOnUiThread(() -> {
                        if (success) {
                            Toast.makeText(RecruiterDashboardActivity.this, "Đã cập nhật trạng thái đơn thành " + newStatus, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Updated status to " + newStatus + " for application ID: " + application.getApplicationId());
                            loadApplications();
                        } else {
                            Toast.makeText(RecruiterDashboardActivity.this, "Lỗi: Không thể cập nhật trạng thái.", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to update status for application ID: " + application.getApplicationId());
                        }
                    });
                });
            }
        }, "recruiter");
        recyclerViewApplications.setAdapter(applicationAdapter);

        // Tải danh sách đơn ứng tuyển và kiểm tra thông báo
        loadApplications();
        checkNotifications();
        setupNotificationBadgeClick(); // Thêm sự kiện click cho badge
    }

    private void loadApplications() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Application> resultApplications = dbHelper.getApplicationsByCompanyId(companyId);
            runOnUiThread(() -> {
                applicationList.clear();
                if (resultApplications != null && !resultApplications.isEmpty()) {
                    applicationList.addAll(resultApplications);
                    Log.d(TAG, "Loaded " + resultApplications.size() + " applications for company ID: " + companyId);
                } else {
                    Toast.makeText(this, "Không có đơn ứng tuyển nào.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "No applications found for company ID: " + companyId);
                }
                applicationAdapter.notifyDataSetChanged();
            });
        });
    }

    private void goToCreateInternship() {
        Intent intent = new Intent(this, CreateInternshipActivity.class);
        intent.putExtra("userId", companyId);
        startActivity(intent);
    }

    private void checkNotifications() {
        if (companyId != -1) {
            Executors.newSingleThreadExecutor().execute(() -> {
                List<String> notifications = dbHelper.getAllNotifications(companyId); // Lấy tất cả thông báo
                runOnUiThread(() -> {
                    List<String> unreadNotifications = new ArrayList<>();
                    for (String notification : notifications) {
                        if (dbHelper.isNotificationUnread(companyId, notification)) {
                            unreadNotifications.add(notification);
                        }
                    }
                    if (!unreadNotifications.isEmpty()) {
                        notificationBadge.setText(String.valueOf(unreadNotifications.size()));
                        notificationBadge.setVisibility(View.VISIBLE);
                        showNotificationSnackbar(unreadNotifications);
                    } else {
                        notificationBadge.setVisibility(View.GONE);
                    }
                });
            });
        }
    }

    private void showNotificationSnackbar(List<String> notifications) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Bạn có " + notifications.size() + " thông báo mới từ ứng viên!", Snackbar.LENGTH_LONG)
                .setAction("Xem", v -> {
                    Intent intent = new Intent(this, NotificationDetailsActivity.class);
                    intent.putStringArrayListExtra("notifications", new ArrayList<>(notifications));
                    startActivity(intent);
                    dbHelper.markNotificationsAsRead(companyId); // Đánh dấu đã đọc
                    notificationBadge.setVisibility(View.GONE);
                    checkNotifications(); // Cập nhật lại badge
                });
        snackbar.show();
    }

    private void setupNotificationBadgeClick() {
        notificationBadge.setOnClickListener(v -> {
            if (companyId != -1) {
                Executors.newSingleThreadExecutor().execute(() -> {
                    List<String> notifications = dbHelper.getAllNotifications(companyId); // Lấy tất cả thông báo
                    runOnUiThread(() -> {
                        List<String> unreadNotifications = new ArrayList<>();
                        for (String notification : notifications) {
                            if (dbHelper.isNotificationUnread(companyId, notification)) {
                                unreadNotifications.add(notification);
                            }
                        }
                        if (!unreadNotifications.isEmpty()) {
                            Intent intent = new Intent(this, NotificationDetailsActivity.class);
                            intent.putStringArrayListExtra("notifications", new ArrayList<>(unreadNotifications));
                            startActivity(intent);
                        }
                    });
                });
            }
        });
    }

    private void logout() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Xóa tất cả dữ liệu đăng nhập
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack activity
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApplications();
        checkNotifications(); // Kiểm tra lại thông báo khi quay lại activity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}