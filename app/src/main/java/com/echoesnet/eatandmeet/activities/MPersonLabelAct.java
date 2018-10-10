package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.PresonLabelAdapter;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoRelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MPersonLabelAct extends BaseActivity
{
    public final static String TAG = MPersonLabelAct.class.getSimpleName();
    private static final String SHAREDNAME = "LabelsHistory";
    private final int MAX_NUM = 6;
    private ArrayList<String> listTemp = new ArrayList<String>();
    private String[] labels = {"大叔", "暴力萝莉", "筋肉男", "御姐范", "工作狂魔", "小吃货", "永远18岁", "欧美范", "土豪"};
    private List<String> searchList;
    @BindView(R.id.arl_setup)
    AutoRelativeLayout arlSetup;
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.lv_labels)
    ListView lvLabels;

    private PresonLabelAdapter adapter;
    private ArrayList<String> labList;
    private String etResult;
    private Activity mContext;
    private ArrayList<String> listLab = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_preson_label);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        adapter = new PresonLabelAdapter(this, searchList, listTemp, labList);
        lvLabels.setAdapter(adapter);
        initListener();

    }

    private void initAfterView()
    {
        mContext = this;
        searchList = new ArrayList<String>();
        //服务器返回数据,从上个页面获得标签
        labList = getIntent().getStringArrayListExtra("lab");

        //把传进的值也添加到将要提交的集合中，否则提交不会提交原有的值
        listTemp.addAll(labList);



        String listHistory = SharePreUtils.getPersonLabels(mContext);
        if (TextUtils.isEmpty(listHistory))
        {
            for (int i = 0; i < labels.length; i++)
            {
                searchList.add(labels[i]);
            }
            try
            {
                String listStr = CommonUtils.sceneListToString(searchList);
                SharePreUtils.setPersonLabels(mContext, listStr);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                searchList = CommonUtils.stringToSceneList(listHistory);
            } catch (IOException e)
            {
                e.printStackTrace();
            } catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        //无重复并集
        if (labList!=null&&searchList!=null)
        {
            labList.removeAll(searchList);
            searchList.addAll(labList);
        }
        etInput.addTextChangedListener(new TextWatcher()
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
                etResult = etInput.getText().toString().trim();
            }
        });

        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setText(getResources().getString(R.string.myinfo_edit_submit));
        topBar.getRightButton().setTextColor(Color.WHITE);
        topBar.setTitle(getResources().getString(R.string.preson_label_title));
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                for (int i = 0; i < listTemp.size(); i++)
                {
                    resultList.add(listTemp.get(i));
                }
                resultList.clear();
                Intent intent = new Intent();
                intent.putStringArrayListExtra("temp", listTemp);
                MPersonLabelAct.this.setResult(RESULT_OK, intent);
                MPersonLabelAct.this.finish();
                mContext.finish();
            }

            @Override
            public void left2Click(View view)
            {

            }

            @Override
            public void rightClick(View view)
            {
                for (int i = 0; i < listTemp.size(); i++)
                {
                    resultList.add(listTemp.get(i));
                }
                Intent intent = new Intent();
                intent.putStringArrayListExtra("temp", resultList);
                MPersonLabelAct.this.setResult(RESULT_OK, intent);
                MPersonLabelAct.this.finish();
            }
        });

    }

    @OnClick({R.id.arl_setup})
    void click(View view)
    {
        switch (view.getId())
        {
            case R.id.arl_setup:
                search(etInput);
                break;
        }
    }

    private void initListener()
    {
        lvLabels.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                PresonLabelAdapter.ViewHolder holder = (PresonLabelAdapter.ViewHolder) view.getTag();
                if (listTemp.size() >= MAX_NUM)
                {
//                    holder.cb_label.toggle(); // 每次获取点击的item时改变checkbox的状态
                    if (!removeData(searchList.get(position)))
                    {
                        holder.cb_label.setChecked(false);
                        ToastUtils.showShort("最多选择"+MAX_NUM+"个标签");
                    }
                    else
                    {
                        holder.cb_label.toggle(); // 每次获取点击的item时改变checkbox的状态
                        PresonLabelAdapter.isSelected.put(position, false); // 同时修改map的值
                    }
//                    return;
                }
                else
                {
                    holder.cb_label.toggle(); // 每次获取点击的item时改变checkbox的状态
                    Logger.t(TAG).d("holder.cb_label.isChecked():" + holder.cb_label.isChecked());
                    PresonLabelAdapter.isSelected.put(position, holder.cb_label.isChecked()); // 同时修改map的值
                }

                if (holder.cb_label.isChecked())
                {
                    listTemp.add(searchList.get(position));
                    holder.cb_label.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.choise_lanyuanda_yes_xhdpi));
                }
                else
                {
                    listTemp.remove(searchList.get(position));
                    holder.cb_label.setButtonDrawable(ContextCompat.getDrawable(mContext, R.drawable.choise_lanyuanda_no_xhdpi));
                }
            }
        });
    }

    private boolean removeData(String imageItem)
    {
        if (listTemp.contains(imageItem))
        {
            listTemp.remove(imageItem);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            for (int i = 0; i < listTemp.size(); i++)
            {
                resultList.add(listTemp.get(i));
            }
            resultList.clear();
            Intent intent = new Intent();
            intent.putStringArrayListExtra("temp", listTemp);
            MPersonLabelAct.this.setResult(RESULT_OK, intent);
            MPersonLabelAct.this.finish();
            Logger.t(TAG).d("关闭当前");
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    ArrayList<String> resultList = new ArrayList<String>();

    private void search(EditText editText)
    {
        String searchContent = editText.getText().toString().trim();
        if (!TextUtils.isEmpty(searchContent))
        {
            if (!searchList.contains(searchContent))
            {
                searchList.add(searchContent);
            }
            else
            {
                for (int i = 0; i < searchList.size(); i++)
                {
                    if (searchContent.equals(searchList.get(i)))
                    {
                        ToastUtils.showShort("添加标签重复");
                        return;
                    }
                }
                searchList.add(searchContent);
            }

            try
            {
                String listStr = CommonUtils.sceneListToString(searchList);
                SharePreUtils.setPersonLabels(mContext, listStr);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            etInput.setText("");
        }
        else
        {
            ToastUtils.showShort("请输入添加内容");
            return;
        }
    }

}
