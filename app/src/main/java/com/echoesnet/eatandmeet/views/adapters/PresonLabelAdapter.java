package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.HashMap;
import java.util.List;

public class PresonLabelAdapter extends BaseAdapter
{
    public static final String TAG = PresonLabelAdapter.class.getSimpleName();
    private Context context;
    //    public List<Boolean> mChecked;
    private List<String> listPerson, selectList;
    HashMap<Integer, View> map = new HashMap<Integer, View>();
    public static HashMap<Integer, Boolean> isSelected;


    public PresonLabelAdapter(Context context, List<String> listPerson, List<String> selectList, List<String> labList)
    {
        this.context = context;
        this.listPerson = listPerson;
        this.selectList = selectList;
        isSelected = new HashMap<Integer, Boolean>();

        Logger.t(TAG).d("selectList.size()" + selectList.size());

        for (int j = 0; j < listPerson.size(); j++)
        {
            isSelected.put(j,false);
        }

        for (int i = 0; i < selectList.size(); i++)
        {
            for (int j = 0; j < listPerson.size(); j++)
            {
                if (selectList.get(i).equals(listPerson.get(j)))
                {
                    isSelected.put(j, true);
                    break;
                }
            }
        }
    }

    @Override
    public int getCount()
    {
        return listPerson.size();
    }

    @Override
    public Object getItem(int position)
    {
        return listPerson.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view;
        final ViewHolder holder;
        if (map.get(position) == null)
        {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.presin_label_item, null);
            holder = new ViewHolder();

            holder.cb_label = (CheckBox) view.findViewById(R.id.cb_label);
            holder.tv_label = (TextView) view.findViewById(R.id.tv_label);
            holder.arl_bg = (AutoRelativeLayout) view.findViewById(R.id.arl_bg);
            final int p = position;
            map.put(position, view);
            Logger.t(TAG).d("isSeleted:"+isSelected.get(position));
            if(isSelected.get(position)!=null)
            holder.cb_label.setChecked(isSelected.get(position));
            view.setTag(holder);
        }
        else
        {
            view = map.get(position);
            holder = (ViewHolder) view.getTag();
        }

        if (holder.cb_label.isChecked())
        {
            holder.cb_label.setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.choise_lanyuanda_yes_xhdpi));
        }
        else
        {
            holder
                    .cb_label
                    .setButtonDrawable(ContextCompat.getDrawable(context, R.drawable.choise_lanyuanda_no_xhdpi));
        }


        /*if (selectList.contains(listPerson.get(position)))
        {
            holder.cb_label.setChecked(true);
        }
        else
        {
            holder.cb_label.setChecked(false);
        }*/

        holder.tv_label.setText(listPerson.get(position));

        return view;
    }

    public class ViewHolder
    {
        public CheckBox cb_label;
        public TextView tv_label;
        public AutoRelativeLayout arl_bg;

    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l)
    {
        mOnItemClickListener = l;
    }

    public interface OnItemClickListener
    {
        public void onItemClick(CheckBox view, int position, boolean isChecked);
    }


}
