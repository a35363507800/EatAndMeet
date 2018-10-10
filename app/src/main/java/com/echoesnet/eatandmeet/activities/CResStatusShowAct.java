package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.GlideApp;

import com.bumptech.glide.request.target.SimpleTarget;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.models.bean.DinersBean;
import com.echoesnet.eatandmeet.models.bean.ResLayoutBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;
import com.echoesnet.eatandmeet.models.datamodel.EamCode4Result;
import com.echoesnet.eatandmeet.presenters.ImpCResStatusShowView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ICResStatusShowView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.IDinerClickListener;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.SelectTableView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;



import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class CResStatusShowAct extends MVPBaseActivity<ICResStatusShowView, ImpCResStatusShowView> implements ICResStatusShowView
{
    private final static String TAG = CResStatusShowAct.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.st_st_layout)
    SelectTableView stvSelectTable;
    @BindView(R.id.lv_floors)
    ListView lvFloors;
    //选择楼层按钮
    @BindView(R.id.btn_select_floor)
    Button btnMoreFloor;
    @BindView(R.id.all_floor_container)
    AutoLinearLayout allFloorPanel;

    private Activity mContext;
    private String resId = "", floorNum = "";
    private Dialog pDialog;
    //endregion

    //所有楼层的数据
    private List<ResLayoutBean> resLayouts = new ArrayList<>();
    //每一层的tables
    private List<TableBean> tableEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cres_status_show);
        ButterKnife.bind(this);
        afterViews();
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
        //EamApplication.getRefWatcher(this).watch(this);
    }

    @Override
    protected ImpCResStatusShowView createPresenter()
    {
        return new ImpCResStatusShowView();
    }

    private void afterViews()
    {
        mContext = this;
        //获得餐厅id
        resId = getIntent().getStringExtra("resId");
        floorNum = getIntent().getStringExtra("floorNum");
        topBar.setTitle(getIntent().getStringExtra("resName"));
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
        //根据不同的dip来相应的放大
        stvSelectTable.setInitScale(getResources().getDisplayMetrics().density / 2 + 0.3f);
        stvSelectTable.setOnDinerClickListener(new IDinerClickListener()
        {
            @Override
            public void OnDinerClick(DinersBean dinersBean)
            {
                //隐身且不是自己的话不许查看详情
                if (dinersBean.getPrivateFlag().equals("1") &&
                        !dinersBean.getuId().equals(SharePreUtils.getUId(mContext)))
                {
                    ToastUtils.showShort("对方处于隐身状态，无法查看！");
                    return;
                }
                //如果是直播则进入直播间
                if (dinersBean.getStatus().equals("1"))
                {
                    if (TextUtils.isEmpty(dinersBean.getRoomId()))
                    {
                        ToastUtils.showShort("直播房间不存在。");
                        return;
                    }
                    //Logger.t(TAG).d("用餐人》"+dinersBean.toString());
                    CommonUtils.startLiveProxyAct(mContext, LiveRecord.ROOM_MODE_MEMBER,"","","",dinersBean.getRoomId(),null, EamCode4Result.reqNullCode);
                }
                else
                {
                    Intent intent = new Intent(mContext,CNewUserInfoAct.class);
                    intent.putExtra("checkWay","UId");
                    intent.putExtra("toUId", dinersBean.getuId());
                    mContext.startActivity(intent);
                }
            }
        });
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);

        getTableData(resId);
    }

    @OnClick({R.id.btn_select_floor})
    void clickView(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_select_floor:
                Logger.t(TAG).d("aaaaaaaaaaaaa");
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

    /**
     * 获得所有楼层数据源并初始化指定楼层
     *
     * @param resId 餐厅id
     */
    private void getTableData(final String resId)
    {
        pDialog.show();
        try
        {
/*            File file = new File(NetHelper.getRootDirPath(mContext) + resId + "_table.json");
            Logger.t(TAG).d("文件路径："+NetHelper.getRootDirPath(mContext)+resId + "_table.json");
            //如果需要更新则删除文件重新下载
            if (EamApplication.getInstance().resJsonDataMap!=null&&
                    CommonUtils.strWithSeparatorToList(EamApplication.getInstance().resJsonDataMap.get(resId),CommonUtils.SEPARATOR)
                            .contains(resId + "_table.json"))
            {
                if (file.exists())
                {
                    Logger.t(TAG).d("文件更新了");
                    file.delete();
                }
            }
            if (file.exists())
            {
                parseResInfoFromJson(file);
                setLayoutInfo(resId,floorNum);
                setFloorsData(resLayouts.size());
            }
            else*/
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE + CdnHelper.fileFolder + resId + "_table.json")
                        .build()
                        .execute(new FileCallBack(NetHelper.getRootDirPath(mContext), resId + "_table.json")
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                Logger.t(TAG).d(e.getMessage());
                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }

                            @Override
                            public void onResponse(File response)
                            {
                                parseResInfoFromJson(response);
                                setLayoutInfo(resId, floorNum);
                                //setFloorsData(resLayouts);

                                if (pDialog != null && pDialog.isShowing())
                                    pDialog.dismiss();
                            }

                            @Override
                            public void inProgress(float progress, long total)
                            {

                            }
                        });
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 从文件解析json数据
     *
     * @param jsonFile
     */
    private void parseResInfoFromJson(File jsonFile)
    {
        String result = CommonUtils.getJsonFromFile(jsonFile);
        Logger.t(TAG).d("桌子数据--> " + result);
        resLayouts = new Gson().fromJson(result, new TypeToken<ArrayList<ResLayoutBean>>()
        {
        }.getType());
        setFloorsData(resLayouts);
    }

    //设置楼层数
    private void setFloorsData(List<ResLayoutBean> resLays)
    {
        String firstLayoutId = resLays.get(0).getLayoutId();
        //初始化一下floorNum
        floorNum = firstLayoutId.substring(firstLayoutId.length() - 2);
        //如果只有一层，不显示楼层按钮
        if (resLays.size() == 1)
        {
            btnMoreFloor.setVisibility(View.GONE);
            //return;
        }
        else
        {
            btnMoreFloor.setVisibility(View.VISIBLE);
            btnMoreFloor.setText(floorNum);
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
                setLayoutInfo(resId, floorNum);
            }
        });
    }

    //根据楼层来改变餐厅显示信息2
    private void setLayoutInfo(String resId, String floorNum)
    {
/*
        final Dialog pDialog1=DialogUtil.getCommonDialog(mContext,"正在处理");
        pDialog1.show();
*/
        Logger.t(TAG).d("setLayoutInfo");
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
        //pDialog1.dismiss();

        setTablesStatus();
        //显示用餐人
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getDinersInfo(resId, floorNum, tableEntities);
        }
//        getDinersInfo(resId,floorNum,tableEntities);
        stvSelectTable.setFloorSize(Float.parseFloat(rlb.getFloor().getWidth()), Float.parseFloat(rlb.getFloor().getHeight()));
    }

    /**
     * 刷新餐厅桌子选择的状态，每次点击时间或者楼层时刷新
     */
    private void setTablesStatus()
    {
        Logger.t(TAG).d("setTablesStatus");
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
                status = "0";
            }
            //Logger.t(TAG).d(CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName()+status,mContext));
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName() + status, mContext))
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>((int) tableEntities.get(num).getWidth2(), (int) tableEntities.get(num).getHeight2())
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            tableEntities.get(num).setBitImg(ImageUtils.rotateImageView(tableEntities.get(num).getAngle2(), resource));
                            //tableEntities.get(num).setBitImg(resource);
                            //设置完桌子的图片后为选座控件设置数据源
                            if (num == (tableEntities.size() - 1))
                            {
                                stvSelectTable.setTableLst(tableEntities);
                            }
                        }
                    });
        }
    }

    /**
     * 为订餐人设置坐标和头像
     *
     * @param tableEntities
     * @param dinerLst
     */
    private void setDinersDataSource(List<TableBean> tableEntities, List<DinersBean> dinerLst)
    {
        //将餐厅位置加到里面,并设置图片
        for (int i = 0; i < dinerLst.size(); i++)
        {
            DinersBean db = dinerLst.get(i);
            if (db.getBitImg() == null)
                db.setBitImg(BitmapFactory.decodeResource(getResources(), R.drawable.userhead));
            //由于后台认为一个人既可以直播也可以聊天（理论上可能，其实实际没有人会这么干），所以我们需要自己根据直播状态设置一下聊天状态
            if ("1".equals(db.getStatus()))
            {
                db.setChatting("0");
            }
            //为了测试使用
            if (db.getImgUrls() == null || db.getImgUrls().size() < 3)
            {
                List<String> tLst = new ArrayList<>();
                tLst.add("http://h.hiphotos.baidu.com/image/h%3D360/sign=4882823172c6a7efa626ae20cdfbafe9/f9dcd100baa1cd11dd1855cebd12c8fcc2ce2db5.jpg");
                tLst.add("http://h.hiphotos.baidu.com/image/h%3D360/sign=4882823172c6a7efa626ae20cdfbafe9/f9dcd100baa1cd11dd1855cebd12c8fcc2ce2db5.jpg");
                tLst.add("http://h.hiphotos.baidu.com/image/h%3D360/sign=4882823172c6a7efa626ae20cdfbafe9/f9dcd100baa1cd11dd1855cebd12c8fcc2ce2db5.jpg");
                getDinerBitmap(db, db.getUphUrl(), tLst);
            }
            else
            {
                getDinerBitmap(db, db.getUphUrl(), db.getImgUrls());
            }
            Logger.t(TAG).d("就餐人信息" + db.toString());
            for (TableBean tb : tableEntities)
            {
                Logger.t(TAG).d("桌子信息" + tb.toString());
                if (db.getTableId().equals(tb.getTableId()))
                {
                    db.setW("103");
                    db.setH("110");
                    db.setX(String.valueOf(tb.getX2() + (tb.getWidth2() - db.getWidth2()) / 2));
                    //70为头像高度
                    db.setY(String.valueOf(tb.getY2() + (tb.getHeight2() - 70) / 2));
                    Logger.t(TAG).d("执行" + db.getTableId());
                    break;
                }
            }
        }
        //此处由于回调问题可能会出问题
        stvSelectTable.setDinerLst(dinerLst);
    }

    private void startDinersAnimate(int fre)
    {
        List<Bitmap> bits = new ArrayList<>();
        bits.add(BitmapFactory.decodeResource(getResources(), R.drawable.p0));
        bits.add(BitmapFactory.decodeResource(getResources(), R.drawable.p1));
        bits.add(BitmapFactory.decodeResource(getResources(), R.drawable.p3));
        bits.add(BitmapFactory.decodeResource(getResources(), R.drawable.p4));
        bits.add(BitmapFactory.decodeResource(getResources(), R.drawable.p5));
        stvSelectTable.setTalkAnimate(bits);
        stvSelectTable.startTalkAnimate(fre);
    }

    //获取就餐人信息
    private void getDinerBitmap(final DinersBean dinersBean, String toPeopleUrl, final List<String> imgUrls)
    {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.c_diner_info, null);
        //rvTo和rvFrom3是真实的头像
        final RoundedImageView rvTo = (RoundedImageView) view.findViewById(R.id.c_riv_to_head);
        final RoundedImageView rvFrom1 = (RoundedImageView) view.findViewById(R.id.c_riv_from_head1);
        final RoundedImageView rvFrom2 = (RoundedImageView) view.findViewById(R.id.c_riv_from_head2);
        final RoundedImageView rvFrom3 = (RoundedImageView) view.findViewById(R.id.c_riv_from_head3);
        final ImageView ivLive = (ImageView) view.findViewById(R.id.iv_c_live);
        final FrameLayout flFromUsers = (FrameLayout) view.findViewById(R.id.fl_from_take_users);
        if (dinersBean.getStatus().equals("1"))
            ivLive.setVisibility(View.VISIBLE);
        if (dinersBean.getPrivateFlag().equals("1"))
            flFromUsers.setVisibility(View.INVISIBLE);

        //所有图片全部下载完成时才转图
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(toPeopleUrl)
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.userhead)
                .into(new SimpleTarget<Bitmap>(64, 64)
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                    {
                        rvTo.setImageBitmap(resource);
                        GlideApp.with(EamApplication.getInstance())
                                .asBitmap()
                                .load(imgUrls.get(2))
                                .skipMemoryCache(true)
                                .centerCrop()
                                .error(R.drawable.userhead)
                                .into(new SimpleTarget<Bitmap>(30, 30)
                                {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                                    {
                                        rvFrom1.setImageBitmap(resource);
                                        GlideApp.with(EamApplication.getInstance())
                                                .asBitmap()
                                                .load(imgUrls.get(1))
                                                .skipMemoryCache(true)
                                                .centerCrop()
                                                .error(R.drawable.userhead)
                                                .into(new SimpleTarget<Bitmap>(30, 30)
                                                {
                                                    @Override
                                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                                                    {
                                                        rvFrom2.setImageBitmap(resource);
                                                        GlideApp.with(EamApplication.getInstance())
                                                                .asBitmap()
                                                                .load(imgUrls.get(0))
                                                                .skipMemoryCache(true)
                                                                .centerCrop()
                                                                .error(R.drawable.userhead)
                                                                .into(new SimpleTarget<Bitmap>(30, 30)
                                                                {
                                                                    @Override
                                                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                                                                    {
                                                                        rvFrom3.setImageBitmap(resource);
                                                                        dinersBean.setBitImg(CommonUtils.getViewBitmap(view));
                                                                        //刷新
                                                                        stvSelectTable.refreshSelectTableView(true);
                                                                    }
                                                                });
                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
    }


    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {

    }

    @Override
    public void requestNetErrorCallback(String interfaceName, Throwable e)
    {

    }

    @Override
    public void getDinersInfoCallback(List<DinersBean> response)
    {
        try
        {
            setDinersDataSource(tableEntities, response);
            startDinersAnimate(300);
        } catch (Exception e)
        {
            Logger.t(TAG).d(e.getMessage());
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

}
