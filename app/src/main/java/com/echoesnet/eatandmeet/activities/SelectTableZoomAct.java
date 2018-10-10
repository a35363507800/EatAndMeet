package com.echoesnet.eatandmeet.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;

import com.bumptech.glide.request.target.SimpleTarget;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.ResLayoutBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;
import com.echoesnet.eatandmeet.models.eventmsgs.OrderTableMsg;
import com.echoesnet.eatandmeet.presenters.ImpISelectTableZoomView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ISelectTableZoomView;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.ITableClickListener;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.SelectTableView;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @modifier
 * @createDate
 * @description
 */
public class SelectTableZoomAct extends BaseActivity implements ISelectTableZoomView
{
    public final static String TAG = SelectTableZoomAct.class.getSimpleName();

    @BindView(R.id.st_st_zoom_layout)
    SelectTableView stvSelectTable;
    @BindView(R.id.lv_floors)
    ListView lvFloors;
    @BindView(R.id.btn_select_floor)
    Button btnMoreFloor;//选择楼层按钮
    @BindView(R.id.all_floor_container)
    LinearLayout allFloorPanel;
    @BindView(R.id.tv_select_detail)
    TextView tvSelectTableInfo;

    private String floorNum;
    private String resId = "";
    private String resName = "";
    private String orderTime;
    //所有楼层的数据
    private List<ResLayoutBean> resLayouts = new ArrayList<>();
    //每一层的tables
    private List<TableBean> tableEntities;
    private List<TableBean> selectedTables;
    private String selectTableInstruction = "";

    private ImpISelectTableZoomView selectTableZoomView;

    private Context mContext;
    private Dialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_select_table_zoom);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(SelectTableZoomAct.this);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(SelectTableZoomAct.this);

        //停止时发送一个消息
        EventBus.getDefault().post(new OrderTableMsg(floorNum, resLayouts,
                orderTime, resId, selectedTables));
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //resLayouts=null;
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void initAfterView()
    {
        mContext = this;
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(true);
        selectTableZoomView = new ImpISelectTableZoomView(mContext, this);
        stvSelectTable.setOnTableClickListener(new ITableClickListener()
        {
            @Override
            public void onTableClick(final TableBean te)
            {
                //如果是装饰物不处理
                if (te.getType().equals("00"))
                    return;
                switch (te.getStatus())
                {
                    //未选中
                    case "0":
                        if (selectedTables.size() > 1)
                        {
                            ToastUtils.showShort( "一次最多可以预定两张餐桌");
                            return;
                        }
                        te.setStatus("2");
                        //te.setFloorNumber(floorNum);
                        selectedTables.add(te);
                        break;
                    case "1":
                        return;
                    case "2":
                        te.setStatus("0");
                        //te.setFloorNumber("");
                        selectedTables.remove(te);
                        break;
                }

                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(CdnHelper.getInstance().getMaterialUrl(te.getPicName() + te.getStatus(), mContext))
                        .skipMemoryCache(true)
                        .into(new SimpleTarget<Bitmap>((int) te.getWidth2(), (int) te.getHeight2())
                        {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                            {
                                Bitmap bitmap = ImageUtils.rotateImageView(te.getAngle2(), resource);
                                te.setBitImg(bitmap);
                                stvSelectTable.dataSourceChanged(tableEntities);
                                //ivTestView.setImageBitmap(te.getBitImg());

                                selectTableInstruction = "";
                                if (selectedTables.size() == 1)
                                {
                                    TableBean te1 = selectedTables.get(0);
                                    selectTableInstruction = String.format("%s层%s(%s人)", te1.getFloorNumber(), te1.getTableName(), te1.getTableType());
                                }
                                else if (selectedTables.size() == 2)
                                {
                                    TableBean te1 = selectedTables.get(0);
                                    selectTableInstruction = String.format("%s层%s(%s人)", te1.getFloorNumber(), te1.getTableName(), te1.getTableType());
                                    TableBean te2 = selectedTables.get(1);
                                    selectTableInstruction += String.format(" %s层%s(%s人)", te2.getFloorNumber(), te2.getTableName(), te2.getTableType());
                                }else if (selectedTables.size() == 0)
                                {
                                    selectTableInstruction = "您未选择任何餐桌，请点击图中按钮选择";
                                }
                                tvSelectTableInfo.setText(selectTableInstruction);
                            }
                        });

            }
        });
        float density = getResources().getDisplayMetrics().density;
        Logger.t(TAG).d("密度》" + density);
        stvSelectTable.setInitScale(getResources().getDisplayMetrics().density / 2);
    }

    //根据楼层来改变餐厅显示信息
    private void setLayoutInfo(String resId, String floorNum, String orderTime)
    {
        ResLayoutBean rlb = null;
        for (int i = 0; i < resLayouts.size(); i++)
        {
            if (resLayouts.get(i).getLayoutId().equals(resId + floorNum))
            {
                rlb = resLayouts.get(i);
                break;
            }
        }
        if (rlb == null)
            return;
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(rlb.getFloor().getImgUrl())
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(Integer.parseInt(rlb.getFloor().getWidth()), Integer.parseInt(rlb.getFloor().getHeight()))
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                    {
                        stvSelectTable.setFloorImg(resource);
                    }
                });
        tableEntities = rlb.getTables();
        List<HashMap<String, String>> sMapLst = new ArrayList<>();
        for (int i = 0; i < tableEntities.size(); i++)
        {
            if (tableEntities.get(i).getStatus().equals("2"))
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("tNum", tableEntities.get(i).getTableId());
                map.put("tStatus", tableEntities.get(i).getStatus());
                sMapLst.add(map);
            }
        }
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (selectTableZoomView != null)
            selectTableZoomView.getTableStatusFromSever(resId, floorNum, orderTime, sMapLst);

        stvSelectTable.setTableLst(tableEntities);
        stvSelectTable.setFloorSize(Float.parseFloat(rlb.getFloor().getWidth()), Float.parseFloat(rlb.getFloor().getHeight()));
    }

    //刷新餐厅桌子选择的状态，每次点击时间或者楼层时刷新
    private void setTablesStatus(List<HashMap<String, String>> statueLst)
    {
        for (int i = 0; i < tableEntities.size(); i++)
        {
            final int num = i;
            String status = "0";
            String tableId = tableEntities.get(i).getTableId();
            //Logger.t(TAG).d("桌子id："+tableId);
            //如果不是桌子则不处理 type 01 为桌子
            if (tableEntities.get(i).getType().equals("00"))
            {
                status = "";
            }
            else
            {
                for (HashMap<String, String> map : statueLst)
                {
                    if (map.get("tNum").equals(tableId))
                    {
                        status = map.get("tStatus");
                        tableEntities.get(i).setStatus(status);
                        //Logger.t(TAG).d("状态："+status);
                        break;
                    }
                }
            }
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName() + status, mContext))
                    .into(new SimpleTarget<Bitmap>((int) tableEntities.get(num).getWidth2(), (int) tableEntities.get(num).getHeight2())
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            tableEntities.get(num).setBitImg(ImageUtils.rotateImageView(tableEntities.get(num).getAngle2(), resource));
                            //tableEntities.get(num).setBitImg(resource);
                            if (num == (tableEntities.size() - 1))
                            {
//                                Logger.t(TAG).d("5***********==="+num);
                                stvSelectTable.setTableLst(tableEntities);
                            }
                        }
                    });
        }
    }

    //设置楼层数
    private void setFloorsData(List<ResLayoutBean> resLays, final String orderTime)
    {
        //String firstLayoutId=resLays.get(0).getLayoutId();
        //初始化一下floorNum
        //floorNum=firstLayoutId.substring(firstLayoutId.length()-2);
        //如果只有一层，不显示楼层按钮
        if (resLays.size() == 1)
        {
            btnMoreFloor.setVisibility(View.GONE);
            //return;
        }
        final List<Map<String, Object>> mapLst = new ArrayList<>();
        for (ResLayoutBean rlb : resLays)
        {
            Map<String, Object> map = new HashMap<>();
            map.put("floorNum", rlb.getLayoutId().substring(rlb.getLayoutId().length() - 2));
            map.put("img", R.drawable.white);
            mapLst.add(map);
        }
        SimpleAdapter floorCountAdapter = new SimpleAdapter(mContext, mapLst, R.layout.litem_small_floors,
                new String[]{"floorNum", "img"}, new int[]{R.id.tv_small_floor, R.id.iv_small_floor_split});
        lvFloors.setAdapter(floorCountAdapter);
        lvFloors.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                btnMoreFloor.setText(String.valueOf(mapLst.get(position).get("floorNum")));
                allFloorPanel.setVisibility(View.GONE);
                floorNum = String.valueOf(String.valueOf(mapLst.get(position).get("floorNum")));
                setLayoutInfo(resId, floorNum, orderTime);
            }
        });
    }

    //初始化前一页面传入信息
    private void initData(String resId, String floorNum, String orderTime, int floorCount)
    {
        btnMoreFloor.setText(floorNum);
        setLayoutInfo(resId, floorNum, orderTime);
        setFloorsData(resLayouts, orderTime);
        refreshSelectStatus();
    }

    private void refreshSelectStatus()
    {
        for (ResLayoutBean rlb : resLayouts)
        {
            String layoutId = rlb.getLayoutId();
            for (TableBean te : rlb.getTables())
            {
                //如果是装饰物不处理
                if (te.getType().equals("00"))
                    continue;
                if (te.getStatus().equals("2"))
                {
                    if (selectedTables.size() == 1)
                    {
                        selectTableInstruction = String.format("%s层%s(%s人桌)", layoutId.substring(layoutId.length() - 2), te.getTableName(), te.getTableType());
                    }
                    else if (selectedTables.size() == 2)
                    {
                        selectTableInstruction += String.format("%s层%s(%s人桌)", layoutId.substring(layoutId.length() - 2), te.getTableName(), te.getTableType());
                    }
                }
            }
        }
        if (selectedTables.size() == 0)
        {
            selectTableInstruction = "您未选择任何餐桌，请点击图中按钮选择";
        }
        tvSelectTableInfo.setText(selectTableInstruction);
    }

    @OnClick({R.id.btn_st_zoom_close, R.id.btn_select_floor})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_st_zoom_close:
                SelectTableZoomAct.this.finish();
                break;
            case R.id.btn_select_floor:
                if (lvFloors.getCount() > 1)
                {
                    if (allFloorPanel.getVisibility() == View.VISIBLE)
                        allFloorPanel.setVisibility(View.GONE);
                    else
                        allFloorPanel.setVisibility(View.VISIBLE);
                }

                break;
            default:
                break;
        }
    }

    @Subscribe(sticky = false, threadMode = ThreadMode.MAIN)
    public void onOrderTableMsg(OrderTableMsg event)
    {
        Logger.t(TAG).d("从前面获得数据》" + event.toString());
        resLayouts = event.getLayoutEntities();
        selectedTables = event.getSelectedTables();
        resId = event.getRestId();
        orderTime = event.getOrderTime();
        floorNum = event.getFloorNum();
        initData(event.getRestId(), event.getFloorNum(), event.getOrderTime(), resLayouts.size());
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {

    }

    @Override
    public void getTableStatusFromSeverCallback(ArrayMap<String, Object> maps)
    {
        String response = (String) maps.get("response");
        List<HashMap<String, String>> selectedTables = (List<HashMap<String, String>>) maps.get("selectedTables");
        try
        {
                JSONObject body = new JSONObject(response);
                JSONArray resLayouts = body.getJSONArray("restrLayouts");
                JSONObject resLayout = resLayouts.getJSONObject(0);
                JSONArray tableStatus = resLayout.getJSONArray("tableStatusBeen");
                List<HashMap<String, String>> lst = new ArrayList<>();
                for (int i = 0; i < tableStatus.length(); i++)
                {
                    JSONObject tableStatusBean = tableStatus.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("tNum", tableStatusBean.getString("tNum"));
                    map.put("tStatus", tableStatusBean.getString("tStatus"));
                    for (HashMap<String, String> map1 : selectedTables)
                    {
                        //如果与传入的值一致则修改
                        if (map1.get("tNum").equals(tableStatusBean.getString("tNum")))
                        {
                            map.put("tNum", map1.get("tNum"));
                            map.put("tStatus", map1.get("tStatus"));
                        }
                    }
                    lst.add(map);
                }
                setTablesStatus(lst);

        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).e(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }
}
