package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.echoesnet.eatandmeet.activities.CPhoneContactAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.CPhoneContactUserBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICPhoneContactView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by ben on 2016/12/26.
 */

public class ImpICPhoneContactView extends BasePresenter<ICPhoneContactView>
{
    private final String TAG = ImpICPhoneContactView.class.getSimpleName();

    /**
     * 从后台获得已经注册过《看脸吃饭》的用户信息
     */
    public void getFriendByContact(String phoneSplitString)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledNetError(Throwable throwable)
            {
                super.onHandledNetError(throwable);
                if (getView() != null)
                    getView().requestNetErrorCallback(NetInterfaceConstant.EncounterC_newaccostToPerson,throwable);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);

                List<CPhoneContactUserBean> contactLst=new ArrayList<>();
                contactLst = new Gson().fromJson(response, new TypeToken<List<CPhoneContactUserBean>>(){}.getType());
                if (getView() != null)
                    getView().getFriendByContactCallback(contactLst);
            }
        },NetInterfaceConstant.FriendC_myPhoneContact,reqParamMap);

    }

    public void getPhoneContactLst()
    {
        final ICPhoneContactView mIcPhoneView = getView();
        if (mIcPhoneView == null)
            return;
        Activity mAct = (CPhoneContactAct) getView();
        ContentResolver cr = mAct.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        Logger.t(TAG).d("cur:" + (cur != null ? cur.getCount() : cur));
        final List<ContactEntity> contactEntities = new ArrayList<>();
        if (cur == null)
        {
            mAct.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mIcPhoneView != null)
                        mIcPhoneView.getPhoneContactLstCallback(null);
                }
            });
            return;
        }

        Logger.t(TAG).d("获得联系人" + cur.getCount() + "");
        if (cur.getCount() > 0)
        {
            while (cur.moveToNext())
            {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNoT = "";
                Logger.t(TAG).d("获得联系人名称 " + name);
                //如果有多个号码，取第一个
                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0)
                {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext())
                    {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNoT = phoneNo;
                        break;
                    }
                    pCur.close();
                }
                ContactEntity contact = new ContactEntity();
                contact.setId(id);
                contact.setName(name);
                contact.setPhoneNo(phoneNoT.replaceAll("\\s+", ""));
                contactEntities.add(contact);
            }
        }
        cur.close();
        mAct.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (mIcPhoneView != null)
                    mIcPhoneView.getPhoneContactLstCallback(contactEntities);
            }
        });

    }

    public static class ContactEntity
    {
        private String id;
        private String name;
        private String phoneNo;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getPhoneNo()
        {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo)
        {
            this.phoneNo = phoneNo;
        }

        @Override
        public String toString()
        {
            return "ContactEntity{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", phoneNo='" + phoneNo + '\'' +
                    '}';
        }
    }

}
