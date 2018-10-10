package com.echoesnet.eatandmeet.presenters;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.View;

import com.echoesnet.eatandmeet.activities.CNewFriendsAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.fragments.LiveNewFriendsFragment;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.CNewFriendBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICNewFriendsView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/12/20.
 */

public class ImpCNewFriendsView extends BasePresenter<ICNewFriendsView>
{
    private final String TAG = ImpCNewFriendsView.class.getSimpleName();

    public void getAllNewFriends(String getItemStartIndex, String getItemNum, final String operateType)
    {
        final ICNewFriendsView icNewFriendsView = getView();
        if (icNewFriendsView == null)
            return;
        Context mAct;
        if (getView() instanceof CNewFriendsAct)
        {
            mAct = (CNewFriendsAct) getView();
        }
        else
        {
            mAct = ((LiveNewFriendsFragment) getView()).getActivity();
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mAct);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (icNewFriendsView != null)
                    icNewFriendsView.getAllNewFriendsCallback(response, operateType);
            }
        },NetInterfaceConstant.NeighborC_preFriendList,reqParamMap);

    }

    public void deletePreFriend(String lUid, final CNewFriendBean friendBean)
    {
        if (getView() == null)
            return;
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.flg, friendBean.getWelgiftBean() == null ? "0" : "1");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("获得的结果：" + response);
                if (getView() != null)
                    getView().deletePreFriendCallback(response, friendBean);
            }
        },NetInterfaceConstant.NeighborC_delPreFriend,reqParamMap);
    }

    public void saveContactStatusToServer(String amount, String streamId, final String payType, final View view, final CNewFriendBean newFriendBean)
    {
        if (getView() == null)
            return;

        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.lUId, newFriendBean.getuId());
        reqParamMap.put(ConstCodeTable.amount, amount);
        reqParamMap.put(ConstCodeTable.streamId, streamId);
        reqParamMap.put(ConstCodeTable.payType, payType);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (getView()!=null)
                    getView().callServerErrorCallback(NetInterfaceConstant.NeighborC_friend,apiE.getErrorCode(),apiE.getErrBody());
            }

            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (getView() != null)
                    getView().saveContactStatusToServerCallback(response, view, newFriendBean);
            }
        },NetInterfaceConstant.NeighborC_friend,reqParamMap);
    }
    /**
     * 删除好友申请
     * @param lUid
     * @param friendBean 要删除的对象
     */
    /*private void deletePreFriend(String lUid,final CNewFriendBean friendBean)
    {
        mContext.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (pDialog!=null&&!pDialog.isShowing())
                    pDialog.show();
            }
        });
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, lUid);
        reqParamMap.put(ConstCodeTable.flg, friendBean.getWelgiftBean()==null?"0":"1");
        Logger.t(TAG).d("请求的参数》"+NetHelper.getRequestJsonStr("NeighborC/delPreFriend", new Gson().toJson(reqParamMap)));

        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("NeighborC/delPreFriend", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext, null,TAG,e);
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("获得的结果：" + response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status = jsonResponse.getInt("status");
                            if (status == 0)
                            {
                                dataSource.remove(friendBean);
                                newFriendAdapter.notifyDataSetChanged();
                                ToastUtils.showShort(mContext, "删除成功");
                            }
                            else
                            {
                                String code = jsonResponse.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code,mContext))
                                    ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s", code);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        } finally
                        {
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/

    /*public void getAllNewFriends(String getItemStartIndex, String getItemNum, final String operateType)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.num, getItemNum);
        reqParamMap.put(ConstCodeTable.startIdx, getItemStartIndex);

        Logger.t(TAG).d(NetHelper.getRequestJsonStr("NeighborC/preFriendList", new Gson().toJson(reqParamMap)));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("NeighborC/preFriendList", new Gson().toJson(reqParamMap)))
                .build()
                .execute(new ListNewFriendCallback(mContext)
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext, null, TAG, e);
                    }

                    @Override
                    public void onResponse(List<CNewFriendBean> response)
                    {
                        //下拉刷新
                        if (operateType.equals("refresh"))
                        {
                            dataSource.clear();
                        }
                        //后台有时候会返回null，不可理喻
                        if (response!=null)
                            dataSource.addAll(response);
                        newFriendAdapter.notifyDataSetChanged();
                        if (mPullToRefreshListview != null)
                            mPullToRefreshListview.onRefreshComplete();

                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
    }*/
}
