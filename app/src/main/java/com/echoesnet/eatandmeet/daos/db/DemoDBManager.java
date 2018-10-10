package com.echoesnet.eatandmeet.daos.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.daos.db.InviteMessage.InviteMesageStatus;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.ChatCommonUtils;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class DemoDBManager
{
    private static final String TAG = DemoDBManager.class.getSimpleName();

    static private DemoDBManager dbMgr = new DemoDBManager();
    private DbOpenHelper dbHelper;

    private DemoDBManager()
    {
        dbHelper = DbOpenHelper.getInstance(EamApplication.getInstance().getApplicationContext());
    }

    public static synchronized DemoDBManager getInstance()
    {
        if (dbMgr == null)
        {
            dbMgr = new DemoDBManager();
        }
        return dbMgr;
    }

    /**
     * 保存联系人列表
     *
     * @param contactList
     */
    synchronized public void saveContactList(List<EaseUser> contactList)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Logger.t(TAG).d("开始保存联系人信息");
        if (db.isOpen())
        {
            db.delete(UserDao.TABLE_NAME, null, null);
            for (EaseUser user : contactList)
            {
                ContentValues values = new ContentValues();
                values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
                if (user.getNickName() != null)
                    values.put(UserDao.COLUMN_NAME_NICK, user.getNickName());
                if (user.getAvatar() != null)
                    values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
                if (user.getuId() != null)
                    values.put(UserDao.COLUMN_U_ID, user.getuId());
                if (user.getId() != null)
                    values.put(UserDao.COLUMN_ID, user.getId());
                if (user.getAge() != null)
                    values.put(UserDao.COLUMN_AGE, user.getAge());
                if (user.getSex() != null)
                    values.put(UserDao.COLUMN_GENDER, user.getSex());
                if (user.getLevel() != null)
                    values.put(UserDao.COLUMN_LEVEL, user.getLevel());
                if (user.getRemark() != null)
                    values.put(UserDao.COLUMN_REMARK, user.getRemark());
                if (user.getIsVuser() != null)
                    values.put(UserDao.COLUMN_VUSER, user.getIsVuser());

                db.replace(UserDao.TABLE_NAME, null, values);
                Logger.t(TAG).d("联系人信息》" + user.getAvatar() + "  uid： " + user.getuId());
            }
        }
    }

    /**
     * 获得联系人列表
     *
     * @return
     */
    synchronized public Map<String, EaseUser> getContactList()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, EaseUser> users = new Hashtable<String, EaseUser>();
        if (db.isOpen())
        {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME, null);
            while (cursor.moveToNext())
            {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String uId = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_U_ID));
                String id = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_ID));
                String age = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_AGE));
                String gender = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_GENDER));
                String level = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_LEVEL));
                String remark = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_REMARK));
                String vUser = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_VUSER));
                EaseUser user = new EaseUser(username);
                user.setNickName(nick);
                user.setAvatar(avatar);
                user.setuId(uId);
                user.setId(id);
                user.setAge(age);
                user.setSex(gender);
                user.setLevel(level);
                user.setRemark(remark);
                user.setIsVuser(vUser);
                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT))
                {
                    user.setInitialLetter("");
                }
                else
                {
                    ChatCommonUtils.setUserInitialLetter(user);
                }
                users.put(username, user);
            }
            cursor.close();
        }
        Logger.t(TAG).d("------>从本地数据库获取联系人列表》" + users.toString());
        return users;
    }

    /**
     * delete a contact
     *
     * @param username
     */
    synchronized public void deleteContact(String username)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen())
        {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * save a contact
     *
     * @param user
     */
    synchronized public void saveContact(EaseUser user)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
        if (user.getNickName() != null)
            values.put(UserDao.COLUMN_NAME_NICK, user.getNickName());
        if (user.getAvatar() != null)
            values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
        if (user.getuId() != null)
            values.put(UserDao.COLUMN_U_ID, user.getuId());
        if (user.getId() != null)
            values.put(UserDao.COLUMN_ID, user.getId());
        if (user.getAge() != null)
            values.put(UserDao.COLUMN_AGE, user.getAge());
        if (user.getSex() != null)
            values.put(UserDao.COLUMN_GENDER, user.getSex());
        if (user.getLevel() != null)
            values.put(UserDao.COLUMN_LEVEL, user.getLevel());
        if (user.getRemark() != null)
            values.put(UserDao.COLUMN_REMARK, user.getRemark());
        if (user.getIsVuser() != null)
            values.put(UserDao.COLUMN_VUSER, user.getIsVuser());

        if (db.isOpen())
        {
            db.replace(UserDao.TABLE_NAME, null, values);
        }
        Logger.t(TAG).d("------>保存联系人到本地》" + user.toString());
    }


    public void setDisabledGroups(List<String> groups)
    {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups()
    {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids)
    {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds()
    {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList)
    {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList)
        {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen())
        {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals(""))
        {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array != null && array.length > 0)
        {
            List<String> list = new ArrayList<String>();
            for (String str : array)
            {
                list.add(str);
            }

            return list;
        }

        return null;
    }

    /**
     * save a message
     *
     * @param message
     * @return return cursor of the message
     */
    public synchronized Integer saveMessage(InviteMessage message)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int id = -1;
        if (db.isOpen())
        {
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_FROM, message.getFrom());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_ID, message.getGroupId());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUP_Name, message.getGroupName());
            values.put(InviteMessgeDao.COLUMN_NAME_REASON, message.getReason());
            values.put(InviteMessgeDao.COLUMN_NAME_TIME, message.getTime());
            values.put(InviteMessgeDao.COLUMN_NAME_STATUS, message.getStatus().ordinal());
            values.put(InviteMessgeDao.COLUMN_NAME_GROUPINVITER, message.getGroupInviter());
            db.insert(InviteMessgeDao.TABLE_NAME, null, values);

            Cursor cursor = db.rawQuery("select last_insert_rowid() from " + InviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst())
            {
                id = cursor.getInt(0);
            }

            cursor.close();
        }
        return id;
    }

    /**
     * update message
     *
     * @param msgId
     * @param values
     */
    synchronized public void updateMessage(int msgId, ContentValues values)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen())
        {
            db.update(InviteMessgeDao.TABLE_NAME, values, InviteMessgeDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    /**
     * get messges
     *
     * @return
     */
    synchronized public List<InviteMessage> getMessagesList()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<InviteMessage> msgs = new ArrayList<InviteMessage>();
        if (db.isOpen())
        {
            Cursor cursor = db.rawQuery("select * from " + InviteMessgeDao.TABLE_NAME + " desc", null);
            while (cursor.moveToNext())
            {
                InviteMessage msg = new InviteMessage();
                int id = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_FROM));
                String groupid = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_ID));
                String groupname = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUP_Name));
                String reason = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_REASON));
                long time = cursor.getLong(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_TIME));
                int status = cursor.getInt(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_STATUS));
                String groupInviter = cursor.getString(cursor.getColumnIndex(InviteMessgeDao.COLUMN_NAME_GROUPINVITER));

                msg.setId(id);
                msg.setFrom(from);
                msg.setGroupId(groupid);
                msg.setGroupName(groupname);
                msg.setReason(reason);
                msg.setTime(time);
                msg.setGroupInviter(groupInviter);

                if (status == InviteMessage.InviteMesageStatus.BEINVITEED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEINVITEED);
                else if (status == InviteMesageStatus.BEAGREED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEAGREED);
                else if (status == InviteMesageStatus.BEREFUSED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEREFUSED);
                else if (status == InviteMesageStatus.AGREED.ordinal())
                    msg.setStatus(InviteMesageStatus.AGREED);
                else if (status == InviteMesageStatus.REFUSED.ordinal())
                    msg.setStatus(InviteMesageStatus.REFUSED);
                else if (status == InviteMesageStatus.BEAPPLYED.ordinal())
                    msg.setStatus(InviteMesageStatus.BEAPPLYED);
                else if (status == InviteMesageStatus.GROUPINVITATION.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION);
                else if (status == InviteMesageStatus.GROUPINVITATION_ACCEPTED.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION_ACCEPTED);
                else if (status == InviteMesageStatus.GROUPINVITATION_DECLINED.ordinal())
                    msg.setStatus(InviteMesageStatus.GROUPINVITATION_DECLINED);

                msgs.add(msg);
            }
            cursor.close();
        }
        return msgs;
    }

    /**
     * delete invitation message
     *
     * @param from
     */
    synchronized public void deleteMessage(String from)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen())
        {
            int i = db.delete(InviteMessgeDao.TABLE_NAME, InviteMessgeDao.COLUMN_NAME_FROM + " = ?", new String[]{from});
            Logger.t(DemoDBManager.class.getSimpleName()).d("i:" + i + ",tableName:" + InviteMessgeDao.TABLE_NAME + ",columnName:" + InviteMessgeDao.COLUMN_NAME_FROM + ",form:" + from);
        }
    }

    synchronized int getUnreadNotifyCount()
    {
        int count = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db.isOpen())
        {
            Cursor cursor = db.rawQuery("select " + InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InviteMessgeDao.TABLE_NAME, null);
            if (cursor.moveToFirst())
            {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    synchronized void setUnreadNotifyCount(int count)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen())
        {
            ContentValues values = new ContentValues();
            values.put(InviteMessgeDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InviteMessgeDao.TABLE_NAME, values, null, null);
        }
    }

    synchronized public void closeDB()
    {
        if (dbHelper != null)
        {
            dbHelper.closeDB();
        }
        dbMgr = null;
    }
}
