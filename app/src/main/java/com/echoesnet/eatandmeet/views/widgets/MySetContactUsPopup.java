package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.ContactListAdapter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ben on 2016/8/9.
 */
public class MySetContactUsPopup extends PopupWindow
{
    private Activity mContext;
    private List<HashMap<String, String>> source;

    public MySetContactUsPopup(Activity context, List<HashMap<String, String>> source)
    {
        this.mContext = context;
        this.source = source;
        initWindow();
    }

    private void initWindow()
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.popup_contact_us_mode, null);
        ListView lvContacts = (ListView) popupView.findViewById(R.id.lv_contact_lst);
        ContactListAdapter adapter = new ContactListAdapter(mContext, source);
        lvContacts.setAdapter(adapter);
        adapter.setOnItemClickListener(new ContactListAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                CommonUtils.makeCall(mContext, source.get(position).get("content"));
            }
        });
        Button mySetContactUsCancle = (Button) popupView.findViewById(R.id.my_set_contact_us_cancle);
        mySetContactUsCancle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(popupView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(CommonUtils.getScreenSize(mContext).width);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setOutsideTouchable(true);
        this.setFocusable(true);
        this.setAnimationStyle(R.style.PopupAnimation);
        this.backgroundAlpha(0.5f);
        //让pop可以点击外面消失掉
        this.setBackgroundDrawable(new ColorDrawable(0));
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
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent)
    {
        if (!this.isShowing())
        {
            this.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        }
    }
}
