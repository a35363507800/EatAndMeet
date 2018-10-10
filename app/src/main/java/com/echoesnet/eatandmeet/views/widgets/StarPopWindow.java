package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.StarChartBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.LoadFootView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.adapters.StarChartAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/12/4
 * @description
 */
public class StarPopWindow extends PopupWindow
{
    private final String TAG = StarPopWindow.class.getSimpleName();
    private Activity mContext;
    protected Rect mRect = new Rect();
    private final int[] mLocation = new int[2];
    private PullToRefreshListView ptflvView;
    private List<StarChartBean> starList;
    private ListView listView;
    private StarChartAdapter mAdapter;
    private final int PAGE_COUNT = 20;
    private LevelHeaderView rivMyIcon;
    private TextView tvTopNumShow;
    private TextView tvRefresh;
    private String roomId;
    private String isVUser;
    private View hideView;
    // 没有更多内容时添加的底部提示布局
    private View footView;
    // 再没有更多数据获取时,禁止列表上拉加载动作
    private boolean pullMove = true;
    private Dialog pDialog;


    public StarPopWindow(Activity context, String mRoomId, String isVUser)
    {
        //设置布局的参数
        this(context, ViewGroup.LayoutParams.MATCH_PARENT, CommonUtils.dp2px(context, 444), mRoomId, isVUser);
    }

    public StarPopWindow(Activity context, int width, int height, String roomId, String isVUser)
    {
        super();
        this.mContext = context;
        this.roomId = roomId;
        this.isVUser = isVUser;
        //设置可以获得焦点
        setFocusable(true);
        //设置弹窗内可点击
        setTouchable(true);
        //设置弹窗外可点击
        setOutsideTouchable(true);
        this.setAnimationStyle(R.style.StarAnimationPop);
        //设置弹窗的宽度和高度
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置弹窗的布局界面
        setContentView(LayoutInflater.from(mContext).inflate(R.layout.pop_star_chart, null));
        initUI();
    }

    /**
     * 初始化弹窗列表
     */
    private void initUI()
    {
        pDialog = DialogUtil.getCommonDialog(mContext, "正在刷新...");
        pDialog.setCancelable(false);
        rivMyIcon = (LevelHeaderView) getContentView().findViewById(R.id.riv_my_icon);
        tvTopNumShow = (TextView) getContentView().findViewById(R.id.tv_top_num_show);
        tvRefresh = (TextView) getContentView().findViewById(R.id.tv_refresh);
        ptflvView = (PullToRefreshListView) getContentView().findViewById(R.id.ptflv_view);
        footView = LayoutInflater.from(mContext).inflate(R.layout.footview_star_show, null);
        starList = new ArrayList<>();
        mAdapter = new StarChartAdapter(mContext, starList);
        ptflvView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        listView = ptflvView.getRefreshableView();
        listView.setAdapter(mAdapter);
        ptflvView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //to do nothing
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {

                getAllAnchor(String.valueOf(starList.size()), PAGE_COUNT + "", "add");
                LoadFootView.showFootView(listView, false, footView, "没有更多了...");

            }
        });
        getAllAnchor(String.valueOf(starList.size()), PAGE_COUNT + "", "add");
        tvRefresh.setOnClickListener((v) ->
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            getAllAnchor(0 + "", String.valueOf(starList.size()), "refresh");
        });
    }

    //获取榜单列表数据
    private void getAllAnchor(String startIdx, String num, String type)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put(ConstCodeTable.startIdx, startIdx);
        reqParamMap.put(ConstCodeTable.num, num);
        reqParamMap.put(ConstCodeTable.roomId, roomId);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("返回参数》》" + response.toString());
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    String ranking = jsonObject.getString("ranking");
                    String list = jsonObject.getString("list");
                    String phUrl = jsonObject.getString("phUrl");
                     String isVusers = jsonObject.getString("isVuser");
                    List<StarChartBean> mList = new Gson().fromJson(list, new TypeToken<List<StarChartBean>>()
                    {
                    }.getType());

                    setDataOnUi(ranking, mList, phUrl, isVusers, type);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d("解析异常失败》》" + e.getMessage());
                }
            }
        }, NetInterfaceConstant.AnchorActivityC_ranking, reqParamMap);
    }

    //设置数据适配器
    private void setDataOnUi(String ranking, List<StarChartBean> mList, String phUrl, String isVuser, String type)
    {

        ptflvView.onRefreshComplete();
        if (!TextUtils.isEmpty(ranking))
        {
            tvTopNumShow.setText("当前排名：" + (TextUtils.equals("0", ranking) ? "未上榜" : ranking));
            rivMyIcon.setHeadImageByUrl(phUrl);
            rivMyIcon.showRightIcon(isVuser);
        }

        if (mList.size() == 0)
        {
            LoadFootView.showFootView(listView, true, footView, "没有更多了...");
            //禁止上啦
            pullMove = false;
        }
        if (TextUtils.equals("refresh", type))
        {
            starList.clear();
            LoadFootView.showFootView(listView, false, footView, "没有更多了...");
            //允许上啦
            pullMove = true;
        }

        if (ptflvView != null)
        {
            ptflvView.onRefreshComplete();
            if (pullMove)
            {
                Logger.t(TAG).d("允许上下拉动"); // 恢复上拉
                ptflvView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
            } else
            {
                Logger.t(TAG).d("禁止上拉"); // 禁止上拉
                ptflvView.setMode(PullToRefreshBase.Mode.DISABLED);
            }
        }
        starList.addAll(mList);
        mAdapter.notifyDataSetChanged();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }


    /**
     * 显示弹窗列表界面
     */
    public void show(View view, View hideView)
    {
        if (!this.isShowing())
        {
            if (hideView == null)
            {
                backgroundAlpha(0.5f);
            } else
            {
                this.hideView = hideView;
                hideView.setVisibility(View.VISIBLE);
            }
            //获得点击屏幕的位置坐标
            view.getLocationOnScreen(mLocation);
            //设置矩形的大小
            mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(), mLocation[1] + view.getHeight());
            //显示弹窗的位置
            showAtLocation(view, Gravity.BOTTOM, 0, 0);
        } else
        {
            this.dismiss();
        }
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }

    /**
     * 消失弹窗，设置添加屏幕的背景透明度
     */
    @Override
    public void dismiss()
    {
        this.backgroundAlpha(1.0f);
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
        if (hideView != null)
            hideView.setVisibility(View.GONE);
        super.dismiss();
    }
}
