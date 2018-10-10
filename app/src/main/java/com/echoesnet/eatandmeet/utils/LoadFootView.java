package com.echoesnet.eatandmeet.utils;

import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;


/**
 * Created by Administrator on 2017/2/16.
 */

public class LoadFootView
{


    public static void showFootView(ListView listView, boolean isShow, View footView, String text)
    {
        if (footView == null)
        {
            return;
        }
        TextView textView = (TextView) footView.findViewById(R.id.tv);
        LinearLayout llBg= (LinearLayout) footView.findViewById(R.id.ll_bg);
        if (TextUtils.isEmpty(text))
        {
            textView.setText("- 别拉了, 宝宝也是有底线的 -");
        } else
        {
            textView.setText(text);
        }

        if (isShow)
        {
            listView.addFooterView(footView, null, false);
        } else
        {
            listView.removeFooterView(footView);
        }

    }




    //新加代码

}
