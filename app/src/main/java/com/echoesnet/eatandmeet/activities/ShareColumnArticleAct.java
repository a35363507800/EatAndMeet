package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIShareColumnArticleView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IFRestaurant4FindFrgView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IShareColumnArticleView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.Constant;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

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
 * @createDate 2017/9/16 0016
 * @description 分享到动态页面
 */
public class ShareColumnArticleAct extends MVPBaseActivity<ShareColumnArticleAct, ImpIShareColumnArticleView> implements IShareColumnArticleView
{

    private final String TAG = ShareColumnArticleAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.edit_content)
    EditText contentEdit;
    @BindView(R.id.img_column)
    ImageView columnImg;
    @BindView(R.id.tv_column_name)
    TextView columnNameTv;
    @BindView(R.id.tv_column_title)
    TextView columnTitleTv;
    @BindView(R.id.ll_select_location)
    LinearLayout selectLocationLL;
    @BindView(R.id.tv_location)
    TextView locationTv;

    private String vArticalId, location;
    private double posx, posy;
    private Activity mAct;
    private String shareType;
    private String activityType;//活动类型
    private String activityId;//活动Id
    private String imgUrl;
    private String clubContent;
    private String clubId;
    private boolean isShared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_share_column_article);
        ButterKnife.bind(this);
        mAct = this;

        vArticalId = getIntent().getStringExtra("vArticalId");
        shareType = getIntent().getStringExtra("shareType");
        activityType = getIntent().getStringExtra("activityType");
        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        imgUrl = getIntent().getStringExtra("imgUrl");
        clubContent = getIntent().getStringExtra("clubContent");
        clubId = getIntent().getStringExtra("clubId");
        activityId = getIntent().getStringExtra("activityId");


        if (!TextUtils.isEmpty(title))
            columnNameTv.setText(title);
        else
            columnNameTv.setVisibility(View.GONE);
        columnTitleTv.setText(content);
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .centerCrop()
                .load(imgUrl)
                .placeholder(R.drawable.qs_cai_user)
                .error(R.drawable.qs_cai_user)
                .into(columnImg);
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                Intent data = new Intent();
                data.putExtra("isShare", isShared);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                share();
            }
        }).setText("分享");
        topBarSwitch.setBottomLineVisibility(View.VISIBLE);
        List<Map<String, TextView>> navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            Map<String, TextView> map = navBtns.get(i);
            TextView tv = map.get(TopBarSwitch.NAV_BTN_ICON);
            switch (i)
            {
                case 0:
                    tv.setText("取消");
                    tv.setTextSize(15);
                    tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0321));
                    break;
                case 1:
                    tv.setText("发布");
                    tv.setTextSize(15);
                    tv.setVisibility(View.VISIBLE);
                    tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
                    break;
                default:
                    break;
            }
        }
    }

    private void share()
    {
        String content = contentEdit.getText().toString().trim();
        Logger.t(TAG).d("activityType =="+ activityType);
        if ("column".equals(shareType) && !TextUtils.isEmpty(vArticalId))
        {
            mPresenter.shareArticle(content, vArticalId, String.valueOf(posx), String.valueOf(posy), location);
        }else if ("activity".equals(shareType))
        {
            mPresenter.shareBannerAct2Trends(content,activityType,activityId,posx, posy, location);
        }else if ("club".equals(shareType))
        {
            mPresenter.shareClubToTrends(content,imgUrl,clubContent,clubId,posx, posy, location);
        }
    }

    @OnClick({R.id.ll_select_location})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_select_location:
                Intent intent = new Intent(mAct, TrendsSelectLocationAct.class);
                intent.putExtra("showNo", TextUtils.isEmpty(location));
                startActivityForResult(intent, EamConstant.EAM_OPEN_SELECT_LOCATION);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        data.putExtra("isShare", isShared);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected ImpIShareColumnArticleView createPresenter()
    {
        return new ImpIShareColumnArticleView();
    }

    @Override
    public void shareArticleCallback(String response)
    {
        ToastUtils.showShort("分享成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void shareActivityCallback(String response)
    {
        ToastUtils.showShort("分享成功");
        isShared = true;
        Intent data = new Intent();
        data.putExtra("isShare", isShared);
        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public void shareBannerActivityCallback(String response)
    {
        ToastUtils.showShort("分享成功");
        isShared = true;
        Intent data = new Intent();
        data.putExtra("isShare", isShared);
        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public void requestErr(String code)
    {
        Logger.t(TAG).d("分享失败==" + code);
        ToastUtils.showShort("分享失败");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_SELECT_LOCATION:
                if (resultCode == RESULT_OK)
                {
                    boolean isShow = data.getBooleanExtra("isShow", false);
                    if (isShow)
                    {
                        location = data.getStringExtra("locationName");
                        posx = data.getDoubleExtra("latitude", 0);
                        posy = data.getDoubleExtra("longitude", 0);
                        Logger.t(TAG).d(location + "|" + posx + "|" + posy);
                        locationTv.setText(location);
                        locationTv.setFocusable(true);
                        locationTv.requestFocus();
                        locationTv.setSelected(true);
                    } else
                    {
                        locationTv.setText("选择位置");
                    }
                }
                break;
        }
    }
}
