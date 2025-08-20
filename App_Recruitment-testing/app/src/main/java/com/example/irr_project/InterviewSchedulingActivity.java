package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.irr_project.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class InterviewSchedulingActivity extends AppCompatActivity implements InterviewAdapter.OnInterviewActionListener {
    private static final String TAG = "InterviewSchedulingActivity";
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private static final String KEY_USER_ROLE = "logged_user_role";
    private RecyclerView recyclerViewInterviews;
    private InterviewAdapter interviewAdapter;
    private DatabaseHelper dbHelper;
    private List<Interview> interviewList;
    private TextView tvNoInterviews;
    private EditText etProposedTime;
    private Button btnSubmitTime;
    private int currentUserId = -1;
    private String userRole;
    private int applicationId = -1;
    private int studentId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Enable StrictMode to avoid hiddenapi issues on BlueStacks
        android.os.StrictMode.setThreadPolicy(new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build());
        setContentView(R.layout.activity_interview_scheduling);

        // Khởi tạo giao diện
        recyclerViewInterviews = findViewById(R.id.recycler_view_interviews);
        tvNoInterviews = findViewById(R.id.tv_no_interviews);
        etProposedTime = findViewById(R.id.et_proposed_time);
        btnSubmitTime = findViewById(R.id.btn_submit_time);

        if (recyclerViewInterviews == null || tvNoInterviews == null || etProposedTime == null || btnSubmitTime == null) {
            Log.e(TAG, "Không tìm thấy một hoặc nhiều thành phần giao diện");
            Toast.makeText(this, "Lỗi giao diện: Không tìm thấy thành phần", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Lấy thông tin người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
        currentUserId = sharedPreferences.getInt(KEY_USER_ID, -1);
        userRole = sharedPreferences.getString(KEY_USER_ROLE, null);
        if (currentUserId == -1 || userRole == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Không tìm thấy userId hoặc userRole.");
            finish();
            return;
        }

        // Lấy application_id và student_id từ Intent
        applicationId = getIntent().getIntExtra("application_id", -1);
        studentId = getIntent().getIntExtra("student_id", -1);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        interviewList = new ArrayList<>();
        recyclerViewInterviews.setLayoutManager(new LinearLayoutManager(this));
        interviewAdapter = new InterviewAdapter(this, interviewList, this, userRole);
        recyclerViewInterviews.setAdapter(interviewAdapter);

        // Ẩn/hiện giao diện dựa trên vai trò
        if (userRole.equals("student")) {
            etProposedTime.setVisibility(View.GONE);
            btnSubmitTime.setVisibility(View.GONE);
        } else if (userRole.equals("recruiter")) {
            etProposedTime.setVisibility(View.VISIBLE);
            btnSubmitTime.setVisibility(View.VISIBLE);
            btnSubmitTime.setOnClickListener(v -> proposeInterviewTime());
        } else {
            Toast.makeText(this, "Vai trò không hợp lệ.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Tải danh sách phỏng vấn
        loadInterviews();
    }

    private void loadInterviews() {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Interview> resultInterviews = dbHelper.getInterviewsByUserId(currentUserId, userRole);
            runOnUiThread(() -> {
                interviewList.clear();
                if (resultInterviews != null && !resultInterviews.isEmpty()) {
                    interviewList.addAll(resultInterviews);
                    tvNoInterviews.setVisibility(View.GONE);
                    recyclerViewInterviews.setVisibility(View.VISIBLE);
                    Log.d(TAG, "Loaded " + resultInterviews.size() + " interviews for user ID: " + currentUserId);
                } else {
                    tvNoInterviews.setVisibility(View.VISIBLE);
                    recyclerViewInterviews.setVisibility(View.GONE);
                    Log.d(TAG, "No interviews found for user ID: " + currentUserId);
                }
                interviewAdapter.notifyDataSetChanged();
            });
        });
    }

    private void proposeInterviewTime() {
        String proposedTime = etProposedTime.getText().toString().trim();
        if (TextUtils.isEmpty(proposedTime)) {
            Toast.makeText(this, "Vui lòng nhập thời gian phỏng vấn.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (applicationId == -1 || studentId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin đơn ứng tuyển.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid application_id or student_id");
            return;
        }

        // Kiểm tra trạng thái đơn ứng tuyển
        String applicationStatus = dbHelper.getApplicationStatus(studentId, dbHelper.getInternshipIdFromApplication(applicationId));
        if (!Application.Status.ACCEPTED.toString().equals(applicationStatus)) {
            Toast.makeText(this, "Chỉ có thể đề xuất phỏng vấn cho đơn đã được chấp nhận.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Application status is not Accepted: " + applicationStatus);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            boolean success = dbHelper.proposeInterview(applicationId, studentId, currentUserId, proposedTime);
            runOnUiThread(() -> {
                if (success) {
                    String internshipTitle = dbHelper.getInternshipTitle(dbHelper.getInternshipIdFromApplication(applicationId));
                    Toast.makeText(this, "Đã đề xuất thời gian phỏng vấn.", Toast.LENGTH_SHORT).show();
                    dbHelper.insertNotification(
                            studentId,
                            "Nhà tuyển dụng đã đề xuất phỏng vấn cho " + internshipTitle + " vào " + proposedTime,
                            "InterviewInvite"
                    );
                    etProposedTime.setText("");
                    loadInterviews();
                } else {
                    Toast.makeText(this, "Lỗi khi đề xuất thời gian phỏng vấn.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to propose interview for application ID: " + applicationId);
                }
            });
        });
    }

    @Override
    public void onConfirmClick(Interview interview, int position) {
        if (!interview.getStatus().equals(Interview.Status.PROPOSED.toString())) {
            Toast.makeText(this, "Chỉ có thể xác nhận lịch phỏng vấn đang ở trạng thái Đề xuất.", Toast.LENGTH_SHORT).show();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean success = dbHelper.updateInterviewStatus(interview.getInterviewId(), Interview.Status.CONFIRMED.toString());
            runOnUiThread(() -> {
                if (success) {
                    dbHelper.insertNotification(
                            interview.getCompanyId(),
                            "Sinh viên đã xác nhận phỏng vấn cho " + interview.getInternshipTitle() + " vào " + interview.getTime(),
                            "InterviewInvite"
                    );
                    Toast.makeText(this, "Đã xác nhận lịch phỏng vấn.", Toast.LENGTH_SHORT).show();
                    loadInterviews();
                } else {
                    Toast.makeText(this, "Lỗi khi xác nhận lịch phỏng vấn.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to confirm interview ID: " + interview.getInterviewId());
                }
            });
        });
    }

    @Override
    public void onDeclineClick(Interview interview, int position) {
        if (!interview.getStatus().equals(Interview.Status.PROPOSED.toString())) {
            Toast.makeText(this, "Chỉ có thể từ chối lịch phỏng vấn đang ở trạng thái Đề xuất.", Toast.LENGTH_SHORT).show();
            return;
        }
        Executors.newSingleThreadExecutor().execute(() -> {
            boolean success = dbHelper.updateInterviewStatus(interview.getInterviewId(), Interview.Status.DECLINED.toString());
            runOnUiThread(() -> {
                if (success) {
                    dbHelper.insertNotification(
                            interview.getCompanyId(),
                            "Sinh viên đã từ chối phỏng vấn cho " + interview.getInternshipTitle() + " vào " + interview.getTime(),
                            "InterviewInvite"
                    );
                    Toast.makeText(this, "Đã từ chối lịch phỏng vấn.", Toast.LENGTH_SHORT).show();
                    loadInterviews();
                } else {
                    Toast.makeText(this, "Lỗi khi từ chối lịch phỏng vấn.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed to decline interview ID: " + interview.getInterviewId());
                }
            });
        });
    }

    @Override
    public void onProposeClick(Interview interview, int position) {
        proposeInterviewTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInterviews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.closeDatabase();
        }
    }
}