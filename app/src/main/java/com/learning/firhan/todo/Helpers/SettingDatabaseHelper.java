package com.learning.firhan.todo.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learning.firhan.todo.Constant.SettingNames;
import com.learning.firhan.todo.Models.Setting;
import com.learning.firhan.todo.Models.TodoItem;

public class SettingDatabaseHelper extends SQLiteOpenHelper {
    SQLiteDatabase sqLiteDatabase;
    public static final String DATABASE_NAME = "Setting.db";
    public static final String TABLE_NAME = "setting_table";
    public static final String COL_Id = "Id";
    public static final String COL_Title = "Title";
    public static final String COL_Value = "Value";

    public SettingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        sqLiteDatabase = this.getWritableDatabase();
    }

    public long insertData(Setting setting){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_Title, setting.getTitle());
        contentValues.put(COL_Value, setting.getValue());
        long newRowId = sqLiteDatabase.insert(TABLE_NAME, null, contentValues);
        return newRowId;
    }

    public long updateData(String settingName, String value){
        //get setting by name
        Setting setting = getSettingByTitle(settingName, true);

        long rowId = 0;

        if(setting!=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_Value, value);
            rowId = sqLiteDatabase.update(TABLE_NAME, contentValues, "Id="+setting.getId(), null);
        }

        return rowId;
    }

    public int deleteData(int id){
        int deletedRowsId = sqLiteDatabase.delete(TABLE_NAME, COL_Id+"="+id, null);
        return deletedRowsId;
    }

    public int deleteDataByTitle(String title){
        int deletedRowsId = sqLiteDatabase.delete(TABLE_NAME, COL_Title+"='"+title+"'", null);
        return deletedRowsId;
    }

    public Setting getSettingByTitle(String title, Boolean createNew){
        Setting setting = null;

        SQLiteDatabase db = this.getReadableDatabase();
        try{
            Log.d("SettingDatabaseHelper", "Finding: "+title);
            Cursor result = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE Title='"+title+"'", null);
            if(result!=null){
                if(result.getCount() > 0){
                    result.moveToFirst();
                    setting = new Setting(result.getInt(0), result.getString(1), result.getString(2));
                }else {
                    if (createNew) {
                        //delete possible duplicate title
                        deleteDataByTitle(title);

                        //create new
                        setting = new Setting(0, title, "");
                        long newRowId = insertData(setting);

                        Log.d("SettingDatabaseHelper", title + " Created with Id:" + newRowId);
                    }
                }
            }
        }catch (Exception ex){
            Log.d("SettingDatabaseHelper", ex.getMessage());
        }

        return setting;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+TABLE_NAME +
                "("+COL_Id+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ""+COL_Title+" VARCHAR," +
                ""+COL_Value+" VARCHAR"+
                ")"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}
