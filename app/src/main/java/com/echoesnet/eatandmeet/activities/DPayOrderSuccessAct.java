package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.echoesnet.eatandmeet.presenters.ImpDPayOrderSuccessView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDPayOrderSuccessView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.PushMeetPersonsAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class DPayOrderSuccessAct extends MVPBaseActivity<IDPayOrderSuccessView, ImpDPayOrderSuccessView> implements IDPayOrderSuccessView
{
    private final static String TAG = DPayOrderSuccessAct.class.getSimpleName();

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.gv_meet_person)
    PullToRefreshGridView mPullRefreshGridView;
    @BindView(R.id.btn_back_res)
    Button btnReturnHome;
    @BindView(R.id.btn_check_order)
    Button btnCheckOrders;

    private List<MeetPersonBean> meetPersonLst;
    private Activity mActivity;
    private PushMeetPersonsAdapter mPushMeetPersonAdapter;
    private GridView mGridView;
    private String latitude, longitude;
    private int geotable_id;
    private String orderId;
    private Dialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_pay_order_success);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected ImpDPayOrderSuccessView createPresenter()
    {
        return new ImpDPayOrderSuccessView();
    }


    void initAfterViews()
    {
        mActivity = this;
        pDialog = DialogUtil.getCommonDialog(this, "正在加载...");
        pDialog.setCancelable(false);
        //latitude = SharePreUtils.getLatitude(mActivity);
        //longitude = SharePreUtils.getLongitude(mActivity);
        latitude = EamApplication.getInstance().geoPosition[0];
        longitude = EamApplication.getInstance().geoPosition[1];
        geotable_id = CommonUtils.BAIDU_GEOTABLE_ID;
        orderId = getIntent().getStringExtra("orderId");
        Logger.t(TAG).d("获取主页的纬度===> " + latitude + " , 经度--> " + longitude + " , geotable_id--> " + geotable_id);
        topBar.setTitle("支付成功");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.getLeftButton().setVisibility(View.GONE);
        meetPersonLst = new ArrayList<>();
        getaroundResIds(String.valueOf(CommonUtils.BAIDU_GEO_RADIUS), CommonUtils.BAIDU_GEOTABLE_ID, longitude, latitude);
        mGridView = mPullRefreshGridView.getRefreshableView();
        mPullRefreshGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(mActivity,CNewUserInfoAct.class);
                intent.putExtra("checkWay","UId");
                intent.putExtra("toUId", meetPersonLst.get(position).getLuId());
                startActivity(intent);
            }
        });
        mPullRefreshGridView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        mPullRefreshGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<GridView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView)
            {
                finishRefreshLoserCom();
            }
        });
//        mGridView.setAdapter(mPushMeetPersonAdapter);
    }

//    @OnItemClick(R.id.gv_meet_person)
//    void itemClick(int position)
//    {
//        Intent intent = new Intent(mActivity,CUserInfoAct.class);
//        intent.putExtra("checkWay","UId");
//        intent.putExtra("toUId", meetPersonLst.get(position).getLuId());
//        startActivity(intent);
//    }

    void finishRefreshLoserCom()
    {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mPushMeetPersonAdapter != null)
                    mPushMeetPersonAdapter.notifyDataSetChanged();
                mPullRefreshGridView.onRefreshComplete();
            }
        }, 1000);

    }

    @OnClick({R.id.btn_back_res, R.id.btn_check_order})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_back_res:
                // 跳转到餐厅详情
                Intent intent = new Intent(mActivity,DOrderMealDetailAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("restId", SharePreUtils.getRestId(mActivity));
                intent.putExtra("index", 0);
//                intent.putExtra("resName", SharePreUtils.getResName(mActivity));
//                //String[] location = {SharePreUtils.getLongitude(mActivity), SharePreUtils.getLatitude(mActivity)};
//                intent.putExtra("location", EamApplication.getInstance().geoPosition);
//                intent.putExtra("source", SharePreUtils.getSource(mActivity));
                startActivity(intent);
                this.finish();
                break;
            case R.id.btn_check_order:
                if (SharePreUtils.getSource(mActivity).equals("myColloect"))
                {
                    Intent intent1 = new Intent(mActivity,DOrderRecordDetail.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("orderId", getIntent().getStringExtra("orderId"));
                    mActivity.startActivity(intent1);
                    this.finish();
                }
                else if (SharePreUtils.getSource(mActivity).equals("unPay"))
                {
                    Intent intent1 = new Intent(mActivity,DOrderRecordDetail.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("orderId", getIntent().getStringExtra("orderId"));
                    mActivity.startActivity(intent1);
                    this.finish();
                }
                else if (SharePreUtils.getSource(mActivity).equals("myInfo"))
                {
                    Intent intent1 = new Intent(mActivity,DOrderRecordDetail.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent1.putExtra("orderId", getIntent().getStringExtra("orderId"));
                    mActivity.startActivity(intent1);
                    this.finish();
                }
                else
                {
                    Intent intent1 = new Intent(mActivity,DOrderRecordDetail.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent1.putExtra("orderId", getIntent().getStringExtra("orderId"));
                    intent1.putExtra(EamConstant.EAM_ORDER_DETAIL_OPEN_SOURCE, "pay");
                    mActivity.startActivity(intent1);
                    this.finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取周边餐厅的rids
     */
    public void getaroundResIds(String radius, int geotable_id, String longitude, String latitude)
    {
        String geotableListUrl = NetHelper.GET_AROUND_RESID + radius + "&geotable_id=" + geotable_id + "&location=" + longitude + "," + latitude + "&page_size=" +
                CommonUtils.BAIDU_RETURN_NUMBER + "&rStatus=1";
        Logger.t(TAG).d("提交的参数为》" + geotableListUrl);
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        OkHttpUtils
                .get()
                .url(geotableListUrl)
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mActivity, null, TAG, e);
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                        ToastUtils.showShort("获取周边餐厅信息失败");
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).d("获取周边餐厅信息--> " + response);
                        try
                        {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("status");
                            if (status == 0)
                            {
                                JSONArray jsonArray = new JSONArray(jsonObject.getString("contents"));
                                List<String> ids = new ArrayList<String>();
                                for (int i = 0; i < jsonArray.length(); i++)
                                {
                                    JSONObject jObject = (JSONObject) jsonArray.get(i);
                                    String rStatus = jObject.getString("rStatus");
                                    if (rStatus.equals("1"))
                                    {
                                        String rid = jObject.getString("rId");
                                        ids.add(rid);
                                    }
                                }

                                if (mPresenter != null)
                                {
                                    if (pDialog != null && !pDialog.isShowing())
                                        pDialog.show();
                                    mPresenter.getMeetPersonList(CommonUtils.listToStrWishSeparator(ids, CommonUtils.SEPARATOR), orderId);
                                }
                            }
                            else
                            {
                                ToastUtils.showShort("获取周边餐厅失败");
                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                            Logger.t(TAG).d(e.getMessage());
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            Logger.t(TAG).d(e.getMessage());
                        }
                    }
                });
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {

    }

    @Override
    public void getMeetPersonListCallback(List<MeetPersonBean> response)
    {
        Logger.t(TAG).d("获取邂逅人成功信息" + response);
        if (response == null)
        {
            ToastUtils.showShort("获取邂逅人信息失败");
        }
        else if (response.size() == 0)
        {
            ToastUtils.showShort("暂无邂逅人信息");
        }
        else
        {
            meetPersonLst.addAll(response);
            mPushMeetPersonAdapter = new PushMeetPersonsAdapter(mActivity, meetPersonLst);
            mPullRefreshGridView.setAdapter(mPushMeetPersonAdapter);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

}
