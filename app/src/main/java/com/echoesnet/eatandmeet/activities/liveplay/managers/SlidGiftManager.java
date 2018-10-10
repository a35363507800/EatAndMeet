package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by TDJ on 2016/7/6.
 */
public final class SlidGiftManager
{
    private  Activity mAct;
    private  int gIconSize;
    private  LinearLayout mContainer;
    private static final LinkedList<SlidGift.GiftRecord> prepList = new LinkedList<>();

    private static final LinkedList<View> gViewList = new LinkedList<>();

    private boolean waiting = false;

    private SlidGiftManager()
    {
    }

    public void waiting()
    {
        waiting = true;
        prepList.clear();
        gViewList.clear();
    }

    public void working()
    {
        waiting = false;
    }

    private static final class SlidInstance
    {
        private static final SlidGiftManager t = new SlidGiftManager();
    }

    public static SlidGiftManager getInstance()
    {
        return SlidInstance.t;
    }

    public void init(int container, Activity act, int iconSize)
    {
        mAct = act;
        gIconSize = iconSize;
        prepList.clear();
        gViewList.clear();
        mContainer = (LinearLayout) mAct.findViewById(container);
        if (mContainer == null)
            return ;

        for (int childCount = mContainer.getChildCount(); childCount > 0; childCount--)
        {
            View v = mContainer.getChildAt(childCount - 1);
            v.setVisibility(View.INVISIBLE);
            v.setTag(R.id.viewTagFirst, "");//gid
            gViewList.offer(v);
        }
    }

    public void recycle()
    {
        mContainer = null;
        mAct = null;
        prepList.clear();
        gViewList.clear();
    }


    public void put(SlidGift.GiftRecord record)
    {
        if (waiting)
        {
            return;
        }

        if (record == null)
            throw new ExceptionInInitializerError("without gift");
        prepList.offer(record);
        synchronized (gViewList)
        {
            View v = findTargetView(record.gid);
            if (null != v)
            {
                doAnim(v, record);
                prepList.poll();
            }
        }
    }

    private View findTargetView(@NonNull String gid)
    {
        for (int i = 0; i < gViewList.size(); i++)
        {
            View v = gViewList.get(i);
            String vGid = (String) v.getTag(R.id.viewTagFirst);
            if (vGid.equals(gid))
            {
                return v;   //正在动画，时间重制
            }
        }
        for (int i = 0; i < gViewList.size(); i++)
        {
            View v = gViewList.get(i);
            String vGid = (String) v.getTag(R.id.viewTagFirst);
            if ("".equals(vGid))
            {
                return v;   //无人使用, 时间重制
            }
        }
        return null; //沾满了
    }


    //Anim
    private static final int TIME_PARENT_TRANSLATION = 100;
    private static final int GIFT_ANIM_START = 1;
    private static final int GIFT_ANIM_SCALE_TEXT = 1<<1;
    private static final int GIFT_ANIM_DISAPPEAR = 1<<2;
    private static final int GIFT_ANIM_RESET = 1<<3;

    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            View view=(View) msg.obj;
            switch (msg.what)
            {
                case GIFT_ANIM_START://页面上没有动画的情况
                    view.setVisibility(View.VISIBLE);
                    Animator appearAnim = (Animator) view.getTag(R.id.viewTagSecond);
                    if (appearAnim == null)
                    {
                        appearAnim = startAnim(view);
                        view.setTag(R.id.viewTagSecond, appearAnim);
                    }
                    appearAnim.start();
                    break;
                case GIFT_ANIM_SCALE_TEXT:
                    TextView mGiftNumber = (TextView) view.findViewById(R.id.giftNum);
                    Animator scaleAnim = (Animator) mGiftNumber.getTag();
                    if (scaleAnim == null)
                    {
                        scaleAnim = scaleTextAnim(view);
                        mGiftNumber.setTag(scaleAnim);
                    }

//                    Animator disappearAnim = (Animator)view.getTag(R.id.viewTagThird);
//                    if (disappearAnim != null && disappearAnim.isRunning())
//                    {
//                        return;
//                    }
//                    if (disappearAnim != null && disappearAnim.isStarted())
//                    {
//                        disappearAnim.cancel();
//                    }
//                    if (scaleAnim.isRunning())
//                    {
//                        scaleAnim.cancel();
//                    }
                    scaleAnim.start();
                    break;
                case GIFT_ANIM_DISAPPEAR:
                    Animator disappearAnim1 = (Animator) view.getTag(R.id.viewTagThird);
                    if (disappearAnim1 == null)
                    {
                        disappearAnim1 = disappearAnim((View) msg.obj);
                        view.setTag(R.id.viewTagThird, disappearAnim1);
                    }
                    disappearAnim1.setStartDelay(TIME_PARENT_TRANSLATION * 4);
                    disappearAnim1.start();
                    break;
                case GIFT_ANIM_RESET:
                    ((View) msg.obj).setTag(R.id.viewTagFirst, "");
                    doNext();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 执行动画效果
     * @param v 要执行动画效果的view
     * @param record
     */
    private void doAnim(View v, @NonNull SlidGift.GiftRecord record)
    {
        String vGid = (String) v.getTag(R.id.viewTagFirst);
        Message msg = new Message();
//        msg.arg1=0;
//        msg.arg2=0;
//        msg.obj = v;  // take view to do Anim

        if ("".equals(vGid))
        {
            //无人使用, 数据，动画重制
            TextView mGiftNumber = (TextView) v.findViewById(R.id.giftNum);
            mGiftNumber.setTag(R.id.viewTagFirst,Integer.parseInt(record.giftNumber));
            mGiftNumber.setTag(R.id.viewTagSecond,1); // save Gift Number Amount and index, repeat Scale Text Anim
            // add this for easy fullfill the requirement, change gift anim at march 2018.
            if (null!=mGiftNumber.getTag(R.id.viewTagThird)){
                ArrayList<Integer> arrayList = (ArrayList<Integer>) mGiftNumber.getTag(R.id.viewTagThird);
                arrayList.clear();
                arrayList.add(Integer.parseInt(record.giftNumber));
                mGiftNumber.setTag(R.id.viewTagThird,arrayList);
            }else {
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.parseInt(record.giftNumber));
                mGiftNumber.setTag(R.id.viewTagThird,arrayList);
            }
            mGiftNumber.setTag(R.id.viewTagFourth,1);
            mGiftNumber.setText("x1");



            ImageView mGiftImg = (ImageView) v.findViewById(R.id.ivgift);
            LevelHeaderView mUsrIcon = (LevelHeaderView) v.findViewById(R.id.crvheadimage);
            TextView mUsrName = (TextView) v.findViewById(R.id.tv_user_name);
            TextView mUsrDisc = (TextView) v.findViewById(R.id.tv_gift_title);
            ImageView mUsrLevel = (ImageView) v.findViewById(R.id.iv_level);

            v.setTag(R.id.viewTagFirst, record.gid);
            try
            {
                if("0".equals(record.level))
                    mUsrLevel.setImageDrawable(null);
                    else
                    mUsrLevel.setImageDrawable(ContextCompat.getDrawable(mAct, LevelView.getLevelRoundImage(Integer.parseInt(record.level))));
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
            mUsrName.setText(record.name);
            mUsrDisc.setText(record.disc);
            mUsrIcon.showRightIcon(record.vUser);
            mUsrIcon.setLiveState(false);
            mUsrIcon.setHeadImageByUrl(record.usrIcon);
            mUsrIcon.setLevel(record.level);
            LruCacheBitmapLoader.getInstance().putBitmapInto(mAct, record.giftImg, mGiftImg, gIconSize, gIconSize);

            // trigger Anim cycle
            msg.what = GIFT_ANIM_START;
            msg.obj = v;
            mHandler.sendMessage(msg);
        }
        else
        {
            //加数量
            TextView mGiftNumber = (TextView) v.findViewById(R.id.giftNum);
            int amount = (int) mGiftNumber.getTag(R.id.viewTagFirst);
            // add new Gift Number Amount
            mGiftNumber.setTag(R.id.viewTagFirst,Integer.parseInt(record.giftNumber)+amount);
            // add this for easy fullfill the requirement, change gift anim at march 2018.
            if (null!=mGiftNumber.getTag(R.id.viewTagThird)){
                ArrayList<Integer> arrayList = (ArrayList<Integer>) mGiftNumber.getTag(R.id.viewTagThird);
                arrayList.add(Integer.parseInt(record.giftNumber));
                mGiftNumber.setTag(R.id.viewTagThird,arrayList);
            }else {
                ArrayList<Integer> arrayList = new ArrayList<>();
                arrayList.add(Integer.parseInt(record.giftNumber));
                mGiftNumber.setTag(R.id.viewTagThird,arrayList);
            }
        }
    }


    private Animator startAnim(View v)
    {
        ImageView mGiftImg = (ImageView) v.findViewById(R.id.ivgift);
        TextView mGiftNumber = (TextView) v.findViewById(R.id.giftNum);

        int distance = -v.getPaddingLeft() - v.getMeasuredWidth();
        Animator anim0 = ofAnimators(v, "translationX", distance, 0f);
        Animator anima = ofAnimators(v, "alpha", 0.8f, 1.0f);
        Animator animy = ofAnimators(v, "translationY", 0.0f, 0.0f);
        anim0.setDuration(TIME_PARENT_TRANSLATION);

        Animator anim1 = ofAnimators(mGiftImg, "translationX", distance, 0f);
        anim1.setDuration((int) (TIME_PARENT_TRANSLATION * 2.5));

        Animator anim2 = ofAnimators(mGiftNumber, "translationX", distance, 0f);
        anim2.setDuration((int) (TIME_PARENT_TRANSLATION * 2.5));

        AnimatorSet set = new AnimatorSet();

        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Message msg = new Message();
                msg.what = GIFT_ANIM_SCALE_TEXT;
                msg.obj = v;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        set.playTogether(anim0, anima, animy, anim1, anim2);
        return set;
    }

    private Animator scaleTextAnim(View v)
    {
        TextView mGiftNumber = (TextView) v.findViewById(R.id.giftNum);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(ofAnimators(mGiftNumber, "scaleX", 1.0f, 1.5f, 2.0f, 1.5f, 1.0f),
                ofAnimators(mGiftNumber, "scaleY", 1.0f, 1.5f, 2.0f, 1.5f, 1.0f));
        set.setDuration((int) (TIME_PARENT_TRANSLATION * 3.2));
        set.setStartDelay(50);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                int amount = (int)mGiftNumber.getTag(R.id.viewTagFirst);
                int index = (int)mGiftNumber.getTag(R.id.viewTagSecond);
                int numOfOnce = ((ArrayList<Integer>)mGiftNumber.getTag(R.id.viewTagThird)).get(0);
                int iOfOnce = (int)mGiftNumber.getTag(R.id.viewTagFourth);
                if(iOfOnce >= numOfOnce){
                    iOfOnce = 0;
                    mGiftNumber.setTag(R.id.viewTagFourth,1);
                    if (null!=mGiftNumber.getTag(R.id.viewTagThird)){
                        ArrayList<Integer> arrayList = (ArrayList<Integer>) mGiftNumber.getTag(R.id.viewTagThird);
                        if(arrayList.size()>0){
                            arrayList.remove(0);
                        }
                        mGiftNumber.setTag(R.id.viewTagThird,arrayList);
                    }
                }

                if(index<amount){
//                    set.playTogether(ofAnimators(mGiftNumber, "scaleX", 1.5f, 2.0f, 1.5f),
//                            ofAnimators(mGiftNumber, "scaleY", 1.5f, 2.0f, 1.5f));
                    int growth=0;
                    if(numOfOnce<=10){
                        growth = (iOfOnce + 1) > numOfOnce ? numOfOnce-iOfOnce : 1;
                        set.setDuration((int) (TIME_PARENT_TRANSLATION * 1.8));
                    }else if(numOfOnce<=66){
                        growth = (iOfOnce + 2) > numOfOnce ? numOfOnce-iOfOnce : 2;
                        set.setDuration((int) (TIME_PARENT_TRANSLATION * 1.0));
                    }else if(numOfOnce<=233){
                        growth = (iOfOnce + 1) > numOfOnce ? numOfOnce-iOfOnce : 1;
                        set.setDuration((int) (TIME_PARENT_TRANSLATION * 0.4));
                    }else if(numOfOnce<=520){
                        growth = (iOfOnce + 4) > numOfOnce ? numOfOnce-iOfOnce : 4;
                        set.setDuration((int) (TIME_PARENT_TRANSLATION * 0.6));
                    }else if(numOfOnce<=1314){
                        growth = (iOfOnce + 6) > numOfOnce ? numOfOnce-iOfOnce : 6;
                        set.setDuration((int) (TIME_PARENT_TRANSLATION * 0.4));
                    }else if(numOfOnce<=6666){
                        growth = (iOfOnce + 6) > numOfOnce ? numOfOnce-iOfOnce : 6;
                        set.setDuration((int) (TIME_PARENT_TRANSLATION * 0.4));
                    }
                    index += growth;
                    iOfOnce += growth;

                    mGiftNumber.setTag(R.id.viewTagSecond,index);
                    mGiftNumber.setTag(R.id.viewTagFourth,iOfOnce);
                    mGiftNumber.setText("x"+index);
//                    set.setDuration((int) (TIME_PARENT_TRANSLATION * 3.5 * ((float)index/(float) amount)));


                    set.start();
                }else {
                    set.setDuration((int) (TIME_PARENT_TRANSLATION * 2.5));
                    Message msg = new Message();
                    msg.what = GIFT_ANIM_DISAPPEAR;
                    msg.obj = v;
                    mHandler.sendMessage(msg);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return set;
    }

    private Animator disappearAnim(View v)
    {
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ofAnimators(v, "translationY", 0.0f, -176), ofAnimators(v, "alpha", 1.0f, 0.0f));
        set.setDuration(TIME_PARENT_TRANSLATION * 4);
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Message msg = new Message();
                msg.what = GIFT_ANIM_RESET;
                msg.obj = v;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return set;
    }


    private Animator ofAnimators(View target, String name, float... values)
    {
        return ObjectAnimator.ofFloat(target, name, values);
    }


    private void doNext()
    {
        SlidGift.GiftRecord record = prepList.poll();
        if (null != record)
        {
            View v = findTargetView(record.gid);
            if (null != v)
            {
                doAnim(v, record);
            }
        }
    }


//    private SlidGift.OnAnimatorEndListener getEndListener() {
//        return new SlidGift.OnAnimatorEndListener() {
//            @Override
//            public void onAnimatorEnd(@NonNull SlidGift entity) {
//                synchronized (currentList) {
//                    currentList.remove(entity);
//                    int show = SHOWLen - currentList.size();
//                    for (int i = 0; i < show; i++) {
//                        SlidGift g1 = newSlidGift(prepList.poll());
//                        if (g1 != null) {
//                            currentList.add(g1);
//                            g1.relationView(mContainer, getLLParams());
//                        }
//                    }
//
////                    switch (currentList.size()) {
////                        case 0:
////                            SlidGift g1 = newSlidGift(prepList.poll());
////                            SlidGift g2 = newSlidGift(prepList.poll());
////                            if (g1 != null) {
////                                currentList.add(g1);
////                                g1.relationView(mContainer, getLayoutParams(0));
////                            }
////                            if (g2 != null) {
////                                currentList.add(g2);
////                                g2.relationView(mContainer, getLayoutParams(1));
////                            }
////                            break;
////                        case 1:
////                            SlidGift g = newSlidGift(prepList.poll());
////                            if (g != null) {
////                                currentList.add(g);
////                                if (currentList.get(0) != null && currentList.get(0).getRules()[RelativeLayout.ALIGN_PARENT_BOTTOM] == RelativeLayout.TRUE) {
////                                    g.relationView(mContainer, getLayoutParams(0));
////                                } else {
////                                    g.relationView(mContainer, getLayoutParams(1));
////                                }
////                            }
////                            break;
////                    }
//                }
//            }
//        };
//    }
}
