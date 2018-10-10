package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ConversationBean;
import com.echoesnet.eatandmeet.models.bean.SearchUserBean;
import com.echoesnet.eatandmeet.presenters.ImpICSearchUserView;
import com.echoesnet.eatandmeet.presenters.ManagerConversion;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICSearchConversationView;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.SearchBar;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.SearchUserAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.joanzapata.iconify.widget.IconTextView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017/7/25 20:15
 * @description
 */

public class CSearchConversationAct extends MVPBaseActivity<ICSearchConversationView, ImpICSearchUserView> implements ICSearchConversationView
{
    private static final String TAG = CSearchConversationAct.class.getSimpleName();
    @BindView(R.id.tab_bar)
    TopBarSwitch topBar;
    //    @BindView(R.id.cl_filter_cons)
//    ConversationList clFilterCons;
    @BindView(R.id.rl_live_content)
    PullToRefreshListView clFilterCons;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.search_empty_view)
    EmptyView searchEmptyView;


    private final String DEFAULT_NUM = "20";

    private SearchBar searchBar;
    private List<SearchUserBean> dataSource = new ArrayList<>();

    private SearchUserAdapter adapter;
    private String keywordTemp;
    private Activity mAct;
    private boolean isClear = false;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_chat_conversation);
        ButterKnife.bind(this);
        this.mAct = this;
        View customView = topBar.inflateCustomCenter(R.layout.include_search_bar, null);
        searchBar = (SearchBar) customView.findViewById(R.id.search_bar);
        searchBar.setHint("请输入TA的看脸ID或用户名");
        TextView tvCancel = (TextView) customView.findViewById(R.id.tv_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        topBar.getNavBtns2(new int[]{0, 0, 0, 0});

        searchEmptyView.setContent("暂无相关信息，请再次输入哦~");
//        searchEmptyView.setImageId(R.drawable.bg_nochat);

        searchBar.setSearchTriggerListener(new SearchBar.ISearchTriggerListener()
        {
            @Override
            public void searching(String keyword)
            {
                if (TextUtils.isEmpty(keyword.trim()))
                {
                    dataSource.clear();
                    adapter.notifyDataSetChanged();
                    clFilterCons.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    searchEmptyView.setVisibility(View.GONE);
                }
                else
                {
                    isClear = true;
                    adapter.setSearchKey(keyword);
                    search(keyword);
                    keywordTemp = keyword;
                }
            }
        });
        clFilterCons.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        clFilterCons.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>()
        {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView)
            {
                isClear = false;
                mPresenter.searchUser(dataSource.size() + "", DEFAULT_NUM, keywordTemp);
            }
        });
        adapter = new SearchUserAdapter(mAct, dataSource);
        adapter.setOnFocusClickListener(new SearchUserAdapter.OnFocusClickListener()
        {
            @Override
            public void onFocusClick(int position, View view)
            {
                if (mPresenter != null)
                    mPresenter.focusPerson(dataSource, "1", position, view);
            }
        });
        ListView listView = clFilterCons.getRefreshableView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                position = position - 1;
                Intent intent = new Intent(mAct, CNewUserInfoAct.class);
                intent.putExtra("checkWay", "UId");
                intent.putExtra("toUId", dataSource.get(position).getUId());
                startActivity(intent);
            }
        });
        clFilterCons.setAdapter(adapter);
//        clFilterCons.init(dataSource, emptyView);
        //emptyView.setContent("暂无相关信息，请再次输入哦~");
    }

    @Override
    protected ImpICSearchUserView createPresenter()
    {
        return new ImpICSearchUserView(mAct, this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!TextUtils.isEmpty(keywordTemp))
        {
            mPresenter.searchUser("0", dataSource.size() + "", keywordTemp);
        }

    }

    private void search(final String keyword)
    {
        mPresenter.searchUser("0", DEFAULT_NUM, keyword);

        /*getConversationByType(new Consumer<List<ConversationBean>>()
        {
            @Override
            public void accept(List<ConversationBean> conversations) throws Exception
            {
                Logger.t(TAG).d("数据》" + conversations.toString());
                Observable.fromIterable(conversations)
                        .doOnDispose(new Action()
                        {
                            @Override
                            public void run() throws Exception
                            {
                                Logger.t(TAG).d("Unsubscribing subscription from onCreate()");
                            }
                        })
                        .filter(new Predicate<ConversationBean>()
                        {
                            @Override
                            public boolean test(ConversationBean conversation) throws Exception
                            {
                                if (TextUtils.isEmpty(keyword.trim()))
                                    return false;
                                else
                                    return conversation.getNickName().contains(keyword) || conversation.getId().contains(keyword);
                            }
                        })
                        .toList()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(CSearchConversationAct.this.<List<ConversationBean>>bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(new Consumer<List<ConversationBean>>()
                        {
                            @Override
                            public void accept(List<ConversationBean> conversationBeen) throws Exception
                            {
                                dataSource.clear();
                                dataSource.addAll(conversationBeen);
                                if (dataSource.size() == 0)
                                    emptyView.setText("没有搜索到相关信息，换个字符试一下~");
                                clFilterCons.refreshUI();
                            }
                        });
            }
        });*/
    }

    /**
     * 加载数据
     *
     * @param consumer
     */
    private void getConversationByType(Consumer<List<ConversationBean>> consumer)
    {
        Observable.fromIterable(EMClient.getInstance().chatManager().getConversationsByType(EMConversation.EMConversationType.Chat))
                .filter(new Predicate<EMConversation>()
                {
                    @Override
                    public boolean test(EMConversation conversation) throws Exception
                    {
                        boolean result = false;
                        List<EMMessage> msgs = conversation.getAllMessages();
                        if (msgs.isEmpty())
                            return result;
                        EMMessage lastMsgFromOther = conversation.getLatestMessageFromOthers();
                        if (lastMsgFromOther == null)//说明此会话里面只包含自己发送的消息，对方没有回应，处于聊天列表中
                            result = true;
                        else
                            result = lastMsgFromOther.getBooleanAttribute(EamConstant.EAM_CHAT_ATTR_HELLO, false) == false;
                        return result;
                    }
                })
                .map(new Function<EMConversation, ConversationBean>()
                {
                    @Override
                    public ConversationBean apply(EMConversation conversation) throws Exception
                    {
                        return ManagerConversion.emcon2Conbean(conversation);
                    }
                })
                .sorted(new Comparator<ConversationBean>()
                {
                    @Override
                    public int compare(ConversationBean o1, ConversationBean o2)
                    {
                        long time1 = o1.getTime();
                        long time2 = o2.getTime();
                        if (time1 == time2)
                        {
                            return 0;
                        }
                        else if (time2 > time1)
                        {
                            return 1;
                        }
                        else
                        {
                            return -1;
                        }
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<List<ConversationBean>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(consumer);
    }

    @Override
    public void searchUserCallback(List<SearchUserBean> list)
    {
        clFilterCons.onRefreshComplete();
        if (isClear)
            dataSource.clear();
        dataSource.addAll(list);
        if (dataSource.size() == 0)
        {
            searchEmptyView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            clFilterCons.setVisibility(View.GONE);
        }
        else
        {
            clFilterCons.setVisibility(View.VISIBLE);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void focusCallBack(int position, View clickView)
    {
        if (!mAct.isFinishing())
        {
            View view = LayoutInflater.from(mAct).inflate(R.layout.toast_ok_bg, null);
            IconTextView tvIcon = (IconTextView) view.findViewById(R.id.toast_bg_g);
            IconTextView tvContent = (IconTextView) view.findViewById(R.id.toast_content);
            tvIcon.setTextSize(60);
            tvContent.setText("关注成功");
            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
            ToastUtils.showCustomShortSafe(view);
            ToastUtils.cancel();
        }
        dataSource.get(position).setFocus("1");
        adapter.notifyDataSetChanged();
    }
}
