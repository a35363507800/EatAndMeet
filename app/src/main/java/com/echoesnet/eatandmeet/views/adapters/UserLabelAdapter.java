package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/5/6.
 */
public class UserLabelAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;

    public UserLabelAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    int textBackgrount[]=new int[]{R.drawable.tvlabel_bg_0311,
            R.drawable.tvlabel_bg_0312,
            R.drawable.tvlabel_bg_0313,
            R.drawable.tvlabel_bg_0315,
            R.drawable.tvlabel_bg_0412};
    int textColor[] =new int[]{R.color.C0311,R.color.C0312,R.color.C0313,R.color.C0315,R.color.C0412};

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.user_label_item, parent, false);
            viewHolder.tv_label = (TextView) convertView.findViewById(R.id.tv_label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tv_label.setText(list.get(position));
        int number=getRandomNumber();
        viewHolder.tv_label.setBackgroundResource(textBackgrount[number]);
        viewHolder.tv_label.setTextColor(ContextCompat.getColor(context,textColor[number]));

        return convertView;
    }

    class ViewHolder {
        private TextView tv_label;
    }

    private int getRandomNumber()
    {
        return new Random().nextInt(4);
    }
}
