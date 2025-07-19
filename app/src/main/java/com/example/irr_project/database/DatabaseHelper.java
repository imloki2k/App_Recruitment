package com.example.irr_project.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.irr_project.Internship;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
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

    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
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
                COL_RESUME + " TEXT, " + // Lưu Uri hoặc đường dẫn file
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
        db.execSQL("INSERT INTO users (email, password, role, name, university) VALUES ('student1@example.com', 'pass123', 'student', 'Nguyen Van A', 'Hanoi University');");
        db.execSQL("INSERT INTO users (email, password, role, name, company) VALUES ('recruiter1@example.com', 'pass789', 'recruiter', 'FPT Software', 'FPT Corporation');");
        db.execSQL("INSERT INTO internships (title, company_id, location, duration, field, description, requirements, stipend, deadline, date_posted) VALUES ('Android Developer Intern', 2, 'Hanoi', '3 months', 'IT', 'Develop Android apps', 'Java/Kotlin', '5000000 VND', '2025-08-01', '2025-07-01');");
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
        values.put(COL_RESUME, resumeUri); // Lưu Uri hoặc đường dẫn file
        values.put(COL_STATUS, status);
        long result = db.insert(TABLE_APPLICATIONS, null, values);
        return result != -1;
    }

    public String getResumeUri(int studentId, int internshipId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_RESUME};
        String selection = COL_STUDENT_ID + "=? AND " + COL_INTERNSHIP_ID + "=?";
        String[] selectionArgs = {String.valueOf(studentId), String.valueOf(internshipId)};
        Cursor cursor = db.query(TABLE_APPLICATIONS, columns, selection, selectionArgs, null, null, null);
        String resumeUri = null;
        if (cursor.moveToFirst()) {
            resumeUri = cursor.getString(cursor.getColumnIndexOrThrow(COL_RESUME));
        }
        cursor.close();
        return resumeUri;
    }

    // Existing methods (getUserRole, getAllInternships, etc.) remain unchanged
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

    public List<Internship> getAllInternships() {
        List<Internship> internships = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COL_INTERNSHIP_ID, COL_TITLE, COL_COMPANY_ID, COL_LOCATION, COL_DURATION, COL_FIELD, COL_DATE_POSTED, COL_DESCRIPTION, COL_REQUIREMENTS, COL_STIPEND, COL_DEADLINE};
        Cursor cursor = db.query(TABLE_INTERNSHIPS, columns, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idIndex = cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(COL_TITLE);
            int companyIdIndex = cursor.getColumnIndexOrThrow(COL_COMPANY_ID);
            int locationIndex = cursor.getColumnIndexOrThrow(COL_LOCATION);
            int durationIndex = cursor.getColumnIndexOrThrow(COL_DURATION);
            int fieldIndex = cursor.getColumnIndexOrThrow(COL_FIELD);
            int datePostedIndex = cursor.getColumnIndexOrThrow(COL_DATE_POSTED);
            int descriptionIndex = cursor.getColumnIndexOrThrow(COL_DESCRIPTION);
            int requirementsIndex = cursor.getColumnIndexOrThrow(COL_REQUIREMENTS);
            int stipendIndex = cursor.getColumnIndexOrThrow(COL_STIPEND);
            int deadlineIndex = cursor.getColumnIndexOrThrow(COL_DEADLINE);
            String companyName = getCompanyName(db, cursor.getInt(companyIdIndex));
            internships.add(new Internship(
                    cursor.getInt(idIndex),
                    cursor.getString(titleIndex),
                    companyName,
                    cursor.getString(locationIndex),
                    cursor.getString(durationIndex),
                    cursor.getString(fieldIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getString(requirementsIndex),
                    cursor.getString(stipendIndex),
                    cursor.getString(deadlineIndex),
                    cursor.getString(datePostedIndex)
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
            int idIndex = cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(COL_TITLE);
            int companyIdIndex = cursor.getColumnIndexOrThrow(COL_COMPANY_ID);
            int locationIndex = cursor.getColumnIndexOrThrow(COL_LOCATION);
            int durationIndex = cursor.getColumnIndexOrThrow(COL_DURATION);
            int fieldIndex = cursor.getColumnIndexOrThrow(COL_FIELD);
            int datePostedIndex = cursor.getColumnIndexOrThrow(COL_DATE_POSTED);
            int descriptionIndex = cursor.getColumnIndexOrThrow(COL_DESCRIPTION);
            int requirementsIndex = cursor.getColumnIndexOrThrow(COL_REQUIREMENTS);
            int stipendIndex = cursor.getColumnIndexOrThrow(COL_STIPEND);
            int deadlineIndex = cursor.getColumnIndexOrThrow(COL_DEADLINE);
            internship = new Internship(
                    cursor.getInt(idIndex),
                    cursor.getString(titleIndex),
                    getCompanyName(db, cursor.getInt(companyIdIndex)),
                    cursor.getString(locationIndex),
                    cursor.getString(durationIndex),
                    cursor.getString(fieldIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getString(requirementsIndex),
                    cursor.getString(stipendIndex),
                    cursor.getString(deadlineIndex),
                    cursor.getString(datePostedIndex)
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
            int idIndex = cursor.getColumnIndexOrThrow(COL_INTERNSHIP_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(COL_TITLE);
            int companyIdIndex = cursor.getColumnIndexOrThrow(COL_COMPANY_ID);
            int locationIndex = cursor.getColumnIndexOrThrow(COL_LOCATION);
            int durationIndex = cursor.getColumnIndexOrThrow(COL_DURATION);
            int fieldIndex = cursor.getColumnIndexOrThrow(COL_FIELD);
            int datePostedIndex = cursor.getColumnIndexOrThrow(COL_DATE_POSTED);
            int descriptionIndex = cursor.getColumnIndexOrThrow(COL_DESCRIPTION);
            int requirementsIndex = cursor.getColumnIndexOrThrow(COL_REQUIREMENTS);
            int stipendIndex = cursor.getColumnIndexOrThrow(COL_STIPEND);
            int deadlineIndex = cursor.getColumnIndexOrThrow(COL_DEADLINE);
            String companyName = getCompanyName(db, cursor.getInt(companyIdIndex));
            internships.add(new Internship(
                    cursor.getInt(idIndex),
                    cursor.getString(titleIndex),
                    companyName,
                    cursor.getString(locationIndex),
                    cursor.getString(durationIndex),
                    cursor.getString(fieldIndex),
                    cursor.getString(descriptionIndex),
                    cursor.getString(requirementsIndex),
                    cursor.getString(stipendIndex),
                    cursor.getString(deadlineIndex),
                    cursor.getString(datePostedIndex)
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

    public void closeDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}