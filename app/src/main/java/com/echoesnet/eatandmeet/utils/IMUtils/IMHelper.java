package com.echoesnet.eatandmeet.utils.IMUtils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;
import com.tencent.TIMCallBack;
import com.tencent.TIMFriendshipManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.UUID;

/**
 * Created by wangben on 2016/7/4.
 */
public class IMHelper
{
    private static final String TAG = IMHelper.class.getSimpleName();

    private static IMHelper instance = null;
    private IHxRegisterFinishedListener mRegisterFinishedListener;
    private IHxLogoutFinishedListener mHxLogoutFinishedListener;
    private IHxLoginFinishedListener mHxLoginFinishedListener;

    private IMHelper()
    {
    }

    public synchronized static IMHelper getInstance()
    {
        if (instance == null)
        {
            instance = new IMHelper();
        }
        return instance;
    }


    public void setOnIRegisterFinishedListener(IHxRegisterFinishedListener listener)
    {
        mRegisterFinishedListener = listener;
    }

    public void removeIRegisterFinishedListener()
    {
        mRegisterFinishedListener = null;
    }

    public void setOnILogoutFinishListener(IHxLogoutFinishedListener listener)
    {
        mHxLogoutFinishedListener = listener;
    }

    public void removeILogoutFinishListener()
    {
        mHxLogoutFinishedListener = null;
    }

    public void setOnILoginFinishListener(IHxLoginFinishedListener listener)
    {
        mHxLoginFinishedListener = listener;
    }

    public void removeLoginFinishListener()
    {
        mHxLoginFinishedListener = null;
    }


    public void register(final String username, final String pwd)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if(EMClient.getInstance()!=null)
                    EMClient.getInstance().createAccount(username, pwd);

                    if (mRegisterFinishedListener != null)
                        mRegisterFinishedListener.onSuccess(username, pwd);

                } catch (HyphenateException e)
                {
                    if (mRegisterFinishedListener != null)
                        mRegisterFinishedListener.onFailed(e.getErrorCode());
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void reRegisterHx(final Context mContext, final String userName, final String psw)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    //region 注册环信
                    String hxId = userName;
                    String hxPsw = psw;
                    if (TextUtils.isEmpty(hxId) || TextUtils.isEmpty(hxPsw))
                    {
                        hxId = SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().toLowerCase().substring(0, 8);
                        hxPsw = UUID.randomUUID().toString().toLowerCase().substring(0, 6);
                    }
                    EMClient.getInstance().createAccount(hxId, hxPsw);
                    addHXAccountToServer(mContext, hxId, hxPsw);
                } catch (HyphenateException e)
                {
                    Logger.t(TAG).d("错误码：" + e.getErrorCode() + " " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 保存环信账号到后台,并登陆
     *
     * @param userName
     * @param psw
     */
    private void addHXAccountToServer(final Context mContext, final String userName, final String psw)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.imuId, userName);
        reqParamMap.put(ConstCodeTable.imPass, psw);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onError(Throwable e)
            {
                super.onError(e);
                Logger.t(TAG).d("保存环信账号失败--> " + e.getMessage());
            }

            @Override
            public void onNext(String response)
            {

                super.onNext(response);
                Logger.t(TAG).d("保存环信账号返回--> " + response);
                EMClient.getInstance().login(userName, psw, new EMCallBack()
                {
                    @Override
                    public void onSuccess()
                    {
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        if (mHxLoginFinishedListener != null)
                            mHxLoginFinishedListener.onSuccess();
                    }

                    @Override
                    public void onError(int i, String s)
                    {
                        if (mHxLoginFinishedListener != null)
                            mHxLoginFinishedListener.onFailed(i, s);
                    }

                    @Override
                    public void onProgress(int i, String s)
                    {

                    }
                });
                SharePreUtils.setHxId(mContext, userName);
            }
        },NetInterfaceConstant.UserC_addImu,reqParamMap);
    }

    /**
     * if ever logged in
     *
     * @return
     */
    public boolean isLoggedIn()
    {
        return EMClient.getInstance().isLoggedInBefore() && EMClient.getInstance().isConnected();
    }

    /**
     * 环信登录
     */
    public void huanXinLogin(@Nullable final Context mAct)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                String code = apiE.getErrorCode();
                //如果发现用户信息中没有绑定环信账号，则重新创建并绑定
                if ("IMUINFO_NULL".equals(code))
                {
                    reRegisterHx(mAct, null, null);
                }
                Logger.t(TAG).d("错误码为：%s", code);
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获取环信账号信息成功》 " + response.toString());
                try
                {
                    JSONObject body = new JSONObject(response);
                    String userName = body.getString("imuId");
                    String psw = body.getString("imPass");
                    SharePreUtils.setHxId(mAct, userName);
                    hxLogin(userName, psw, mAct);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("获取环信账号信息异常：" + e.getMessage());
                }
            }
        },NetInterfaceConstant.UserC_imuInfo,reqParamMap);
    }

    private int hxLoginNum = 0;
    private void hxLogin(final String userName, final String psw, final Context context)
    {
        try
        {
            EMClient.getInstance().login(userName, psw, new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    if (mHxLoginFinishedListener != null)
                        mHxLoginFinishedListener.onSuccess();
                }

                @Override
                public void onError(int i, String s)
                {
                    EamLogger.t(TAG).writeToDefaultFile("环信登录失败 错误码为》" + i + " 错误信息》" + s);
                    if (i == EMError.USER_ALREADY_LOGIN)//用户已经登录
                    {
                        if (mHxLoginFinishedListener != null)
                            mHxLoginFinishedListener.onSuccess();
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                    }
                    else if (i == EMError.USER_NOT_FOUND)
                    {
                        reRegisterHx(context, userName, psw);
                    }
                    else if (hxLoginNum < 3)
                    {
                        hxLoginNum++;
                        hxLogin(userName, psw, context);
                    }
                    else
                    {
                        if (mHxLoginFinishedListener != null)
                            mHxLoginFinishedListener.onFailed(i, s);
                    }
                }

                @Override
                public void onProgress(int i, String s)
                {

                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
            EamLogger.t("HXIM").writeToDefaultFile("登录错误》" + e.getMessage());
        }
    }

    /**
     * 环信登出
     */
    public void huanXinLogout(final Context mContext)
    {
        try
        {
            EMClient.getInstance().logout(true, new EMCallBack()
            {
                @Override
                public void onSuccess()
                {
                    if (mHxLogoutFinishedListener != null)
                        mHxLogoutFinishedListener.onSuccess();
                }

                @Override
                public void onError(int i, String s)
                {
                    if (mHxLogoutFinishedListener != null)
                        mHxLogoutFinishedListener.onFailed(i, s);
                }

                @Override
                public void onProgress(int i, String s)
                {

                }
            });
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setTxUserInfo(final Context mContext)
    {
        TIMFriendshipManager.getInstance().setNickName(SharePreUtils.getNicName(mContext), new TIMCallBack()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("设置昵称失败" + "错误码 " + i + "msg " + s);
            }

            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("设置昵称成功");
            }
        });
        TIMFriendshipManager.getInstance().setFaceUrl(SharePreUtils.getHeadImg(mContext), new TIMCallBack()
        {
            @Override
            public void onError(int i, String s)
            {
                Logger.t(TAG).d("设置头像失败" + "错误码 " + i + "msg " + s);
            }

            @Override
            public void onSuccess()
            {
                Logger.t(TAG).d("设置头像成功>" + SharePreUtils.getHeadImg(mContext));
            }
        });
    }

    /**
     * 查看是否奔溃过
     */
    public void reGetLivePlayContextStatus(final Context context, final TIMCallBack callBack)
    {
        // TODO: 2017/3/14 0014  
//        AVContext avContext = QavsdkControl.getInstance().getAVContext();
        //解决崩溃后这个为null（本来应该奔溃结束程序了，但是不知道为什么好多手机不结束app）--wb
//        if (avContext == null)
//        {
//            //退出直播sdk
////            QavsdkControl.getInstance().stopContext();
//            //初始化avsdk imsdk
//            MyInitBusinessHelper.initApp(context.getApplicationContext());
//            IMHelper.getInstance().TXImLogin(context, SharePreUtils.getTlsName(context), SharePreUtils.getTlsSign(context), new TIMCallBack()
//            {
//                @Override
//                public void onError(int i, String s)
//                {
//                    Logger.t(TAG).d("错误码》" + i + "信息》" + s);
//                    if (callBack != null)
//                        callBack.onError(i, s);
//                }
//
//                @Override
//                public void onSuccess()
//                {
//                    IMHelper.getInstance().startAVSDK(SharePreUtils.getTlsName(context), SharePreUtils.getTlsSign(context));
//                    if (callBack != null)
//                        callBack.onSuccess();
//                }
//            });
//        }
    }

    public interface IHxRegisterFinishedListener
    {
        void onSuccess(String userName, String psw);

        void onFailed(int errorCode);
    }

    public interface IHxLoginFinishedListener
    {
        void onSuccess();

        void onFailed(int i, String s);
    }

    public interface IHxLogoutFinishedListener
    {
        void onSuccess();

        void onFailed(int i, String s);
    }
}
