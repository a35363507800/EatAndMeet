package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.models.bean.MyFocusPersonBean;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/17.
 */

public class LiveFansPersonAdapter extends BaseAdapter
{
    private static final String TAG = LiveFansPersonAdapter.class.getSimpleName();
    private Activity mContext;
    List<MyFocusPersonBean> list = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;
    Dialog pDialog;
    Holder holder;

    public LiveFansPersonAdapter(Activity mContext, List<MyFocusPersonBean> list)
    {
        this.mContext = mContext;
        this.list = list;
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
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
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            holder = new Holder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.live_fans_person_adapter, parent, false);
            holder.riFocus = (LevelHeaderView) convertView.findViewById(R.id.ri_focus);
            holder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvFocusAge = (IconTextView) convertView.findViewById(R.id.tv_fans_age);
            holder.tvAddFocus = (IconTextView) convertView.findViewById(R.id.tv_add_focus);
            holder.llInLive = (AutoLinearLayout) convertView.findViewById(R.id.ll_inLive);
            holder.llAll = (AutoLinearLayout) convertView.findViewById(R.id.ll_all);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }
       holder.riFocus.setHeadImageByUrl(list.get(position).getUphUrl());
       holder.riFocus.setLevel(list.get(position).getLevel());
        holder.levelView.setLevel(list.get(position).getLevel(),LevelView.USER);
       holder.tvName.setText(list.get(position).getNicName());

        setSex(list.get(position).getSex(), position);
        //是否关注
        if (list.get(position).getFocus().equals("0"))
        {
            changeFocusUi(false);
        }
        else
        {
            changeFocusUi(true);
        }

        holder.tvAddFocus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View view)
            {
                if (view.getTag().equals("已关注"))
                {
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("取消关注将无法收到主播开播提醒，是否取消关注？")
                            .setPositiveButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    changeFocusState(list.get(position).getuId(), "0", view);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();

                }
                else
                {
                    changeFocusState(list.get(position).getuId(), "1", view);
                }

            }
        });

        holder.llInLive.setVisibility(View.GONE);
        if ("1".equals(list.get(position).getStatus()))
        {
            holder.llInLive.setVisibility(View.VISIBLE);
        }

        //如果设置了回调，则设置点击事件
        if (mOnItemClickListener != null)
        {
            holder.llAll.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickListener.onItemClick(holder.llAll, position);
                }
            });
        }

        return convertView;
    }

    private void setSex(String sex, int position)
    {
        if (sex.equals("女"))
        {
            holder.tvFocusAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.tvFocusAge.setText(String.format("%s %s", "{eam-e94f}",list.get(position).getAge()));
        }
        else
        {
            holder.tvFocusAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.tvFocusAge.setText(String.format("%s %s", "{eam-e950}",list.get(position).getAge()));
        }
    }

    private void changeFocusUi(boolean isFocus)
    {
        if (isFocus)
        {
            holder.tvAddFocus.setBackgroundResource(R.drawable.shape_round_2corner_15_fc4);
            holder.tvAddFocus.setText(String.format("%s %s", "—", "取 消"));
            holder.tvAddFocus.setTag("已关注");
        }
        else
        {
            holder.tvAddFocus.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.tvAddFocus.setText(String.format("%s %s", "＋", "加关注"));
            holder.tvAddFocus.setTag("未关注");
        }
    }

    private void changeFocusState(String luId, final String operFlag, final View view)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.operFlag, operFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (operFlag.equals("0"))
                {
                    Logger.t(TAG).d("取消关注成功");
                    view.setTag("未关注");
                    view.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
                    ((IconTextView) view).setText(String.format("%s %s", "＋", "加关注"));
                }
                else if (operFlag.equals("1"))
                {
                    Logger.t(TAG).d("关注成功");
                    view.setTag("已关注");
                    view.setBackgroundResource(R.drawable.shape_round_2corner_15_fc4);
                    ((IconTextView) view).setText(String.format("%s %s", "—", "取 消"));
                }
            }
        },NetInterfaceConstant.LiveC_focus,reqParamMap);
    }

    class Holder
    {
        LevelHeaderView riFocus;
        TextView tvName;
        LevelView levelView;
        IconTextView tvFocusAge;
        IconTextView tvAddFocus;
        AutoLinearLayout llInLive;
        AutoLinearLayout llAll;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener)
    {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * ItemClick的回调接口
     */
    public interface OnItemClickListener
    {
        void onItemClick(View view, int position);
    }

}
