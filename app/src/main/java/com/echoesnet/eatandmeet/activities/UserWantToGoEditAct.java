package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.widget.WheelView;

public class UserWantToGoEditAct extends BaseActivity
{
    private final String TAG = UserWantToGoEditAct.class.getSimpleName();
    private final int CHOOSE_RES = 110;

    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.arl_go_time)
    AutoRelativeLayout arlGoTime;
    @BindView(R.id.arl_go_where)
    AutoLinearLayout arlGoWhere;
    @BindView(R.id.tv_go_time)
    TextView tvGoTime;
    @BindView(R.id.tv_res_name)
    TextView tvResName;
    @BindView(R.id.itv_clear_time)
    IconTextView itvClearTime;
    @BindView(R.id.itv_clear_res)
    IconTextView itvClearRes;


    private Activity mAct;
    private String goResId = "";
    private String resName;
    private String resTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_want_to_go_edit_act);
        ButterKnife.bind(this);
        afterView();
    }
    private void afterView()
    {
        mAct = this;
        topBar.setTitle("最近要去");
        topBar.setRightTitle("提交");
        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {
                if (TextUtils.isEmpty(tvGoTime.getText().toString()) && !TextUtils.isEmpty(tvResName.getText().toString()))
                {
                    ToastUtils.showShort("请选择时间");
                    return;
                }
                if (TextUtils.isEmpty(tvResName.getText().toString()) && !TextUtils.isEmpty(tvGoTime.getText().toString()))
                {
                    ToastUtils.showShort("请选择餐厅");
                    return;
                }
                Intent intent = new Intent(mAct,MyInfoEditAct.class);
                intent.putExtra("goTime", tvGoTime.getText().toString());
                intent.putExtra("goResName", tvResName.getText().toString());
                intent.putExtra("goResId", goResId);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        this.resName = getIntent().getStringExtra("resName");
        this.resTime = getIntent().getStringExtra("resTime");
        if (!TextUtils.isEmpty(resName) && !TextUtils.isEmpty(resTime))
        {
            tvResName.setText(resName);
            tvGoTime.setText(resTime);
            itvClearRes.setVisibility(View.VISIBLE);
            itvClearTime.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.arl_go_time, R.id.arl_go_where, R.id.itv_clear_time, R.id.itv_clear_res})
    void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.arl_go_time:
                showDatePicker(tvGoTime);
                break;
            case R.id.arl_go_where:
                Intent intent = new Intent(mAct,ChooseGoWhereAct.class);
                startActivityForResult(intent, CHOOSE_RES);
                break;
            case R.id.itv_clear_time:
                tvGoTime.setText("");
                itvClearTime.setVisibility(View.GONE);
                break;
            case R.id.itv_clear_res:
                tvResName.setText("");
                goResId = "";
                itvClearRes.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    /**
     * 生日选择器
     */
    private void showDatePicker(final TextView showDateView)
    {
        DatePicker picker = new DatePicker(mAct, DateTimePicker.YEAR_MONTH_DAY);
        picker.setCycleDisable(false);
        picker.setLineVisible(true);
        picker.setTopLineVisible(false);
        picker.setShadowVisible(false);
        picker.setTitleText("选择日期");
        picker.setTitleTextSize(14);
        picker.setTitleTextColor(ContextCompat.getColor(mAct,R.color.C0321));
        picker.setCancelTextColor(ContextCompat.getColor(mAct,R.color.C0311));
        picker.setSubmitTextColor(ContextCompat.getColor(mAct,R.color.C0311));
        picker.setTopLineVisible(false);
        picker.setRangeStart(1900, 1, 1);
        picker.setRangeEnd(2200, 12, 31);
        picker.setSelectedItem(1990, 1, 1);
        WheelView.LineConfig config1 = new WheelView.LineConfig();
        config1.setColor(0xFF33B5E5);//线颜色
        config1.setAlpha(140);//线透明度
        config1.setRatio((float) (1.0));//线比率
        picker.setLineConfig(config1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener()
        {
            @Override
            public void onDatePicked(String year, String month, String day)
            {
                showDateView.setText(year + "-" + month + "-" + day);
                itvClearTime.setVisibility(View.VISIBLE);
            }
        });
        picker.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("requestCode:" + requestCode + ",resultCode:" + resultCode);
        switch (requestCode)
        {
            case CHOOSE_RES:
                if (resultCode == RESULT_OK)
                {
                    if (!TextUtils.isEmpty(data.getStringExtra("resName")))
                        tvResName.setText(data.getStringExtra("resName"));
                    itvClearRes.setVisibility(View.VISIBLE);
                    this.goResId = data.getStringExtra("goResId");
                }
                break;
            default:
                break;
        }
    }
}
