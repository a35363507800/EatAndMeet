package com.echoesnet.eatandmeet.views.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.ClubFoodDetailAct;
import com.echoesnet.eatandmeet.activities.ClubInfoAct;
import com.echoesnet.eatandmeet.models.bean.ClubInfoBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.adapters.ClubInfoChildAdapter;
import com.joanzapata.iconify.widget.IconTextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.internal.operators.observable.ObservableElementAt;

/**
 *
 */
public class ClubListView extends LinearLayout
{

    public ClubListView(Context context)
    {
        super(context);
    }

    public ClubListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ClubListView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private static final int CLUB_COUNT = 4; //可选项数量

    private ClubInfoBean clubInfoBean;

    public ClubInfoBean getClubInfoBean()
    {
        return clubInfoBean;
    }

    public void setDatas(Context mContext, ClubInfoBean bean)
    {
        removeAllViews();
        clubInfoBean=bean;
        for (int i = 0; i < CLUB_COUNT; i++)
        {
            final int item=i;
            View view = LayoutInflater.from(mContext).inflate(R.layout.act_club_info_adapter, null);


            TextView title = view.findViewById(R.id.tv_title);
            RecyclerView recyclerView = view.findViewById(R.id.rv_clubinfo);


            LinearLayout.LayoutParams pa = (LayoutParams) recyclerView.getLayoutParams();
            pa.bottomMargin = CommonUtils.dp2px(mContext, 19);
            recyclerView.setLayoutParams(pa);


            if (i == 0)
            {
                checkListStates(bean);
                title.setText("预定日期");
            }
            else if (i == 1)
                title.setText("预定场次");
            else if (i == 2)
            {
                IconTextView icon=view.findViewById(R.id.itv_food_info);
                icon.setVisibility(View.VISIBLE);
                icon.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Intent intent=new Intent(mContext, ClubFoodDetailAct.class);
                        intent.putParcelableArrayListExtra("Packages", (ArrayList<? extends Parcelable>) bean.getPackages());
                        intent.putExtra("index",getCheckPosition(2));
                        ((Activity)mContext).startActivityForResult(intent, ClubInfoAct.INDEX_OK);
                    }
                });
                title.setText("套餐规格");
            }
            else if (i == 3)
            {
                title.setText("私人定制主题");
                if(bean.getTheme().size()==0)
                title.setVisibility(View.GONE);
            }

            ClubInfoChildAdapter clubInfoChildAdapter = new ClubInfoChildAdapter(mContext, bean, i);
            if(i==1)
            {
                for(int k=0;k<bean.getReserveDate().size();k++)
                {
                   if("0".equals(bean.getReserveDate().get(k).getStates()))
                    {
                        clubInfoChildAdapter=new ClubInfoChildAdapter(mContext,bean.getReserveDate().get(k).getScreenings());
                        break;
                    }
                }
            }
            clubInfoChildAdapter.setOnItemtClickListener(new ClubInfoChildAdapter.OnItemClickListener()
            {
                @Override
                public void onItemClick(View view, int position)
                {
                    if(onItemClickListener!=null)
                        onItemClickListener.onItemClick(view,item,position);
                }
            });
            recyclerView.setAdapter(clubInfoChildAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
            if(i==0)
            {
                for(int k=0;k<bean.getReserveDate().size();k++)
                {
                    final int position=k;
                    if("0".equals(bean.getReserveDate().get(k).getStates()))
                    {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.smoothScrollToPosition(position);
                            }
                        },100);

                        break;
                    }
                }

        }


            addView(view);
        }
    }

    private void checkListStates(ClubInfoBean bean)
    {
        List<ClubInfoBean.ReserveDateBean> reserveList=bean.getReserveDate();
        for(int i=0;i<bean.getReserveDate().size();i++)
        {
            ClubInfoBean.ReserveDateBean reserveDateBean =reserveList.get(i);
            List<ClubInfoBean.ScreeningsBean> screeningsBeanList =reserveDateBean.getScreenings();
            int count=screeningsBeanList.size();
              for(int k=0;k<screeningsBeanList.size();k++)
              {
                  if("1".equals(screeningsBeanList.get(k).getStatus()))
                  {
                      count--;
                  }
              }
              if(count==0)
                  reserveList.get(i).setStates("1");
              else
                  reserveList.get(i).setStates("0");
        }
    }

    public int getCheckPosition(int itemPostion)
    {
          if(itemPostion<getChildCount())
          {
              View view =getChildAt(itemPostion);
              RecyclerView recyclerView = view.findViewById(R.id.rv_clubinfo);
              return ((ClubInfoChildAdapter)recyclerView.getAdapter()).getCheckPosition();
          }

          return -1;
    }

    public void setScorllPosition(int itemPostion,int postion)
    {
        if(itemPostion<getChildCount())
        {
            View view =getChildAt(itemPostion);
            RecyclerView recyclerView = view.findViewById(R.id.rv_clubinfo);
            recyclerView.smoothScrollToPosition(postion);
        }

    }

    public void setCheckPosition(int itemPostion,int index)
    {
        if(itemPostion<getChildCount())
        {
            View view =getChildAt(itemPostion);
            RecyclerView recyclerView = view.findViewById(R.id.rv_clubinfo);
         ((ClubInfoChildAdapter)recyclerView.getAdapter()).setCheckPosition(index);
        }
    }
    public void  setList(List list,int itemPostion)
    {
        if(itemPostion<getChildCount())
        {
            View view =getChildAt(itemPostion);
            RecyclerView recyclerView = view.findViewById(R.id.rv_clubinfo);
            ((ClubInfoChildAdapter)recyclerView.getAdapter()).setList(list);
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener
    {
        void onItemClick(View view,int item,int postion);
    }
}
