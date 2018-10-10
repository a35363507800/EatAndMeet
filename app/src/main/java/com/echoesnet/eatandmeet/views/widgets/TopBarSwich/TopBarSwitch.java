package com.echoesnet.eatandmeet.views.widgets.TopBarSwich;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate 2017/2/17
 * @description 通用顶部栏
 */
public class TopBarSwitch extends RelativeLayout
{
    private static final String TAG = TopBarSwitch.class.getSimpleName();

    public static final String NAV_BTN_ICON = "icon";
    public static final String NAV_BTN_NOTE = "note";

    private Context mContext;
    private IconTextView leftBtn, leftBtn2, rightBtn, rightBtn2;
    private RelativeLayout leftR, leftR2, rightR, rightR2;
    private LinearLayout centerContentContainer;//中间内容的容器
    private LinearLayout btnsContainer;//以tab形式显示时，切换按钮的容器
    private float switchBtnsIntervalPx;
    private View wholeContainer;
    private TopbarType mTopbarType;
    private Drawable mBackground;
    private ITopbarSwitchListener mListener;
    private GestureDetector detector;
    private static final TopbarType[] sTopbarTypeArray = {
            TopbarType.SWITCH,
            TopbarType.TEXT,
            TopbarType.CUSTOM
    };

    public TopBarSwitch(Context context)
    {
        this(context, null);
    }

    public TopBarSwitch(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs)
    {
        mContext = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TopBarSwitch, 0, 0);
        switchBtnsIntervalPx = ta.getDimension(R.styleable.TopBarSwitch_switchBtnInterval, CommonUtils.dp2px(mContext, 52));
        final int index = ta.getInt(R.styleable.TopBarSwitch_topbarType, 0);
        if (index >= 0)
        {
            setTopbarType(sTopbarTypeArray[index]);
        }
        mBackground = ta.getDrawable(R.styleable.TabLayout_tabBackground);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        wholeContainer = LayoutInflater.from(getContext()).inflate(R.layout.top_bar_swich, this);
        leftBtn = (IconTextView) wholeContainer.findViewById(R.id.btn_left);
        leftBtn2 = (IconTextView) wholeContainer.findViewById(R.id.btn_left2);
        rightBtn = (IconTextView) wholeContainer.findViewById(R.id.btn_right);
        rightBtn2 = (IconTextView) wholeContainer.findViewById(R.id.btn_right2);

        leftR = (RelativeLayout) wholeContainer.findViewById(R.id.btn_left_c);
        leftR2 = (RelativeLayout) wholeContainer.findViewById(R.id.btn_left2_c);
        rightR = (RelativeLayout) wholeContainer.findViewById(R.id.btn_right_c);
        rightR2 = (RelativeLayout) wholeContainer.findViewById(R.id.btn_right2_c);

        centerContentContainer = (LinearLayout) wholeContainer.findViewById(R.id.arl_center_container);
        if (mBackground != null)
        {
            LinearLayout ll = (LinearLayout) wholeContainer.findViewById(R.id.ll_topbar);
            ll.setBackground(mBackground);
            View vBottomSeparator = wholeContainer.findViewById(R.id.v_bottom_separator);
            vBottomSeparator.setBackground(mBackground);
            invalidate();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && !Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
        {
            wholeContainer.findViewById(R.id.title_color).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.C0321T50));
        }

        if (Build.MANUFACTURER.equalsIgnoreCase("Meitu"))
        {
            wholeContainer.findViewById(R.id.title_color).setBackgroundColor(ContextCompat.getColor(getContext(), R.color.C0321T50));
        }
    }

    /**
     * 设置顶部栏的背景
     *
     * @param drawable 背景图片
     */
    public void setBackground(Drawable drawable)
    {
        LinearLayout ll = (LinearLayout) wholeContainer.findViewById(R.id.ll_topbar);
        ll.setBackground(drawable);
        View vBottomSeparator = wholeContainer.findViewById(R.id.v_bottom_separator);
        vBottomSeparator.setBackground(drawable);
        invalidate();
    }

    /**
     * 设置底部分割线的可见性
     *
     * @param visibility 是否可见
     */
    public void setBottomLineVisibility(int visibility)
    {
        View vBottomSeparator = wholeContainer.findViewById(R.id.v_bottom_separator);
        vBottomSeparator.setVisibility(visibility);
    }

    /**
     * 获取顶部栏中间容器
     *
     * @return 顶部栏中间容器，是一个LinearLayout
     */
    public LinearLayout getCenterContainer()
    {
        return centerContentContainer;
    }

    /**
     * 设置顶部栏的类型
     *
     * @param topbarType  类型枚举
     */
    public void setTopbarType(TopbarType topbarType)
    {
        if (topbarType == null)
        {
            throw new NullPointerException();
        }
        if (mTopbarType != topbarType)
            mTopbarType = topbarType;
    }

    //region SWITCH TYPE

    /**
     * 获得切换类型的顶部栏，按钮名称的字体为16sp, 红点为显示数字的红点（大红点）
     *
     * @param switchBtnList        切换按钮列表
     * @param defaultSelectedIndex 默认选择的切换按钮
     * @param listener             顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     */
    public void inflateSwitchBtns(List<String> switchBtnList, int defaultSelectedIndex, final ITopbarSwitchListener listener)
    {
        inflateSwitchBtns(switchBtnList, 16, defaultSelectedIndex, listener);
    }


    /**
     * 获得切换类型的顶部栏，按钮名称的字体为16sp,
     * @param switchBtnList         切换按钮列表
     * @param defaultSelectedIndex  默认选择的button
     * @param withNum               消息提示红点是否是有数字的
     * @param listener              顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     */
    public void inflateSwitchBtns(List<String> switchBtnList, int defaultSelectedIndex, boolean withNum, ITopbarSwitchListener listener)
    {
        inflateSwitchBtns(switchBtnList, 16, withNum, defaultSelectedIndex, listener);
    }


    /**
     * 获得切换类型的顶部栏，红点为显示数字的红点
     * @param switchBtnList         切换按钮列表
     * @param btnTextSize           切换按钮上字体尺寸单位sp
     * @param defaultSelectedIndex  默认选中的按钮
     * @param listener              顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     */
    public void inflateSwitchBtns(List<String> switchBtnList, float btnTextSize, int defaultSelectedIndex, final ITopbarSwitchListener listener)
    {
        inflateSwitchBtns(switchBtnList, btnTextSize, true, defaultSelectedIndex, listener);
    }

    /**
     * 获得切换类型的顶部栏,参数最全的函数，其他的都是此函数的重载
     *
     * @param switchBtnList        切换按钮列表
     * @param defaultSelectedIndex 默认选择的button
     * @param listener             顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     * @param btnTextSize          字体大小单位sp
     * @param withNum              是否是显示数字的红点
     */
    public void inflateSwitchBtns(List<String> switchBtnList, float btnTextSize, boolean withNum, int defaultSelectedIndex, final ITopbarSwitchListener listener)
    {
        if (mTopbarType != TopbarType.SWITCH)
            throw new UnsupportedOperationException("在为非Switch模式下不支持此操作,请设置Topbar的类型为TopbarType.SWITCH");
        this.mListener = listener;
        float offset = 16.0f;
        if (withNum == false)
            offset = 6.0f;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        btnsContainer = (LinearLayout) inflater.inflate(R.layout.top_bar_switch_ll, null);
        for (int i = 0; i < switchBtnList.size(); i++)
        {
            RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.switch_btn_textview, null);

            TextView tv = (TextView) rl.findViewById(R.id.tv_1);
            TextView tvB = (TextView) rl.findViewById(R.id.tv_bottom);
            tv.setText(switchBtnList.get(i));
            tv.setTextSize(btnTextSize);

            if (i == 0)
            {
                LinearLayout.LayoutParams llpInter = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                llpInter.setMargins(CommonUtils.dp2px(mContext, offset), 0, 0, 0);//为了使切换tab整体居中。
                btnsContainer.addView(rl, llpInter);
            }
            else
            {
                LinearLayout.LayoutParams llpInter = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                Logger.t(TAG).d("间隔》" + switchBtnsIntervalPx);
                llpInter.setMargins((int) (switchBtnsIntervalPx - CommonUtils.dp2px(mContext, offset)), 0, 0, 0);//由于红点的长度会变化，这个写法有一定的误差
                btnsContainer.addView(rl, llpInter);
            }

            if (i == defaultSelectedIndex)
            {
                tv.setTypeface(null, Typeface.BOLD);
                tv.setSelected(true);
                tvB.setSelected(true);
            }
            else
            {
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setSelected(false);
                tvB.setSelected(false);
            }
        }
        LinearLayout.LayoutParams llpOuter = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CommonUtils.dp2px(mContext, 35));
        centerContentContainer.addView(btnsContainer, llpOuter);
        setNavBtnsListener(listener);

        for (int i = 0; i < btnsContainer.getChildCount(); i++)
        {
            TextView view = (TextView) btnsContainer.getChildAt(i).findViewById(R.id.tv_1);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    changeSwitchBtn(finalI);
                    listener.switchBtn(v, finalI);
                }
            });
        }
    }

    /**
     * 获得切换类型的顶部栏
     *
     * @param switchBtnList        切换按钮列表
     * @param btnTextSize          tab显示字体的大小 (sp)
     * @param withNumTapIndexArray 指示tab上红点是否有数字的数组，1：表示有数字 0：表示没有，如果传入null 表示所有tab的红点都是没有数字的红点
     * @param defaultSelectedIndex 默认选择的切换按钮
     * @param listener             顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     */
    public void inflateSwitchBtns(List<String> switchBtnList, float btnTextSize, @Nullable int[] withNumTapIndexArray, int defaultSelectedIndex, final ITopbarSwitchListener listener)
    {
        if (mTopbarType != TopbarType.SWITCH)
            throw new UnsupportedOperationException("在为非Switch模式下不支持此操作,请设置Topbar的类型为TopbarType.SWITCH");
        if (switchBtnList == null)
            throw new NullPointerException("tab 列表不能为空");
        if (withNumTapIndexArray == null)
        {
            withNumTapIndexArray = new int[switchBtnList.size()];
        }
        if (switchBtnList.size() != withNumTapIndexArray.length)
        {
            throw new IllegalArgumentException("withNumTapIndexArray 与switchBtnList长度应该相等，数值是0和1，1：表示有数字的红点，0表示无数字的红点");
        }
        float offset = 12.0f;//默认无数字
        this.mListener = listener;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        btnsContainer = (LinearLayout) inflater.inflate(R.layout.top_bar_switch_ll, null);
        for (int i = 0; i < switchBtnList.size(); i++)
        {
            RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.switch_btn_textview, null);

            TextView tv = (TextView) rl.findViewById(R.id.tv_1);
            TextView tvB = (TextView) rl.findViewById(R.id.tv_bottom);
            tv.setText(switchBtnList.get(i));
            tv.setTextSize(btnTextSize);

            if (withNumTapIndexArray[i] == 0)
            {
                offset = 18.0f;
            }
            else if (withNumTapIndexArray[i] == 1)
            {
                offset = 25.0f;
            }
            if (i == 0)
            {
                LinearLayout.LayoutParams llpInter = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                llpInter.setMargins(CommonUtils.dp2px(mContext, offset), 0, 0, 0);//为了使切换tab整体居中。
                btnsContainer.addView(rl, llpInter);
            }
            else
            {
                LinearLayout.LayoutParams llpInter = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                Logger.t(TAG).d("间隔》" + switchBtnsIntervalPx);
                llpInter.setMargins((int) (switchBtnsIntervalPx - CommonUtils.dp2px(mContext, i == 1 ? offset - 10 : offset)), 0, 0, 0);//由于红点的长度会变化，这个写法有一定的误差
                btnsContainer.addView(rl, llpInter);
            }

            if (i == defaultSelectedIndex)
            {
                tv.setTypeface(null, Typeface.BOLD);
                tv.setSelected(true);
                tvB.setSelected(true);
            }
            else
            {
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setSelected(false);
                tvB.setSelected(false);
            }
        }
        LinearLayout.LayoutParams llpOuter = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CommonUtils.dp2px(mContext, 35));
        centerContentContainer.addView(btnsContainer, llpOuter);
        setNavBtnsListener(listener);

        for (int i = 0; i < btnsContainer.getChildCount(); i++)
        {
            TextView view = (TextView) btnsContainer.getChildAt(i).findViewById(R.id.tv_1);
            final int finalI = i;
            view.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    changeSwitchBtn(finalI);
                    listener.switchBtn(v, finalI);
                }
            });
        }
    }

    /**
     * 切换按钮
     *
     * @param position 要切换按钮的index
     */
    public void changeSwitchBtn(int position)
    {
        if (mTopbarType != TopbarType.SWITCH)
            throw new UnsupportedOperationException("在非Switch模式下不支持此操作,请设置Topbar的类型为TopbarType.SWITCH");
        if (position >= btnsContainer.getChildCount())
            throw new IllegalArgumentException("位置超出按钮个数");
        for (int j = 0; j < btnsContainer.getChildCount(); j++)
        {
            TextView tv = (TextView) btnsContainer.getChildAt(j).findViewById(R.id.tv_1);
            if (position == j)
            {
                tv.setTypeface(null, Typeface.BOLD);
                tv.setSelected(true);
                btnsContainer.getChildAt(j).findViewById(R.id.tv_bottom).setSelected(true);
/*                btnsContainer.getChildAt(j).findViewById(R.id.tv_msg_indicator).setVisibility(View.INVISIBLE);
                btnsContainer.getChildAt(j).findViewById(R.id.tv_msg_indicator2).setVisibility(View.INVISIBLE);*/
            }
            else
            {
                tv.setTypeface(null, Typeface.NORMAL);
                btnsContainer.getChildAt(j).findViewById(R.id.tv_1).setSelected(false);
                btnsContainer.getChildAt(j).findViewById(R.id.tv_bottom).setSelected(false);
            }
        }
        hindMsgIndicator(position);
    }

    /**
     * 显示切换按钮右上角的消息提示，此消息没有数字，只是红点
     *
     * @param btnIndex 要显示提示的按钮的index
     */
    public void showMsgIndicator(int btnIndex)
    {
        if (mTopbarType != TopbarType.SWITCH)
            throw new UnsupportedOperationException("在非Switch模式下不支持此操作,请设置Topbar的类型为TopbarType.SWITCH");
        for (int i = 0; i < btnsContainer.getChildCount(); i++)
        {
            //TextView view = (TextView) btnsContainer.getChildAt(i).findViewById(R.id.tv_msg_indicator);
            TextView view2 = (TextView) btnsContainer.getChildAt(i).findViewById(R.id.tv_msg_indicator2);
            if (i == btnIndex)
            {
//                if (isWithNumber)
//                    view.setVisibility(View.VISIBLE);
//                else
                view2.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 显示切换按钮右上角的消息提示,此消息红点有数字
     *
     * @param btnIndex  切换按钮的index
     * @param showCount 红点上要显示的内容
     */
    public void showMsgIndicator(int btnIndex, @NonNull String showCount)
    {
        if (mTopbarType != TopbarType.SWITCH)
            throw new UnsupportedOperationException("在非Switch模式下不支持此操作,请设置Topbar的类型为TopbarType.SWITCH");
        for (int i = 0; i < btnsContainer.getChildCount(); i++)
        {
            RelativeLayout switchBtn = (RelativeLayout) btnsContainer.getChildAt(i);
            TextView view = (TextView) switchBtn.findViewById(R.id.tv_msg_indicator);
            //TextView view2 = (TextView) btnsContainer.getChildAt(i).findViewById(R.id.tv_msg_indicator2);
            if (i == btnIndex)
            {
                //if (isWithNumber)
                {
                    view.setVisibility(View.VISIBLE);
                    view.setText(showCount);
                }
//                else
//                    view2.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 隐藏切换按钮右上角的消息提示红点
     * @param btnIndex 要隐藏的按钮index
     */
    public void hindMsgIndicator(int btnIndex)
    {
        if (mTopbarType != TopbarType.SWITCH)
            throw new UnsupportedOperationException("在非Switch模式下不支持此操作,请设置Topbar的类型为TopbarType.SWITCH");
        if (btnIndex >= btnsContainer.getChildCount())
            throw new IllegalArgumentException("位置超出按钮个数");
        for (int j = 0; j < btnsContainer.getChildCount(); j++)
        {
            if (btnIndex == j)
            {
                TextView tvRedNoteWithNum = (TextView) btnsContainer.getChildAt(j).findViewById(R.id.tv_msg_indicator);
                TextView tvRedNoteWithoutNum = (TextView) btnsContainer.getChildAt(j).findViewById(R.id.tv_msg_indicator2);
                if (tvRedNoteWithNum.getVisibility() == View.VISIBLE)
                {
                    tvRedNoteWithNum.setVisibility(View.INVISIBLE);
                    if (mListener != null)
                        mListener.refreshPage(j);
                }
                if (tvRedNoteWithoutNum.getVisibility() == View.VISIBLE)
                {
                    tvRedNoteWithoutNum.setVisibility(View.INVISIBLE);
                    if (mListener != null)
                        mListener.refreshPage(j);
                }
                break;
            }
        }
    }
    //endregion

    //region CUSTOM TYPE

    /**
     * 自定义模式下构建顶部栏中间的显示View
     *
     * @param layoutId     中间布局的layout id
     * @param listener     顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     * @return             中间的view
     */
    public View inflateCustomCenter(int layoutId, final ITopbarSwitchListener listener)
    {
        if (mTopbarType != TopbarType.CUSTOM)
            throw new UnsupportedOperationException("在为非CUSTOM模式下不支持此操作,请设置Topbar的类型为TopbarType.CUSTOM");

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(layoutId, null);
        LinearLayout.LayoutParams llpOuter = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        centerContentContainer.addView(v, llpOuter);
        setNavBtnsListener(listener);
        return v;
    }
    //endregion

    //region TEXT TYPE

    /**
     * TEXT模式下构建中间展示的 textview
     * @param listener   顶部栏监听，负责监听切换按钮的一些事件，例如点击，切换等
     * @return   中间展示TextView
     */
    public TextView inflateTextCenter(final ITopbarSwitchListener listener)
    {
        if (mTopbarType != TopbarType.TEXT)
            throw new UnsupportedOperationException("在为非TEXT模式下不支持此操作,请设置Topbar的类型为TopbarType.TEXT");
        TextView showText = new TextView(mContext);
        showText.setTextSize(18);
        showText.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        showText.setMaxWidth(CommonUtils.dp2px(mContext, 250));
        showText.setEllipsize(TextUtils.TruncateAt.END);
        showText.setLines(1);
        LinearLayout.LayoutParams llpOuter = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llpOuter.gravity = Gravity.CENTER_VERTICAL;
        centerContentContainer.addView(showText, llpOuter);
        setNavBtnsListener(listener);
        return showText;
    }
    //endregion

    /**
     * 从左向右获取控件上的导航按钮（左边两个，右边两个）
     * 已经废弃，请使用{@link #getNavBtns2(int[])} 替代
     *
     * @param witchBtn 传入长度为4，元素为0，1的数组。例如[1,1,0,1] 表示获取 左1 左2 右2 按钮
     * @return 导航按钮集合
     * @throws IllegalArgumentException 参数不合法
     */
    @Deprecated
    public List<TextView> getNavBtns(int[] witchBtn)
    {
        List<TextView> btns = new ArrayList<>();
        btns.add(leftBtn);
        btns.add(leftBtn2);
        btns.add(rightBtn);
        btns.add(rightBtn2);

        List<TextView> resultLst = new ArrayList<>();
        if (witchBtn == null)
        {
            throw new NullPointerException();
        }
        else
        {
            if (witchBtn.length > btns.size())
                throw new IllegalArgumentException("你应该传入一个长度为4元素为0 1 的整型数组");

            for (int i = 0; i < witchBtn.length; i++)
            {
                TextView tv = btns.get(i);
                if (witchBtn[i] == 1)
                {
                    tv.setVisibility(VISIBLE);
                    resultLst.add(btns.get(i));
                }
                else if (witchBtn[i] == 0)
                {
                    tv.setVisibility(GONE);
                }
                else
                    throw new IllegalArgumentException("骚年，好好传参，此数组只能包含0和1");
            }
        }
        return resultLst;
    }

    /**
     * 从左向右获取控件上的导航按钮（左边两个，右边两个）
     *
     * @param witchBtn 传入长度为4，元素为0，1的数组。例如[1,1,0,1] 表示获取 左1 左2 右2 按钮
     * @return 包含图标和通知红点的按钮集合, 默认各个按钮右上角的红点是隐藏的，图标是显示的，如果需要使红点可见则需要在代码中设置
     * @throws IllegalArgumentException 参数不合法
     */
    public List<Map<String, TextView>> getNavBtns2(int[] witchBtn)
    {
        List<RelativeLayout> btns = new ArrayList<>();
        btns.add(leftR);
        btns.add(leftR2);
        btns.add(rightR);
        btns.add(rightR2);

        List<Map<String, TextView>> resultLst = new ArrayList<>();
        if (witchBtn == null)
        {
            throw new NullPointerException();
        }
        else
        {
            if (witchBtn.length > btns.size())
                throw new IllegalArgumentException("你应该传入一个长度为4元素为0 1 的整型数组");

            for (int i = 0; i < witchBtn.length; i++)
            {
                RelativeLayout tv = btns.get(i);
                if (witchBtn[i] == 1)
                {
                    tv.setVisibility(VISIBLE);
                    tv.getChildAt(0).setVisibility(VISIBLE);
                    Map<String, TextView> map = new HashMap<>();
                    map.put(NAV_BTN_ICON, (TextView) tv.getChildAt(0));
                    map.put(NAV_BTN_NOTE, (TextView) tv.getChildAt(1));
                    resultLst.add(map);
                }
                else if (witchBtn[i] == 0)
                {
                    tv.setVisibility(GONE);
                }
                else
                    throw new IllegalArgumentException("骚年，好好传参，此数组只能包含0和1");
            }
        }
        return resultLst;
    }

    private void setNavBtnsListener(final ITopbarSwitchListener listener)
    {
        if (listener != null)
        {
            leftR.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.leftClick(v);
                }
            });
            leftR2.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.left2Click(v);
                }
            });
            rightR.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.rightClick(v);
                }
            });
            rightR2.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    listener.right2Click(v);
                }
            });
            wholeContainer.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Logger.t(TAG).d("----->topClick");
                }
            });
            wholeContainer.setOnTouchListener(new OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (detector == null)
                    {
                        detector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener()
                        {
                            @Override
                            public boolean onDoubleTap(MotionEvent e)
                            {
                                listener.topDoubleClick(v);
                                return super.onDoubleTap(e);
                            }
                        });
                    }
                    return detector.onTouchEvent(event);
                }
            });
        }
    }

    public enum TopbarType
    {
        /**
         * From XML, use this syntax: <code>android:TopbarType="SWITCH"</code>.
         */
        SWITCH(0),
        TEXT(1),
        CUSTOM(2);

        TopbarType(int value)
        {
            nativeInt = value;
        }

        final int nativeInt;
    }
}
