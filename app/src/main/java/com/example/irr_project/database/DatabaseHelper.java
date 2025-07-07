package com.example.irr_project.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.irr_project.InternshipListingsActivity;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "internship_app.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_INTERNSHIPS = "internships";
    private static final String TABLE_APPLICATIONS = "applications";
    private static final String TABLE_INTERVIEWS = "interviews";
    private static final String TABLE_MESSAGES = "messages";
    private static final String TABLE_NOTIFICATIONS = "notifications";

    // Users table columns
    private static final String COL_USER_ID = "user_id";
    private static final String COL_EMAIL = "email";
    private static final String COL_PASSWORD = "password"; // In production, hash passwords
    private static final String COL_ROLE = "role"; // "student" or "recruiter"
    private static final String COL_NAME = "name";
    private static final String COL_UNIVERSITY = "university"; // Nullable for recruiters
    private static final String COL_COMPANY = "company"; // Nullable for students

    // Internships table columns
    private static final String COL_INTERNSHIP_ID = "internship_id";
    private static final String COL_TITLE = "title";
    private static final String COL_COMPANY_ID = "company_id"; // References user_id of recruiter
    private static final String COL_LOCATION = "location";
    private static final String COL_DURATION = "duration";
    private static final String COL_FIELD = "field"; // e.g., IT, Marketing
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_REQUIREMENTS = "requirements";
    private static final String COL_STIPEND = "stipend";
    private static final String COL_DEADLINE = "deadline";
    private static final String COL_DATE_POSTED = "date_posted";

    // Applications table columns
    private static final String COL_APPLICATION_ID = "application_id";
    private static final String COL_STUDENT_ID = "student_id"; // References user_id
    private static final String COL_RESUME = "resume"; // Text or file path
    private static final String COL_STATUS = "status"; // e.g., Pending, Accepted, Rejected

    // Interviews table columns
    private static final String COL_INTERVIEW_ID = "interview_id";
    private static final String COL_TIME = "time"; // ISO 8601 string or timestamp
    private static final String COL_STATUS_INTERVIEW = "status_interview"; // e.g., Proposed, Confirmed, Declined

    // Messages table columns
    private static final String COL_MESSAGE_ID = "message_id";
    private static final String COL_SENDER_ID = "sender_id"; // References user_id
    private static final String COL_RECEIVER_ID = "receiver_id"; // References user_id
    private static final String COL_CONTENT = "content";
    private static final String COL_TIMESTAMP = "timestamp";

    // Notifications table columns
    private static final String COL_NOTIFICATION_ID = "notification_id";
    private static final String COL_USER_ID_NOTIF = "user_id_notif"; // References user_id
    private static final String COL_MESSAGE = "message";
    private static final String COL_TYPE = "type"; // e.g., ApplicationUpdate, InterviewInvite
    private static final String COL_READ = "read"; // 0 for unread, 1 for read

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_ROLE + " TEXT NOT NULL CHECK(" + COL_ROLE + " IN ('student', 'recruiter')), " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_UNIVERSITY + " TEXT, " +
                COL_COMPANY + " TEXT);";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Internships table
        String CREATE_INTERNSHIPS_TABLE = "CREATE TABLE " + TABLE_INTERNSHIPS + " (" +
                COL_INTERNSHIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TITLE + " TEXT NOT NULL, " +
                COL_COMPANY_ID + " INTEGER NOT NULL, " +
                COL_LOCATION + " TEXT, " +
                COL_DURATION + " TEXT, " +
                COL_FIELD + " TEXT, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_REQUIREMENTS + " TEXT, " +
                COL_STIPEND + " TEXT, " +
                COL_DEADLINE + " TEXT, " +
                COL_DATE_POSTED + " TEXT, " +
                "FOREIGN KEY(" + COL_COMPANY_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";
        db.execSQL(CREATE_INTERNSHIPS_TABLE);

        // Create Applications table
        String CREATE_APPLICATIONS_TABLE = "CREATE TABLE " + TABLE_APPLICATIONS + " (" +
                COL_APPLICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_STUDENT_ID + " INTEGER NOT NULL, " +
                COL_INTERNSHIP_ID + " INTEGER NOT NULL, " +
                COL_RESUME + " TEXT, " +
                COL_STATUS + " TEXT NOT NULL CHECK(" + COL_STATUS + " IN ('Pending', 'Accepted', 'Rejected', 'Under Review')), " +
                "FOREIGN KEY(" + COL_STUDENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_INTERNSHIP_ID + ") REFERENCES " + TABLE_INTERNSHIPS + "(" + COL_INTERNSHIP_ID + "));";
        db.execSQL(CREATE_APPLICATIONS_TABLE);

        // Create Interviews table
        String CREATE_INTERVIEWS_TABLE = "CREATE TABLE " + TABLE_INTERVIEWS + " (" +
                COL_INTERVIEW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_APPLICATION_ID + " INTEGER NOT NULL, " +
                COL_STUDENT_ID + " INTEGER NOT NULL, " +
                COL_COMPANY_ID + " INTEGER NOT NULL, " +
                COL_TIME + " TEXT, " +
                COL_STATUS_INTERVIEW + " TEXT NOT NULL CHECK(" + COL_STATUS_INTERVIEW + " IN ('Proposed', 'Confirmed', 'Declined')), " +
                "FOREIGN KEY(" + COL_APPLICATION_ID + ") REFERENCES " + TABLE_APPLICATIONS + "(" + COL_APPLICATION_ID + "), " +
                "FOREIGN KEY(" + COL_STUDENT_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_COMPANY_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";
        db.execSQL(CREATE_INTERVIEWS_TABLE);

        // Create Messages table
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + " (" +
                COL_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SENDER_ID + " INTEGER NOT NULL, " +
                COL_RECEIVER_ID + " INTEGER NOT NULL, " +
                COL_CONTENT + " TEXT NOT NULL, " +
                COL_TIMESTAMP + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_SENDER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_RECEIVER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";
        db.execSQL(CREATE_MESSAGES_TABLE);

        // Create Notifications table
        String CREATE_NOTIFICATIONS_TABLE = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COL_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_ID_NOTIF + " INTEGER NOT NULL, " +
                COL_MESSAGE + " TEXT NOT NULL, " +
                COL_TYPE + " TEXT NOT NULL CHECK(" + COL_TYPE + " IN ('ApplicationUpdate', 'InterviewInvite')), " +
                COL_READ + " INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(" + COL_USER_ID_NOTIF + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);

        // Insert sample data
        // Users: 2 students, 2 recruiters
        db.execSQL("INSERT INTO users (email, password, role, name, university) VALUES " +
                "('student1@example.com', 'pass123', 'student', 'Nguyen Van A', 'Hanoi University');");
        db.execSQL("INSERT INTO users (email, password, role, name, university) VALUES " +
                "('student2@example.com', 'pass456', 'student', 'Tran Thi B', 'Saigon University');");
        db.execSQL("INSERT INTO users (email, password, role, name, company) VALUES " +
                "('recruiter1@example.com', 'pass789', 'recruiter', 'FPT Software', 'FPT Corporation');");
        db.execSQL("INSERT INTO users (email, password, role, name, company) VALUES " +
                "('recruiter2@example.com', 'pass012', 'recruiter', 'VNG Corp', 'VNG Corporation');");

        // Internships: 3 internships by recruiters
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Android Developer Intern', 3, 'Hanoi', '3 months', 'IT', 'Develop Android applications', 'Basic Java/Kotlin knowledge', '5000000 VND', '2025-08-01', '2025-07-01');");
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Marketing Intern', 3, 'Ho Chi Minh City', '6 months', 'Marketing', 'Assist in marketing campaigns', 'Good communication skills', '3000000 VND', '2025-07-15', '2025-07-02');");
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Web Developer Intern', 4, 'Da Nang', '4 months', 'IT', 'Build web applications', 'HTML, CSS, JavaScript', '4000000 VND', '2025-07-20', '2025-07-03');");

        // Applications: 2 applications by students
        db.execSQL("INSERT INTO applications (student_id, internship_id, resume, status) VALUES " +
                "(1, 1, 'Resume: Experienced in Java programming', 'Pending');");
        db.execSQL("INSERT INTO applications (student_id, internship_id, resume, status) VALUES " +
                "(2, 2, 'Resume: Skilled in digital marketing', 'Under Review');");

        // Interviews: 1 interview scheduled
        db.execSQL("INSERT INTO interviews (application_id, student_id, company_id, time, status_interview) VALUES " +
                "(1, 1, 3, '2025-07-10T10:00:00', 'Proposed');");

        // Messages: Sample chat between student and recruiter
        db.execSQL("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES " +
                "(1, 3, 'Hello, I’m interested in the Android Intern position.', '2025-07-07T10:00:00');");
        db.execSQL("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES " +
                "(3, 1, 'Great! Please provide more details about your experience.', '2025-07-07T10:05:00');");

        // Notifications: Sample notifications
        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(1, 'Your application for Android Developer Intern is pending.', 'ApplicationUpdate', 0);");
        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(1, 'Interview scheduled for Android Developer Intern on 2025-07-10.', 'InterviewInvite', 0);");
        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(3, 'New application received for Android Developer Intern.', 'ApplicationUpdate', 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables if upgrading
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERNSHIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Method to manually insert data for testing
    public void insertSampleData(SQLiteDatabase db) {
        // Same insert statements as in onCreate
        db.execSQL("INSERT INTO users (email, password, role, name, university) VALUES " +
                "('student3@example.com', 'pass345', 'student', 'Le Van C', 'Da Nang University');");
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Data Analyst Intern', 4, 'Hanoi', '3 months', 'Data Science', 'Analyze data sets', 'Basic Python knowledge', '4500000 VND', '2025-08-01', '2025-07-07');");
        db.execSQL("INSERT INTO applications (student_id, internship_id, resume, status) VALUES " +
                "(3, 4, 'Resume: Proficient in Python and SQL', 'Pending');");
        db.execSQL("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES " +
                "(3, 4, 'I’m interested in the Data Analyst Intern role.', '2025-07-07T12:00:00');");
        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(3, 'Your application for Data Analyst Intern is pending.', 'ApplicationUpdate', 0);");
    }
    // Method to check user credentials and get role
    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_ROLE};
        String selection = COL_EMAIL + "=? AND " + COL_PASSWORD + "=?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        String role = null;
        if (cursor.moveToFirst()) {
            role = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLE));
        }
        cursor.close();
        db.close();
        return role;
    }

    public List<InternshipListingsActivity.Internship> getAllInternships() {
        List<InternshipListingsActivity.Internship> internships = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INTERNSHIP_ID, COL_TITLE, COL_COMPANY_ID, COL_LOCATION, COL_DURATION, COL_FIELD, COL_DATE_POSTED};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(COL_TITLE);
            int companyIdIndex = cursor.getColumnIndexOrThrow(COL_COMPANY_ID);
            int locationIndex = cursor.getColumnIndexOrThrow(COL_LOCATION);
            int durationIndex = cursor.getColumnIndexOrThrow(COL_DURATION);
            int fieldIndex = cursor.getColumnIndexOrThrow(COL_FIELD);
            int datePostedIndex = cursor.getColumnIndexOrThrow(COL_DATE_POSTED);

            String companyName = getCompanyName(db, cursor.getInt(companyIdIndex));
            internships.add(new InternshipListingsActivity.Internship(
                    cursor.getString(titleIndex),
                    companyName,
                    cursor.getString(locationIndex),
                    cursor.getString(durationIndex),
                    cursor.getString(fieldIndex),
                    cursor.getString(datePostedIndex)
            ));
        }
        cursor.close();
        db.close();
        return internships;
    }

    public InternshipListingsActivity.Internship getInternshipById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INTERNSHIP_ID, COL_TITLE, COL_COMPANY_ID, COL_LOCATION, COL_DURATION, COL_FIELD, COL_DATE_POSTED, COL_DESCRIPTION, COL_REQUIREMENTS, COL_STIPEND, COL_DEADLINE};
        String selection = COL_INTERNSHIP_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, selection, selectionArgs, null, null, null);

        InternshipListingsActivity.Internship internship = null;
        if (cursor.moveToFirst()) {
            int companyId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPANY_ID));
            String companyName = getCompanyName(db, companyId);
            internship = new InternshipListingsActivity.Internship(
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    companyName,
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FIELD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_POSTED))
            );
        }
        cursor.close();
        db.close();
        return internship;
    }
    private String getCompanyName(SQLiteDatabase db, int companyId) {
        String[] columns = {COL_NAME};
        String selection = COL_USER_ID + "=?";
        String[] selectionArgs = {String.valueOf(companyId)};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        String companyName = "Unknown";
        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
        }
        cursor.close();
        return companyName;
    }
}