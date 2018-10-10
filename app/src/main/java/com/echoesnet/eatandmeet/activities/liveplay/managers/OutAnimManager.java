package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.views.widgets.LevelView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzy on 2017/5/12.
 */

public class OutAnimManager
{

    private ImageView outLv;
    private TextView outName;
    private ImageView outImg;
    private ArrayList<Map<String, String>> outList;
    private View prerogativeView;
    //进房特效的等级要求，应该从后台获取，这里暂写成固定 --lzy
    public final int OUT_LEVEL = 10;
    private Context mContext;
    private DataBindListener dataBindListener;

    public OutAnimManager(Context context, Window window, int id)
    {
        this.mContext = context;
        dataBindListener = getechoListener();
        outList = new ArrayList<>();
        prerogativeView = window.findViewById(id);
    }

    //判断用户是否达到进房特效条件
    public boolean checkLvAnim(String level, String sign)
    {
        if ("1".equals(sign))
            return true;

        int lv = 0;
        try
        {
            lv = Integer.parseInt(level);
        } catch (NumberFormatException ex)
        {
            lv = 0;
        }

        if (lv >= OUT_LEVEL)
            return true;
        else
            return false;
    }

    //进场动画
    public void addOutAnim(String level, String name)
    {

        Map map = new HashMap();
        map.put("level", level);
        map.put("name", name);
        outList.add(map);

        if (prerogativeView.getVisibility() == View.GONE)
        {
            outList.remove(0);
            outAnim(map);
        }

    }

    private void outAnim(Map<String, String> map)
    {
        prerogativeView.setVisibility(View.VISIBLE);
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.room_prerogative);
        if (dataBindListener != null)
        {
            dataBindListener.childView(prerogativeView, map).setAnimation(anim);
        }
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                prerogativeView.setVisibility(View.GONE);
                if (outList.size() > 0)
                {
                    Map map = outList.get(0);
                    outList.remove(0);
                    outAnim(map);
                }
            }
        }, 3100);
    }

    public interface DataBindListener
    {
        View childView(View view, Map<String, String> barrageBean);
    }

    public DataBindListener getechoListener()
    {
        return new DataBindListener()
        {
            @Override
            public View childView(View view, Map<String, String> dataMap)
            {
                if (outLv == null || outName == null)
                {
                    outLv = (ImageView) prerogativeView.findViewById(R.id.llv_room_welcome_lv);
                    outName = (TextView) prerogativeView.findViewById(R.id.tv_room_welcome_name);
                    outImg = (ImageView) prerogativeView.findViewById(R.id.welcome_bg_im);
                }
                outName.setText(dataMap.get("name"));
                try
                {
                    outLv.setImageDrawable(ContextCompat.getDrawable(mContext,LevelView.getLevelRoundImage(Integer.parseInt(dataMap.get("level")))));
                } catch (Exception e)
                {
                    outLv.setImageDrawable(null);
                }
                outImg.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.out_gift));


                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        AnimationDrawable animationDrawable = (AnimationDrawable) outImg
                                .getDrawable();
                        animationDrawable.start();
                    }
                }, 1100);

                return view;
            }


        };
    }
}