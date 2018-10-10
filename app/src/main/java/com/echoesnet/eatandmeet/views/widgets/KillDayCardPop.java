package com.echoesnet.eatandmeet.views.widgets;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.FrameAnimator;
import com.echoesnet.eatandmeet.views.adapters.KillCardAdapter;
import com.echoesnet.eatandmeet.views.adapters.KillDayCardAdapter;
import com.echoesnet.eatandmeet.views.adapters.KillDayPrizeAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lc on 2017/7/14 10.
 */

public class KillDayCardPop extends Dialog
{

    private Activity mActivity;
    @BindView(R.id.gv_killcard)
    GridView gridView;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    @BindView(R.id.iv_getday)
    ImageView ivGet;
    @BindView(R.id.iv_getday2)
    TextView ivGet2;
    @BindView(R.id.prize_list)
    ListView prizeLv;
    @BindView(R.id.yue_selete)
    ImageView ivSelete;
    @BindView(R.id.killcard_layout)
    RelativeLayout yueRl;
    @BindView(R.id.s_killcard_layout)
    RelativeLayout qiRl;
    @BindView(R.id.iv_day_anim)
    ImageView ivDayAnim;

    @BindView(R.id.s_gv_killcard)
    GridView gridViewS;
    @BindView(R.id.s_gv_killcard2)
    GridView gridView2S;
    @BindView(R.id.s_iv_getday)
    ImageView ivGetS;
    @BindView(R.id.s_iv_getday2)
    TextView ivGet2S;
    @BindView(R.id.animation_lottie)
    LottieAnimationView animLottie;
    @BindView(R.id.selete_qi_click)
    View qiView;

    //选项卡
    private int position = -1;
    private boolean firstShow = true;
    private boolean sevenOver = false;
    private OnClickListenern onClick;
    private OnShowListenern onShow;
    private KillDayPrizeAdapter killDayPrizeAdapter;
    private List<Map<String, String>> prizeList;
    private String skin;

    public KillDayCardPop(Activity mActivity)
    {
        super(mActivity, R.style.dialog2);
        this.mActivity = mActivity;
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.pup_killdaycard, null);
        this.setContentView(contentView);
        ButterKnife.bind(this);
        prizeList = new ArrayList<>();
        killDayPrizeAdapter = new KillDayPrizeAdapter(mActivity, prizeList);
        animLottie.setAnimation("aeScrips/signin.json");
        animLottie.useHardwareAcceleration(true);
        animLottie.addAnimatorListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                animLottie.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
        initPopWindow();
    }

    private void initPopWindow()
    {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        this.getWindow().getWindowManager();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    @OnClick({R.id.selete_qi_click, R.id.selete_yue_click})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.selete_qi_click:
                if (position != 1 || position == -1)
                {
                    seletePosition(1, true);
                }
                break;
            case R.id.selete_yue_click:
                if (position != 0 || position == -1)
                {
                    seletePosition(0, true);
                }
                break;
            default:
                break;
        }
    }

    private void seletePosition(int position, boolean isAnim)
    {
        if (position == 0)
        {
            this.position = position;
            ivSelete.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.bg_find_yue));
            yueRl.setVisibility(View.VISIBLE);
            qiRl.setVisibility(View.GONE);

            if (firstShow && isAnim && !TextUtils.isEmpty(skin))
            {
                playAnim();
                firstShow = false;
            }
        }
        else
        {
            this.position = position;
            ivSelete.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable
                    .bg_find_qiri));
            yueRl.setVisibility(View.GONE);
            qiRl.setVisibility(View.VISIBLE);
        }
    }

    public void setQiDate(final List<Map<String, String>> list)
    {
        gridViewS.setAdapter(new KillCardAdapter(mActivity, list.subList(0, 3)));
        gridView2S.setAdapter(new KillCardAdapter(mActivity, list.subList(3, list.size())));
        ivGet2S.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onClick != null)
                    onClick.onQiClick();
                sevenOver=true;
                if ("今日签到".equals(ivGet2.getText()))
                {
                    seletePosition(0, true);
                }
                else
                {
                    //  dismiss();
                }
            }
        });

        //通知act可以获取签到状态了
        if (onShow != null)
            onShow.onShowState(true);
    }

    @Override
    public void dismiss()
    {
        super.dismiss();
        if (qiView.getVisibility() != View.GONE)
        {
            if ("领取福利".equals(ivGet2S.getText()))
            {
                seletePosition(1, false);
            }
            else
            {
                seletePosition(0, false);
            }
        }
        firstShow = true;
        sevenOver=false;
        this.skin = "";
    }


    public void showDialog(final List<Map<String, String>> list, final List<Map<String, String>>
            prizeList, boolean isCheckIn, String skin, String icon)
    {
        this.skin = skin;
        final KillDayCardAdapter adp = new KillDayCardAdapter(mActivity, list, skin, icon);
        this.prizeList.clear();
        this.prizeList.addAll(prizeList);
        killDayPrizeAdapter.notifyDataSetChanged();
        gridView.setAdapter(adp);

        ivGet2.setOnClickListener((v)->
        {
                if (onClick != null)
                    onClick.onYueClick();

                if ("领取福利".equals(ivGet2S.getText()))
                {
                    seletePosition(1, false);
                }
                else
                {
                    //  dismiss();
                }
        });

        if (!isCheckIn)
        {
            if (!isShowing())
            {
                show();
                if (firstShow && position == 0 && !TextUtils.isEmpty(skin))
                {
                    playAnim();
                    firstShow = false;
                }

            }
        }
        //设置今日或者明日礼物
        prizeLv.setAdapter(killDayPrizeAdapter);

        //通知act可以获取签到状态了
        if (onShow != null)
            onShow.onShowState(true);
    }

    public interface OnClickListenern
    {
        void onYueClick();

        void onQiClick();

    }

    public interface OnShowListenern
    {
        void onShowState(boolean isShow);
    }

    public void setOnShowListener(OnShowListenern onShowListenern)
    {
        onShow = onShowListenern;
    }

    public void setOnClickListener(OnClickListenern onClickListenern)
    {
        onClick = onClickListenern;
    }

    public void setCartState(boolean state)
    {
        if (ivGet == null)
            return;

        if (state)
        {
            killDayPrizeAdapter.upData(null, true);
            ivGet2.setBackground(ContextCompat.getDrawable(mActivity, R.drawable
                    .btn_killcard_c0331));
            ivGet2.setText("今日已签");
        }
        else
        {
            killDayPrizeAdapter.upData(null, false);
            ivGet2.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.btn_killcard_red));
            ivGet2.setText("今日签到");
        }
    }

    public void setQiCartState(String sevenWeal)
    {
        if (ivGet2S == null)
            return;

        switch (sevenWeal)
        {
            case "0":
                ivGet2S.setBackground(ContextCompat.getDrawable(mActivity, R.drawable
                        .btn_killcard_red));
                ivGet2S.setText("领取福利");
                seletePosition(1, false);
                break;
            case "1":
                ivGet2S.setBackground(ContextCompat.getDrawable(mActivity, R.drawable
                        .btn_killcard_c0331));
                ivGet2S.setText("今日已领");
                if (ivGet2.getText().equals("今日签到"))
                    seletePosition(0, false);
                break;
            case "2":
                ivGet2S.setText("今日已领");
                ivGet2S.setBackground(ContextCompat.getDrawable(mActivity, R.drawable
                        .btn_killcard_c0331));
                if (sevenOver)
                    break;
                seletePosition(0, false);
                ivSelete.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable
                        .bg_alone_yue));
                qiView.setEnabled(false);
                qiView.setVisibility(View.GONE);
                break;
        }
    }

    private void playAnim()
    {
        animLottie.setVisibility(View.VISIBLE);
        animLottie.playAnimation();
    }

}
