package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInfoSystemRemindBean;
import com.echoesnet.eatandmeet.utils.IMUtils.TXConstants;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.util.ConvertUtils;

/**
 * Created by Administrator on 2016/7/18.
 */
public class MyInfoSystemRemindAdapter extends BaseAdapter
{
    private List<MyInfoSystemRemindBean> list;
    private Context context;
    private IgnoreOrBindClickListener ignoreOrBindClickListener;
    private ViewHolder viewHolder;

    public MyInfoSystemRemindAdapter(Context context, List<MyInfoSystemRemindBean> list)
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

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.litem_system_remind, null);
            viewHolder.tv_time_date = (TextView) convertView.findViewById(R.id.tv_time_date);
            viewHolder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
            viewHolder.btn_ignore = (Button) convertView.findViewById(R.id.btn_ignore);
            viewHolder.btn_bind = (Button) convertView.findViewById(R.id.btn_bind);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final MyInfoSystemRemindBean bean = list.get(position);
        String time = bean.getDate();
        Date date = null;
        try
        {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            String resDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            viewHolder.tv_time_date.setText(resDate.substring(0, resDate.length() - 3));
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        StringBuilder data=new StringBuilder();
        if(TextUtils.isEmpty(bean.getMap().getNicName()))
        {
            data.append(String.format("<font color=%s>%s</font>",ContextCompat.getColor(context,R.color.black),bean.getMsg()));
        }
        else
        {
            try
            {
                data.append(String.format("<font color=%s>%s</font>",
                        ContextCompat.getColor(context,R.color.black),
                        bean.getMsg().substring(0,bean.getMsg().indexOf(bean.getMap().getNicName()))));

                data.append(String.format("<font color=%s>%s</font>",
                        ContextCompat.getColor(context,R.color.C0313),
                        bean.getMap().getNicName()+" (ID: "+bean.getMap().getId()+")"));

                data.append(String.format("<font color=%s>%s</font>",
                        ContextCompat.getColor(context,R.color.black),
                        bean.getMsg().substring(bean.getMsg().indexOf(bean.getMap().getId())+bean.getMap().getId().length()+1,bean.getMsg().length())));
            } catch (Exception e)
            {
                Logger.t("MyInfoSystemRemindAdapter").d(e.getMessage());
                e.printStackTrace();
                data.append(String.format("<font color=%s>%s</font>",ContextCompat.getColor(context,R.color.black),bean.getMsg()));
            }
        }
        viewHolder.tv_context.setText(Html.fromHtml(data.toString()));

        if (bean.getTip().equals("CONSULTANT_INVALID_SOON") || bean.getTip().equals("CONSULTANT_BIND_INVALID"))
        {
            viewHolder.btn_ignore.setVisibility(View.VISIBLE);
            viewHolder.btn_bind.setVisibility(View.VISIBLE);
        }
        else
        {
            viewHolder.btn_ignore.setVisibility(View.GONE);
            viewHolder.btn_bind.setVisibility(View.GONE);
        }
        viewHolder.btn_ignore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ignoreOrBindClickListener != null)
                    ignoreOrBindClickListener.IgnoreBtnClick(bean);
            }
        });
        viewHolder.btn_bind.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ignoreOrBindClickListener != null)
                    ignoreOrBindClickListener.BindBtnClick(bean);
            }
        });
        return convertView;
    }

    class ViewHolder
    {
        TextView tv_time_date;
        TextView tv_context;
        Button btn_ignore;
        Button btn_bind;
    }

    public void setIgnoreOrBindClickListener(IgnoreOrBindClickListener ignoreOrBindClickListener)
    {
        this.ignoreOrBindClickListener = ignoreOrBindClickListener;
    }

    public interface IgnoreOrBindClickListener
    {
        void IgnoreBtnClick(MyInfoSystemRemindBean bean);

        void BindBtnClick(MyInfoSystemRemindBean bean);
    }

}
