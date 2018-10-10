package com.echoesnet.eatandmeet.utils.IMUtils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;


public class EaseUserUtils
{
    private final static String TAG = EaseUserUtils.class.getSimpleName();

    /**
     * 根据消息获取用户
     *
     * @param message 环信消息
     * @return
     */
    public static EaseUser getUserInfo(EMMessage message)
    {
        EaseUser eUser = new EaseUser(message.getFrom());
        try
        {
            eUser.setuId(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_UID));
            eUser.setId(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_ID));
            eUser.setNickName(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_NICKNAME));
            eUser.setAvatar(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_HEADIMAGE));
            eUser.setAge(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_AGE, "18"));
            eUser.setSex(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_GENDER));
            eUser.setLevel(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_LEVEL, "0"));
            eUser.setRemark(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_REMARK, ""));
            eUser.setIsVuser(message.getStringAttribute(EamConstant.EAM_CHAT_ATTR_VUSER, "0"));
        } catch (HyphenateException e)
        {
            e.printStackTrace();
        }
        return eUser;
    }

    public static EaseUser getCurrentUserInfo(@Nullable Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        String username = EMClient.getInstance().getCurrentUser();
        EaseUser currentUser = new EaseUser(username);
        currentUser.setNickName(SharePreUtils.getNicName(context));
        currentUser.setAvatar(SharePreUtils.getHeadImg(context));
        currentUser.setuId(SharePreUtils.getUId(context));
        currentUser.setId(SharePreUtils.getId(context));
        currentUser.setLevel(SharePreUtils.getLevel(context) + "");
        currentUser.setSex(SharePreUtils.getSex(context));
        currentUser.setAge(SharePreUtils.getAge(context));
        currentUser.setIsVuser(SharePreUtils.getIsVUser(context));
        Logger.t(TAG).d("获取用户自己：" + currentUser.getAvatar() + "  " +
                currentUser.getNickName() + "uId " + currentUser.getuId() + "nickname " + currentUser.getNickName());
        return currentUser;
    }

    /**
     * set user avatar
     */
    public static void setUserAvatar(String vUser, String headImg, LevelHeaderView imageView)
    {
        try
        {
            imageView.setLiveState(false);
            imageView.setHeadImageByUrl(headImg);
            imageView.showRightIcon(vUser);
        } catch (Exception e)
        {
            imageView.setLiveState(false);
            imageView.setImageResourceByID(R.drawable.ease_default_avatar);
            imageView.showRightIcon(vUser);
//            imageView.setLevel(headImg);
        }
    }

    /**
     * set user's nickname
     */
    public static void setUserNick(String nickName, TextView textView)
    {
        if (textView != null)
        {
            textView.setText(nickName);
        }
    }
}
