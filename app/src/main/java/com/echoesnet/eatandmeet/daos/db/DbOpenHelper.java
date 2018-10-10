/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echoesnet.eatandmeet.daos.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.orhanobut.logger.Logger;


public class DbOpenHelper extends SQLiteOpenHelper
{
    private final static String TAG = DbOpenHelper.class.getSimpleName();
    //每次修改数据库记得更新这边的版本号--wb
    private static final int DATABASE_VERSION = 7;
    private static DbOpenHelper instance;

    private static final String USER_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_U_ID + " TEXT, "
            + UserDao.COLUMN_ID + " TEXT, "
            + UserDao.COLUMN_AGE + " TEXT, "
            + UserDao.COLUMN_GENDER + " TEXT, "
            + UserDao.COLUMN_LEVEL + " TEXT, "
            + UserDao.COLUMN_REMARK + " TEXT, "
            + UserDao.COLUMN_VUSER + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    private static final String USER_TABLE_DELETE = "DROP TABLE IF EXISTS "
            + UserDao.TABLE_NAME;

    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InviteMessgeDao.TABLE_NAME + " ("
            + InviteMessgeDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InviteMessgeDao.COLUMN_NAME_FROM + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_ID + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUP_Name + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_REASON + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_ISINVITEFROMME + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InviteMessgeDao.COLUMN_NAME_TIME + " TEXT, "
            + InviteMessgeDao.COLUMN_NAME_GROUPINVITER + " TEXT); ";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.ROBOT_TABLE_NAME + " ("
            + UserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + UserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";

    private DbOpenHelper(Context context)
    {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
    }

    public static DbOpenHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    private static String getUserDatabaseName()
    {
        return HuanXinIMHelper.getInstance().getCurrentUsernName() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Logger.t(TAG).d("Old>" + oldVersion + "New>" + newVersion);
        //此处没有考虑将旧有数据导入新的数据库中--wb
        if (oldVersion < DATABASE_VERSION)
        {
            db.execSQL(USER_TABLE_DELETE);
            db.execSQL(USER_TABLE_CREATE);
        }
    }

    public void closeDB()
    {
        if (instance != null)
        {
            try
            {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e)
            {
                e.printStackTrace();
            }
            instance = null;
        }
    }
}
