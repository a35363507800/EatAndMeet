package com.echoesnet.eatandmeet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.UsersBean;
import com.echoesnet.eatandmeet.presenters.ImpIMakeUserInfoView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMakeUserInfoView;
import com.echoesnet.eatandmeet.utils.AndroidBug5497Workaround;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.IMUtils.TencentHelper;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CityWheelPicker.CityWheelPicker;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.MaxByteLengthEditText;
import com.echoesnet.eatandmeet.views.widgets.SelectWheelPicker.ISelectItemFinishListener;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.hyphenate.EMError;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.widget.WheelView;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

/**
 * 完善用户资料页
 */
public class MakeUserInfo extends MVPBaseActivity<MakeUserInfo, ImpIMakeUserInfoView> implements IMakeUserInfoView
{
    public static final String TAG = MakeUserInfo.class.getSimpleName();
    //region 变量
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBarSwitch;
    @BindView(R.id.tv_profile_nickname)
    MaxByteLengthEditText fetNickName;
    @BindView(R.id.tv_profile_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_profile_city)
    TextView tvCity;
    @BindView(R.id.tv_make_money)
    TextView tvMakeInfoMoney;
    @BindView(R.id.tv_profile_invitation_code)
    EditText fetInviteCode;
    @BindView(R.id.all_parent)
    LinearLayout aalParent;
    @BindView(R.id.riv_edit_head)
    RoundedImageView rivHeadImg;
    @BindView(R.id.btn_profile_ok)
    Button btnSubmit;
    @BindView(R.id.tv_upload_progress)
    TextView tvUploadProgress;
    @BindView(R.id.rl_male)
    RelativeLayout rlMale;
    @BindView(R.id.rl_female)
    RelativeLayout rlFeMale;
    @BindView(R.id.tv_male)
    TextView tvMale;
    @BindView(R.id.tv_female)
    TextView tvFeMale;
    @BindView(R.id.itv_male)
    IconTextView itvMale;
    @BindView(R.id.itv_female)
    IconTextView itvFeMale;
    @BindView(R.id.rl_birthday)
    RelativeLayout rlBirthday;
    @BindView(R.id.itv_add_icon)
    IconTextView itvAddIcon;

    private List<RelativeLayout> genderRlViews = new ArrayList<>();
    private Activity mAct;
    private String headImgUrl;
    private String genderStr;
    private CityWheelPicker mCityWheelPicker;
    private MyProgressDialog pDialog;

    PopupWindow headerPopupWindow;
    View headerView;
    AutoLinearLayout ll_popup;

    //标示尝试注册环信的次数，如果大于3次则放弃注册（几率不大，不然环信可以扔了）
    private int commitCount = 0;

    private UsersBean user = null;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_make_user_info);
        ButterKnife.bind(this);
        initAfterView();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    /**
     * popupWindow在显示中点击返回杀死当前页会造成窗口泄露
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (headerPopupWindow != null && headerPopupWindow.isShowing())
        {
            headerPopupWindow.dismiss();
            headerPopupWindow = null;
        }
        pDialog = null;
        IMHelper.getInstance().removeIRegisterFinishedListener();
        IMHelper.getInstance().removeLoginFinishListener();
    }


    void initAfterView()
    {
        mAct = this;
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText(getResources().getString(R.string.make_userInfo));

        topBarSwitch.getNavBtns(new int[]{0, 0, 0, 0});
        AndroidBug5497Workaround.assistActivity(this);
        pDialog = new MyProgressDialog()
                .buildDialog(mAct)
                .setDescription("正在处理...");
        pDialog.setCancelable(false);
        //给文本框限制字节
        fetNickName.setMaxByteLength(14);
        fetInviteCode.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                    ToastUtils.showShort("邀请码要区分大小写");
            }
        });
        if (mPresenter != null)
            mPresenter.getRegisterPresent();

        //初始化性别列表
        genderRlViews.clear();
        rlMale.setSelected(false);
        rlFeMale.setSelected(false);
        genderRlViews.add(rlMale);
        genderRlViews.add(rlFeMale);
    }

    @OnClick({R.id.tv_profile_birthday, R.id.tv_profile_city,
            R.id.riv_edit_head, R.id.btn_profile_ok, R.id.rl_male, R.id.rl_female, R.id.rl_birthday,
            R.id.rl_city})
    void onViewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rl_city:
                mCityWheelPicker = new CityWheelPicker(MakeUserInfo.this);
                mCityWheelPicker.setUpData("provincedata/province_data.xml");
                mCityWheelPicker.setUpListener();
                mCityWheelPicker.setOnSelectFinishListener(new ISelectItemFinishListener()
                {
                    @Override
                    public void finishSelect(int selectedIndex, String sItem)
                    {
                        tvCity.setText(sItem);
                        tvCity.setTextColor(ContextCompat.getColor(MakeUserInfo.this, R.color.c3));
                    }
                });
                mCityWheelPicker.setOnDismissListener(new PopupWindow.OnDismissListener()
                {
                    @Override
                    public void onDismiss()
                    {
                        mCityWheelPicker.backgroundAlpha(1f);
                    }
                });
                mCityWheelPicker.showPopupWindow(aalParent);
                break;
            case R.id.rl_birthday:
                showDatePicker(tvBirthday);
                break;
            case R.id.rl_male:
//                showPopupReview(0, R.layout.popup_review_head_portrait).showAsDropDown(rlMale);
                setGenderRl(R.id.rl_male);
                genderStr = "男";
                break;
            case R.id.rl_female:
//                showPopupReview(1, R.layout.popup_review_head_portrait_yes).showAsDropDown(rlFeMale);
                setGenderRl(R.id.rl_female);
                genderStr = "女";
                break;
            case R.id.tv_profile_birthday:
                showDatePicker(tvBirthday);
                break;
            case R.id.tv_profile_city:
                mCityWheelPicker = new CityWheelPicker(MakeUserInfo.this);
                mCityWheelPicker.setUpData("provincedata/province_data.xml");
                mCityWheelPicker.setUpListener();
                mCityWheelPicker.setOnSelectFinishListener(new ISelectItemFinishListener()
                {
                    @Override
                    public void finishSelect(int selectedIndex, String sItem)
                    {
                        tvCity.setText(sItem);
                        tvCity.setTextColor(ContextCompat.getColor(MakeUserInfo.this, R.color.c3));
                    }
                });
                mCityWheelPicker.setOnDismissListener(new PopupWindow.OnDismissListener()
                {
                    @Override
                    public void onDismiss()
                    {
                        mCityWheelPicker.backgroundAlpha(1f);
                    }
                });
                mCityWheelPicker.showPopupWindow(aalParent);
                break;
            case R.id.riv_edit_head:
//                chooseMakeUserPhoto();
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setPreviewEnabled(true)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .start(mAct, Crop.REQUEST_PICK);
                break;
            case R.id.btn_profile_ok://提交
                if (verifyInput())
                {
                    if (CommonUtils.isFastDoubleClick())
                        return;
//                    StatService.onEvent(mAct, "make_user_info", getString(R.string.baidu_other), 1);
                    if (user == null)
                        user = new UsersBean();
                    user.setNicName(fetNickName.getText().toString());
                    user.setBirth(tvBirthday.getText().toString());
                    user.setCity(tvCity.getText().toString());
                    user.setSex(genderStr);
                    user.setMobile(SharePreUtils.getUserMobile(this));
                    user.setUphUrl(headImgUrl);
                    user.setInCode(fetInviteCode.getText().toString());

                    /**
                     * 请不要轻易改动此处流程，改动时需小心-wb
                     * 注册流程：1.注册环信  成功》向后台提交注册数据(inputUserInfo)
                     *                      失败》在有网的情况先尝试3次重新注册，仍然失败后向后台提交注册数据(inputUserInfo)
                     *          2.保存环信账号数据到本地并登陆，保存腾讯账号数据并登陆
                     *          3.不理会上述三方服务登陆成功与否，直接启动home页
                     */
                    registerHuanXin(user);
                }
                break;
            default:
                break;
        }
    }

    private boolean isFirstSetGenderRl()
    {
        boolean isFirst = true;
        for (RelativeLayout genderView : genderRlViews)
        {
            if (genderView.isSelected())
            {
                isFirst = false;
                break;
            }
        }
        return isFirst;
    }

    private void setGenderRl(int rlId)
    {
        if (isFirstSetGenderRl())
        {
            new CustomAlertDialog(mAct)
                    .builder()
                    .setMsg("注册成功后，不允许修改性别，请慎重选择！")
                    .setPositiveButton("确定", new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                        }
                    }).show();
        }
        for (RelativeLayout genderView : genderRlViews)
        {
            if (genderView.getId() == rlId)
            {
                genderView.setSelected(true);
                if (genderView.getId() == R.id.rl_male)
                {
                    rlMale.setBackground(ContextCompat.getDrawable(mAct, R.drawable.male_pressed));
                    itvMale.setTextColor(ContextCompat.getColor(mAct, R.color.white));
                    tvMale.setTextColor(ContextCompat.getColor(mAct, R.color.C0311));
                }
                else
                {
                    rlFeMale.setBackground(ContextCompat.getDrawable(mAct, R.drawable.female_pressed));
                    itvFeMale.setTextColor(ContextCompat.getColor(mAct, R.color.white));
                    tvFeMale.setTextColor(ContextCompat.getColor(mAct, R.color.C0313));
                }
            }
            else
            {
                genderView.setSelected(false);
                if (genderView.getId() == R.id.rl_male)
                {
                    rlMale.setBackground(ContextCompat.getDrawable(mAct, R.drawable.male_bg_normal));
                    itvMale.setTextColor(ContextCompat.getColor(mAct, R.color.FC3));
                    tvMale.setTextColor(ContextCompat.getColor(mAct, R.color.FC3));
                }
                else
                {
                    rlFeMale.setBackground(ContextCompat.getDrawable(mAct, R.drawable.male_bg_normal));
                    itvFeMale.setTextColor(ContextCompat.getColor(mAct, R.color.FC3));
                    tvFeMale.setTextColor(ContextCompat.getColor(mAct, R.color.FC3));
                }
            }
        }
    }

    /**
     * 生日选择器
     */
    private void showDatePicker(final TextView birth)
    {
        DatePicker picker = new DatePicker(mAct, DateTimePicker.YEAR_MONTH_DAY);
        picker.setCycleDisable(true);
        picker.setLineVisible(true);
        picker.setTopLineVisible(false);
        picker.setShadowVisible(false);
        picker.setTitleText("选择生日");
        picker.setTitleTextSize(14);
        picker.setTitleTextColor(ContextCompat.getColor(mAct, R.color.C0321));
        picker.setCancelTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        picker.setSubmitTextColor(ContextCompat.getColor(mAct, R.color.C0311));
        picker.setTopLineVisible(false);
        picker.setRangeStart(Calendar.getInstance().get(Calendar.YEAR) - 99, 1, 1);
        picker.setRangeEnd(Calendar.getInstance().get(Calendar.YEAR) - 10, 12, 31);
        picker.setSelectedItem(Calendar.getInstance().get(Calendar.YEAR) - 10, 12, 31);
        WheelView.LineConfig config1 = new WheelView.LineConfig();
        config1.setColor(0xFF33B5E5);//线颜色
        config1.setAlpha(140);//线透明度
        config1.setRatio((float) (1.0));//线比率
        picker.setLineConfig(config1);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener()
        {
            @Override
            public void onDatePicked(String year, String month, String day)
            {
                birth.setText(year + "-" + month + "-" + day);
                birth.setTextColor(ContextCompat.getColor(MakeUserInfo.this, R.color.c3));
            }
        });
        picker.show();
    }

    /**
     * 从相机获取图片
     */
    private void toTakePhotoFromCamera()
    {
        boolean isCameraPermissions = CommonUtils.cameraIsCanUse();
        if (isCameraPermissions)
        {
            File storageDir = this.getExternalCacheDir();
            File image = new File(storageDir, "TempImage.jpg");
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (openCameraIntent.resolveActivity(this.getPackageManager()) != null)
            {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
//                {
//                    //对于Android N 的处理  详情参考 http://blog.csdn.net/honjane/article/details/52057132  ---yqh
//                    ContentValues contentValues = new ContentValues(1);
//                    contentValues.put(MediaStore.Images.Media.DATA, image.getAbsolutePath());
//                    Uri hostUri = mAct.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
//                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, hostUri);
//                }else
//                {
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.echoesnet.eatandmeet.provider", image));
//                }
                //openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
//                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(openCameraIntent, EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA);
            }
        }
        else
        {
            ToastUtils.showShort("请打开相机功能");
        }
    }

    /**
     * 从相册获取图片
     */
    private void toPhotoFromGallery()
    {
            PhotoPicker.builder()
                    .setPhotoCount(1)
                    .setPreviewEnabled(true)
                    .setShowCamera(true)
                    .setShowGif(false)
                    .start(mAct, Crop.REQUEST_PICK);


    }


    private void chooseMakeUserPhoto()
    {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        headerView = inflater.inflate(R.layout.item_popupwindows, null);
        headerPopupWindow = new PopupWindow(headerView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        headerPopupWindow.setAnimationStyle(R.style.PopupAnimation);
        headerPopupWindow.setOutsideTouchable(true);
        headerPopupWindow.setTouchable(true);
        ll_popup = (AutoLinearLayout) headerView.findViewById(R.id.ll_popup);
        TextView itemPopupWindowsCamera = (TextView) headerView.findViewById(R.id.item_popupwindows_camera);
        TextView itemPopupWindowsPhoto = (TextView) headerView.findViewById(R.id.item_popupwindows_Photo);
        Button itemPopupWindowsCancel = (Button) headerView.findViewById(R.id.item_popupwindows_cancel);
        itemPopupWindowsCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toTakePhotoFromCamera();
                headerPopupWindow.dismiss();
            }
        });
        itemPopupWindowsPhoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toPhotoFromGallery();
                headerPopupWindow.dismiss();
            }
        });
        itemPopupWindowsCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                headerPopupWindow.dismiss();
            }
        });

        headerPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                backgroundAlpha(1f);
            }
        });
        backgroundAlpha(0.5f);
        headerPopupWindow.showAtLocation(MakeUserInfo.this.findViewById(R.id.all_parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);



    }
    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mAct.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mAct.getWindow().setAttributes(lp);
    }
    @Override
    public void onBackPressed()
    {
        //super.onBackPressed();
    }

    //验证输入
    private boolean verifyInput()
    {
        if (TextUtils.isEmpty(headImgUrl))
        {
            ToastUtils.showShort("请设置您的头像");
            return false;
        }
        else if (TextUtils.isEmpty(genderStr))
        {
            ToastUtils.showShort("请设置您的性别");
            return false;
        }
        else if (TextUtils.isEmpty(tvBirthday.getText()))
        {
            ToastUtils.showShort("请设置您的年龄");
            return false;
        }
        return true;
    }

    /**
     * 先注册环信，然后再完善资料
     *
     * @param usersBean 用户信息
     */
    private void registerHuanXin(final UsersBean usersBean)
    {
        if (pDialog != null && !pDialog.isShowing())
            pDialog.show();
        if (!TextUtils.isEmpty(usersBean.getImuId()))
        {
            if (mPresenter != null)
                mPresenter.inputUserInfo(usersBean, usersBean.getImuId(), usersBean.getImPass());
            return;
        }
        //region 注册环信
        final String userName = SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().toLowerCase().substring(0, 8);
        final String psw = UUID.randomUUID().toString().toLowerCase().substring(0, 6);
        usersBean.setImuId(userName);
        usersBean.setImPass(psw);
        IMHelper.getInstance().setOnIRegisterFinishedListener(new IMHelper.IHxRegisterFinishedListener()
        {
            @Override
            public void onSuccess(String userName, String psw)
            {
                if (mPresenter != null)
                    mPresenter.inputUserInfo(usersBean, userName, psw);
            }

            @Override
            public void onFailed(int errorCode)
            {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                if (errorCode == EMError.NETWORK_ERROR)
                {
                    ToastUtils.showShort("注册失败，请检查网络后重试");
                }
                else if (errorCode == EMError.USER_ALREADY_EXIST)
                {
                    String userName = SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().toLowerCase().substring(0, 8);
                    String psw = UUID.randomUUID().toString().toLowerCase().substring(0, 6);
                    usersBean.setImuId(userName);
                    usersBean.setImPass(psw);
                    IMHelper.getInstance().register(userName, psw);
                }
                else if (errorCode == EMError.USER_AUTHENTICATION_FAILED)
                {
                    //Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                }
                else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT)
                {
                    // Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (commitCount <= 3)
                    {
                        commitCount++;
                        IMHelper.getInstance().register(userName, psw);
//                        runOnUiThread(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                ToastUtils.showShort("注册失败，正在重试...");
//                            }
//                        });
                        Logger.t(TAG).d("注册失败，正在重试...");
                    }
                }
                //尝试3次注册仍然失败后，进入注册的下一步
                if (commitCount > 3)
                {
                    if (mPresenter != null)
                        mPresenter.inputUserInfo(usersBean, userName, psw);
                }
                Logger.t(TAG).d(String.format("注册失败,错误码为》%s", errorCode));
            }
        });
        IMHelper.getInstance().register(userName, psw);
    }

    //在 onResume() 前调用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        Logger.t(TAG).d(requestCode + "," + resultCode);
        switch (requestCode)
        {
            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK)
                {
                    File imgPath = new File(getExternalCacheDir() + "/" + "TempImage.jpg");
                    String tempFileName = "a_" + SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                    String outPutImagePath = NetHelper.getRootDirPath(mAct) + CommonUtils.toMD5(tempFileName);

                    ArrayList<String> mResults = result.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    Logger.t(TAG).d("图片地址：" + mResults.get(0));


                    File outFile = new File(outPutImagePath);
                    int degree = ImageUtils.readPictureDegree(mResults.get(0));
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.fromFile(new File(mResults.get(0))), mAct);
                    if (bitmap != null)
                    {
                        ImageUtils.compressBitmap(bitmap, outFile.getPath(), 150, degree);
                        Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                        beginCrop(Uri.fromFile(new File(mResults.get(0))));
                        boolean isDelete = imgPath.delete();
                        Logger.t(TAG).d("删除结果" + isDelete);
                    }
                    else
                    {
                        ToastUtils.showShort("选择图片失败，请重新选择");
                    }

                }
                break;
//            case Crop.REQUEST_CROP:
//                handleCrop(resultCode, result);
//                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
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
                        Log.d(TAG, "照片路径：" + imgPath.getAbsolutePath());
                        final File outFile = new File(outPutImagePath);

                        // zdw --- 添加针对三星拍照后旋转90显示问题
                        int degree = ImageUtils.readPictureDegree(imgPath.getAbsolutePath());
                        Bitmap bitmap = ImageUtils.getBitmapFromFile(mAct, imgPath.getAbsolutePath());
                        if (bitmap != null)
                        {
                            ImageUtils.compressBitmap(bitmap, outFile.getPath(), 130, degree);
                            Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);

                            beginCrop(FileProvider.getUriForFile(this, "com.echoesnet.eatandmeet.provider", outFile));
                            imgPath.delete();
                        }
                        else
                        {
                            ToastUtils.showShort("选择图片失败，请重新选择");
                        }

                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void beginCrop(Uri source)
    {
        String fileName = "a_" + SharePreUtils.getUserMobile(this) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        Logger.t(TAG).d(getCacheDir() + "/" + fileName);
//        Uri destination = Uri.fromFile(new File(getCacheDir(), CommonUtils.toMD5(fileName)));
//        Crop.of(source, destination).asSquare().withMaxSize(500, 500).start(this);

        CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .start(this);


    }

    private void handleCrop(int resultCode, Intent result)
    {
        if (resultCode == RESULT_OK)
        {
//            Uri uri = Crop.getOutput(result);
            CropImage.ActivityResult cropResult = CropImage.getActivityResult(result);
            uploadHeadImg(cropResult.getUri());
        }
        else if (resultCode == Crop.RESULT_ERROR)
        {
            String error=Crop.getError(result).getMessage();
            Logger.t(TAG).d("拍照返回错误信息--> " +error );
            if (TextUtils.isEmpty(error))
            {
                ToastUtils.showShort("照片裁剪失败");
            }
            else if (error.toLowerCase().contains("gif"))
            {
                ToastUtils.showShort("不能使用GIF图片");
            }
            else
            {
                ToastUtils.showShort(error);
            }
        }
    }

    private void uploadHeadImg(final Uri inputUri)
    {
        String fileKeyName = CdnHelper.userImage + SharePreUtils.getUserMobile(this) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        CdnHelper.getInstance().setOnCdnFeedbackListener(new CdnFeedbackListener(this, inputUri));
        if (pDialog != null && pDialog.isShowing())
            pDialog.show();
        rivHeadImg.setBackground(ContextCompat.getDrawable(mAct, R.drawable.shape_circle_c0412_bg));
        CdnHelper.getInstance().putFile(new File(inputUri.getPath()), "img", fileKeyName, 0);
    }

    private void addHxAccountToLocalAndLogin(final String hxUserName)
    {
        SharePreUtils.setHxId(mAct, hxUserName);
        IMHelper.getInstance().setOnILoginFinishListener(
                new HxLoginFinishedListener(MakeUserInfo.this));
        //登录环信
        IMHelper.getInstance().huanXinLogin(mAct);
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_detail:
                if (code.equals(ErrorCodeTable.USER_DETAILED))
                {
//                    ToastUtils.showShort("已完善资料");
                    //首次签到
                    try
                    {
                        JSONObject body = new JSONObject(errBody);
                        SharePreUtils.setFirst(mAct, body.getString("first"));
                        Intent intent = new Intent(mAct, HomeAct.class);
                        mAct.startActivity(intent);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    } finally
                    {
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();
                    }
                }
                else if (code.equals(ErrorCodeTable.SENSITIVE_NAME))
                {
                    if (fetNickName != null)
                        fetNickName.setText("");
                }
                Logger.t(TAG).d("错误码为：%s", code);
                break;
            default:
                break;
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getRegisterPresentCallback(String str)
    {
        tvMakeInfoMoney.setText(str);
    }

    @Override
    public void inputUserInfoCallback(ArrayMap<String, Object> str)
    {
        UsersBean usersBean = (UsersBean) str.get("usersBean");
        String response = (String) str.get("response");
        String userName = (String) str.get("userName");
        //String psw = (String) str.get("psw");
        try
        {

            JSONObject body = new JSONObject(response);
            SharePreUtils.setToken(mAct, body.getString("token"));
            SharePreUtils.setUId(mAct, body.getString("token"));//第一次，token就是uid
            SharePreUtils.setId(mAct, body.getString("id"));
            SharePreUtils.setHeadImg(mAct, usersBean.getUphUrl());
            SharePreUtils.setSex(mAct, usersBean.getSex());
            SharePreUtils.setAge(mAct, usersBean.getAge());
            //首次签到
            SharePreUtils.setFirst(mAct, body.getString("first"));

            if (TextUtils.isEmpty(usersBean.getNicName()))
            {
                StringBuffer buffer = new StringBuffer(SharePreUtils.getUserMobile(mAct));
                buffer.replace(3, 7, "****");
                SharePreUtils.setNicName(mAct, buffer.toString());
            }
            else
            {
                SharePreUtils.setNicName(mAct, usersBean.getNicName());
            }
            Logger.t(TAG).d("注册》1");
            //将环信账号保存到本地并登陆
            addHxAccountToLocalAndLogin(userName);
            Logger.t(TAG).d("注册》2");
            //mPresenter.addHXAccountToServerAndLogin(userName, psw, jsonResponse);
            addTXAccountToLocalAndLogin(body.getString("name"), body.getString("sign"));

            Logger.t(TAG).d("注册》3");
            jumpToHomePage(response, fetInviteCode.getText().toString());

        } catch (JSONException e)
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        } catch (Exception e)
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            Logger.t(TAG).d(e.getMessage());
            e.printStackTrace();
        }
    }


    // FIXME: 2017/4/25 测试完成后可以删除
    @Override
    public void addHXAccountToServerAndLoginCallback(ArrayMap<String, Object> map)
    {
/*        try
        {
            String response = (String) map.get("response");
            JSONObject jsonResponse = (JSONObject) map.get("jsonResponse");
            String userName = (String) map.get("userName");
            String psw = (String) map.get("psw");
            Logger.t(TAG).d("保存环信账号返回--> " + response);

            JSONObject jsonObject = new JSONObject(response);
            String messageJson = jsonObject.getString("messageJson");
            JSONObject result = new JSONObject(messageJson);
            int status = result.getInt("status");
            if (status == 0)
            {
                IMHelper.getInstance().setOnILoginFinishListener(
                        new HxLoginFinishedListener(MakeUserInfo.this, jsonResponse, fetInviteCode.getText().toString()));
                //登录环信
                IMHelper.getInstance().huanXinLogin((Activity) mAct, pDialog);
                SharePreUtils.setHxId(mAct, userName);
            }
            else
            {
                String code = jsonObject.getString("code");
                if (!ErrorCodeTable.handleErrorCode(code, mAct))
                    ToastUtils.showShort(mAct, ErrorCodeTable.parseErrorCode(code));
                Logger.t(TAG).d("错误码为：%s", code);
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("环信账户创建异常：" + e.getMessage());
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }*/
    }

    /**
     * 保存腾讯账号到本地，并登录
     *
     * @param tUserName
     * @param tUserSign
     */
    private void addTXAccountToLocalAndLogin(final String tUserName, String tUserSign)
    {
        //腾讯用户名和签名
        SharePreUtils.setTlsName(mAct, tUserName);
        SharePreUtils.setTlsSign(mAct, tUserSign);
        //腾讯IM登录
        TencentHelper.txLogin(null);
    }

    private void jumpToHomePage(String response, String editText)
    {
        Logger.t(TAG).d("开始跳转》" + response.toString() + " editText " + editText);
        try
        {
            if (mAct != null)
            {
                Logger.t(TAG).d("登录成功");
                SharePreUtils.setShowPop(mAct, true);
                Logger.t(TAG).d("保存审核弹窗显示状态>>" + SharePreUtils.getShowPop(mAct));
                JSONObject body = new JSONObject(response);
                Intent intent = new Intent(mAct, HomeAct.class);

                if (TextUtils.isEmpty(editText))
                {
                    intent.putExtra("isInvited", "false");
                    intent.putExtra("regReward", body.getString("regReward"));
                }
                else
                {
                    intent.putExtra("isInvited", "true");
                    intent.putExtra("regReward", body.getString("regReward"));
                    intent.putExtra("inviteReward", body.getString("inviteReward"));
                }
                Logger.t(TAG).d("开始跳转");
                mAct.startActivity(intent);
            }
        } catch (JSONException e)
        {
            Logger.t(TAG).d("错误" + e.getMessage());
        }

    }


    private static class CdnFeedbackListener implements IOnCdnFeedbackListener
    {
        private final WeakReference<MakeUserInfo> mActRef;
        private Uri imgUri;

        private CdnFeedbackListener(MakeUserInfo mAct, Uri imgUri)
        {
            this.mActRef = new WeakReference<MakeUserInfo>(mAct);
            this.imgUri = imgUri;
        }

        @Override
        public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
        {
            final MakeUserInfo cAct = mActRef.get();
            if (cAct != null)
            {
                cAct.tvUploadProgress.setVisibility(View.GONE);
                Logger.t(TAG).d("成功：" + response.toString());
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(imgUri)
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(cAct.rivHeadImg);
                cAct.headImgUrl = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                if (cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
            }
        }

        @Override
        public void onProcess(long len)
        {
            MakeUserInfo cAct = mActRef.get();
            cAct.itvAddIcon.setVisibility(View.GONE);
            cAct.tvUploadProgress.setText((int) len + "%");

        }

        @Override
        public void onFail(JSONObject response, File file)
        {
            final MakeUserInfo cAct = mActRef.get();
            if (cAct != null)
            {
                if (cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
                Logger.t(cAct.TAG).d("错误：" + response.toString());
            }
        }
    }

    private static class HxLoginFinishedListener implements IMHelper.IHxLoginFinishedListener
    {
        private WeakReference<MakeUserInfo> mActRef;

        private HxLoginFinishedListener(MakeUserInfo mAct)
        {
            this.mActRef = new WeakReference<MakeUserInfo>(mAct);
        }

        @Override
        public void onSuccess()
        {
            MakeUserInfo cAct = mActRef.get();
            if (cAct != null)
            {
                if (cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
                cAct.finish();
            }
        }

        @Override
        public void onFailed(int i, String s)
        {
            final MakeUserInfo cAct = mActRef.get();
            if (cAct != null)
            {
                Logger.t(cAct.TAG).d(s);
                if (cAct.pDialog != null && cAct.pDialog.isShowing())
                    cAct.pDialog.dismiss();
                cAct.finish();
            }
            // ToastUtils.showShort(mAct, "H登录失败：" + s);
        }
    }

    @Override
    protected ImpIMakeUserInfoView createPresenter()
    {
        return new ImpIMakeUserInfoView();
    }

    private PopupWindow popupWindow;

    private PopupWindow showPopupReview(int type, int layoutId)
    {
        View contentView = LayoutInflater.from(mAct).inflate(layoutId, null);
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.showAtLocation(findViewById(R.id.all_parent), Gravity.CENTER | Gravity.CENTER, 0, 0);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        contentView.setFocusable(true);
        contentView.setFocusableInTouchMode(true);
        Button leftBtn = (Button) contentView.findViewById(R.id.review_left_btn);
        Button rightBtn = (Button) contentView.findViewById(R.id.review_right_btn);
        Button singleBtn = (Button) contentView.findViewById(R.id.review_single_btn);
        if (type == 0)
        {
            leftBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            });
            rightBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    popupWindow.dismiss();
                    popupWindow = null;
                    chooseMakeUserPhoto();
                }
            });
        }
        else
        {
            singleBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            });
        }

        contentView.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    if (popupWindow != null)
                    {
                        popupWindow.dismiss();
                        popupWindow = null;
                    }
                }
                return false;
            }
        });
        return popupWindow;
    }
}
