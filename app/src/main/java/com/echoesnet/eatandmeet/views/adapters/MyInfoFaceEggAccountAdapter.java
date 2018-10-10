package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.FaceListBean;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;


public class MyInfoFaceEggAccountAdapter extends BaseAdapter {
    private static final String TAG = MyInfoFaceEggAccountAdapter.class.getSimpleName();
    private List<FaceListBean.faceList> accountList;
    private Context context;
    private int mCount;
    private OnRechargeItemClick onRechargeItemClick;
    private int checkEgg=-1;

    public MyInfoFaceEggAccountAdapter(Context context, List<FaceListBean.faceList> accountList) {
        this.context = context;
        this.accountList = accountList;
    }
    public void setOnRechargeItemClick(OnRechargeItemClick itemClick)
    {
        this.onRechargeItemClick=itemClick;
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Override
    public Object getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.gitem_myinfo_face_egg_account, null);

            holder.allOne = (TextView) convertView.findViewById(R.id.tv_egg_count);
            holder.allTwo= (TextView) convertView.findViewById(R.id.tv_egg_twocount);
            holder.allLayout= (RelativeLayout) convertView.findViewById(R.id.rl_egg);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

         if(position==checkEgg)
         {
             holder.allOne.setTextColor(ContextCompat.getColor(context,R.color.C0313));
             holder.allTwo.setTextColor(ContextCompat.getColor(context,R.color.C0313));
             holder.allLayout.setBackgroundResource(R.drawable.egg_count_bg0313);
             holder.allOne.getPaint().setFakeBoldText(true);
         }else
             {
                 holder.allOne.setTextColor(ContextCompat.getColor(context,R.color.C0322));
                 holder.allTwo.setTextColor(ContextCompat.getColor(context,R.color.C0323));
                 holder.allLayout.setBackgroundResource(R.drawable.egg_count_bg);
                 holder.allOne.getPaint().setFakeBoldText(false);
             }



        String amount = accountList.get(position).getGetAmount();
        holder.allTwo.setText(amount+"枚");


        String amount2 = accountList.get(position).getRechargeAmount();
        holder.allOne.setText("￥"+amount2);


        return convertView;
    }

    class ViewHolder {
        public TextView allOne;
        public RelativeLayout allLayout;
        public TextView allTwo;

    }

    public interface OnRechargeItemClick
    {
        void onItemClick(int position);
    }

    public void checkIndex(int position)
    {
        checkEgg=position;
        notifyDataSetChanged();
    }
    public int getIndex()
    {
      return checkEgg;
    }
}
