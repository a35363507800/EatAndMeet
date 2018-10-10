package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CollectBean;
import com.echoesnet.eatandmeet.presenters.ImpMyInfoCollectView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoCollectView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.EmptyView;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MyInfoCollectAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier zdw
 * @createDate
 * @description 我的收藏
 */

public class MyInfoCollectAct extends BaseActivity implements AdapterView.OnItemClickListener, IMyInfoCollectView
{
    private static final String TAG = MyInfoCollectAct.class.getSimpleName();
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.btn_delete)
    Button btnDelete;
    @BindView(R.id.drink_list)
    ListView listView;
    @BindView(R.id.emptyView)
    EmptyView emptyView;


    private MyInfoCollectAdapter adapter;
    private Dialog pDialog;
    private Activity mAct;
    private Boolean isShow = false;
    private TextView rightButton;
    private List<CollectBean> beanList;
    private ImpMyInfoCollectView impMyInfoCollectView;
    private int CurrentPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myinfo_collection);
        ButterKnife.bind(this);
        afterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
//        locationClient.stop();
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
        mAct = this;
        impMyInfoCollectView = new ImpMyInfoCollectView(mAct, this);
        List<Map<String, TextView>> navBtns = topBar.getNavBtns2(new int[]{1, 0, 0, 1});
        rightButton = navBtns.get(1).get(TopBarSwitch.NAV_BTN_ICON);
        rightButton.setText(getResources().getString(R.string.collect_right_title));
        rightButton.setTextSize(16);
        rightButton.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
                if (isShow)
                {
                    Message message = Message.obtain();
                    message.what = 0;
                    handler.sendMessage(message);
                    rightButton.setText(getResources().getString(R.string.collect_right_title));
                    btnDelete.setVisibility(View.GONE);
                } else
                {
                    Message message = Message.obtain();
                    message.what = 1;
                    handler.sendMessage(message);
                    rightButton.setText(getResources().getString(R.string.collect_cancel_title));
                    btnDelete.setVisibility(View.VISIBLE);
                    for (int i = 0; i < beanList.size(); i++)
                    {
                        if (beanList.get(i).isSelect())
                        {
                            beanList.get(i).setSelect(false);
                        }
                    }
                }
            }
        }).setText(getResources().getString(R.string.collect_title));
        listView.setOnItemClickListener(this);
        pDialog = DialogUtil.getCommonDialog(mAct, "正在处理");
        pDialog.setCancelable(false);


        if (impMyInfoCollectView != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            impMyInfoCollectView.getCollectData();
        }
    }

    @OnClick({R.id.btn_delete})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_delete:
                if (isShow)
                {
                    List<CollectBean> listTemp = new ArrayList<>();
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < beanList.size(); i++)
                    {
                        if (beanList.get(i).isSelect())
                        {
                            listTemp.add(beanList.get(i));
                            stringBuilder.append(beanList.get(i).getDelCollectId());
                            stringBuilder.append(CommonUtils.SEPARATOR);
                        }
                    }
                    beanList.removeAll(listTemp);
                    listTemp.clear();

                    for (int i = 0; i < beanList.size(); i++)
                    {
                        MyInfoCollectAdapter.getIsSelected().put(i, false);
                    }

                    adapter.notifyDataSetChanged();
                    if (beanList.size() == 0)
                    {
                        rightButton.setVisibility(View.GONE);
                        btnDelete.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        emptyView.setContent("您暂时没有收藏哦~");
                        emptyView.setImageId(R.drawable.bg_wushoucang);
                    }

                    if (TextUtils.isEmpty(stringBuilder.toString()))
                    {
                        Logger.t(TAG).d("选择餐厅的id为空");
                        ToastUtils.showShort("你还没有选择餐厅");
                    } else
                    {
                        String rids = stringBuilder.toString().substring(0, stringBuilder.toString().length() - CommonUtils.SEPARATOR.length());
                        Logger.t(TAG).d("选择餐厅的id--> " + stringBuilder.toString() + " , " + rids);
                        if (impMyInfoCollectView != null)
                        {
                            if (pDialog != null && !pDialog.isShowing())
                                pDialog.show();
                            impMyInfoCollectView.deleteCollectData(rids);
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (isShow)
        {
            Logger.t(TAG).d("不跳转, 显示多选框");
            // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
            MyInfoCollectAdapter.ViewHolder holder = (MyInfoCollectAdapter.ViewHolder) view.getTag();
            // 改变CheckBox的状态
            holder.cb.toggle();
            // 将CheckBox的选中状况记录下来
            MyInfoCollectAdapter.getIsSelected().put(position, holder.cb.isChecked());
            beanList.get(position).setSelect(holder.cb.isChecked());
            MyInfoCollectAdapter.setIsSelected(MyInfoCollectAdapter.getIsSelected());
            boolean isHasSelect = false;
            for (CollectBean collectBean : beanList)
            {
                if (collectBean.isSelect())
                {
                    isHasSelect = true;
                    break;
                }
            }
            btnDelete.setBackgroundColor(ContextCompat.getColor(mAct, isHasSelect ? R.color.C0317 : R.color.C0331));
        } else
        {
            CurrentPosition = position;
            Logger.t(TAG).d("跳转, 未显示多选框--> " + beanList.get(position).getLessPrice());
            String location = beanList.get(position).getPosxy();
            //String[] locationArr = location.split(",");
            SharePreUtils.setToOrderMeal(mAct, "noDate");
            Intent intent = null;
            if ("1".equals(beanList.get(position).getHomeparty()))
            {
                intent = new Intent(mAct, ClubDetailAct.class);
                intent.putExtra("clubId", beanList.get(position).getrId());
                startActivityForResult(intent,EamConstant.EAM_OPEN_HP_ACT);
            } else
            {
                intent = new Intent(mAct, DOrderMealDetailAct.class);
                intent.putExtra("restId", beanList.get(position).getrId());
                intent.putExtra("source", "myColloect");
                startActivityForResult(intent, EamConstant.EAM_OPEN_RES_ACT);
            }
            SharePreUtils.setSource(mAct, "myColloect");
            SharePreUtils.setRestId(mAct, beanList.get(position).getrId());

        }
    }

    private Handler handler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            if (msg.what == 1)
            {
                if (beanList != null)
                {
                    adapter = new MyInfoCollectAdapter(beanList, getApplicationContext(), true);
                    listView.setAdapter(adapter);
                    isShow = true;
                }
            } else if (msg.what == 0)
            {
                if (beanList != null)
                {
                    adapter = new MyInfoCollectAdapter(beanList, getApplicationContext(), false);
                    listView.setAdapter(adapter);
                    isShow = false;
                }
            }
        }

        ;
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_RES_ACT://打开餐厅
                if (data!=null)
                {
                   boolean isCollect = data.getBooleanExtra("isCollect",true);
                   if (!isCollect)
                   {
                       if (beanList!=null && CurrentPosition != -1)
                       {
                           beanList.remove(CurrentPosition);
                       }
                       if (adapter!=null)
                       adapter.notifyDataSetChanged();
//                       if (impMyInfoCollectView != null)
//                       {
//                           if (pDialog != null && !pDialog.isShowing())
//                               pDialog.show();
//                           impMyInfoCollectView.getCollectData();
//                       }
                   }
                }
                break;
            case EamConstant.EAM_OPEN_HP_ACT://打开沙龙
                if (data!=null)
                {
                    boolean isCollect = data.getBooleanExtra("isCollect",true);
                    if (!isCollect)
                    {
                        if (beanList!=null && CurrentPosition != -1)
                        {
                            beanList.remove(CurrentPosition);
                        }
                        if (adapter!=null)
                        adapter.notifyDataSetChanged();
//                        if (impMyInfoCollectView != null)
//                        {
//                            if (pDialog != null && !pDialog.isShowing())
//                                pDialog.show();
//                            impMyInfoCollectView.getCollectData();
//                        }
                    }
                }
                break;
                default:
                    break;
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {
        NetHelper.handleNetError(mAct, null, interfaceName, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getCollectDataCallback(List<CollectBean> response)
    {
        if (response == null)
        {
            ToastUtils.showShort("获取收藏信息失败");
        } else
        {
            beanList = response;
            if (beanList.size() == 0)
            {
                rightButton.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setContent("您暂时没有收藏哦~");
                emptyView.setImageId(R.drawable.bg_wushoucang);
                beanList.clear();
                adapter = new MyInfoCollectAdapter(beanList, getApplicationContext(), isShow);
                listView.setAdapter(adapter);
            } else
            {
                emptyView.setVisibility(View.GONE);
                rightButton.setVisibility(View.VISIBLE);
                adapter = new MyInfoCollectAdapter(beanList, getApplicationContext(), isShow);
                listView.setAdapter(adapter);
            }
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

    }

    @Override
    public void deleteCollectDataCallback(String response)
    {
        if (!TextUtils.isEmpty(response))
        {
            ToastUtils.showShort("删除成功");
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            boolean isHasSelect = false;
            for (CollectBean collectBean : beanList)
            {
                if (collectBean.isSelect())
                {
                    isHasSelect = true;
                    break;
                }
            }
            btnDelete.setBackgroundColor(ContextCompat.getColor(mAct, isHasSelect ? R.color.C0317 : R.color.C0331));
        }
    }


}
