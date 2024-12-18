package com.example.tpnews_ungdungdocbao.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabase extends SQLiteOpenHelper {
    private Context context;
    private static final String DATABASE_NAME = "New_user.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "user_info";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ADMIN = "admin_active";



    public MyDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT, " + COLUMN_PASSWORD + " TEXT, " + COLUMN_ADMIN + " INTEGER);";

        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    //Thêm user
    public void addUser(String username, String password, int active) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_USERNAME, username);
        cv.put(COLUMN_PASSWORD, password);
        cv.put(COLUMN_ADMIN, active);
        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1) {
            Toast.makeText(context, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
        }

    }


    //Đọc hết dữ liệu
    public Cursor readAllUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }


    //Kiểm tra user tồn tại
    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?", new String[] {username});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    //Kiểm tra thông tin user
    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ? and " + COLUMN_PASSWORD + " = ?", new String[] {username, password});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    //Lấy active để đăng nhập
    public int getActive(String logUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        int active = 0;

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{logUsername});

        if (cursor != null && cursor.moveToFirst()) {
            active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ADMIN));
            cursor.close();
        }
        db.close();
        return active;
    }

    //Xóa user
    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, "username = ?", new String[]{username});
        db.close();
        return result > 0;
    }

    //Sửa user
    public boolean updateUser(String username, String newPassword, Integer newActive) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (newPassword != null) {
            values.put(COLUMN_PASSWORD, newPassword);
        }
        if (newActive != null) {
            values.put(COLUMN_ADMIN, newActive);
        }

        int newUser = db.update(TABLE_NAME, values, COLUMN_USERNAME + " = ?", new String[]{username});
        return newUser > 0;
    }
}