package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.View.LiveRoomAct1;
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
import com.echoesnet.eatandmeet.views.widgets.ContextMenuDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 好友申请列表，里面可以接受好友申请
 */
public class CNewFriendsAct extends MVPBaseActivity<ICNewFriendsView, ImpCNewFriendsView> implements ICNewFriendsView
{
    private final static String TAG = CNewFriendsAct.class.getSimpleName();
    private static final String PAGE_COUNT = "10";
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.ptfl_lstview)
    PullToRefreshListView mPullToRefreshListview;

    private ListView actualListView;
    private Activity mContext;
    private Dialog pDialog;

    private List<CNewFriendBean> dataSource;
    private CNewFriendsAdapter newFriendAdapter;

    private LiveRoomAct1 mLiveRoomAct1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cnew_friends);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        //如果没有数据则加载，有数据则刷新
        if (dataSource != null && dataSource.size() == 0)
        {

            if (mPresenter != null)
            {
                if (pDialog != null && !pDialog.isShowing())
                    pDialog.show();
                mPresenter.getAllNewFriends("0", PAGE_COUNT, "add");
            }
        } else
        {
            if (mPresenter != null)
            {
                if (pDialog != null && !pDialog.isShowing())
                    pDialog.show();
                mPresenter.getAllNewFriends("0", String.valueOf(dataSource.size()), "refresh");
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    private void afterViews()
    {
        mContext = this;
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);
        topBar.setTitle("新的朋友");
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {

            }
        });
        mPullToRefreshListview.setMode(PullToRefreshBase.Mode.BOTH);
        mPullToRefreshListview.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {

                //下拉刷新
                if (mPresenter != null)
                    mPresenter.getAllNewFriends("0", String.valueOf(dataSource.size()), "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                //上拉加载

                if (mPresenter != null)
                    mPresenter.getAllNewFriends(String.valueOf(dataSource.size()), PAGE_COUNT, "add");
            }
        });
        mPullToRefreshListview.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener()
        {
            @Override
            public void onLastItemVisible()
            {
                // Toast.makeText(context, "上拉刷新", Toast.LENGTH_SHORT).show();
            }
        });
        actualListView = mPullToRefreshListview.getRefreshableView();
        mPullToRefreshListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(mContext, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", dataSource.get(position - 1).getuId());
                mContext.startActivity(intent);
            }
        });
        // Need to use the Actual ListView when registering for Context Menu
        // registerForContextMenu(actualListView);

        LinearLayout empty = (LinearLayout) findViewById(R.id.empty_view);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有新朋友，上拉加载试试");
        mPullToRefreshListview.setEmptyView(empty);

        dataSource = new ArrayList<>();
        newFriendAdapter = new CNewFriendsAdapter(mContext, dataSource);
        actualListView.setAdapter(newFriendAdapter);
        newFriendAdapter.setViewClickListener(new CNewFriendsAdapter.IViewClickListener()
        {
            @Override
            public void onBtnClick(View v, int position)
            {
                if (pDialog != null && !pDialog.isShowing())
                {
                    pDialog.show();
                }

                CNewFriendBean newFriendBean = dataSource.get(position);
                CGiftBean cGiftBean = newFriendBean.getWelgiftBean();
                if (cGiftBean == null)//newFriendBean.getuId(), newFriendBean.getAnchorImuId()
                    acceptAsFriend(newFriendBean, "", "", "", v);
                else
                    acceptAsFriend(newFriendBean, cGiftBean.getAmount(), cGiftBean.getStreamId(), cGiftBean.getPayType(), v);
            }
        });
        final List<String> menuList = new ArrayList<String>();
        menuList.add(this.getResources().getString(R.string.Delete_the_contact));

        actualListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                new ContextMenuDialog(new ContextMenuDialog.MenuDialogCallBack()
                {
                    @Override
                    public void menuOnClick(String menuItem, int position)
                    {
                        CNewFriendBean friendBean = dataSource.get(position - 1);
                        if (getResources().getString(R.string.Delete_the_contact).equals(menuItem))
                        {
                            if (mPresenter != null)
                            {
                                if (pDialog != null && !pDialog.isShowing())
                                    pDialog.show();
                                mPresenter.deletePreFriend(friendBean.getuId(), friendBean);
                            }
                        }
                        //保留黑名单代码，以待后期添加
/*        else if (item.getItemId() == R.id.add_to_blacklist)
        {
            moveToBlacklist(toBeProcessUsername);
            return true;
        }*/
                    }
                }).showContextMenuBox(mContext, menuList);
                return true;
            }
        });

    }


//TODO 原文本菜单显示方式被替换成dialog方式，因部分手机显示效果不理想 可考虑删除
//    @Overrides
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
//    {r
//        menu.add(0, 0, 0, "删除");
//        super.onCreateContextMenu(menu, v, menuInfo);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item)
//    {
//        // 得到当前被选中的item信息
//        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        Logger.t(TAG).d("位置》" + menuInfo.id);
//        Logger.t(TAG).d("position:" + menuInfo.position);
//
//        CNewFriendBean friendBean = dataSource.get(menuInfo.position - 1);
//
//        switch (item.getItemId())
//        {
//            case 0:
//                //ToastUtils.showShort(mContext,"点击>"+menuInfo.position+"  "+menuInfo.id+" "+menuInfo.targetView.toString());
//                if (mPresenter != null)
//                {
//                    if (pDialog != null && !pDialog.isShowing())
//                        pDialog.show();
//                    mPresenter.deletePreFriend(friendBean.getuId(), friendBean);
//                }
//                break;
//        }
//        return super.onContextItemSelected(item);
//    }

    @Override
    protected ImpCNewFriendsView createPresenter()
    {
        return new ImpCNewFriendsView();
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
            dataSource.addAll(list);
        newFriendAdapter.notifyDataSetChanged();
        if (mPullToRefreshListview != null)
            mPullToRefreshListview.onRefreshComplete();

        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void deletePreFriendCallback(String response, CNewFriendBean friendBean)
    {
        Logger.t(TAG).d("获得的结果：" + response);
        dataSource.remove(friendBean);
        newFriendAdapter.notifyDataSetChanged();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void saveContactStatusToServerCallback(String response, final View view, CNewFriendBean newFriendBean)
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
        if (pDialog != null && !pDialog.isShowing())
            pDialog.dismiss();
        if (mPresenter != null)
        {
            Logger.t(TAG).d("接受好友btn被按下了，开始同步状态");
            mPresenter.getAllNewFriends("0", String.valueOf(dataSource.size()), "refresh");
        }


    }

    private void sendMsg(String hxId, final String toAddUserUid)
    {
        //参数为要添加的好友的username和添加理由
        try
        {
            EMClient.getInstance().contactManager().acceptInvitation(hxId);
            Logger.t(TAG).d("接受" + hxId + "的申请成功");
            //添加成功，发送一条添加成功消息
            EMMessage message = EMMessage.createTxtSendMessage(SharePreUtils.getNicName(EamApplication.getInstance())
                    + "通过了你的好友请求，现在可以发起聊天了", hxId);

            message.setAttribute("avatar", SharePreUtils.getHeadImg(mContext));
            message.setAttribute("uid", SharePreUtils.getUId(mContext));
            message.setAttribute("nickname", SharePreUtils.getNicName(mContext));
            message.setAttribute("level", SharePreUtils.getLevel(mContext));
            message.setAttribute("sex", SharePreUtils.getSex(mContext));
            message.setAttribute("age", SharePreUtils.getAge(mContext));

            EMClient.getInstance().chatManager().sendMessage(message);

            Logger.t(TAG).d("向" + hxId + "发送了消息：" + message.getBody().toString());
//            Thread.sleep(200);
            mContext.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                    Intent intent = new Intent(mContext, CNewUserInfoAct.class);
                    intent.putExtra("checkWay", "UId");
                    intent.putExtra("toUId", toAddUserUid);
                    mContext.startActivity(intent);
//
//                    if (pDialog != null && pDialog.isShowing())
//                        pDialog.dismiss();
                }
            });

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
}
