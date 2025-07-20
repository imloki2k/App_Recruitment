package com.example.irr_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.irr_project.database.DatabaseHelper;
// import com.example.irr_project.Internship; // Đảm bảo import đúng

import java.util.ArrayList;
import java.util.List;

public class RecruiterDashboardActivity extends AppCompatActivity {

    private ListView listViewInternships;
    private DatabaseHelper dbHelper;
    private List<Internship> internshipList;
    private InternshipAdapter internshipAdapter;
    // private int companyId; // Sẽ thay bằng currentRecruiterId (là userId của recruiter)

    // Biến và hằng số cho chức năng thông báo
    private int currentRecruiterId = -1; // ID của người dùng recruiter đã đăng nhập
    public static final String SHARED_PREFS_USER = "user_prefs"; // Phải giống với LoginActivity
    public static final String KEY_USER_ID = "user_id";       // Phải giống với LoginActivity

    private static final String CHANNEL_ID = "app_notification_channel_recruiter";
    private static final int NOTIFICATION_ID_BASE = 3000;
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 301;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruiter_dashboard);

        // Initialize UI components
        listViewInternships = findViewById(R.id.listViewInternships);
        Button buttonCreateInternship = findViewById(R.id.buttonCreateInternship);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        // dbHelper.getReadableDatabase(); // Gọi getReadable/Writable sẽ tự tạo DB

        // Lấy currentRecruiterId từ SharedPreferences
        currentRecruiterId = getCurrentRecruiterUserIdFromPrefs();

        if (currentRecruiterId == -1) {
            Log.e("RecruiterDashboard", "Recruiter User ID not found. Redirecting to login might be needed.");
            // Tùy chọn: Chuyển về LoginActivity
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
            // return;
        }

        // Load internships
        loadInternships();

        // Set up Create button
        buttonCreateInternship.setOnClickListener(v -> {
            Intent intent = new Intent(RecruiterDashboardActivity.this, CreateInternshipActivity.class);
            // Bạn có thể muốn truyền currentRecruiterId (hoặc companyId nếu nó khác) vào CreateInternshipActivity
            // intent.putExtra("recruiterId", currentRecruiterId);
            startActivity(intent);
        });

        // Set up ListView item click for edit
        listViewInternships.setOnItemClickListener((parent, view, position, id) -> {
            Internship internship = (Internship) parent.getItemAtPosition(position);
            if (internship != null) {
                Intent intent = new Intent(RecruiterDashboardActivity.this, EditInternshipActivity.class);
                intent.putExtra("internshipId", internship.getId());
                startActivity(intent);
            }
        });

        // Khởi tạo cho chức năng thông báo
        createNotificationChannel();
        requestNotificationPermission();
    }

    private void loadInternships() {
        if (currentRecruiterId == -1) {
            Toast.makeText(this, "Cannot load internships without recruiter ID.", Toast.LENGTH_SHORT).show();
            internshipList = new ArrayList<>(); // Khởi tạo rỗng để tránh null pointer
        } else {
            // Giả sử getInternshipsByCompanyId thực chất lấy theo userId của recruiter
            // Nếu bạn có một company_id riêng liên kết với user recruiter, bạn cần lấy nó
            internshipList = dbHelper.getInternshipsByCompanyId(currentRecruiterId);
        }

        if (internshipList == null || internshipList.isEmpty()) {
            if (currentRecruiterId != -1) { // Chỉ hiển thị nếu đã có ID
                Toast.makeText(this, "You have not posted any internships yet.", Toast.LENGTH_SHORT).show();
            }
            internshipList = new ArrayList<>(); // Đảm bảo không null
        }

        // Tạo hoặc cập nhật adapter
        if (internshipAdapter == null) {
            internshipAdapter = new InternshipAdapter(this, R.layout.internship_item, internshipList);
            listViewInternships.setAdapter(internshipAdapter);
        } else {
            internshipAdapter.clear();
            internshipAdapter.addAll(internshipList);
            internshipAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // currentRecruiterId đã được lấy trong onCreate
        // Nếu nó có thể thay đổi sau khi activity được tạo, bạn cần lấy lại ở đây
        // currentRecruiterId = getCurrentRecruiterUserIdFromPrefs();

        if (currentRecruiterId != -1) {
            loadInternships(); // Refresh list when returning
            checkForNewNotifications(); // Kiểm tra thông báo mới
        }
    }

    // Phương thức để lấy User ID của Recruiter từ SharedPreferences
    private int getCurrentRecruiterUserIdFromPrefs() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }


    // --- CÁC PHƯƠNG THỨC CHO CHỨC NĂNG THÔNG BÁO ---

    private void checkForNewNotifications() {
        if (dbHelper == null || currentRecruiterId == -1) {
            Log.w("NotificationCheck", "Recruiter: DatabaseHelper not initialized or User ID is invalid.");
            return;
        }
        new Thread(() -> {
            // Giả sử thông báo cho recruiter cũng được lưu với target_user_id là recruiter's user_id
            List<DatabaseHelper.AppNotification> unreadNotifications = dbHelper.getUnreadNotifications(currentRecruiterId);
            Log.d("NotificationCheck", "Found " + unreadNotifications.size() + " unread notifications for recruiter user " + currentRecruiterId);

            runOnUiThread(() -> {
                if (!unreadNotifications.isEmpty()) {
                    for (DatabaseHelper.AppNotification notification : unreadNotifications) {
                        Log.d("NotificationCheck", "Showing notification for recruiter: " + notification.getMessage());
                        showAppNotification(notification, NOTIFICATION_ID_BASE + (int) notification.getId());
                    }
                }
            });
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Recruiter App Notifications"; // getString(R.string.channel_name_recruiter);
            String description = "Notifications for recruiters regarding new applications and interview updates."; // getString(R.string.channel_description_recruiter);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationCheck", "Requesting POST_NOTIFICATIONS permission for recruiter.");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                Log.d("NotificationCheck", "POST_NOTIFICATIONS permission already granted for recruiter.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationCheck", "POST_NOTIFICATIONS permission granted by recruiter.");
                if (currentRecruiterId != -1) {
                    checkForNewNotifications();
                }
            } else {
                Log.w("NotificationCheck", "POST_NOTIFICATIONS permission denied by recruiter.");
                Toast.makeText(this, "Notification permission denied. You might miss important updates.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showAppNotification(DatabaseHelper.AppNotification appNotification, int uniqueNotificationId) {
        Intent intent;
        // Tùy chỉnh Intent dựa trên loại thông báo cho Nhà Tuyển Dụng
        if (DatabaseHelper.NotificationType.NEW_APPLICATION.equals(appNotification.getType())) {
            // Khi recruiter nhấn, mở màn hình xem danh sách ứng viên cho internship cụ thể,
            // hoặc danh sách tất cả ứng viên mới.
            intent = new Intent(this, CreateInternshipActivity.class); // Giả sử bạn có Activity này
            // Bạn cần truyền ID của internship hoặc application liên quan
            intent.putExtra("internship_id_with_new_app", appNotification.getRelatedItemId());
            // Hoặc nếu relatedItemId là application_id:
            // intent.putExtra("application_id_to_view", appNotification.getRelatedItemId());
        } else if (DatabaseHelper.NotificationType.INTERVIEW_RESPONSE.equals(appNotification.getType())) {
            // Khi recruiter nhấn, mở màn hình quản lý phỏng vấn để xem phản hồi
            intent = new Intent(this, EditInternshipActivity.class); // Giả sử bạn có Activity này
            intent.putExtra("interview_id_with_response", appNotification.getRelatedItemId());
        } else {
            // Mặc định, mở lại RecruiterDashboardActivity
            intent = new Intent(this, RecruiterDashboardActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                uniqueNotificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // THAY BẰNG ICON THÔNG BÁO CỦA BẠN (ví dụ: R.drawable.ic_notification_recruiter)
                .setContentTitle(determineNotificationTitle(appNotification.getType()))
                .setContentText(appNotification.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            try {
                notificationManager.notify(uniqueNotificationId, builder.build());
                new Thread(() -> {
                    if (dbHelper != null) {
                        dbHelper.markNotificationAsRead(appNotification.getId());
                    }
                }).start();
                Log.d("NotificationRecruiter", "Notification sent successfully with ID: " + uniqueNotificationId);
            } catch (SecurityException e) {
                Log.e("NotificationRecruiter", "SecurityException: " + e.getMessage());
            }
        } else {
            Log.w("NotificationRecruiter", "POST_NOTIFICATIONS permission not granted.");
            Toast.makeText(this, appNotification.getMessage(), Toast.LENGTH_LONG).show();
            new Thread(() -> {
                if (dbHelper != null) {
                    dbHelper.markNotificationAsRead(appNotification.getId());
                }
            }).start();
        }
    }

    private String determineNotificationTitle(String notificationType) {
        // Tùy chỉnh tiêu đề cho thông báo của Recruiter
        if (DatabaseHelper.NotificationType.NEW_APPLICATION.equals(notificationType)) {
            return "Đơn ứng tuyển mới";
        } else if (DatabaseHelper.NotificationType.INTERVIEW_RESPONSE.equals(notificationType)) {
            return "Phản hồi phỏng vấn";
        }
        return "Thông báo mới";
    }

    // Custom ArrayAdapter for ListView (giữ nguyên hoặc cải thiện nếu cần)
    public static class InternshipAdapter extends ArrayAdapter<Internship> {
        private Context context;
        // private List<Internship> internshipList; // Không cần thiết nếu dùng getItem()

        public InternshipAdapter(Context context, int resource, List<Internship> internshipList) {
            super(context, resource, internshipList);
            this.context = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(context).inflate(R.layout.internship_item, parent, false);
            }

            Internship currentInternship = getItem(position); // Lấy item hiện tại

            if (currentInternship != null) {
                TextView textViewTitle = listItemView.findViewById(R.id.textViewTitle);
                TextView textViewCompany = listItemView.findViewById(R.id.textViewCompany); // Có thể ẩn đi vì đây là dashboard của công ty đó
                TextView textViewLocation = listItemView.findViewById(R.id.textViewLocation);
                TextView textViewDuration = listItemView.findViewById(R.id.textViewDuration);

                if (textViewTitle != null) {
                    textViewTitle.setText(currentInternship.getTitle() != null ? currentInternship.getTitle() : "N/A");
                }
                if (textViewCompany != null) {
                    // Trong dashboard của recruiter, tên công ty có thể không cần thiết hoặc hiển thị khác
                    textViewCompany.setText("Company: " + (currentInternship.getCompany() != null ? currentInternship.getCompany() : "N/A"));
                    // Hoặc ẩn đi: textViewCompany.setVisibility(View.GONE);
                }
                if (textViewLocation != null) {
                    textViewLocation.setText("Location: " + (currentInternship.getLocation() != null ? currentInternship.getLocation() : "N/A"));
                }
                if (textViewDuration != null) {
                    textViewDuration.setText("Duration: " + (currentInternship.getDuration() != null ? currentInternship.getDuration() : "N/A"));
                }
            }
            return listItemView;
        }
    }
}
