package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.DateBaseInfoBean;
import com.echoesnet.eatandmeet.models.bean.DateBaseInfoCopyBean;
import com.echoesnet.eatandmeet.models.bean.DateCommentBean;
import com.echoesnet.eatandmeet.models.bean.PayBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.models.bean.YueValuateBean;
import com.echoesnet.eatandmeet.presenters.ImpIDateInfoPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDateInfoView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IHsPayPendingListener;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.DateCommentRecycleView;
import com.echoesnet.eatandmeet.views.YueChooseTimePop;
import com.echoesnet.eatandmeet.views.YueChooseTimePop2;
import com.echoesnet.eatandmeet.views.adapters.DateInfoRecycleViewAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jungly.gridpasswordview.GridPasswordView;
import com.orhanobut.logger.Logger;
import com.pingplusplus.android.Pingpp;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.echoesnet.eatandmeet.models.bean.ConstCodeTable.date;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/14 16.
 * @description app个人详情页的约会 Tab页面
 */

public class DateInfoFrg extends MVPBaseFragment<DateInfoFrg, ImpIDateInfoPre> implements IDateInfoView
{
    private final String TAG = DateInfoFrg.class.getSimpleName();
    @BindView(R.id.rv_yue_content)
    DateCommentRecycleView rvYueContent;
    Unbinder unbinder;
    @BindView(R.id.tv_emptyview)
    EmptyView tvEmptyview;
    private Activity mActivity;
    private DateInfoRecycleViewAdapter mAdapter;
    private YueValuateBean bean;
    private String acceptTime;//可约时间
    private String startIdex = "0";
    private String Num = "6";
    private String toCheckUserUid;//查看用户的uId;
    private String id;
    private String price;
    private String desc;
    private String status;
    //用来标记是否正在向最后一个滑动
    private boolean isSlidingToLast = false;

    private String streamID;

    private List<DateCommentBean> mList;
    private List<String> dataList;

    //private YueChooseTimePop pop;
    private YueChooseTimePop2 pop;

    private boolean isLoadingMore = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_date_new_yue, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView()
    {
        mActivity = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvYueContent.setLayoutManager(linearLayoutManager);
        mList = new ArrayList<>();
        dataList = new ArrayList<>();
        Bundle buddle = getArguments();
        if (buddle != null)
        {
            toCheckUserUid = buddle.getString("uId", "");
            id = buddle.getString("id", "");
        }
        tvEmptyview.setTop2DivideShow(true);
        tvEmptyview.setContent("");
        tvEmptyview.setImageId(R.drawable.bg_wushoucang);
        tvEmptyview.setImageGone(true);
        if (TextUtils.equals(SharePreUtils.getUId(mActivity),toCheckUserUid))
        { 
            mAdapter = new DateInfoRecycleViewAdapter(mActivity, mList, dataList,true,tvEmptyview);
            Logger.t(TAG).d( "isSelfInfo >>true");
        }
        else
        {
            mAdapter = new DateInfoRecycleViewAdapter(mActivity, mList, dataList,false,tvEmptyview);
            Logger.t(TAG).d( "isSelfInfo >>false");
        }


        mAdapter.setOnTopClickListener(new DateInfoRecycleViewAdapter.OnTopClickListener()
        {
            @Override
            public void onTopClick(View view)
            {
                if ("1".equals(status))
                {
////                    //去查询进度
////                    Intent intent = new Intent(mActivity, QueryScheduleAct.class);
////                    intent.putExtra("luid", toCheckUserUid);
////                    intent.putExtra("streamID", streamID);
////                    Logger.t(TAG).d("streamID>>>>" + streamID);
////                    startActivity(intent);
                } else
                {
                    if (mPresenter!=null)
                    mPresenter.checkReceive(toCheckUserUid);
                }
            }
        });

        rvYueContent.init(mAdapter, tvEmptyview);
        rvYueContent.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState)
                {
                    case RecyclerView.SCROLL_STATE_IDLE: // 停止滑动判断是否可以播放暂停
                        break;
                }
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();

                    Logger.t(TAG).d("数据数目》" + totalItemCount + "最后可见的位置》" + lastVisibleItem + "滑动方向》" + isSlidingToLast + "uid" + toCheckUserUid);
                    // 判断是否滚动到底部
                    if (lastVisibleItem == (totalItemCount - 1))
                    {
                        //加载更多
                        if (mPresenter != null && isSlidingToLast)
                        {
                            if (!isLoadingMore)
                            {
                                Logger.t(TAG).d("加载更多>>>请求接口");
                                mPresenter.getUserAppointment(TextUtils.isEmpty(toCheckUserUid) ? id : toCheckUserUid, String.valueOf(mList.size()), Num);
                            }
                            isLoadingMore = true;
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                //dy <0 表示 上滑， dy>0 表示下滑
                if (dy>0)
                {
                    isSlidingToLast = true;
                }
                else
                {
                    isSlidingToLast = false;
                }
            }
        });

        if (mPresenter != null)
        {
            Logger.t(TAG).d("oncreate>>>请求接口");
            mPresenter.getUserAppointment(TextUtils.isEmpty(toCheckUserUid) ? id : toCheckUserUid, startIdex, Num);
          //  mPresenter.checkReceive(toCheckUserUid);
        }
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showDatePop(List<DateBaseInfoBean> list,List<DateBaseInfoCopyBean> copyList)
    {
       // pop = new YueChooseTimePop(mActivity);
        pop = new YueChooseTimePop2(mActivity,list,copyList);
        if (pop != null)
        {
            if (!pop.isShowing())
            {
                pop.show();
            }
        }
        pop.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                pop.dismissPop();
            }
        });
        pop.setOnSelectFinishListener(finishedSelectPeriodsListener);
        pop.showAtLocation(rvYueContent, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    protected ImpIDateInfoPre createPresenter()
    {
        return new ImpIDateInfoPre();
    }



    YueChooseTimePop2.IFinishedSelectPeriodsListener finishedSelectPeriodsListener = new YueChooseTimePop2.IFinishedSelectPeriodsListener()
    {
        @Override
        public void selectPeriodsFinish(String periods)
        {
            acceptTime = "";
            if (periods != null)
            {
                acceptTime = periods;
            }
            Logger.t(TAG).d(">>" + acceptTime + "<<");

            if (acceptTime != null)
            {
                if (mPresenter != null)
                {
                    List<Map<String, String>> list = new ArrayList<>();
                    Map<String, String> map = new HashMap<>();
                    map.put("uId", toCheckUserUid);
                    map.put("cash", price);
                    list.add(map);

                    String luid = new Gson().toJson(list);
                    mPresenter.checkWish(luid, acceptTime, "0");
                }
            }


        }
    };

    @Override
    public void userAppointmentCallBack(String body)
    {
        Logger.t(TAG).d("返回参数》》" + body.toString());
        try
        {
            JSONObject jsonObject = new JSONObject(body);
            String evaluate = jsonObject.getString("evaluate");
            status = jsonObject.getString("status");
            desc = jsonObject.getString("desc");
            price = jsonObject.getString("price");
            streamID = jsonObject.getString("streamId");
            Logger.t(TAG).d("返回参数PRICE==" + price);
            List<String> mDataList = new ArrayList<>();

            mDataList.add(status);
            mDataList.add(desc);
            dataList.addAll(mDataList);


            List<DateCommentBean> mgsList = EamApplication.getInstance()
                    .getGsonInstance().fromJson(evaluate, new TypeToken<List<DateCommentBean>>()
                    {
                    }.getType());
            if (mgsList.size() > 0)
            {
                // mList.clear();
                mList.addAll(mgsList);
            }
            removeDuplicate(mList);
            isLoadingMore = false;
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解析出现异常》》"+e.getMessage());
        }

    }

    //过滤重复数据
    public void removeDuplicate(List<DateCommentBean> list)
    {
        //去重复
        for (int i = 0; i < list.size(); i++)
        {
            for (int j = list.size() - 1; j > i; j--)
            {
                if (list.get(j).getTime().equals(list.get(i).getTime())&&list.get(j).getuId().equals(list.get(i).getuId()))
                {
                    list.remove(j);
                }
            }
        }
    }

    @Override
    public void appointPayCallBack(String body)
    {
        ToastUtils.showShort("邀请发送成功,等待接受");
        PayHelper.clearPopupWindows();
    }

    @Override
    public void checkWishCallBack(String body)
    {
        PayHelper.clearPayHelperListeners();
        //用汇昇币支付触发
        PayHelper.setIHsPayPendingListener(new PendingPayFinish(DateInfoFrg.this));
        PayBean payBean = new PayBean();
        payBean.setOrderId("");
        payBean.setMyPayType(EamConstant.EAM_PAY_PENDING);
        payBean.setAmount(price);
        payBean.setSubject("看脸吃饭");               // 商品的标题
        payBean.setBody("约直播吃饭");        // 商品的描述信息
        Logger.t(TAG).d("hostUId==" + toCheckUserUid);
        PayHelper.payOrder(mActivity.getWindow().getDecorView(), payBean, mActivity, new PayMetadataBean(price, toCheckUserUid, "", "5", "", acceptTime));
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.AppointmentC_checkWish:
                try
                {
                    if (TextUtils.equals(code, "HASRECEIVE_ERROR"))
                    {
                        JSONObject jsonObject = new JSONObject(errBody);
                        ToastUtils.showShort(jsonObject.getString("msg"));
                        Logger.t(TAG).d("code:"+code);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case NetInterfaceConstant.AppointmentC_payWish:
                if (ErrorCodeTable.PAYPWD_ERR.equals(code))
                {
                    try
                    {
                        JSONObject jsonResponse = new JSONObject(errBody);
                        String bodyStr = jsonResponse.getString("most");
                        String surplus = jsonResponse.getString("surplus");
                        PayHelper.clearPayPassword(mActivity);
                        ToastUtils.showShort("密码错误,剩余" + surplus + "次");
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }else if (ErrorCodeTable.RECEIVE_DELING.equals(code))
                {
                    new CustomAlertDialog(mActivity)
                            .builder()
                            .setTitle("温馨提示")
                            .setMsg("主播已有约，支付失败")
                            .setPositiveButton("确定", null)
                            .setCancelable(false)
                            .show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void checkReceiveCallBack(String body)
    {
        Logger.t(TAG).d("主播可约日期》》"+body.toString());
        try
        {
            JSONObject obj = new JSONObject(body);
            Iterator<String> iterator = obj.keys();
            List<DateBaseInfoBean> list = new ArrayList<>();
            List<DateBaseInfoCopyBean> listCopy = new ArrayList<>();

            while(iterator.hasNext())
            {
                DateBaseInfoBean bean = new DateBaseInfoBean();
                DateBaseInfoCopyBean bean1= new DateBaseInfoCopyBean();
                String key=iterator.next();
                bean.setDate(key);
                bean.setStatus(obj.getString(key));
                list.add(bean);

                bean1.setDate(key);
                bean1.setStatus(obj.getString(key));
                listCopy.add(bean1);
            }
            Logger.t(TAG).d(list.toString());
            showDatePop(list,listCopy);
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("解析异常》》"+e.getMessage());
        }

    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    //余额支付
    private class PendingPayFinish implements IHsPayPendingListener
    {
        private final WeakReference<DateInfoFrg> mActRef;

        private PendingPayFinish(DateInfoFrg mAct)
        {
            this.mActRef = new WeakReference<DateInfoFrg>(mAct);
        }

        @Override
        public void payPending(String passWord, GridPasswordView gridPasswordView)
        {
            DateInfoFrg cFrg = mActRef.get();
            if (mActivity != null)
            {
                Logger.t(TAG).d("余额支付触发 moneyStr>>>>");
                mPresenter.sendAppointment(toCheckUserUid, "0", price, passWord, acceptTime);

            }
        }
    }

    //第三方支付
    private class PayFinish implements IPayFinishedListener
    {
        private final WeakReference<DateInfoFrg> mFrgRef;

        private PayFinish(DateInfoFrg mFrg)
        {
            this.mFrgRef = new WeakReference<DateInfoFrg>(mFrg);
        }

        @Override
        public void PayFinished(String orderId, String streamId, String payType)
        {
            String payTypeR = "none";
            if (payType.equals("alipay") || payType.equals("alipay_wap"))
            {
                payTypeR = "1";
            } else if (payType.equals("wx"))
                payTypeR = "2";
            else
                payTypeR = "3";
            final DateInfoFrg cAct = mFrgRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("pay finished");
                ToastUtils.showShort("邀请发送成功,等待接受");

//                if (cAct.wishH5BeanList != null)
//                    for (DateWishH5Bean wishH5Bean : cAct.wishH5BeanList)
//                    {
//                        cAct.sendC2CMessage(Constants.AVIMCMD_Booty_Call, "", wishH5Bean.getRoomId()
//                                , wishH5Bean.getLiveSource(), wishH5Bean.getImuId());
//                    }
                PayHelper.clearPopupWindows();
                if (mPresenter != null)
                {
                
                    mPresenter.getUserAppointment(TextUtils.isEmpty(toCheckUserUid) ? id : toCheckUserUid, startIdex, Num);
                }
            }
        }

        @Override
        public void PayFailed(String orderId, String streamId, String payType)
        {
            final DateInfoFrg cFrg = mFrgRef.get();
            if (cFrg != null)
            {
                Logger.t(TAG).d("pay failed");
            }

            PayHelper.clearPopupWindows();
        }
    }

    /**
     * Ping++回调,onActivityResult()发生在onResume()之前
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("pay requestCode==" + requestCode + "pay resultCode==" + resultCode);
        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String result = data.getExtras().getString("pay_result");
                Logger.t(TAG).d("pay result==" + result);
                if (result.equals("success"))
                {
                    PayHelper.setIPayFinishedListener(new PayFinish(this));
                    PayHelper.thirdPartyPayStateCheck2(mActivity, new PayMetadataBean("", toCheckUserUid, "", "8", "", date));
                } else if (result.equals("cancel"))
                {
                    ToastUtils.showShort("支付取消");
                } else
                {
                    ToastUtils.showShort("支付失败, 请重试");
                }
            }
        }
    }


    /**
     * 发送C2C消息
     *
     * @param cmd
     */
//    public void sendC2CMessage(final int cmd, String Param)
//    {
//        if ("1".equals(liveSource))
//        {
//            sendTxC2CMessage(cmd,Param);
//        }else if ("2".equals(liveSource))
//        {
//            sendHxCmdC2CMsg(String.valueOf(cmd),hxId,null);
//        }
//    }
}
