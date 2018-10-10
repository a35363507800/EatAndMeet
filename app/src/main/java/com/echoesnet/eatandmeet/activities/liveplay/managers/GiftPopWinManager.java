package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.GiftBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.echoesnet.eatandmeet.utils.MD5Util;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.echoesnet.eatandmeet.utils.GlideOptions.bitmapTransform;

/**
 * Created by liuyang on 2017/3/28.
 */

public class GiftPopWinManager
{
    private final String TAG = GiftPopWinManager.class.getSimpleName();
    private Activity mActivity;
    private int gIconSize;
    private PopupWindow popupGiftWin;
    private ArrayList<GiftBean> gList = new ArrayList<>();


    private Button btnSendGift;
    private TextView tvEggCharge;
    private TextView tvFaceEggBalance;
    private LinearLayout llSelector;
    private ViewPager vpGift;



    //每一页的item数目
    private final int constEachPageItemCount = 8;
    private final int constGiftColumns = 4;
    private int[] number4GiftTimes = {1, 10, 66, 233, 520, 1314, 6666};

    //event
    private IOnViewClickListener mViewClickListener;
    private String faceEggBalance = "0";

    //---------------------------------------------------------------------------------------------------------
    // return values;
    private String chosenGiftID = "";
    private int chosenTimes;
    private GiftBean chosenGiftBean;

    public String getChosenGiftID()
    {
        return chosenGiftID;
    }

    public String getChosenGiftNum()
    {
        return number4GiftTimes[chosenTimes] + "";
    }

    public GiftBean getChosenGiftBean()
    {
        return chosenGiftBean;
    }

    public GiftPopWinManager(Activity act, int iconSize)
    {
        mActivity = act;
        gIconSize = iconSize;
    }

    public String getUIFaceEggBalance()
    {
        String egg = "0";
        if (tvFaceEggBalance != null)
        {
            egg = tvFaceEggBalance.getText().toString();
        }
        return egg;
    }

    public boolean isShowing()
    {
        return popupGiftWin != null && popupGiftWin.isShowing();
    }

    public void dismiss()
    {
        if (popupGiftWin != null && popupGiftWin.isShowing())
            popupGiftWin.dismiss();
    }

    public void refreshUIFaceEggBalance(String egg)
    {
        if (tvFaceEggBalance != null)
        {
            tvFaceEggBalance.setText(egg);
        }
        this.faceEggBalance = egg;
    }

    public void refillGiftList(List<GiftBean> arrGift4EH, String balance)
    {
        gList.clear();
        gList.addAll(arrGift4EH);
        faceEggBalance = balance;

        if(vpGift!=null)
        {
            vpGift.setAdapter(giftPagerAdapter);
        }
    }

    public void recycle()
    {
        mActivity = null;
        popupGiftWin = null;
    }


    public void show(View anchorView)
    {
        if (null == popupGiftWin)
        {
            popupGiftWin = new PopupWindow(mActivity);
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View contentView = inflater.inflate(R.layout.popup_live_play_gift, null);

            tvEggCharge = (TextView) contentView.findViewById(R.id.tv_egg_pay);//充值
            tvFaceEggBalance = (TextView) contentView.findViewById(R.id.tv_balance);
            btnSendGift = (Button) contentView.findViewById(R.id.tv_send_gift);
            RelativeLayout rlGiftChargeGroup = (RelativeLayout) contentView.findViewById(R.id.rlGiftChargeGroup);
            vpGift = (ViewPager) contentView.findViewById(R.id.vp_gift);
            llSelector = (LinearLayout) contentView.findViewById(R.id.ll_selector);
            llSelector.setVisibility(View.GONE);

            btnSendGift.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mViewClickListener != null)
                        mViewClickListener.onClick(v, "sendGift");
                }
            });

            tvEggCharge.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (mViewClickListener != null)
                        mViewClickListener.onClick(v, "chargeFaceEgg");
                }
            });
            //礼物item 100dp , icon 40dp
            RelativeLayout.LayoutParams lpvp = (RelativeLayout.LayoutParams) vpGift.getLayoutParams();
            lpvp.height = CommonUtils.dp2px(mActivity, 105) * (constEachPageItemCount / constGiftColumns);
            vpGift.setLayoutParams(lpvp);

            int pageCount = gList.size() % constEachPageItemCount > 0 ? gList.size() / constEachPageItemCount + 1 : gList.size() / constEachPageItemCount;
            if (pageCount > 1)
            {
                llSelector.setVisibility(View.VISIBLE);
                int size = CommonUtils.dp2px(mActivity, 8);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
                layoutParams.setMargins(size, 0, size, 0);
                for (int i = 0; i < pageCount; i++)
                {
                    View v = new View(mActivity);
                    v.setLayoutParams(layoutParams);
                    v.setBackgroundResource(i == 0 ? R.drawable.round_cornor_36_mc1_bg : R.drawable.round_cornor_36_fc4_bg);
                    llSelector.addView(v);
                }

                vpGift.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
                {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
                    {
                    }

                    @Override
                    public void onPageSelected(int position)
                    {
                        if (llSelector != null && llSelector.getChildCount() > 0)
                        {
                            for (int i = 0; i < llSelector.getChildCount(); i++)
                            {
                                llSelector.getChildAt(i).setBackgroundResource(R.drawable.round_cornor_36_fc4_bg);
                            }
                            llSelector.getChildAt(position).setBackgroundResource(R.drawable.round_cornor_36_mc1_bg);
                        }
                    }

                    @Override
                    public void onPageScrollStateChanged(int state)
                    {
                    }
                });
            } else
            {
                llSelector.setVisibility(View.GONE);
            }
            vpGift.setAdapter(giftPagerAdapter);
            tvFaceEggBalance.setText(faceEggBalance);
            popupGiftWin.setContentView(contentView);
            popupGiftWin.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            popupGiftWin.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
            popupGiftWin.setTouchable(true);
            popupGiftWin.setOutsideTouchable(true);
            popupGiftWin.update();// 刷新状态
            popupGiftWin.setBackgroundDrawable(new ColorDrawable(0xb0000000));
            popupGiftWin.setAnimationStyle(R.style.AnimationBottomInOut);
        }

        giftPagerAdapter.notifyDataSetChanged();
        if (!popupGiftWin.isShowing())
        {
            popupGiftWin.showAtLocation(anchorView, Gravity.BOTTOM, 0, 0);
        }
    }

    private PagerAdapter giftPagerAdapter = new PagerAdapter()
    {
        @Override
        public int getCount()
        {
            return gList.size() % constEachPageItemCount > 0 ?
                    gList.size() / constEachPageItemCount + 1 : gList.size() / constEachPageItemCount;
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            return createPagerContentView(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            if (container != null && object != null)
                container.removeView((View) object);
        }
    };


    /**
     * 创建每一页的礼物列表
     *
     * @param container
     * @param position  页码
     * @return
     */
    private View createPagerContentView(ViewGroup container, int position)
    {
        int rows = constEachPageItemCount / constGiftColumns;
        LinearLayout containLLView = new LinearLayout(mActivity);
        containLLView.setOrientation(LinearLayout.VERTICAL);

        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout.LayoutParams llparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llparam.weight = 1;

        for (int i = 0; i < rows; i++)
        {
            LinearLayout llrow = new LinearLayout(mActivity);
            llrow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            llrow.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < constGiftColumns; j++)
            {
                View itemView = inflater.inflate(R.layout.item_lgift_list, null);
                itemView.setLayoutParams(llparam);

                int p = position * constEachPageItemCount + i * constGiftColumns + j;
                if (p < gList.size())
                {
                    GiftBean giftBean = gList.get(p);
                    TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_gift_price);
                    TextView tvName = (TextView) itemView.findViewById(R.id.tv_gift_name);
                    RelativeLayout rlBorder = (RelativeLayout) itemView.findViewById(R.id.rl_gift_number);
                    TextView tvTimes = (TextView) itemView.findViewById(R.id.tv_gift_number);
                    TextView tvPrivilege = (TextView) itemView.findViewById(R.id.tv_gift_isPrivilege);
                    ImageView ivIcon = (ImageView) itemView.findViewById(R.id.img_gift_icon);
                    int giftcount = 0;
                    try
                    {
                        giftcount = Integer.parseInt(giftBean.getGcount());
                    } catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                        Logger.t(TAG).d(e.getMessage());
                    }

                    tvPrivilege.setVisibility(View.GONE);
                    //是否是特权礼物 0否 1是
                    String isPrivilege = giftBean.getIsPrivilege();
                    if ("1".equals(isPrivilege))
                    {
                        tvPrivilege.setVisibility(View.VISIBLE);
                        tvPrivilege.setBackgroundResource(R.drawable.round_cornor_0313);
                        tvPrivilege.setTextColor(mActivity.getResources().getColor(R.color.C0324));
                        tvPrivilege.setText("特权");
                    }
                    //是否是活动礼物 0否 1是
                    if (TextUtils.equals("1",giftBean.getIsActivity()))
                    {
                        tvPrivilege.setVisibility(View.VISIBLE);
                        tvPrivilege.setBackgroundResource(R.drawable.round_cornor_0312);
                        tvPrivilege.setTextColor(mActivity.getResources().getColor(R.color.C0321));
                        tvPrivilege.setText("活动");
                    }


                    if (giftcount > 0)
                    {
                        tvName.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.c1));
                        tvPrice.getPaint().setFakeBoldText(true);
                        tvName.getPaint().setFakeBoldText(true);
                        if (giftcount > 9999)
                            tvPrice.setText("剩余" + 9999 + "+");
                        else
                            tvPrice.setText("剩余" + giftcount);
                        tvName.setText(giftBean.getgName());
                    } else
                    {
                        tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.MC8));
                        tvName.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                        tvPrice.getPaint().setFakeBoldText(false);
                        tvName.getPaint().setFakeBoldText(false);

                        tvName.setText(giftBean.getgName());
                        tvPrice.setText(giftBean.getgPrice());
                    }

                    if (chosenGiftID.equals(giftBean.getgId()))
                    {
                        int numberOfGift = number4GiftTimes[chosenTimes < number4GiftTimes.length ? chosenTimes : number4GiftTimes.length - 1];
                        tvTimes.setText("x" + numberOfGift);
                    } else
                    {
                        rlBorder.setVisibility(View.INVISIBLE);
                        tvTimes.setVisibility(View.INVISIBLE);
                    }
                    try
                    {
                        Logger.t("giftbean00").d("name>>" + giftBean.getgName() + ">>url" + giftBean.getgUrl() + ">>" + MD5Util.MD5(giftBean.getgUrl()));
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    try
                    {
                        Logger.t(TAG).d("name:"+giftBean.getgName()+" url:"+MD5Util.MD5(giftBean.getgUrl()));
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    LruCacheBitmapLoader.getInstance().putBitmapInto(mActivity, giftBean.getgUrl(), ivIcon, gIconSize, gIconSize);

                    itemView.setTag(R.id.viewTagFirst, giftBean);
                    itemView.setTag(giftBean.getgId() + "L");
                    tvTimes.setTag(giftBean.getgId() + "T");
                    rlBorder.setTag(giftBean.getgId() + "B");

                    itemView.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            chosenGiftBean = (GiftBean) view.getTag(R.id.viewTagFirst);
                            chosenGift(chosenGiftBean.getgId());
                        }
                    });
                } else
                {
                    itemView.setVisibility(View.INVISIBLE);
                }
                llrow.addView(itemView);
            }

            containLLView.addView(llrow);
        }

        container.addView(containLLView);
        return containLLView;
    }

    private void chosenGift(String giftID)
    {
        String toInvisibleID = "";
        if (chosenGiftID.equals(giftID))
        {
            if ("0".equals(chosenGiftBean.getgType()))
                chosenTimes++;
            if (chosenTimes >= number4GiftTimes.length)
            {
                chosenTimes = 0;
            }
        } else
        {
            toInvisibleID = chosenGiftID;
            chosenGiftID = giftID;
            chosenTimes = 0;
        }

        TextView tvTimes = (TextView) popupGiftWin.getContentView().findViewWithTag(toInvisibleID + "T");
        //TextView tvPrerogative = (TextView) popupGiftWin.getContentView().findViewWithTag(toInvisibleID + "P");
        RelativeLayout rlBorder = (RelativeLayout) popupGiftWin.getContentView().findViewWithTag(toInvisibleID + "B");
        if (null != tvTimes && null != rlBorder)
        {

            tvTimes.setVisibility(View.INVISIBLE);
            rlBorder.setVisibility(View.INVISIBLE);
        }
        //tvPrerogative = (TextView) popupGiftWin.getContentView().findViewWithTag(giftID + "P");
        tvTimes = (TextView) popupGiftWin.getContentView().findViewWithTag(giftID + "T");
        rlBorder = (RelativeLayout) popupGiftWin.getContentView().findViewWithTag(giftID + "B");
        if (null != tvTimes && null != rlBorder)
        {
            tvTimes.setVisibility(View.VISIBLE);
            rlBorder.setVisibility(View.VISIBLE);
            int numberOfGift = number4GiftTimes[chosenTimes < number4GiftTimes.length ? chosenTimes : number4GiftTimes.length - 1];
            tvTimes.setText("x" + numberOfGift);
        }
        checkGiftSendBtn();
    }


    //更改对应礼物ID背包数量
    public void updataGiftCount(String giftId,String giftCount)
    {
        if(popupGiftWin!=null)

        {
            View itemView = popupGiftWin.getContentView().findViewWithTag(giftId + "L");
            if (itemView == null)
                return;

            TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_gift_price);
            TextView tvName = (TextView) itemView.findViewById(R.id.tv_gift_name);
        }
    }

    //更改礼物背包数量
    public void changeGiftCount()
    {
        if (chosenGiftBean == null)
            return;
        View itemView = popupGiftWin.getContentView().findViewWithTag(chosenGiftBean.getgId() + "L");
        if (itemView == null)
            return;

        TextView tvPrice = (TextView) itemView.findViewById(R.id.tv_gift_price);
        TextView tvName = (TextView) itemView.findViewById(R.id.tv_gift_name);

        GiftBean giftBean = (GiftBean) itemView.getTag(R.id.viewTagFirst);

        int result = Integer.parseInt(giftBean.getGcount()) - number4GiftTimes[chosenTimes];
        if (result > 0)
        {
            giftBean.setGcount(String.valueOf(result));
            tvName.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.c1));
            tvPrice.getPaint().setFakeBoldText(true);
            tvName.getPaint().setFakeBoldText(true);
            if (result > 9999)
                tvPrice.setText("剩余" + 9999 + "+");
            else
                tvPrice.setText("剩余" + result);
            tvName.setText(giftBean.getgName());
        } else
        {
            giftBean.setGcount("0");
            tvPrice.setTextColor(ContextCompat.getColor(mActivity, R.color.MC8));
            tvName.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            tvPrice.getPaint().setFakeBoldText(false);
            tvName.getPaint().setFakeBoldText(false);

            tvName.setText(giftBean.getgName());
            tvPrice.setText(giftBean.getgPrice());
        }

        giftPagerAdapter.notifyDataSetChanged();
    }


    //检测脸蛋余额  并设置赠送按钮,（出现过btnSendGift为null的崩溃！）
    public void checkGiftSendBtn()
    {
        try
        {
            if (null != chosenGiftBean)
            {
                //钱够了
                int faceEgg = Integer.parseInt(faceEggBalance);
                int giftCount = Integer.parseInt(chosenGiftBean.getGcount());
                int price = Integer.parseInt(chosenGiftBean.getgPrice());

                if (faceEgg >= ((number4GiftTimes[chosenTimes] - giftCount) * price))
                {
                    btnSendGift.setEnabled(true);
                    btnSendGift.setBackgroundResource(R.drawable.round_cornor_36_mc1_bg);
                    //检查等级是否够发特权礼物
                    if (chosenGiftBean.getAuthority().equals("0"))
                        setSendBtnStatus(false);
                    else
                        setSendBtnStatus(true);
                } else
                {
                    btnSendGift.setEnabled(false);
                    btnSendGift.setBackgroundResource(R.drawable.round_cornor_36_fc4_bg);
                }
            } else
            {
                btnSendGift.setEnabled(false);
                btnSendGift.setBackgroundResource(R.drawable.round_cornor_36_fc4_bg);
            }
        } catch (Exception e)
        {
            EamLogger.writeToDefaultFile("Module：直播| 检查发送礼物按钮异常：" + e.getMessage());
        }
    }

    public void setSendBtnStatus(boolean isAble)
    {
        if (btnSendGift == null)
            return;
        if (isAble)
        {
            btnSendGift.setEnabled(true);
            btnSendGift.setBackgroundResource(R.drawable.round_cornor_36_mc1_bg);
        } else
        {
            btnSendGift.setEnabled(false);
            btnSendGift.setBackgroundResource(R.drawable.round_cornor_36_fc4_bg);
        }
    }

    public interface IOnViewClickListener
    {
        void onClick(View view, String viewId);
    }

    public void setViewClickListener(IOnViewClickListener listener)
    {
        this.mViewClickListener = listener;
    }

/*  不要使用这种view绑定的方式，要写成通用的
  public interface GiftPopWinEvents
    {
        void bind(View vSend, View vEggCharge);
    }

    public void bindEvents(GiftPopWinEvents bEvents)
    {
        this.bEvents = bEvents;
    }*/
}
