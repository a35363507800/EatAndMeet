package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import com.echoesnet.eatandmeet.activities.ApproveProgressAct;
import com.echoesnet.eatandmeet.activities.IdentityAuthAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveReadyView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by Administrator on 2016/10/27.
 */

public class ImpLiveReadyView extends BasePresenter<ILiveReadyView> {
    private static final String TAG = ImpLiveReadyView.class.getSimpleName();
    private Activity mContext;
    private ILiveReadyView myLiveReadyView;
    private Dialog pDialog;

    public ImpLiveReadyView(Activity context) {
        mContext = context;
    }

    public ImpLiveReadyView(Activity context, ILiveReadyView liveReadyView) {
        mContext = context;
        this.myLiveReadyView = liveReadyView;
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
    }

    /**
     * 发起直播
     */
    public void startLive(String roomName, String coverPath, String receiveTime) {
        if (pDialog != null && !pDialog.isShowing()) {
            pDialog.show();
        }
        Map<String, String> params = NetHelper.getCommonPartOfParam(mContext);
        params.put(ConstCodeTable.roomName, roomName);
        params.put(ConstCodeTable.receiveTime, receiveTime);
        params.put(ConstCodeTable.roomUrl, coverPath);
        //TODO: 2017/8/14 测试用， 不用时需注释  ---   yqh
//        params.put(ConstCodeTable.liveSource, "2");
//       params.put(ConstCodeTable.liveSource, "1");
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>() {
            @Override
            public void onHandledError(ApiException apiE) {

                if (apiE.getErrorCode().equals(ErrorCodeTable.REALNAME_DO_NOT_PASS)||apiE.getErrorCode().equals(ErrorCodeTable.NO_REALNAME)) {

                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("请进行实名认证")
                            .setPositiveButton("前往认证", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mContext.startActivity(  new Intent(mContext, IdentityAuthAct.class));
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();

                } else if (apiE.getErrorCode().equals(ErrorCodeTable.REALNAME_UNDER_REVIEW)) {

                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("实名认证审核中，请到实名认证界面查看审核进度")
                            .setPositiveButton("前往查看", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mContext.startActivity(new Intent(mContext, ApproveProgressAct.class));
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                }
                            }).show();

                } else
                {
                    super.onHandledError(apiE);
                }



                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable) {
                super.onHandledNetError(throwable);
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onNext(String response) {
                super.onNext(response);
                if (myLiveReadyView != null)
                    myLiveReadyView.startLiveSuccess(response);
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }


            }
        }, NetInterfaceConstant.LiveC_startLive, params);
    }

    /**
     * 获取常驻位置
     */
    public void getPermanentOrNot() {
        if (pDialog != null && !pDialog.isShowing()) {
            pDialog.show();
        }
        Map<String, String> params = NetHelper.getCommonPartOfParam(mContext);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>() {
            @Override
            public void onNext(String response) {
                super.onNext(response);
                if (myLiveReadyView != null)
                    myLiveReadyView.getPermanentOrNotCallback(response);

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onHandledNetError(Throwable throwable) {
                super.onHandledNetError(throwable);
                if (myLiveReadyView != null)
                    myLiveReadyView.requestNetError(throwable, NetInterfaceConstant.LiveC_setPermanentOrNot);

                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }

            @Override
            public void onHandledError(ApiException apiE) {

                if (mContext == null && mContext.isFinishing())
                    return;

               if (!apiE.getErrorCode().equals(ErrorCodeTable.PERMANENT_NULL))
                    super.onHandledError(apiE);
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            }
        }, NetInterfaceConstant.LiveC_setPermanentOrNot, params);
    }

    /**
     * 如果此房间存在则删除
     *
     * @param avRoomId
     */
    public void checkLiveIsAlreadyCreate(final String avRoomId, final String vedioName) {
        Map<String, String> reqParamMap = new HashMap<>();
        reqParamMap.put("GroupId", avRoomId);
        String paramJson = new Gson().toJson(reqParamMap);
        Logger.t(TAG).d("提交的请求：" + NetHelper.getRequestStrToTx(NetInterfaceConstant.TX_DestroyGroup, null)
                + paramJson);
        OkHttpUtils
                .postString()
                .url(NetHelper.getRequestStrToTx(NetInterfaceConstant.TX_DestroyGroup, null))
                .content(paramJson)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e) {
                        if (myLiveReadyView != null)
                            myLiveReadyView.requestNetError(e, NetInterfaceConstant.TX_DestroyGroup);
                    }

                    @Override
                    public void onResponse(String response) {
                        Map<String, String> map = new HashMap<>();
                        map.put("response", response);
                        map.put("roomId", avRoomId);
                        map.put("vedioName", vedioName);
                        if (myLiveReadyView != null)
                            myLiveReadyView.checkLiveIsAlreadyCreateCallback(map);
                    }
                });
    }

    /**
     * 查看是否奔溃过
     */
    public void checkLivePlayContextStatus() {
//        if (pDialog != null && !pDialog.isShowing())
//        {
//            pDialog.show();
//        }
        // TODO: 2017/3/14 0014
//        AVContext avContext = QavsdkControl.getInstance().getAVContext();
//        //解决崩溃后这个为null（本来应该奔溃结束程序了，但是不知道为什么好多手机不结束app）--wb
//        if (avContext == null)
//        {
//            //退出直播sdk
//            QavsdkControl.getInstance().stopContext();
//            //初始化avsdk imsdk
//            MyInitBusinessHelper.initApp(mContext.getApplicationContext());
//            IMHelper.getInstance().TXImLogin(mContext, SharePreUtils.getTlsName(mContext), SharePreUtils.getTlsSign(mContext), new TIMCallBack()
//            {
//                @Override
//                public void onError(int i, String s)
//                {
//                    Logger.t(TAG).d("错误码》" + i + "信息》" + s);
//                    if (pDialog != null && pDialog.isShowing())
//                    {
//                        pDialog.dismiss();
//                    }
//                }
//
//                @Override
//                public void onSuccess()
//                {
//                    IMHelper.getInstance().startAVSDK(SharePreUtils.getTlsName(mContext), SharePreUtils.getTlsSign(mContext));
//                    if (pDialog != null && pDialog.isShowing())
//                    {
//                        pDialog.dismiss();
//                    }
//                }
//            });
//        }
//        else
//        {
//            if (pDialog != null && pDialog.isShowing())
//            {
//                pDialog.dismiss();
//            }
//        }
    }

    public void dialogAtNull() {
        pDialog = null;
    }
}
