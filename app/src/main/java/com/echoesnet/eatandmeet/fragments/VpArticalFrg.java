package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ColumnArticleDetailAct;
import com.echoesnet.eatandmeet.activities.TrendsDetailAct;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.presenters.ImpIVpArticalPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IVpArticalView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.views.adapters.VpArticalAdapter;
import com.orhanobut.logger.Logger;
import com.ss007.swiprecycleview.AdapterLoader;
import com.ss007.swiprecycleview.SwipeRefreshRecycleView;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static android.app.Activity.RESULT_OK;
import static com.echoesnet.eatandmeet.activities.TrendsDetailAct.RESULT_TRENDS_DELETE;


/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author lc
 * @version 1.0
 * @modifier
 * @createDate 2017/9/12
 * @description 个人详情页大V Tab页面
 */
public class VpArticalFrg extends MVPBaseFragment<VpArticalFrg, ImpIVpArticalPre> implements IVpArticalView, VpArticalAdapter.VpArticalItemClick
{
    private final static String TAG = VpArticalFrg.class.getSimpleName();
    @BindView(R.id.srrv_recyview)
    SwipeRefreshRecycleView srrvRecyview;
    @BindView(R.id.empty_view)
    EmptyView emptyView;
    private String luid = "";
    private String id;
    private Activity mAct;
    private List<FTrendsItemBean> vPItemList;
    private Unbinder unbinder;
    private VpArticalAdapter adapter;
    private boolean refreshPraise = false;
    private final int PAGE_SIZE = 6;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frg_big_artical, null, false);
        unbinder = ButterKnife.bind(this, view);
        mAct = getActivity();
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            luid = bundle.getString("uId", "");
            id = bundle.getString("id", "");
        }
        vPItemList = new ArrayList<>();
        adapter = new VpArticalAdapter(mAct,vPItemList);
        adapter.setHasMore(true);
        adapter.setVpArticalItemClick(this);

        emptyView.setContent("暂时还没有发布文章哦~");
        emptyView.setImageId(R.drawable.bg_nochat);
        srrvRecyview.init(adapter, emptyView);
        srrvRecyview.setAdapter(adapter);
        srrvRecyview.setPullRefreshEnable(false);
        adapter.setOnRefreshLoadMoreListener(new AdapterLoader.OnRefreshLoadMoreListener()
        {
            @Override
            public void onRefresh()
            {
                srrvRecyview.setRefresh(false);
            }

            @Override
            public void onLoadMore()
            {
                if (mPresenter != null)
                    mPresenter.getArticalList(TextUtils.isEmpty(luid) ? id : luid, String.valueOf(vPItemList.size()), PAGE_SIZE+"", "add");
            }
        });

        if (mPresenter != null)
        {
            mPresenter.getArticalList(TextUtils.isEmpty(luid) ? id : luid, "0", "5", "add");
        }

        return view;
    }

    @Override
    protected ImpIVpArticalPre createPresenter()
    {
        return new ImpIVpArticalPre();
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }

    @Override
    public void praiseClick(TextView tvPraise, int position, FTrendsItemBean itemBean)
    {

        if (mPresenter != null)
            mPresenter.likeArtical(position, itemBean.getIsLike(), itemBean.gettId(), itemBean.getLikedNum());
    }

    @Override
    public void commentClick(FTrendsItemBean itemBean)
    {
        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.getExt().getArticleId());
        intent.putExtra("data", itemBean);
        getActivity().startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
    }


    @Override
    public void contentClick(TextView view,FTrendsItemBean itemBean)
    {
        if ("4".equals(itemBean.getType()))
        {
            FTrendsItemBean.ExtBean extBean = itemBean.getExt();
            Intent intent = new Intent(mAct, ColumnArticleDetailAct.class);
            if (extBean != null)
            {
                intent.putExtra("articleId", extBean.getArticleId());
                intent.putExtra("columnName", extBean.getColumnName());
                intent.putExtra("columnTitle", extBean.getTitle());
                intent.putExtra("imgUrl", extBean.getImgUrl());
            }
            getActivity().startActivity(intent);

            CommonUtils.serverIncreaseRead(mAct, extBean.getArticleId(), new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {

                    new Handler().postDelayed(()->
                    {

                            view.setText(response+"人 已读");
                            itemBean.setReadNum(response);
                            Logger.t(TAG).d("response==>>"+response);
                            if (adapter!=null)
                            {
                                adapter.notifyDataSetChanged(true);
                            }
                    },500);

                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });
        }
    }

    @Override
    public void itemClick(TextView view,int position, FTrendsItemBean itemBean)
    {
        Intent intent = new Intent(getActivity(), TrendsDetailAct.class);
        intent.putExtra("tId", itemBean.gettId());
        intent.putExtra("data", itemBean);
        getActivity().startActivityForResult(intent, EamConstant.EAM_OPEN_TRENDS_DETAIL);
    }

    @Override
    public void praiseClickCallBack(int position, String flg, int likeNumInt)
    {
        FTrendsItemBean itemBean = vPItemList.get(position);
        itemBean.setIsLike(flg);
        itemBean.setLikedNum(String.valueOf(likeNumInt));
        adapter.notifyDataSetChanged(true);
    }

    @Override
    public void getArticalListCallBack(String type, List<FTrendsItemBean> aticalsList)
    {
        Logger.t(TAG).d("aticalsList>"+aticalsList);
        if (aticalsList != null)
        {
            if (TextUtils.equals(type, "refresh"))
            {
                vPItemList.clear();
            }
            if (aticalsList.isEmpty())
                adapter.setHasMore(false);
            else
                adapter.setHasMore(true);

            aticalsList.removeAll(vPItemList);
            vPItemList.addAll(aticalsList);
            adapter.setList(vPItemList);
        }
        srrvRecyview.setRefresh(false);

    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        Logger.t(TAG).d("错误码为：%s", code);
        switch (interfaceName)
        {
            case NetInterfaceConstant.TrendC_likeArticle:
                break;
            case NetInterfaceConstant.TrendC_articleList:
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy()
    {
        if (adapter != null)
        {
            adapter.onDestroy();
        }
        super.onDestroy();
    }

    public void reFreshInfo(boolean reFresh, String type)
    {
        if (reFresh)
        {
            if (mPresenter != null)
                mPresenter.getArticalList(TextUtils.isEmpty(luid) ? id : luid, "0", "8", type);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        Logger.t(TAG).d("requestCode>>" + requestCode + "|" + resultCode);
        int toPosition = 0;
        String tId = "";
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_ARTICAL_DETAIL:
                if (resultCode == RESULT_OK)
                {
                    tId = data.getStringExtra("tId");
                    String likedNum = data.getStringExtra("likedNum");
                    String commentNum = data.getStringExtra("commentNum");
                    String isLike = data.getStringExtra("isLike");
                    String readNum = data.getStringExtra("readNum");
                    toPosition = data.getIntExtra("position", 0);
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.getExt().setArticleId(tId);
                    int index = vPItemList.indexOf(fTrendsItemBean);
                    Logger.t(TAG).d("index>>" + index + "|" + tId + "|" + likedNum);
                    if (index < 0)
                        return;
                    ((FTrendsItemBean) vPItemList.get(index)).setLikedNum(likedNum);
                    ((FTrendsItemBean) vPItemList.get(index)).setCommentNum(commentNum);
                    ((FTrendsItemBean) vPItemList.get(index)).setIsLike(isLike);
                    ((FTrendsItemBean) vPItemList.get(index)).setReadNum(readNum);
                    adapter.notifyDataSetChanged(true);
                } else if (resultCode == RESULT_TRENDS_DELETE)
                {
                    String deleteTid = data.getStringExtra("tId");
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.getExt().setArticleId(deleteTid);
                    int index = vPItemList.indexOf(fTrendsItemBean);
                    if (index >= 0)
                    {
                        vPItemList.remove(index);
                        adapter.notifyDataSetChanged(false);
                    }
                }
                break;
            case EamConstant.EAM_OPEN_TRENDS_DETAIL:
                if (resultCode == RESULT_OK)
                {
                    tId = data.getStringExtra("tId");
                    String likedNum = data.getStringExtra("likedNum");
                    String commentNum = data.getStringExtra("commentNum");
                    String isLike = data.getStringExtra("isLike");
                    String readNum = data.getStringExtra("readNum");
                    toPosition = data.getIntExtra("position", 0);
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.settId(tId);
                    int index = vPItemList.indexOf(fTrendsItemBean);
                    Logger.t(TAG).d("index>>" + index + "|" + tId + "|" + likedNum);
                    if (index < 0)
                        return;
                    ((FTrendsItemBean) vPItemList.get(index)).setLikedNum(likedNum);
                    ((FTrendsItemBean) vPItemList.get(index)).setCommentNum(commentNum);
                    ((FTrendsItemBean) vPItemList.get(index)).setIsLike(isLike);
                    ((FTrendsItemBean) vPItemList.get(index)).setReadNum(readNum);
                    adapter.notifyDataSetChanged(true);
                } else if (resultCode == RESULT_TRENDS_DELETE)
                {
                    String deleteTid = data.getStringExtra("tId");
                    FTrendsItemBean fTrendsItemBean = new FTrendsItemBean();
                    fTrendsItemBean.settId(deleteTid);
                    int index = vPItemList.indexOf(fTrendsItemBean);
                    if (index >= 0)
                    {
                        vPItemList.remove(index);
                        adapter.notifyDataSetChanged(false);
                    }
                }
                break;
            default:
                break;
        }

    }
}
