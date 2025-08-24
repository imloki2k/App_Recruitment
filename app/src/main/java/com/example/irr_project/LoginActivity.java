package com.example.irr_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import com.example.irr_project.utils.UserSession;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String SHARED_PREFS_USER = "user_prefs_details";
    private static final String KEY_USER_ID = "logged_user_id";
    private static final String KEY_USER_ROLE = "logged_user_role";
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            // Khởi tạo giao diện
            editTextEmail = findViewById(R.id.editTextEmail);
            editTextPassword = findViewById(R.id.editTextPassword);
            buttonLogin = findViewById(R.id.buttonLogin);

            if (editTextEmail == null || editTextPassword == null || buttonLogin == null) {
                Log.e(TAG, "Không tìm thấy một hoặc nhiều thành phần giao diện");
                Toast.makeText(this, "Lỗi giao diện: Không tìm thấy thành phần", Toast.LENGTH_LONG).show();
                return;
            }

            // Khởi tạo DatabaseHelper
            dbHelper = new DatabaseHelper(this);
            dbHelper.getWritableDatabase();

            // Xử lý sự kiện nút đăng nhập
            buttonLogin.setOnClickListener(v -> {
                try {
                    String email = editTextEmail.getText().toString().trim();
                    String password = editTextPassword.getText().toString().trim();

                    // Xác thực đầu vào
                    if (!validateInputs(email, password)) {
                        return;
                    }

                    // Kiểm tra thông tin đăng nhập và lấy vai trò
                    String role = dbHelper.getUserRole(email, password);
                    Log.d(TAG, "Role retrieved: " + role);
                    if (role != null) {
                        // Lấy userId từ email
                        int userId = getUserIdFromEmail(email);
                        Log.d(TAG, "User ID retrieved: " + userId);
                        if (userId != -1) {
                            // Lưu userId và userRole vào SharedPreferences
                            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_USER, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(KEY_USER_ID, userId);
                            editor.putString(KEY_USER_ROLE, role);
                            editor.apply();
                            new UserSession(getApplicationContext()).setUserLoggedIn(email, role);

                            Toast.makeText(this, "Đăng nhập thành công với vai trò " + role, Toast.LENGTH_SHORT).show();

                            // Chuyển hướng đến dashboard phù hợp
                            Intent intent;
                            if ("student".equals(role)) {
                                intent = new Intent(LoginActivity.this, InternshipListingsActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, RecruiterDashboardActivity.class);
                            }
                            intent.putExtra("userId", userId);
                            intent.putExtra("role", "student".equals(role) ? 0 : 1);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Không tìm thấy ID người dùng", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Lỗi khi đăng nhập: ", e);
                    Toast.makeText(this, "Lỗi đăng nhập: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khởi tạo LoginActivity: ", e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Vui lòng nhập email hợp lệ");
            return false;
        }
        if (password.isEmpty() || password.length() < 6) {
            editTextPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    public void goToRegister(View view) {
        try {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi chuyển đến RegisterActivity: ", e);
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

    private int getUserIdFromEmail(String email) {
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String[] columns = {DatabaseHelper.COL_USER_ID};
            String selection = DatabaseHelper.COL_EMAIL + "=?";
            String[] selectionArgs = {email};
            Cursor cursor = db.query(DatabaseHelper.TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            int userId = -1;
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID));
            }
            cursor.close();
            return userId;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi lấy userId từ email: ", e);
            return -1;
        }
    }
    public void goToForgotPassword(View view) {
        try {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to ForgotPasswordActivity: ", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}