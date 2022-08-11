package com.example.finalproject_newsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class dbOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "bbcArticlesDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "BBC_ARTICLES";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_LINKS = "LINKS";
    public final static String COL_DESCRIPTION = "DESCRIPTION";
    public final static String COL_DATE = "DATE";
    public final static String COL_ID = "_id";
    private Context ctx;
    String[] columns = {dbOpener.COL_TITLE, dbOpener.COL_LINKS, dbOpener.COL_DESCRIPTION, dbOpener.COL_DATE};

    public dbOpener(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);

        this.ctx = ctx;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TITLE
                + " TEXT," + COL_LINKS + " TEXT," + COL_DESCRIPTION + " TEXT," + COL_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    void addItem(String title, String link, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cnt = new ContentValues();
        cnt.put(COL_TITLE, title);
        cnt.put(COL_LINKS, link);
        cnt.put(COL_DESCRIPTION, description);
        cnt.put(COL_DATE, date);
        db.insert(TABLE_NAME, null, cnt);
    }

    Cursor readData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("SELECT * from " + TABLE_NAME, null);
        return results;
    }

    void deleteRow(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "TITLE=?";
        String[] whereArgs = new String[]{String.valueOf(name)};
        db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public void printCursor(Cursor c) {
        SQLiteDatabase db = this.getReadableDatabase();
        int version = db.getVersion();
        int colCount = c.getColumnCount();
        String[] colNames = c.getColumnNames();
        int rows = c.getCount();
        ArrayList<String> rowValues = new ArrayList<>();

        int titleColIndex = c.getColumnIndex(dbOpener.COL_TITLE);
        int linksColIndex = c.getColumnIndex(dbOpener.COL_LINKS);
        int descriptionColIndex = c.getColumnIndex(dbOpener.COL_DESCRIPTION);
        int dateColIndex = c.getColumnIndex(dbOpener.COL_DATE);
        int idColIndex = c.getColumnIndex(dbOpener.COL_ID);

        while (c.moveToNext()) {
            String title = c.getString(titleColIndex);
            String links = c.getString(linksColIndex);
            String description = c.getString(descriptionColIndex);
            String date = c.getString(dateColIndex);
            long id = c.getLong(idColIndex);

            String string_ID = String.valueOf(id);
            rowValues.add(new String(title));
            rowValues.add(new String(links));
            rowValues.add(new String(description));
            rowValues.add(new String(date));
            rowValues.add(new String(string_ID));

            Log.d("Database version", String.valueOf(version));
            Log.d("Row count", String.valueOf(rows));
            Log.d("Column count", String.valueOf(colCount));
            Log.d("Column names", Arrays.toString(colNames));
            Log.d("Row values", rowValues.toString());
        }
    }
}


