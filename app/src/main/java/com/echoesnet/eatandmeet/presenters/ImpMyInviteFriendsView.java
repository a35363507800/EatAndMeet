package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.http4retrofit2.entity4http.ResponseResult;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInviteFriendsView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

/**
 * Created by Administrator on 2016/12/16.
 */

public class ImpMyInviteFriendsView
{
    private final String TAG = ImpMyInviteFriendsView.class.getSimpleName();
    private Activity mAct;
    private IMyInviteFriendsView iMyInviteFriendsView;

    public ImpMyInviteFriendsView(Activity mAct, IMyInviteFriendsView iMyInviteFriendsView)
    {
        this.mAct = mAct;
        this.iMyInviteFriendsView = iMyInviteFriendsView;
    }

    public void getInviteCode()
    {
        //请求服务器获取我的邀请码
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                iMyInviteFriendsView.getInviteCodeCallback(response);
            }
        }, NetInterfaceConstant.UserC_myInCode, reqParamMap);
    }

    public void getInviteFriends()
    {
        //请求服务器获取接受我邀请的用户
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iMyInviteFriendsView != null)
                    iMyInviteFriendsView.getInviteFriendsCallback(response);
            }
        }, NetInterfaceConstant.UserC_myInvite, reqParamMap);
    }

    /*private void getInviteCode()
    {
        if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
            pDialog.show();
        //请求服务器获取我的邀请码
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("UserC/myInCode", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext, null, TAG, e);
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).json(response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("status");
                            if (status == 0)
                            {
                                JSONObject body = new JSONObject(jsonResponse.getString("body"));
                                inviteNum = body.getString("inCode");
                                tvInviteNum.setText(inviteNum);
                            }
                            else if (status == 1)
                            {
                                String code = jsonResponse.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code, mContext))
                                    ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s", code);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        } finally
                        {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }

    private void getInviteFriends()
    {
        if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
            pDialog.show();
        //请求服务器获取接受我邀请的用户
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        Logger.t(TAG).d("请求参数》" + NetHelper.getRequestJsonStr("UserC/myInvite", new Gson().toJson(reqParamMap)));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("UserC/myInvite", new Gson().toJson(reqParamMap)))
                .build()
                .execute(
                        new InviteFriendCallback(mContext)
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                NetHelper.handleNetError(mContext, null, TAG, e);
                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }

                            @Override
                            public void onResponse(ResMyInviteBean response)
                            {
                                if (response.getTotalReward() == null && response.getUserBeen() == null)
                                {
//                                    tvInviteMoney.setText("0");
                                }
                                else
                                {
//                                    tvInviteMoney.setText(response.getTotalReward());
                                    myInviteFriendLst.clear();
                                    myInviteFriendLst.addAll(response.getUserBeen());
                                    if (myInviteFriendLst.size() != 0)
                                    {
                                        rl_all.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        rl_all.setVisibility(View.GONE);
                                    }
                                    inviteFriendsAdapter.notifyDataSetChanged();
//                                inviteFriendsAdapter.setList(myInviteFriendLst);
//                                inviteFriendsAdapter.ReNotify();
                                }
                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        }
                );
    }*/
}
