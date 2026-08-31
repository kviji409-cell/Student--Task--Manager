package com.example.studenttaskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "tasks.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE = "tasks";

    public TaskDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "description TEXT," +
                "due_date TEXT," +
                "completed INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    public long addTask(String title, String description, String dueDate) {
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("due_date", dueDate);
        values.put("completed", 0);
        return getWritableDatabase().insert(TABLE, null, values);
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TABLE, null, null, null, null, null,
                "completed ASC, id DESC");
        try {
            while (c.moveToNext()) {
                tasks.add(new Task(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("title")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        c.getString(c.getColumnIndexOrThrow("due_date")),
                        c.getInt(c.getColumnIndexOrThrow("completed")) == 1
                ));
            }
        } finally {
            c.close();
        }
        return tasks;
    }

    public void setCompleted(long id, boolean completed) {
        ContentValues values = new ContentValues();
        values.put("completed", completed ? 1 : 0);
        getWritableDatabase().update(TABLE, values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(long id) {
        getWritableDatabase().delete(TABLE, "id=?", new String[]{String.valueOf(id)});
    }
}
