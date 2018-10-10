package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.app.Dialog;
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
import com.echoesnet.eatandmeet.models.bean.CGiftBean;
import com.echoesnet.eatandmeet.models.bean.CNewFriendBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.List;


/**
 * Created by wangben on 2016/7/7.
 * 接受好友的列表adpter
 */
public class CNewFriendsAdapter extends BaseAdapter
{
    private final String TAG = CNewFriendsAdapter.class.getSimpleName();
    private List<CNewFriendBean> dishSource;
    private Activity mContext;
    private Dialog pDialog;
    private IViewClickListener viewClickListener;

    public CNewFriendsAdapter(Activity mContext, List<CNewFriendBean> dishSource)
    {
        this.mContext = mContext;
        this.dishSource = dishSource;
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
    }

    public void setViewClickListener(IViewClickListener listener)
    {
        this.viewClickListener = listener;
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
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        CNewFriendBean newFriendBean = (CNewFriendBean) getItem(position);
        Logger.t(TAG).d("newFriendBean:"+newFriendBean.toString());
        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_c_newfriend, parent, false);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_u_nickName);
            holder.tvUserDescription = (TextView) convertView.findViewById(R.id.tv_u_description);
            holder.tvUserAppType = (TextView) convertView.findViewById(R.id.tv_u_apply_type);
            holder.ivUserImg = (LevelHeaderView) convertView.findViewById(R.id.rdv_u_img);
            holder.levelView = (LevelView) convertView.findViewById(R.id.level_view);
            holder.itvAge = (IconTextView) convertView.findViewById(R.id.tv_age);
            holder.tvUserOper = (Button) convertView.findViewById(R.id.btn_operate);
            holder.toUserDetail = (AutoLinearLayout) convertView.findViewById(R.id.rl_to_userDetail);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        if (TextUtils.isEmpty(newFriendBean.getRemark()))
        {
            holder.tvUserName.setText(newFriendBean.getNicName());
        }
        else
        {
            holder.tvUserName.setText(newFriendBean.getRemark());
        }

        final CGiftBean cGiftBean = newFriendBean.getWelgiftBean();
        //普通申请
        if (cGiftBean == null)
        {
            holder.tvUserAppType.setVisibility(View.GONE);
            holder.tvUserDescription.setText("申请成为好友");
        }
        else
        {
            String htmlStr = String.format("<p>送您【<font color=%s>%s</font>元】<font color=%s>见面礼</font></p>", "red",
                    CommonUtils.keep2Decimal(Double.parseDouble(cGiftBean.getAmount())), "red");
            holder.tvUserAppType.setText(Html.fromHtml(htmlStr));
            holder.tvUserAppType.setVisibility(View.VISIBLE);
            holder.tvUserDescription.setText("24小时内未领取将退回对方账户");
        }

        String status = newFriendBean.getStat();
        //0等待接收
        if (status.equals("0"))
        {
            holder.tvUserOper.setText("接 受");
            holder.tvUserOper.setEnabled(true);
            holder.tvUserOper.setBackgroundResource(R.drawable.round_cornor_18_mc7_bg);
            holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.MC7));
        }
        //已添加
        else if (status.equals("1"))
        {
            holder.tvUserOper.setText("已添加");
            holder.tvUserOper.setEnabled(false);
            holder.tvUserOper.setBackgroundResource(R.color.white);
            holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        }
        //2是忽略，但是后天不确定代码怎么写的暂时为过期
        else if (status.equals("2"))
        {
            holder.tvUserOper.setText("已过期");
            holder.tvUserOper.setEnabled(false);
            holder.tvUserOper.setBackgroundResource(R.color.white);
            holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        }
        //过期，后台不清楚3是不是用到
        else
        {
            holder.tvUserOper.setText("已过期");
            holder.tvUserOper.setEnabled(false);
            holder.tvUserOper.setBackgroundResource(R.color.white);
            holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        }
        //接收或者拒绝按钮
        holder.tvUserOper.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (viewClickListener != null)
                {
                    viewClickListener.onBtnClick(v, position);
                }
            }
        });
        holder.ivUserImg.setLiveState(false);
        holder.ivUserImg.setHeadImageByUrl(newFriendBean.getUphUrl());
        holder.ivUserImg.setLevel(newFriendBean.getLevel());
        holder.levelView.setLevel(newFriendBean.getLevel(),LevelView.USER);
        setSex(newFriendBean, holder);
        return convertView;
    }

    private void setSex(CNewFriendBean newFriendBean, ViewHolder holder)
    {
        String age = newFriendBean.getAge();
        age = TextUtils.isEmpty(age) ? "18" : age;
        if ("女".equals(newFriendBean.getSex()))
        {
            holder.itvAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
            holder.itvAge.setText(String.format("%s %s", "{eam-e94f}", age));
        }
        else
            //11:43:26
        {
            holder.itvAge.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
            holder.itvAge.setText(String.format("%s %s", "{eam-e950}", age));
        }
    }

    /*    //接受好友后需要用透传消息通知一下对方，让对方从服务器上刷新好友关系，此方法废弃，由于环信自身有回调
        private void sendNoteAckMsg(String toUserHxId,String senderUid)
        {
            //创建透传消息
            final EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            cmdMsg.setChatType(EMMessage.ChatType.Chat);
            EMCmdMessageBody cmdBody = new EMCmdMessageBody(EamConstant.EAM_C_NOTIFY_NICKNAME_CHANGE);
            cmdMsg.addBody(cmdBody);
            //发送给某个人
            cmdMsg.setReceipt(toUserHxId);
            cmdMsg.setAttribute(EamConstant.EAM_C_NOTICE_SENDER_UID,senderUid);
            cmdMsg.setMessageStatusCallback(new EMCallbackListener(this));
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
        }*/
    public final class ViewHolder
    {
        TextView tvUserName;
        TextView tvUserAppType;
        TextView tvUserDescription;
        Button tvUserOper;
        LevelHeaderView ivUserImg;
        AutoLinearLayout toUserDetail;
        LevelView levelView;
        IconTextView itvAge;
    }

    public interface IViewClickListener
    {
        void onBtnClick(View view, int position);
    }
}
