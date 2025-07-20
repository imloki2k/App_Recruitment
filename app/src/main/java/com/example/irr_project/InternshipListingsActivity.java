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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.irr_project.database.DatabaseHelper;
// Đảm bảo bạn đã import lớp Internship nếu nó ở package khác
// import com.example.irr_project.Internship; // Nếu Internship là class riêng

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InternshipListingsActivity extends AppCompatActivity {

    private ListView listViewInternships;
    private InternshipAdapter internshipAdapter;
    private DatabaseHelper dbHelper;
    private Spinner spinnerFilter;
    private CheckBox checkBoxSortByDate;
    private List<Internship> internshipList; // Bạn đã có internshipList này

    // Biến và hằng số cho chức năng thông báo
    private int currentUserId = -1;
    public static final String SHARED_PREFS_USER = "user_prefs"; // Phải giống với LoginActivity
    public static final String KEY_USER_ID = "user_id";       // Phải giống với LoginActivity
    // public static final String KEY_USER_ROLE = "user_role"; // Nếu bạn cần role ở đây

    private static final String CHANNEL_ID = "app_notification_channel_student";
    private static final int NOTIFICATION_ID_BASE = 2000; // Để tạo ID duy nhất cho mỗi notification
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_listings);

        // Initialize UI components (bạn đã có)
        listViewInternships = findViewById(R.id.listViewInternships);
        spinnerFilter = findViewById(R.id.spinnerFilter);
        checkBoxSortByDate = findViewById(R.id.checkBoxSortByDate);

        // Initialize DatabaseHelper (bạn đã có)
        dbHelper = new DatabaseHelper(this);
        // dbHelper.getReadableDatabase(); // Gọi getReadableDatabase() hoặc getWritableDatabase() sẽ tạo DB nếu chưa có

        // Lấy currentUserId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);

        if (currentUserId == -1) {
            Log.e("InternshipListings", "User ID not found. Redirecting to login might be needed.");
            // Tùy chọn: Chuyển về LoginActivity nếu không có user ID
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
            // return;
        }

        // Set up ListView (bạn đã có)
        loadInternshipData(); // Tách ra thành một phương thức cho dễ đọc

        // Set up Spinner and CheckBox (bạn đã có)
        setupFiltersAndSorting();

        // Set up ListView item click listener (bạn đã có)
        listViewInternships.setOnItemClickListener((parent, view, position, id) -> {
            // Lấy internship từ adapter để đảm bảo đúng item sau khi filter/sort
            Internship internship = (Internship) parent.getItemAtPosition(position);
            if (internship != null) {
                Intent intent = new Intent(InternshipListingsActivity.this, InternshipDetailsActivity.class);
                intent.putExtra("internshipId", internship.getId());
                startActivity(intent);
            }
        });

        // Khởi tạo cho chức năng thông báo
        createNotificationChannel();
        requestNotificationPermission(); // Yêu cầu quyền nếu cần
    }

    private void loadInternshipData() {
        internshipList = dbHelper.getAllInternships();
        if (internshipList == null) {
            internshipList = new ArrayList<>();
            Toast.makeText(this, "No internships available at the moment.", Toast.LENGTH_SHORT).show();
        } else if (internshipList.isEmpty()) {
            Toast.makeText(this, "No internships available at the moment.", Toast.LENGTH_SHORT).show();
        }
        // Khởi tạo adapter với danh sách gốc
        internshipAdapter = new InternshipAdapter(this, R.layout.internship_item, new ArrayList<>(internshipList));
        listViewInternships.setAdapter(internshipAdapter);
        filterInternships(); // Áp dụng filter/sort mặc định ban đầu
    }

    private void setupFiltersAndSorting() {
        String[] fields = {"All", "IT", "Marketing", "Data Science"}; // Lấy từ DB hoặc resource nếu có thể
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fields);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterInternships();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkBoxSortByDate.setOnCheckedChangeListener((buttonView, isChecked) -> filterInternships());
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (currentUserId != -1) {
            // Có thể bạn muốn load lại internship data ở đây nếu nó có thể thay đổi từ các activity khác
            // loadInternshipData();
            checkForNewNotifications(); // Kiểm tra thông báo mới
        }
    }

    private void filterInternships() {
        if (internshipList == null) return; // Guard clause

        String selectedField = spinnerFilter.getSelectedItem() != null ? spinnerFilter.getSelectedItem().toString() : "All";
        boolean sortByDate = checkBoxSortByDate.isChecked();

        List<Internship> SourcedList = new ArrayList<>(internshipList); // Làm việc trên bản sao để không thay đổi internshipList gốc
        List<Internship> filteredList = new ArrayList<>();

        if (selectedField.equals("All")) {
            filteredList.addAll(SourcedList);
        } else {
            for (Internship internship : SourcedList) {
                if (internship.getField() != null && internship.getField().equals(selectedField)) {
                    filteredList.add(internship);
                }
            }
        }

        if (sortByDate) {
            Collections.sort(filteredList, (i1, i2) -> {
                if (i1.getDatePosted() == null && i2.getDatePosted() == null) return 0;
                if (i1.getDatePosted() == null) return 1; // nulls last
                if (i2.getDatePosted() == null) return -1; // nulls last
                return i2.getDatePosted().compareTo(i1.getDatePosted()); // Newest first
            });
        }
        // Nếu không sort by date, có thể bạn muốn một thứ tự mặc định, ví dụ theo ID hoặc Title
        // else {
        //    Collections.sort(filteredList, Comparator.comparing(Internship::getTitle, String.CASE_INSENSITIVE_ORDER));
        // }

        if (internshipAdapter != null) {
            internshipAdapter.clear();
            internshipAdapter.addAll(filteredList);
            internshipAdapter.notifyDataSetChanged();
        } else {
            // Khởi tạo adapter nếu chưa có (trường hợp filterInternships được gọi trước khi adapter sẵn sàng)
            internshipAdapter = new InternshipAdapter(this, R.layout.internship_item, filteredList);
            listViewInternships.setAdapter(internshipAdapter);
        }
    }


    // --- CÁC PHƯƠNG THỨC CHO CHỨC NĂNG THÔNG BÁO ---

    private void checkForNewNotifications() {
        if (dbHelper == null || currentUserId == -1) {
            Log.w("NotificationCheck", "DatabaseHelper not initialized or User ID is invalid.");
            return;
        }
        new Thread(() -> {
            // Đảm bảo dbHelper được sử dụng đúng cách (đã được khởi tạo)
            List<DatabaseHelper.AppNotification> unreadNotifications = dbHelper.getUnreadNotifications(currentUserId);
            Log.d("NotificationCheck", "Found " + unreadNotifications.size() + " unread notifications for student user " + currentUserId);

            runOnUiThread(() -> {
                if (!unreadNotifications.isEmpty()) {
                    for (DatabaseHelper.AppNotification notification : unreadNotifications) {
                        Log.d("NotificationCheck", "Showing notification for student: " + notification.getMessage());
                        // Sử dụng ID thông báo từ DB để đảm bảo tính duy nhất khi hiển thị nhiều thông báo
                        showAppNotification(notification, NOTIFICATION_ID_BASE + (int) notification.getId());
                    }
                }
            });
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Student App Notifications"; // getString(R.string.channel_name_student);
            String description = "Notifications for application updates and interview invites for students."; // getString(R.string.channel_description_student);
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
                Log.d("NotificationCheck", "Requesting POST_NOTIFICATIONS permission for student.");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATIONS);
            } else {
                Log.d("NotificationCheck", "POST_NOTIFICATIONS permission already granted for student.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("NotificationCheck", "POST_NOTIFICATIONS permission granted by student.");
                if (currentUserId != -1) {
                    checkForNewNotifications(); // Kiểm tra lại thông báo nếu quyền vừa được cấp
                }
            } else {
                Log.w("NotificationCheck", "POST_NOTIFICATIONS permission denied by student.");
                Toast.makeText(this, "Notification permission denied. You might miss important updates.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showAppNotification(DatabaseHelper.AppNotification appNotification, int uniqueNotificationId) {
        Intent intent;
        // Tùy chỉnh Intent dựa trên loại thông báo cho Sinh viên
        if (DatabaseHelper.NotificationType.APPLICATION_UPDATE.equals(appNotification.getType())) {
            // Khi sinh viên nhấn, mở màn hình quản lý đơn ứng tuyển của họ
            intent = new Intent(this, MyApplicationsActivity.class); // Giả sử bạn có Activity này
            // Bạn có thể truyền ID của application để highlight hoặc scroll tới
            intent.putExtra("application_id_to_show", appNotification.getRelatedItemId());
        } else if (DatabaseHelper.NotificationType.INTERVIEW_INVITE.equals(appNotification.getType())) {
            // Khi sinh viên nhấn, mở màn hình lịch phỏng vấn
            intent = new Intent(this, InternshipDetailsActivity.class); // Giả sử bạn có Activity này
            intent.putExtra("interview_id_to_show", appNotification.getRelatedItemId());
        } else {
            // Mặc định, mở lại InternshipListingsActivity
            intent = new Intent(this, InternshipListingsActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                uniqueNotificationId, // Request code phải duy nhất cho mỗi PendingIntent khác nhau
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) //  THAY BẰNG ICON THÔNG BÁO CỦA BẠN (ví dụ: R.drawable.ic_notification_student)
                .setContentTitle(determineNotificationTitle(appNotification.getType()))
                .setContentText(appNotification.getMessage())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // Tự động hủy thông báo khi người dùng nhấn vào

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            try {
                notificationManager.notify(uniqueNotificationId, builder.build());
                // Đánh dấu đã đọc sau khi hiển thị thành công
                new Thread(() -> {
                    if (dbHelper != null) {
                        dbHelper.markNotificationAsRead(appNotification.getId());
                    }
                }).start();
                Log.d("NotificationStudent", "Notification sent successfully with ID: " + uniqueNotificationId);
            } catch (SecurityException e) {
                Log.e("NotificationStudent", "SecurityException while trying to post notification: " + e.getMessage());
            }
        } else {
            Log.w("NotificationStudent", "Attempted to show notification but POST_NOTIFICATIONS permission is not granted.");
            // Giải pháp thay thế nếu không có quyền: Hiển thị Toast
            Toast.makeText(this, appNotification.getMessage(), Toast.LENGTH_LONG).show();
            new Thread(() -> {
                if (dbHelper != null) {
                    dbHelper.markNotificationAsRead(appNotification.getId());
                }
            }).start();
        }
    }

    private String determineNotificationTitle(String notificationType) {
        if (DatabaseHelper.NotificationType.APPLICATION_UPDATE.equals(notificationType)) {
            return "Cập nhật đơn ứng tuyển";
        } else if (DatabaseHelper.NotificationType.INTERVIEW_INVITE.equals(notificationType)) {
            return "Lời mời phỏng vấn mới";
        }
        return "Thông báo mới"; // Tiêu đề mặc định
    }


    // Custom ArrayAdapter for ListView (bạn đã có)
    public static class InternshipAdapter extends ArrayAdapter<Internship> {
        // Nội dung của InternshipAdapter giữ nguyên như bạn đã cung cấp
        private Context context;
        // private List<Internship> internshipList; // Không cần thiết nếu bạn dùng getItem()

        public InternshipAdapter(Context context, int resource, List<Internship> internshipList) {
            super(context, resource, internshipList);
            this.context = context;
            // this.internshipList = internshipList != null ? internshipList : new ArrayList<>();
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View listItemView = convertView;
            if (listItemView == null) {
                listItemView = LayoutInflater.from(context).inflate(R.layout.internship_item, parent, false);
            }

            Internship currentInternship = getItem(position); // Sử dụng getItem() là cách tốt hơn

            if (currentInternship != null) {
                TextView textViewTitle = listItemView.findViewById(R.id.textViewTitle);
                TextView textViewCompany = listItemView.findViewById(R.id.textViewCompany);
                TextView textViewLocation = listItemView.findViewById(R.id.textViewLocation);
                TextView textViewDuration = listItemView.findViewById(R.id.textViewDuration);
                // TextView textViewDatePosted = listItemView.findViewById(R.id.textViewDatePosted); // Nếu bạn có hiển thị ngày đăng

                if (textViewTitle != null) {
                    textViewTitle.setText(currentInternship.getTitle() != null ? currentInternship.getTitle() : "N/A");
                }
                if (textViewCompany != null) {
                    textViewCompany.setText("Company: " + (currentInternship.getCompany() != null ? currentInternship.getCompany() : "N/A"));
                }
                if (textViewLocation != null) {
                    textViewLocation.setText("Location: " + (currentInternship.getLocation() != null ? currentInternship.getLocation() : "N/A"));
                }
                if (textViewDuration != null) {
                    textViewDuration.setText("Duration: " + (currentInternship.getDuration() != null ? currentInternship.getDuration() : "N/A"));
                }
                // if (textViewDatePosted != null && currentInternship.getDatePosted() != null) {
                //    textViewDatePosted.setText("Posted: " + formatDate(currentInternship.getDatePosted())); // Cần hàm formatDate
                // }
            }
            return listItemView;
        }
    }
}

