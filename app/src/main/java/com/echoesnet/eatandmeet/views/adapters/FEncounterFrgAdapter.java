package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.EncounterBean;
import com.echoesnet.eatandmeet.models.datamodel.ImageDisposalType;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by an on 2017/3/29 0029.
 */

public class FEncounterFrgAdapter extends BaseAdapter
{
    private static final String TAG = FEncounterFrgAdapter.class.getSimpleName();
    private List<EncounterBean> mData;
    private Activity mActivity;
    private OnItemClickListener itemClick;


    public FEncounterFrgAdapter(List<EncounterBean> mData, Activity mActivity)
    {
        this.mData = mData;
        this.mActivity = mActivity;
    }

    public void setOnItemClickListener(OnItemClickListener itemClick)
    {
        this.itemClick = itemClick;
    }

    @Override
    public int getCount()
    {
        return mData.size();
    }

    @Override
    public EncounterBean getItem(int position)
    {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        String oldUrl = null;
        EncounterBean itemBean = mData.get(position);
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mActivity).inflate(R.layout.frg_encounter_item, parent, false);
            viewHolder.encounterLivingImg = (ImageView) convertView.findViewById(R.id.img_encounter_living);
            viewHolder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            viewHolder.sexImg = (GenderView) convertView.findViewById(R.id.img_encounter_sex);
            viewHolder.userNameTv = (TextView) convertView.findViewById(R.id.tv_encounter_name);
            viewHolder.userHeadImg = (RoundedImageView) convertView.findViewById(R.id.img_encounter_head);
            viewHolder.userLookYou = (TextView) convertView.findViewById(R.id.tv_look_you);
            viewHolder.userDistance = (TextView) convertView.findViewById(R.id.tv_distance);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
            oldUrl = (String) viewHolder.userHeadImg.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (itemClick != null)
                    itemClick.itemClick(position,itemBean);
            }
        });

        viewHolder.userDistance.setText(TextUtils.isEmpty(itemBean.getDistance()) ? "火星" : itemBean.getDistance());

        if (itemBean.getLookTime().equals(""))
        {
            viewHolder.userLookYou.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.userLookYou.setVisibility(View.VISIBLE);
            viewHolder.userLookYou.setText(itemBean.getLookTime() + "看过你");
        }


        String name = !TextUtils.isEmpty(itemBean.getRemark()) ? itemBean.getRemark() : itemBean.getNicName();
        if (name != null && name.length() > 4)
            name = (name.substring(0, 4) + "...");
        viewHolder.userNameTv.setText(name);
        if (!(itemBean.getPhUrl()).equals(oldUrl))
        {
            String urlByUCloud = CommonUtils.getThumbnailImageUrlByUCloud(itemBean.getPhUrl(), ImageDisposalType.THUMBNAIL, 8, 118, 118);
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(urlByUCloud)
                    .placeholder(R.drawable.qs_head_rect)
                    .error(R.drawable.qs_head_rect)
                    .centerCrop()
                    .into(viewHolder.userHeadImg);
        }
        viewHolder.levelView.setLevel(itemBean.getLevel(), 1);
        viewHolder.userHeadImg.setTag(itemBean.getPhUrl());

        viewHolder.sexImg.setSex(itemBean.getAge(), itemBean.getSex());
        if ("1".equals(itemBean.getLiving()))
        {
            viewHolder.encounterLivingImg.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.encounterLivingImg.setVisibility(View.GONE);
        }
        return convertView;
    }

    class ViewHolder
    {
        private RoundedImageView userHeadImg;
        private LevelView levelView;
        private ImageView encounterLivingImg;
        private GenderView sexImg;
        private TextView userNameTv;
        private TextView userDistance;
        private TextView userLookYou;
    }

   public interface OnItemClickListener
    {
        void itemClick(int position,EncounterBean itemBean);
    }
}
