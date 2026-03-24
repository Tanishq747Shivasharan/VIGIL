package com.vigil.security.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class VigilDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME    = "vigil_security.db";
    private static final int    DB_VERSION = 1;

    public static final String TABLE_HISTORY  = "scan_history";
    public static final String COL_ID         = "id";
    public static final String COL_SCAN_TYPE  = "scan_type";
    public static final String COL_SUMMARY    = "summary";
    public static final String COL_RISK_LEVEL = "risk_level";
    public static final String COL_RISK_SCORE = "risk_score";
    public static final String COL_DETAILS    = "details";
    public static final String COL_TIMESTAMP  = "timestamp";

    private static final String CREATE_TABLE_HISTORY =
            "CREATE TABLE " + TABLE_HISTORY + " (" +
                    COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SCAN_TYPE  + " TEXT NOT NULL, "                     +
                    COL_SUMMARY    + " TEXT, "                              +
                    COL_RISK_LEVEL + " TEXT, "                              +
                    COL_RISK_SCORE + " INTEGER DEFAULT 0, "                 +
                    COL_DETAILS    + " TEXT, "                              +
                    COL_TIMESTAMP  + " INTEGER NOT NULL"                    +
                    ")";

    private static VigilDatabase instance;

    public static synchronized VigilDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new VigilDatabase(context.getApplicationContext());
        }
        return instance;
    }

    private VigilDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_HISTORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }
}
