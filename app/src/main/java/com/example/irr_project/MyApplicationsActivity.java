package com.example.irr_project;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.irr_project.database.DatabaseHelper;

// Import lớp Application đã được tách riêng
// import com.example.irr_project.model.Application; // Nếu bạn đặt Application trong package model
// Import ApplicationAdapter (nếu nó ở cùng package thì không cần, nhưng rõ ràng hơn)
// import com.example.irr_project.ApplicationAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

// Implement interface từ ApplicationAdapter đã import
public class MyApplicationsActivity extends AppCompatActivity implements ApplicationAdapter.OnApplicationActionListener {

    private static final String TAG = "MyApplicationsActivity";

    private RecyclerView recyclerViewApplications;
    private ApplicationAdapter applicationAdapter; // Sử dụng ApplicationAdapter đã import
    private DatabaseHelper dbHelper;
    private List<Application> applicationList; // Danh sách các đối tượng Application đã import
    private TextView tvNoApplications;
    private int currentStudentId = -1;

    public static final String SHARED_PREFS_USER = "user_prefs_details";
    public static final String KEY_USER_ID = "logged_user_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        currentStudentId = sharedPreferences.getInt(KEY_USER_ID, -1);
        Log.d(TAG, "Retrieved studentId from SharedPreferences: " + currentStudentId);

        if (currentStudentId == -1 && savedInstanceState == null) { // Thêm kiểm tra savedInstanceState
            // Chạy ở chế độ TEST nếu không tìm thấy studentId và không phải là lần khôi phục trạng thái
            currentStudentId = 1; // GIẢ SỬ studentId là 1 để test nếu chưa có login.
            Toast.makeText(this, "TEST MODE: Using studentId=1 (No logged in user found)", Toast.LENGTH_LONG).show();
            Log.d(TAG, "TEST MODE: Forcing studentId=1 as no user found in SharedPreferences");
        } else if (currentStudentId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin sinh viên. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Student ID not found in SharedPreferences.");
        }


        dbHelper = new DatabaseHelper(this);
        applicationList = new ArrayList<>();

        tvNoApplications = findViewById(R.id.tvNoApplications);
        recyclerViewApplications = findViewById(R.id.recyclerViewMyApplications);
        recyclerViewApplications.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo Adapter với Context, danh sách Application và listener (chính Activity này)
        applicationAdapter = new ApplicationAdapter(this, applicationList, this);
        recyclerViewApplications.setAdapter(applicationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentStudentId != -1) {
            loadApplicationsForStudent();
        } else {
            Log.w(TAG, "onResume: currentStudentId is -1, not loading applications.");
            applicationList.clear();
            if (applicationAdapter != null) { // Kiểm tra null cho adapter
                applicationAdapter.notifyDataSetChanged();
            }
            tvNoApplications.setVisibility(View.VISIBLE);
            recyclerViewApplications.setVisibility(View.GONE);
        }
    }

    private void loadApplicationsForStudent() {
        if (currentStudentId == -1) {
            Log.e(TAG, "Cannot load applications, studentId is -1");
            tvNoApplications.setVisibility(View.VISIBLE);
            recyclerViewApplications.setVisibility(View.GONE);
            applicationList.clear();
            if(applicationAdapter != null) applicationAdapter.notifyDataSetChanged();
            return;
        }
        Log.d(TAG, "Attempting to load applications for studentId: " + currentStudentId);
        new LoadApplicationsTask(this, currentStudentId, dbHelper).execute();
    }

    // --- Implement OnApplicationActionListener (từ Adapter) ---
    // Sử dụng Application đã import
    @Override
    public void onWithdrawClick(Application application, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận rút đơn")
                .setMessage("Bạn có chắc chắn muốn rút đơn ứng tuyển cho vị trí '" + application.getInternshipTitle() + "' không?")
                .setPositiveButton("Rút đơn", (dialog, which) -> {
                    // Gọi UpdateApplicationStatusTask với Application.Status.WITHDRAWN
                    new UpdateApplicationStatusTask(MyApplicationsActivity.this, application.getApplicationId(), Application.Status.WITHDRAWN, dbHelper).execute();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onScheduleInterviewClick(Application application, int position) {
        Toast.makeText(this, "Chức năng lên lịch phỏng vấn cho: " + application.getInternshipTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Implement navigation to ScheduleInterviewActivity or show a dialog
    }


    private static class LoadApplicationsTask extends AsyncTask<Void, Void, List<Application>> {
        private final WeakReference<MyApplicationsActivity> activityReference;
        private final int studentId;
        private final DatabaseHelper dbHelper;

        LoadApplicationsTask(MyApplicationsActivity context, int studentId, DatabaseHelper dbHelper) {
            this.activityReference = new WeakReference<>(context);
            this.studentId = studentId;
            this.dbHelper = dbHelper;
        }

        @Override
        protected List<Application> doInBackground(Void... voids) {
            // Gọi phương thức từ dbHelper để lấy danh sách applications
            // Đảm bảo DatabaseHelper.getApplicationsByStudentId trả về List<Application>
            if (dbHelper != null) {
                return dbHelper.getApplicationsByStudentId(studentId);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Application> resultApplications) {
            MyApplicationsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            activity.applicationList.clear();
            if (resultApplications != null && !resultApplications.isEmpty()) {
                activity.applicationList.addAll(resultApplications);
                activity.tvNoApplications.setVisibility(View.GONE);
                activity.recyclerViewApplications.setVisibility(View.VISIBLE);
            } else {
                activity.tvNoApplications.setVisibility(View.VISIBLE);
                activity.recyclerViewApplications.setVisibility(View.GONE);
                Log.d(TAG, "No applications found or list is null for studentId: " + studentId);
            }
            if (activity.applicationAdapter != null) { // Luôn cập nhật adapter
                activity.applicationAdapter.notifyDataSetChanged();
            }
        }
    }


    private static class UpdateApplicationStatusTask extends AsyncTask<Void, Void, Boolean> {
        private final WeakReference<MyApplicationsActivity> activityReference;
        private final int applicationId;
        private final String newStatus;
        private final DatabaseHelper dbHelper;

        UpdateApplicationStatusTask(MyApplicationsActivity context, int applicationId, String newStatus, DatabaseHelper dbHelper) {
            this.activityReference = new WeakReference<>(context);
            this.applicationId = applicationId;
            this.newStatus = newStatus;
            this.dbHelper = dbHelper;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Gọi phương thức từ dbHelper để cập nhật trạng thái
            if (dbHelper != null) {
                return dbHelper.updateApplicationStatus(applicationId, newStatus);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            MyApplicationsActivity activity = activityReference.get();
            if (activity == null || activity.isFinishing()) return;

            if (success) {
                Toast.makeText(activity, "Đã cập nhật trạng thái đơn ứng tuyển.", Toast.LENGTH_SHORT).show();
                // Tải lại danh sách để phản ánh thay đổi
                activity.loadApplicationsForStudent();
            } else {
                Toast.makeText(activity, "Lỗi: Không thể cập nhật trạng thái.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
