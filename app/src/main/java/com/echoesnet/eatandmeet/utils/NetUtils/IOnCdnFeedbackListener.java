package com.echoesnet.eatandmeet.utils.NetUtils;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by wangben on 2016/6/7.
 */
public interface IOnCdnFeedbackListener
{
     void onSuccess(JSONObject response,File file,String fileKeyName,int uploadOrder);
     void onProcess(long len);
     void onFail(JSONObject response,File file);
}
