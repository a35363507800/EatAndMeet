package com.echoesnet.eatandmeet.presenters.viewinterface;

import com.echoesnet.eatandmeet.models.bean.UserEditInfoBean;

import org.json.JSONObject;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/15.
 */

public interface IMyInfoEditView
{

    /**
     * 错误回调
     *
     * @param interfaceName
     * @param code
     * @param errBody
     */
    void callServerErrorCallback(String interfaceName, String code, String errBody);

    void requestNetError(Call call, Exception e, String exceptSource);

    void getUserInfoCallback(UserEditInfoBean userInfo );

    void postUserInfoCallback(String response, final UserEditInfoBean bean);

    void postUserGalleryImagesCallback(JSONObject response);
}
