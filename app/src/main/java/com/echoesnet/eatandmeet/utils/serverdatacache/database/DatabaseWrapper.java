package com.echoesnet.eatandmeet.utils.serverdatacache.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.echoesnet.eatandmeet.utils.serverdatacache.database.orm.ServerDataORM;


public class DatabaseWrapper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseWrapper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "elixir.db";

    public DatabaseWrapper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called if the database named DATABASE_NAME doesn't exist in order to create it.
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(TAG, "Creating database [" + DATABASE_NAME + " v." + DATABASE_VERSION + "]...");
        sqLiteDatabase.execSQL(ServerDataORM.SQL_DROP_TABLE);
        sqLiteDatabase.execSQL(ServerDataORM.SQL_CREATE_TABLE);
    }

    /**
     * Called when the DATABASE_VERSION is increased.
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database ["+DATABASE_NAME+" v." + oldVersion+"] to ["+DATABASE_NAME+" v." + newVersion+"]...");

        sqLiteDatabase.execSQL(ServerDataORM.SQL_DROP_TABLE);

        onCreate(sqLiteDatabase);
    }
}
