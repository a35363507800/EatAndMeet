package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CPhoneContactUserBean;
import com.echoesnet.eatandmeet.presenters.ImpICPhoneContactView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICPhoneContactView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.views.adapters.CAddFriendByPhoneAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description
 */
public class CPhoneContactAct extends MVPBaseActivity<ICPhoneContactView, ImpICPhoneContactView> implements ICPhoneContactView
{
    //region 变量
    private static final String TAG = CPhoneContactAct.class.getSimpleName();
    @BindView(R.id.lv_contact_lst)
    PullToRefreshListView contactListView;
    @BindView(R.id.tab_bar)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.loading_view)
    RelativeLayout loadingView;//加载布局

    private CAddFriendByPhoneAdapter mCAddFriendByPhoneAdapter;
    private List<CPhoneContactUserBean> mContactFriendLst;
    private Activity mAct;
    private List<ImpICPhoneContactView.ContactEntity> contactLst;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cphone_contact);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        mAct = this;
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText(R.string.phone_contact_title);
        contactListView.setMode(PullToRefreshBase.Mode.DISABLED);
        EmptyView emptyView = new EmptyView(mAct);
        emptyView.setContent("您的好友暂时还没有开通哦~");
        emptyView.setImageId(R.drawable.bg_wutongxunluhaoyou);
        contactListView.setEmptyView(emptyView);

        mContactFriendLst = new ArrayList<>();
        mCAddFriendByPhoneAdapter = new CAddFriendByPhoneAdapter(mAct, mContactFriendLst);
        contactListView.setAdapter(mCAddFriendByPhoneAdapter);

        getFriendByContact();
    }

    private void getFriendContactByPartial(final List<String> phoneNos, final int perNum)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
//                        pDialog.show();
                    }
                });
                if (phoneNos.size() <= perNum)
                {
                    if (mPresenter != null)
                        mPresenter.getFriendByContact(CommonUtils.listToStrWishSeparator(phoneNos, CommonUtils.SEPARATOR));
                }
                else
                {
                    Logger.t(TAG).d("开始分份");
                    int parties = 0;
                    //份数
                    //可以整除
                    if (phoneNos.size() % perNum == 0)
                    {
                        parties = phoneNos.size() / perNum;
                    }
                    else
                    {
                        parties = phoneNos.size() / perNum + 1;
                    }
                    Logger.t(TAG).d("PARTIAL: " + parties);
                    int lastIndex = perNum;
                    for (int i = 0; i < parties; i++)
                    {
                        if (lastIndex < phoneNos.size())
                        {
                            lastIndex = perNum * (i + 1);
                            if (mPresenter != null)
                                mPresenter.getFriendByContact(CommonUtils.listToStrWishSeparator(
                                        phoneNos.subList(perNum * i, perNum * (i + 1)), CommonUtils.SEPARATOR));
                        }
                        else
                        {
                            if (mPresenter != null)
                                mPresenter.getFriendByContact(CommonUtils.listToStrWishSeparator(
                                        phoneNos.subList(lastIndex, phoneNos.size()), CommonUtils.SEPARATOR));
                        }
                        try
                        {
                            Thread.sleep(300);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();

    }

    /**
     * 从后台获得已经注册过《看脸吃饭》的用户信息
     */
    private void getFriendByContact()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                if (mPresenter != null)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 0, null);
                        }
                    });
                    mPresenter.getPhoneContactLst();
                }
            }
        }.start();

    }


    private void setUiContent(List<CPhoneContactUserBean> cLst)
    {
        //将通讯录中的联系人姓名添加到数据源中
        for (int i = 0; i < cLst.size(); i++)
        {
            for (ImpICPhoneContactView.ContactEntity ce : contactLst)
            {
                if (cLst.get(i).getMobile().equals(ce.getPhoneNo()))
                {
                    cLst.get(i).setContactName(ce.getName());
                    break;
                }
            }
        }
        mContactFriendLst.addAll(cLst);
        mCAddFriendByPhoneAdapter.notifyDataSetChanged();
    }

    //权限回调
/*    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        Logger.t(TAG).d("权限回调 " + requestCode);
        switch (requestCode)
        {
            case 3:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Logger.t(TAG).d("回调获得权限");
                    getFriendByContact();
                }
                else
                {
                    ToastUtils.showShort("未获得查看联系人权限，此功能不可用，请打开权限");
                }
                break;
            default:
                break;
        }
    }*/

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
//        if (pDialog != null && pDialog.isShowing())
//        {
//            pDialog.dismiss();
//            pDialog = null;
//        }
    }

    @Override
    protected ImpICPhoneContactView createPresenter()
    {
        return new ImpICPhoneContactView();
    }



    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mAct, null, interfaceName, e);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void getFriendByContactCallback(List<CPhoneContactUserBean> response)
    {
        setUiContent(response);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
    }

    @Override
    public void getPhoneContactLstCallback(List<ImpICPhoneContactView.ContactEntity> contactLst)
    {
        //contactLst 为空 是没有权限 Cursor 返回 就是空  如果 有权限 cursor 会返回0
        if (contactLst == null)
        {
            LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
            String body = "没有获取到联系人，可能是读取联系人权限被禁止，请到权限设置面板设置允许访问通讯录";
            new CustomAlertDialog(mAct)
                    .builder()
                    .setTitle("权限提示")
                    .setMsg(body)
                    .setCancelable(false)
                    .setPositiveButton("确认", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {

                        }
                    }).show();
            return;
        }
        this.contactLst = contactLst;
        Logger.t(TAG).d(contactLst.toString());
        final List<String> phoneNos = new ArrayList<>();
        for (ImpICPhoneContactView.ContactEntity c : contactLst)
        {
            phoneNos.add(c.getPhoneNo().replaceAll("\\s+", ""));
        }
        final String result = CommonUtils.listToStrWishSeparator(phoneNos, CommonUtils.SEPARATOR);
        Logger.t(TAG).d("phones:" + result);
        if (mPresenter != null)
        {
            mPresenter.getFriendByContact(result);
        }
    }
}
