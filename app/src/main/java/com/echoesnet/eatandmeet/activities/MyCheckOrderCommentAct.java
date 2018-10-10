package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.MResDishCommentAdapter;
import com.echoesnet.eatandmeet.views.adapters.ResCommentImgsAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.linearlistview.LinearListView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCheckOrderCommentAct extends BaseActivity
{
    private final static String TAG = MyCheckOrderCommentAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.et_refund_reason)
    TextView tvResComment;
    @BindView(R.id.rv_comment_imgs)
    RecyclerView rcvCommentImgs;
    //菜品列表
    @BindView(R.id.lv_res_dish_lst)
    LinearListView llvDishComments;
    @BindView(R.id.rateBar)
    CustomRatingBar ratingBar;
    @BindView(R.id.all_dish)
    AutoLinearLayout allDish;

    private Activity mContext;
    private ResCommentImgsAdapter mResCommentAdapter;
    private List<String> imgLst;
    private MResDishCommentAdapter mResDishCommentAdapter;
    private Dialog pDialog;

    private List<DishBean> dishLst;
    private String orderType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_check_order_comment);
        ButterKnife.bind(this);
        initAfterViews();
    }

    private void initAfterViews()
    {
        mContext = this;
        topBar.setTitle("餐厅评价");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });

        // 区分正常订单和闪付订单
        orderType = getIntent().getStringExtra("orderType");
        Logger.t(TAG).d("订单列表传的标识类型--> " + orderType);

        if ("quickType".equals(orderType))
        {
            allDish.setVisibility(View.GONE);
            Logger.t(TAG).d("11111--> " + orderType);
        }
        else
        {
            allDish.setVisibility(View.VISIBLE);
            Logger.t(TAG).d("22222--> " + orderType);
        }

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCommentImgs.setLayoutManager(linearLayoutManager);
        imgLst = new ArrayList<>();
        //设置适配器
        mResCommentAdapter = new ResCommentImgsAdapter(mContext, imgLst);
        mResCommentAdapter.setOnItemClickListener(new ResCommentImgsAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                CommonUtils.showImageBrowser(mContext, imgLst, position, view);
            }
        });
        rcvCommentImgs.setAdapter(mResCommentAdapter);

        dishLst = new ArrayList<>();
        mResDishCommentAdapter = new MResDishCommentAdapter(mContext, dishLst, "show");
        llvDishComments.setAdapter(mResDishCommentAdapter);

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(true);

        setViewContent((MyResCommentBean) getIntent().getSerializableExtra("commentInfo"));
    }

    private void setViewContent(MyResCommentBean commentBean)
    {
        topBar.setTitle(commentBean.getResName());
        tvResComment.setText(commentBean.getEvalContent());
        if (!TextUtils.isEmpty(commentBean.getEpUrls()))
        {
            String imgUrls[] = commentBean.getEpUrls().split(CommonUtils.SEPARATOR);
            for (int i = 0; i < imgUrls.length; i++)
            {
                imgLst.add(imgUrls[i]);
                //imgLst.add(Uri.parse(imgUrls[i]));
            }
        }
        mResCommentAdapter.notifyDataSetChanged();

        dishLst.addAll(commentBean.getDishLevel());
        mResDishCommentAdapter.notifyDataSetChanged();

        ratingBar.setIndicator(true);
        ratingBar.setRatingBar(Integer.parseInt(commentBean.getrStar()));

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        pDialog = null;
    }

}
