package com.org.book.note;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
/*
* Reference : SQLite tutorials
* https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/
 */
public class NoteDB extends SQLiteOpenHelper {
    //DB columns
    public static final String dbname = "MyNotes.db";
    public static final String _id = "_id";
    public static final String name = "name";
    public static final String remark = "remark";
    public static final String dates = "dates";
    public static final String image_array="image_array";
    public static final String mynotes = "mynotes";
    private HashMap hp;
    SQLiteDatabase db;

    public NoteDB(Context context) {
        super(context, dbname, null, 1);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table mynotes"
                + "(_id integer primary key, name text,remark text,dates text, image_array blob)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + mynotes);
        onCreate(db);
    }

    // to list all added notes
    public Cursor fetchAll() {
        db = this.getReadableDatabase();
        Cursor mCursor = db.query(mynotes, new String[] { "_id", "name",
                "dates", "remark", "image_array"}, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    //to add any new note
    public boolean addNotes(String name, String dates, String remark, byte[] image_array) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("dates", dates);
        contentValues.put("remark", remark);
        contentValues.put("image_array", image_array);
        db.insert(mynotes, null, contentValues);
        return true;
    }

    // to edit or view a single note
    public Cursor getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor z = db.rawQuery("select * from " + mynotes + " where _id=" + id
                + "", null);
        return z;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, mynotes);
        return numRows;
    }

    //to update any note
    public boolean updateNote(Integer id, String name, String dates,
                               String remark, byte[] image_array) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("dates", dates);
        contentValues.put("remark", remark);
        contentValues.put("image_array", image_array);
        db.update(mynotes, contentValues, "_id = ? ",
                new String[] { Integer.toString(id) });
        return true;
    }


    public Integer deleteNote(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(mynotes, "_id = ? ",
                new String[] { Integer.toString(id) });
    }
    public ArrayList getAll() {
        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + mynotes, null);
        res.moveToFirst();
        while (res.isAfterLast() == false) {
            array_list.add(res.getString(res.getColumnIndex("_id")));
            array_list.add(res.getString(res.getColumnIndex(remark)));
            array_list.add(res.getString(res.getColumnIndex(dates)));
            array_list.add(res.getString(res.getColumnIndex(name)));
            array_list.add(res.getString(res.getColumnIndex(image_array)));
            res.moveToNext();
        }
        return array_list;
    }
}
