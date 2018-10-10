package com.echoesnet.eatandmeet.activities.live;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.DFlashPayInputAct;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.models.bean.EaseUser;
import com.echoesnet.eatandmeet.presenters.ImpILiveChooseAnchorView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveChooseAnchorView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.LiveChooseAnchorAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.SideBar;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.iconify.IconDrawable;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


public class LiveChooseAnchorAct extends MVPBaseActivity<ILiveChooseAnchorView, ImpILiveChooseAnchorView> implements ILiveChooseAnchorView
{
    private static final String TAG = LiveChooseAnchorAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.ll_search)
    AutoLinearLayout llSearch;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;
    @BindView(R.id.sidebar)
    SideBar sideBar;
    @BindView(R.id.text_dialog)
    TextView textDialog;
    @BindView(R.id.ptrListView)
    PullToRefreshListView ptrListView;

    private ListView listView;


    //List<EaseUser> signedAnchorList = new ArrayList<>();
    List<EaseUser> anchorList = new ArrayList<>();
    private Dialog pDialog;
    private Activity mAct;
    private String PAGE_COUNT = "20";
    private List<EaseUser> hostList = new ArrayList<>();
    private List<EaseUser> freeList = new ArrayList<>();
    private LiveChooseAnchorAdapter adapter;

    //    private ChooseSignedAnchorAdapter signedAnchorAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_live_choose_anchor);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected ImpILiveChooseAnchorView createPresenter()
    {
        return new ImpILiveChooseAnchorView();
    }

    private void initAfterViews()
    {
        mAct = this;
        pDialog = DialogUtil.getCommonDialog(mAct, "正在获取...");
        pDialog.setCancelable(false);
        topBar.setTitle(getResources().getString(R.string.choose_anchor));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
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

        sideBar.setTextView(textDialog);
        sideBar.setOnTouchingLetterChangedListener(letterChangedListener);

        ivSearch.setImageDrawable(new IconDrawable(mAct, EchoesEamIcon.eam_s_search).colorRes(R.color.c4));
        listView = ptrListView.getRefreshableView();
        this.adapter = new LiveChooseAnchorAdapter(mAct, anchorList);
        listView.setAdapter(adapter);
        ptrListView.setMode(PullToRefreshBase.Mode.BOTH);
        ptrListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (mPresenter != null)
                    mPresenter.getAnchor("0", freeList.size() + "", "refresh");
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
//                liveChooseAnchorView.getAnchor("0", PAGE_COUNT, "refresh");
                if (mPresenter != null)
                    mPresenter.getAnchor(freeList.size() + "", PAGE_COUNT, "add");
            }
        });
        initView();
        SetEditTextSearchListener();
        ptrListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                position = position - 1;
                Intent intent = new Intent(LiveChooseAnchorAct.this, DFlashPayInputAct.class);
                intent.putExtra("recommendHostUId", anchorList.get(position).getuId());
                intent.putExtra("id", anchorList.get(position).getId());
                intent.putExtra("uphUrl", anchorList.get(position).getAvatar());
                intent.putExtra("nicName", anchorList.get(position).getNickName());
                setResult(RESULT_OK, intent);
                startActivity(intent);
                finish();
            }
        });
//        signedAnchorAdapter = new ChooseSignedAnchorAdapter(mAct,signedAnchorList);
        //hotListView.setAdapter(signedAnchorAdapter);
        if (pDialog != null && !pDialog.isShowing())
        {
            pDialog.show();
        }
        if (mPresenter != null)
            mPresenter.getAnchor("0", PAGE_COUNT, "refresh");
    }

    @OnClick(R.id.iv_delete)
    void clickEvent(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_delete:
                etSearch.setText("");
                ptrListView.setMode(PullToRefreshBase.Mode.BOTH);
                break;
            default:
                break;
        }
    }

    private void SetEditTextSearchListener()
    {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if (!TextUtils.isEmpty(etSearch.getText().toString().trim()))
                    {
                        // 先隐藏键盘
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(mAct.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        ptrListView.setMode(PullToRefreshBase.Mode.DISABLED);
                        if (mPresenter != null)
                            mPresenter.searchAnchor(etSearch.getText().toString().trim());
                        return true;
                    } else
                    {
                        ToastUtils.showShort("请输入主播昵称或ID");
                    }
                }
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                Logger.t(TAG).d("输入的搜索内容：" + s);
                if (etSearch.getText().length() != 0)
                {
                    if (ivDelete.getVisibility() == View.GONE)
                    {
                        ivDelete.setVisibility(View.VISIBLE);
                    }
                    ivDelete.setImageDrawable(new IconDrawable(mAct, EchoesEamIcon.eam_s_close2).colorRes(R.color.FC3));
                } else
                {
                    if (ivDelete.getVisibility() == View.VISIBLE)
                    {
                        ivDelete.setVisibility(View.GONE);
                    }
                    Logger.t(TAG).d("清空输入框后freeList.size()" + freeList.size());
                    if (mPresenter != null)
                        mPresenter.getAnchor("0", freeList.size() + "", "refresh");
                }

            }
        });
    }

    private void initView()
    {
//        listView = contactListLayout.getListView();
        LinearLayout empty = (LinearLayout) findViewById(R.id.empty_view);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("没有相应主播，请换个名字试试~");
        listView.setEmptyView(empty);
//        contactListLayout.init(anchorList);
    }

    /**
     * get contact list and sort, will filter out users in blacklist
     */
    private void getContactList()
    {
        // sorting
        Collections.sort(anchorList, new Comparator<EaseUser>()
        {
            @Override
            public int compare(EaseUser lhs, EaseUser rhs)
            {

                if (lhs.getInitialLetter().equals(rhs.getInitialLetter()))
                {
                    //if (TextUtils.isEmpty(lhs.getInitialLetter()))
                    if ("1".equals(lhs.getUserType()))
                        return rhs.getMealTotal() - lhs.getMealTotal();
                    else
                        return lhs.getNickName().compareTo(rhs.getNickName());
                } else
                {
                    if ("#".equals(lhs.getInitialLetter()))
                    {
                        return 1;
                    } else if ("#".equals(rhs.getInitialLetter()))
                    {
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }
            }
        });

    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        ptrListView.onRefreshComplete();
        NetHelper.handleNetError(mAct, "", interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getAnchorCallBack(ArrayMap<String, Object> response)
    {
        if (ptrListView != null && ptrListView.isRefreshing())
            ptrListView.onRefreshComplete();
        if (response.get("operateType").equals("refresh"))
        {
            anchorList.clear();
        }


            //signedAnchorList.addAll((Collection<? extends EaseUser>) response.get("signedAnchorList"));
            //signedAnchorAdapter.notifyDataSetChanged();
            Logger.t(TAG).d("map:" + response.toString());
            List<EaseUser> signedList = new ArrayList<>();
            signedList.addAll((ArrayList<EaseUser>) response.get("signedAnchorList"));
            freeList.clear();
            freeList.addAll((ArrayList<EaseUser>) response.get("freeAnchorList"));
            for (EaseUser sbean : signedList)
            {
                sbean.setUserType("1");
            }
            for (EaseUser EaseUser : signedList)
            {
                if (!hostList.contains(EaseUser))
                {
                    hostList.add(EaseUser);
                }
            }
            for (EaseUser EaseUser : this.freeList)
            {
                if (!hostList.contains(EaseUser))
                {
                    hostList.add(EaseUser);
                }
            }
            anchorList.clear();
            for (EaseUser anchorBean : this.hostList)
            {
                CommonUtils.setUserInitialLetter(anchorBean);
                anchorList.add(anchorBean);
            }
            getContactList();

            dataFormat();

            adapter.notifyDataSetChanged();
//            contactListLayout.refresh();
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }

    @Override
    public void searchAnchorCallback(String response)
    {
        try
        {
            Logger.t(TAG).d("搜索返回结果：" + response);
            JSONArray array = new JSONArray(response);
            anchorList.clear();
            for (int i = 0; i < array.length(); i++)
            {
                JSONObject obj = array.getJSONObject(i);
                EaseUser sbean = new EaseUser("");
                sbean.setuId(obj.getString("uId"));
                sbean.setNickName(obj.getString("nicName"));
                sbean.setAvatar(obj.getString("uphUrl"));
                sbean.setId(obj.getString("id"));
                sbean.setUserType(obj.getString("sign"));
                sbean.setMealTotal(Integer.parseInt(obj.getString("mealTotal")));
                CommonUtils.setUserInitialLetter(sbean);
                anchorList.add(sbean);
            }
            getContactList();
            dataFormat();

            adapter.notifyDataSetChanged();
//                contactListLayout.refresh();

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 此代码为整理数据，将数据分组，来显示分组效果    ---  yqh
     * 例  AAABBBCCC  给他们的tag设置为122122122 来显示隐藏头部  1显示  2隐藏   笨办法，
     */
    private void dataFormat()
    {
        String tempHeader = "";
        int index = 0;
        for (EaseUser easeUser : anchorList)
        {
            if (!tempHeader.equals(easeUser.getInitialLetter()))
            {
                index = 0;
                tempHeader = easeUser.getInitialLetter();
            }
            if (tempHeader.equals(easeUser.getInitialLetter()))
            {
                if (index == 0)
                    easeUser.setTag("1");
                else
                    easeUser.setTag("2");
                index += 1;
            }
        }
    }

    SideBar.OnTouchingLetterChangedListener letterChangedListener = new SideBar.OnTouchingLetterChangedListener()
    {
        @Override
        public void onTouchingLetterChanged(String s)
        {
            //该字母首次出现的位置
            int position = adapter.getPositionForSection(s);
            if (position != -1)
            {
                listView.setSelection(position + 1);
            }
        }
    };

}
