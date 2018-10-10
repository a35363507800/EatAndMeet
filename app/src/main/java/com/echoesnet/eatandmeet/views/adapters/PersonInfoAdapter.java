package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DOrderMealDetailAct;
import com.echoesnet.eatandmeet.activities.DOrderRecordDetail;
import com.echoesnet.eatandmeet.models.bean.CUserInfoBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lc on 2017/7/25 09.
 */

public class PersonInfoAdapter extends RecyclerView.Adapter
{
    private CUserInfoBean bean;
    private Activity mActivity;
    private String[] arr = {"性别", "身高", "情感状态", "星座", "生日", "学历", "职业", "月收入", "上次出现过", "个性签名"};

    public PersonInfoAdapter(Activity mActivity, CUserInfoBean bean)
    {
        this.mActivity = mActivity;
        this.bean = bean;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_user_info, parent, false);
        PersonInfoAdapter.UserInfoViewHolder viewHolder = new PersonInfoAdapter.UserInfoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        PersonInfoAdapter.UserInfoViewHolder viewHolder = (PersonInfoAdapter.UserInfoViewHolder) holder;
        if (bean != null)
        {
            switch (position)
            {
                case 0:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getSex()) ? "保密" : bean.getSex());
                    break;
                case 1:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getHeight()) ? "保密" : bean.getHeight());
                    break;
                case 2:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getEmState()) ? "保密" : bean.getEmState());
                    break;
                case 3:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getConstellation()) ? "保密" : bean.getConstellation());
                    break;
                case 4:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getBirth()) ? "保密" : bean.getBirth());
                    break;
                case 5:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getEducation()) ? "保密" : bean.getEducation());
                    break;
                case 6:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getOccupation()) ? "保密" : bean.getOccupation());
                    break;
                case 7:
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getIncome()) ? "保密" : bean.getIncome());
                    break;
                case 8:
                    if (bean.getrName()!=null)
                    {
                       String rName =  bean.getrName();
                        if(rName.length()>14)
                            rName=(rName.substring(0,14)+"...");
                        viewHolder.userContent.setText(TextUtils.isEmpty(rName) ? "还没有定过餐哦" :rName);
                        if (TextUtils.isEmpty(rName))
                        {
                            viewHolder.userContent.setTextColor(ContextCompat.getColor(mActivity, R.color.C0322));
                            viewHolder.userContent.setClickable(false);
                        }
                        else
                        {
                            viewHolder.userContent.setTextColor(ContextCompat.getColor(mActivity, R.color.C0412));
                            viewHolder.userContent.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent intent = new Intent(mActivity, DOrderMealDetailAct.class);
                                    intent.putExtra("restId", bean.getrId());
                                    mActivity.startActivity(intent);
                                }
                            });
                        }
                    }


                    break;
                case 9:
                    LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) viewHolder.userContent.getLayoutParams();
                    param.setMargins(0, CommonUtils.dp2px(mActivity,7),0 ,CommonUtils.dp2px(mActivity,7));
                    viewHolder.userContent.setLayoutParams(param);
                    viewHolder.userContent.setText(TextUtils.isEmpty(bean.getSignature()) ? "这家伙很懒，什么都没有留下哦~" : bean.getSignature());
                    break;
                default:
                    break;

            }
        }
        viewHolder.userTitle.setText(arr[position]);
    }

    @Override
    public int getItemCount()
    {
        return arr.length;
    }


    public class UserInfoViewHolder extends RecyclerView.ViewHolder
    {
        private TextView userTitle;
        private TextView userContent;


        public UserInfoViewHolder(View itemView)
        {
            super(itemView);
            userTitle = (TextView) itemView.findViewById(R.id.tv_title);
            userContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
