package com.echoesnet.eatandmeet.activities.live;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.IdentityAuthAct;
import com.echoesnet.eatandmeet.activities.MVPBaseActivity;
import com.echoesnet.eatandmeet.activities.liveplay.Model.LiveRecord;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.ShareToFaceBean;
import com.echoesnet.eatandmeet.presenters.ImpLiveReadyView;
import com.echoesnet.eatandmeet.presenters.viewinterface.ILiveReadyView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.Log.EamLogger;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.LiveChooseAppointmentTimePop;
import com.echoesnet.eatandmeet.views.widgets.MaxByteLengthEditText;
import com.echoesnet.eatandmeet.views.widgets.MySurfaceView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.jakewharton.rxbinding2.view.RxView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.iwf.photopicker.PhotoPicker;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.echoesnet.eatandmeet.R.id.resident_location;

@RuntimePermissions
public class LiveReadyActivity extends MVPBaseActivity<ILiveReadyView, ImpLiveReadyView> implements ILiveReadyView
{
    private static final String TAG = LiveReadyActivity.class.getSimpleName();
    public static final int TO_IDENTITY_AUTH = 1001;
    public static final int TO_IDENTITY_AUTH_OK = 1002;
    @BindView(R.id.live_ready)
    AutoRelativeLayout liveReady;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.iv_shared_to_friend)
    CheckBox shared2Friend;
    @BindView(R.id.iv_shared_to_sina)
    CheckBox shared2Sina;
    @BindView(R.id.iv_shared_to_weChatFriend)
    CheckBox shared2weChatFriend;
    @BindView(R.id.iv_shared_to_weChatMoments)
    CheckBox shared2weChatMoment;
    @BindView(R.id.iv_shared_to_QQ)
    CheckBox shared2QQ;
    @BindView(R.id.iv_shared_to_QZone)
    CheckBox shared2QZone;
    @BindView(R.id.iv_addCover)
    AutoRelativeLayout addCover;
    @BindView(R.id.live_start)
    Button liveStart;
    @BindView(R.id.et_roomName)
    MaxByteLengthEditText etRoomName;
    @BindView(R.id.iv_cover)
    RoundedImageView ivCover;
    @BindView(R.id.rlPopCover)
    RelativeLayout rlPopCover;
    @BindView(R.id.appointment_time)
    TextView appointmentTime;
    @BindView(resident_location)
    TextView residentLocation;

    List<CheckBox> shareWays = new ArrayList<>();

    // 提交bean
    private String coverPath;//封面url
    private Dialog pDialog;
    private Activity mAct;
    //房间名
    private String roomName;
    private String acceptTime = "19:30 - 00:00";//可约时间
    private static final int SET_ADDRESS = 100;
    private List<Map<String, TextView>> navBtns;

    private String resultJsonStr;
    private LiveChooseAppointmentTimePop chooseAppointmentTimePop;
    private boolean isSetPermanent = false;

    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Logger.t(TAG).d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_ready);
        ButterKnife.bind(this);
        initAfterView();

    }

    @Override
    protected void onResume()
    {
        Logger.t(TAG).d("onResume");
        addNewCameraPreview();
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        Logger.t(TAG).d("onStart");
        super.onStart();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Logger.t(TAG).d("onPause");
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }


    @Override
    protected void onStop()
    {
        super.onStop();
        Logger.t(TAG).d("onStop");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mPresenter.dialogAtNull();
        if (disposable != null)
            disposable.dispose();
    }

    private void addNewCameraPreview()
    {
        removeCameraPreview();
        MySurfaceView cameraPreview = new MySurfaceView(mAct);
        cameraPreview.setId(R.id.liveReadyCameraPreview);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        cameraPreview.setLayoutParams(params);
        liveReady.addView(cameraPreview, 0);
    }

    private void removeCameraPreview()
    {
        MySurfaceView cameraPreview = (MySurfaceView) liveReady.findViewById(R.id.liveReadyCameraPreview);
        if (cameraPreview != null)
            liveReady.removeView(cameraPreview);
    }

    @Override
    protected ImpLiveReadyView createPresenter()
    {
        return new ImpLiveReadyView(this, this);
    }

    private void initAfterView()
    {
        mAct = this;
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
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
        });
        topBarSwitch.setBackground(ContextCompat.getDrawable(mAct, R.drawable.transparent));
        navBtns = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 0});

        for (int i = 0; i < navBtns.size(); i++)
        {
            TextView tv = navBtns.get(i).get(TopBarSwitch.NAV_BTN_ICON);
            tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0324));

        }
        pDialog = DialogUtil.getCommonDialog(this, "正在处理...");
        pDialog.setCancelable(false);

        shareWays.clear();
        shareWays.add(shared2Friend);
        shareWays.add(shared2Sina);
        shareWays.add(shared2weChatFriend);
        shared2weChatMoment.setChecked(true);
        shareWays.add(shared2weChatMoment);
        shareWays.add(shared2QQ);
        shareWays.add(shared2QZone);

        appointmentTime.setSelected(true);
        residentLocation.setSelected(true);
        etRoomName.setMaxByteLength(30);
        appointmentTime.setText("约会时间为：" + acceptTime);
        if (mPresenter != null)
        {
            mPresenter.checkLivePlayContextStatus();
            mPresenter.getPermanentOrNot();
        }

        RxView.clicks(liveStart)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>()
                {
                    @Override
                    public void accept(Object o) throws Exception
                    {
                        if (!isSetPermanent)
                        {
                            ToastUtils.showShort("您还未设置常驻位置，请先进行设置");
                            return;
                        }
                        if (TextUtils.isEmpty(acceptTime))
                        {
                            ToastUtils.showShort("请先选择您可约会的时间");
                            if (chooseAppointmentTimePop == null)
                                chooseAppointmentTimePop = new LiveChooseAppointmentTimePop(mAct);
                            chooseAppointmentTimePop.setOnDismissListener(new PopupWindow.OnDismissListener()
                            {
                                @Override
                                public void onDismiss()
                                {
                                    rlPopCover.setVisibility(View.GONE);
                                }
                            });
                            chooseAppointmentTimePop.setOnSelectFinishListener(finishedSelectPeriodsListener);
                            chooseAppointmentTimePop.showAtLocation(mAct.findViewById(R.id.live_ready), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                            rlPopCover.setVisibility(View.VISIBLE);
                            return;
                        }
                        LiveReadyActivityPermissionsDispatcher.onRecordPermGrantedWithPermissionCheck(LiveReadyActivity.this);
                    }
                });

    }


    @OnClick({R.id.iv_shared_to_friend, R.id.iv_shared_to_sina, R.id.iv_shared_to_weChatFriend,
            R.id.iv_shared_to_weChatMoments, resident_location, R.id.iv_addCover, R.id.appointment_time,
            R.id.iv_shared_to_QQ, R.id.iv_shared_to_QZone})
    void onclick(final View view)
    {
        switch (view.getId())
        {
            //添加封面
            case R.id.iv_addCover:
//                if (headerPopupWindow == null || !headerPopupWindow.isShowing())
//                {
//                    takePhotoAndCameraPop();
//                    headerPopupWindow.showAtLocation(findViewById(R.id.iv_addCover), Gravity.BOTTOM, 0, 0);
//                }

                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setPreviewEnabled(true)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .start(mAct, EamConstant.EAM_TAKE_PHOTO_FROM_GALLERY);

                break;
            case R.id.iv_shared_to_friend:
                selectShareWay(shareWays, 0);
                break;
            case R.id.iv_shared_to_sina:
                selectShareWay(shareWays, 1);
                break;
            case R.id.iv_shared_to_weChatFriend:
                selectShareWay(shareWays, 2);
                break;
            case R.id.iv_shared_to_weChatMoments:
                selectShareWay(shareWays, 3);
                break;
            case R.id.iv_shared_to_QQ:
                selectShareWay(shareWays, 4);
                break;
            case R.id.iv_shared_to_QZone:
                selectShareWay(shareWays, 5);
                break;
            case R.id.resident_location:
                Intent intent = new Intent(mAct, LiveSetAddressAct.class);
                startActivityForResult(intent, SET_ADDRESS);
                break;
            case R.id.appointment_time:
                if (chooseAppointmentTimePop == null)
                    chooseAppointmentTimePop = new LiveChooseAppointmentTimePop(mAct);
                chooseAppointmentTimePop.setOnDismissListener(new PopupWindow.OnDismissListener()
                {
                    @Override
                    public void onDismiss()
                    {
                        rlPopCover.setVisibility(View.GONE);
                    }
                });
                chooseAppointmentTimePop.setOnSelectFinishListener(finishedSelectPeriodsListener);
                chooseAppointmentTimePop.showAtLocation(mAct.findViewById(R.id.live_ready), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                rlPopCover.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void selectShareWay(List<CheckBox> shareWays, int index)
    {
        for (int i = 0; i < shareWays.size(); i++)
        {
            CheckBox cb = shareWays.get(i);
            if (index != i)
                cb.setChecked(false);
        }
    }

    /**
     * 检查是否选中除了分享邻座外的其他方式
     */
    private void checkIsHaveShared()
    {
        ShareToFaceBean shareBean = new ShareToFaceBean();
        shareBean.setShareType(Platform.SHARE_WEBPAGE);
        shareBean.setShareTitle(roomName);
        shareBean.setShareTitleUrl(NetHelper.LIVE_SHARE_ADDRESS + SharePreUtils.getTlsName(mAct).substring(1));
        shareBean.setShareWeChatMomentsTitle("越帅越优惠 越靓越实惠,看脸吃饭 【" + roomName + "】");
        shareBean.setShareUrl(NetHelper.LIVE_SHARE_ADDRESS + SharePreUtils.getTlsName(mAct).substring(1));
        shareBean.setShareImgUrl(TextUtils.isEmpty(coverPath) ? SharePreUtils.getHeadImg(mAct) : coverPath);
        shareBean.setShareContent(roomName + ",颜值高不高，直播才知道，赶快来给我送礼物吧！");
        shareBean.setShareWeChatMomentsContent("越帅越优惠 越靓越实惠,看脸吃饭 【" + roomName + "】");
        shareBean.setShareSinaContent("我的直播间是“" + roomName + "”,颜值高不高，直播才知道，赶快来给我送礼物吧！"
                + NetHelper.LIVE_SHARE_ADDRESS + SharePreUtils.getTlsName(mAct).substring(1));
        shareBean.setShareAppImageUrl(NetHelper.LIVE_SHARE_PIC);
        shareBean.setShareSite("看脸吃饭");
        shareBean.setOpenSouse("liveShare");
        shareBean.setRoomId(SharePreUtils.getId(mAct));
        shareBean.setRoomName(SharePreUtils.getNicName(mAct));
        shareBean.setShareSiteUrl(NetHelper.SERVER_SITE);
//
        shareBean.setShareListener(listener);
        if (shared2Sina.isChecked())
        {
            Logger.t(TAG).d("微博分享");
            CommonUtils.shareWithApp(mAct, "新浪微博", shareBean);
        } else if (shared2Friend.isChecked())
        {
            Logger.t(TAG).d("看脸好友");
            CommonUtils.shareWithApp(mAct, "看脸好友", shareBean);
            //  return;
        } else if (shared2weChatFriend.isChecked())
        {
            Logger.t(TAG).d("朋友");
            CommonUtils.shareWithApp(mAct, "微信好友", shareBean);
        } else if (shared2weChatMoment.isChecked())
        {
            Logger.t(TAG).d("朋友圈");
            CommonUtils.shareWithApp(mAct, "微信朋友圈", shareBean);
        } else if (shared2QQ.isChecked())
        {
            CommonUtils.shareWithApp(mAct, "QQ好友", shareBean);
        } else if (shared2QZone.isChecked())
        {
            CommonUtils.shareWithApp(mAct, "QQ空间", shareBean);
        } else
        {
            startLive();
        }
    }

    private void startLive()
    {

        Logger.t(TAG).d("设备版本号" + "Product Model: " + android.os.Build.MODEL);

        try
        {
            if (TextUtils.isEmpty(resultJsonStr))
                return;
            JSONObject object = new JSONObject(resultJsonStr);
            //房间id
            Logger.t(TAG).d(">>>"+resultJsonStr.toString());
            String roomId = object.getString("roomId");
            String roomName = object.getString("roomName");
            String sign = object.getString("sign");
            String vedioName = object.getString("vedioName");
            // TODO: 2017/11/30 lc
            String star = object.getString("star");//星光活动，主播当前拥有的星光值
            String ranking = object.getString("ranking");//星光活动，主播当前排名，0：未上榜
            String content = String.format("房间号》%s; 房间名》%s;sign》%s%n%n", roomId, roomName, sign);
            mPresenter.checkLiveIsAlreadyCreate(roomId, vedioName);
            //CommonUtils.writeLog2File(mAct, content, "livequitlog.txt");
            EamLogger.t(TAG).writeToDefaultFile(content);

        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    PlatformActionListener listener = new PlatformActionListener()
    {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    startLive();
                    ToastUtils.showShort("分享成功");
                }
            });
            NetHelper.addLiveShareCount(mAct);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable)
        {
            if (throwable instanceof cn.sharesdk.tencent.qzone.QQClientNotExistException)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ToastUtils.showLong("请安装QQ客户端");
                    }
                });
            } else
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        startLive();
                        ToastUtils.showShort("分享失败");
                    }
                });
            }
        }

        @Override
        public void onCancel(Platform platform, int i)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    startLive();
                    ToastUtils.showShort("分享取消");
                }
            });
        }
    };

    /**
     * 裁剪
     *
     * @param source
     */
    private void beginCrop(Uri source)
    {
        Logger.t(TAG).d("beginCrop");
        String fileName = "a_" + SharePreUtils.getUserMobile(this) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        Logger.t(TAG).d(getCacheDir() + "/" + fileName);
        Uri destination = Uri.fromFile(new File(getCacheDir(), CommonUtils.toMD5(fileName)));
        Crop.of(source, destination).asSquare().withAspect(4, 4).withMaxSize(500, 500).start(this);
    }

    /**
     * 处理裁剪后图片处理
     *
     * @param resultCode
     * @param result
     */
    private void handleCrop(int resultCode, Intent result)
    {
        if (resultCode == RESULT_OK)
        {
            Uri uri = Crop.getOutput(result);
            Logger.t(TAG).d("handleCrop");
            updateImg(uri);
        } else if (resultCode == Crop.RESULT_ERROR)
        {
           ToastUtils.showShort( Crop.getError(result).getMessage());
        }
    }

    //传封面
    private void updateImg(final Uri inputUri)
    {
        String fileKeyName = CdnHelper.liveImage + SharePreUtils.getUserMobile(this) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        CdnHelper.getInstance().setOnCdnFeedbackListener(new CdnFeedbackListener(this, inputUri));
        CdnHelper.getInstance().putFile(new File(inputUri.getPath()), "img", fileKeyName, 0);
    }


    //相机取图
    private void toTakePhotoFromCamera()
    {
        removeCameraPreview();
        boolean iscamerapermissions = CommonUtils.cameraIsCanUse();
        if (iscamerapermissions)
        {
            File storageDir = this.getExternalCacheDir();
            File image = new File(storageDir, "TempImage.jpg");
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (openCameraIntent.resolveActivity(this.getPackageManager()) != null)
            {
                //openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.echoesnet.eatandmeet.provider", image));
                startActivityForResult(openCameraIntent, EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA);
            }
        } else
        {
            ToastUtils.showShort("请打开相机功能");
        }
    }


    //相册取图
    private void toPhotoFromImageAblum()
    {
        removeCameraPreview();
        Intent getImageIntent = new Intent(Intent.ACTION_PICK, null);
        getImageIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        if (getImageIntent.resolveActivity(this.getPackageManager()) != null)
        {
            //return the call activity after get the picture.
            startActivityForResult(getImageIntent, Crop.REQUEST_PICK);
        }
    }


    /**
     * 调用拍照和相册的弹出层
     */
    PopupWindow headerPopupWindow;
    View headerView;
    AutoLinearLayout ll_popup;

    private void takePhotoAndCameraPop()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(R.layout.item_popupwindows, null);
        headerPopupWindow = new PopupWindow(headerView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        ll_popup = (AutoLinearLayout) headerView.findViewById(R.id.ll_popup);
        TextView item_popupwindows_camera = (TextView) headerView.findViewById(R.id.item_popupwindows_camera);
        Button item_popupwindows_cancel = (Button) headerView.findViewById(R.id.item_popupwindows_cancel);

        item_popupwindows_camera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toTakePhotoFromCamera();
                headerPopupWindow.dismiss();
            }
        });
        TextView item_popupwindows_Photo = (TextView) headerView.findViewById(R.id.item_popupwindows_Photo);
        item_popupwindows_Photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toPhotoFromImageAblum();
                headerPopupWindow.dismiss();
            }
        });
        headerPopupWindow.getContentView().setFocusable(true);
        headerPopupWindow.setOutsideTouchable(true);
        headerPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        ColorDrawable colorDrawable = new ColorDrawable(0xb0000000);
        headerPopupWindow.setBackgroundDrawable(colorDrawable);
        headerPopupWindow.getContentView().setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0
                        && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (headerPopupWindow != null && headerPopupWindow.isShowing())
                    {
                        headerPopupWindow.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });

        headerView.setOnTouchListener(new View.OnTouchListener()
        {

            public boolean onTouch(View v, MotionEvent event)
            {

                int height = headerView.findViewById(R.id.ll_popup).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (y < height)
                    {
                        headerPopupWindow.dismiss();
                    }
                }
                return true;
            }
        });

        item_popupwindows_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (headerPopupWindow != null && headerPopupWindow.isShowing())
                {
                    headerPopupWindow.dismiss();
                }
            }
        });
    }


    //在 onResume() 前调用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        Logger.t(TAG).d(requestCode + "," + resultCode);
        switch (requestCode)
        {
            case EamConstant.EAM_TAKE_PHOTO_FROM_GALLERY:

                if (resultCode == RESULT_OK)
                {
//
                    ArrayList<String> mResults = result.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    Logger.t(TAG).d("图片地址：" + mResults.get(0));
                    File imgPath = new File(mResults.get(0));

                    String tempFileName = "a_" + SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                    String outPutImagePath = NetHelper.getRootDirPath(mAct) + CommonUtils.toMD5(tempFileName);
                    File outFile = new File(outPutImagePath);

                    // int degree = ImageUtils.readPictureDegree(result.getData().getPath().replace("/raw", ""));
                    int degree = ImageUtils.readPictureDegree(mResults.get(0));
                    //         Logger.t(TAG).d("旋转角度为》" + degree + " , " + result.getData().getPath());

                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.fromFile(imgPath), mAct);
                    if (bitmap != null)
                    {
                        ImageUtils.compressBitmap(bitmap, outFile.getPath(), 150, degree);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                        beginCrop(Uri.fromFile(outFile));
                    }
                    else
                    {
                        ToastUtils.showShort("选择图片失败，请重新选择");
                    }
                }
                break;


            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK)
                {
                    File imgPath = new File(getExternalCacheDir() + "/" + "TempImage.jpg");
                    String tempFileName = "a_" + SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                    String outPutImagePath = NetHelper.getRootDirPath(mAct) + CommonUtils.toMD5(tempFileName);
                    File outFile = new File(outPutImagePath);
                    int degree = ImageUtils.readPictureDegree(imgPath.getAbsolutePath());
                    ImageUtils.compressBitmap(ImageUtils.getBitmapFromUri(result.getData(), mAct), outFile.getPath(), 130, degree);
                    Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                    beginCrop(result.getData());
                    if (!imgPath.delete())
                    {
                        Logger.t(TAG).d("文件删除失败");
                    }
                }
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, result);
                break;
            case EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA:
                Logger.t(TAG).d("拍照进入照片路径");
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        File imgPath = new File(getExternalCacheDir() + "/" + "TempImage.jpg");
                        String tempFileName = "a_" + SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                        String outPutImagePath = NetHelper.getRootDirPath(mAct) + CommonUtils.toMD5(tempFileName);
                        // Uri uri1 = Uri.parse("file://" + "/" + imgPath.getAbsolutePath());
                        Logger.t(TAG).d("照片路径：" + imgPath.getAbsolutePath());
                        final File outFile = new File(outPutImagePath);

                        // zdw --- 添加针对三星拍照后旋转90显示问题
                        int degree = ImageUtils.readPictureDegree(imgPath.getAbsolutePath());
                        ImageUtils.compressBitmap(ImageUtils.getBitmapFromFile(mAct, imgPath.getAbsolutePath()), outFile.getPath(), 130, degree);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);

                        beginCrop(FileProvider.getUriForFile(this, "com.echoesnet.eatandmeet.provider", outFile));
                        imgPath.delete();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case EamConstant.EAM_LIVE_SHARED_OPEN_SOURCE:
//                if (resultCode == RESULT_OK)
//                {
//
//
//                   // mPresenter.startLive(etRoomName.getText().toString(), coverPath, acceptTime);
//                }
                startLive();
                break;
            case SET_ADDRESS:
                if (resultCode == RESULT_OK)
                {
                    isSetPermanent = true;
                    String address = result.getStringExtra("address");
                    residentLocation.setText(address);
                }
                break;
            case TO_IDENTITY_AUTH:
                if (resultCode == TO_IDENTITY_AUTH_OK)
                {
                    startLive();
                }
                break;
            case EamConstant.EAM_OPEN_RELATION:
                if (resultCode == RESULT_OK)
                {
                    NetHelper.addLiveShareCount(mAct);
                    startLive();
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void requestNetError(Throwable call, String interfaceName)
    {
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }

    }
    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_startLive:
                if (ErrorCodeTable.NO_REALNAME.equals(code))
                {
                    Intent intent = new Intent(this, IdentityAuthAct.class);
                    intent.putExtra("fromType", "liveReady");
                    startActivityForResult(intent, LiveReadyActivity.TO_IDENTITY_AUTH);
                }
                Logger.t(TAG).d("错误码为：%s", code);
                break;
            default:
                break;
        }

    }


    @Override
    public void startLiveSuccess(String resultJsonStr)
    {
        this.resultJsonStr = resultJsonStr;
        EamApplication.getInstance().hasCallServerStartLived = true;
        checkIsHaveShared();
        //   showShareToFriends();
    }


    @Override
    public void getPermanentOrNotCallback(String response)
    {
        try
        {
            isSetPermanent = true;
            JSONObject object = new JSONObject(response);
            String permanent = object.getString("permanent");
            residentLocation.setText("您设置的常驻位置：" + permanent);
            if (pDialog != null && pDialog.isShowing())
            {
                pDialog.dismiss();
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d(e.getMessage());
        }
    }

    @Override
    public void checkLiveIsAlreadyCreateCallback(Map<String, String> map)
    {
        String response = map.get("response");
        String roomId = map.get("roomId");
        String vedioName = map.get("vedioName");
        Logger.t(TAG).d("腾讯删除群组返回的结果》" + response);
        try
        {
            JSONObject result = new JSONObject(response);
            String status = result.getString("ActionStatus");
            EamApplication.getInstance().livePage.put(roomId, coverPath);
            CommonUtils.startLiveProxyAct(mAct, LiveRecord.ROOM_MODE_HOST, roomName, vedioName, coverPath, roomId, null, 1);
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    finish();
                }
            },1000);
            if (status.equals("OK"))
            {

            } else
            {
                int errorCode = result.getInt("ErrorCode");
                Logger.t(TAG).d("错误码》" + errorCode);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("错误码》" + e.getMessage());
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("错误码》" + e.getMessage());
        }
    }

    /**
     * CND调用回调
     */
    private static class CdnFeedbackListener implements IOnCdnFeedbackListener
    {
        private final WeakReference<LiveReadyActivity> mActRef;
        private Uri imgUri;

        private CdnFeedbackListener(LiveReadyActivity mAct, Uri imgUri)
        {
            this.mActRef = new WeakReference<LiveReadyActivity>(mAct);
            this.imgUri = imgUri;
        }

        @Override
        public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
        {
            final LiveReadyActivity cAct = mActRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("成功：" + response.toString());
                cAct.addCover.getChildAt(0).setVisibility(View.GONE);
                GlideApp.with(cAct)
                        .asBitmap()
                        .load(imgUri)
                        .centerCrop()
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(cAct.ivCover);
                cAct.coverPath = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                if (cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
            }
        }

        @Override
        public void onProcess(long len)
        {
        }

        @Override
        public void onFail(JSONObject response, File file)
        {
            final LiveReadyActivity cAct = mActRef.get();
            if (cAct != null)
                Logger.t(cAct.TAG).d("错误：" + response.toString());
        }
    }


    LiveChooseAppointmentTimePop.IFinishedSelectPeriodsListener finishedSelectPeriodsListener = new LiveChooseAppointmentTimePop.IFinishedSelectPeriodsListener()
    {
        @Override
        public void selectPeriodsFinish(List<String> periods)
        {
            acceptTime = "";
            if (periods != null)
            {
                if (periods.size() == 1)
                {
                    acceptTime = periods.get(0);
                } else
                {
                    for (String period : periods)
                    {
                        acceptTime += period + " ";
                    }
                }
            }
            Logger.t(TAG).d(">>" + acceptTime + "<<");
            appointmentTime.setText("约会时间：" + acceptTime);
        }
    };

    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void onRecordPermGranted()
    {
        if (TextUtils.isEmpty(etRoomName.getText().toString()))
        {
            roomName = SharePreUtils.getNicName(mAct) + "的直播间";
        } else
        {
            roomName = etRoomName.getText().toString();
        }
        if (TextUtils.isEmpty(coverPath))
        {
            coverPath = SharePreUtils.getHeadImg(mAct);
        }

        if (mPresenter != null)//是否一定传入名字待定
        {
            Logger.t(TAG).d("shared2Friend>>>>" + "进入");
            mPresenter.startLive(etRoomName.getText().toString(), coverPath, acceptTime);
        }
    }

    @OnPermissionDenied(Manifest.permission.RECORD_AUDIO)
    void onRecordPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain(Manifest.permission.RECORD_AUDIO)
    void onRecordPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        CommonUtils.openPermissionSettings(mAct, getResources().getString(R.string.per_permission_never_ask).replace(CommonUtils.SEPARATOR, "录音"));
//        ToastUtils.showLong(getString(R.string.per_record_never_ask));
    }

    @OnShowRationale({android.Manifest.permission.CAMERA})
    void onRecordPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的录音权限才能完成此功能！")
                .setPositiveButton("允许", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.proceed();
                    }
                })
                .setNegativeButton("拒绝", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        request.cancel();
                    }
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LiveReadyActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
