package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.CPhoneContactUserBean;
import com.echoesnet.eatandmeet.presenters.ImpICPhoneContactView;

import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/26.
 */

public interface ICPhoneContactView
{
    void callServerErrorCallback(String interfaceName, String code, String errBody);
    void requestNetErrorCallback(String interfaceName,Throwable e);
    void getFriendByContactCallback(List<CPhoneContactUserBean> response);
    void getPhoneContactLstCallback(List<ImpICPhoneContactView.ContactEntity> contactEntities);
}
