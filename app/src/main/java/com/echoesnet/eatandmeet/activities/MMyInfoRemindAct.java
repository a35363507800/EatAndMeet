package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.MyInfoRemindAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class MMyInfoRemindAct extends BaseActivity {
    private static final String TAG = MMyInfoRemindAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.lv_message)
    ListView lvMessage;

    private Dialog pDialog;
    private Activity mContext;
    private String[] iconDrawables = {"{eam-s-fork @color/white @dimen/f7}", "{eam-s-ticket @color/white @dimen/f7}", "{eam-e9a4 @color/white @dimen/f7}"};
    private String[] names = {"用餐提醒", "优惠券提醒", "审核提醒"};
    private String[] hints = {"此处显示最近一条提醒信息, 最多显示两行, 如内容超出两行则显示...", "用餐提醒、优惠券使用提醒等", "您的实名认证审核已通过"};
    private ColorDrawable[] colorDrawables = {new ColorDrawable(0xffeb8b31), new ColorDrawable(0xff1986bc), new ColorDrawable(0xffdb3c38)};

    private List<HashMap<String, Object>> list;
    private MyInfoRemindAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myinfo_remind);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(pDialog!=null&&pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog=null;
        }
    }

    private void afterViews() {
        mContext = this;
        topBar.setTitle("提醒");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener() {
            @Override
            public void leftClick(View view) {
                mContext.finish();
            }

            @Override
            public void left2Click(View view) {

            }

            @Override
            public void rightClick(View view) {
            }
        });

        list = new ArrayList<>();

        for (int i = 0; i < iconDrawables.length; i++) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("img", iconDrawables[i]);
            hashMap.put("name", names[i]);
            hashMap.put("hint", hints[i]);
            hashMap.put("color", colorDrawables[i]);
            list.add(hashMap);
        }

        adapter = new MyInfoRemindAdapter(mContext, list);
        lvMessage.setAdapter(adapter);

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);
    }

    @OnItemClick({R.id.lv_message})
    void onItemClick(int position) {
        switch (position) {
            case 0:
                Intent intent = new Intent(mContext, MMyInfoOrderRemindAct.class);
                startActivity(intent);
                break;
            case 1:
                /*Logger.t(TAG).d("扫码验证");
                Intent intent = new Intent(mContext, CaptureActivity.class);
                startActivity(intent);*/
                Logger.t(TAG).d("待定");
                break;
            case 2:
                Intent intent1 = new Intent(mContext, MMyInfoCheckRemindAct.class);
                startActivity(intent1);
                break;
        }
    }

}
