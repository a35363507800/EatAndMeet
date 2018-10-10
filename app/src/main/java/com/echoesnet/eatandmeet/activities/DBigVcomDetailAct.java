package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.eventmsgs.BigVcommentMsg;
import com.echoesnet.eatandmeet.presenters.ImpDBigVcomDetailView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDBigVcomDetailView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.ImageIndicatorView.NetworkImageIndicatorView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.panxw.android.imageindicator.AutoPlayManager;
import com.panxw.android.imageindicator.ImageIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class DBigVcomDetailAct extends MVPBaseActivity<IDBigVcomDetailView, ImpDBigVcomDetailView> implements IDBigVcomDetailView
{
    public final static String TAG = DBigVcomDetailAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.icv_cycle_view)
    NetworkImageIndicatorView mImageCycleView;
    @BindView(R.id.iv_bigv_detail_support_btn)
    IconTextView ivSupportIcon;
    @BindView(R.id.riv_bigv_detail_head)
    LevelHeaderView ivHeadImg;
    @BindView(R.id.iv_bigv_detail_close_btn)
    ImageView ivClose;
    @BindView(R.id.tv_bigv_comment)
    TextView tvBigvComment;
    @BindView(R.id.iv_lv)
    LevelView ivLv; //等级标示
    @BindView(R.id.iv_sex)
    ImageView ivSex; //性别显示
    //    @ViewById(R.id.tv_bigv_title)
//    TextView tvBigvTitle;
    @BindView(R.id.tv_bigv_detail_username)
    TextView tvBigvNickName;
    @BindView(R.id.tv_bigv_detail_support)
    TextView tvPraiseNum;
    @BindView(R.id.rating_bar)
    CustomRatingBar ratingBar;

    private Activity mContext;
    private Dialog pDialog;
    private String isPraise = "0", bigVuId = "";
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bigv_com_detail);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    protected ImpDBigVcomDetailView createPresenter()
    {
        return new ImpDBigVcomDetailView();
    }

    private void initAfterViews()
    {
        mContext = this;
        ratingBar.setIndicator(true);
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);

    }

    @Subscribe(sticky = true)
    public void onMsgReceived(BigVcommentMsg bigcMsg)
    {
        Logger.t(TAG).d(bigcMsg.getBigVcommentBean().toString());
        int rating = bigcMsg.getBigVcommentBean().getRating();
        ratingBar.setRatingBar(rating);
        tvBigvComment.setText(bigcMsg.getBigVcommentBean().getComment());
        //tvBigvTitle.setText(bigcMsg.getBigVcommentBean().getTitle());
        tvBigvNickName.setText(bigcMsg.getBigVcommentBean().getNickName());
        ivHeadImg.setHeadImageByUrl(bigcMsg.getBigVcommentBean().getUserHeadImg());
        ivHeadImg.setLevel(bigcMsg.getBigVcommentBean().getLevel());
      /*  GlideApp.with(mContext)
                .load(bigcMsg.getBigVcommentBean().getUserHeadImg())
                .asBitmap()
                .centerCrop()
                .placeholder(R.drawable.userhead)
                .into(ivHeadImg);*/
        //获得性别显示
        String sex = bigcMsg.getBigVcommentBean().getSex();
        if (!TextUtils.isEmpty(sex))
        {
            if (sex.equals("男"))
            {
                ivSex.setImageResource(R.drawable.man_1_xxhdpi);
            } else
            {
                ivSex.setImageResource(R.drawable.women_1_xxhdpi);
            }
        }
        //获得等级显示
        ivLv.setLevel(bigcMsg.getBigVcommentBean().getLevel(), LevelView.USER);
        initCycleViewData(bigcMsg.getBigVcommentBean().getCommentImgUrlLst());
        //获得赞数
        bigVuId = bigcMsg.getBigVcommentBean().getuId();
        if (mPresenter != null)
            mPresenter.getBigVSupportCount(bigVuId);
    }

    @OnClick({R.id.iv_bigv_detail_close_btn, R.id.ll_commit})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_bigv_detail_close_btn:
                this.finish();
                break;
            case R.id.ll_commit:
                if (mPresenter != null)
                    mPresenter.supportBigV(bigVuId);
                break;
            default:
                break;
        }
    }

    private void initCycleViewData(List<String> urlList)
    {
        if (urlList == null)
        {
            urlList = new ArrayList<>();
            urlList.add("http://huisheng.ufile.ucloud.com.cn/test/120101000201.jpg");
        }

        mImageCycleView.setupLayoutByImageUrl(urlList);
        mImageCycleView.show();
        mImageCycleView.setOnItemClickListener(new ImageIndicatorView.OnItemClickListener()
        {
            @Override
            public void OnItemClick(View view, int position)
            {

            }
        });
        AutoPlayManager autoBrocastManager = new AutoPlayManager(mImageCycleView);
        autoBrocastManager.setBroadcastEnable(true);
        autoBrocastManager.setBroadCastTimes(10000);//loop times
        autoBrocastManager.setBroadcastTimeIntevel(2 * 1000, 2 * 1000);//设置第一次展示时间以及间隔，间隔不能小于1秒
        autoBrocastManager.loop();
    }

    private void setUiContent(String praiseCount, String isPraise)
    {
        tvPraiseNum.setText(String.format("%s 人", praiseCount));
        if (!TextUtils.isEmpty(isPraise))
        {
            if (isPraise.equals("1"))
            {
                ivSupportIcon.setText("{eam-n-praise}");
                ivSupportIcon.setTextColor(Color.parseColor("#9d45f9"));
            } else
            {
                ivSupportIcon.setText("{eam-p-praise}");
                ivSupportIcon.setTextColor(Color.parseColor("#999999"));
            }

        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, "", exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getBigVSupportCountCallBack(String response)
    {
        Logger.t(TAG).d("返回结果1》" + response);
        try
        {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject jsonStarLike = jsonResponse.getJSONObject("starLike");
            setUiContent(jsonStarLike.getString("amount"), jsonStarLike.getString("praise"));
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void supportBigVCallBack(String response)
    {
        Logger.t(TAG).d("返回结果2》" + response);
        try
        {
            JSONObject jsonResponse = new JSONObject(response);
            JSONObject jsonStarSupport = jsonResponse.getJSONObject("starLike");
            setUiContent(jsonStarSupport.getString("amount"), jsonStarSupport.getString("praise"));
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
