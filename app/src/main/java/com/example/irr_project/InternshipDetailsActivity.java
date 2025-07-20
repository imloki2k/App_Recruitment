package com.example.irr_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.irr_project.database.DatabaseHelper;
import com.example.irr_project.Internship;
import android.content.SharedPreferences;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class InternshipDetailsActivity extends AppCompatActivity {

    private TextView textViewTitle, textViewCompany, textViewDescription, textViewRequirements, textViewStipend, textViewDeadline, textViewStatus;
    private EditText editTextResume;
    private Button buttonApply, buttonUploadFile;
    private DatabaseHelper dbHelper;
    private Internship internship;
    private int internshipId;
    private int userId;
    private ActivityResultLauncher<Intent> pickFileLauncher;
    private Uri selectedFileUri;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internship_details);

        // Initialize UI components
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewCompany = findViewById(R.id.textViewCompany);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewRequirements = findViewById(R.id.textViewRequirements);
        textViewStipend = findViewById(R.id.textViewStipend);
        textViewDeadline = findViewById(R.id.textViewDeadline);
        textViewStatus = findViewById(R.id.textViewStatus);
        editTextResume = findViewById(R.id.editTextResume);
        buttonApply = findViewById(R.id.buttonApply);
        buttonUploadFile = findViewById(R.id.buttonUploadFile);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);
        dbHelper.getReadableDatabase();

        // Request storage permission (for Android < 13)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            requestStoragePermission();
        }

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get internship data from intent
        internshipId = getIntent().getIntExtra("internshipId", -1);
        if (internshipId != -1) {
            internship = dbHelper.getInternshipById(internshipId);
            if (internship != null) {
                displayInternshipDetails();
                checkApplicationStatus();
            } else {
                Toast.makeText(this, "Internship not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Set up file picker using ACTION_OPEN_DOCUMENT for broader file access
        pickFileLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedFileUri = result.getData().getData();
                        if (selectedFileUri != null) {
                            saveFileToInternalStorage(selectedFileUri); // Lưu file vào bộ nhớ nội bộ
                            editTextResume.setText("File selected: " + getFileName(selectedFileUri));
                        } else {
                            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Set up apply button
        buttonApply.setOnClickListener(v -> {
            String resumeText = editTextResume.getText().toString().trim();
            String resumeContent = selectedFileUri != null ? selectedFileUri.toString() : resumeText;
            if (resumeContent.isEmpty()) {
                editTextResume.setError("Please enter your resume or select a file");
                return;
            }
            applyForInternship(resumeContent);
        });
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    // Phương thức xử lý onClick từ layout
    public void onPickFile(View view) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*"); // Allow all file types
            pickFileLauncher.launch(intent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) == PackageManager.PERMISSION_GRANTED) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                pickFileLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Media permission required. Please allow in settings.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                }, STORAGE_PERMISSION_CODE);
            }
        } else { // Android < 13
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                pickFileLauncher.launch(intent);
            } else {
                Toast.makeText(this, "Storage permission required. Please allow in the permission popup.", Toast.LENGTH_LONG).show();
                requestStoragePermission();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                onPickFile(null); // Retry file picking
            } else {
                Toast.makeText(this, "Permission denied. Please enable it in Settings > Apps > [Your App Name] > Permissions.", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Helper method to get file name from Uri
    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result != null ? result : "UnknownFile";
    }

    // Method to save file to internal storage
    private void saveFileToInternalStorage(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            if (inputStream != null) {
                String fileName = getFileName(fileUri);
                File file = new File(getFilesDir(), "resumes/" + fileName); // Tạo thư mục "resumes" nếu chưa có
                file.getParentFile().mkdirs(); // Tạo thư mục nếu chưa tồn tại
                OutputStream outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.close();
                inputStream.close();
                selectedFileUri = Uri.fromFile(file); // Cập nhật Uri mới
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayInternshipDetails() {
        textViewTitle.setText(internship.getTitle());
        textViewCompany.setText("Company: " + internship.getCompany());
        textViewDescription.setText("Description: " + internship.getDescription());
        textViewRequirements.setText("Requirements: " + internship.getRequirements());
        textViewStipend.setText("Stipend: " + (internship.getStipend() != null ? internship.getStipend() : "Not specified"));
        textViewDeadline.setText("Deadline: " + internship.getDeadline());
    }

    private void checkApplicationStatus() {
        String status = dbHelper.getApplicationStatus(userId, internshipId);
        if (status != null) {
            textViewStatus.setText("Status: " + status);
            buttonApply.setEnabled(false);
        } else {
            textViewStatus.setText("Status: Not Applied");
            buttonApply.setEnabled(true);
        }
    }

    private void applyForInternship(String resume) {
        String resumeUri = selectedFileUri != null ? selectedFileUri.toString() : resume;
        if (dbHelper.addApplication(userId, internshipId, resumeUri, "Pending")) {
            Toast.makeText(this, "Application submitted successfully", Toast.LENGTH_SHORT).show();
            textViewStatus.setText("Status: Pending");
            buttonApply.setEnabled(false);
            selectedFileUri = null; // Reset after successful submission
        } else {
            Toast.makeText(this, "Failed to submit application", Toast.LENGTH_SHORT).show();
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