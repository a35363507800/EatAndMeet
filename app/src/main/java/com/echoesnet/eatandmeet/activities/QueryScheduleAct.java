package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.HtmlUtils.EAMCheckCacheFiles;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ProgressBridgeWebView.ProgressBridgeWebView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lc on 2017/7/27 11.
 */

public class QueryScheduleAct extends BaseActivity
{

    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.rbwv_view)
    ProgressBridgeWebView rbwvView;
    private final String TAG = QueryScheduleAct.class.getSimpleName();
    private Activity mActivity;
    private Dialog pDialog;
    private final int TO_SHOW_BOOTY_CALL = 1;
    private int TO_MY_COMMENT = 2;
    private String luid;
    private String streamID;


    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        mActivity = this;
        setContentView(R.layout.act_query_schedule);
        ButterKnife.bind(this);
        initTopBar();
        initView();

    }

    private void initTopBar()
    {
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                rbwvView.callHandler("getType", "", new CallBackFunction()
                {
                    @Override
                    public void onCallBack(String data)
                    {
                        //invite; //0是邀请人 1是被邀请人
                        Logger.t(TAG).d("getType返回参数》》"+data.toString());
                        try
                        {
                            JSONObject jsonObject  = new JSONObject(data);
                            String userType = jsonObject.getString("userType");
                            if (TextUtils.equals(userType,"1"))
                            {
                                new CustomAlertDialog(mActivity).builder().setTitle("提示！").setMsg("为保证您的安全及利益，当您成功赴约以后，红包将存至您的账户余额。").setNegativeButton("确定",null).show();
                            }
                            else
                            {
                                new CustomAlertDialog(mActivity).builder().setTitle("提示！").setMsg("为了保证您的财产不受损失,您支付的红包将暂时存于第三方平台,只有成功约会后才进行支付。若因主播原因导致约会失败,红包将立即退回至您的账户余额").setNegativeButton("确定",null).show();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }).setText("进度查询");

        List<Map<String,TextView>> navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 1});

        for (int i = 0; i < navBtns.size(); i++)
        {
            Map<String, TextView> map = navBtns.get(i);
            TextView tv = map.get(TopBarSwitch.NAV_BTN_ICON);
            switch (i)
            {
                case 0:
                    break;
                case 1:
                    tv.setText("{eam-e60d}");
                    tv.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
                    break;

                default:
                    break;
            }
        }

        topBar.setBottomLineVisibility(View.VISIBLE);
    }

    private void initView()
    {
        luid = getIntent().getStringExtra("luid");
        streamID = getIntent().getStringExtra("streamID");

        pDialog = DialogUtil.getCommonDialog(mActivity, "正在加载...");
        pDialog.setCancelable(false);
        Logger.t(TAG).d("QueryScheduleAct");

        String url= NetHelper.H5_ADDRESS +"/h5/query-process.html";


        rbwvView.getSettings().setLoadWithOverviewMode(true);
        rbwvView.getSettings().setUseWideViewPort(true);
        rbwvView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        rbwvView.loadUrl(EAMCheckCacheFiles.tryH5CacheFile(url));


        //必须注册
        rbwvView.registerHandler("shareDataBetweenJavaAndJs", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("JStoJava" + data+"/"+luid+"/"+streamID);
                //传给js的数据
                Map<String, Object> reqParamMap = NetHelper.getH5CommonMap(mActivity);
                reqParamMap.put("luid",luid);
                reqParamMap.put("streamID",streamID);
                function.onCallBack(new Gson().toJson(reqParamMap));
            }
        });


        rbwvView.registerHandler("showQRCode", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("showQRCode JStoJava" + data);
                CommonUtils.getQRCode(mActivity, CommonUtils.createQRImage(mActivity, data, 200, 200),false);
/*                //传给js的数据
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String code = jsonObject.getString("code");
                    if (code == null)
                    {
                        showMindDialog();
                    }
                    else
                    {
                        CommonUtils.getQRCode(mActivity, CommonUtils.createQRImage(mActivity, code, 200, 200),false);
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }*/
            }
        });


         //判断是不是邀请方，还是接受方



        //查看大图片
        rbwvView.registerHandler("showImage", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据  luId：被邀请人uId
                Logger.t(TAG).d("showImage" + data);
                try
                {
                    JSONObject json = new JSONObject(data);
                    int position = json.getInt("position");
                    String urls = json.getString("urlList");
                    Logger.t(TAG).d("urls" + urls);
                    JSONArray jsonArray = new JSONArray(urls);
                    ArrayList<String> urlList = new ArrayList<String>();
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        urlList.add(jsonArray.get(i).toString());
                        Logger.t(TAG).d(jsonArray.get(i).toString());
                    }
                    Logger.t(TAG).d(urlList.toString());
                    CommonUtils.showImageBrowser(mActivity, urlList, position, null);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });

        // 开始订餐
        rbwvView.registerHandler("toOrderMeal", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                //从js获得数据
                Logger.t(TAG).d("开始订餐--> " + data);
                JSONObject jsonObject = null;
                try
                {
                    jsonObject = new JSONObject(data);
                    String streamId = jsonObject.getString("streamId");
                    String date = jsonObject.getString("date");
                    Intent intent = new Intent(mActivity, CopyOrderMealAct.class);
                    intent.putExtra("bootyCallDate", date);
                    intent.putExtra("openFrom", "dateEnter");
                    SharePreUtils.setToOrderMeal(mActivity, "toOrderMeal");
                    SharePreUtils.setOrderType(mActivity, "1");
                    EamApplication.getInstance().dateStreamId = streamId;
                    mActivity.startActivity(intent);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        // 评价主播
        rbwvView.registerHandler("evaluateReceive", new BridgeHandler()
        {
            @Override
            public void handler(String data, CallBackFunction function)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(data);
                    String streamId = jsonObject.getString("streamId");
                    //从js获得数据
                    Logger.t(TAG).d("评价主播--> " + data);
                    Intent intent = new Intent(mActivity, MyCommentAct.class);
                    intent.putExtra("streamId", streamId);
                    intent.putExtra("luid", luid);
                    startActivityForResult(intent, TO_MY_COMMENT);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showMindDialog()
    {
        new CustomAlertDialog(mActivity).builder().setTitle("提示")
                .setMsg("必须先和主播完成餐厅订餐,才能查看此二维码哦~").setPositiveButton("我知道了", new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        }).show();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    /**
     * 二维码弹出层
     *
     * @param bitmap
     */
    private void getQRCode(Context context, Bitmap bitmap)
    {
        final Dialog dialog = new Dialog(mActivity, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(context).inflate(R.layout.qr_code_pop, null);
        dialog.setContentView(contentView);
        TextView tv_qr_content = (TextView) contentView.findViewById(R.id.tv_qr_content);
        SpannableStringBuilder builder = new SpannableStringBuilder(tv_qr_content.getText().toString());
        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(getResources().getColor(R.color.MC3));
        builder.setSpan(redSpan, 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_qr_content.setText(builder);

        ImageView iv_qr = (ImageView) contentView.findViewById(R.id.iv_qr);
        iv_qr.setImageBitmap(bitmap);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int) (CommonUtils.getScreenSize(mActivity).width * 0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_SHOW_BOOTY_CALL | requestCode == TO_MY_COMMENT)
        {
            rbwvView.reload();
        }
    }


}
