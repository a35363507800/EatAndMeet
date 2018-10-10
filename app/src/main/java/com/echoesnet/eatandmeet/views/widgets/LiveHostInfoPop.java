package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.activities.ReportFoulsUserAct;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.LookAnchorBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.CircleTextView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

/**
 * Created by lc on 2017/7/14 10.
 */

public class LiveHostInfoPop extends PopupWindow
{
    private static final String TAG = LiveHostInfoPop.class.getSimpleName();
    private LiveHostInfoPop mLiveHostInfoPop;
    private Activity mActivity;
    private LookAnchorBean bean;
    private String uId;
    private OnFocusItemClickListener mOnFocusItemClickListener;
    private OnHelloItemClickListener mOnHelloItemClickListener;
    private OnYuePaoItemClickListener mOnYuePaoItemClickListener;
    private TextView tvReport;
    private IconTextView itvClose;
    private TextView tvHostNickName;
    private GenderView itvSexAge;
    private TextView tvHostId;
    private IconTextView itvPersonIdenty;
    private TextView tvAdminLevel;
    private TextView tvIdentyWord;
    private TextView tvPhoneWord;
    private TextView tvHostLevel;
    private TextView tvAddFocusHost;
    private TextView tvHello;
    private TextView tvYue;
    private TextView tvFanPiao;
    private TextView tvFans;
    private TextView tvFanFocus;
    private LevelHeaderView lhvHeadPic;
    private String roomId;
    private View hideView;
    private CircleTextView ctvCicle;
    private ImageView ivLevelPerson;
    private ImageView ivHostLevel;
    private RelativeLayout rlBgWhite;
    private RelativeLayout rlAllBg;

    public LiveHostInfoPop(Activity mActivity, LookAnchorBean bean, String uId, String roomId)
    {
        super(mActivity);
        this.mActivity = mActivity;
        this.bean = bean;
        this.roomId = roomId;
        this.uId = uId;
        initPopWindow();
    }

    private void initPopWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.pop_live_host_info, null);
//        mLiveHostInfoPop = new LiveHostInfoPop(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mActivity).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 刷新状态
        this.update();
        //  this.setAnimationStyle(R.style.PopupAnimation);
        this.getContentView().setFocusableInTouchMode(true);
        this.getContentView().setFocusable(true);

        tvReport = (TextView) contentView.findViewById(R.id.tv_report);
        itvClose = (IconTextView) contentView.findViewById(R.id.itv_close);
        tvHostNickName = (TextView) contentView.findViewById(R.id.tv_host_nickName);
        itvSexAge = (GenderView) contentView.findViewById(R.id.itv_sex_age);
        tvHostId = (TextView) contentView.findViewById(R.id.tv_host_id);
        itvPersonIdenty = (IconTextView) contentView.findViewById(R.id.itv_person_identy);
        tvIdentyWord = (TextView) contentView.findViewById(R.id.tv_identy_word);
        tvPhoneWord = (TextView) contentView.findViewById(R.id.tv_phone_word);
        tvYue = (TextView) contentView.findViewById(R.id.tv_yue);
        tvHello = (TextView) contentView.findViewById(R.id.tv_hello);
        tvAddFocusHost = (TextView) contentView.findViewById(R.id.tv_add_focus_host);
        lhvHeadPic = (LevelHeaderView) contentView.findViewById(R.id.lhv_head_pic);

        ctvCicle = (CircleTextView) contentView.findViewById(R.id.ctv_cicle);

        tvFanPiao = (TextView) contentView.findViewById(R.id.tv_fan_piao);
        tvFans = (TextView) contentView.findViewById(R.id.tv_fans);
        tvFanFocus = (TextView) contentView.findViewById(R.id.tv_fan_focus);

        ivLevelPerson = (ImageView) contentView.findViewById(R.id.iv_level_person);
        ivHostLevel = (ImageView) contentView.findViewById(R.id.iv_host_level);

        rlBgWhite = (RelativeLayout) contentView.findViewById(R.id.rl_bg_white);

        rlAllBg = (RelativeLayout) contentView.findViewById(R.id.rl_all_bg);

        int meal = Integer.parseInt(bean.getMeal());
        Logger.t(TAG).d("bean>>"+bean.toString());

        ctvCicle.setBackgroundColor(Color.WHITE);
        if (meal > 10000)
        {
            String mealNum = division(meal);
            tvFanPiao.setText("饭票 " + mealNum + "万");
        } else
        {
            tvFanPiao.setText("饭票 " + meal);
        }

        int fans = Integer.parseInt(bean.getFansNum());
        if (fans > 10000)
        {
            String fansNum = division(fans);
            tvFans.setText("粉丝 " + fansNum + "万");
        } else
        {
            tvFans.setText("粉丝 " + fans);
        }

        int focus = Integer.parseInt(bean.getFocusNum());
        if (focus > 10000)
        {
            String focusNum = division(focus);
            tvFanFocus.setText("关注 " + focusNum + "万");
        } else
        {
            tvFanFocus.setText("关注 " + focus);
        }

        if ("0".equals(bean.getInWish()))
        {
            tvYue.setText("+ 约会");
            tvYue.setEnabled(true);
            tvYue.setTag("true");
            tvYue.setBackgroundResource(R.drawable.shape_pay_sure_press);
        } else
        {
            tvYue.setText("已添加");
            tvYue.setEnabled(false);
            tvYue.setTag("false");
            tvYue.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        }
        if ("0".equals(bean.getFocus()))
        {
            tvHello.setText("打招呼");
            tvHello.setVisibility(View.VISIBLE);
        } else
        {
            tvHello.setText("发消息");
            tvHello.setVisibility(View.VISIBLE);
        }


//------------------------------------------------等级中标规范start--------------------------------------------------
        int level = 0;
        try
        {
            level = Integer.parseInt(bean.getLevel());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        int hostLevel = 0;
        try
        {
            hostLevel = Integer.parseInt(bean.getAnchorLevel());
        } catch (NumberFormatException e)
        {
            e.printStackTrace();
        }

        if (level == 0)
        {
            ivLevelPerson.setImageResource(R.drawable.lv00_xxhdpi);
        } else
        {
        ivLevelPerson.setImageResource(LevelView.getLevelMiddleImage(level));
        }

        if (hostLevel == 0)
        {
            ivHostLevel.setImageResource(R.drawable.old_zblv00_xxhdpi);
        } else
        {
        ivHostLevel.setImageResource(LevelView.getLevelMiddleImageHost(hostLevel));
        }

//------------------------------------------------等级中标规范end--------------------------------------------------
//
//        int anchorLevel = 0;
//        try
//        {
//            anchorLevel = Integer.parseInt(bean.getAnchorLevel());
//        } catch (NumberFormatException e)
//        {
//            e.printStackTrace();
//        }
//
//        int level = 0;
//        try
//        {
//            level = Integer.parseInt(bean.getLevel());
//        } catch (NumberFormatException e)
//        {
//            e.printStackTrace();
//        }
//
//
//        tvHostLevel.setText(bean.getAnchorLevel());
//        tvAdminLevel.setText(bean.getLevel());
//
//
//        if(level==0||level>9)
//        {
//
//            tvHostLevel.setText(""+anchorLevel);
//        }
//        else
//        {
//            tvHostLevel.setText("0"+anchorLevel);
//        }
//
//
//        if(level==0||level>9)
//        {
//            tvAdminLevel.setText(""+level);
//        }
//        else
//        {
//            tvAdminLevel.setText("0"+level);
//        }
//        rlHostLevel.setBackgroundResource(getLevelBgID(anchorLevel%21));
//        rlUserLevel.setBackgroundResource(getLevelBgID(level%21));
//        itvHostIcon.setText(LevelView.getIcon(anchorLevel));



        if ("0".equals(bean.getFocus()))
        {
            tvAddFocusHost.setText("+ 关注");
            tvAddFocusHost.setEnabled(true);
            tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_press);
        } else if ("1".equals(bean.getFocus()))
        {
            tvAddFocusHost.setText("已关注");
            tvAddFocusHost.setEnabled(false);
            tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_normal);
        }

        if ("1".equals(bean.getRmFlg()))
        {
            tvIdentyWord.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
            itvPersonIdenty.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
            tvIdentyWord.setText("身份已认证");
        } else
        {
            tvIdentyWord.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
            tvIdentyWord.setText("身份未认证");
            itvPersonIdenty.setTextColor(ContextCompat.getColor(mActivity, R.color.C0323));
        }
        tvHostNickName.setText(TextUtils.isEmpty(bean.getRemark()) ? bean.getNicName() : bean.getRemark());

        itvSexAge.setSex(bean.getAge(),bean.getSex());

        tvHostId.setText("ID: " + bean.getId());

        lhvHeadPic.setHeadImageByUrl(bean.getUphUrl());
        lhvHeadPic.showRightIcon(bean.getIsVuser());

        tvYue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnYuePaoItemClickListener != null)
                    mOnYuePaoItemClickListener.onYuePaoClick();
            }
        });
        tvAddFocusHost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnFocusItemClickListener != null)
                    mOnFocusItemClickListener.onFocusClick();
            }
        });
        tvHello.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mOnHelloItemClickListener != null)
                    mOnHelloItemClickListener.onHelloClick();
            }
        });


        tvReport.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent reportIntent = new Intent(mActivity, ReportFoulsUserAct.class);
                reportIntent.putExtra("luId", bean.getuId());
                mActivity.startActivity(reportIntent);
            }
        });
        itvClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (this != null)
                {
                    dismiss();
                }
            }
        });

        lhvHeadPic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (EamApplication.getInstance().controlUInfo.size() == 2)
                {
                    if (EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()) != null)
                    {
                        EamApplication.getInstance().controlUInfo.get(CNewUserInfoAct.class.getSimpleName()).finish();
                        EamApplication.getInstance().controlUInfo.clear();
                    }
                }
                Intent intent = new Intent(mActivity, CNewUserInfoAct.class);
                intent.putExtra("toUId", uId);
                intent.putExtra("chatRoomId",roomId);
                mActivity.startActivity(intent);
            }
        });
        contentView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if (this != null)
                    {
                        dismiss();
                        mLiveHostInfoPop = null;
                    }
                }
                return false;
            }
        });
    }

    public void showPopupWindow(View parent, View hideView)
    {
        if (!this.isShowing())
        {
            if (hideView == null)
            {
                backgroundAlpha(0.5f);
            } else
            {
                this.hideView = hideView;
                hideView.setVisibility(View.VISIBLE);

            }
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
        } else
        {
            this.dismiss();
        }
    }

    @Override
    public void dismiss()
    {
        if (hideView == null)
        {
            backgroundAlpha(1.0f);
        } else
        {
            hideView.setVisibility(View.GONE);
        }
        super.dismiss();
    }

    /**
     * 隐藏三个按钮
     */
    public void hideSelfState()
    {
        tvYue.setVisibility(View.GONE);
        tvHello.setVisibility(View.GONE);
        tvAddFocusHost.setVisibility(View.GONE);
        //不要举报自己
        tvReport.setVisibility(View.GONE);

        //改变弹窗的高度，减去下面三个按钮的高度，重新布局
        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) rlAllBg.getLayoutParams();
        params2.height = CommonUtils.dp2px(mActivity, 260);
        rlAllBg.setLayoutParams(params2);

        //改变弹窗的高度，减去下面三个按钮的高度，重新布局
        RelativeLayout.LayoutParams params1 = (RelativeLayout.LayoutParams) rlBgWhite.getLayoutParams();
        params1.height = CommonUtils.dp2px(mActivity, 220);
        rlBgWhite.setLayoutParams(params1);

    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mActivity.getWindow().setAttributes(lp);
    }


    public void setOnFocusItemClickListener(OnFocusItemClickListener OnFocusItemClickListener)
    {
        this.mOnFocusItemClickListener = OnFocusItemClickListener;
    }


    public void setOnHelloItemClickListener(OnHelloItemClickListener onHelloItemClickListener)
    {
        this.mOnHelloItemClickListener = onHelloItemClickListener;
    }


    public void setOnYuePaoItemClickListener(OnYuePaoItemClickListener onYuePaoItemClickListener)
    {
        this.mOnYuePaoItemClickListener = onYuePaoItemClickListener;
    }


    public interface OnFocusItemClickListener
    {
        void onFocusClick();
    }


    public interface OnHelloItemClickListener
    {
        void onHelloClick();
    }


    public interface OnYuePaoItemClickListener
    {
        void onYuePaoClick();
    }


    //关注主播
    public void focusAuthorSuccess()
    {
        tvAddFocusHost.setText("已关注");
        tvAddFocusHost.setEnabled(false);
        tvHello.setText("发消息");
        bean.setIsSayHello("0");
        tvAddFocusHost.setBackgroundResource(R.drawable.shape_pay_sure_normal);
    }

    //约会主播
    public void yueAuthorSuccess()
    {
        tvYue.setText("已添加");
        tvYue.setEnabled(false);
        tvYue.setBackgroundResource(R.drawable.shape_pay_sure_normal);
    }

    public boolean dismissShareWin()
    {
        boolean isShowing = true;
        if (mLiveHostInfoPop != null && mLiveHostInfoPop.isShowing())
        {
            mLiveHostInfoPop.dismiss();
            isShowing = false;
        }
        return isShowing;
    }

    public void killShareWin()
    {
        if (mLiveHostInfoPop != null)
        {
            mLiveHostInfoPop.dismiss();
            mLiveHostInfoPop = null;
        }
    }
    //整数相除 保留一位小数
    public static String division(int a)
    {
        a = (a / 1000);
        float num = (float) a / 10;
        return num + "";
    }

}
