package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.baidu.mapapi.search.core.PoiInfo;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.presenters.ImpITrendsSelectLocationPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsSelectLocationView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.views.adapters.TrendsSearchLocationAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/18 0018
 * @description 选择位置页
 */
public class TrendsSelectLocationAct extends MVPBaseActivity<TrendsSelectLocationAct,ImpITrendsSelectLocationPre> implements ITrendsSelectLocationView
{
    @BindView(R.id.icon_tv_search)
    IconTextView searchIconTv;
    @BindView(R.id.rlv_search_location)
    RecyclerView searchLocationRlv;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;

    private TrendsSearchLocationAdapter trendsSearchLocationAdapter;
    private List<PoiInfo> poiInfoList;
    private Activity mAct;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_select_location);
        ButterKnife.bind(this);
        mAct = this;
        poiInfoList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchLocationRlv.setLayoutManager(linearLayoutManager);
        trendsSearchLocationAdapter = new TrendsSearchLocationAdapter(this,poiInfoList);
        trendsSearchLocationAdapter.setSelectPosition(getIntent().getBooleanExtra("showNo",false)?0:-1);
        searchLocationRlv.setAdapter(trendsSearchLocationAdapter);
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {

            }
        }).setText(mAct.getResources().getString(R.string.trends_select_location));
        trendsSearchLocationAdapter.setItemClickListener(new TrendsSearchLocationAdapter.ItemClickListener()
        {
            @Override
            public void itemClick(PoiInfo poiInfo)
            {
                Intent intent = new Intent();
                intent.putExtra("isShow",true);
                intent.putExtra("locationName",poiInfo.name);
                intent.putExtra("latitude",poiInfo.location.latitude);
                intent.putExtra("longitude",poiInfo.location.longitude);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void noShowLocationClick()
            {
                Intent intent = new Intent();
                intent.putExtra("isShow",false);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        mPresenter.searNearLocation();
    }

    @OnClick({R.id.icon_tv_search})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.icon_tv_search:
                Intent intent = new Intent(mAct,TrendsSearchLocationAct.class);
                intent.putExtra("city",mPresenter.getmCity());
                startActivityForResult(intent,EamConstant.EAM_OPEN_SEARCH_LOCATION);
                break;
        }
    }

    @Override
    protected ImpITrendsSelectLocationPre createPresenter()
    {
        return new ImpITrendsSelectLocationPre();
    }

    @Override
    public void searchLocationCallBack(List<PoiInfo> poiInfos)
    {
        poiInfoList.clear();
        poiInfoList.addAll(poiInfos);
        trendsSearchLocationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == EamConstant.EAM_OPEN_SEARCH_LOCATION && resultCode == RESULT_OK)
        {
            Intent intent = new Intent();
            intent.putExtra("isShow",data.getBooleanExtra("isShow",false));
            intent.putExtra("locationName",data.getStringExtra("locationName"));
            intent.putExtra("latitude",data.getDoubleExtra("latitude",0));
            intent.putExtra("longitude",data.getDoubleExtra("longitude",0));
            Logger.t("select>").d(intent.toString());
            setResult(RESULT_OK,intent);
            finish();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}
