package com.echoesnet.eatandmeet.presenters;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.AroundFrg;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/28
 * @description
 */

public class ImpIAroundPre extends BasePresenter<AroundFrg>
{
    private final String TAG = ImpIAroundPre.class.getSimpleName();

    public void getPhoneContactLst()
    {
        final AroundFrg fragment = getView();
        if (fragment == null)
            return;
        Fragment mAct = getView();
        ContentResolver cr = mAct.getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        final List<ImpIAroundPre.ContactEntity> contactEntities = new ArrayList<>();
        if (cur == null)
        {
            mAct.getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (fragment != null)
                        fragment.getPhoneContactLstCallback(null);
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
                ImpIAroundPre.ContactEntity contact = new ImpIAroundPre.ContactEntity();
                contact.setId(id);
                contact.setName(name);
                contact.setPhoneNo(phoneNoT.replaceAll("\\s+", ""));
                contactEntities.add(contact);
            }
        }
        cur.close();
        mAct.getActivity().runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (fragment != null)
                    fragment.getPhoneContactLstCallback(contactEntities);
            }
        });
    }

    /**
     * 从后台获得已经注册过《看脸吃饭》的用户信息
     */
    public void getFriendByContact(String phoneSplitString)
    {
        final AroundFrg fragmentView = getView();
        if (fragmentView == null)
            return;
        Fragment mAct = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct.getActivity());
        reqParamMap.put(ConstCodeTable.contactList, phoneSplitString);
        String paramJson = NetHelper.getRequestJsonStr(NetInterfaceConstant.FriendC_refreshPhoneContact, new Gson().toJson(reqParamMap));
        Logger.t(TAG).d("上传通讯录参数--> " + paramJson);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (fragmentView != null)
                    fragmentView.refreshPhoneContactCallback();
            }
        }, NetInterfaceConstant.FriendC_refreshPhoneContact, null, reqParamMap);
    }

    public void loadSayHelloAndTrendsNum()
    {
        final AroundFrg fragment = getView();
        if (fragment == null)
            return;
        Fragment mAct = getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct.getActivity());
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<ResponseResult>()
        {
            @Override
            public void onNext(ResponseResult response)
            {
                super.onNext(response);
                if (fragment != null)
                    fragment.loadSayHelloAndTrendsNumCallback(response.getBody());
            }
        }, NetInterfaceConstant.FriendC_countHello, null, reqParamMap);
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
