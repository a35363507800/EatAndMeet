package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.listeners.ContactPopupDismissListener;
import com.echoesnet.eatandmeet.presenters.ImpIMySettingPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMySettingView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.DataCleanManager;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.HuanXinIMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.PreferenceManager;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.CustomAlterDialogs.DialogWith2BtnAtBottom;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.DownloadAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.MySetContactUsPopup;
import com.echoesnet.eatandmeet.views.widgets.SwitchView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;

/**
 * 系统的设置页面
 */

public class MySettingAct extends MVPBaseActivity<MySettingAct, ImpIMySettingPre> implements IMySettingView
{
    //region 变量
    private static final String TAG = MySettingAct.class.getSimpleName();
    private static final String STAT_ON = "1";
    private static final String STAT_OFF = "0";

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.setting_hongdian)
    View vRed;
    @BindView(R.id.btn_quit_system)
    Button btnQuitSystem;
    @BindView(R.id.all_my_user_feedback)
    RelativeLayout allMyUserFeedback;
    @BindView(R.id.all_my_user_help)
    LinearLayout allMyUserHelp;
    @BindView(R.id.all_my_user_about)
    RelativeLayout allMyUserAbout;
    @BindView(R.id.all_my_user_contact_us)
    RelativeLayout allMyUserContactUs;
    @BindView(R.id.all_my_user_check_update)
    RelativeLayout allMyUserCheckUpdate;
    @BindView(R.id.all_my_user_clear_catch)
    RelativeLayout allMyUserClearCatch;
    @BindView(R.id.all_my_user_account_security)
    RelativeLayout allMyUserAccountSecurity;
    /*    @BindView(R.id.my_set_person_show)
        SwitchView mySetPersonShow;*/
    @BindView(R.id.my_set_msg_show)
    SwitchView mySetMsgShow;
    @BindView(R.id.my_set_order_show)
    SwitchView mySetOrderShow;
    @BindView(R.id.clear_sise)
    IconTextView clearSize;

    private Dialog pDialog;
    private Activity mContext;
    private MySetContactUsPopup contactUsPopup;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_setting);
        ButterKnife.bind(this);
        afterViews();
    }

    /**
     * popupwindow在显示中点击返回杀死当前页会造成窗口泄露
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (contactUsPopup != null && contactUsPopup.isShowing())
        {
            contactUsPopup.dismiss();
            contactUsPopup = null;
        }
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void afterViews()
    {
        mContext = this;
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

            }
        }).setText(getResources().getString(R.string.setting));

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理");
        pDialog.setCancelable(false);

        getStatOnBack();
        initListener();

        //检测缓存文件大小
        mPresenter.getFileLength();

    }

    /**
     * 获取后台的设置信息
     */
    private void getStatOnBack()
    {
//        if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
//            pDialog.show();
        if (mPresenter != null)
            mPresenter.getStatOnBack();
    }

    /**
     * 为开关设置监听
     */
    private void initListener()
    {
/*        mySetPersonShow.setOnStateChangedListener(new SwitchView.OnStateChangedListener()
        {
            @Override
            public void toggleToOn()
            {
                // mySetPersonShow.toggleSwitch(true);
                if (mPresenter != null)
                    mPresenter.changePrivateStat(STAT_ON);
            }

            @Override
            public void toggleToOff()
            {
                // mySetPersonShow.toggleSwitch(false);
                if (mPresenter != null)
                    mPresenter.changePrivateStat(STAT_OFF);
            }
        });*/
        mySetMsgShow.setOnStateChangedListener(new SwitchView.OnStateChangedListener()
        {
            @Override
            public void toggleToOn()
            {
                SharePreUtils.setIsShowNotify(mContext, "1");
                PreferenceManager.getInstance().setSettingMsgNotification(true);
                //SharePreUtils.setMsgPushState(mContext,true);
                if (mPresenter != null)
                    mPresenter.changePushStat(STAT_ON);
            }

            @Override
            public void toggleToOff()
            {
                SharePreUtils.setIsShowNotify(mContext, "0");
                PreferenceManager.getInstance().setSettingMsgNotification(false);
                //SharePreUtils.setMsgPushState(mContext,false);
                if (mPresenter != null)
                    mPresenter.changePushStat(STAT_OFF);
            }
        });
        mySetOrderShow.setOnStateChangedListener(new SwitchView.OnStateChangedListener()
        {
            @Override
            public void toggleToOn()
            {
                if (mPresenter != null)
                    mPresenter.changeOrderStat(STAT_ON);
            }

            @Override
            public void toggleToOff()
            {
                if (mPresenter != null)
                    mPresenter.changeOrderStat(STAT_OFF);
            }
        });
    }

    @OnClick({R.id.btn_quit_system, R.id.all_my_user_feedback, R.id.all_my_user_help, R.id.all_my_user_about, R.id.all_my_user_contact_us
            , R.id.all_my_user_check_update, R.id.all_my_user_clear_catch, R.id.all_my_user_account_security})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            //退出程序
            case R.id.btn_quit_system:
                new CustomAlertDialog(mContext)
                        .builder()
                        .setTitle("提示")
                        .setMsg("确认要退出程序吗？")
                        .setPositiveTextColor(Color.parseColor("#666666"))
                        .setPositiveButton("确认", new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                HuanXinIMHelper.getInstance().quitNormal(mContext, null);
                            }
                        }).setNegativeButton("取消", new View.OnClickListener()
                {

                    @Override
                    public void onClick(View v)
                    {

                    }
                }).show();
                break;
            case R.id.all_my_user_feedback:
                startActivity(new Intent(mContext, MySetUserFeedbackAct.class));
                break;
            case R.id.all_my_user_help:
                startActivity(new Intent(mContext, MyUserHelperAct.class));
                overridePendingTransition(R.anim.fade_in_short, R.anim.fade_out_short);
                break;
            case R.id.all_my_user_about:
                startActivity(new Intent(mContext, MyAboutAppAct.class));
                break;
            case R.id.all_my_user_contact_us://联系我们
//                if (!mContext.isFinishing() && pDialog != null && !pDialog.isShowing())
//                    pDialog.show();
                if (mPresenter != null)
                    mPresenter.getCompContact();
                break;
            case R.id.all_my_user_check_update:
                if (mPresenter != null)
                    mPresenter.getVersionCode();
                break;
            case R.id.all_my_user_clear_catch:
                showClearCatchDialog();//清除缓存
                break;
            case R.id.all_my_user_account_security:
                startActivity(new Intent(mContext, MySetAccountSecurityAct.class));
                break;
            default:
                break;
        }
    }

    private void showClearCatchDialog()
    {
        if (!"0MB".equals(clearSize.getText().toString().trim()))
        {
            new CustomAlertDialog(mContext)
                    .builder()
                    .setTitle("提示")
                    .setMsg("确认要清除本地缓存数据吗？")
                    .setPositiveTextColor(Color.parseColor("#666666"))
                    .setPositiveButton("确认", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
//                            new Thread()
//                            {
//                                @Override
//                                public void run()
//                                {
//                                    GlideApp.get(mContext).clearDiskCache();
//                                }
//                            };
                            GlideApp.get(mContext).clearMemory();
                            clearCatch();
                        }
                    }).setNegativeButton("取消", new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {

                }
            }).show();
        }
        else
        {
            ToastUtils.showShort("暂无缓存");
        }

    }


    private void clearCatch()
    {
        final String path = NetHelper.getRootDirPath(mContext);
//        String path = "/sdcard/EatAndMeet/";
        Logger.t(TAG).d("路径为：" + path);
        /*File file = new File(path);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++)
        {
            if (!files[i].getColumnName().contains(".json"))
            {
                files[i].delete();
            }
        }*/
        if (pDialog != null && !pDialog.isShowing())
        {
            pDialog.show();
        }

        //遍历所有file下的文件
        Observable.create(new ObservableOnSubscribe<String>()
        {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception
            {
                Thread.sleep(2000);
                mPresenter.clearFile();
                // DataCleanManager.cleanCustomCache(path, ".json");
                DataCleanManager.cleanInternalCache(mContext);
                DataCleanManager.cleanExternalCache(mContext);
                DataCleanManager.cleanFiles(mContext);
                e.onNext("");
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(MySettingAct.this.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Consumer<String>()
                {
                    @Override
                    public void accept(String s) throws Exception
                    {
                        pDialog.dismiss();
                        ToastUtils.showShort("清理成功");
                        clearSize.setText("0MB");
                    }
                });

    }


    private void showDownloadDialog(String url)
    {
        // 弹出下载Dialog
        new DownloadAlertDialog(mContext)
                .build()
                .setTitle("看脸吃饭.apk")
                .setImage(R.mipmap.ic_launcher)
                .setDownLoadFileUrl(url)
                .show();
    }

    @Override
    protected ImpIMySettingPre createPresenter()
    {
        return new ImpIMySettingPre();
    }

    @Override
    public void onBackPressed()
    {
        if (contactUsPopup != null && contactUsPopup.isShowing())
        {
            contactUsPopup.dismiss();
        }
        else
        {
            finish();
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, getString(R.string.pay_fault_due_to_net), exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getVersionCodeCallback(String str)
    {
        try
        {
            JSONObject obj = new JSONObject(str);
            String ServerVersion = obj.getString("version");
            int SerVersion = Integer.parseInt(ServerVersion);
            final String url = obj.getString("url");
            final String updateContent = obj.getString("msg");
            Logger.t(TAG).d("SerVersion:" + SerVersion + ",versionCode" + CommonUtils.getVerCode(mContext));
            if (CommonUtils.getVerCode(mContext) >= SerVersion)
            {
                ToastUtils.showShort("已是最新版本");
            }
            else if (CommonUtils.getVerCode(mContext) < SerVersion)
            {
                View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_download_apk, null);
                TextView tvContent = (TextView) view.findViewById(R.id.update_content);
                tvContent.setText(updateContent);
                new DialogWith2BtnAtBottom(mContext)
                        .buildDialog(mContext)
                        .setDialogTitle("发现新版本，现在就去下载！", false)
                        .setContent(view)
                        .setCommitBtnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                String NetStat = "";
                                //返回值 -1：没有网络  1：WIFI网络2：wap网络3：net网络
                                int i = NetHelper.getNetworkStatus(mContext);
                                switch (i)
                                {
                                    case -1:
                                        NetStat = "没有网络";
                                        ToastUtils.showShort( "当前无网络连接");
                                        break;
                                    case 1:
                                        NetStat = "WiFi网络";
                                        //WiFi网络
                                        showDownloadDialog(url);
                                        break;
                                    case 2:
                                    case 3:
                                        NetStat = "移动网络";
                                        new CustomAlertDialog(mContext)
                                                .builder()
                                                .setMsg(getString(R.string.mobie_net_tip))
                                                .setTitle("提示")
                                                .setPositiveButton("土豪请继续", new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        showDownloadDialog(url);
                                                    }
                                                }).setNegativeButton("取消", new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {

                                            }
                                        }).show();
                                        break;
                                }
                            }
                        })
                        .setCancelBtnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                            }
                        })
                        .show();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void getContactCallback(String str)
    {
        try
        {
            JSONObject body = new JSONObject(str);
            Logger.t(TAG).d("body>>" + body.toString());
            List<HashMap<String, String>> source = new ArrayList<>();
            JSONArray hotlines = body.getJSONArray("hotline");
            JSONArray contacts = body.getJSONArray("contact");
            for (int i = 0; i < contacts.length(); i++)
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("type", "contact");
                map.put("content", contacts.getString(i));
                source.add(map);
            }
            for (int i = 0; i < hotlines.length(); i++)
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("type", "hotline");
                map.put("content", hotlines.getString(i));
                source.add(map);
            }
            contactUsPopup = new MySetContactUsPopup(mContext, source);
            contactUsPopup.setOnDismissListener(new ContactPopupDismissListener(contactUsPopup));
            contactUsPopup.showAtLocation(mContext.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

        } catch (JSONException e)
        {
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void getStatCallBack(String str)
    {
        try
        {
            JSONObject obj = new JSONObject(str);
            String pushFlag = obj.getString("pushFlag");
            String orderFlag = obj.getString("orderFlag");
            String privateFlag = obj.getString("privateFlag");
            SharePreUtils.setIsShowNotify(mContext, pushFlag);
            Logger.t(TAG).d(orderFlag + "," + pushFlag + "," + privateFlag);
            if (orderFlag.equals(STAT_ON))
            {
                mySetOrderShow.toggleSwitch(true);
            }
            else
            {
                mySetOrderShow.toggleSwitch(false);
            }
            if (pushFlag.equals(STAT_ON))
            {
                mySetMsgShow.toggleSwitch(true);
            }
            else
            {
                mySetMsgShow.toggleSwitch(false);
            }
/*                if (privateFlag.equals(STAT_ON))
                {
                    mySetPersonShow.toggleSwitch(true);
                }
                else
                {
                    mySetPersonShow.toggleSwitch(false);
                }*/

        } catch (JSONException e)
        {
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } finally
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    @Override
    public void changeOrderStatCallback(ArrayMap<String, Object> map)
    {
        String response = (String) map.get("response");
        String stat = (String) map.get("state");
        Logger.t(TAG).d("获得的结果：" + response);
        if (stat.equals("1"))
        {
            mySetOrderShow.toggleSwitch(true);
        }
        else
        {
            mySetOrderShow.toggleSwitch(false);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

    }


    @Override
    public void changePushStatCallback(String body, String stat)
    {
        Logger.t(TAG).d("获得的结果：" + body);
        if (stat.equals("1"))
        {
            mySetMsgShow.toggleSwitch(true);
        }
        else
        {
            mySetMsgShow.toggleSwitch(false);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getFileLengthCallback(long length)
    {
        long fileLengthMb = Math.round(length / 1024 / 1024);
        if (length != 0 && fileLengthMb == 0)
            clearSize.setText(Math.round(length / 1024) + "KB");
        else
            clearSize.setText(fileLengthMb + "MB");
    }
}
