package com.echoesnet.eatandmeet.utils.serverdatacache.database.orm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.echoesnet.eatandmeet.utils.serverdatacache.database.DatabaseWrapper;
import com.echoesnet.eatandmeet.utils.serverdatacache.model.ServerData;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by kylewbanks on 2013-10-10.
 */
public class ServerDataORM
{
    //    private static final String TAG = "Elixir-ServerDataORM";
    private static final String TAG = "Elixir"; //日志好看

    private static final String TABLE_NAME = "server_data";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_ID_TYPE = "INTEGER PRIMARY KEY";
    private static final String COLUMN_ID = "id";

    private static final String COLUMN_SIGN_TYPE = "TEXT";
    private static final String COLUMN_SIGN = "sign";

    private static final String COLUMN_TIMESTAMP_TYPE = "TEXT";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    private static final String COLUMN_BODY_TYPE = "TEXT";
    private static final String COLUMN_BODY = "body";


    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " " + COLUMN_ID_TYPE + COMMA_SEP +
                    COLUMN_SIGN + " " + COLUMN_SIGN_TYPE + COMMA_SEP +
                    COLUMN_TIMESTAMP + " " + COLUMN_TIMESTAMP_TYPE + COMMA_SEP +
                    COLUMN_BODY + " " + COLUMN_BODY_TYPE +

                    ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final SimpleDateFormat _dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.ENGLISH);
    //private static DatabaseWrapper databaseWrapper;
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private static final Lock r = rwl.readLock();
    private static final Lock w = rwl.writeLock();

    /**
     * Fetches a single ServerData identified by the specified Sign
     *
     * @param context
     * @param sign
     * @return
     */
    public static ServerData findDatabySign(Context context, String sign)
    {
        //if (databaseWrapper == null)
            DatabaseWrapper  databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getReadableDatabase();

        ServerData sData = null;
        r.lock();
        try
        {
            if (database != null)
            {
                Log.i(TAG, "│ Loading ServerData[" + sign + "]...");
                Cursor cursor = database.rawQuery("SELECT * FROM " + ServerDataORM.TABLE_NAME + " WHERE " + ServerDataORM.COLUMN_SIGN + " = '" + sign + "'", null);

                if (cursor.getCount() > 0)
                {
                    cursor.moveToFirst();
                    sData = cursorToServerData(cursor);
                    Log.i(TAG, "│ ServerData loaded successfully!");
                }
                else
                {
                    Log.i(TAG, "│ ServerData[" + sign + "] didn't exist!");
                }
                database.close();
            }
        } finally
        {
            r.unlock();
        }
        return sData;
    }


    /**
     * Inserts a Post object into the local database
     *
     * @param context
     * @param sData
     * @return
     */
    public static long insertData(Context context, ServerData sData)
    {
        Log.i(TAG, "├───────── insertData ─────────");
        if (findDatabySign(context, sData.getSign()) != null)
        {
            Log.i(TAG, "│ ServerData[" + sData.getSign() + "] already exists in database, update it!");
            return updateData(context, sData);
        }

        ContentValues values = postToContentValues(sData);
        //if (databaseWrapper == null)
            DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();

        long dataId = -1;
        w.lock();
        try
        {
            if (database != null)
            {
                dataId = database.insert(ServerDataORM.TABLE_NAME, "null", values);
                Log.i(TAG, "│ Inserted new ServerData[" + sData.getSign() + "] at id: " + dataId);
            }
        } catch (NullPointerException ex)
        {
            Log.e(TAG, "│ Failed to insert ServerData[" + sData.getSign() + "] due to: " + ex);
        } finally
        {
            if (database != null)
            {
                database.close();
            }
            w.unlock();
        }

        return dataId;
    }

    public static long updateData(Context context, ServerData sData)
    {
        ContentValues values = postToContentValues(sData);
        //if (databaseWrapper == null)
            DatabaseWrapper databaseWrapper = new DatabaseWrapper(context);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();

        long dataId = -1;
        w.lock();
        try
        {
            if (database != null)
            {
                dataId = database.update(ServerDataORM.TABLE_NAME, values, ServerDataORM.COLUMN_SIGN + " = '" + sData.getSign() + "'", null);
                Log.i(TAG, "│ Updated ServerData[" + sData.getSign() + "]... at " + dataId);
            }
        } catch (NullPointerException ex)
        {
            Log.e(TAG, "│ Failed to update ServerData[" + sData.getSign() + "] due to: " + ex);
        } finally
        {
            if (database != null)
            {
                database.close();
            }
            w.unlock();
        }
        return dataId;
    }

    /**
     * Packs a ServerData object into a ContentValues map for use with SQL inserts.
     *
     * @param sData
     * @return
     */
    private static ContentValues postToContentValues(ServerData sData)
    {
        ContentValues values = new ContentValues();
        values.put(ServerDataORM.COLUMN_SIGN, sData.getSign());
        values.put(ServerDataORM.COLUMN_TIMESTAMP, sData.getTimestamp());
        values.put(ServerDataORM.COLUMN_BODY, sData.getBody());

        return values;
    }

    /**
     * Populates a ServerData object with data from a Cursor
     *
     * @param cursor
     * @return
     */
    private static ServerData cursorToServerData(Cursor cursor)
    {
        ServerData sData = new ServerData();
        sData.setSign(cursor.getString(cursor.getColumnIndex(COLUMN_SIGN)));
        sData.setTimestamp(cursor.getString(cursor.getColumnIndex(COLUMN_TIMESTAMP)));
        sData.setBody(cursor.getString(cursor.getColumnIndex(COLUMN_BODY)));

        return sData;
    }

    public static void rebuild(Context ctx)
    {
        DatabaseWrapper databaseWrapper = new DatabaseWrapper(ctx);
        SQLiteDatabase database = databaseWrapper.getWritableDatabase();

        database.execSQL(SQL_DROP_TABLE);
        database.execSQL(SQL_CREATE_TABLE);
    }
}
