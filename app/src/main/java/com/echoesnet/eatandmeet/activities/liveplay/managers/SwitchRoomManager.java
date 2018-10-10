package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewConfigurationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.LAnchorsListBean;

import java.util.ArrayList;
import java.util.List;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;

/**
 * Created by liuyang on 2017/2/23.
 * <p>
 * 刷屏事件分发太麻烦了。
 * 直接吧消息列表的recycle view加入进来。。。现在就一个上下滑动的，相互冲突；
 */

public class SwitchRoomManager implements ViewPager.OnPageChangeListener
{
    private Activity mActivity;
    private VerticalViewPager verticalViewPager;
    private View mFollowView;
    private View mFollowView_1;

    private ArrayList<ImageView> arrRoom = new ArrayList<>();

    private int currentPosition = 1;

    private int livingRoomDataPosition = 0;

    private int mTouchSlop;
    private float mLastTouchX;
    private float mLastTouchY;
    private List<LAnchorsListBean> singletonLivingRoomList;

    public SwitchRoomManager(Activity mActivity, int verticalViewPagerID, View followView, View followView_1, View collisionV, String roomid, List<LAnchorsListBean> singletonLivingRoomList)
    {
        this.mActivity = mActivity;
        this.mFollowView = followView;
        this.mFollowView_1 = followView_1;
        this.singletonLivingRoomList = singletonLivingRoomList;


        //find room id position
        findP:
        {
            for (int i = 0; i < singletonLivingRoomList.size(); i++)
            {
                if (singletonLivingRoomList.get(i).getRoomId().equals(roomid))
                {
                    livingRoomDataPosition = i;
                    break findP;
                }
            }
        }


        //三个房间背景
        arrRoom.add(new ImageView(mActivity));
        arrRoom.add(new ImageView(mActivity));
        arrRoom.add(new ImageView(mActivity));
        verticalViewPager = (VerticalViewPager) mActivity.findViewById(verticalViewPagerID);
        verticalViewPager.setAdapter(new PagerAdapter()
        {
            @Override
            public int getCount()
            {
                return arrRoom.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object)
            {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position)
            {
                container.addView(arrRoom.get(position));
                return arrRoom.get(position);
            }
        });


        verticalViewPager.setOnPageChangeListener(this);
        verticalViewPager.setOffscreenPageLimit(2); //预加载出来,没重写 adapter的destory
        verticalViewPager.setCurrentItem(1, false);


        ViewConfiguration configuration = ViewConfiguration.get(mActivity);
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);


        verticalViewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent event)
            {
                boolean roomlayer = mFollowView.dispatchTouchEvent(event);
//                boolean avlayer = mFollowView_1.dispatchTouchEvent(event);
//                return roomlayer || avlayer;
                return roomlayer;
            }
        });

        fillSwitchRoomData();
    }

    private void fillSwitchRoomData()
    {
        LAnchorsListBean switchRoomData[] = new LAnchorsListBean[3];
        int lastData = singletonLivingRoomList.size() - 1;

        switchRoomData[0] = singletonLivingRoomList.get(livingRoomDataPosition == 0 ? lastData : livingRoomDataPosition - 1);
        switchRoomData[1] = singletonLivingRoomList.get(livingRoomDataPosition);
        switchRoomData[2] = singletonLivingRoomList.get(livingRoomDataPosition == lastData ? 0 : livingRoomDataPosition + 1);


        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(switchRoomData[0].getRoomUrl())
                .placeholder(R.drawable.qs_photo)
                .centerCrop()
                .into(arrRoom.get(0));

        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(switchRoomData[1].getRoomUrl())
                .placeholder(R.drawable.qs_photo)
                .centerCrop()
                .into(arrRoom.get(1));

        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(switchRoomData[2].getRoomUrl())
                .placeholder(R.drawable.qs_photo)
                .centerCrop()
                .into(arrRoom.get(2));
    }


    @Override
    public void onPageScrollStateChanged(int state)
    {
        if (state == ViewPager.SCROLL_STATE_IDLE)
        {
            if (currentPosition == 2)
            { //上滑->到2
                if ((livingRoomDataPosition + 1) >= singletonLivingRoomList.size())
                {
                    livingRoomDataPosition = 0;
                }
                else
                {
                    livingRoomDataPosition++;
                }
                fillSwitchRoomData();

                verticalViewPager.setCurrentItem(1, false);

            }
            else if (currentPosition == 0)
            {// 下滑->到0
                if ((livingRoomDataPosition - 1) < 0)
                {
                    livingRoomDataPosition = singletonLivingRoomList.size() - 1;
                }
                else
                {
                    livingRoomDataPosition--;
                }
                fillSwitchRoomData();

                verticalViewPager.setCurrentItem(1, false);
            }

//            arrRoom.get(1).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int scrolledPosition, float percent, int pixels)
    {
        Log.i("ppppp", scrolledPosition + " | " + percent + " | " + pixels + " | ");

        if (scrolledPosition == 0)
        { //网上移动
            int y = pixels - mFollowView.getHeight();
            mFollowView.scrollTo(0, y);
            mFollowView_1.scrollTo(0, pixels - mFollowView_1.getHeight());
        }
        else if (scrolledPosition == 1)
        {//往下移动
            mFollowView.scrollTo(0, pixels);
            mFollowView_1.scrollTo(0, pixels);
        }
        else
        {
            mFollowView.scrollTo(0, 0);
            mFollowView_1.scrollTo(0, 0);
        }
    }

    @Override
    public void onPageSelected(int position)
    {
        if (position == 1)
        {
            if (listener != null)
            {
                LAnchorsListBean switchRoomData = singletonLivingRoomList.get(livingRoomDataPosition);
                listener.onRoomSelected(switchRoomData);

            }

            arrRoom.get(1).setVisibility(View.GONE);
        }
        currentPosition = position;
        Log.i("ppppp", " | " + currentPosition + " | ");
    }

    private SwitchRoomManagerListener listener;

    public void setListener(SwitchRoomManagerListener listener)
    {
        this.listener = listener;
    }

    public interface SwitchRoomManagerListener
    {
        void onRoomSelected(LAnchorsListBean gotoRoomEH);
    }


    public void recycle()
    {
        verticalViewPager = null;
        mFollowView = null;
        mFollowView_1 = null;
        arrRoom.clear();
        arrRoom = null;
        mActivity = null;
    }

}
