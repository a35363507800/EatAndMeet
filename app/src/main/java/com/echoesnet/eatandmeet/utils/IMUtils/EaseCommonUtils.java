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
package com.echoesnet.eatandmeet.utils.IMUtils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.hyphenate.util.HanziToPinyin;
import com.hyphenate.util.HanziToPinyin.Token;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EaseCommonUtils
{
    private static final String TAG = EaseCommonUtils.class.getSimpleName();
    public static String speakSwitch = "able";

    /**
     * check if network avalable
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context)
    {
        if (context != null)
        {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null)
            {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }

        return false;
    }

    /**
     * check if sdcard exist
     *
     * @return
     */
    public static boolean isSdcardExist()
    {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    public static EMMessage createExpressionMessage(String toChatUsername, String expressioName, String identityCode, String expressionUrl)
    {
        EMMessage message = EMMessage.createTxtSendMessage("[" + expressioName + "]", toChatUsername);
        if (identityCode != null)
        {
            message.setAttribute(EamConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode);
            message.setAttribute(EamConstant.MESSAGE_ATTR_EXPRESSION_URL, expressionUrl);
        }
        message.setAttribute(EamConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true);
        return message;
    }

    public static EMMessage createLiveShareMessage(String toChatUsername, String roomId, String RoomName, String roomUrl)
    {
        EMMessage message = EMMessage.createTxtSendMessage(RoomName, toChatUsername);
        message.setAttribute("isLiveShare", true);
        message.setAttribute("roomId", roomId);
        message.setAttribute("RoomName", RoomName);
        message.setAttribute("roomUrl", roomUrl);
        return message;
    }

    /**
     * 聊天消息设置已读
     *
     * @param conversation 会话
     * @param message      消息
     * @param isAckMsgRead 是否通知对方已读
     */
    public static void makeMessageAsRead(String tag, EMConversation conversation, EMMessage message, boolean isAckMsgRead)
    {
        try
        {
            Logger.t(TAG).d("conversation id:" + conversation.conversationId() + "unreadcount:" + conversation.getUnreadMsgCount() + " message:" + message.getBody());
            conversation.markMessageAsRead(message.getMsgId());
            conversation.markAllMessagesAsRead();
            if (isAckMsgRead)
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            if (CommonUtils.isInLiveRoom)
                EamLogger.t(TAG).writeToDefaultFile("makeMessageAsRead tag:" + tag + " | message:" + message + " | isAckMsgRead:" + isAckMsgRead);
            Logger.t(TAG).d("unreadcount:" + conversation.getUnreadMsgCount());
        } catch (HyphenateException e)
        {
            Logger.t(TAG).d("设置消息已读失败:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get digest according message type and content
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, @Nullable Context context)
    {
        if (context == null)
            context = EamApplication.getInstance();
        String digest = "";
        switch (message.getType())
        {
            case LOCATION:

                digest = "[位置]";

//                if (message.direct() == EMMessage.Direct.RECEIVE)
//                {
//                    digest = "[位置]";
//                    //不显示发送位置的人
//                    //digest = getString(context, R.string.location_recv);
//                    //digest = String.format(digest, message.getFrom());
//                    return digest;
//                }
//                else
//                {
//                    digest = getString(context, R.string.location_prefix);
//                }
                break;
            case IMAGE:
                digest = getString(context, R.string.picture);
                break;
            case VOICE:
                digest = getString(context, R.string.voice_prefix);
                break;
            case VIDEO:
                digest = getString(context, R.string.video);
                break;
            case TXT:
                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
                if (message.getBooleanAttribute(EamConstant.MESSAGE_ATTR_IS_VOICE_CALL, false))
                {
                    digest = getString(context, R.string.voice_call) + txtBody.getMessage();
                }
                else if (message.getBooleanAttribute(EamConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false))
                {
                    digest = getString(context, R.string.video_call) + txtBody.getMessage();
                }
                else if (message.getBooleanAttribute(EamConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false))
                {
                    if (!TextUtils.isEmpty(txtBody.getMessage()))
                    {
                        digest = txtBody.getMessage();
                    }
                    else
                    {
                        digest = getString(context, R.string.dynamic_expression);
                    }
                }
                else
                {
                    digest = txtBody.getMessage();
                }
                break;
            case FILE:
                digest = getString(context, R.string.file);
                break;
            default:
                EMLog.e(TAG, "error, unknow type");
                return "";
        }

        return digest;
    }

    static String getString(Context context, int resId)
    {
        return context.getResources().getString(resId);
    }

    /**
     * get top activity
     *
     * @param context
     * @return
     */
    public static String getTopActivity(Context context)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

        if (runningTaskInfos != null)
            return runningTaskInfos.get(0).topActivity.getClassName();
        else
            return "";
    }

    /**
     * set initial letter of according user's nickname( username if no nickname)
     *
     * @param user
     */
    public static void setUserInitialLetter(EaseUser user)
    {
        final String DefaultLetter = "#";
        String letter = DefaultLetter;

        final class GetInitialLetter
        {
            String getLetter(String name)
            {
                if (TextUtils.isEmpty(name))
                {
                    return DefaultLetter;
                }
                char char0 = name.toLowerCase().charAt(0);
                if (Character.isDigit(char0))
                {
                    return DefaultLetter;
                }
                ArrayList<Token> l = HanziToPinyin.getInstance().get(name.substring(0, 1));
                if (l != null && l.size() > 0 && l.get(0).target.length() > 0)
                {
                    Token token = l.get(0);
                    String letter = token.target.substring(0, 1).toUpperCase();
                    char c = letter.charAt(0);
                    if (c < 'A' || c > 'Z')
                    {
                        return DefaultLetter;
                    }
                    return letter;
                }
                return DefaultLetter;
            }
        }

        if (!TextUtils.isEmpty(user.getNickName()))
        {
            letter = new GetInitialLetter().getLetter(user.getNickName());
            user.setInitialLetter(letter);
            return;
        }
        if (letter == DefaultLetter && !TextUtils.isEmpty(user.getUsername()))
        {
            letter = new GetInitialLetter().getLetter(user.getUsername());
        }
        user.setInitialLetter(letter);
    }


    /**
     * 向文件中写log信息
     *
     * @param context
     * @param content  log信息
     * @param fileName 写入文件名
     */
    public static void writeLog2File(Context context, String content, String fileName)
    {
        String rootPath = "";
        if (TextUtils.isEmpty(fileName))
            fileName = "localLog.txt";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()))
            rootPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "EatAndMeet/";
        else
            rootPath = context.getFilesDir().getPath() + File.separator + "EatAndMeet/";
        writeFile(rootPath + fileName,
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ":" + content + "\n", true);
    }

    private static void writeFile(String filePath, String content, boolean append)
    {
        if (TextUtils.isEmpty(content))
            content = "";
        FileWriter fileWriter = null;
        try
        {
            File file = new File(filePath);
            if (!file.exists())
            {
                file.mkdir();
            }
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e)
        {
            throw new RuntimeException("IOException occurred. ", e);
        }
    }

    /**
     * \~chinese
     * 判断是否是免打扰的消息,如果是app中应该不要给用户提示新消息
     *
     * @param message return
     *                <p>
     *                \~english
     *                check if the message is kind of slient message, if that's it, app should not play tone or vibrate
     * @param message
     * @return
     */
    public static boolean isSilentMessage(EMMessage message)
    {
        return message.getBooleanAttribute("em_ignore_notification", false);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics());
    }
}
