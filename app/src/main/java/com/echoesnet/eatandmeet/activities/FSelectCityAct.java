package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;

import com.echoesnet.eam.icontextmodule.model.EchoesEamIcon;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.SelectCityAdapter;
import com.echoesnet.eatandmeet.views.widgets.MultiGridView;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.joanzapata.iconify.IconDrawable;
import com.zhy.autolayout.AutoLinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class FSelectCityAct extends BaseActivity
{
    private static final String TAG = FSelectCityAct.class.getSimpleName();

    @BindView(R.id.ll_search)
    AutoLinearLayout ll_search;
    @BindView(R.id.iv_delete)
    ImageView iv_delete;
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.mgv_city)
    MultiGridView mgvCity;
    @BindView(R.id.tv_current_city)
    TextView currentCity;
    private Activity mActivity;
    private SelectCityAdapter adapter;
    private String[] cityData = {"北京", "上海", "广州", "深圳", "西安", "杭州", "成都", "重庆"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_city);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }


    void initAfterView()
    {
        mActivity = this;
        topBar.setTitle(getResources().getString(R.string.find_city));
        topBar.getRightButton().setVisibility(View.GONE);
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                FSelectCityAct.this.finish();
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
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

        iv_search.setImageDrawable(new IconDrawable(FSelectCityAct.this, EchoesEamIcon.eam_s_search).colorRes(R.color.c4));

        et_search.addTextChangedListener(new MyEditTextListener());
        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    if (!et_search.getText().toString().trim().equals(""))
                    {
                        // 先隐藏键盘
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(FSelectCityAct.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        search();
                        return true;
                    }
                    else
                    {
                        ToastUtils.showShort("请输入城市名、拼音或首字母查询");
                    }

                }
                return false;
            }
        });

        adapter = new SelectCityAdapter(FSelectCityAct.this, cityData);
        mgvCity.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (position == 4)
                {
                    ToastUtils.showShort( "西安城市即将开放,敬请期待");
                }
            }
        });
        mgvCity.setAdapter(adapter);
        currentCity.setText("天津");
    }

    @OnItemClick ({R.id.mgv_city})
    void onItemClick(final int position)
    {
        /*String cityName = cityData[position];
        currentCity.setText(cityName);*/
    }

    @OnClick ({R.id.ll_search, R.id.iv_search})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ll_search:
                et_search.setText("");
                break;
            case R.id.iv_search:

                search();
                break;
        }
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
            if (et_search.getText().length() != 0)
            {
                if (iv_delete.getVisibility() == View.GONE)
                {
                    iv_delete.setVisibility(View.VISIBLE);
                }
                iv_delete.setImageDrawable(new IconDrawable(FSelectCityAct.this, EchoesEamIcon.eam_s_close2).colorRes(R.color.FC3));
            }
            else
            {
                if (iv_delete.getVisibility() == View.VISIBLE)
                {
                    iv_delete.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
    }

    private void search()
    {
        String searchContent = et_search.getText().toString().trim();
        if (!TextUtils.isEmpty(searchContent))
        {
        }
        else
        {
            ToastUtils.showShort("请输入搜索内容");
        }
    }

}
