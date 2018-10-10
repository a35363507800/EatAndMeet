package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ReportFoulsRoomAct;
import com.echoesnet.eatandmeet.models.bean.LookAnchorBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.KillCardAdapter;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by lc on 2017/7/14 10.
 */

public class KillCardPop extends Dialog
{
    private KillCardPop mLiveHostInfoPop;
    private Activity mActivity;
    @BindView(R.id.gv_killcard)
     GridView gridView;
    @BindView(R.id.gv_killcard2)
     GridView gridView2;
    @BindView(R.id.iv_getday)
     ImageView ivGet;
    @BindView(R.id.iv_getday2)
    TextView ivGet2;

    private OnClickListenern onClick;
    private OnShowListenern onShow;
    public KillCardPop(Activity mActivity)
    {
        super(mActivity,R.style.dialog2);
        this.mActivity = mActivity;
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.pup_killcard, null);
        this.setContentView(contentView);
        ButterKnife.bind(this);
        initPopWindow();
    }

    private void initPopWindow()
    {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        this.getWindow().setBackgroundDrawableResource(R.color.transparent);
        lp.width = CommonUtils.getScreenWidth(mActivity);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);

//        AnimationDrawable animationDrawable = (AnimationDrawable) ivGet
//                .getDrawable();
//        animationDrawable.start();


    }


    public void showDialog(List<Map<String,String>> list)
    {

        gridView.setAdapter(new KillCardAdapter(mActivity,list.subList(0,3)));
        gridView2.setAdapter(new KillCardAdapter(mActivity,list.subList(3,list.size())));
        ivGet2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(onClick!=null)
                    onClick.onClick();
            }
        });
        if(!isShowing())
            show();

        if(onShow!=null)
            onShow.onShowState(true);
    }
    public interface OnClickListenern
    {
        void onClick();
    }
    public interface OnShowListenern
    {
        void onShowState(boolean isShow);
    }

    public void setOnClickListener(OnClickListenern onClickListenern)
    {
        onClick=onClickListenern;
    }

    public void setOnShowListener(OnShowListenern onShowListenern)
    {
        onShow=onShowListenern;
    }

    public void setCartState(boolean state)
    {
        if (ivGet2 == null)
            return;

        if (state)
        {
//            ivGet.setImageResource(R.drawable.yiqian);
//            ivGet.setEnabled(false);

            ivGet2.setBackgroundResource(R.drawable.btn_killcard_c0331);
            ivGet2.setText("领取福利");
        } else
        {
//            ivGet.setEnabled(true);
//            ivGet.setImageResource(R.drawable.lingqu);

            ivGet2.setBackgroundResource(R.drawable.btn_killcard_red);
            ivGet2.setText("领取福利");
        }
    }
}
