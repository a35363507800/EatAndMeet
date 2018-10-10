package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PackagesBean;
import com.echoesnet.eatandmeet.models.bean.PayMetadataBean;
import com.echoesnet.eatandmeet.presenters.ImpIClubFoodDetailView;
import com.echoesnet.eatandmeet.presenters.ImpIClubInfoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubFoodDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IClubInfoView;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.payUtil.IPayFinishedListener;
import com.echoesnet.eatandmeet.utils.payUtil.PayHelper;
import com.echoesnet.eatandmeet.views.adapters.ClubInfoChildAdapter;
import com.echoesnet.eatandmeet.views.adapters.ClubInfoFoodAdapter;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.panxw.android.imageindicator.ImageIndicatorView;
import com.pingplusplus.android.Pingpp;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClubFoodDetailAct extends MVPBaseActivity<IClubFoodDetailView, ImpIClubFoodDetailView> implements IClubFoodDetailView
{
    private static final String TAG = ClubFoodDetailAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.rv_food_list)
    RecyclerView rvFoodList;
    @BindView(R.id.rv_food_type)
    RecyclerView rvFoodTypeList;
    private Activity mContext;
    private ClubInfoFoodAdapter clubInfoFoodAdapter;
    private ClubInfoChildAdapter clubInfoChildAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_club_food_detail);
        ButterKnife.bind(this);
        initAfterViews();
    }

    private void initAfterViews()
    {
        mContext=this;

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

            }
        }).setText("套餐详情");

        topBar.setBottomLineVisibility(View.VISIBLE);

        List<PackagesBean> list=getIntent().getParcelableArrayListExtra("Packages");
        int index=getIntent().getIntExtra("index",0);
        clubInfoChildAdapter = new ClubInfoChildAdapter(mContext, list);
        clubInfoChildAdapter.setCheckPosition(index);
        rvFoodTypeList.setAdapter(clubInfoChildAdapter);
        rvFoodTypeList.setLayoutManager(new GridLayoutManager(mContext, 3));
        clubInfoChildAdapter.setOnItemtClickListener(new ClubInfoChildAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                if(clubInfoFoodAdapter!=null)
                    clubInfoFoodAdapter.setList(list.get(position).getFood());
            }
        });

        clubInfoFoodAdapter=new ClubInfoFoodAdapter(mContext,list.get(clubInfoChildAdapter.getCheckPosition()).getFood());
        rvFoodList.setAdapter(clubInfoFoodAdapter);
        rvFoodList.setLayoutManager(new LinearLayoutManager(mContext,  LinearLayoutManager.VERTICAL, false));
    }




    @Override
    protected void onDestroy()
    {
        super.onDestroy();

    }

    @Override
    public void finish() {
        int index=clubInfoChildAdapter.getCheckPosition();

        Intent intent =getIntent();
        intent.putExtra("index",index);
        setResult(RESULT_OK,intent);
        super.finish();
    }

    @Override
    protected ImpIClubFoodDetailView createPresenter()
    {
        return new ImpIClubFoodDetailView();
    }


}
