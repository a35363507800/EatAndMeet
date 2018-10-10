package com.echoesnet.eatandmeet.views.widgets.payPopupWindow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.payUtil.IPayCancelListener;
import com.echoesnet.eatandmeet.views.adapters.OrderPayAdapter;
import com.joanzapata.iconify.IconDrawable;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangben on 2016/6/24.
 */
public class PayWaysPopup extends PopupWindow
{
    private static final String TAG = PayWaysPopup.class.getSimpleName();
    private Activity mContext;
    private List<Map<String, Object>> payWays;
    private int selectedItem = 0;
    private Button btnPayOk;
    private String payAmount;

    private IPayTypeSelectedListener payTypeListener;
    private IPayCancelListener mPayCancelListener;

    /**
     *
     * @param context
     * @param accountBalance 账户余额
     * @param payAmount 支付金额
     */
    public PayWaysPopup(Activity context, String accountBalance, String payAmount,final String cancelItem[])
    {
        this.mContext = context;
        this.payAmount = payAmount;
        initWindow(accountBalance, payAmount,cancelItem);
    }

    private void initWindow(final String accountBalance, final String payAmount,final String cancelItem[])
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup_pay_mode, null);
        ListView payLst = (ListView) popupView.findViewById(R.id.lv_pay);
        payWays = new ArrayList<Map<String, Object>>();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("payWay", "余额");
        map1.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_e923).colorRes(R.color.C0412));
        map1.put("balance", accountBalance);
        map1.put("isSelected", true);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("payWay", "支付宝支付");
        map2.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_s_ali_pay).colorRes(R.color.C0311));
        map2.put("balance", "");
        map2.put("isSelected", false);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("payWay", "微信支付");
        map3.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_s_wechat_pay).colorRes(R.color.C0315));
        map3.put("balance", "");
        map3.put("isSelected", false);
        Map<String, Object> map4 = new HashMap<>();
/*        map4.put("payWay", "银联支付");
        map4.put("icon", new IconDrawable(mContext, EchoesEamIcon.eam_s_bank_pay).colorRes(R.color.c12));
        map4.put("balance", "");
        map4.put("isSelected", false);*/



        boolean cancelItem1=false;
        boolean cancelItem2=false;
        boolean cancelItem3=false;
        if(cancelItem!=null)
        {
            for (int i = 0; i < cancelItem.length; i++)
            {
                if (map1.get("payWay").equals(cancelItem[i]))
                    cancelItem1=true;
                if (map2.get("payWay").equals(cancelItem[i]))
                    cancelItem2=true;
                if (map3.get("payWay").equals(cancelItem[i]))
                    cancelItem3=true;
            }

        }
        if(!cancelItem1)
        payWays.add(map1);
        if(!cancelItem2)
        payWays.add(map2);
        if(!cancelItem3)
        payWays.add(map3);

        final boolean cancel1=cancelItem1;
        //payWays.add(map4);

//        final Button btnPayOk = (Button) popupView.findViewById(R.id.btn_pay_ok);
//        final OrderPayAdapter adapter = new OrderPayAdapter(mContext, payWays,this);
        btnPayOk = (Button) popupView.findViewById(R.id.btn_pay_ok);
        payLst.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        adapter = new OrderPayAdapter(mContext, payWays, this);
        payLst.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrderPayAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Logger.t(TAG).d("点击事件  " + position);
                selectedItem = position;
                adapter.setSelection(position);
                adapter.notifyDataSetChanged();
                if(!cancel1)
                {
                    if (selectedItem == 0 && Double.compare(Double.parseDouble(payWays.get(0).get("balance").toString()), (Double.parseDouble(payAmount) / 100)) < 0)
                    {
                        btnPayOk.setBackgroundResource(R.color.FC7);
                        btnPayOk.setEnabled(false);
                        btnPayOk.setText("余额不足");
                    } else
                    {
                        btnPayOk.setBackgroundResource(R.drawable.btn6_selector);
                        btnPayOk.setEnabled(true);
                        btnPayOk.setText("确定");
                    }
                }else
                {
                    btnPayOk.setBackgroundResource(R.drawable.btn6_selector);
                    btnPayOk.setEnabled(true);
                    btnPayOk.setText("确定");
                }
            }
        });
        btnPayOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if("余额".equals(payWays.get(selectedItem).get("payWay")))
                {
                    if (payTypeListener != null)
                        payTypeListener.HsPaySelected();
                }

                if("支付宝支付".equals(payWays.get(selectedItem).get("payWay")))
                {
                    if (payTypeListener != null)
                        payTypeListener.ThirdPartPaySelected("alipay");
                }

                if("微信支付".equals(payWays.get(selectedItem).get("payWay")))
                {
                    if (payTypeListener != null)
                        payTypeListener.ThirdPartPaySelected("wx");
                }

                if("银联支付".equals(payWays.get(selectedItem).get("payWay")))
                {
                    if (payTypeListener != null)
                        payTypeListener.ThirdPartPaySelected("upacp");
                }
            }
        });
        //初始化
        if(!cancel1)
        {
            if (selectedItem == 0 && Double.parseDouble(accountBalance) < Double.parseDouble(payAmount) / 100)
            {
                btnPayOk.setEnabled(false);
                btnPayOk.setText("余额不足");
                btnPayOk.setBackgroundResource(R.color.FC7);
            }
        }

        Button btnPayCancel = (Button) popupView.findViewById(R.id.btn_pay_cancel);
        btnPayCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
                if (mPayCancelListener != null)
                {
                    mPayCancelListener.payCanceled();
                }
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mContext).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        this.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), (Bitmap) null));
        this.setAnimationStyle(R.style.PopupAnimation);
        this.getContentView().setFocusableInTouchMode(true);
        this.getContentView().setFocusable(true);
        this.setTouchInterceptor(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
                {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            registerBroadcastReceiver();
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }

    public void setPayTypeSelectedListener(IPayTypeSelectedListener listener)
    {
        this.payTypeListener = listener;
    }

    public void setPayCancelListener(IPayCancelListener listener)
    {
        this.mPayCancelListener = listener;
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }

    /**
     * 定义广播接收
     */
    private OrderPayAdapter adapter;
    public BroadcastReceiver UpdateUIBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (action.equals(EamConstant.ACTION_UPDATE_BALANCE))
            {
                if(!"余额".equals(payWays.get(0).get("payWay")))
                    return;
                payWays.get(0).put("balance", intent.getExtras().getString("balance"));
                adapter.notifyDataSetChanged();

                Logger.t(TAG).d("广播是否执行--> " + Double.parseDouble(payWays.get(0).get("balance").toString())
                        + " , " + payAmount);


                if (selectedItem == 0 && Double.parseDouble(payWays.get(0).get("balance").toString()) < Double.parseDouble(payAmount) / 100)
                {
                    btnPayOk.setEnabled(false);
                    btnPayOk.setText("余额不足");
                    btnPayOk.setBackgroundResource(R.color.FC7);
                }
                else
                {
                    btnPayOk.setBackgroundResource(R.drawable.btn6_selector);
                    btnPayOk.setEnabled(true);
                    btnPayOk.setText("确定");
                }
            }
        }
    };

//    private boolean mReceiverTag = false; // 广播接受者标识
    public void registerBroadcastReceiver()
    {
//        if(!mReceiverTag) {
            IntentFilter myIntentFilter = new IntentFilter();
//            mReceiverTag = true;  // 表示广播已注册
            myIntentFilter.addAction(EamConstant.ACTION_UPDATE_BALANCE);
            //myIntentFilter.
            Logger.t(TAG).d("注册广播");
            //注册广播
            mContext.getApplication().registerReceiver(UpdateUIBroadcastReceiver, myIntentFilter);
//        }

    }

    public void unregisterBroadcastReceiver()
    {
//        if(mReceiverTag) { // 判断广播是否注册
//            mReceiverTag = false;  // 广播已经注销
            if (UpdateUIBroadcastReceiver != null)
            {
                Logger.t(TAG).d("注销广播");
                mContext.getApplication().unregisterReceiver(UpdateUIBroadcastReceiver);
            }
//        }


    }
}
