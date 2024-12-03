package com.example.tpnews_ungdungdocbao;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tpnews_database.db";
    private static final int DATABASE_VERSION = 1;
//    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE Category (" +
//            "id INTEGER PRIMARY KEY, " +
//            "name TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(CREATE_TABLE_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS Category");
//        onCreate(db);
    }

//    public ArrayList<Category> getCategory() {
//        ArrayList<Category> catList = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM Category", null);
//        if (cursor.moveToFirst()) {
//            do {
//                String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
//                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
//                catList.add(new Category(id, name));
//            } while (cursor.moveToNext());
//        }
//
//        cursor.close();
//        db.close();
//        return catList;
//    }
//    public void insertCategory (Category cat)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put("id",cat.getId());
//        values.put("name", cat.getName());
//        db.insert("Category", null, values);
//        db.close();
//    }

}
