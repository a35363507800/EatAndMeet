package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.WhoSeenMeBean;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class WhoSeenMeAdapter extends BaseAdapter
{
    private static final String TAG = WhoSeenMeAdapter.class.getSimpleName();
    // 填充数据的list
    private List<WhoSeenMeBean> list;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public WhoSeenMeAdapter(List<WhoSeenMeBean> list, Context context)
    {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        if (convertView == null)
        {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.item_who_seen_me, null);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_info = (TextView) convertView.findViewById(R.id.tv_info);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            holder.riv_head = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
            //添加等级 年龄
            holder.ll_level = (LevelView) convertView.findViewById(R.id.ll_level);
            holder.itv_age = (IconTextView) convertView.findViewById(R.id.itv_age);
            // 为view设置标签
            convertView.setTag(holder);
        }
        else
        {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
         WhoSeenMeBean bean = list.get(position);
        if (TextUtils.isEmpty(bean.getRemark()))
        {
            holder.tv_title.setText(bean.getNicName());
            holder.tv_info.setText(bean.getNicName() + "查看了你的个人信息");
        }
        else
        {
            holder.tv_title.setText(bean.getRemark());
            holder.tv_info.setText(bean.getRemark() + "查看了你的个人信息");
        }
        holder.tv_time.setText(bean.getvTime());
        holder.riv_head.setLiveState(false);
        holder.riv_head.setHeadImageByUrl(bean.getUphUrl());
        holder.riv_head.setLevel(bean.getLevel());

        holder.ll_level.setLevel(bean.getLevel(),LevelView.USER);
        if("男".equals(bean.getSex())){
            holder.itv_age.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.itv_age.setText(String.format("%s %s", "{eam-e950}",bean.getAge()));
        }else {
            holder.itv_age.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.itv_age.setText(String.format("%s %s", "{eam-e94f}",bean.getAge()));
        }


        return convertView;
    }

    public class ViewHolder
    {
        public LevelHeaderView riv_head;
        public TextView tv_title;
        public TextView tv_info;
        public TextView tv_time;
        public LevelView ll_level;
        public IconTextView itv_age;
    }
}
