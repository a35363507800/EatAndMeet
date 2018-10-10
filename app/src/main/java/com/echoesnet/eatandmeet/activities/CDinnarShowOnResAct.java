package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.DinersBean;
import com.echoesnet.eatandmeet.models.bean.ResLayoutBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.IDinerClickListener;
import com.echoesnet.eatandmeet.views.widgets.selectTableView.SelectTableView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class CDinnarShowOnResAct extends BaseActivity
{
    private final static String TAG=CDinnarShowOnResAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.st_st_layout)
    SelectTableView stvSelectTable;

    private Activity mContext;
    private Dialog pDialog;

    //所有楼层的数据
    private List<ResLayoutBean> resLayouts=new ArrayList<>();
    //每一层的tables
    private List<TableBean> tableEntities;
    private List<DinersBean>dinerEntities=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_cdinnar_show_on_res);
        ButterKnife.bind(this);
        initAfterView();
    }

    private void initAfterView()
    {
        mContext=this;
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
        stvSelectTable.setInitScale(getResources().getDisplayMetrics().density/2+0.3f);
        stvSelectTable.setOnDinerClickListener(new IDinerClickListener()
        {
            @Override
            public void OnDinerClick(DinersBean dinersBean)
            {
                return;
            }
        });
        pDialog= DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);

        getDinersInfo(getIntent().getStringExtra("tableId"),getIntent().getStringExtra("headImg"));
        getTableData(getIntent().getStringExtra("resId"),getIntent().getStringExtra("floorNum"),getIntent().getStringExtra("orderTime"));
    }

    /**
     * 获得所有楼层数据源并初始化指定楼层
     * @param resId 餐厅id
     * @param floorNum 餐厅楼层
     */
    private void getTableData(final String resId, final String floorNum, final String orderTime)
    {
        if (!pDialog.isShowing())
            pDialog.show();
        try
        {
/*
 缓存暂时去掉
 File file = new File(NetHelper.getRootDirPath(mContext) + resId + "_table.json");
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
                setLayoutInfo(resId,floorNum,orderTime);
            }
            else*/
            {
                OkHttpUtils.get()
                        .url(CdnHelper.CDN_ORIGINAL_SITE +CdnHelper.fileFolder+resId+"_table.json")
                        .build()
                        .execute(new StringCallback()
                        {
                            @Override
                            public void onError(Call call, Exception e)
                            {
                                Logger.t(TAG).d(e.getMessage());
                                if(pDialog!=null&&pDialog.isShowing())
                                    pDialog.dismiss();
                            }

                            @Override
                            public void onResponse(String response)
                            {
                                resLayouts=new Gson().fromJson(response,new TypeToken<ArrayList<ResLayoutBean>>(){}.getType());
                                if(pDialog!=null&&pDialog.isShowing())
                                    pDialog.dismiss();
                                setLayoutInfo(resId,floorNum,orderTime);
                            }
                        });
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
            if(pDialog!=null&&pDialog.isShowing())
                pDialog.dismiss();
        }
    }
    /**
     * 从文件解析json数据
     * @param jsonFile
     */
    private void parseResInfoFromJson(File jsonFile)
    {
        String result= CommonUtils.getJsonFromFile(jsonFile);
        resLayouts=new Gson().fromJson(result,new TypeToken<ArrayList<ResLayoutBean>>(){}.getType());
        if(pDialog!=null&&pDialog.isShowing())
            pDialog.dismiss();
    }
    //根据楼层来改变餐厅显示信息2
    private void setLayoutInfo(String resId,String floorNum,String orderTime)
    {
        ResLayoutBean rlb=null;
        for (int i=0;i<resLayouts.size();i++)
        {
            if (resLayouts.get(i).getLayoutId().equals(resId+floorNum))
            {
                rlb=resLayouts.get(i);
                break;
            }
        }
        if(rlb==null)
            return;
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(rlb.getFloor().getImgUrl())
                .skipMemoryCache(true)
                .into(new SimpleTarget<Bitmap>(Integer.parseInt(rlb.getFloor().getWidth()),Integer.parseInt(rlb.getFloor().getHeight()))
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                    {
                        stvSelectTable.setFloorImg(resource);
                    }
                });
        tableEntities=rlb.getTables();

        stvSelectTable.setFloorSize(Float.parseFloat(rlb.getFloor().getWidth()),Float.parseFloat(rlb.getFloor().getHeight()));
        setTablesStatus();
        //显示用餐人
        setDinersDataSource(tableEntities,dinerEntities,orderTime);
    }
    private void getDinersInfo(String tableId,String headImgUrl)
    {
        DinersBean db=new DinersBean();
        db.setTableId(tableId);
        db.setUphUrl(headImgUrl);
        dinerEntities.clear();
        dinerEntities.add(db);
    }
    /**
     * 刷新餐厅桌子选择的状态，每次点击时间或者楼层时刷新
     */
    private void setTablesStatus()
    {
        for (int i=0;i<tableEntities.size();i++)
        {
            final int num=i;
            String status="0";
            String tableId=tableEntities.get(i).getTableId();
            //Logger.t(TAG).d("桌子id："+tableId);
            //如果不是桌子则不处理 type 01 为桌子
            if (tableEntities.get(i).getType().equals("00"))
            {
                status="";
            }
            else
            {
                status="0";
            }
            Logger.t(TAG).d(CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName()+status,mContext));
            GlideApp.with(EamApplication.getInstance())
                    .asBitmap()
                    .load(CdnHelper.getInstance().getMaterialUrl(tableEntities.get(i).getPicName()+status,mContext))
                    .skipMemoryCache(true)
                    .into(new SimpleTarget<Bitmap>((int) tableEntities.get(num).getWidth2(),(int) tableEntities.get(num).getHeight2())
                    {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                        {
                            tableEntities.get(num).setBitImg(ImageUtils.rotateImageView(tableEntities.get(num).getAngle2(),resource));
                            //tableEntities.get(num).setBitImg(resource);
                            //设置完桌子的图片后为选座控件设置数据源
                            if (num==(tableEntities.size()-1))
                            {
                                stvSelectTable.setTableLst(tableEntities);
                            }
                        }
                    });
        }
    }
    /**
     * 为订餐人设置坐标和头像
     * @param tableEntities
     * @param dinerLst
     */
    private void setDinersDataSource(List<TableBean> tableEntities, List<DinersBean> dinerLst,String orderTime)
    {
        //将餐厅位置加到里面,并设置图片
        for (int i=0;i<dinerLst.size();i++)
        {
            DinersBean db=dinerLst.get(i);
            Logger.t(TAG).d("就餐人信息"+ db.toString());
            if (db.getBitImg()==null)
                db.setBitImg(BitmapFactory.decodeResource(getResources(),R.drawable.userhead));
            for (TableBean tb:tableEntities)
            {
                Logger.t(TAG).d("桌子信息"+ tb.toString());
                if (db.getTableId().equals(tb.getTableId()))
                {
                    //db.setW("160");
                    db.setW("175");
                    db.setH("175");
                    db.setX(String.valueOf(tb.getX2()+(tb.getWidth2()-db.getWidth2())/2));
                    //70为头像高度
                    db.setY(String.valueOf(tb.getY2()+(tb.getHeight2()-70)/2));
                    db.setTableName(tb.getTableName());
                    Logger.t(TAG).d("执行"+db.getTableId());
                    break;
                }
            }
            getDinerBitmap(db,db.getTableName(), orderTime);
            Logger.t(TAG).d("就餐人信息"+ db.toString());
        }
        stvSelectTable.setDinerLst(dinerLst);
    }

    //获取就餐人信息
    private void getDinerBitmap(final DinersBean dinersBean,String tableNum,String orderTime)
    {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.c_diner_single_info,null);
        final RoundedImageView rvTo= (RoundedImageView) view.findViewById(R.id.c_riv_to_head);
        final TextView tvOrderInfo= (TextView) view.findViewById(R.id.tv_order_info);
        //这边显示桌子的名称，由后台保证
        tvOrderInfo.setText(String.format("已预定：%s%n (%s%n%s)",tableNum,orderTime.split(" ")[0],orderTime.split(" ")[1]));

        //所有图片全部下载完成时才转图
        GlideApp.with(EamApplication.getInstance()).asBitmap()
                .load(dinersBean.getUphUrl())
                .skipMemoryCache(true)
                .centerCrop()
                .error(R.drawable.userhead)
                .into(new SimpleTarget<Bitmap>(70,70)
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> glideAnimation)
                    {
                        rvTo.setImageBitmap(resource);

                        Bitmap imageBitmap=CommonUtils.getViewBitmap(rvTo);
                        Bitmap textBitmap=CommonUtils.getViewBitmap(tvOrderInfo);
                        Bitmap bitmap=Bitmap.createBitmap(175,175, Bitmap.Config.ARGB_8888);
                        Canvas canvas=new Canvas(bitmap);

                        canvas.drawBitmap(imageBitmap,175/2-imageBitmap.getWidth()/2,0,null);
                        canvas.drawBitmap(textBitmap,175/2-textBitmap.getWidth()/2,imageBitmap.getHeight()+20,null);
                        canvas=null;

                        dinersBean.setBitImg(bitmap);
                        //dinersBean.setW(String.valueOf(bitmap.getWidth()));
                        //dinersBean.setH(String.valueOf(bitmap.getHeight()));
                        //刷新
                        stvSelectTable.refreshSelectTableView(true);
                    }
                });
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(pDialog!=null&&pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog=null;
        }
    }
}
