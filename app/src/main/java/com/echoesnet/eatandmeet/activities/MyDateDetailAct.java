package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveShowBootyCallAct;
import com.echoesnet.eatandmeet.activities.live.LiveShowLevelInforAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by an on 2016/11/22 0022.
 */
public class MyDateDetailAct extends BaseActivity
{
    private final String TAG = MyDateDetailAct.class.getSimpleName();
    private int TO_SHOW_BOOTY_CALL = 1;
    private int TO_MY_COMMENT = 2;
    @BindView(R.id.web_ranking)
    ProgressBridgeWebView mWebView;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;

    private Activity mActivity;
    //约会流水号
    private String streamId;
    private int currentPage;
    private String isHtml;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_date_detail);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void afterViews()
    {
        mActivity = this;
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                Logger.t(TAG).d(">>>>>>>>leftClick");
                finish();
            }

            @Override
            public void rightClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText(getResources().getString(R.string.my_date_detail_title));
        topBar.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.transparent));
        topBar.getNavBtns(new int[]{1, 0, 0, 0});

        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        Logger.t(TAG).d("afterViews");
        currentPage = getIntent().getIntExtra("currentPage", -1);

        isHtml = getIntent().getStringExtra("isHtml");

        if (!TextUtils.isEmpty(isHtml) && isHtml.equals("isHtml"))
        {
            Logger.t(TAG).d("H5正常传值");
            try
            {
                JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("streamId"));
                streamId = jsonObject.getString("streamId");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        } else
        {
            Logger.t(TAG).d("推送传值或餐厅详情返回传值");
            streamId = getIntent().getStringExtra("streamId");
        }

        Logger.t(TAG).d("streamId格式--> " + getIntent().getStringExtra("streamId"));

        Logger.t(TAG).d("传值streamId--> " + streamId);
        mWebView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(NetInterfaceConstant.level_Introduce_Page));

        // 开始订餐
        mWebView.registerHandler("toOrderMeal", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("开始订餐--> " + data);
                /*Intent intent = new Intent(mActivity, HomeAct.class);
                intent.putExtra("showPage", 3);
                intent.putExtra("isHide", "gone");  // 是约会订单的话，去隐藏主页下面的bar
                SharePreUtils.setToOrderMeal(mActivity, "toOrderMeal");
                SharePreUtils.setOrderType(mActivity, "1");
                EamApplication.getInstance().dateStreamId=streamId;
                mActivity.startActivity(intent);*/
                Intent intent = new Intent(mActivity, CopyOrderMealAct.class);
                SharePreUtils.setToOrderMeal(mActivity, "toOrderMeal");
                SharePreUtils.setOrderType(mActivity, "1");
                EamApplication.getInstance().dateStreamId = streamId;
                mActivity.startActivity(intent);
            }
        });
        // 评价主播
        mWebView.registerHandler("evaluateReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("评价主播--> " + data);
                Intent intent = new Intent(mActivity, MyCommentAct.class);
                intent.putExtra("streamId", streamId);
                startActivityForResult(intent, TO_MY_COMMENT);
            }
        });
        // 查看主播
        mWebView.registerHandler("anchorReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("查看主播状态 anchorReceive--> " + data);
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String roomId = jsonObject.getString("roomId");
                    String luId = jsonObject.getString("luId");
                    Intent intent = new Intent(mActivity,LiveShowBootyCallAct.class);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("hostUId", luId);
                    startActivityForResult(intent, TO_SHOW_BOOTY_CALL);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        // 返回事件
        mWebView.registerHandler("goBack", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("返回事件--> " + data + " , 跳转控制--> " + SharePreUtils.getToDate(mActivity));
                if (SharePreUtils.getToDate(mActivity).equals("toDate"))
                {
                    Intent intent = new Intent(mActivity, MyDateAct.class);
                    intent.putExtra("currentPage", currentPage);
                    startActivity(intent);
                    SharePreUtils.setToDate(mActivity, "");
                } else
                {
                    mActivity.finish();
                }
            }
        });

        // 向H5传参
        mWebView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mActivity);
                reqParamMap.put("streamId", streamId);
                Logger.t(TAG).d("streamId----> " + streamId);
                function.onCallBack(new Gson().toJson(reqParamMap));
                Logger.t(TAG).d("javaTojs" + new Gson().toJson(reqParamMap));
            }
        });
        //查看等级说明
        mWebView.registerHandler("showLevelIntro", new BridgeHandler()
        {

            @Override
            public void handler(String data, CallBackFunction function)
            {
                Logger.t(TAG).d("打开等级说明" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    String lUid = json.getString("luId");
                    Intent intent = new Intent(mActivity,LiveShowLevelInforAct.class);
                    intent.putExtra("luid",lUid);
                    startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_SHOW_BOOTY_CALL | requestCode == TO_MY_COMMENT)
        {
            mWebView.reload();
        }
    }

}