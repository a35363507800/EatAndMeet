package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CUserInfoBean;
import com.echoesnet.eatandmeet.views.MyItemDecoration;
import com.echoesnet.eatandmeet.views.adapters.PersonInfoAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @createDate 2017/7/17 16.
 * @description app个人详情页的个人资料Tab页面
 */

public class CUserInfoFrg extends BaseFragment
{

    @BindView(R.id.rv_view)
    RecyclerView rvView;
    private Activity mActivity;
    private String toCheckUserUid;
    private PersonInfoAdapter mAdapter;
    private CUserInfoBean bean;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_user_info, null, false);
        unbinder = ButterKnife.bind(this, view);
        toCheckUserUid = getArguments().getString("uId");
        bean = getArguments().getParcelable("userbean");
        afterViews();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

    }

    private void afterViews()
    {
        mActivity = getActivity();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvView.setLayoutManager(linearLayoutManager);
        mAdapter = new PersonInfoAdapter(mActivity, bean);
        rvView.addItemDecoration(new MyItemDecoration());
        rvView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected String getPageName()
    {
        return CUserInfoFrg.class.getSimpleName();
    }
}
