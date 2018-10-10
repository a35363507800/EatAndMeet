package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.models.bean.UserEditInfoBean;
import com.echoesnet.eatandmeet.presenters.ImpMyInfoEditView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoEditView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.IMUtils.IMHelper;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnDeletebackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MEditPhotoListAdapter;
import com.echoesnet.eatandmeet.views.adapters.UserLabelAdapter;
import com.echoesnet.eatandmeet.views.widgets.CityWheelPicker.CityWheelPicker;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.MaxByteLengthEditText;
import com.echoesnet.eatandmeet.views.widgets.MyGridView;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.NumberPicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import cn.qqtheme.framework.widget.WheelView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;


/**
 * 重构图片上传功能  by ben 2017/2/23
 */
public class MyInfoEditAct extends MVPBaseActivity<MyInfoEditAct, ImpMyInfoEditView> implements IMyInfoEditView
{
    private static final String TAG = MyInfoEditAct.class.getSimpleName();
    //region 变量
    private List<String> heightOther, states, constellations, eductions, eductionsOther, incomes, incomesOther, jobs, jobsOther;
    @BindView(R.id.noScrollgridview)
    MyGridView noScrollgridview;// 用户图片列表
    @BindView(R.id.mgv_label)
    MyGridView mgvLabel;// 标签列表
    @BindView(R.id.arl_brithday)  // 生日
            RelativeLayout arlBrithday;
    @BindView(R.id.et_name) // 昵称
            MaxByteLengthEditText etName;
    @BindView(R.id.tv_height) // 身高
            TextView tvHeight;
    @BindView(R.id.tv_constellation) // 星座
            TextView tvConstellation;
    @BindView(R.id.tv_state) // 感情状态
            TextView tvState;
    @BindView(R.id.tv_brithday) // 生日
            TextView tvBrithday;
    @BindView(R.id.tv_education) // 学历
            TextView tvEducation;
    @BindView(R.id.tv_city) // 城市
            TextView tvCity;
    @BindView(R.id.tv_work) // 职业
            TextView tvWork;
    @BindView(R.id.tv_income) // 月收入
            TextView tvIncome;
    @BindView(R.id.tv_sign) // 个性签名
            TextView tvSign;
    // 择偶TextViews
    @BindView(R.id.tv_work_other) // 职业
            TextView tvWorkOther;
    @BindView(R.id.tv_income_other) // 月收入
            TextView tvIncomeOther;
    @BindView(R.id.tv_education_other) // 学历
            TextView tvEducationOther;
    @BindView(R.id.tv_height_other) // 身高
            TextView tvHeightOther;
    @BindView(R.id.tv_city_other) // 所在城市
            TextView tvCityOther;
    @BindView(R.id.tv_want_to_go) // 最近要去
            TextView tvWantToGo;
    @BindView(R.id.tv_identity) // 认证状态
            TextView tvIdentity;
    @BindView(R.id.riv_head)// 头像
            RoundedImageView rivHead;
    @BindView(R.id.top_bar_switch)
    TopBarSwitch topBar;
    @BindView(R.id.act_myinfo_layout)
    LinearLayout glayout;

    private Activity mContext;
    // 标签设置
    private UserLabelAdapter userLabelAdapter;
    private String labelResult = ""; // 标签的最终信息
    private ArrayList<String> listLab = new ArrayList<>();
    private static final int PRESON_LABEL = 0x000003;
    private static final int UWANT_TO_GO = 0x000004;
    private static final int USER_SIGN = 0x000005;
    private int intentWhere = 0;  //跳转逻辑 0选渠道 1审核进度
    private String headerPath;
    //画册数据源的Url
    private List<String> imgLst;
    //存放已经上传到网络上的图片
    private List<String> remoteImgLst = new ArrayList<>();
    private MEditPhotoListAdapter mEditPhotoListAdapter;
    public static final String addImg = "android.resource://com.echoesnet.eatandmeet/drawable/wode_addcz_edit2";
    private Dialog pDialog;
    private String goTime;
    private String goResId;
    private UserEditInfoBean usersBean;
    private Observable observable;
    private CityWheelPicker mCityWheelPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_myinfo_edit);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
        if (observable != null)
            observable.unsubscribeOn(Schedulers.io());
    }

    @Override
    protected ImpMyInfoEditView createPresenter()
    {
        return new ImpMyInfoEditView();
    }

    private void initAfterViews()
    {
        mContext = this;
        initData();
        //给文本框限制字节
        etName.setMaxByteLength(14);

        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                mContext.finish();
            }

            @Override
            public void right2Click(View view)
            {
                if (TextUtils.isEmpty(etName.getText().toString().trim()))
                {
                    new CustomAlertDialog(MyInfoEditAct.this)
                            .builder()
                            .setTitle("提示")
                            .setMsg("昵称不能为空!")
                            .setNegativeButton("确定", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                }
                            }).show();
                }
                else
                {
                    UserEditInfoBean bean = new UserEditInfoBean();
                    bean.setMobile(SharePreUtils.getUserMobile(mContext));  // 手机号
                    //设置去除转义字符后的昵称
                    bean.setNicName(CommonUtils.removeBlank(etName.getText().toString()));      // 昵称
                    bean.setHeight(tvHeight.getText().toString().replace("请选择", "保密"));
                    bean.setConstellation(tvConstellation.getText().toString().replace("请选择", "保密"));
                    bean.setEmState(tvState.getText().toString().replace("请选择", "保密"));         // 情感状态
                    bean.setEducation(tvEducation.getText().toString().replace("请选择", "保密"));   // 学历
                    bean.setCity(tvCity.getText().toString().replace("请选择", "天津市"));             // 城市
                    bean.setOccupation(tvWork.getText().toString().replace("请选择", "保密"));       // 职业
                    bean.setIncome(tvIncome.getText().toString().replace("请选择", "保密"));         // 收入
                    bean.setWhitherName(TextUtils.isEmpty(tvWantToGo.getText()) ? "" : tvWantToGo.getText().toString());
                    bean.setWhitherTime(goTime);//最近想去
                    bean.setWhitherId(goResId);
                    bean.setBirth(TextUtils.isEmpty(tvBrithday.getText().toString()) ? "1990-01-09" : tvBrithday.getText().toString());// 生日
                    bean.setSignature(TextUtils.isEmpty(SharePreUtils.getUserSign(mContext)) ? "这家伙很懒，什么都没有留下哦~" : SharePreUtils.getUserSign(mContext));   // 个性签名
                    bean.setuLab(labelResult);                              // 用户标签
                    bean.setCmOccupation(tvWorkOther.getText().toString().replace("请选择", "保密"));  // 职业
                    bean.setCmIncome(tvIncomeOther.getText().toString().replace("请选择", "保密"));    // 收入
                    bean.setCmEducation(tvEducationOther.getText().toString().replace("请选择", "保密")); // 学历
                    bean.setCmHeight(tvHeightOther.getText().toString().replace("请选择", "保密"));    // 身高
                    bean.setCmCity(tvCityOther.getText().toString());        // 城市
                    bean.setUphUrl(headerPath);

                    if (mPresenter != null)
                    {
                        List<String> galleryImgUrlsNoAdd = new ArrayList<String>(imgLst);
                        if (galleryImgUrlsNoAdd.contains(addImg))
                            galleryImgUrlsNoAdd.remove(addImg);
                        if (pDialog != null && !pDialog.isShowing())
                            pDialog.show();
                        mPresenter.postUserProfileInfo(galleryImgUrlsNoAdd, remoteImgLst, bean);
                    }
                    //百度统计
//                    StatService.onEvent(mContext, "edit_user_info", getString(R.string.baidu_other), 1);
                }
            }
        }).setText("编辑资料");

        List<TextView> navBtns = topBar.getNavBtns(new int[]{1, 0, 0, 1});
        navBtns.get(1).setText("提交");
        navBtns.get(1).setTextSize(16);

        pDialog = DialogUtil.getCommonDialog(mContext, "正在加载...");
        pDialog.setCancelable(false);

        // 进入该页面调用用户信息接口 3.1.14(接口序号)
        if (mPresenter != null)
        {
            if (pDialog != null && !pDialog.isShowing())
                pDialog.show();
            mPresenter.getUserInfo();
        }

        imgLst = new ArrayList<>();
        mEditPhotoListAdapter = new MEditPhotoListAdapter(mContext, imgLst, new MEditPhotoListAdapter.OnClick()
        {
            @Override
            public void OnClick(int position)
            {
                deleteSelectImage(position);
            }
        });
        noScrollgridview.setAdapter(mEditPhotoListAdapter);
        // 相册列表添加及查看动作
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Logger.t(TAG).d("点击位置：" + position);
                if (imgLst.get(position).equals(addImg))
                {
                    int maxImg = 6 - imgLst.size() + 1;
                    PhotoPicker.builder()
                            .setPhotoCount(maxImg)
                            .setPreviewEnabled(true)
                            .setShowCamera(true)
                            .setShowGif(false)
                            .start(mContext, EamConstant.EAM_OPEN_IMAGE_PICKER);
                    //ImageUtils.openImagePicker(mContext, maxImg, 100 * 1024, true);
                }
                else
                {
                    // 相册列表编辑动作
                    final ArrayList<String> finalImgLst = new ArrayList<String>(imgLst);
                    Logger.t(TAG).d("长按位置：" + position + " , " + finalImgLst.size());
                    int imgLength = finalImgLst.size();
                    if (finalImgLst.contains(addImg))
                    {
                        Logger.t(TAG).d("包含addImg");
                        finalImgLst.remove(imgLength - 1);
                    }
                    else
                    {
                        Logger.t(TAG).d("不包含addImg");
                    }
                    CommonUtils.showImageBrowser(mContext, finalImgLst, position, view);
                    // showContextMenuBox(finalImgLst, position);
                }
            }
        });

        userLabelAdapter = new UserLabelAdapter(this, listLab);
        mgvLabel.setAdapter(userLabelAdapter);
        mgvLabel.setEnabled(false);
        mgvLabel.setClickable(false);
    }

    private void initData()
    {
        heightOther = Arrays.asList(getResources().getStringArray(R.array.myinf_lover_height));
        states = Arrays.asList(getResources().getStringArray(R.array.myinf_emotion_state));
        constellations = Arrays.asList(getResources().getStringArray(R.array.myinfo_xinzuo));
        eductions = Arrays.asList(getResources().getStringArray(R.array.myinfo_education_level));
        eductionsOther = Arrays.asList(getResources().getStringArray(R.array.myinfo_lover_education));
        incomes = Arrays.asList(getResources().getStringArray(R.array.myinfo_income));
        incomesOther = Arrays.asList(getResources().getStringArray(R.array.myinfo_lover_income));
        jobs = Arrays.asList(getResources().getStringArray(R.array.myinfo_job));
        jobsOther = new ArrayList<>(jobs);
        jobsOther.add(0, "不限");
        etName.setSelection(etName.length());
    }

    /**
     * 显示操作图片上下文菜单
     *
     * @param operUrls 要操作的图片url集合
     * @param position 点击的当前图片位置
     * @return
     */
    private Dialog showContextMenuBox(final ArrayList<String> operUrls, final int position)
    {
        final Dialog dialog = new Dialog(mContext, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.dialog_img_operation, null);
        dialog.setContentView(contentView);
        TextView tvCheckImg = (TextView) contentView.findViewById(R.id.tv_check_img);
        TextView tvDeleteImg = (TextView) contentView.findViewById(R.id.tv_delete_img);
        tvCheckImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtils.showImageBrowser(mContext, operUrls, position, v);
                dialog.dismiss();
            }
        });
        tvDeleteImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                deleteSelectImage(position);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = CommonUtils.dp2px(mContext, 250);
        //lp.width= (int) (CommonUtils.getScreenSize(mContext).width*0.85f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

    private void deleteSelectImage(int position)
    {
        imgLst.remove(position);
        if (position < remoteImgLst.size())
        {
            remoteImgLst.remove(position);
        }
        //当删除到第5张时出现添加按钮
        if (imgLst.size() == 5)
        {
            if (!imgLst.contains(addImg))
                imgLst.add(addImg);
        }
        mEditPhotoListAdapter.notifyDataSetChanged();
    }

    /**
     * 以备将来使用，暂时留
     *
     * @param imgUrls
     * @param position
     */
    private void deleteMyInfoImgs(final List<String> imgUrls, final int position)
    {
        CdnHelper.getInstance().setOnCdnDeletebackListener(new IOnCdnDeletebackListener()
        {
            @Override
            public void onSuccess(JSONObject response, String fileKeyName)
            {
                Logger.t(TAG).d("服务器上的图片数量22：" + remoteImgLst.size());
            }

            @Override
            public void onProcess(long len)
            {

            }

            @Override
            public void onFail(JSONObject response)
            {
                Logger.t(TAG).d("========>delete failed" + response.toString());
            }
        });
        for (int i = 0; i < imgUrls.size() - 1; i++)
        {
            File file = new File(imgUrls.get(i));
            String fileKeyName = file.getName();
            CdnHelper.getInstance().deleteFile(fileKeyName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        Logger.t(TAG).d(requestCode + "," + resultCode);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_IMAGE_PICKER:
                if (resultCode == RESULT_OK)
                {
                    ArrayList<String> mResults = result.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    assert mResults != null;
                    imgLst.addAll(imgLst.size() - 1, mResults);
                    if (imgLst.size() > 6)
                    {
                        imgLst.remove(imgLst.size() - 1);
                    }
                    mEditPhotoListAdapter.notifyDataSetChanged();
                    for (String str : mResults)
                    {
                        Logger.t(TAG).d(str);
                    }
                }
                break;
            case PRESON_LABEL:
                Logger.t(TAG).d("标签返回");
                listLab.clear();
                listLab.addAll(result.getStringArrayListExtra("temp"));
                for (int i = 0; i < listLab.size(); i++)
                {
                    Logger.t(TAG).d("ceshik--> " + listLab.get(i).toString());
                }

                if (listLab == null || listLab.size() == 0)
                {
                    Logger.t(TAG).d("标签返回为空");
                }
                else
                {
                    labelResult = CommonUtils.listToStrWishSeparator(listLab, CommonUtils.SEPARATOR);
                    Logger.t(TAG).d("标签信息--> " + labelResult);
                }
                userLabelAdapter.notifyDataSetChanged();

                break;
            case EamConstant.EAM_TAKE_PHOTO_FROM_GALLERY:
                if (resultCode == RESULT_OK)
                {
//
                    ArrayList<String> mResults = result.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    Logger.t(TAG).d("图片地址：" + mResults.get(0));
                    File imgPath = new File(mResults.get(0));

                    String tempFileName = "a_" + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                    String outPutImagePath = NetHelper.getRootDirPath(mContext) + CommonUtils.toMD5(tempFileName);
                    File outFile = new File(outPutImagePath);

                    // int degree = ImageUtils.readPictureDegree(result.getData().getPath().replace("/raw", ""));
                    int degree = ImageUtils.readPictureDegree(mResults.get(0));
                    //         Logger.t(TAG).d("旋转角度为》" + degree + " , " + result.getData().getPath());

                    Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.fromFile(imgPath), mContext);
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
//            case Crop.REQUEST_CROP:
//                handleCrop(resultCode, result);
//                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                handleCrop(resultCode, result);
                break;
            case EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA:  // 头像
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        File imgPath = new File(getExternalCacheDir() + "/" + "TempImage.jpg");
                        String tempFileName = "a_" + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                        String outPutImagePath = NetHelper.getRootDirPath(mContext) + CommonUtils.toMD5(tempFileName);
                        // Uri uri1 = Uri.parse("file://" + "/" + imgPath.getAbsolutePath());
                        Logger.t(TAG).d("照片路径：" + imgPath.getAbsolutePath());
                        File outFile = new File(outPutImagePath);

                        int degree = ImageUtils.readPictureDegree(imgPath.getAbsolutePath());
                        Bitmap bitmap = ImageUtils.getBitmapFromFile(mContext, imgPath.getAbsolutePath());
                        if (bitmap != null)
                        {
                            ImageUtils.compressBitmap(bitmap, outFile.getPath(), 130, degree);
                            Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                            beginCrop(Uri.fromFile(outFile));
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
            //获取签名
            case USER_SIGN:
                if (resultCode == RESULT_OK)
                {
                    if (!TextUtils.isEmpty(result.getStringExtra("sign")))
                    {
                        tvSign.setText(result.getStringExtra("sign"));
                    }
                    else
                    {
                        tvSign.setText("");
                        tvSign.setHint("这家伙很懒，什么都没有留下哦~");
                    }
                }
                break;
            case UWANT_TO_GO:
                if (resultCode == RESULT_OK)
                {
                    tvWantToGo.setText(result.getStringExtra("goResName"));
                    this.goTime = result.getStringExtra("goTime");
                    this.goResId = result.getStringExtra("goResId");
                }
                break;
        }
    }

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
//        Uri destination = Uri.fromFile(new File(getCacheDir(), CommonUtils.toMD5(fileName)));

        CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1)
                .start(this);

//        Crop.of(source, destination).asSquare().withMaxSize(500, 500).start(this);

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
//            Uri uri = Crop.getOutput(result);
            CropImage.ActivityResult cropResult = CropImage.getActivityResult(result);
            Logger.t(TAG).d("handleCrop");
            updateHeadImg(cropResult.getUri());
        }
        else if (resultCode == Crop.RESULT_ERROR)
        {
            ToastUtils.showShort(Crop.getError(result).getMessage());
        }
    }

    @OnClick({R.id.arl_sign, R.id.arl_lable, R.id.arl_height, R.id.arl_state, R.id.arl_constellation
            , R.id.arl_education, R.id.arl_city, R.id.arl_work, R.id.arl_income, R.id.arl_want_to_go, R.id.arl_work_other, R.id.arl_income_other
            , R.id.arl_education_other, R.id.arl_height_other, R.id.arl_city_other, R.id.arl_brithday, R.id.arl_identity, R.id.et_name, R.id.arl_header})
    void onViewClick(View v)
    {

        if (v.getId() != R.id.et_name)
            etName.clearFocus();

        switch (v.getId())
        {
            case R.id.arl_sign:
                Intent i = new Intent(this, CPersonSignatureAct.class);
                startActivityForResult(i, USER_SIGN);
                break;
            case R.id.arl_lable:
                Intent intentLabel = new Intent(this, MPersonLabelAct.class);
                intentLabel.putStringArrayListExtra("lab", listLab);
                startActivityForResult(intentLabel, PRESON_LABEL);
                break;
            case R.id.arl_header: // 设置头像
                chooseHeadPic();
                break;
            case R.id.arl_height:   // 身高
                chooseHeight();
                break;
            case R.id.arl_constellation: // 星座
                chooseConstellation();
                break;
            case R.id.arl_state:  // 感情状态
                String[] stateArr = states.toArray(new String[states.size()]);
                OptionPicker statePicker = new OptionPicker(this, stateArr);
                statePicker.setSelectedIndex(getItem(tvState, states));
                setOptionPicker(statePicker, tvState);
                break;
            case R.id.arl_education:  // 学历
                String[] educations = eductions.toArray(new String[eductions.size()]);
                OptionPicker educationPicker = new OptionPicker(this, educations);
                educationPicker.setSelectedIndex(getItem(tvEducation, eductions));
                setOptionPicker(educationPicker, tvEducation);
                break;
            case R.id.arl_city:  // 城市
                setCityOption(tvCity);
                break;
            case R.id.arl_work:  // 职业
                String[] works = jobs.toArray(new String[jobs.size()]);
                OptionPicker workPicker = new OptionPicker(this, works);
                workPicker.setSelectedIndex(getItem(tvWork, jobs));
                setOptionPicker(workPicker, tvWork);
                break;
            case R.id.arl_income:  // 月收入
                String[] incomeArr = incomes.toArray(new String[incomes.size()]);
                OptionPicker incomePicker = new OptionPicker(this, incomeArr);
                incomePicker.setSelectedIndex(getItem(tvIncome, incomes));
                setOptionPicker(incomePicker, tvIncome);
                break;
            case R.id.arl_want_to_go:
                Intent intent = new Intent(mContext, UserWantToGoEditAct.class);
                if (usersBean != null && TextUtils.isEmpty(this.goTime))
                {
                    intent.putExtra("resName", usersBean.getWhitherName());
                    intent.putExtra("resTime", usersBean.getWhitherTime());
                }
                if (!TextUtils.isEmpty(tvWantToGo.getText()) && !TextUtils.isEmpty(this.goTime))
                {
                    intent.putExtra("resName", usersBean.getWhitherName());
                    intent.putExtra("resTime", usersBean.getWhitherTime());
                }
                startActivityForResult(intent, UWANT_TO_GO);
                break;

            case R.id.arl_work_other:
                String[] otherWorks = jobsOther.toArray(new String[jobsOther.size()]);
                OptionPicker otherWorkPicker = new OptionPicker(this, otherWorks);
                otherWorkPicker.setSelectedIndex(getItem(tvWorkOther, jobsOther));
                setOptionPicker(otherWorkPicker, tvWorkOther);
                break;
            case R.id.arl_income_other:
                String[] otherIncomeArr = incomesOther.toArray(new String[incomesOther.size()]);
                OptionPicker otherIncomePicker = new OptionPicker(this, otherIncomeArr);
                otherIncomePicker.setSelectedIndex(getItem(tvIncomeOther, incomesOther));
                setOptionPicker(otherIncomePicker, tvIncomeOther);
                break;
            case R.id.arl_education_other:
                String[] otherEducations = eductionsOther.toArray(new String[eductionsOther.size()]);
                OptionPicker otherEducationPicker = new OptionPicker(this, otherEducations);
                otherEducationPicker.setSelectedIndex(getItem(tvEducationOther, eductionsOther));
                setOptionPicker(otherEducationPicker, tvEducationOther);
                break;
            case R.id.arl_height_other:
                String[] otherHeights = heightOther.toArray(new String[heightOther.size()]);
                OptionPicker otherHeightPicker = new OptionPicker(this, otherHeights);
                otherHeightPicker.setSelectedIndex(getItem(tvHeightOther, heightOther));
                setOptionPicker(otherHeightPicker, tvHeightOther);
                break;
            case R.id.arl_city_other:
                setCityOption(tvCityOther);
                break;
            case R.id.arl_brithday:  // 生日
                setNewBirthdayPicker(tvBrithday);
                break;
            case R.id.arl_identity:  // 身份认证
                Intent identityIntent = null;
                if (intentWhere == 1)
                    identityIntent = new Intent(mContext, ApproveProgressAct.class);
                else if (intentWhere == 0)
                    identityIntent = new Intent(mContext, IdentityAuthAct.class);

                startActivity(identityIntent);
                break;

        }
    }

    private int getItem(TextView veiw, List listText)
    {

        for (int i = 0; i < listText.size(); i++)
        {
            if (veiw.getText().toString().equals(listText.get(i)))
                return i;
        }
        return 2;
    }

    /**
     * 头像选择器
     */
    private void chooseHeadPic()
    {
        OptionPicker headPicker = new OptionPicker(this, new String[]{"拍照", "从相册中选择"});
        headPicker.setShadowVisible(false);
        headPicker.setDividerVisible(true);
        headPicker.setTopLineHeight(1);
        headPicker.setTopLineColor(0xFF33B5E5);
        headPicker.setCancelTextColor(0xFF33B5E5);
        headPicker.setCancelTextSize(13);
        headPicker.setSubmitTextColor(0xFF33B5E5);
        headPicker.setSubmitTextSize(13);
        headPicker.setTextSize(14);
        headPicker.setOffset(2);
        headPicker.setCycleDisable(true);
        headPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener()
        {
            @Override
            public void onOptionPicked(int index, String item)
            {
                if (item.equals("拍照"))
                {
                    toTakePhotoFromCamera();
                }
                else
                {
                    toPhotoFromImageAblum();
                }
            }
        });
        headPicker.show();
    }

    /**
     * 身高选择器
     */
    private void chooseHeight()
    {
        int itemHeight = 140;
        if (tvHeight.getText().toString().trim().contains("cm"))
        {
            String heightText = tvHeight.getText().toString().trim();
            itemHeight = Integer.parseInt(heightText.substring(0, heightText.indexOf("c")));
        }
        NumberPicker picker = new NumberPicker(this);
        picker.setCycleDisable(true);
        picker.setDividerVisible(true);
        picker.setShadowVisible(false);
        picker.setTopLineVisible(false);
        picker.setTopLineHeight(1);
        // picker.setTopLineColor(0xFF33B5E5);
        picker.setCancelTextColor(0xFF33B5E5);
        picker.setCancelTextSize(13);
        picker.setSubmitTextColor(0xFF33B5E5);
        picker.setSubmitTextSize(13);
        picker.setTextSize(14);
        picker.setOffset(2); //偏移量
        picker.setRange(140, 200, 1); //数字范围
        picker.setSelectedItem(itemHeight);
//                picker.setItemWidth(200);
        picker.setLabel("cm");
        WheelView.DividerConfig config1 = new WheelView.DividerConfig();
        config1.setColor(0xFF33B5E5);//线颜色
        config1.setAlpha(140);//线透明度
        config1.setRatio((float) (1.0 / 8.0));//线比率
        picker.setDividerConfig(config1);
        picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener()
        {
            @Override
            public void onNumberPicked(int index, Number item)
            {
                tvHeight.setText(String.valueOf(item.intValue()) + "cm");
            }
        });
        picker.show();
    }

    /**
     * 星座选择器
     */
    private void chooseConstellation()
    {
        String[] constellationArr = constellations.toArray(new String[constellations.size()]);

        OptionPicker optionPicker = new OptionPicker(this, constellationArr);
        optionPicker.setCycleDisable(true);//不禁用循环
        optionPicker.setTopBackgroundColor(0xFFEEEEEE);
        optionPicker.setTopLineVisible(false);
        optionPicker.setTopHeight(50);
        optionPicker.setTopLineColor(0xFF33B5E5);
        optionPicker.setTopLineHeight(1);
        optionPicker.setTitleTextColor(0xFF999999);
        optionPicker.setTitleTextSize(12);
        optionPicker.setCancelTextColor(0xFF33B5E5);
        optionPicker.setCancelTextSize(13);
        optionPicker.setSubmitTextColor(0xFF33B5E5);
        optionPicker.setSubmitTextSize(13);
        optionPicker.setTextColor(0xFF33B5E5, 0xFF999999);
        cn.qqtheme.framework.widget.WheelView.LineConfig config = new cn.qqtheme.framework.widget.WheelView.LineConfig();
        config.setColor(0xFF33B5E5);//线颜色
        config.setAlpha(140);//线透明度
        config.setRatio((float) (1.0 / 100.0));//线比率
        optionPicker.setLineConfig(config);
        optionPicker.setItemWidth(200);
        // optionPicker.setBackgroundColor(0xFFE1E1E1);
        optionPicker.setSelectedIndex(getItem(tvConstellation, constellations));
        optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener()
        {
            @Override
            public void onOptionPicked(int index, String item)
            {
                tvConstellation.setText(item);
            }
        });
        optionPicker.show();
    }

    /**
     * 选项选择器
     *
     * @param optionPicker
     * @param textView
     */
    private void setOptionPicker(OptionPicker optionPicker, final TextView textView)
    {
        optionPicker.setCycleDisable(true);
        optionPicker.setLineVisible(true);
        optionPicker.setShadowVisible(false);
        optionPicker.setTopLineVisible(false);
        optionPicker.setTopLineHeight(1);
        cn.qqtheme.framework.widget.WheelView.LineConfig config = new cn.qqtheme.framework.widget.WheelView.LineConfig();
        config.setColor(0xFF33B5E5);//线颜色
        config.setAlpha(140);//线透明度
        config.setRatio((float) (1.0 / 100.0));//线比率
        optionPicker.setLineConfig(config);
        //  optionPicker.setTopLineColor(0xFF33B5E5);
        optionPicker.setCancelTextColor(0xFF33B5E5);
        optionPicker.setCancelTextSize(13);
        optionPicker.setSubmitTextColor(0xFF33B5E5);
        optionPicker.setSubmitTextSize(13);
        optionPicker.setItemWidth(200);
        optionPicker.setTextSize(14);
        optionPicker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener()
        {
            @Override
            public void onOptionPicked(int index, String item)
            {
                textView.setText(item);
            }
        });
        optionPicker.show();
    }

    /**
     * 生日选择器
     */
    private void setNewBirthdayPicker(final TextView birth)
    {
        int year = Calendar.getInstance().get(Calendar.YEAR) - 99;
        int month = 3;
        int day = 3;

        String[] text = birth.getText().toString().split("-");
        if (text.length == 3)
        {

            try
            {
                if (Integer.parseInt(text[0]) >= year)
                    year = Integer.parseInt(text[0]);

                if (Integer.parseInt(text[0]) > Calendar.getInstance().get(Calendar.YEAR) - 10)
                    year = Calendar.getInstance().get(Calendar.YEAR) - 10;

                month = Integer.parseInt(text[1]);
                day = Integer.parseInt(text[2]);
            } catch (NumberFormatException e)
            {
                e.printStackTrace();
            }
        }

        DatePicker picker = new DatePicker(mContext, DateTimePicker.YEAR_MONTH_DAY);
        picker.setCycleDisable(true);
        picker.setDividerVisible(true);
        picker.setTopLineVisible(false);
        picker.setShadowVisible(false);
        picker.setTitleText("选择生日");
        picker.setTitleTextSize(14);
        picker.setTitleTextColor(ContextCompat.getColor(mContext, R.color.C0321));
        picker.setTitleTextColor(ContextCompat.getColor(mContext, R.color.C0311));
        picker.setTitleTextColor(ContextCompat.getColor(mContext, R.color.C0311));
        picker.setTopLineVisible(false);
        picker.setRangeStart(Calendar.getInstance().get(Calendar.YEAR) - 99, 1, 1);
        picker.setRangeEnd(Calendar.getInstance().get(Calendar.YEAR) - 10, Calendar.getInstance().get(Calendar.MONTH) + 1, Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        picker.setSelectedItem(year, month, day);
        WheelView.DividerConfig config1 = new WheelView.DividerConfig();
        config1.setColor(0xFF33B5E5);//线颜色
        config1.setAlpha(140);//线透明度
        config1.setRatio((float) (1.0));//线比率
        picker.setDividerConfig(config1);

        picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener()
        {
            @Override
            public void onDatePicked(String year, String month, String day)
            {
                birth.setText(year + "-" + month + "-" + day);
            }
        });
        picker.show();
    }


    private void setCityOption(final TextView tvCity)
    {
        observable = Observable.create(new ObservableOnSubscribe<ArrayList<Province>>()
        {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<Province>> e) throws Exception
            {
                ArrayList<Province> data = new ArrayList<>();
                String json = ConvertUtils.toString(mContext.getAssets().open("city.json"));
                data.addAll((ArrayList<Province>) new Gson().fromJson(json, new TypeToken<ArrayList<Province>>()
                {
                }.getType()));
                e.onNext(data);
                e.onComplete();
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<ArrayList<Province>>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<ArrayList<Province>>()
                {
                    private Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        disposable = d;
                    }

                    @Override
                    public void onNext(ArrayList<Province> provinces)
                    {
                        if (provinces.size() > 0)
                        {
                            AddressPicker picker = new AddressPicker(mContext, provinces);
                            picker.setHideProvince(false);
                            picker.setHideCounty(true);
                            picker.setTopLineVisible(false);
                            picker.setTitleText("选择城市");
                            picker.setCancelTextColor(ContextCompat.getColor(mContext, R.color.C0311));
                            picker.setTitleTextSize(14);
                            picker.setSubmitTextColor(ContextCompat.getColor(mContext, R.color.C0311));
                            // picker.setColumnWeight(1 / 3.0, 2 / 3.0);//将屏幕分为3份，省级和地级的比例为1:2
                            picker.setSelectedItem("北京", "北京", "");
                            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener()
                            {
                                @Override
                                public void onAddressPicked(Province province, City city, County county)
                                {
                                    tvCity.setText(city.getAreaName());
                                }
                            });
                            picker.show();
                        }
                        else
                        {
                            ToastUtils.showShort("数据初始化失败");
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        Logger.t(TAG).d("e>" + e);
                    }

                    @Override
                    public void onComplete()
                    {
                        Logger.t(TAG).d("city's data parse has completed");
                    }
                });
    }

    //相机取图
    private void toTakePhotoFromCamera()
    {
        boolean iscamerapermissions = CommonUtils.cameraIsCanUse();
        if (iscamerapermissions)
        {
            File storageDir = this.getExternalCacheDir();
            File image = new File(storageDir, "TempImage.jpg");
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (openCameraIntent.resolveActivity(this.getPackageManager()) != null)
            {
                //openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
                startActivityForResult(openCameraIntent, EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA);
            }
        }
        else
        {
            ToastUtils.showShort("请打开相机功能");
        }
    }

    //相册取图
    private void toPhotoFromImageAblum()
    {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setPreviewEnabled(true)
                .setShowCamera(true)
                .setShowGif(false)
                .start(mContext, EamConstant.EAM_TAKE_PHOTO_FROM_GALLERY);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    /**
     * 获取接口信息 设置内容
     *
     * @param userInfo
     */
    private void setDataToView(UserEditInfoBean userInfo)
    {
        usersBean = userInfo;
        //将网络上的图片存起来
        remoteImgLst.clear();
        remoteImgLst.addAll(CommonUtils.strWithSeparatorToList(usersBean.getUpUrls(), CommonUtils.SEPARATOR));
        imgLst.clear();
        imgLst.addAll(remoteImgLst);
        Logger.t(TAG).d("服务器上的图片数量：" + remoteImgLst.size());
        //加号
        if (imgLst.size() < 6)
        {
            imgLst.add(addImg);
        }
        mEditPhotoListAdapter.notifyDataSetChanged();

        if (usersBean != null)
        {
            etName.setText(TextUtils.isEmpty(usersBean.getNicName()) ? "" : usersBean.getNicName());
            tvHeight.setText(transString(usersBean.getHeight()));
            tvConstellation.setText(transString(usersBean.getConstellation()));
            tvState.setText(usersBean.getEmState());
            tvBrithday.setText(transString(usersBean.getBirth()));
            tvEducation.setText(transString(usersBean.getEducation()));
            tvCity.setText(transString(usersBean.getCity()));
            tvWork.setText(transString(usersBean.getOccupation()));
            tvIncome.setText(transString(usersBean.getIncome()));
            if (TextUtils.isEmpty(usersBean.getSignature()))
            {
                tvSign.setText("");
                tvSign.setHint("这家伙很懒，什么都没有留下哦~");
                SharePreUtils.setUserSign(MyInfoEditAct.this, "");
            }
            else
            {
                tvSign.setText(usersBean.getSignature());
            }
            tvWorkOther.setText(transString(usersBean.getCmOccupation()));
            tvIncomeOther.setText(transString(usersBean.getCmIncome()));
            tvEducationOther.setText(transString(usersBean.getCmEducation()));
            tvHeightOther.setText(transString(usersBean.getCmHeight()));
            //最近想去
            if (!TextUtils.isEmpty(usersBean.getWhitherName()))
            {
                tvWantToGo.setText(usersBean.getWhitherName());
                this.goTime = usersBean.getWhitherTime();
                this.goResId = usersBean.getWhitherId();
            }
            else
            {
                tvWantToGo.setHint("请选择餐厅");
            }
            tvCityOther.setText(transString(usersBean.getCmCity()));
            if (TextUtils.isEmpty(usersBean.getuLab()))
            {
                Logger.t(TAG).d("暂无标签信息");
            }
            else
            {
                listLab.clear();
                listLab.addAll(CommonUtils.strToList(usersBean.getuLab()));
                labelResult = CommonUtils.listToStrWishSeparator(listLab, CommonUtils.SEPARATOR);
                userLabelAdapter.notifyDataSetChanged();
            }

            headerPath = usersBean.getUphUrl();
            // 用户头像
            if (!TextUtils.isEmpty(usersBean.getUphUrl()))
            {
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(usersBean.getUphUrl())
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(rivHead);
            }
            if (TextUtils.isEmpty(usersBean.getUpUrls()))
            {
                Logger.t(TAG).d("暂无相册路径");

            }

            String rmAnFlg = usersBean.getRmAnFlg(); //官方认证
            switch (rmAnFlg)
            {
                case "0": //未提交
                    tvIdentity.setText("去认证");
                    tvIdentity.setTextColor(ContextCompat.getColor(mContext, R.color.C0313));
                    intentWhere = 0;
                    break;
                case "1": //审核中
                    tvIdentity.setText("审核中");
                    tvIdentity.setTextColor(ContextCompat.getColor(mContext, R.color.C0315));
                    intentWhere = 0;
                    break;
                case "2": //通过
                    tvIdentity.setText("已认证");
                    tvIdentity.setTextColor(ContextCompat.getColor(mContext, R.color.C0321));
                    intentWhere = 1;
                    break;
                case "3": //未通过
                    tvIdentity.setText("未通过");
                    tvIdentity.setTextColor(ContextCompat.getColor(mContext, R.color.C0313));
                    intentWhere = 0;
                    break;
            }


            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    private String transString(String originlStr)
    {
        if (originlStr != null)
        {
            if ("".equals(originlStr) || originlStr.equals("保密"))
                return "请选择";
            else if (originlStr.equals("请选择"))
                return "保密";
        }
        else
            return "";
        return originlStr;
    }

    private void updateHeadImg(final Uri inputUri)
    {
        String fileKeyName = CdnHelper.userImage + SharePreUtils.getUserMobile(this) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        CdnHelper.getInstance().setOnCdnFeedbackListener(new CdnFeedbackListener(this, inputUri));
        CdnHelper.getInstance().putFile(new File(inputUri.getPath()), "img", fileKeyName, 0);
    }

    /**
     * CND调用回调
     */
    private static class CdnFeedbackListener implements IOnCdnFeedbackListener
    {
        private final WeakReference<MyInfoEditAct> mActRef;
        private Uri imgUri;

        private CdnFeedbackListener(MyInfoEditAct mAct, Uri imgUri)
        {
            this.mActRef = new WeakReference<MyInfoEditAct>(mAct);
            this.imgUri = imgUri;
        }

        @Override
        public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
        {
            final MyInfoEditAct cAct = mActRef.get();
            if (cAct != null)
            {
                Logger.t(TAG).d("成功：" + response.toString());
                try
                {
                    GlideApp.with(EamApplication.getInstance())
                            .asBitmap()
                            .load(imgUri)
                            .placeholder(R.drawable.userhead)
                            .error(R.drawable.userhead)
                            .into(cAct.rivHead);
                    cAct.headerPath = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                    if (cAct.pDialog != null && cAct.pDialog.isShowing())
                        cAct.pDialog.dismiss();
                } catch (Exception e)
                {
                    Logger.t(cAct.TAG).d(e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onProcess(long len)
        {
        }

        @Override
        public void onFail(JSONObject response, File file)
        {
            final MyInfoEditAct cAct = mActRef.get();
            if (cAct != null)
                Logger.t(cAct.TAG).d("错误：" + response.toString());
        }
    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();

        switch (interfaceName)
        {
            case NetInterfaceConstant.UserC_uInfo:
                etName.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getUserInfoCallback(UserEditInfoBean userInfo)
    {
        setDataToView(userInfo);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void postUserInfoCallback(String response, final UserEditInfoBean bean)
    {
        Logger.t(TAG).d("编辑资料接口返回" + response);
        if (response == null)
        {
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
            ToastUtils.showShort("获取信息失败");
        }
        else
        {
            SharePreUtils.setHeadImg(mContext, bean.getUphUrl());
            SharePreUtils.setNicName(mContext, bean.getNicName());
            IMHelper.getInstance().setTxUserInfo(mContext);
            //发送广播，通知直播账户相关余额已经改变
            Intent intent1 = new Intent(EamConstant.ACTION_UPDATE_USER_INFO);
            intent1.putExtra("needRefreshUserInfo", true);
            sendBroadcast(intent1);
            ToastUtils.showShort("提交资料成功");
            mContext.finish();
            SharePreUtils.setUserSign(MyInfoEditAct.this, SharePreUtils.getUserSign(mContext));
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }


    //上传图片失败后回调
    @Override
    public void postUserGalleryImagesCallback(JSONObject response)
    {
        ToastUtils.showShort("上传图片失败，请稍后重试");
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }
}
