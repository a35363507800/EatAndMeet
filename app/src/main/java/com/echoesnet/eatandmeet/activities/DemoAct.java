package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.eventmsgs.MessageEvent;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//import com.echoesnet.etmdbhelper.DaoSession;
/*****
 * 用于演示一些开发功能，不包含在项目中
 * ****/
public class DemoAct extends Activity
{
    final static String TAG=DemoAct.class.getSimpleName();
    @BindView(R.id.et_txt)
    TextView tvShow;
    @BindView(R.id.tv_text2)
    TextView tvDemoBus;
    @BindView(R.id.list)
    ListView lvLst;
    @BindView(R.id.ll_selecttable)
    LinearLayout llShow;
    @BindView(R.id.iv_img)
    ImageView imgTest;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_demo);
        ButterKnife.bind(this);
        onInjectDependencies();
        showTable();
    }


    private void onInjectDependencies()
    {
        //获得ActivityComponent对象
/*        ActivityComponent activityComponent= DaggerActivityComponent.builder()
                .appComponent(((EamApplication)getApplication()).getAppComponent())
                .build();
        //注入到HomeAct中
        activityComponent.inject(this);

        //通过ActivityComponent中的依赖对象获取接口获得OSHelper对象
        OSHelper osHelper=activityComponent.getOSHelper();
        Logger.t(TAG).d("onCreate: " + osHelper.getDeviceBrand());*/
    }


    private void showTable()
    {
        Log.d(TAG, "view 已经初始化完成: ");

        GlideApp.with(this)
                .load("http://inthecheesefactory.com/uploads/source/glidepicasso/cover.jpg")
                .into(imgTest);

/*        SelectTableView stv= (SelectTableView) findViewById(R.id.stv_show);
        //stv.setViewBg(getResources().getDrawable(R.mipmap.ic_launcher));
        List<TableBean> lstTable=new ArrayList<>();
        TableBean te1=new TableBean();
        te1.setTableId("1");
        te1.setX(10);
        te1.setY(10);
        te1.setW(150);
        te1.setH(150);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        te1.setBitImg(bitmap);
        lstTable.add(te1);

        TableBean te2=new TableBean();
        te2.setTableId("2");
        te2.setX(100);
        te2.setY(500);
        te2.setW(150);
        te2.setH(150);
        Bitmap bitmap2=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        te2.setBitImg(bitmap2);
        lstTable.add(te2);

        TableBean te3=new TableBean();
        te3.setTableId("3");
        te3.setX(400);
        te3.setY(200);
        te3.setW(150);
        te3.setH(150);
        te3.setAngle(25.0f);
        te3.setStat(2);
        Bitmap bitmap3=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
        te3.setBitImg(bitmap3);
        lstTable.add(te3);

        stv.setOnTableClickListener(new ITableClickListener()
        {
            @Override
            public void onTableClick(TableBean te)
            {
                switch (te.getStat())
                {
                    case 0:
                        //te.setBitImg(BitmapFactory.decodeResource(DemoAct.this.getResources(),R.drawable.desk1));
                        te.setStat(1);
                        break;
                    case 1:
                        //te.setBitImg(BitmapFactory.decodeResource(DemoAct.this.getResources(),R.drawable.desk2));
                        te.setStat(0);
                        break;
                    case 2:
                        break;
                }
                Toast.makeText(DemoAct.this,"桌子："+ te.getTableId()+"被点击了"+te.getStat(),Toast.LENGTH_SHORT).show();
            }
        });
        stv.setTableLst(lstTable);
        llShow.addView(stv);*/

    }

    @OnClick({R.id.btnOk,R.id.et_txt})
    void buttonClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnOk:
                tvShow.setText("the button is clicked");
                EventBus.getDefault().post(new MessageEvent("this is eventbus demo"));
                break;
            case R.id.et_txt:
                tvShow.setText("the textview is clicked");
                break;
            default:
                break;

        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /*有四种执行方式
    * 1.POSTING :与发布线程为同一线程
    * 2.MAIN:    在UI线程执行
    * 3.BACKGROUND:如何发布线程是UI线程，则新建一个线程执行，如果发布线程是后台线程，则在此线程中执行
    * 4.ASYNC :在独立线程执行
    * */

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEvent(MessageEvent event){
        Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
    }

    /*greedDao
    *
    * */
/*    private DaoSession setupDatabase()
    {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "lease-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }*/

/*    //private LeaseDao getLeaseDao(DaoSession daoSession)
    {
        return daoSession.getLeaseDao();
    }*/
}
