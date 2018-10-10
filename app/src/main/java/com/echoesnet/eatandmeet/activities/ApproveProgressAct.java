package com.echoesnet.eatandmeet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.listeners.ContactPopupDismissListener;
import com.echoesnet.eatandmeet.presenters.ImplApproveProgressPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IApproveProgressView;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.MySetContactUsPopup;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/14.
 */

public class ApproveProgressAct extends MVPBaseActivity<IApproveProgressView, ImplApproveProgressPre> implements IApproveProgressView
{
    private static final String TAG = MWhoSeenMeAct.class.getSimpleName();

    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.iv_1)
    ImageView ivOne;
    @BindView(R.id.iv_2)
    ImageView ivTwo;
    @BindView(R.id.iv_3)
    ImageView ivThr;
    @BindView(R.id.tv_1)
    TextView tvOne;
    @BindView(R.id.tv_2)
    TextView tvTwo;
    @BindView(R.id.tv_3)
    TextView tvThr;
    @BindView(R.id.tv_4_1)
    TextView tvFourO;
    @BindView(R.id.tv_4_2)
    TextView tvFourT;
    @BindView(R.id.tv_4_3)
    TextView tvFourTh;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.phone_layout)
    LinearLayout llPhone;
    @BindView(R.id.bt_submit)
    Button submit;


    private Map<String, String> mData;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_approve_progress);
        ButterKnife.bind(this);

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
        }).setText("实名认证");
        mData = new HashMap<>();
        mPresenter.getReal();
        mPresenter.getCompContact();
    }

    @OnClick({R.id.tv_phone, R.id.bt_submit})
    void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_phone:
                MySetContactUsPopup contactUsPopup = new MySetContactUsPopup(this, source);
                contactUsPopup.setOnDismissListener(new ContactPopupDismissListener(contactUsPopup));
                contactUsPopup.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                break;
            case R.id.bt_submit:
                Intent intent = new Intent(this, MyAuthenticationAct.class);
                ArrayList list = new ArrayList();
                list.add(mData);
                intent.putParcelableArrayListExtra("userAuthenticationState", list);
                intent.putExtra("openSource", "alipay");
                startActivityForResult(intent, IdentityAuthAct.GO_TO_MY_AUTHENTICATION);
                break;
            default:

                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected ImplApproveProgressPre createPresenter()
    {
        return new ImplApproveProgressPre(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mPresenter.getReal();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void getRealCallBack(String response)
    {
        try
        {
            JSONObject jb = new JSONObject(response);
            String realName = jb.getString("realName");
            String idCard = jb.getString("idCard");
            String rmAnFlg = jb.getString("rmAnFlg");
            String rejectReason = jb.getString("rejectReason");
            String idCardUrl = jb.getString("idCardUrl");
            String posPhUrl = jb.getString("posPhUrl");
            String alipayFlg = jb.getString("alipayFlg");
            String swap = jb.getString("swap");

            mData.put("realName", realName);
            mData.put("idCard", idCard);
            mData.put("idCardUrl", idCardUrl);
            mData.put("posPhUrl", posPhUrl);
            mData.put("rmAnFlg", rmAnFlg);
            mData.put("rejectReason", rejectReason);
            mData.put("alipayFlg", alipayFlg);
            mData.put("swap", swap);

            tvOne.setVisibility(View.GONE);
            tvTwo.setVisibility(View.GONE);
            tvThr.setVisibility(View.GONE);
            tvFourO.setVisibility(View.GONE);
            tvFourT.setVisibility(View.GONE);
            tvFourTh.setVisibility(View.GONE);
            llPhone.setVisibility(View.GONE);
            submit.setVisibility(View.GONE);

            switch (rmAnFlg)
            {
                case "0":

                    break;
                case "1":
                    tvOne.setVisibility(View.VISIBLE);
                    tvOne.setText("您的实名认证正在审核中");
                    tvTwo.setVisibility(View.VISIBLE);
                    tvTwo.setText("请耐心等待...");

                    ivOne.setImageResource(R.drawable.wode_ico_yitijiao);
                    ivTwo.setImageResource(R.drawable.wode_ico_shenhezhong);
                    ivThr.setImageResource(R.drawable.wode_ico_ready);
                    llPhone.setVisibility(View.VISIBLE);
                    break;
                case "2":
                    tvOne.setVisibility(View.VISIBLE);
                    tvOne.setText("您提交的信息");
                    tvTwo.setVisibility(View.VISIBLE);
                    tvTwo.setText(realName + " (" + idCard + ")");
                    tvThr.setVisibility(View.VISIBLE);
                    tvThr.setText("实名认证成功");
                    tvThr.setTextColor(ContextCompat.getColor(this, R.color.C0315));

                    ivOne.setImageResource(R.drawable.wode_ico_yitijiao);
                    ivTwo.setImageResource(R.drawable.wode_ico_shenhezhong);
                    ivThr.setImageResource(R.drawable.wode_ico_chenggong);
                    break;
                case "3":
                    tvOne.setVisibility(View.VISIBLE);
                    tvOne.setText("您提交的信息");
                    tvTwo.setVisibility(View.VISIBLE);
                    tvTwo.setText(realName + " (" + idCard + ")");
                    tvThr.setVisibility(View.VISIBLE);
                    tvThr.setText("实名认证失败");
                    tvThr.setTextColor(ContextCompat.getColor(this, R.color.C0313));
                    tvFourO.setVisibility(View.VISIBLE);
                    tvFourO.setText("原因是");
                    tvFourT.setVisibility(View.VISIBLE);
                    tvFourT.setText(rejectReason + ",");
                    tvFourT.setTextColor(ContextCompat.getColor(this, R.color.C0313));
                    tvFourTh.setVisibility(View.VISIBLE);
                    tvFourTh.setText("请重新上传");


                    ivOne.setImageResource(R.drawable.wode_ico_yitijiao);
                    ivTwo.setImageResource(R.drawable.wode_ico_shenhezhong);
                    ivThr.setImageResource(R.drawable.wode_ico_shibai);

                    submit.setVisibility(View.VISIBLE);
                    break;
                case "4":
                    break;
                default:
                    break;

            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private List<HashMap<String, String>> source;

    @Override
    public void getContactCallback(String str)
    {
        try
        {
            JSONObject body = new JSONObject(str);
            Logger.t(TAG).d("body>>" + body.toString());
            source = new ArrayList<>();
            JSONArray hotlines = body.getJSONArray("hotline");
            JSONArray contacts = body.getJSONArray("contact");
            for (int i = 0; i < contacts.length(); i++)
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("type", "contact");
                map.put("content", contacts.getString(i));
                source.add(map);
            }
            for (int i = 0; i < hotlines.length(); i++)
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("type", "hotline");
                map.put("content", hotlines.getString(i));
                tvPhone.setText(hotlines.getString(i));
                source.add(map);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}
