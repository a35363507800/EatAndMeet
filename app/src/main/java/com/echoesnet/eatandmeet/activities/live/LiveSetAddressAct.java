package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.presenters.ImpISetAddressView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISetAddressView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.LiveSetAddressPopup;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by yqh on 2017/2/23.
 */
public class LiveSetAddressAct extends MVPBaseActivity<ISetAddressView, ImpISetAddressView> implements ISetAddressView
{
    private static final String TAG = LiveSetAddressAct.class.getSimpleName();
    private Activity mAct;
    @BindView(R.id.ll_choose_area)
    AutoRelativeLayout llChooseArea;
    @BindView(R.id.ll_choose_road)
    AutoRelativeLayout llChooseRoad;
    @BindView(R.id.tv_area)
    TextView tvArea;
    @BindView(R.id.tv_road)
    TextView tvRoad;
    @BindView(R.id.top_bar)
    TopBarSwitch topBarSwitch;

    private String areaAddress = "";
    private String[] posXy = null;

    private LiveSetAddressPopup liveSetAddressPopup;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_set_address_act);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    private void initAfterView()
    {
        mAct = this;

        TextView textView = topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                if (posXy == null && TextUtils.isEmpty(areaAddress))
                {
                    ToastUtils.showShort("请选择地区");
                    return;
                }
                if (tvRoad.getText().toString().contains("请选择"))
                {
                    ToastUtils.showShort("请选择街道");
                    return;
                }
                String[] result = areaAddress.split(CommonUtils.SEPARATOR);
                if (mPresenter != null)
                    mPresenter.setPermanent(result[0], result[1], result[2], tvRoad.getText().toString(), posXy[0], posXy[1]);
            }
        });
        List<TextView> navBtns = topBarSwitch.getNavBtns(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i);
            if (i == 1)
            {
                tv.setText("保存");
                tv.setTextSize(16);
            }
        }
        textView.setText("设置常驻位置");
    }

    @OnClick({R.id.ll_choose_area, R.id.ll_choose_road})
    void clickEvent(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_choose_area:
                showAddressPopup(0);
                break;
            case R.id.ll_choose_road:
                showAddressPopup(1);
                break;
            default:
                break;
        }
    }

    private void showAddressPopup(int type)
    {
        if (type == 1)
        {
            if (tvArea.getText().toString().contains("请选择"))
            {
                ToastUtils.showShort( "请先选择地区");
                return;
            }
        }
        if (liveSetAddressPopup == null)
            liveSetAddressPopup = new LiveSetAddressPopup(mAct);
        liveSetAddressPopup.switchData(type, areaAddress);
        liveSetAddressPopup.setOnSelectFinishListener(itemFinishListener);
        liveSetAddressPopup.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                liveSetAddressPopup.setBackgroundAlpha(1f);
            }
        });
        liveSetAddressPopup.setBackgroundAlpha(0.5f);
        liveSetAddressPopup.showAtLocation(findViewById(R.id.ll_choose_area), Gravity.BOTTOM, 0, 0);
    }

    LiveSetAddressPopup.ISelectedAreaFinishListener itemFinishListener = new LiveSetAddressPopup.ISelectedAreaFinishListener()
    {
        @Override
        public void selectedArea(String province, String city, String area)
        {
            tvArea.setText(String.format("%s %s %s ", province, city, area));
            areaAddress = province + CommonUtils.SEPARATOR + city + CommonUtils.SEPARATOR + area;
            tvRoad.setText("请选择");
        }

        @Override
        public void selectedRoad(String roadName, String position)
        {
            Logger.t(TAG).d("roadName:" + roadName + " | position:" + position);
            tvRoad.setText(roadName);
            posXy = position.split(",");
        }
    };

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
    }

    @Override
    public void setPermanentCallback(String response)
    {
        ToastUtils.showShort("保存成功");
        Intent intent = new Intent(mAct, LiveReadyActivity.class);
        intent.putExtra("address", tvArea.getText().toString() + " " + tvRoad.getText().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected ImpISetAddressView createPresenter()
    {
        return new ImpISetAddressView();
    }
}
