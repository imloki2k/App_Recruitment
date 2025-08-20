package com.example.irr_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.example.irr_project.Application;
import com.example.irr_project.Internship;
import com.example.irr_project.Interview;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "internship_app.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_INTERNSHIPS = "internships";
    public static final String TABLE_APPLICATIONS = "applications";
    public static final String TABLE_INTERVIEWS = "interviews";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_NOTIFICATIONS = "notifications";

    // Users table columns
    public static final String COL_USER_ID = "user_id";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_ROLE = "role";
    public static final String COL_NAME = "name";
    public static final String COL_UNIVERSITY = "university";
    public static final String COL_COMPANY = "company";

    // Internships table columns
    public static final String COL_INTERNSHIP_ID = "internship_id";
    public static final String COL_TITLE = "title";
    public static final String COL_COMPANY_ID = "company_id";
    public static final String COL_LOCATION = "location";
    public static final String COL_DURATION = "duration";
    public static final String COL_FIELD = "field";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_REQUIREMENTS = "requirements";
    public static final String COL_STIPEND = "stipend";
    public static final String COL_DEADLINE = "deadline";
    public static final String COL_DATE_POSTED = "date_posted";

    // Applications table columns
    public static final String COL_APPLICATION_ID = "application_id";
    public static final String COL_STUDENT_ID = "student_id";
    public static final String COL_RESUME = "resume";
    public static final String COL_STATUS = "status";

    // Interviews table columns
    public static final String COL_INTERVIEW_ID = "interview_id";
    public static final String COL_TIME = "time";
    public static final String COL_STATUS_INTERVIEW = "status_interview";

    // Messages table columns
    public static final String COL_MESSAGE_ID = "message_id";
    public static final String COL_SENDER_ID = "sender_id";
    public static final String COL_RECEIVER_ID = "receiver_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_TIMESTAMP = "timestamp";

    // Notifications table columns
    public static final String COL_NOTIFICATION_ID = "notification_id";
    public static final String COL_USER_ID_NOTIF = "user_id_notif";
    public static final String COL_MESSAGE = "message";
    public static final String COL_TYPE = "type";
    public static final String COL_READ = "read";

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
                COL_STATUS + " TEXT NOT NULL CHECK(" + COL_STATUS + " IN ('Pending', 'Accepted', 'Rejected', 'Under Review', 'Withdrawn')), " +
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
                COL_TYPE + " TEXT NOT NULL CHECK(" + COL_TYPE + " IN ('ApplicationUpdate', 'InterviewInvite', 'NewApplication')), " +
                COL_READ + " INTEGER NOT NULL DEFAULT 0, " +
                "FOREIGN KEY(" + COL_USER_ID_NOTIF + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "));";
        db.execSQL(CREATE_NOTIFICATIONS_TABLE);

        // Insert sample data
        db.execSQL("INSERT INTO users (email, password, role, name, university) VALUES " +
                "('student1@example.com', 'pass123', 'student', 'Nguyen Van A', 'Hanoi University');");
        db.execSQL("INSERT INTO users (email, password, role, name, university) VALUES " +
                "('student2@example.com', 'pass456', 'student', 'Tran Thi B', 'Saigon University');");
        db.execSQL("INSERT INTO users (email, password, role, name, company) VALUES " +
                "('recruiter1@example.com', 'pass789', 'recruiter', 'FPT Software', 'FPT Corporation');");
        db.execSQL("INSERT INTO users (email, password, role, name, company) VALUES " +
                "('recruiter2@example.com', 'pass012', 'recruiter', 'VNG Corp', 'VNG Corporation');");

        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Android Developer Intern', 3, 'Hanoi', '3 months', 'IT', 'Develop Android applications', 'Basic Java/Kotlin knowledge', '5000000 VND', '2025-08-01', '2025-07-01');");
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Marketing Intern', 3, 'Ho Chi Minh City', '6 months', 'Marketing', 'Assist in marketing campaigns', 'Good communication skills', '3000000 VND', '2025-07-15', '2025-07-02');");
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES " +
                "('Web Developer Intern', 4, 'Da Nang', '4 months', 'IT', 'Build web applications', 'HTML, CSS, JavaScript', '4000000 VND', '2025-07-20', '2025-07-03');");

        db.execSQL("INSERT INTO applications (student_id, internship_id, resume, status) VALUES " +
                "(1, 1, 'Resume: Experienced in Java programming', 'Accepted');");
        db.execSQL("INSERT INTO applications (student_id, internship_id, resume, status) VALUES " +
                "(2, 2, 'Resume: Skilled in digital marketing', 'Under Review');");

        db.execSQL("INSERT INTO interviews (application_id, student_id, company_id, time, status_interview) VALUES " +
                "(1, 1, 3, '2025-07-21T10:00:00', 'Proposed');");

        db.execSQL("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES " +
                "(1, 3, 'Hello, I’m interested in the Android Intern position.', '2025-07-07T10:00:00');");
        db.execSQL("INSERT INTO messages (sender_id, receiver_id, content, timestamp) VALUES " +
                "(3, 1, 'Great! Please provide more details about your experience.', '2025-07-07T10:05:00');");

        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(1, 'Your application for Android Developer Intern is accepted.', 'ApplicationUpdate', 0);");
        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(1, 'Interview scheduled for Android Developer Intern on 2025-07-21.', 'InterviewInvite', 0);");
        db.execSQL("INSERT INTO notifications (user_id_notif, message, type, read) VALUES " +
                "(3, 'New application received for Android Developer Intern.', 'NewApplication', 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTERNSHIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void closeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

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
        return role;
    }

    public int getUserId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_EMAIL + "=? AND " + COL_PASSWORD + "=?";
        String[] selectionArgs = {email, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
        }
        cursor.close();
        return userId;
    }

    public List<Internship> getAllInternships(String field, boolean sortByDate) {
        List<Internship> internships = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT i.*, u." + COL_NAME + " AS company_name FROM " + TABLE_INTERNSHIPS + " i" +
                " JOIN " + TABLE_USERS + " u ON i." + COL_COMPANY_ID + "=u." + COL_USER_ID;
        if (field != null && !field.equals("Tất cả")) {
            query += " WHERE i." + COL_FIELD + "=?";
        }
        if (sortByDate) {
            query += " ORDER BY i." + COL_DATE_POSTED + " DESC";
        }
        Cursor cursor = field != null && !field.equals("Tất cả") ?
                db.rawQuery(query, new String[]{field}) : db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            internships.add(new Internship(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    cursor.getString(cursor.getColumnIndexOrThrow("company_name")),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FIELD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REQUIREMENTS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STIPEND)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DEADLINE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_POSTED))
            ));
        }
        cursor.close();
        return internships;
    }

    public Internship getInternshipById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INTERNSHIP_ID, COL_TITLE, COL_COMPANY_ID, COL_LOCATION, COL_DURATION, COL_FIELD, COL_DATE_POSTED, COL_DESCRIPTION, COL_REQUIREMENTS, COL_STIPEND, COL_DEADLINE};
        String selection = COL_INTERNSHIP_ID + "=?";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, selection, selectionArgs, null, null, null);
        Internship internship = null;
        if (cursor.moveToFirst()) {
            internship = new Internship(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    getCompanyName(db, cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPANY_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FIELD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REQUIREMENTS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STIPEND)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DEADLINE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_POSTED))
            );
        }
        cursor.close();
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

    public List<Internship> getInternshipsByCompanyId(int companyId) {
        List<Internship> internships = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INTERNSHIP_ID, COL_TITLE, COL_COMPANY_ID, COL_LOCATION, COL_DURATION, COL_FIELD, COL_DATE_POSTED, COL_DESCRIPTION, COL_REQUIREMENTS, COL_STIPEND, COL_DEADLINE};
        String selection = COL_COMPANY_ID + "=?";
        String[] selectionArgs = {String.valueOf(companyId)};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            internships.add(new Internship(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)),
                    getCompanyName(db, cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPANY_ID))),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DURATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_FIELD)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_REQUIREMENTS)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_STIPEND)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DEADLINE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_POSTED))
            ));
        }
        cursor.close();
        return internships;
    }

    public boolean addInternship(int companyId, String title, String location, String duration, String field,
                                 String description, String requirements, String stipend, String deadline, String datePosted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_COMPANY_ID, companyId);
        values.put(COL_TITLE, title);
        values.put(COL_LOCATION, location);
        values.put(COL_DURATION, duration);
        values.put(COL_FIELD, field);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_REQUIREMENTS, requirements);
        values.put(COL_STIPEND, stipend);
        values.put(COL_DEADLINE, deadline);
        values.put(COL_DATE_POSTED, datePosted);
        long result = db.insert(TABLE_INTERNSHIPS, null, values);
        return result != -1;
    }

    public boolean updateInternship(int internshipId, String title, String location, String duration, String field,
                                    String description, String requirements, String stipend, String deadline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TITLE, title);
        values.put(COL_LOCATION, location);
        values.put(COL_DURATION, duration);
        values.put(COL_FIELD, field);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_REQUIREMENTS, requirements);
        values.put(COL_STIPEND, stipend);
        values.put(COL_DEADLINE, deadline);
        String whereClause = COL_INTERNSHIP_ID + "=?";
        String[] whereArgs = {String.valueOf(internshipId)};
        int result = db.update(TABLE_INTERNSHIPS, values, whereClause, whereArgs);
        return result > 0;
    }

    public String getApplicationStatus(int userId, int internshipId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_STATUS};
        String selection = COL_STUDENT_ID + "=? AND " + COL_INTERNSHIP_ID + "=?";
        String[] selectionArgs = {String.valueOf(userId), String.valueOf(internshipId)};
        Cursor cursor = db.query(TABLE_APPLICATIONS, columns, selection, selectionArgs, null, null, null);
        String status = null;
        if (cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
        }
        cursor.close();
        return status;
    }

    public boolean addApplication(int studentId, int internshipId, String resumeUri, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_INTERNSHIP_ID, internshipId);
        values.put(COL_RESUME, resumeUri);
        values.put(COL_STATUS, status);
        long result = db.insert(TABLE_APPLICATIONS, null, values);
        if (result != -1) {
            int companyId = getCompanyIdFromInternship(internshipId);
            if (companyId != -1) {
                String internshipTitle = getInternshipTitle(internshipId);
                insertNotification(companyId, "New application received for " + internshipTitle, "NewApplication");
            }
        }
        return result != -1;
    }

    public boolean updateApplicationStatus(int applicationId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS, status);
        String whereClause = COL_APPLICATION_ID + "=?";
        String[] whereArgs = {String.valueOf(applicationId)};
        int result = db.update(TABLE_APPLICATIONS, values, whereClause, whereArgs);
        if (result > 0) {
            int studentId = getStudentIdFromApplication(applicationId);
            String internshipTitle = getInternshipTitle(getInternshipIdFromApplication(applicationId));
            insertNotification(studentId, "Your application for " + internshipTitle + " is now " + status + ".", "ApplicationUpdate");
        }
        return result > 0;
    }

    public List<Application> getApplicationsByStudentId(int studentId) {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a." + COL_APPLICATION_ID + ", a." + COL_STUDENT_ID + ", a." + COL_INTERNSHIP_ID + ", a." + COL_STATUS + ", i." + COL_TITLE + ", u." + COL_NAME +
                " FROM " + TABLE_APPLICATIONS + " a" +
                " JOIN " + TABLE_INTERNSHIPS + " i ON a." + COL_INTERNSHIP_ID + "=i." + COL_INTERNSHIP_ID +
                " JOIN " + TABLE_USERS + " u ON i." + COL_COMPANY_ID + "=u." + COL_USER_ID +
                " WHERE a." + COL_STUDENT_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(studentId)});
        while (cursor.moveToNext()) {
            int applicationId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APPLICATION_ID));
            int internshipId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID));
            int studentIdFromDb = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STUDENT_ID)); // Lấy studentId từ cursor
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
            String internshipTitle = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String companyName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            applications.add(new Application(applicationId, internshipId, studentIdFromDb, internshipTitle, companyName, status)); // Sử dụng studentIdFromDb
        }
        cursor.close();
        return applications;
    }

    public List<Application> getApplicationsByCompanyId(int companyId) {
        List<Application> applications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a." + COL_APPLICATION_ID + ", a." + COL_INTERNSHIP_ID + ", a." + COL_STUDENT_ID + ", a." + COL_STATUS + ", i." + COL_TITLE + ", u." + COL_NAME +
                " FROM " + TABLE_APPLICATIONS + " a" +
                " JOIN " + TABLE_INTERNSHIPS + " i ON a." + COL_INTERNSHIP_ID + "=i." + COL_INTERNSHIP_ID +
                " JOIN " + TABLE_USERS + " u ON i." + COL_COMPANY_ID + "=u." + COL_USER_ID +
                " WHERE i." + COL_COMPANY_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(companyId)});
        while (cursor.moveToNext()) {
            int applicationId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APPLICATION_ID));
            int internshipId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID));
            int studentId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STUDENT_ID));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
            String internshipTitle = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            String companyName = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            applications.add(new Application(applicationId, internshipId, studentId, internshipTitle, companyName, status));
        }
        cursor.close();
        return applications;
    }

    public int getCompanyIdFromInternship(int internshipId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_COMPANY_ID};
        String selection = COL_INTERNSHIP_ID + "=?";
        String[] selectionArgs = {String.valueOf(internshipId)};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, selection, selectionArgs, null, null, null);
        int companyId = -1;
        if (cursor.moveToFirst()) {
            companyId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPANY_ID));
        }
        cursor.close();
        return companyId;
    }

    public boolean insertNotification(int userId, String message, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_ID_NOTIF, userId);
        values.put(COL_MESSAGE, message);
        values.put(COL_TYPE, type);
        values.put(COL_READ, 0);
        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        return result != -1;
    }

    public List<String> getUnreadNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_MESSAGE};
        String selection = COL_USER_ID_NOTIF + "=? AND " + COL_READ + "=0";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_NOTIFICATIONS, columns, selection, selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            notifications.add(cursor.getString(cursor.getColumnIndexOrThrow(COL_MESSAGE)));
        }
        cursor.close();
        return notifications;
    }

    public boolean markNotificationsAsRead(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_READ, 1);
        String whereClause = COL_USER_ID_NOTIF + "=? AND " + COL_READ + "=0";
        String[] whereArgs = {String.valueOf(userId)};
        int result = db.update(TABLE_NOTIFICATIONS, values, whereClause, whereArgs);
        return result > 0;
    }

    public boolean proposeInterview(int applicationId, int studentId, int companyId, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_APPLICATION_ID, applicationId);
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_COMPANY_ID, companyId);
        values.put(COL_TIME, time);
        values.put(COL_STATUS_INTERVIEW, "Proposed");
        long result = db.insert(TABLE_INTERVIEWS, null, values);
        if (result != -1) {
            String internshipTitle = getInternshipTitle(getInternshipIdFromApplication(applicationId));
            insertNotification(studentId, "Nhà tuyển dụng đã đề xuất phỏng vấn cho " + internshipTitle + " vào " + time, "InterviewInvite");
        }
        return result != -1;
    }

    public boolean updateInterviewStatus(int interviewId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_STATUS_INTERVIEW, status);
        String whereClause = COL_INTERVIEW_ID + "=?";
        String[] whereArgs = {String.valueOf(interviewId)};
        int result = db.update(TABLE_INTERVIEWS, values, whereClause, whereArgs);
        return result > 0;
    }

    public List<Interview> getInterviewsByUserId(int userId, String role) {
        List<Interview> interviews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String column = role.equals("student") ? COL_STUDENT_ID : COL_COMPANY_ID;
        String query = "SELECT i." + COL_INTERVIEW_ID + ", i." + COL_APPLICATION_ID + ", i." + COL_STUDENT_ID + ", i." + COL_COMPANY_ID + ", i." + COL_TIME + ", i." + COL_STATUS_INTERVIEW + ", a." + COL_INTERNSHIP_ID + ", t." + COL_TITLE +
                " FROM " + TABLE_INTERVIEWS + " i" +
                " JOIN " + TABLE_APPLICATIONS + " a ON i." + COL_APPLICATION_ID + "=a." + COL_APPLICATION_ID +
                " JOIN " + TABLE_INTERNSHIPS + " t ON a." + COL_INTERNSHIP_ID + "=t." + COL_INTERNSHIP_ID +
                " WHERE i." + column + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        while (cursor.moveToNext()) {
            int interviewId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERVIEW_ID));
            int applicationId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_APPLICATION_ID));
            int studentId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STUDENT_ID));
            int companyId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_COMPANY_ID));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS_INTERVIEW));
            String internshipTitle = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
            interviews.add(new Interview(interviewId, applicationId, studentId, companyId, time, status, internshipTitle));
        }
        cursor.close();
        return interviews;
    }

    private String getApplicationStatusById(int applicationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_STATUS};
        String selection = COL_APPLICATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(applicationId)};
        Cursor cursor = db.query(TABLE_APPLICATIONS, columns, selection, selectionArgs, null, null, null);
        String status = null;
        if (cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS));
        }
        cursor.close();
        return status;
    }

    public int getStudentIdFromApplication(int applicationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_STUDENT_ID};
        String selection = COL_APPLICATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(applicationId)};
        Cursor cursor = db.query(TABLE_APPLICATIONS, columns, selection, selectionArgs, null, null, null);
        int studentId = -1;
        if (cursor.moveToFirst()) {
            studentId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STUDENT_ID));
        }
        cursor.close();
        return studentId;
    }

    public int getInternshipIdFromApplication(int applicationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INTERNSHIP_ID};
        String selection = COL_APPLICATION_ID + "=?";
        String[] selectionArgs = {String.valueOf(applicationId)};
        Cursor cursor = db.query(TABLE_APPLICATIONS, columns, selection, selectionArgs, null, null, null);
        int internshipId = -1;
        if (cursor.moveToFirst()) {
            internshipId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID));
        }
        cursor.close();
        return internshipId;
    }

    public String getInternshipTitle(int internshipId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_TITLE};
        String selection = COL_INTERNSHIP_ID + "=?";
        String[] selectionArgs = {String.valueOf(internshipId)};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, selection, selectionArgs, null, null, null);
        String title = "Unknown";
        if (cursor.moveToFirst()) {
            title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
        }
        cursor.close();
        return title;
    }
    public boolean isNotificationUnread(int userId, String notification) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT read FROM notifications WHERE user_id_notif = ? AND message = ? AND read = 0";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), notification});
        boolean isUnread = cursor.getCount() > 0;
        cursor.close();
        return isUnread;
    }

    public List<String> getAllNotifications(int userId) {
        List<String> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT message FROM notifications WHERE user_id_notif = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                notifications.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notifications;
    }

    public List<Recruiter> getAllRecruiters() {
        List<Recruiter> recruiters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID, COL_EMAIL, COL_NAME, COL_COMPANY};
        String selection = COL_ROLE + "=?";
        String[] selectionArgs = {"recruiter"};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String company = cursor.getString(cursor.getColumnIndexOrThrow(COL_COMPANY));

            recruiters.add(new Recruiter(id, email, name, company));
        }
        cursor.close();
        db.close();
        return recruiters;
    }

    public String getRecruiterEmailById(int recruiterId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_EMAIL};
        String selection = COL_USER_ID + "=? AND " + COL_ROLE + "=?";
        String[] selectionArgs = {String.valueOf(recruiterId), "recruiter"};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        String email = null;
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
        }
        cursor.close();
        db.close();
        return email;
    }


    public String getStudentEmailById(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_EMAIL};
        String selection = COL_USER_ID + "=? AND " + COL_ROLE + "=?";
        String[] selectionArgs = {String.valueOf(studentId), "student"};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        String email = null;
        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
        }
        cursor.close();
        db.close();
        return email;
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_USER_ID, COL_EMAIL, COL_NAME, COL_UNIVERSITY};
        String selection = COL_ROLE + "=?";
        String[] selectionArgs = {"student"};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            String university = cursor.getString(cursor.getColumnIndexOrThrow(COL_UNIVERSITY));

            students.add(new Student(id, email, name, university));
        }
        cursor.close();
        db.close();
        return students;
    }

    public static class Student {
        private int id;
        private String email;
        private String name;
        private String university;

        public Student(int id, String email, String name, String university) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.university = university;
        }

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getUniversity() { return university; }

        @Override
        public String toString() {
            return email + " - " + name + " (" + university + ")";
        }
    }

    // Inner class for Recruiter
    public static class Recruiter {
        private int id;
        private String email;
        private String name;
        private String company;

        public Recruiter(int id, String email, String name, String company) {
            this.id = id;
            this.email = email;
            this.name = name;
            this.company = company;
        }

        public int getId() { return id; }
        public String getEmail() { return email; }
        public String getName() { return name; }
        public String getCompany() { return company; }

        @Override
        public String toString() {
            return email + " - " + name + " (" + company + ")";
        }
    }
}