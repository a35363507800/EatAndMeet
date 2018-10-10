package com.echoesnet.eatandmeet.views.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.CNewUserInfoAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.CPhoneContactUserBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by wangben on 2016/7/12.
 */
public class CAddFriendByPhoneAdapter extends BaseAdapter
{
    private final static String TAG = CAddFriendByPhoneAdapter.class.getSimpleName();
    private List<CPhoneContactUserBean> dishSource;
    private Activity mContext;
    private Dialog pDialog;

    public CAddFriendByPhoneAdapter(Activity mContext, List<CPhoneContactUserBean> dishSource)
    {
        this.mContext = mContext;
        this.dishSource = dishSource;
        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(true);
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
        final CPhoneContactUserBean newFriendBean = (CPhoneContactUserBean) getItem(position);
        ViewHolder holder = null;
        if (convertView == null)
        {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.litem_c_contact_add_friend, parent, false);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_u_nickName);
            holder.tvNote = (TextView) convertView.findViewById(R.id.tv_note);
            holder.ivUserImg = (LevelHeaderView) convertView.findViewById(R.id.rdv_u_img);
            holder.tvUserOper = (Button) convertView.findViewById(R.id.btn_operate);
            holder.lvView = (LevelView) convertView.findViewById(R.id.level_u_view);
            holder.imSex = (GenderView) convertView.findViewById(R.id.img_u_sex);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.lvView.setLevel(newFriendBean.getLevel(), LevelView.USER);
        holder.imSex.setSex(newFriendBean.getAge(),newFriendBean.getSex());

        if (TextUtils.isEmpty(newFriendBean.getRemark()))
        {
            holder.tvUserName.setText(newFriendBean.getNicName());
        } else
        {
            holder.tvUserName.setText(newFriendBean.getRemark());
        }

        holder.tvNote.setText(newFriendBean.getSignature());
        String focus = newFriendBean.getFocus();
        switch (focus)
        {
            case "0":
                holder.tvUserOper.setText("+关注");
                holder.tvUserOper.setEnabled(true);
                holder.tvUserOper.setBackgroundResource(R.drawable.round_cornor_11_c0412_bg);
                holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.C0412));
                break;
            case "1":
                holder.tvUserOper.setText("已关注");
                holder.tvUserOper.setEnabled(false);
                holder.tvUserOper.setBackgroundResource(R.color.white);
                holder.tvUserOper.setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
                break;
            default:
                break;
        }
        if (SharePreUtils.getUId(mContext).equals(newFriendBean.getuId()))
            holder.tvUserOper.setVisibility(View.GONE);
        else
            holder.tvUserOper.setVisibility(View.VISIBLE);

        holder.tvUserOper.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                focusFriendCallServer(newFriendBean.getuId(), "1", v);
            }
        });
        holder.ivUserImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", newFriendBean.getuId());
                mContext.startActivity(intent);
            }
        });

        holder.ivUserImg.setHeadImageByUrl(newFriendBean.getUphUrl());
//        holder.ivUserImg.setLiveState(false);
//        holder.ivUserImg.setLevel(newFriendBean.getLevel());

        return convertView;
    }

    /**
     * 关注
     * @param luId
     * @param operaFlag
     * @param view
     */
    private void focusFriendCallServer(final String luId, final String operaFlag, final View view)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, luId);
        reqParamMap.put(ConstCodeTable.operFlag, operaFlag);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ((Button) view).setText("已关注");
                ((Button) view).setEnabled(false);
                ((Button) view).setBackgroundResource(R.color.white);
                ((Button) view).setTextColor(ContextCompat.getColor(mContext, R.color.C0322));
            }
        }, NetInterfaceConstant.LiveC_focus, reqParamMap);
    }

    /**
     * 接受好友
     *
     * @param hToAddUsername
     * @param reason
     */
    private void acceptAsFriend(final String toAddUserUid, final String hToAddUsername, final String reason, final View view)
    {
        pDialog.show();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //参数为要添加的好友的username和添加理由
                try
                {
                    EMClient.getInstance().contactManager().acceptInvitation(hToAddUsername);
                    saveContactStatusToServer(toAddUserUid, reason, view);
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void saveContactStatusToServer(final String toAddUserUid, String gift, final View view)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, toAddUserUid);
        reqParamMap.put(ConstCodeTable.gift, gift);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                ((Button) view).setText("已添加");
                ((Button) view).setEnabled(false);
                ((Button) view).setBackgroundResource(R.color.white);
                ((Button) view).setTextColor(ContextCompat.getColor(mContext, R.color.FC3));

                //添加成功，发送一条添加成功消息
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        EMMessage message = EMMessage.createTxtSendMessage(mContext.getResources().getString(R.string.add_friend_success), toAddUserUid);
                        message.setAttribute("faceurl", SharePreUtils.getHeadImg(mContext));
                        message.setAttribute("uid", SharePreUtils.getUId(mContext));
                        message.setAttribute("nickname", SharePreUtils.getNicName(mContext));
                        message.setAttribute("level", SharePreUtils.getLevel(mContext));
                        message.setAttribute("sex", SharePreUtils.getSex(mContext));
                        message.setAttribute("age", SharePreUtils.getAge(mContext));
                        EMClient.getInstance().chatManager().sendMessage(message);
                    }
                }).start();
            }
        },NetInterfaceConstant.NeighborC_friend,reqParamMap);
    }

    /**
     * 申请好友
     *
     * @param hToAddUsername
     * @param reason
     */

    private void applyAsFriend(final String toAddUserUid, final String hToAddUsername, final String reason, final View btn)
    {
        pDialog.show();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //参数为要添加的好友的username和添加理由
                try
                {
                    EMClient.getInstance().contactManager().addContact(hToAddUsername, reason);
                    applyFriendByHello(toAddUserUid, btn);
                    //region 待用
/*            EMClient.getInstance().contactManager().setContactListener(new EMContactListener()
            {

                @Override
                public void onContactAgreed(String username)
                {
                    //好友请求被同意
                    if (username.equals(hToAddUsername))
                    {

                    }
                }

                @Override
                public void onContactRefused(String username)
                {
                    //好友请求被拒绝
                }

                @Override
                public void onContactInvited(String username, String reason)
                {
                    //收到好友邀请
                }

                @Override
                public void onContactDeleted(String username)
                {
                    //被删除时回调此方法
                }


                @Override
                public void onContactAdded(String username)
                {
                    //增加了联系人时回调此方法
                }
            });*/
                    //endregion

                } catch (HyphenateException e)
                {
                    //ToastUtils.showShort(mContext,"环信调用失败+"+e.getMessage());
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }).start();
    }

    /**
     * 普通申请
     *
     * @param toAddUserUid 要申请人的uId
     */
    private void applyFriendByHello(String toAddUserUid, final View btn)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.lUId, toAddUserUid);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                appleSendSuccess(btn);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        },NetInterfaceConstant.NeighborC_preFriend,reqParamMap);
    }

    private void appleSendSuccess(View btn)
    {
        ((Button) btn).setText("等待接受");
        btn.setEnabled(false);
        btn.setBackgroundResource(R.color.white);
        ((Button) btn).setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
    }

    public final class ViewHolder
    {
        LevelHeaderView ivUserImg;
        TextView tvUserName;
        TextView tvNote;
        Button tvUserOper;
        LevelView lvView;
        GenderView imSex;
    }
}
