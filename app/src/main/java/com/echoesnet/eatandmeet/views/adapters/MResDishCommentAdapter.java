package com.echoesnet.eatandmeet.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.IRatingBarClickedListener;

import java.util.List;

/**
 * Created by wangben on 2016/6/25.
 */
public class MResDishCommentAdapter extends BaseAdapter
{
    private List<DishBean> dishSource;
    private Context mContext;
    //为了复用这个adapter
    private String adapterType;
    public MResDishCommentAdapter(Context mContext, List<DishBean>dishSource,String type)
    {
        this.mContext=mContext;
        this.dishSource=dishSource;
        this.adapterType=type;
    }
    @Override
    public int getCount()
    {
        return dishSource.size();
    }

    @Override
    public Object getItem(int position)
    {
        return dishSource.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        convertView= LayoutInflater.from(mContext).inflate(R.layout.litem_comment_dish,parent,false);
        TextView dishName= (TextView) convertView.findViewById(R.id.tv_dish_name);
        CustomRatingBar dishRating= (CustomRatingBar) convertView.findViewById(R.id.crb_dish_ratingbar);

        final DishBean dishBean= (DishBean) getItem(position);
        dishName.setText(dishBean.getDishName());

        //如果是提交评价
        if (adapterType.equals("commit"))
        {
            dishRating.setIndicator(false);
            dishRating.setIRatingBarClickedListener(new IRatingBarClickedListener()
            {
                @Override
                public void startClicked(int starNum)
                {
                    //Logger.t("菜品").d("执行了");
                    dishBean.setDishStar(String.valueOf(starNum));
                    //MResDishCommentAdapter.this.notifyDataSetChanged();
                }
            });
        }
        //如果是显示评价
        else
        {
            dishRating.setIndicator(true);
            dishRating.setRatingBar(Integer.parseInt(dishBean.getDishStar()));
        }

        return convertView;
    }
}
