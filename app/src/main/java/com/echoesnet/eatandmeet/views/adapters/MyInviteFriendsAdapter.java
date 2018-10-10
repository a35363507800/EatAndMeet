package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.MyInviteBody;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yqh on 2016/8/3.
 */
public class MyInviteFriendsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private static final String TAG = MyInviteFriendsAdapter.class.getSimpleName();
    private Activity mContext;
    List<MyInviteBody> list;

    public MyInviteFriendsAdapter(Activity mContext, List<MyInviteBody> list)
    {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //此列表中只有一种对象，所有viewType用不到
        View view = LayoutInflater.from(mContext).inflate(R.layout.my_invite_friend_list_ietm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        ViewHolder viewHolder = (ViewHolder) holder;
        MyInviteBody body = list.get(position);
        viewHolder.listItemNicName.setText(body.getNicName());
//        viewHolder.listItemReward.setText(String.format("%s元", body.getReward()));
        String time = body.getRegTime().substring(0, 10);
        Logger.t(TAG).d("time:" + time);
        Date date = null;
        try
        {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
            date = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            String resDate = format1.format(date);
            viewHolder.listItemRegTime.setText(resDate);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        //        @BindView(R.id.list_item_nicName)
        TextView listItemNicName;
        //        @BindView(R.id.list_item_regTime)
        TextView listItemRegTime;
        //        @BindView(R.id.list_item_reward)
        TextView listItemReward;

        ViewHolder(View view)
        {
            super(view);
//            ButterKnife.bind(this, view);
            listItemNicName = (TextView) view.findViewById(R.id.list_item_nicName);
            listItemRegTime = (TextView) view.findViewById(R.id.list_item_regTime);
            listItemReward = (TextView) view.findViewById(R.id.list_item_reward);
        }
    }
}
