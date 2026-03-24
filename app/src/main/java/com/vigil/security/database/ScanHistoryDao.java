package com.vigil.security.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vigil.security.models.ScanRecord;

import java.util.ArrayList;
import java.util.List;

import static com.vigil.security.database.VigilDatabase.*;

public class ScanHistoryDao {

    private final VigilDatabase dbHelper;

    public ScanHistoryDao(VigilDatabase dbHelper) {
        this.dbHelper = dbHelper;
    }

    // ── INSERT ──────────────────────────────────────────────────────────────

    public long insert(ScanRecord record) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SCAN_TYPE,  record.getScanType());
        values.put(COL_SUMMARY,    record.getSummary());
        values.put(COL_RISK_LEVEL, record.getRiskLevel());
        values.put(COL_RISK_SCORE, record.getRiskScore());
        values.put(COL_DETAILS,    record.getDetails());
        values.put(COL_TIMESTAMP,  record.getTimestamp());
        return db.insert(TABLE_HISTORY, null, values);
    }

    // ── SELECT ──────────────────────────────────────────────────────────────

    public List<ScanRecord> getAll() {
        List<ScanRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null, null, null, null, null,
                COL_TIMESTAMP + " DESC");
        while (cursor.moveToNext()) records.add(cursorToRecord(cursor));
        cursor.close();
        return records;
    }

    public List<ScanRecord> getByType(String scanType) {
        List<ScanRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null,
                COL_SCAN_TYPE + " = ?", new String[]{scanType},
                null, null, COL_TIMESTAMP + " DESC");
        while (cursor.moveToNext()) records.add(cursorToRecord(cursor));
        cursor.close();
        return records;
    }

    public List<ScanRecord> getRecent(int limit) {
        List<ScanRecord> records = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_HISTORY +
                        " ORDER BY " + COL_TIMESTAMP + " DESC LIMIT ?",
                new String[]{String.valueOf(limit)});
        while (cursor.moveToNext()) records.add(cursorToRecord(cursor));
        cursor.close();
        return records;
    }

    public int getTotalCount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_HISTORY, null);
        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public ScanRecord getLatestByType(String scanType) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_HISTORY, null,
                COL_SCAN_TYPE + " = ?", new String[]{scanType},
                null, null, COL_TIMESTAMP + " DESC", "1");
        ScanRecord record = null;
        if (cursor.moveToFirst()) record = cursorToRecord(cursor);
        cursor.close();
        return record;
    }

    // ── DELETE ──────────────────────────────────────────────────────────────

    public int deleteById(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(TABLE_HISTORY, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(TABLE_HISTORY, null, null);
    }

    // ── PRIVATE HELPER ──────────────────────────────────────────────────────

    private ScanRecord cursorToRecord(Cursor cursor) {
        ScanRecord record = new ScanRecord(
                cursor.getString(cursor.getColumnIndexOrThrow(COL_SCAN_TYPE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_SUMMARY)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_RISK_LEVEL)),
                cursor.getInt   (cursor.getColumnIndexOrThrow(COL_RISK_SCORE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DETAILS))
        );
        record.setId       (cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        record.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
        return record;
    }
}
