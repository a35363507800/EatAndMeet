package com.echoesnet.eatandmeet.views;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.AreaBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomFlowGroup;
import com.echoesnet.eatandmeet.views.widgets.CustomFlowView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;

import java.util.HashMap;
import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/8/15
 * @description
 */

public class ReservationPopupWindow extends PopupWindow
{
    private static final String TAG = ReservationPopupWindow.class.getSimpleName();
    private View mView;
    private CustomFlowGroup flowAreaGroup, flowBusinessAreaGroup, flowFoodGroup, flowFilterGroup;
    private LinearLayout llFoodAll,llAll;
    private TextView tvBusinessTitle;
    private Button btnQuery;
    private Context context;
    private int position;
    private View vEmpty;
    private LinearLayout llAllview;
    private String areaParamResult = "", businessParamResult = "", classParamResult = "", filterParamResult = "";
    private String businessParamBefore = "", classParamBefore = "", filterParamBefore = "";

    public ReservationPopupWindow(final Context context,
                                  final List<String> areaList, final String areaParam,
                                  final List<AreaBean> areaBeanList, final String businessParam,
                                  final List<String> sortList, final String classParam,
                                  final List<String> filterList, final String filterParam, final int index)
    {
        this.context = context;
        this.position = index;
        this.areaParamResult = areaParam;
        this.businessParamResult = businessParam;
        this.classParamResult = classParam;
        this.filterParamResult = filterParam;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.popup_restaurant_options, null);
        this.setContentView(mView);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0000000000);
        this.setBackgroundDrawable(dw);
        this.setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss()
            {

            }
        });
        flowAreaGroup = (CustomFlowGroup) mView.findViewById(R.id.cfg_area);
        flowBusinessAreaGroup = (CustomFlowGroup) mView.findViewById(R.id.cfg_business_area);
        flowFoodGroup = (CustomFlowGroup) mView.findViewById(R.id.cfg_food);
        flowFilterGroup = (CustomFlowGroup) mView.findViewById(R.id.cfg_filter);
        tvBusinessTitle = (TextView) mView.findViewById(R.id.tv_business_title);

        llAllview = (LinearLayout) mView.findViewById(R.id.ll_allview);
        llFoodAll = (LinearLayout)mView.findViewById(R.id.ll_food_all);
        vEmpty = (View)mView.findViewById(R.id.v_empty);
        llAll = (LinearLayout)mView.findViewById(R.id.ll_all);


        vEmpty.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
        // 区域
        flowAreaGroup.setData(areaList, context, areaParam, new CustomFlowGroup.ViewOnclickListener()
        {
            @Override
            public void onClickCallback(View v)
            {
                if (!((CustomFlowView) v).isSelected)
                {
                    setTextStyle(flowAreaGroup, v);
                    areaParamResult = ((CustomFlowView) v).getText().toString();
                    for (int m = 0; m < areaBeanList.size(); m++)
                    {
                        if (areaBeanList.get(m).getPart().equals(areaParamResult))
                        {
                            position = m;
                            resetBusinessData(position, areaBeanList);
                        }
                    }
                } else
                {
                    ((CustomFlowView) v).setTextColor(ContextCompat.getColor(context, R.color.C0323));
                    v.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                    v.setSelected(false);
                    areaParamResult = "";
                    position = 0;
                    resetBusinessData(position, areaBeanList);
                }
            }
        });

        // 默认商圈
        flowBusinessAreaGroup.setData(areaBeanList.get(position).getTrades(), context, businessParam, new CustomFlowGroup.ViewOnclickListener()
        {
            @Override
            public void onClickCallback(View v)
            {
                if (!((CustomFlowView) v).isSelected)
                {
                    setTextStyle(flowBusinessAreaGroup, v);
                    businessParamResult = ((CustomFlowView) v).getText().toString();
                } else
                {
                    ((CustomFlowView) v).setTextColor(ContextCompat.getColor(context, R.color.C0323));
                    v.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                    v.setSelected(false);
                    businessParamResult = "";
                }
            }
        });

        // 美食
        flowFoodGroup.setData(sortList, context, classParam, new CustomFlowGroup.ViewOnclickListener()
        {
            @Override
            public void onClickCallback(View v)
            {
                if (!((CustomFlowView) v).isSelected)
                {
                    setTextStyle(flowFoodGroup, v);
                    classParamBefore = ((CustomFlowView) v).getText().toString();
                    classParamResult = classParamBefore;
                } else
                {
                    ((CustomFlowView) v).setTextColor(ContextCompat.getColor(context, R.color.C0323));
                    v.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                    v.setSelected(false);
                    classParamResult = "";
                }
            }
        });


        // 筛选
        flowFilterGroup.setData1(filterList, context, filterParam, new CustomFlowGroup.ViewOnclickListener()
        {
            @Override
            public void onClickCallback(View v)
            {
                if (!((CustomFlowView) v).isSelected)
                {
                    setTextStyle(flowFilterGroup, v);
                    filterParamBefore = ((CustomFlowView) v).getText().toString();
                    if (filterParamBefore.equals("离我最近"))
                    {
                        filterParamResult = "1";
                    } else if (filterParamBefore.equals("评价最好"))
                    {
                        filterParamResult = "2";
                    } else if (filterParamBefore.equals("价格最低"))
                    {
                        filterParamResult = "3";
                    } else if (filterParamBefore.equals("价格最高"))
                    {
                        filterParamResult = "4";
                    } else
                    {
                        filterParamResult = "";
                    }
                } else
                {
                    ((CustomFlowView) v).setTextColor(ContextCompat.getColor(context, R.color.C0323));
                    v.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                    v.setSelected(false);
                    filterParamResult = "";
                    filterParamBefore = "";
                }
            }
        });

        btnQuery = (Button) mView.findViewById(R.id.btn_query);
        btnQuery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HashMap<String, Object> paramMap = new HashMap<>();
                paramMap.put("businessParam", businessParamResult);
                paramMap.put("classParam", classParamResult);
                paramMap.put("filterParam", filterParamResult);
                paramMap.put("areaParam", areaParamResult);
                paramMap.put("position", position);
                if (buttonClickListener != null)
                {
                    buttonClickListener.queryCallback(paramMap);
                    dismiss();
                }
            }
        });
    }
    public void setEmptyHeight(int emptyHeight)
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,emptyHeight);
        if (vEmpty!=null)
        vEmpty.setLayoutParams(params);
    }
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            this.showAsDropDown(parent, 0, 2);
          //  this.showAtLocation(parent, Gravity.TOP | Gravity.START, 0, 0);
        } else
        {
            this.dismiss();
        }
    }
    public void setGoodFoodHide()
    {
        if (llFoodAll!=null)
            llFoodAll.setVisibility(View.GONE);
        FrameLayout.LayoutParams parmas = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,220);
        llAll.setLayoutParams(parmas);
    }

    public int getAllViewHeight()
    {
        int height = 0;
        if (llAllview!=null)
        {
            llAllview.measure(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
           height =  llAllview.getMeasuredHeight();
        }

       return height;
    }
    private ButtonClickListener buttonClickListener;
    private ButtonClickCheckListener buttonClickCheckListener;

    public interface ButtonClickListener
    {
        void queryCallback(HashMap<String, Object> paramResult);
    }

    public void setButtonClickListener(ButtonClickListener buttonClickListener)
    {
        this.buttonClickListener = buttonClickListener;
    }
    public interface ButtonClickCheckListener
    {
        void clickCallback(HashMap<String, Object> paramResult);
    }
    public void setButtonClickCheckListener(ButtonClickCheckListener buttonClickListener)
    {
        this.buttonClickCheckListener = buttonClickListener;
    }
    private CancelWindowClickListener cancelWindowClickListener;

    private interface CancelWindowClickListener
    {
        void cancelPopupWindowCallback();
    }

    public void setCancelPopupWindowListener(CancelWindowClickListener cancelWindowClickListener)
    {
        this.cancelWindowClickListener = cancelWindowClickListener;
    }

    private void setTextStyle(CustomFlowGroup flowGroup, View view)
    {
        for (int i = 0; i < flowGroup.getChildCount(); i++)
        {
            ((CustomFlowView) flowGroup.getChildAt(i)).isSelected = false;
            ((CustomFlowView) flowGroup.getChildAt(i)).setTextColor(ContextCompat.getColor(context, R.color.C0323));
            flowGroup.getChildAt(i).setBackgroundResource(R.drawable.round_c0323_bg_hollow);
        }
        ((CustomFlowView) view).setTextColor(ContextCompat.getColor(context, R.color.C0412));
        view.setBackgroundResource(R.drawable.round_c0412_bg_hollow);
        view.setSelected(true);
    }

    private void resetBusinessData(int index, List<AreaBean> areaBeanList)
    {
        businessParamResult = "";
        flowBusinessAreaGroup.removeAllViews();
        flowBusinessAreaGroup.setData(areaBeanList.get(index).getTrades(), context, businessParamResult, new CustomFlowGroup.ViewOnclickListener()
        {
            @Override
            public void onClickCallback(View v)
            {
                if (!((CustomFlowView) v).isSelected)
                {
                    setTextStyle(flowBusinessAreaGroup, v);
                    businessParamBefore = ((CustomFlowView) v).getText().toString();
                    if (businessParamBefore.equals("全部"))
                    {
                        businessParamResult = "";
                    } else
                    {
                        businessParamResult = businessParamBefore;
                    }
                } else
                {
                    ((CustomFlowView) v).setTextColor(ContextCompat.getColor(context, R.color.C0323));
                    v.setBackgroundResource(R.drawable.round_c0323_bg_hollow);
                    v.setSelected(false);
                    businessParamResult = "";
                }
            }
        });
    }
}
