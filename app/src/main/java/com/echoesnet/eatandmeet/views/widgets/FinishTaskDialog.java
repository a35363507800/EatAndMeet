package com.echoesnet.eatandmeet.views.widgets;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.FinishTaskBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.TaskFinishAdapter;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by an on 2017/4/18 0018.
 */

public class FinishTaskDialog extends Dialog implements View.OnClickListener {
    private final String TAG = FinishTaskDialog.class.getSimpleName();

    @BindView(R.id.tv_title)
    TextView titleTv;
    @BindView(R.id.tv_exp_title)
    TextView expTitleTv;
    @BindView(R.id.tv_exp_num)
    TextView expNumTv;
    @BindView(R.id.tv_level_left)
    TextView levelLeftTv;
    @BindView(R.id.tv_level_right)
    TextView levelRightTv;
    @BindView(R.id.gv_icon)
    GridView iconGv;
    @BindView(R.id.tv_tip)
    TextView tipTv;
    @BindView(R.id.tv_confirm)
    TextView confirmTv;
    @BindView(R.id.rl_exp)
    LinearLayout expRl;
    @BindView(R.id.exp_pro)
    ProgressBar expPro;
    @BindView(R.id.tv_toat)
    TextView tvToat;

    private Context mContext;
    private List<FinishTaskBean.RewardsBean> rewardsList;
    private TaskFinishAdapter taskFinishAdapter;
    private DialogClickListener dialogClickListener;

    public FinishTaskDialog( Context context, int theme) {
        super(context,theme);
        mContext = context;
        rewardsList = new ArrayList<>();
        initView();
    }

    public void setDialogClickListener(DialogClickListener dialogClickListener)
    {
        this.dialogClickListener = dialogClickListener;
    }

    public void setConfirm(String confirm){
        confirmTv.setText(confirm);
    }
    public void setGotoAt(String title, View.OnClickListener onClick){
        tvToat.setText(title);
        tvToat.setOnClickListener(onClick);
    }


    private void initView() {
        setContentView(R.layout.dialog_finish_task);
        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.x = CommonUtils.getScreenWidth(mContext)/2;
        p.y = 0;
        getWindow().setAttributes(p);
//        getWindow().setWindowAnimations(R.style.dialogWindowAnim);
        confirmTv.setOnClickListener(this);
    }


    public void show(String title,FinishTaskBean finishTaskBean){
        initData(title,finishTaskBean);
        show();
    }

    private void initData(String title,FinishTaskBean finishTaskBean) {
        titleTv.setText(title);
        rewardsList.clear();
        if (!TextUtils.isEmpty(finishTaskBean.getCash())){
            FinishTaskBean.RewardsBean rewardsBean = new FinishTaskBean.RewardsBean();
            rewardsBean.setIcon(finishTaskBean.getCash_icon());
            rewardsBean.setName("余额");
            rewardsBean.setNum(finishTaskBean.getCash());
            rewardsList.add(rewardsBean);
        }

        if (!TextUtils.isEmpty(finishTaskBean.getFace())){
            FinishTaskBean.RewardsBean rewardsBean = new FinishTaskBean.RewardsBean();
            rewardsBean.setIcon(finishTaskBean.getFace_icon());
            rewardsBean.setName("脸蛋");
            rewardsBean.setNum(finishTaskBean.getFace());
            rewardsList.add(rewardsBean);
        }

        if (!TextUtils.isEmpty(finishTaskBean.getMeal())){
            FinishTaskBean.RewardsBean rewardsBean = new FinishTaskBean.RewardsBean();
            rewardsBean.setIcon(finishTaskBean.getMeal_icon());
            rewardsBean.setName("饭票");
            rewardsBean.setNum(finishTaskBean.getMeal());
            rewardsList.add(rewardsBean);
        }

        if (finishTaskBean.getGift() != null)
            rewardsList.addAll(finishTaskBean.getGift());

        if (!TextUtils.isEmpty(finishTaskBean.getExp()))
        {
            expRl.setVisibility(View.VISIBLE);
            if(rewardsList.size()==0)
            expTitleTv.setText(String.format("恭喜你获得了%s经验",finishTaskBean.getExp()));
            int nowExp = 7;
            int nextExp = 10;
            try
            {
                nowExp = Integer.parseInt(finishTaskBean.getNowExp());
                nextExp = Integer.parseInt(finishTaskBean.getNextExp());
                if (nextExp == 0)
                    nextExp = nowExp;
                Logger.t(TAG).d("nowExp>" + nowExp + "nextExp>" + nextExp);
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
                Logger.t(TAG).d(e.getMessage());
            }
            expPro.setMax(nextExp);
            expPro.setProgress(nowExp);

            String numDes = "经验+" + finishTaskBean.getExp();
            SpannableString spannableString = new SpannableString(numDes);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext,R.color.C0313)),
                    2, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            expNumTv.setText(spannableString);
            final int finalNowExp = nowExp;
            final int finalNextExp = nextExp;
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    LinearLayout.LayoutParams numLayoutParams = (LinearLayout.LayoutParams) expNumTv.getLayoutParams();
                    numLayoutParams.leftMargin = (int) (CommonUtils.dp2px(mContext,160) * ((float) finalNowExp / finalNextExp)) - expNumTv.getMeasuredWidth()/2;
                    if (numLayoutParams.leftMargin < 0)
                        numLayoutParams.leftMargin = 0;
                    Logger.t(TAG).d("" + ((float) finalNowExp / finalNextExp) + "|" + numLayoutParams.leftMargin);
                    expNumTv.setLayoutParams(numLayoutParams);
                }
            },300);
            levelLeftTv.setText(String.format("Lv%s",finishTaskBean.getNowLevel()));
            levelRightTv.setText(String.format("Lv%s",finishTaskBean.getNextLevel()));
            tipTv.setText("获取经验可以提升用户等级哦~");
        }else {
            expRl.setVisibility(View.GONE);
            if (rewardsList.size() > 0)
                tipTv.setText("奖励会直接结算到账户哦~");
        }


        LinearLayout.LayoutParams tipLayoutParams = (LinearLayout.LayoutParams) tipTv.getLayoutParams();
        tipLayoutParams.topMargin = rewardsList.size() == 0 ? 0 :CommonUtils.dp2px(mContext,28);
        tipTv.setLayoutParams(tipLayoutParams);


        if (taskFinishAdapter == null)
        {
            taskFinishAdapter = new TaskFinishAdapter(mContext,rewardsList);
            iconGv.setAdapter(taskFinishAdapter);
        }
        if (rewardsList.size() == 1)
            iconGv.setNumColumns(1);
        else
            iconGv.setNumColumns(2);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iconGv.getLayoutParams();
        if (!TextUtils.isEmpty(finishTaskBean.getExp()) && !"0".equals(finishTaskBean.getExp()) && rewardsList.size() >= 4)
        {
            layoutParams.height = CommonUtils.dp2px(mContext,150);
            tipTv.setText("下滑查看更多奖励哦~");
        }else if (rewardsList.size() >= 6){
            layoutParams.height = CommonUtils.dp2px(mContext,250);
            tipTv.setText("下滑查看更多奖励哦~");
        }else {
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        iconGv.setLayoutParams(layoutParams);
        taskFinishAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
                dismiss();
                if (dialogClickListener != null)
                    dialogClickListener.confirmClick();
                break;
        }
    }

    public interface DialogClickListener {
        void confirmClick();
    }
}
