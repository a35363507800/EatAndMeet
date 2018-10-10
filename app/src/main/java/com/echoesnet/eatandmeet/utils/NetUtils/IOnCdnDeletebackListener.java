package com.echoesnet.eatandmeet.utils.NetUtils;

import org.json.JSONObject;


public interface IOnCdnDeletebackListener
{
     void onSuccess(JSONObject response, String fileKeyName);
     void onProcess(long len);
     void onFail(JSONObject response);
}
