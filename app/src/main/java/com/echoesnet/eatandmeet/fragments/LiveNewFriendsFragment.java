package com.echoesnet.eatandmeet.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.CGiftBean;
import com.echoesnet.eatandmeet.models.bean.CNewFriendBean;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.presenters.ImpCNewFriendsView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICNewFriendsView;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.CNewFriendsAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/3/29.
 */

public class LiveNewFriendsFragment extends MVPBaseFragment<ICNewFriendsView, ImpCNewFriendsView>
        implements ICNewFriendsView, PullToRefreshBase.OnRefreshListener2, CNewFriendsAdapter.IViewClickListener,
        AdapterView.OnItemLongClickListener
{
    private static final String TAG = LiveNewFriendsFragment.class.getSimpleName();
    private static final String PAGE_COUNT = "10";
    private PullToRefreshListView mPullToRefreshListview;

    private ListView actualListView;
    private Activity mContext;

    private List<CNewFriendBean> dataSource;
    private CNewFriendsAdapter newFriendAdapter;

    // public static View liveNewFriendView;
    private View liveNewFriendView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mContext = getActivity();
//        View viewPager = inflater.inflate(R.layout.frg_new_friends, container, false);
        View view = View.inflate(getActivity(), R.layout.live_msg_empty_view, null);
        liveNewFriendView = LayoutInflater.from(getActivity()).inflate(R.layout.frg_new_friends, null);
        mPullToRefreshListview = (PullToRefreshListView) liveNewFriendView.findViewById(R.id.ptfl_lstview);
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(this);
        actualListView = mPullToRefreshListview.getRefreshableView();
        // Need to use the Actual ListView when registering for Context Menu
        //registerForContextMenu(actualListView);
        actualListView.setOnItemLongClickListener(this);

        ((TextView) view.findViewById(R.id.tv_default_des)).setText("暂时没有新朋友，上拉加载试试");
        actualListView.setEmptyView(view);

        dataSource = new ArrayList<>();
        newFriendAdapter = new CNewFriendsAdapter(mContext, dataSource);
        actualListView.setAdapter(newFriendAdapter);
        newFriendAdapter.setViewClickListener(this);
        if (mPresenter != null)
            mPresenter.getAllNewFriends("0", PAGE_COUNT, "refresh");
        return liveNewFriendView;
    }

    /**
     * 接受好友申请
     */
    private void acceptAsFriend(CNewFriendBean newFriendBean, final String amount, final String streamId, final String payType, final View view)
    {
        if (mPresenter != null)
        {
            mPresenter.saveContactStatusToServer(amount, streamId, payType, view, newFriendBean);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        menu.add(0, 0, 0, "删除");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        // 得到当前被选中的item信息
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CNewFriendBean friendBean = dataSource.get(menuInfo.position - 1);

        switch (item.getItemId())
        {
            case 0:
                //ToastUtils.showShort(mContext,"点击>"+menuInfo.position+"  "+menuInfo.id+" "+menuInfo.targetView.toString());
                if (mPresenter != null)
                    mPresenter.deletePreFriend(friendBean.getuId(), friendBean);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected ImpCNewFriendsView createPresenter()
    {
        return new ImpCNewFriendsView();
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.NeighborC_friend:
                if (code.equals("USR_IS_FRIEND"))
                {
                    ToastUtils.showShort("该用户已经是您的好友了，请下拉刷新状态！");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
    }


    @Override
    public void getAllNewFriendsCallback(String response, String operateType)
    {
        //下拉刷新
        if (operateType.equals("refresh"))
        {
            dataSource.clear();
        }
        List<CNewFriendBean> list = new Gson().fromJson(response, new TypeToken<List<CNewFriendBean>>() {}.getType());
        //后台有时候会返回null，不可理喻
        if (list != null)
            // Logger.t(TAG).d("ji集合第一个元素》》"+list.get(0).toString());
            dataSource.addAll(list);

        newFriendAdapter.notifyDataSetChanged();
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();
    }

    @Override
    public void deletePreFriendCallback(String response, CNewFriendBean friendBean)
    {
        Logger.t(TAG).d("获得的结果：" + response);
        dataSource.remove(friendBean);
        newFriendAdapter.notifyDataSetChanged();
        ToastUtils.showShort("删除成功");
    }

    @Override
    public void saveContactStatusToServerCallback(String response, View view, CNewFriendBean newFriendBean)
    {
        Logger.t(TAG).d("获得的结果>" + response);
        //同步好友数据到本地
        UsersBean usersBean = new UsersBean();
        usersBean.setuId(newFriendBean.getuId());
        usersBean.setImuId(newFriendBean.getImuId());
        usersBean.setUphUrl(newFriendBean.getUphUrl());
        usersBean.setNicName(newFriendBean.getNicName());
        usersBean.setLevel(newFriendBean.getLevel());
        HuanXinIMHelper.getInstance().updateContact(usersBean, newFriendBean.getRemark());
        sendMsg(newFriendBean.getImuId(), newFriendBean.getuId());
        ((TextView) view).setText("已添加");
        ((TextView) view).setEnabled(false);
        ((TextView) view).setBackgroundResource(R.color.white);
        ((TextView) view).setTextColor(ContextCompat.getColor(mContext, R.color.FC3));
        if (mPresenter != null)
        {
            Logger.t(TAG).d("接受好友btn被按下了，开始同步状态");
            mPresenter.getAllNewFriends("0", String.valueOf(dataSource.size()), "refresh");
        }
    }


    private void sendMsg(final String hxId, final String toAddUserUid)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //参数为要添加的好友的username和添加理由
                try
                {
                    EMClient.getInstance().contactManager().acceptInvitation(hxId);
                    Logger.t(TAG).d("接受" + hxId + "的申请成功");
                    //添加成功，发送一条添加成功消息
                    EMMessage message = EMMessage.createTxtSendMessage(SharePreUtils.getNicName(EamApplication.getInstance())
                            + "通过了你的好友请求，现在可以发起聊天了", hxId);
                    message.setAttribute("avatar", SharePreUtils.getHeadImg(getActivity()));
                    message.setAttribute("uid", SharePreUtils.getUId(getActivity()));
                    message.setAttribute("nickname", SharePreUtils.getNicName(getActivity()));
                    message.setAttribute("level", SharePreUtils.getLevel(getActivity()));
                    message.setAttribute("sex", SharePreUtils.getSex(getActivity()));
                    message.setAttribute("age", SharePreUtils.getAge(getActivity()));
                    EMClient.getInstance().chatManager().sendMessage(message);
                    Logger.t(TAG).d("向" + hxId + "发送了消息：" + message.getBody().toString());
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                } catch (Exception e)
                {
                    e.printStackTrace();
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }).start();
    }

    @Override
    public void onPullDownToRefresh(PullToRefreshBase refreshView)
    {
        //下拉刷新
        if (mPresenter != null)
            mPresenter.getAllNewFriends("0", String.valueOf(dataSource.size()), "refresh");
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase refreshView)
    {
        //上拉加载
        if (mPresenter != null)
            mPresenter.getAllNewFriends(String.valueOf(dataSource.size()), PAGE_COUNT, "add");
    }

    @Override
    public void onBtnClick(View view, int position)
    {
        CNewFriendBean newFriendBean = dataSource.get(position);
        CGiftBean cGiftBean = newFriendBean.getWelgiftBean();
        if (cGiftBean == null)
            acceptAsFriend(newFriendBean, "", "", "", view);
        else
            acceptAsFriend(newFriendBean, cGiftBean.getAmount(), cGiftBean.getStreamId(), cGiftBean.getPayType(), view);
        //同步好友数据到本地
        UsersBean usersBean = new UsersBean();
        usersBean.setuId(newFriendBean.getuId());
        usersBean.setImuId(newFriendBean.getImuId());
        usersBean.setUphUrl(newFriendBean.getUphUrl());
        usersBean.setNicName(newFriendBean.getNicName());
        usersBean.setLevel(newFriendBean.getLevel());
        usersBean.setAge(newFriendBean.getAge());
        HuanXinIMHelper.getInstance().updateContact(usersBean, newFriendBean.getRemark());

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l)
    {
        final Dialog dialog = new Dialog(mContext, R.style.AppTheme_Dialog_Alert);
        View view1 = LayoutInflater.from(mContext).inflate(R.layout.live_delete_new_friend, null);
        TextView tvDelFriends = (TextView) view1.findViewById(R.id.delete_friends);
        tvDelFriends.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                CNewFriendBean friendBean = dataSource.get(i - 1);
                if (mPresenter != null)
                    mPresenter.deletePreFriend(friendBean.getuId(), friendBean);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view1);
        dialog.show();
        return true;
    }

    @Override
    protected String getPageName()
    {
        return TAG;
    }
}
