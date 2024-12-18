package com.example.tpnews_ungdungdocbao.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.tpnews_ungdungdocbao.Models.Article;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tpnews_database.db";
    private static final int DATABASE_VERSION = 2;

    // Bảng News
    private static final String TABLE_NEWS = "News";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_OUTLET_NAME = "outletname";
    private static final String COLUMN_OUTLET_LOGO = "outletlogo";
    private static final String COLUMN_CATEGORY = "category";

    // Câu lệnh tạo bảng
    private static final String CREATE_TABLE_NEWS = "CREATE TABLE " + TABLE_NEWS + " (" +
            COLUMN_ID + " TEXT PRIMARY KEY, " +
            COLUMN_TITLE + " TEXT, " +
            COLUMN_DESCRIPTION + " TEXT, " +
            COLUMN_CONTENT + " TEXT, " +
            COLUMN_IMAGE + " TEXT, " +
            COLUMN_OUTLET_NAME + " TEXT, " +
            COLUMN_OUTLET_LOGO + " TEXT, " +
            COLUMN_CATEGORY + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_NEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(db);
    }

    // Lấy danh sách bài báo theo danh mục
    public ArrayList<Article> getNewsByCategory(String category) {
        ArrayList<Article> articleList = new ArrayList<>();
        String query = category == null
                ? "SELECT * FROM " + TABLE_NEWS
                : "SELECT * FROM " + TABLE_NEWS + " WHERE " + COLUMN_CATEGORY + "=?";
        String[] args = category != null ? new String[]{category} : null;

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, args)) {

            while (cursor.moveToNext()) {
                Article article = new Article(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OUTLET_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OUTLET_LOGO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                );
                articleList.add(article);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching news: " + e.getMessage());
        }
        return articleList;
    }

    // Thêm danh sách bài báo vào SQLite
    public void insertNews(ArrayList<Article> articles) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.beginTransaction();
            try {
                for (Article article : articles) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_ID, article.getId());
                    values.put(COLUMN_TITLE, article.getTitle());
                    values.put(COLUMN_DESCRIPTION, article.getDescription());
                    values.put(COLUMN_CONTENT, article.getContent());
                    values.put(COLUMN_IMAGE, article.getImage());
                    values.put(COLUMN_OUTLET_NAME, article.getOutletName());
                    values.put(COLUMN_OUTLET_LOGO, article.getOutletLogo());
                    values.put(COLUMN_CATEGORY, article.getCategory());

                    db.insert(TABLE_NEWS, null, values);
                }
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting news: " + e.getMessage());
        }
    }

    // Xóa tất cả bài báo
    public void clearAllNews() {
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            db.delete(TABLE_NEWS, null, null);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error clearing news: " + e.getMessage());
        }
    }
    public Article getNewsOffline(String id) {
        // Kiểm tra nếu id null, trả về luôn null vì không có bài báo nào để lấy
        if (id == null) return null;

        Article article = null;
        String query = "SELECT * FROM " + TABLE_NEWS + " WHERE " + COLUMN_ID + "=?";
        String[] args = new String[]{id}; // Tham số truyền vào câu truy vấn

        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery(query, args)) {

            if (cursor.moveToNext()) { // Nếu tìm thấy bài báo, tạo đối tượng Article
                article = new Article(
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OUTLET_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_OUTLET_LOGO)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                );
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching news: " + e.getMessage());
        }

        return article;
    }

}

