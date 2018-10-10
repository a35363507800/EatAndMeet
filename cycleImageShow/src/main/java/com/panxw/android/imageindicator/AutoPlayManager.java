package com.panxw.android.imageindicator;

import android.os.Handler;
import android.os.Message;

/**
 * Auto BrocastManager for ImageIndicatorView
 *
 * @author steven-pan
 */
public class AutoPlayManager
{
    /**
     * default play start time
     */
    private static final long DEFAULT_STARTMILS = 2 * 1000;
    /**
     * default play time interval
     */
    private static final long DEFAULT_INTEVALMILS = 3 * 1000;
    /**
     * play times (always)
     */
    private static final int DEFAULT_TIMES = -1;
    /**
     * handler ID
     */
    private static final int LOOP_ID = 1;
    /**
     * broadcast flag, play default
     */
    private boolean broadcastEnable = false;
    /**
     * start play time ms
     */
    private long startMils = DEFAULT_STARTMILS;
    /**
     * play interval ms
     */
    private long intevalMils = DEFAULT_INTEVALMILS;
    /**
     * turn right
     */
    private final static int RIGHT = 0;
    /**
     * turn left
     */
    private final static int LEFT = 1;
    /**
     * current direction
     */
    private int direction = RIGHT;
    /**
     * auto play times
     */
    private int broadcastTimes = DEFAULT_TIMES;
    /**
     * play times record
     */
    private int timesCount = 0;

    /**
     * play handler
     */
    private Handler broadcastHandler = null;

    /**
     * target ImageIndicatorView
     */
    private ImageIndicatorView mImageIndicatorView = null;

    public AutoPlayManager(ImageIndicatorView imageIndicatorView)
    {
        this.mImageIndicatorView = imageIndicatorView;
        this.broadcastHandler = new BroadcastHandler(AutoPlayManager.this);
    }

    /**
     * set play start time and interval
     *
     * @param startMils   start time ms(>2, default 8s)
     * @param intevelMils time interval ms(default 3s)
     */
    public void setBroadcastTimeIntevel(long startMils, long intevelMils)
    {
        this.startMils = startMils;
        this.intevalMils = intevelMils;
    }

    /**
     * set auto play flag
     *
     * @param flag on or off
     */
    public void setBroadcastEnable(boolean flag)
    {
        this.broadcastEnable = flag;
    }

    /**
     * set loop times
     *
     * @param times loop times
     */
    public void setBroadCastTimes(int times)
    {
        this.broadcastTimes = times;
    }

    /**
     * start loop play
     */
    public void loop()
    {
        if (broadcastEnable)
        {
            broadcastHandler.sendEmptyMessageDelayed(0, this.startMils);
        }
    }

    public void stop()
    {
        this.broadcastEnable = false;

        if (this.broadcastHandler.hasMessages(LOOP_ID))
        {
            this.broadcastHandler.removeMessages(LOOP_ID);
        }
    }

    protected void handleMessage(Message msg)
    {
        if (broadcastEnable)
        {
            if (System.currentTimeMillis()
                    - mImageIndicatorView.getRefreshTime() < 2 * 1000)
            {
                // ignore loop less 2s
                broadcastHandler.sendEmptyMessageDelayed(LOOP_ID, this.intevalMils);
                return;
            }
            if ((broadcastTimes != DEFAULT_TIMES)
                    && (timesCount >= (broadcastTimes * 2)))
            {// loop times used out
                return;
            }
            if (direction == RIGHT)
            {
                // roll right
                if (mImageIndicatorView.getCurrentIndex() < mImageIndicatorView
                        .getTotalCount())
                {
//                    if (mImageIndicatorView.getCurrentIndex() == mImageIndicatorView
//                            .getTotalCount() - 1)
//                    {
//                        timesCount++;// add loop play times
//                        mImageIndicatorView.getViewPager().setCurrentItem(0, false);
////                        direction = LEFT;
//                       /* direction = RIGHT;
//                        mImageIndicatorView
//                                .getViewPager()
//                                .setCurrentItem(
//                                        0,
//                                        true);*/
//                    } else
//                    {
                        mImageIndicatorView.getViewPager().setCurrentItem(mImageIndicatorView.getCurrentIndex() + 1, true);
//                    }
                }
            } else
            {
                // roll left
                if (mImageIndicatorView.getCurrentIndex() >= 0)
                {
                    if (mImageIndicatorView.getCurrentIndex() == 0)
                    {
                        direction = RIGHT;
                        timesCount++;
                    } else
                    {
                        mImageIndicatorView
                                .getViewPager()
                                .setCurrentItem(
                                        mImageIndicatorView.getCurrentIndex() - 1,
                                        true);
                    }
                }
            }

            broadcastHandler.sendEmptyMessageDelayed(LOOP_ID, this.intevalMils);
        }
    }

    /**
     * 小米手机使用弱引用轮播效果丢失，不使用的话可能造成内存泄漏，妈妈个逼的
     */
    private static class BroadcastHandler extends Handler
    {
        //private final WeakReference<AutoPlayManager> autoBrocastManagerRef;
        private AutoPlayManager autoBrocastManagerRef;

        public BroadcastHandler(AutoPlayManager autoBrocastManager)
        {
            this.autoBrocastManagerRef = autoBrocastManager;
            //this.autoBrocastManagerRef = new WeakReference<AutoPlayManager>(autoBrocastManager);
        }

        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            //AutoPlayManager autoBrocastManager = autoBrocastManagerRef.get();
            AutoPlayManager autoBrocastManager = autoBrocastManagerRef;
            if (autoBrocastManager != null)
            {
                autoBrocastManager.handleMessage(msg);
            } else
            {
                System.out.println("轮播 弱引用为空");
            }
        }
    }

}
