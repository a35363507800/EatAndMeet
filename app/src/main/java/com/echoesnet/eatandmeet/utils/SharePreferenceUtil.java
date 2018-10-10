package com.echoesnet.eatandmeet.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil
{
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public SharePreferenceUtil(Context context, String file) {
        sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    // 保存搭讪页面是否显示新手引导
    public void setGuideMap(boolean firstuse) {
        editor.putBoolean("GuideMap", firstuse);
        editor.commit();
    }

    public boolean needGuideMap() {
        return sp.getBoolean("GuideMap", true);
    }

    public void setLogined(boolean state) {
        editor.putBoolean("Logined", state);
        editor.commit();
    }

    public boolean isLogined() {
        if (!"".equals(sp.getString("EAMUserTokenID", "")) && sp.getBoolean("Logined", false)) {
            return true;
        } else {
            return false;
        }
    }

    public String getTokenID() {
        return sp.getString("EAMUserTokenID", "");
    }


    public void setTokenID(String token) {
        editor.putString("EAMUserTokenID", token);
        editor.commit();
    }

    public String getUID() {
        return sp.getString("EAMUserID", "");
    }


    public void setUID(String uid) {
        editor.putString("EAMUserID", uid);
        editor.commit();
    }


    //当前用户直播用户名
    public void setTlsName(String tlsName) {
        editor.putString("tlsName", tlsName);
        editor.commit();
    }

    public String getTlsName() {
        return sp.getString("tlsName", "");
    }


    //当前用户直播签名
    public void setTlsSign(String tlsSign) {
        editor.putString("tlsSign", tlsSign);
        editor.commit();
    }

    public String getTlsSign() {
        return sp.getString("tlsSign", "");
    }



    public void setUserMobile(String num) {
        editor.putString("EAMUserMobile", num);
        editor.commit();
    }

    public String getUserMobile() {
        return sp.getString("EAMUserMobile", "");
    }




//    获取短信验证码 执行的 时间戳
    public void setSNSCodeTimeStamp(long timeStamp) {
        editor.putLong("SNSCodeTimeStamp", timeStamp);
        editor.commit();
    }



    public long getSNSCodeTimeStamp() {
        return sp.getLong("SNSCodeTimeStamp", 0);
    }




    public void setHXName(String tlsName) {
        editor.putString("hxName", tlsName);
        editor.commit();
    }

    public String getHXName() {
        return sp.getString("hxName", "");
    }




    public void setHXPass(String hxPass) {
        editor.putString("hxPass", hxPass);
        editor.commit();
    }
    public String getHXPass() {
        return sp.getString("hxPass", "");
    }





    public String getNickName() {
        return sp.getString("EAMUserNickName", "");
    }


    public void setNickName(String nickName) {
        editor.putString("EAMUserNickName", nickName);
        editor.commit();
    }

    public String getAvatar() {
        return sp.getString("EAMUserAvatar", "");
    }

    public void setAvatar(String url) {
        editor.putString("EAMUserAvatar", url);
        editor.commit();
    }






    //刷新余额使用
    public String getFace() {
        return sp.getString("EAMFace", "");
    }

    public void setFace(String face) {
        editor.putString("EAMFace", face);
        editor.commit();
    }

    public String getBalance() {
        return sp.getString("EAMBalance", "");
    }

    public void setBalance(String balance) {
        editor.putString("EAMBalance", balance);
        editor.commit();
    }

    public String getMeal() {
        return sp.getString("EAMMeal", "");
    }

    public void setMeal(String meal) {
        editor.putString("EAMMeal", meal);
        editor.commit();
    }


    //room Act -> chat message list adapter -> bLiveAnimator
    public void setBLiveAnimator(boolean state) {
        editor.putBoolean("BLiveAnimator", state);
        editor.commit();
    }

    public boolean isBLiveAnimator() {
        return sp.getBoolean("BLiveAnimator", false);
    }

















    //reward ??
    public String getInviteReward() {
        return sp.getString("EAMInviteReward", "");
    }

    public void setInviteReward(String money) {
        editor.putString("EAMInviteReward", money);
        editor.commit();
    }

    public String getRegReward() {
        return sp.getString("EAMRegReward", "");
    }

    public void setRegReward(String money) {
        editor.putString("EAMRegReward", money);
        editor.commit();
    }







    //Gift Res Version
    public void setGiftResVersion(int version) {
        editor.putInt("GiftResVersion", version);
        editor.commit();
    }

    public int getGiftResVersion() {
        return sp.getInt("GiftResVersion", -1);
    }

    public String getGiftResFileComplete() {
        return sp.getString("GiftResFileComplete", "");
    }

    public void setGiftResFileComplete(String md5) {
        editor.putString("GiftResFileComplete", md5);
        editor.commit();
    }
















    public void setFirstUse(boolean firstuse) {
        editor.putBoolean("firstuse", firstuse);
        editor.commit();
    }

    public boolean isFirstUse() {


        return sp.getBoolean("firstuse", true);
    }




    public void setHomePageJson(String str_json) {
        editor.putString("HomePageJson", str_json);
        editor.commit();
    }

    public String getHomePageJson() {
        return sp.getString("HomePageJson", "");
    }




    public void setJsonStringBy(String id, String str_json) {
        for (int i = 0; i < 20; i++) {
            String savedPostID = sp.getString("JsonStringID" + i, "");
            if (id.equals(savedPostID)) {
                editor.remove("JsonStringID" + i);
                editor.remove("JsonStringContent" + i);
            }
        }
        int orderid = sp.getInt("JsonStringOrderID", 0);
        if (orderid > 20) {            //缓存20篇帖子
            editor.putInt("JsonStringOrderID", 0);
            editor.putString("JsonStringID" + orderid, id);
            editor.putString("JsonStringContent" + orderid, str_json);
        } else {
            editor.putInt("JsonStringOrderID", orderid + 1);
            editor.putString("JsonStringID" + orderid, id);
            editor.putString("JsonStringContent" + orderid, str_json);
        }
        editor.commit();
    }

    public String getJsonStringBy(String id) {
        for (int i = 0; i < 20; i++) {
            String savedPostID = sp.getString("JsonStringID" + i, "");
            if (id.equals(savedPostID)) {
                return sp.getString("JsonStringContent" + i, "");
            }
        }
        return "";
    }


    //clean edit photo
    public void setCleanEditPhoto(boolean firstuse) {
        editor.putBoolean("CleanEditPhoto", firstuse);
        editor.commit();
    }

    public boolean isCleanEditPhoto() {
        return sp.getBoolean("CleanEditPhoto", true);
    }


    public void setMethods(String json) {
        editor.putString("webserviceMethod", json);
        editor.commit();
    }

    public String getMethods() {
        return sp.getString("webserviceMethod", "");
    }


    public void setMethodBaseUrl(String json) {
        editor.putString("webserviceMethodBaseUrl", json);
        editor.commit();
    }

    public String getMethodBaseUrl() {
        return sp.getString("webserviceMethodBaseUrl", "");
    }

    public void setResRootUrl(String json) {
        editor.putString("webserviceResRootUrl", json);
        editor.commit();
    }

    public String getResRootUrl() {
        return sp.getString("webserviceResRootUrl", "");
    }


}
