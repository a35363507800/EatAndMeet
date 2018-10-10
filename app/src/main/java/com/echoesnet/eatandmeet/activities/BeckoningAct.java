package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MeetPersonBean;
import com.echoesnet.eatandmeet.presenters.ImpIBeckoningActView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IBeckoningActView;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.BeckoningAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.cardSlidePanel.SwipeCardsView;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier ben
 * @createDate 2016/12/26 0026
 * @description 心动一下
 */

public class BeckoningAct extends MVPBaseActivity<BeckoningAct, ImpIBeckoningActView> implements IBeckoningActView
{
    private final String TAG = BeckoningAct.class.getSimpleName();

    @BindView(R.id.image_slide_panel)
    SwipeCardsView cardSlidePanel;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.btn_find_meet_love)
    Button loveBtn;
    @BindView(R.id.btn_find_meet_hate)
    Button hateBtn;

    private Activity mActivity;
    private Dialog pDialog;
    private int currentIndex;
    private List<MeetPersonBean> meetPersonList;
    private BeckoningAdapter beckoningAdapter;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_beckoning);
        ButterKnife.bind(this);
        afterView();
    }

    @Override
    protected ImpIBeckoningActView createPresenter()
    {
        return new ImpIBeckoningActView(this, this);
    }

    private void afterView()
    {
        mActivity = this;
        pDialog = DialogUtil.getCommonDialog(mActivity, "正在处理");
        pDialog.setCancelable(false);
        topBar.setTitle("闻相识美");
        topBar.getLeftButton().setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        meetPersonList = new ArrayList<>();
        cardSlidePanel.retainLastCard(true);
        cardSlidePanel.enableSwipe(true);
        cardSlidePanel.setCardsSlideListener(new SwipeCardsView.CardsSlideListener()
        {
            @Override
            public void onShow(int index)
            {
                currentIndex = index;
                Logger.t(TAG).d("show>>>>" + index);
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type)
            {
                String orientation = "";
                switch (type)
                {
                    case LEFT:
                        orientation = "向左飞出";
                        break;
                    case RIGHT:
                        orientation = "向右飞出";
                        break;
                }
                mPresenter.getAroundPerson("1", "loveOrHateCallback", currentIndex);
            }

            @Override
            public void onItemClick(View cardImageView, int index)
            {
                Logger.t(TAG).d("currentIndex==" + currentIndex + "index==" + index);
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", meetPersonList.get(currentIndex).getLuId());
                startActivity(intent);
            }
        });

        pDialog.show();
        mPresenter.getAroundPerson("20", "refresh", 0);
    }

    @OnClick({R.id.btn_check_userinfo, R.id.btn_find_meet_love, R.id.btn_find_meet_hate})
    void clickEvent(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_check_userinfo:
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", meetPersonList.get(currentIndex).getLuId());
                startActivity(intent);
                break;
            case R.id.btn_find_meet_love:
                makeLoveOrHate(1);
                break;
            case R.id.btn_find_meet_hate:
                makeLoveOrHate(0);
                break;

        }
    }

    /**
     * 心动  或者 不喜欢
     *
     * @param type 0不喜欢    1心动
     */
    private void makeLoveOrHate(int type)
    {
        pDialog.show();
        MeetPersonBean meetPersonBean = meetPersonList.get(currentIndex);
        if (type == 0)
        {
            Logger.t(TAG).d("XXXXXXXXXXXXXX");
            mPresenter.markLoveOrHate(meetPersonBean.getLuId(), "0", currentIndex);
        } else
        {
            Logger.t(TAG).d("❤❤❤❤❤❤❤❤");
            mPresenter.markLoveOrHate(meetPersonBean.getLuId(), "1", currentIndex);
        }
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mActivity, null, TAG, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getAroundPersonCallback(List<MeetPersonBean> str, int currentItem, String operType)
    {
        if (meetPersonList.size() < 20 && str != null && str.size() > 0)
        {
            Logger.t(TAG).d("pull>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            mPresenter.getAroundPerson(20 - meetPersonList.size() + "", "refresh", currentIndex);
        }
        meetPersonList.addAll(str);
        if (beckoningAdapter == null)
        {
            beckoningAdapter = new BeckoningAdapter(meetPersonList, mActivity);
            cardSlidePanel.setAdapter(beckoningAdapter);
        } else
        {
            cardSlidePanel.notifyDatasetChanged(currentIndex);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void loveOrHateCallback(String response, String type)
    {

        Logger.t(TAG).d("loveOrHate=====suc");
        if ("0".equals(type))
        {
            cardSlidePanel.slideCardOut(SwipeCardsView.SlideType.LEFT);
        } else
        {
            cardSlidePanel.slideCardOut(SwipeCardsView.SlideType.RIGHT);
        }
        mPresenter.getAroundPerson("1", "loveOrHateCallback", 0);


    }
}
