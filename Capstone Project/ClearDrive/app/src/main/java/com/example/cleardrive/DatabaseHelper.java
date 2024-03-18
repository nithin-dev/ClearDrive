package com.example.cleardrive;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserData.db";
    private static final int DATABASE_VERSION = 1;

    // Define table and column names in UserModel
    private static final String TABLE_NAME = "users";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_MOBILE = "mobile";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // SQL statement to create the table
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_FIRST_NAME + " TEXT," +
                    COLUMN_LAST_NAME + " TEXT," +
                    COLUMN_MOBILE + " TEXT," +
                    COLUMN_EMAIL + " TEXT," +
                    COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                    COLUMN_PASSWORD + " TEXT)";

    // SQL statement to delete the table
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long addUser(String firstName, String lastName, String mobile, String email, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_MOBILE, mobile);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_NAME, null, values);
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {COLUMN_USERNAME, COLUMN_PASSWORD};
        String selection = COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }
    @SuppressLint("Range")
    public UserModel getUserData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_MOBILE, COLUMN_EMAIL};
        String selection = COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        UserModel userData = null;
        if (cursor.moveToFirst()) {
            userData = new UserModel();
            userData.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
            userData.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
            userData.setMobile(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)));
            userData.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
        }
        cursor.close();
        return userData;
    }
    @SuppressLint("Range")
    public UserModel getUserData() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_FIRST_NAME, COLUMN_LAST_NAME, COLUMN_MOBILE, COLUMN_EMAIL, COLUMN_USERNAME, COLUMN_PASSWORD};
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        UserModel userData = null;
        if (cursor.moveToFirst()) {
            userData = new UserModel();
            userData.setFirstName(cursor.getString(cursor.getColumnIndex(COLUMN_FIRST_NAME)));
            userData.setLastName(cursor.getString(cursor.getColumnIndex(COLUMN_LAST_NAME)));
            userData.setMobile(cursor.getString(cursor.getColumnIndex(COLUMN_MOBILE)));
            userData.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
            userData.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME)));
            userData.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
        }
        cursor.close();
        return userData;
    }
}
