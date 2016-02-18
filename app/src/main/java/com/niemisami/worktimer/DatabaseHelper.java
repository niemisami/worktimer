package com.niemisami.worktimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.IDN;

/**
 * Created by Sami on 27.12.2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String TAG = "DatabaseHelper";
    private static final String DB_NAME = "worktimer_sqlite";
    private static final int DB_VERSION = 1;


    private static final String TABLE_WORK = "table_work";
    private static final String WORK_ID = "work_id";
    private static final String WORK_START_TIME = "work_start_time";
    private static final String WORK_END_TIME = "work_end_time";
    private static final String WORK_WHOLE_BREAK_TIME = "work_break_time";

    private static DatabaseHelper mDatabaseHelper;

    public static DatabaseHelper getInstance(Context context) {
        if(mDatabaseHelper == null) {
            mDatabaseHelper = new DatabaseHelper(context);
        }
        return mDatabaseHelper;
    }

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_WORK + "(" +
                WORK_ID + " INTEGER PRIMARY KEY, " +
                WORK_START_TIME + " INTEGER," +
                WORK_END_TIME + " INTEGER," +
                WORK_WHOLE_BREAK_TIME + " INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Updating database from " + oldVersion + " to " + newVersion);
//        Drop all tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK);
//        create new database
        onCreate(db);
    }

    public long insertWork(Workday workday) {
        ContentValues cv = new ContentValues();
        cv.put(WORK_ID, workday.getId());
        cv.put(WORK_START_TIME, workday.getStartTime());
        cv.put(WORK_END_TIME, workday.getEndTime());
        cv.put(WORK_WHOLE_BREAK_TIME, workday.getBreakTime());
        long result = getWritableDatabase().insert(TABLE_WORK, null, cv);
        return result;
    }


    public Workday queryWorkday(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORK,
                null, WORK_ID +"=?",
                new String[]{Integer.toString(id)},
                null, null, WORK_START_TIME, "asc");
        Log.d(TAG, "count " + cursor.getCount());
        if(cursor.isBeforeFirst() ||cursor.isAfterLast()) {
            return null;
        } else {
            cursor.moveToNext();

            Workday wd = new Workday(id);
            wd.setStartTime(cursor.getInt(cursor.getColumnIndex(WORK_START_TIME)));
            wd.setEndTime(cursor.getInt(cursor.getColumnIndex(WORK_END_TIME)));
            wd.setBreakTime(cursor.getInt(cursor.getColumnIndex(WORK_WHOLE_BREAK_TIME)));
            return wd;
        }

    }

}
