package com.learning.firhan.todo.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.learning.firhan.todo.Models.TodoItem;

public class TodoDatabaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;
    public static final String DATABASE_NAME = "Todo.db";
    public static final String TABLE_NAME = "todo_table";
    public static final String COL_Id = "Id";
    public static final String COL_Title = "Title";
    public static final String COL_Description = "Description";

    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        sqLiteDatabase = this.getWritableDatabase();
    }

    public long insertData(TodoItem todoItem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_Title, todoItem.getTitle());
        contentValues.put(COL_Description, todoItem.getDesciption());
        long newRowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return newRowId;
    }

    public long updateData(TodoItem todoItem){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_Title, todoItem.getTitle());
        contentValues.put(COL_Description, todoItem.getDesciption());
        long newRowId = sqLiteDatabase.update(TABLE_NAME, contentValues, COL_Id+"="+todoItem.getId(), null);
        return newRowId;
    }

    public int deleteData(int id){
        int deletedRowsId = sqLiteDatabase.delete(TABLE_NAME, COL_Id+"="+id, null);
        return deletedRowsId;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME+" ORDER BY Id DESC", null);
        return result;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+TABLE_NAME +
                "("+COL_Id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ""+COL_Title+" VARCHAR," +
                ""+COL_Description+" VARCHAR"+
                ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
