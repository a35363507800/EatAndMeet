package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.fragments.OrderMealFrg;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.zhy.autolayout.AutoFrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CopyOrderMealAct extends BaseActivity
{
    //region 变量
    private static final String TAG = CopyOrderMealAct.class.getSimpleName();
    @BindView(R.id.afl_container)
    AutoFrameLayout afl_container;
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;

    private Activity mContext;
    private Dialog pDialog;
    private OrderMealFrg orderMealFrg;
    private String bootyCallDate;//约吃饭日期
    private String openSource;//聊天进入
    private String openFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_copy_ordermeal);
        ButterKnife.bind(this);
        initAfterView();
    }


    private void initAfterView()
    {
        mContext = this;

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("美 食");

        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
        pDialog.show();
        bootyCallDate = getIntent().getStringExtra("bootyCallDate");
        openSource = getIntent().getStringExtra("chat");
        openFrom = getIntent().getStringExtra("openFrom");
        orderMealFrg = OrderMealFrg.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("bootyCallDate", bootyCallDate);
        bundle.putString("openFrom", openFrom);
        bundle.putString("openSource", openSource);
        orderMealFrg.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.afl_container, orderMealFrg)
                .show(orderMealFrg)
                .commit();
        pDialog.dismiss();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
//        TopBarSwitch topBar = (TopBarSwitch) getSupportFragmentManager()
//                .findFragmentById(R.id.afl_container)
//                .getView().findViewById(R.id.top_bar_switch);
//        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
//        {
//            @Override
//            public void leftClick(View view)
//            {
//                finish();
//            }
//
//            @Override
//            public void right2Click(View view)
//            {
//
//            }
//        }).setText("订餐");

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
//                    Intent intent = MyDateDetailAct_.intent(mContext).get();
//                    intent.putExtra("streamId", EamApplication.getInstance().dateStreamId);
//                    startActivity(intent);
//                    SharePreUtils.setToDate(mContext, "toDate");
//                    SharePreUtils.setToOrderMeal(mContext, "");
//                    SharePreUtils.setOrderType(mContext, "");
            finish();

        }
        return super.onKeyDown(keyCode, event);
    }
}
