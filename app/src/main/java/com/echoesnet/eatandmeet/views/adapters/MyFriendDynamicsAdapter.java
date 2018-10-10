package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyFriendDynamicsBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.HorizontalListView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MyFriendDynamicsAdapter extends BaseAdapter
{
    private static final String TAG = MyFriendDynamicsAdapter.class.getSimpleName();
    private List<MyFriendDynamicsBean> list;
    private Context context;
    private int maxDescripLine = 3;
    private MyInfoPhotoAdapter adapter;
    private ArrayList<String> bitmapPath;
    private String imgUrl[];

    public MyFriendDynamicsAdapter(Context context, List<MyFriendDynamicsBean> list)
    {
        this.context = context;
        this.list = list;
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
        final ViewHolder viewHolder;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_myfriend_dynamics, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.ll_horizontal_listview = (AutoLinearLayout) convertView.findViewById(R.id.ll_horizontal_listview);
            viewHolder.tv_dynamics_address = (TextView) convertView.findViewById(R.id.tv_dynamics_address);
            viewHolder.riv_head = (LevelHeaderView) convertView.findViewById(R.id.riv_head);
            viewHolder.lv_level = (LevelView) convertView.findViewById(R.id.ll_level);
            viewHolder.itv_age = (IconTextView) convertView.findViewById(R.id.itv_age);
            viewHolder.crb_rating_bar = (CustomRatingBar) convertView.findViewById(R.id.crb_rating_bar);
            viewHolder.all_content = ((ExpandableTextView) convertView.findViewById(R.id.all_content));
            viewHolder.hlv_imgs = (HorizontalListView) convertView.findViewById(R.id.hlv_imgs);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (TextUtils.isEmpty(list.get(position).getRemark()))
        {
            viewHolder.tv_name.setText(list.get(position).getNicName());
        }
        else
        {
            viewHolder.tv_name.setText(list.get(position).getRemark());
        }
        //添加等级 年龄
        viewHolder.lv_level.setLevel(list.get(position).getLevel(),LevelView.USER);

        if ("男".equals(list.get(position).getSex())){
            viewHolder.itv_age.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            viewHolder.itv_age.setText(String.format("%s %s", "{eam-e950}",list.get(position).getAge()));
        }else {
            viewHolder.itv_age.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            viewHolder.itv_age.setText(String.format("%s %s", "{eam-e94f}",list.get(position).getAge()));
        }
        String time = list.get(position).getDate().substring(0, 10);
        Date date = null;
        try
        {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            String resDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            viewHolder.tv_time.setText(resDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        Logger.t(TAG).d("list.get(position).getEvalContent()  >" + list.get(position).getEvalContent());
        viewHolder.all_content.setText(list.get(position).getEvalContent());
        viewHolder.tv_dynamics_address.setText(list.get(position).getrAddr());
        viewHolder.crb_rating_bar.setIndicator(true);
        viewHolder.crb_rating_bar.setRatingBar(list.get(position).getrStar());
        viewHolder.riv_head.setLiveState(false);
        viewHolder.riv_head.setHeadImageByUrl(list.get(position).getPhUrl());
        viewHolder.riv_head.setLevel(list.get(position).getLevel());


        imgUrl = list
                .get(position)
                .getEpUrls()
                .split(CommonUtils.SEPARATOR);
        Logger.t(TAG).d("EpUrls:" + list.get(position).getEpUrls());
        if (list.get(position).getEpUrls().equals("") || list.get(position).getEpUrls() == null)
        {
            Logger.t(TAG).d("设置为GONE");
            viewHolder.ll_horizontal_listview.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.ll_horizontal_listview.setVisibility(View.VISIBLE);
            adapter = new MyInfoPhotoAdapter(context, imgUrl);
            viewHolder.hlv_imgs.setAdapter(adapter);
        }


        viewHolder.hlv_imgs.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int positions, long id)
            {
                Logger.t(TAG).d(positions + " " + position);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        bitmapPath = new ArrayList<>();
                        imgUrl = list.get(position).getEpUrls().split("!=end=!");

                        for (int i = 0; i < imgUrl.length; i++)
                        {
                            String path = imgUrl[i];
                            bitmapPath.add(path);
                        }
                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("postions", positions);
//                        bundle.putStringArrayList("list", bitmapPath);
                        message.setData(bundle);
                        message.what = 1;
                        message.obj = view;
                        mHandler.sendMessage(message);
                    }
                }).start();
            }
        });
        return convertView;
    }

    class ViewHolder
    {
        public TextView tv_name;
        public TextView tv_time;
        public TextView tv_dynamics_address;
        public LevelHeaderView riv_head;
        public CustomRatingBar crb_rating_bar;
        public ExpandableTextView all_content;
        public HorizontalListView hlv_imgs;
        public AutoLinearLayout ll_horizontal_listview;
        public LevelView lv_level;
        public IconTextView itv_age;
    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    CommonUtils.showImageBrowser(context, bitmapPath, msg.getData().getInt("postions"), (View) msg.obj);
/*                    Intent intent = new Intent(context, MyFriendDynImageActivity.class);
                    intent.putExtra("ID", msg.getData().getInt("postions"));
                    intent.putStringArrayListExtra("path", bitmapPath);
                    context.startActivity(intent);*/
                    break;
            }
        }
    };
}
