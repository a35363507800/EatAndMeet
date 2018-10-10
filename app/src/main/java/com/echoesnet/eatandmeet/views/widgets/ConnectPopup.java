package com.echoesnet.eatandmeet.views.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;

/**
 * Created by Administrator on 2017/5/16.
 */

public class ConnectPopup
{
    private ConnectClickListener connectClickListener;
    private Context mContext;
    private LevelHeaderView levelHeaderView;
    private LevelView levelView;
    private GenderView tvAge;
    private TextView name;
    private Button btYes, btNo;
    private View view;
    private ViewGroup viewGroup;
    private boolean isShowing = false;

    public ConnectPopup(@NonNull Context context, ConnectClickListener connectClickListener)
    {

//        super(context);
        this.mContext = context;
        this.connectClickListener = connectClickListener;

        initView();
//        setContentView(view);
//        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//        setTouchable(true);
//        setFocusable(true);
//        setOutsideTouchable(true);
//        setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
//        setAnimationStyle(R.style.connectDialogWindowAnim);

    }

    private void initView()
    {
        view = LayoutInflater.from(mContext).inflate(R.layout.live_connect_view, null);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        levelHeaderView = (LevelHeaderView) view.findViewById(R.id.level_invite_header_view);
        levelView = (LevelView) view.findViewById(R.id.invite_level_view);
        tvAge = (GenderView) view.findViewById(R.id.tv_connect_age);
        name = (TextView) view.findViewById(R.id.tv_nick_name);
        btNo = (Button) view.findViewById(R.id.btn_refuse);
        btYes = (Button) view.findViewById(R.id.btn_accept);
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });

        RxView.clicks(btYes)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        if (connectClickListener != null)
                            connectClickListener.onClick("yes");
                        dismiss();
                    }
                });
        RxView.clicks(btNo)
                .throttleFirst(1,TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        if (connectClickListener != null)
                            connectClickListener.onClick("no");
                        dismiss();
                    }
                });
    }

    public boolean isShowing(){

        return isShowing;
    }

    public void showAtLocation(ViewGroup view){
        isShowing = true;
        viewGroup = view;
        view.addView(this.view);
    }

    public void dismiss()
    {
        isShowing = false;
        if (viewGroup != null)
            viewGroup.removeView(this.view);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                CommonUtils.removeClickLock("RoomActivity1");
            }
        }, 500);
    }

    public interface ConnectClickListener
    {
        void onClick(String tag);
    }

    public void setLevelHeaderView(String imageUrl, String isVuser)
    {
        levelHeaderView.setHeadImageByUrl(imageUrl);
        levelHeaderView.showRightIcon(isVuser);
//        levelHeaderView.setLevel(level);
    }

    public void setLevelView(String level)
    {
        levelView.setLevel(level, LevelView.USER);
    }

    public void setUserName(String userName)
    {
        name.setText(userName);
    }

    public void setSexAndAge(String sex, String age)
    {

        tvAge.setSex(age, sex);

        /*if (sex.equals("女"))
        {
            tvAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            tvAge.setText(String.format("%s %s", "{eam-e94f}", age));
        }
        else
        {
            tvAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            tvAge.setText(String.format("%s %s", "{eam-e950}", age));
        }*/
    }
}
