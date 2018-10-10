package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CommentBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/7/25.
 */
public class CommentAdapter extends BaseAdapter {
    private static final String TAG = CommentAdapter.class.getSimpleName();
    private List<CommentBean> list;
    private Context context;
    private LayoutInflater inflater = null;
    private ViewHolder holder = null;
    private Typeface iconfont;

    private HashMap<String, Boolean> chosenGroup;

    public CommentAdapter(List<CommentBean> list, HashMap<String, Boolean> chosenGroup, Context context) {
        this.context = context;
        this.list = list;
        this.chosenGroup = chosenGroup;
        inflater = LayoutInflater.from(context);
        iconfont = Typeface.createFromAsset(context.getAssets(), "iconify/eam_icon.otf");
    }

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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.litem_comment, null);
            holder.tv_dian = (TextView) convertView.findViewById(R.id.tv_dian);
            holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
            holder.rb_yes = (RadioButton) convertView.findViewById(R.id.rb_yes);
            holder.rb_no = (RadioButton) convertView.findViewById(R.id.rb_no);
            holder.group = (RadioGroup) convertView.findViewById(R.id.group);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final String labelStr = list.get(position).labelStr;
        holder.tv_content.setText(labelStr);
//        holder.group.setId(position);
//        holder.group.setOnCheckedChangeListener(null);

        if (chosenGroup.get(labelStr)) {
//            holder.rb_yes.setButtonDrawable(R.drawable.radio_btn_p);
//            holder.rb_no.setButtonDrawable(R.drawable.radio_btn_n);
//            holder.rb_yes.setTextColor(context.getResources().getColor(R.color.MC1));
            holder.rb_yes.setTypeface(iconfont);
            holder.rb_yes.setText("\uE939 很满意");
            holder.rb_yes.setPadding(8, 0, 0, 0);

            holder.rb_no.setTypeface(iconfont);
            holder.rb_no.setText("\uE9AC 不满意");
            holder.rb_no.setPadding(8, 0, 0, 0);
        } else {
//            holder.rb_yes.setButtonDrawable(R.drawable.radio_btn_n);
//            holder.rb_no.setButtonDrawable(R.drawable.radio_btn_p);
            holder.rb_yes.setTypeface(iconfont);
            holder.rb_yes.setText("\uE939 很满意");
            holder.rb_yes.setPadding(8, 0, 0, 0);

            holder.rb_no.setTypeface(iconfont);
            holder.rb_no.setText("\uE9AC 不满意");
            holder.rb_no.setPadding(8, 0, 0, 0);
        }

        holder.group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(null != chosenListener){
//                    if (checkedId == holder.rb_yes.getId()) {
//                        chosenListener.onChosenCallback(labelStr,true);
//                    } else if (checkedId == holder.rb_no.getId()) {
//                        chosenListener.onChosenCallback(labelStr,false);
//                    }

                    if (checkedId == holder.rb_yes.getId()) {
                        chosenListener.onChosenCallback(new CommentBean(true, labelStr),true);
                    } else if (checkedId == holder.rb_no.getId()) {
                        chosenListener.onChosenCallback(new CommentBean(false, labelStr),false);
                    }
                }

//                chosenListener.onChosenCallback(labelStr, true);
//                if (checkedId == holder.rb_yes.getId()) {
//                    chosenGroup.put(labelStr, true);
//                } else if (checkedId == holder.rb_no.getId()) {
//                    chosenGroup.put(labelStr, false);
//                }
//                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public class ViewHolder {
        public TextView tv_dian;
        public TextView tv_content;
        public RadioGroup group;
        public RadioButton rb_yes;
        public RadioButton rb_no;
    }


    private ChosenListener chosenListener;

    public void setChosenListener(ChosenListener chosenListener) {
        this.chosenListener = chosenListener;
    }

    public interface ChosenListener {
        //        void onChosenCallback(String labelStr, boolean value);
        void onChosenCallback(CommentBean bean, boolean value);
    }


}
