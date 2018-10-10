package com.echoesnet.eatandmeet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.presenters.ImpIDResSearchView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDResSearchView;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.RoomSearchDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.iconify.IconDrawable;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ResSearchAct extends MVPBaseActivity<IDResSearchView,ImpIDResSearchView> implements IDResSearchView
{
    private static final String TAG = ResSearchAct.class.getSimpleName();
    @BindView(R.id.ll_search)
    AutoLinearLayout llSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_search)
    PullToRefreshListView lvSearch;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.tv_total)
    TextView tvTotal;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.fl_carimg)
    AutoFrameLayout flCarImg;
    @BindView(R.id.btn_checkout)
    Button btnCheckout;


    private RoomSearchDetailAdapter adapter;
    private int geotableId;
    private String keyword;
    // 分页
    private static final String PAGE_COUNT = "6";
//    private Dialog pDialog;
    private List<SearchRestaurantBean> dataList;
    private String resSource;
    private String[] location;

    private Context mAct;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_room_search_detail);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        if (pDialog != null && pDialog.isShowing())
//        {
//            pDialog.dismiss();
//            pDialog = null;
//        }
    }

    @Override
    protected ImpIDResSearchView createPresenter()
    {
        return new ImpIDResSearchView();
    }


    private void initAfterView()
    {
        mAct = this;
        topBar.setTitle(getResources().getString(R.string.order_search_result));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                ResSearchAct.this.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });
//        pDialog = DialogUtil.getCommonDialog(ResSearchAct.this, "正在处理...");
//        pDialog.setCancelable(false);
        resSource = getIntent().getStringExtra("searchSource");
        Logger.t(TAG).d("传递过来的source--> " + resSource);

        lvSearch.setMode(PullToRefreshBase.Mode.BOTH);
        lvSearch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //下拉刷新
                if (mPresenter != null)
                {
                    mPresenter.getResList("0", String.valueOf(dataList.size()), "refresh", keyword);
                }

                //getRestaurantList("0", String.valueOf(dataList.size()), "refresh", keyword);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载
                if (mPresenter != null)
                {
                    mPresenter.getResList(String.valueOf(dataList.size()), PAGE_COUNT, "add", keyword);
                }
            }
        });

        lvSearch.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                // Toast.makeText(context, "上拉刷新", Toast.LENGTH_SHORT).show();
            }
        });

        ListView actualListView = lvSearch.getRefreshableView();
        View empty = LayoutInflater.from(ResSearchAct.this).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有搜索结果");
        lvSearch.setEmptyView(empty);
        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                    SearchRestaurantBean bean = dataList.get(position-1);
                    if (!TextUtils.isEmpty(resSource) && resSource.equals("sayHello"))
                    {
                        // 进入餐厅布局
                        Intent intent = new Intent(mAct, CResStatusShowAct.class);
                        intent.putExtra("resId", bean.getrId());
                        intent.putExtra("resName", bean.getrName());
                        intent.putExtra("floorNum", bean.getFloor());
                        Logger.t(TAG).d("进入餐厅布局 resId--> " + bean.getrId() + " , resName--> " + bean.getrName() + " , floorNum--> " + bean.getFloor());
                        startActivity(intent);
                    }
                    else
                    {
                        // 进入餐厅详情
                        Intent intent = new Intent(mAct, DOrderMealDetailAct.class);
                        intent.putExtra("restId", bean.getrId());
                        intent.putExtra("source", "resSource");
                        EamApplication.getInstance().lessPrice = bean.getLessPrice();
                        Logger.t(TAG).d("进入餐厅详情 restId--> " + bean.getrId() + " , resName--> " + bean.getrName() + " , 起订价--> " + bean.getLessPrice());
                        startActivity(intent);
                    }
            }
        });
        // Need to use the Actual ListView when registering for Context Menu
        registerForContextMenu(actualListView);

        geotableId = getIntent().getIntExtra("geotable_id", 0);
        keyword = getIntent().getStringExtra("keyword");
        Logger.t(TAG).d("geotable_id：%s", geotableId + keyword);


        ivSearch.setImageDrawable(new IconDrawable(ResSearchAct.this, EchoesEamIcon.eam_s_search).colorRes(R.color.c3));
//        getSearchResData(keyword);


        dataList = new ArrayList<>();
        adapter = new RoomSearchDetailAdapter(ResSearchAct.this, dataList);
        actualListView.setAdapter(adapter);

//        pDialog.show();
        if (mPresenter != null)
        {
//            if (pDialog != null && !pDialog.isShowing())
//                pDialog.show();
            mPresenter.getResList("0", PAGE_COUNT, "add", keyword);
        }
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (lvSearch != null)
            lvSearch.onRefreshComplete();
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        if (lvSearch != null)
            lvSearch.onRefreshComplete();
    }

    @Override
    public void getResListCallback(List<SearchRestaurantBean> response, String operateType)
    {
        if (response == null)
        {
            ToastUtils.showShort("获取信息失败");
        }
        else
        {
            if (operateType.equals("refresh"))
            {
                dataList.clear();
            }
            dataList.addAll(response);
            adapter.notifyDataSetChanged();
        }
        if (lvSearch != null)
            lvSearch.onRefreshComplete();
    }
}
