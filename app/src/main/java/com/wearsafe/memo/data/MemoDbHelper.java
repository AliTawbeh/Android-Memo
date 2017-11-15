package com.wearsafe.memo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ali on 05-Nov-17.
 */

public class MemoDbHelper extends SQLiteOpenHelper {

    //The name of the data base
    private static final String DATABASE_NAME = "memosDb.db";

    //Databse version, must be incremented when modifying the database schema
    private static final int VERSION = 1;

    MemoDbHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    /**
     * onCreate is called once when the database is first created
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //The memo table consists of two columns, the id, which is the primary key and the description of the memo
        final String DATABASE_SCHEMA = "CREATE TABLE " + MemoContract.MemoEntry.TABLE_NAME  + " ("
                + MemoContract.MemoEntry._ID + " INTEGER PRIMARY KEY, "
                + MemoContract.MemoEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(DATABASE_SCHEMA);
    }

    /**
     * This method is triggered when  the database version changes, it drops the old database and recreates it
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        final String DROP_QUERY = "DROP TABLE IF EXISTS " + MemoContract.MemoEntry.TABLE_NAME;
        sqLiteDatabase.execSQL(DROP_QUERY);
        onCreate(sqLiteDatabase);
    }
}
