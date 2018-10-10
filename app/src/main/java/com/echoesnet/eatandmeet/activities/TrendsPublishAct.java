package com.echoesnet.eatandmeet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.listeners.ICommonOperateListener;
import com.echoesnet.eatandmeet.models.bean.FTrendsItemBean;
import com.echoesnet.eatandmeet.models.datamodel.EmojiIcon;
import com.echoesnet.eatandmeet.presenters.ImpITrendsPublishPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.ITrendsPublishView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.EamSmileUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.TrendsPublishImgAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.echoesnet.eatandmeet.views.widgets.TrendsEmojiView;
import com.joanzapata.iconify.widget.IconTextView;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/7/17 0017
 * @description 动态发布页面
 */
@RuntimePermissions
public class TrendsPublishAct extends MVPBaseActivity<TrendsPublishAct, ImpITrendsPublishPre> implements ITrendsPublishView
{
    private final String TAG = TrendsPublishAct.class.getSimpleName();

    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.edit_content)
    EditText contentEdit;
    @BindView(R.id.rlv_img_select)
    RecyclerView imgSelectRlv;
    @BindView(R.id.icon_tv_location)
    TextView addressIconTv;
    @BindView(R.id.icon_tv_video)
    IconTextView videoIconTv;
    @BindView(R.id.icon_tv_pic)
    IconTextView picIconTv;
    @BindView(R.id.icon_tv_emoji)
    IconTextView emojiIconTv;
    @BindView(R.id.emoji_view)
    TrendsEmojiView trendsEmojiView;
    @BindView(R.id.fl_video_thumbnail)
    FrameLayout videoThumbnailFl;
    @BindView(R.id.img_thumbnail)
    ImageView ThumbnailImg;
    @BindView(R.id.img_delete)
    ImageView deleteVideoImg;
    @BindView(R.id.ll_video)
    LinearLayout videoLL;

    private Activity mAct;
    private List<String> imgs;//保存选中的图片
    private TrendsPublishImgAdapter trendsPublishImgAdapter;
    private TrendsPublish trendsPublishType = TrendsPublish.TEXT;
    private String videoPath, thumbnailPath, mShowType;//视频路径 缩略图路径 照片或视频方向
    private final int maxImg = 6;
    private MyProgressDialog progressDialog;
    private String locationName;//选择位置的名称
    private double latitude, longitude;//经纬度
    private List<Map<String, TextView>> navBtn;
    private CustomAlertDialog customAlertDialog;
    private boolean isPublishIng = false;
    private boolean isShowFirstNewbie = true;
    private String type,topic;

    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        setContentView(R.layout.act_trends_publish);
        ButterKnife.bind(this);
        mAct = this;
        imgs = new ArrayList<>();
        type = getIntent().getStringExtra("type");
        topic = getIntent().getStringExtra("topic");
        trendsPublishImgAdapter = new TrendsPublishImgAdapter(imgs, mAct);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mAct);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imgSelectRlv.setLayoutManager(layoutManager);
        imgSelectRlv.setAdapter(trendsPublishImgAdapter);
        trendsPublishImgAdapter.setTrendsPublishItemClick(new TrendsPublishImgAdapter.TrendsPublishItemClick()
        {
            @Override
            public void itemClick(View view, int position)
            {
                CommonUtils.showImageBrowser(mAct, imgs, position, view);
            }

            @Override
            public void deleteClick(View view, int position)
            {
                imgs.remove(position);
                trendsPublishImgAdapter.notifyDataSetChanged();
                refreshTopBar();
            }

            @Override
            public void addClick()
            {
                selectPic();
            }
        });
        trendsEmojiView.setEmojiItemClick(new TrendsEmojiView.EmojiItemClick()
        {
            @Override
            public void itemClick(EmojiIcon emojicon)
            {
                contentEdit.append(EamSmileUtils.getSmiledText(mAct, emojicon.getEmojiText()));
            }

            @Override
            public void deleteClick()
            {
                if (!TextUtils.isEmpty(contentEdit.getText()))
                {
                    KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                    contentEdit.dispatchKeyEvent(event);
                }
            }
        });
        contentEdit.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
//                Logger.t("MyEditText").d("onTextChanged>>" + s + "|start>" + start + "|count>" + count + "|before>" + before);
                String changeString = s.subSequence(start,start + count).toString();
                if (SharePreUtils.getIsHasAct() && "#".equals(changeString) && !s.toString().contains("#下红包雨备战双十一，领千万现金！#"))
                {
                    contentEdit.setText(contentEdit.getText().toString() + "下红包雨备战双十一，领千万现金！#");
                    contentEdit.setSelection(contentEdit.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                refreshTopBar();
                if ("share".equals(type) && !TextUtils.isEmpty(topic) &&
                        contentEdit.getText().toString().length() < topic.length())
                    contentEdit.setText(topic);
            }
        });
        videoThumbnailFl.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                deleteVideoImg.setVisibility(View.VISIBLE);
                return true;
            }
        });
        customAlertDialog = new CustomAlertDialog(mAct).builder()
                .setNegativeButton("取消", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        customAlertDialog.dismiss();
                    }
                })
                // .setTitle("提示")
                .setCancelable(false);
        initTopBar();
        if ("share".equals(type))
        {
            contentEdit.setText(topic);
            contentEdit.setSelection(topic.length());
            contentEdit.setOnKeyListener(new View.OnKeyListener()
            {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event)
                {
                    Logger.t(TAG).d("onkey" + keyCode + "|" + event);
                    if (keyCode == KeyEvent.KEYCODE_DEL
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (contentEdit.getText().toString().length() <= topic.length())
                            return true;
                        else
                            return false;
                    }
                    return false;
                }
            });
            startRecordVideo(TrendsPublish.VIDEO);
        }
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                showNewbieGuide();
            }
        }, 800);
    }

    /**
     * @Description: 显示新手引导
     */
    private void showNewbieGuide()
    {
        if (SharePreUtils.getIsNewBieTrendsPublish(mAct))
        {
            NetHelper.checkIsShowNewbie(mAct, "5", new ICommonOperateListener()
            {
                @Override
                public void onSuccess(String response)
                {
                    if ("0".equals(response))
                    {
                        isShowFirstNewbie = true;
                        final ImageView imageView = new ImageView(mAct);
                        imageView.setImageResource(R.drawable.shooting);
                        final PopupWindow popupWindow = new PopupWindow(imageView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                        popupWindow.setBackgroundDrawable(new ColorDrawable());
                        popupWindow.setFocusable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setContentView(imageView);
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
                        {
                            @Override
                            public void onDismiss()
                            {
                                if (isShowFirstNewbie)
                                {
                                    isShowFirstNewbie = false;
                                    imageView.setImageResource(R.drawable.photo_newbie);
                                    popupWindow.showAsDropDown(picIconTv, CommonUtils.dp2px(mAct,35), -CommonUtils.dp2px(mAct,70));
                                }else {
                                    SharePreUtils.setIsNewBieTrendsPublish(mAct,false);
                                    NetHelper.saveShowNewbieStatus(mAct,"5");
                                }
                            }
                        });
                        if (mAct!=null)
                        {
                            popupWindow.showAsDropDown(videoIconTv, CommonUtils.dp2px(mAct, 35), -CommonUtils.dp2px(mAct, 70));
                        }
                    }else {
                        SharePreUtils.setIsNewBieTrendsPublish(mAct,false);
                    }
                }

                @Override
                public void onError(String code, String msg)
                {

                }
            });

        }
    }

    private void selectPic()
    {
        int count = maxImg - imgs.size();
        if (count <= 0)
        {
            ToastUtils.showShort("最多选取" + maxImg + "张照片,请删除后再选择");
            return;
        }
        PhotoPickActivity.pickPhotos(mAct,EamConstant.EAM_OPEN_IMAGE_PICKER,count,
                1,imgs.size() > 0?PhotoPickActivity.FILETYPE_IMAGE:PhotoPickActivity.FILETYPE_ALL);
//        PhotoPicker.builder()
//                .setPhotoCount(count >= 0 ? count : 0)
//                .setPreviewEnabled(true)
//                .setShowCamera(true)
//                .setShowGif(false)
//                .start(mAct, EamConstant.EAM_OPEN_IMAGE_PICKER);
    }

    private void initTopBar()
    {
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                if (!TextUtils.isEmpty(contentEdit.getText()))
                {
                    if (customAlertDialog == null)
                        customAlertDialog = new CustomAlertDialog(mAct).builder();
                    customAlertDialog.setBoldMsg("退出此次编辑?")
                            .setPositiveButton("退出", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    cancelPublish();
                                }
                            }).show();
                }
                else
                {
                    cancelPublish();
                }

            }

            @Override
            public void right2Click(View view)
            {
                publishTrends();
            }
        }).setText(mAct.getResources().getString(R.string.trends_publish));
        navBtn = topBarSwitch.getNavBtns2(new int[]{1, 0, 0, 1});
        for (int i = 0; i < navBtn.size(); i++)
        {
            if (i == 1)
            {
                TextView tv = navBtn.get(i).get(TopBarSwitch.NAV_BTN_ICON);
                tv.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
                tv.setText("{eam-e983}");
            }
        }
    }

    /**
     *  取消发布 清理 视频缩略图
     */
    private void cancelPublish()
    {
        Logger.t(TAG).d("清理缩略图");
        if (!TextUtils.isEmpty(thumbnailPath))
        {
            File file = new File(thumbnailPath);
            if (file.exists())
                file.delete();
        }
        finish();
    }

    private void refreshTopBar()
    {
        if (navBtn.size() > 1)
        {
            TextView textView = navBtn.get(1).get(TopBarSwitch.NAV_BTN_ICON);
            if (!TextUtils.isEmpty(contentEdit.getText().toString().trim()) || !TextUtils.isEmpty(videoPath) || imgs.size() > 0)
            {
                textView.setTextColor(ContextCompat.getColor(mAct, R.color.C0412));
            }
            else
            {
                textView.setTextColor(ContextCompat.getColor(mAct, R.color.C0323));
            }
        }

    }

    private void publishTrends()
    {
        if (isPublishIng)
            return;
        String content = contentEdit.getText().toString().trim();
        if ("share".equals(type)) // 分享直接发动态
        {
            mPresenter.startPublishTrends(trendsPublishType,imgs,videoPath,thumbnailPath,content,latitude,longitude,locationName,mShowType);
        }else { // 普通发动态 先展示 再发动态
            if (TextUtils.isEmpty(content) && imgs.size() == 0 && TextUtils.isEmpty(videoPath))
            {
                return;
            }
            isPublishIng = true;
            if (imgs.size() > 0)
                trendsPublishType = TrendsPublish.PICTURE;
            else if (!TextUtils.isEmpty(videoPath))
                trendsPublishType = TrendsPublish.VIDEO;
            else
                trendsPublishType = TrendsPublish.TEXT;
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            FTrendsItemBean trendsItemBean = new FTrendsItemBean();
            trendsItemBean.setContent(content);
            trendsItemBean.setIsLike("0");
            trendsItemBean.setLocation(locationName);
            trendsItemBean.setSex(SharePreUtils.getSex(mAct));
            trendsItemBean.setAge(SharePreUtils.getAge(mAct));
            trendsItemBean.setIsVuser(SharePreUtils.getIsVUser(mAct));
            trendsItemBean.setDistance("0.01km");
            trendsItemBean.setTimeToNow("刚刚");
            trendsItemBean.setCommentNum("0");
            trendsItemBean.setLevel(SharePreUtils.getLevel(mAct) + "");
            trendsItemBean.setNicName(SharePreUtils.getNicName(mAct));
            trendsItemBean.setPhurl(SharePreUtils.getHeadImg(mAct));
            trendsItemBean.setPosx(latitude + "");
            trendsItemBean.setPosy(longitude + "");
            trendsItemBean.setType("0");
            trendsItemBean.setLikedNum("0");
            trendsItemBean.setReadNum("0");
            trendsItemBean.setStamp(String.valueOf(System.currentTimeMillis()));
            trendsItemBean.setUp(SharePreUtils.getUId(mAct));
            if (trendsPublishType == TrendsPublish.VIDEO)
            {
                trendsItemBean.setThumbnails(thumbnailPath);
                trendsItemBean.setUrl(videoPath);
                trendsItemBean.setShowType(mShowType);
            }
            else if (trendsPublishType == TrendsPublish.PICTURE)
            {
                trendsItemBean.setUrl(CommonUtils.listToStrWishSeparator(imgs, CommonUtils.SEPARATOR));

                BitmapFactory.Options options = new BitmapFactory.Options();//判断横竖图--wb
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgs.get(0), options);
                int degree = ImageUtils.readPictureDegree(imgs.get(0));
                if (degree == 90 || degree == 270) // 90  或者 270 时宽高反了  反着 比较
                    trendsItemBean.setShowType(options.outHeight/options.outWidth>0?"0":"1");
                else
                    trendsItemBean.setShowType(options.outWidth/options.outHeight>0?"0":"1");
                Logger.t(TAG).d( android.os.Build.MODEL + "|" + degree +"图片宽高width:"+ options.outWidth + "| height:" + options.outHeight + "|" + options.outWidth/options.outHeight);
            }
            trendsItemBean.setTrendsPublish(trendsPublishType);
            bundle.putSerializable("FTrendsItemBean", trendsItemBean);
            intent.putExtra("publish", bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @OnClick({R.id.icon_tv_pic, R.id.ll_address, R.id.icon_tv_video, R.id.icon_tv_emoji, R.id.fl_video_thumbnail, R.id.img_delete})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.icon_tv_pic:
                if (trendsPublishType == TrendsPublish.VIDEO)
                {
                    customAlertDialog.setBoldMsg(mAct.getResources().getString(R.string.switch_to_picture))
                            .setNegativeButton("取消", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    customAlertDialog.dismiss();
                                }
                            })
                            .setPositiveButton("切换", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    startRecordVideo(TrendsPublish.PICTURE);
                                }
                            }).show();
                }
                else
                {
                    startRecordVideo(TrendsPublish.PICTURE);
                }

                break;
            case R.id.ll_address:
                Intent intent = new Intent(mAct, TrendsSelectLocationAct.class);
                intent.putExtra("showNo", TextUtils.isEmpty(locationName));
                startActivityForResult(intent, EamConstant.EAM_OPEN_SELECT_LOCATION);
                break;
            case R.id.icon_tv_video:
                if (trendsPublishType == TrendsPublish.VIDEO && !TextUtils.isEmpty(videoPath))
                {
                    new CustomAlertDialog(mAct)
                            .builder()
                            .setMsg("仅支持发送一个动态视频")
                            .setPositiveButton("确认", null).show();
                }
                else
                {
                    startRecordVideo(TrendsPublish.VIDEO);
                }
                break;
            case R.id.icon_tv_emoji:
                if (trendsEmojiView.getVisibility() == View.VISIBLE)
                {
                    trendsEmojiView.setVisibility(View.GONE);
                    emojiIconTv.setText("{eam-s-smile-face}");
                    contentEdit.requestFocus();
                    ShowSoftInput(true);
                }
                else
                {
                    emojiIconTv.setText("{eam-e662}");
                    trendsEmojiView.setVisibility(View.VISIBLE);
                    contentEdit.clearFocus();
                    ShowSoftInput(false);
                }
                break;
            case R.id.fl_video_thumbnail:
                Intent videoPlayIntent = new Intent(mAct, TrendsPlayVideoAct.class);
                videoPlayIntent.putExtra("url", videoPath);
                videoPlayIntent.putExtra("showType", mShowType);
                startActivity(videoPlayIntent);
                break;
            case R.id.img_delete:
                if (!TextUtils.isEmpty(videoPath))
                {
                    File file = new File(videoPath);
                    if (file.exists())
                        file.delete();
                    videoThumbnailFl.setVisibility(View.GONE);
                    trendsPublishType = TrendsPublish.PICTURE;
                    videoPath = "";
                    refreshTopBar();
                }

                break;
        }
    }

    private void startRecordVideo(TrendsPublish trendsPublish)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            TrendsPublishActPermissionsDispatcher.onCameraAudioPermGrantedWithPermissionCheck((TrendsPublishAct) mAct, trendsPublish);
        }
        else
        {
            onCameraAudioPermGranted(trendsPublish);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        TrendsPublishActPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermGranted(TrendsPublish trendsPublish)
    {
        Logger.t(TAG).d("允许获取权限");
        if (CommonUtils.cameraIsCanUse())
        {
            if (trendsPublish == TrendsPublish.VIDEO)
            {
                Intent intent = new Intent(mAct, TrendsRecordVideoAct.class);
                intent.putExtra("type", trendsPublishType);
                startActivityForResult(intent, EamConstant.EAM_OPEN_RECORD_VIDEO);
            }
            else
            {
                selectPic();
            }
        }
        else
        {
            ToastUtils.showShort("请释放相机资源");
        }
    }

    @OnPermissionDenied({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermDenied()
    {
        Logger.t(TAG).d("拒绝获取权限");
        ToastUtils.showLong("小饭没有获得相应的权限，无法为您进一步提供服务");
    }

    @OnNeverAskAgain({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermNeverAsk()
    {
        Logger.t(TAG).d("点击了不要再次询问获取权限");
        ToastUtils.showLong(getString(R.string.per_camera_never_ask));
    }

    @OnShowRationale({android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO})
    void onCameraAudioPermReason(final PermissionRequest request)
    {
        Logger.t(TAG).d("说明");
        new CustomAlertDialog(mAct)
                .builder()
                .setTitle("请求权限说明")
                .setMsg("需要使用您的相机和录音权限才能完成此功能！")
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_IMAGE_PICKER:
                if (resultCode == RESULT_OK)
                {
                    boolean isVideo = data.getBooleanExtra(PhotoPickActivity.EXTRA_VIDEO,false);
                    if (isVideo)
                    {
                        String videoPath = data.getStringExtra(PhotoPickActivity.EXTRA_VIDEO_PATH);
                        String thumbnailPath = data.getStringExtra(PhotoPickActivity.EXTRA_THUMBNAIL_PATH);
                        mShowType = data.getStringExtra(PhotoPickActivity.EXTRA_SHOW_TYPE);
                        Logger.t(TAG).d("videoPath>>>" + videoPath + "|thumbnailPath>>" + thumbnailPath);
                        publishVideo(videoPath,thumbnailPath);
                    }else {
                        ArrayList<String> mResults = data.getStringArrayListExtra(PhotoPickActivity.EXTRA_RESULT);
                        assert mResults != null;
                        switchToPicture("", mResults);
                        for (String str : mResults)
                        {
                            Logger.t(TAG).d(str);
                        }
                    }

                }
                break;
            case EamConstant.EAM_OPEN_SELECT_LOCATION:
                if (resultCode == RESULT_OK)
                {
                    boolean isShow = data.getBooleanExtra("isShow", false);
                    if (isShow)
                    {
                        locationName = data.getStringExtra("locationName");
                        latitude = data.getDoubleExtra("latitude", 0);
                        longitude = data.getDoubleExtra("longitude", 0);
                        Logger.t(TAG).d(locationName + "|" + latitude + "|" + longitude);
                        addressIconTv.setText(locationName);
                        addressIconTv.setFocusable(true);
                        addressIconTv.requestFocus();
                        addressIconTv.setSelected(true);
                    }
                    else
                    {
                        addressIconTv.setText("你在哪?");
                    }
                }
                break;
            case EamConstant.EAM_OPEN_RECORD_VIDEO:
                if (resultCode == RESULT_OK)
                {
                    String type = data.getStringExtra("type");
                    final String picUrl = data.getStringExtra("picUrl");
                    final String videoUrl = data.getStringExtra("videoUrl");
                    final String thumbnail = data.getStringExtra("thumbnail");
                    final String showType = data.getStringExtra("showType");
                    mShowType = showType;
                    Logger.t(TAG).d("picUrl=" + picUrl + "| videoUrl" + videoUrl + "|thumbnail = " + thumbnail + "|showType = " + showType);
                    if ("pic".equals(type) && !TextUtils.isEmpty(picUrl))
                    {
                        switchToPicture(picUrl, null);
                    }
                    else if (!TextUtils.isEmpty(videoUrl))
                    {
                       publishVideo(videoUrl,thumbnail);
                    }
                }
                break;
        }
    }

    private void publishVideo(String videoUrl, String thumbnail){
        if (trendsPublishType == TrendsPublish.PICTURE && imgs.size() != 0)
        {
            customAlertDialog.setBoldMsg(mAct.getResources().getString(R.string.switch_to_video))
                    .setNegativeButton("取消", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (!TextUtils.isEmpty(videoUrl))
                            {
                                File file = new File(videoUrl);
                                if (file.exists())
                                    file.delete();
                            }

                        }
                    })
                    .setPositiveButton("切换", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            switchToVideo(videoUrl, thumbnail);
                        }
                    }).show();
        }
        else
        {
            switchToVideo(videoUrl, thumbnail);
        }
    }

    private void ShowSoftInput(boolean isShow)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
        {
            if (isShow)
                imm.showSoftInput(contentEdit, 0);
            else
                imm.hideSoftInputFromWindow(contentEdit.getWindowToken(), 0);
        }
    }

    private void switchToPicture(final String picUrl, final List<String> picList)
    {
        videoThumbnailFl.setVisibility(View.GONE);
        imgSelectRlv.setVisibility(View.VISIBLE);
        trendsPublishType = TrendsPublish.PICTURE;
        if (picList != null)
        {
            imgs.addAll(imgs.size(), picList);
        }
        else
        {
            if (imgs.size() == 6)
            {
                ToastUtils.showShort("最多支持6张照片");
                return;
            }
            imgs.add(picUrl);
        }
        trendsPublishImgAdapter.notifyDataSetChanged();
        refreshTopBar();
    }

    private void switchToVideo(String videoUrl, String thumbnail)
    {
        imgs.clear();
        trendsPublishImgAdapter.notifyDataSetChanged();
        videoThumbnailFl.setVisibility(View.VISIBLE);
        deleteVideoImg.setVisibility(View.GONE);
        imgSelectRlv.setVisibility(View.GONE);
        trendsPublishType = TrendsPublish.VIDEO;
        videoPath = videoUrl;
        thumbnailPath = thumbnail;
        GlideApp.with(EamApplication.getInstance())
                .asBitmap()
                .load(thumbnail)
                .centerCrop()
                .into(ThumbnailImg);
        refreshTopBar();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if (ev.getAction() == MotionEvent.ACTION_DOWN)
        {
            View v = getCurrentFocus();
            if (isHideInputOrEmoji(v, ev))
            {
                contentEdit.clearFocus();
                ShowSoftInput(false);
            }
            if (isHideInputOrEmoji(trendsEmojiView, ev))
            {
                emojiIconTv.setText("{eam-s-smile-face}");
                trendsEmojiView.setVisibility(View.GONE);
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev))
        {
            return true;
        }
        return onTouchEvent(ev);
    }

    private boolean isHideInputOrEmoji(View v, MotionEvent event)
    {
        if (v != null && ((v instanceof EditText) || v instanceof TrendsEmojiView))
        {
            int[] leftTop = {0, 0};
            int[] videoLeftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            videoLL.getLocationInWindow(videoLeftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top)
            {
                if (v instanceof EditText && event.getY() > bottom && event.getY() < videoLeftTop[1])
                    return true;
                else
                    return false;
            }
            else
            {
                if (v instanceof TrendsEmojiView && event.getY() > top - videoLL.getHeight())
                    return false;
                else
                    return true;
            }
        }
        return false;
    }


    @Override
    public void requestNetError(String err)
    {
        isPublishIng = false;
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void publishTrendsCallback()
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        ToastUtils.showShort("发布成功");
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void shareAct2TrendsCallback()
    {
        ToastUtils.showShort("分享成功");
        finish();
    }

    @Override
    protected ImpITrendsPublishPre createPresenter()
    {
        return new ImpITrendsPublishPre();
    }

    @Override
    protected void onDestroy()
    {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (customAlertDialog != null)
            customAlertDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        cancelPublish();
    }

    public enum TrendsPublish implements Serializable
    {
        TEXT, VIDEO, PICTURE
    }
}
