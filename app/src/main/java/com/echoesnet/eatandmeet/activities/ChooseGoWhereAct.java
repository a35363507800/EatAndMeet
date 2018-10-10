package com.echoesnet.eatandmeet.activities;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.SearchRestaurantBean;
import com.echoesnet.eatandmeet.presenters.ImpIChooseGoWhereView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IChooseGoWhereView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.RoomSearchDetailAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.joanzapata.iconify.IconDrawable;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

import static com.echoesnet.eatandmeet.R.id.iv_search;

public class ChooseGoWhereAct extends MVPBaseActivity<IChooseGoWhereView, ImpIChooseGoWhereView> implements IChooseGoWhereView
{
    private static final String TAG = ChooseGoWhereAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.ll_search)
    AutoLinearLayout llSearch;
    @BindView(R.id.et_search)
    EditText etSearch;

    @BindView(R.id.lv_search)
    PullToRefreshListView lvSearch;
    @BindView(R.id.iv_delete)
    ImageView ivDelete;

    private final String PAGE_COUNT = "20";

    private Dialog pDialog;
    private Activity mAct;
    private RoomSearchDetailAdapter adapter;
    private List<SearchRestaurantBean> searchList;
    private boolean isSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_go_where_act);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected ImpIChooseGoWhereView createPresenter()
    {
        return new ImpIChooseGoWhereView();
    }


    private void initAfterView()
    {
        mAct = this;
        topBar.setTitle(getResources().getString(R.string.choose_rest));
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
        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理...");
        pDialog.setCancelable(false);

        searchList = new ArrayList<>();
        adapter = new RoomSearchDetailAdapter(ChooseGoWhereAct.this, searchList);
        lvSearch.setAdapter(adapter);

        lvSearch.setMode(PullToRefreshBase.Mode.BOTH);
        lvSearch.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (isSearch)
                {
                    if (mPresenter != null)
                        mPresenter.searchResList("0", PAGE_COUNT, etSearch.getText().toString(), "refresh");
                }
                else
                {
                    if (mPresenter != null)
                        mPresenter.getRes("0", PAGE_COUNT, "", "refresh");
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                if (isSearch)
                {
                    if (mPresenter != null)
                        mPresenter.searchResList(searchList.size() + "", PAGE_COUNT, etSearch.getText().toString(), "add");
                }
                else
                {
                    if (mPresenter != null)
                        mPresenter.getRes(searchList.size() + "", PAGE_COUNT, "", "add");
                }
            }
        });

        View empty = LayoutInflater.from(mAct).inflate(R.layout.empty_view, null);
        ((TextView) empty.findViewById(R.id.tv_default_des)).setText("暂时没有搜索结果");
        lvSearch.setEmptyView(empty);

        lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                    position = position - 1;
           //        Logger.t(TAG).d("resName:"+searchList.get(position).getrName());
                    Intent intent = new Intent(mAct,UserWantToGoEditAct.class);
                    intent.putExtra("resName", searchList.get(position).getrName());
                    intent.putExtra("goResId", searchList.get(position).getrId());
                    setResult(RESULT_OK, intent);
                    finish();
            }
        });


        etSearch.addTextChangedListener(new MyEditTextListener());
        etSearch.setOnEditorActionListener(onEditorActionListener);

        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (mPresenter != null)
            mPresenter.getRes("0", PAGE_COUNT, "", "refresh");
    }




    @OnClick({R.id.ll_search, iv_search})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_search:
                etSearch.setText("");
                isSearch = false;
                break;
            default:
                break;
        }
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mAct, getString(R.string.pay_fault_due_to_net), interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        lvSearch.onRefreshComplete();
    }

    @Override
    public void getResCallBack(ArrayMap<String, Object> map)
    {
        if (!map.get("operateType").equals("add"))
        {
            searchList.clear();
        }
        searchList.addAll((Collection) map.get("searchResult"));
        adapter.notifyDataSetChanged();
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        lvSearch.onRefreshComplete();
    }

    @Override
    public void searchResCallBack(ArrayMap<String, Object> map)
    {
        lvSearch.onRefreshComplete();
        if (map.get("type").equals("refresh"))
        {
            searchList.clear();
        }
        searchList.addAll((Collection) map.get("response"));
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        adapter.notifyDataSetChanged();
    }

    class MyEditTextListener implements TextWatcher
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
            if (etSearch.getText().length() != 0)
            {
                if (ivDelete.getVisibility() == View.GONE)
                {
                    ivDelete.setVisibility(View.VISIBLE);
                }
                ivDelete.setImageDrawable(new IconDrawable(ChooseGoWhereAct.this, EchoesEamIcon.eam_s_close2).colorRes(R.color.FC3));
            }
            else
            {
                if (ivDelete.getVisibility() == View.VISIBLE)
                {
                    ivDelete.setVisibility(View.GONE);
                }
                if (mPresenter != null)
                    mPresenter.getRes("0", PAGE_COUNT, "", "refresh");
            }
        }
    }

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener()
    {


        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                if (!TextUtils.isEmpty(etSearch.getText().toString().trim()))
                {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ChooseGoWhereAct.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    search();
                    isSearch = true;
                    return true;
                }
                else
                {
                    ToastUtils.showShort("请输入餐厅名称");
                }
            }
            return false;
        }
    };

    private void search()
    {
        String searchContent = etSearch.getText().toString().trim();
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (mPresenter != null)
            mPresenter.searchResList("0", PAGE_COUNT, searchContent, "refresh");
    }

}
