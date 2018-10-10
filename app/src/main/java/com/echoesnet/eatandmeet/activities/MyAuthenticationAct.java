package com.echoesnet.eatandmeet.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.live.LiveAuthenticationPassActivity;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.presenters.ImpIMyAuthenticationView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyAuthenticationView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.LoadingProgressUtil;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.CustomAlertDialog;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;
import com.joanzapata.iconify.widget.IconTextView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

public class MyAuthenticationAct extends BaseActivity implements IMyAuthenticationView
{
    final static String TAG = MyAuthenticationAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBarSwitch topBar;
    @BindView(R.id.ev_id_name)
    EditText fetUserIdName;
    @BindView(R.id.ev_id_number)
    EditText fetUserIdNum;
    @BindView(R.id.btn_upload_id_img1)
    ImageView btnUploadIdImg1;
    @BindView(R.id.btn_upload_id_img2)
    ImageView btnUploadIdImg2;
    @BindView(R.id.btn_id_info_commit)
    Button btnIdInfoCommit;
    @BindView(R.id.realName_Not_Pass)
    TextView realName_Not_Pass;
    @BindView(R.id.itv_del_card)
    IconTextView itvDelCard;
    @BindView(R.id.itv_del_person)
    IconTextView itvDelPerson;
    @BindView(R.id.iv_id_pic1)
    RoundedImageView ivIdPic1;
    @BindView(R.id.iv_id_pic2)
    RoundedImageView ivIdPic2;


    //存放截图后的路径
    private String pic1Path;
    private String pic2Path;
    //上传认证后待审核时的回显图片
    private String updateLoadPic1;
    private String updateLoadPic2;

    private String rmAnFlg;

    //标识点击的是身份证照片还是本人照
    private int operImgIndex = 0;

    /**
     * 调用拍照和相册的弹出层
     */
    private PopupWindow headerPopupWindow;
    private View headerView;
    private AutoLinearLayout ll_popup;

    private Activity mContext;
    private MyProgressDialog pDialog;
    ArrayList<String> list = new ArrayList<>();
    private String idCardPic = "android.resource://com.echoesnet.eatandmeet/drawable/wd_rz_newshenfenzheng";
    private String idPersonPic = "android.resource://com.echoesnet.eatandmeet/drawable/wd_newtouzhao";
    private TreeMap<Integer, String> uploadPicMap = new TreeMap<Integer, String>();
    private ImpIMyAuthenticationView myAuthenticationView;
    private String source;
    private View loadingView;
    private int uploadIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_authentication);
        ButterKnife.bind(this);
        initAfterView();
        if (savedInstanceState != null)
            operImgIndex = savedInstanceState.getInt("operImgIndex");
    }

    //在onstart后执行
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt("operImgIndex", operImgIndex);
    }

    private void initAfterView()
    {
        source = getIntent().getStringExtra("openSource");
        mContext = this;
        topBar = (TopBarSwitch) findViewById(R.id.top_bar);
        topBar.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                if (!TextUtils.isEmpty(source))
                {
                    finish();
                }
                else
                {
                    //由于小米系列手机在拍照返回后，可能将homeact已经销毁了,故需要重启
                    Intent intent = new Intent(mContext, HomeAct.class);
                    intent.putExtra("showPage", 4);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("实名认证");
        topBar.getNavBtns(new int[]{1, 0, 0, 0});

        pDialog = new MyProgressDialog()
                .buildDialog(mContext)
                .setDescription("正在获取认证状态...");
        pDialog.setCancelable(false);
        myAuthenticationView = new ImpIMyAuthenticationView(mContext, this);
        HashMap<String, String> map;

        ArrayList list = getIntent().getParcelableArrayListExtra("userAuthenticationState");
        if (list != null && list.get(0) != null)
            setUi((HashMap<String, String>) list.get(0));
        else
        {
            if (myAuthenticationView != null)
                myAuthenticationView.getRealNameState();
        }
//        setUi("0");
    }

    /**
     * 根据不同的状态设置不同的显示内容
     *
     * @param map
     */
    private void setUi(HashMap<String, String> map)
    {
        /*if (!rmAnFlg.equals("4") && !rmAnFlg.equals("3"))
        {
            fetUserIdNum.addValidator(new OrValidator(
                    "请输入正确的身份证号码",
                    new RegexpValidator("15位身份证号不正确", "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$"),
                    new RegexpValidator("18位身份证号不正确", "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{4}$"))
            );
        }*/
        String realName = map.get("realName");
        String idCard = map.get("idCard");
        String idCardUrl = map.get("idCardUrl");
        String posPhUrl = map.get("posPhUrl");
        rmAnFlg = map.get("rmAnFlg");
        String rejectReason = map.get("rejectReason");

        switch (rmAnFlg)
        {
            case "0":
                break;
            //审核中
            case "1":
                updateLoadPic1 = idCardUrl;
                updateLoadPic2 = posPhUrl;
                fetUserIdName.setText(realName);
                fetUserIdNum.setText(idCard);
                fetUserIdName.setEnabled(false);
                fetUserIdNum.setEnabled(false);
                btnUploadIdImg1.setEnabled(false);
                btnUploadIdImg2.setEnabled(false);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(idCardUrl)
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(ivIdPic1);
//                        .into(btnUploadIdImg1);
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(posPhUrl)
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(ivIdPic2);
//                        .into(btnUploadIdImg2);
                btnUploadIdImg1.setVisibility(View.GONE);
                btnUploadIdImg2.setVisibility(View.GONE);
//                RelativeLayout.LayoutParams pa2 = (RelativeLayout.LayoutParams) btnUploadIdImg2.getLayoutParams();
//                pa2.height = CommonUtils.dp2px(mContext, 148);
//                pa2.width = CommonUtils.dp2px(mContext, 200);
//                btnUploadIdImg2.setLayoutParams(pa2);
//                RelativeLayout.LayoutParams pa1 = (RelativeLayout.LayoutParams) btnUploadIdImg1.getLayoutParams();
//                pa1.height = CommonUtils.dp2px(mContext, 148);
//                pa1.width = CommonUtils.dp2px(mContext, 200);
//                btnUploadIdImg1.setLayoutParams(pa1);
                btnIdInfoCommit.setText("审核中");
                btnIdInfoCommit.setEnabled(false);
                break;
            case "2":
                Intent intent = new Intent(mContext, LiveAuthenticationPassActivity.class);
                intent.putExtra("realName", realName);
                intent.putExtra("idCard", idCard);
                startActivity(intent);
                finish();
                break;
            //驳回
            case "3":
                realName_Not_Pass.setText(realName_Not_Pass.getText() + "，驳回原因：" + rejectReason);
                realName_Not_Pass.setVisibility(View.GONE);
                fetUserIdName.setText(realName);
                fetUserIdNum.setText(idCard);
                fetUserIdNum.setEnabled(false);
                fetUserIdName.setEnabled(false);
                break;
            //做过发红包的实名认证但没做过主播实名认证(显示身份证号和姓名)
            case "4":
                fetUserIdName.setText(realName);
                fetUserIdNum.setText(idCard);
                fetUserIdNum.setEnabled(false);
                fetUserIdName.setEnabled(false);
                break;
            case "5":
                if (!TextUtils.isEmpty(realName) && !TextUtils.isEmpty(idCard))
                {
                    fetUserIdName.setText(realName);
                    fetUserIdNum.setText(idCard);
                    fetUserIdNum.setEnabled(false);
                    fetUserIdName.setEnabled(false);
                }
                break;
            default:
                break;
        }
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
    }


    //在onResume前执行
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result)
    {
        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case EamConstant.EAM_TAKE_PHOTO_FROM_GALLERY:
                    {

                        ArrayList<String> mResults = result.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                        Logger.t(TAG).d("图片地址：" + mResults.get(0));

                        //File imgPath = new File(getExternalCacheDir() + "/" + "TempImage.jpg");
                        String tempFileName = "a_" + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                        String outPutImagePath = NetHelper.getRootDirPath(mContext) + CommonUtils.toMD5(tempFileName);
                        File outFile = new File(outPutImagePath);

                        int degree = ImageUtils.readPictureDegree(mResults.get(0));
                        //int degree = ImageUtils.readPictureDegree(imgPath.getAbsolutePath());
                        Logger.t(TAG).d("旋转角度为》" + degree);
                        Bitmap bitmap = ImageUtils.getBitmapFromUri(Uri.fromFile(new File(mResults.get(0))), mContext);
                        if (bitmap != null)
                        {
                            ImageUtils.compressBitmap(bitmap, outFile.getPath(), 130, degree);
                            Logger.t(TAG).d("压缩成功" + outFile.length() / 1024);
                            beginCrop(Uri.fromFile(outFile));
                        }
                        else
                        {
                            ToastUtils.showShort("选择图片失败，请重新选择");
                        }
                        break;
                    }
                    case EamConstant.EAM_TAKE_PHOTO_FROM_CAMERA:
                    {
                        try
                        {
                            File imgPath1 = new File(getExternalCacheDir() + "/" + "TempImage.jpg");
                            String tempFileName1 = "a_" + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                            String outPutImagePath1 = NetHelper.getRootDirPath(mContext) + CommonUtils.toMD5(tempFileName1);
                            // Uri uri1 = Uri.parse("file://" + "/" + imgPath.getAbsolutePath());
                            Logger.t(TAG).d("照片路径：" + imgPath1.getAbsolutePath());
                            final File outFile1 = new File(outPutImagePath1);

                            // zdw --- 添加针对三星拍照后旋转90显示问题
                            int degree1 = ImageUtils.readPictureDegree(imgPath1.getAbsolutePath());
                            ImageUtils.compressBitmap(ImageUtils.getBitmapFromFile(mContext, imgPath1.getAbsolutePath()), outFile1.getPath(), 130, degree1);
                            Logger.t(TAG).d("压缩成功" + outFile1.length() / 1024);

                            beginCrop(FileProvider.getUriForFile(this, "com.echoesnet.eatandmeet.provider", outFile1));
                            if (!imgPath1.delete())
                            {
                                Logger.t(TAG).d("删除失败");
                            }
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case Crop.REQUEST_CROP:
                    {
                        final Uri uri = Crop.getOutput(result);
                        if (operImgIndex == 1)
                        {
                            pic1Path = uri.getPath();
                            Logger.t(TAG).d("pic1Path:" + pic1Path);
                            if (!TextUtils.isEmpty(pic1Path))
                            {
                                GlideApp.with(EamApplication.getInstance())
                                        .asBitmap()
                                        .load(uri.getPath())
                                        .placeholder(R.drawable.userhead)
                                        .error(R.drawable.userhead)
                                        .into(btnUploadIdImg1);
                                itvDelCard.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) btnUploadIdImg1.getLayoutParams();
                                pa.height = CommonUtils.dp2px(mContext, 148);
                                pa.width = CommonUtils.dp2px(mContext, 200);
                                btnUploadIdImg1.setLayoutParams(pa);

                            }
                            else
                            {
                                ToastUtils.showShort("获取照片失败");
                            }
                        }
                        else if (operImgIndex == 2)
                        {
                            pic2Path = uri.getPath();
                            Logger.t(TAG).d("pic2Path:" + pic2Path);
                            if (!TextUtils.isEmpty(pic2Path))
                            {
                                GlideApp.with(EamApplication.getInstance())
                                        .asBitmap()
                                        .load(uri.getPath())
                                        .placeholder(R.drawable.userhead)
                                        .error(R.drawable.userhead)
                                        .into(btnUploadIdImg2);
                                itvDelPerson.setVisibility(View.VISIBLE);
                                RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) btnUploadIdImg2.getLayoutParams();
                                pa.height = CommonUtils.dp2px(mContext, 148);
                                pa.width = CommonUtils.dp2px(mContext, 200);
                                btnUploadIdImg2.setLayoutParams(pa);
                            }
                            else
                            {
                                ToastUtils.showShort("获取照片失败");
                            }
                        }
                        break;
                    }

                    default:
                        break;
                }
                break;
            case Crop.RESULT_ERROR:
                switch (requestCode)
                {
                    case Crop.REQUEST_CROP:
                        ToastUtils.showShort(Crop.getError(result).getMessage());
                        break;
                }
            default:
                break;
        }
    }

    private void beginCrop(Uri source)
    {
        String fileName = CommonUtils.toMD5("a_" + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg");
        Logger.t(TAG).d("文件名为：" + fileName);
        Uri destination = Uri.fromFile(new File(NetHelper.getRootDirPath(mContext), fileName));
        Crop.of(source, destination).withAspect(4, 3).start(this);
    }

    public void hideSoftWindow()
    {
        inputMethodManager.hideSoftInputFromWindow(fetUserIdName.getWindowToken(), 0);
    }

    @OnClick({R.id.btn_upload_id_img1, R.id.btn_upload_id_img2, R.id.btn_id_info_commit,
            R.id.itv_del_card, R.id.itv_del_person, R.id.iv_id_pic1, R.id.iv_id_pic2})
    void viewClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_upload_id_img1:
                if (inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(fetUserIdName.getWindowToken(), 0);
                operImgIndex = 1;

                if (itvDelCard.getVisibility() == View.GONE)
                {
                    if (headerPopupWindow == null || !headerPopupWindow.isShowing())
                    {
                        hideSoftWindow();
                        takePhotoAndCameraPop();
                        headerPopupWindow.showAtLocation(findViewById(R.id.btn_upload_id_img1), Gravity.BOTTOM, 0, 0);
                        backgroundAlpha(0.5f);
                    }
                }
                else
                {
                    list.clear();
                    list.add(pic1Path);
                    CommonUtils.showImageBrowser(mContext, list, 0, v);
                }
                break;
            case R.id.btn_upload_id_img2:
                if (inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(fetUserIdName.getWindowToken(), 0);
                operImgIndex = 2;

                if (itvDelPerson.getVisibility() == View.GONE)
                {
                    if (headerPopupWindow == null || !headerPopupWindow.isShowing())
                    {
                        hideSoftWindow();
                        takePhotoAndCameraPop();
                        headerPopupWindow.showAtLocation(findViewById(R.id.btn_upload_id_img2), Gravity.BOTTOM, 0, 0);
                        backgroundAlpha(0.5f);
                    }
                }
                else
                {
                    list.clear();
                    list.add(pic2Path);
                    CommonUtils.showImageBrowser(mContext, list, 0, v);
                }
                break;
            case R.id.btn_id_info_commit:
                //判断连击
                if (CommonUtils.isFastDoubleClick())
                    return;
                String userIDName = fetUserIdName.getText().toString();
                String userIDNum = fetUserIdNum.getText().toString();
                if (TextUtils.isEmpty(userIDName) || TextUtils.isEmpty(userIDNum))
                {
                    new CustomAlertDialog(mContext)
                            .builder()
                            .setTitle("提示")
                            .setMsg("姓名或身份证不能为空")
                            .setNegativeButton("确认", new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {

                                }
                            }).show();
                    return;
                }
                if (!TextUtils.isEmpty(rmAnFlg))
                {
                    if (!rmAnFlg.equals("4") && !rmAnFlg.equals("3"))
                    {
                        if (!CommonUtils.verifyInput(6, userIDNum))
                        {
                            if (!CommonUtils.verifyInput(7, userIDNum))
                            {
                                ToastUtils.showShort("身份证号不正确");
                                return;
                            }
                        }
                    }
                }
                if (pDialog != null && !pDialog.isShowing())
                {
                    pDialog.setDescription("正在上传图片...");
                    pDialog.show();
                }
                String filePath = "";
                uploadPicMap.clear();
                if (!TextUtils.isEmpty(pic1Path) && !TextUtils.isEmpty(pic2Path))
                {
                    for (int i = 0; i < 2; i++)
                    {
                        uploadIndex = i;
                        if (i == 0)
                        {
                            filePath = pic1Path;
                        }
                        else
                        {
                            filePath = pic2Path;
                        }
                        final File file = new File(filePath);

                        postIdCardPic(file, NetHelper.UPLOAD_IDCARD_PIC, fetUserIdName.getText().toString().trim(),
                                fetUserIdNum.getText().toString().trim(), file.getName(), uploadIndex);
                    }
                }
                else
                {
                    if (pDialog != null && pDialog.isShowing())
                    {
                        pDialog.dismiss();
                    }
                    ToastUtils.showShort("身份证照未上传");
                }
                break;
            case R.id.itv_del_card:
                pic1Path = "";
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(R.drawable.upload_button)
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(btnUploadIdImg1);
                itvDelCard.setVisibility(View.GONE);
                RelativeLayout.LayoutParams pa = (RelativeLayout.LayoutParams) btnUploadIdImg1.getLayoutParams();
                pa.height = CommonUtils.dp2px(mContext, 65);
                pa.width = CommonUtils.dp2px(mContext, 65);
                btnUploadIdImg1.setLayoutParams(pa);
                break;
            case R.id.itv_del_person:
                pic2Path = "";
                GlideApp.with(EamApplication.getInstance())
                        .asBitmap()
                        .load(R.drawable.upload_button)
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(btnUploadIdImg2);
                itvDelPerson.setVisibility(View.GONE);
                pa = (RelativeLayout.LayoutParams) btnUploadIdImg2.getLayoutParams();
                pa.height = CommonUtils.dp2px(mContext, 65);
                pa.width = CommonUtils.dp2px(mContext, 65);
                btnUploadIdImg2.setLayoutParams(pa);
                break;
            case R.id.iv_id_pic1:
                list.clear();
                String imgUrl = "1".equals(rmAnFlg) ? updateLoadPic1 : idCardPic;
                list.add(imgUrl);
//                list.add(idCardPic);
                CommonUtils.showImageBrowser(mContext, list, 0, v);
                break;
            case R.id.iv_id_pic2:
                list.clear();
                String imgUrl2 = "1".equals(rmAnFlg) ? updateLoadPic2 : idPersonPic;
                list.add(imgUrl2);
//                list.add(idPersonPic);
                CommonUtils.showImageBrowser(mContext, list, 0, v);
                break;
            default:
                break;
        }
    }


    //相机取图
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
                //openCameraIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.echoesnet.eatandmeet.provider", image));
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
        headerPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                backgroundAlpha(1.0f);
            }
        });
        TextView item_popupwindows_Photo = (TextView) headerView.findViewById(R.id.item_popupwindows_Photo);
        item_popupwindows_Photo.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                toPhotoFromImageAblum();
            //    CommonUtils.getImageFromGallery(mContext);
                headerPopupWindow.dismiss();
            }
        });
        headerPopupWindow.getContentView().setFocusable(true);
        headerPopupWindow.setOutsideTouchable(true);
        headerPopupWindow.setAnimationStyle(R.style.PopupAnimation);
//        ColorDrawable colorDrawable = new ColorDrawable(0xb0000000);
//        headerPopupWindow.setBackgroundDrawable(colorDrawable);
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

    /**
     * popupwindow在显示中点击返回杀死当前页会造成窗口泄露
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
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    private void postIdCardPic(File file, String RequestURL, final String userName, final String userIdCard, String fileKey, int uploadOrder)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                CdnHelper.getInstance().uploadFileToBackServer(file, RequestURL, fileKey, uploadOrder, new IOnCdnFeedbackListener()
                {
                    @Override
                    public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                    {
                        try
                        {
                            Logger.t(TAG).d("++++++++++++response:" + response.toString() + " | fileKeyName:" + fileKeyName + " | uploadOrder:" + uploadOrder);
                            String status = response.getString("status");
                            if (status.equals("0"))
                            {
//                                file.delete();
                                String body = response.getString("body");
                                uploadPicMap.put(uploadOrder, body);
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        if (uploadPicMap.keySet().size() == 2)
                        {
                            mContext.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (pDialog != null && pDialog.isShowing())
                                    {
                                        pDialog.dismiss();
                                    }
                                }
                            });
                            if (myAuthenticationView != null)
                                myAuthenticationView.postRealName(userName, userIdCard, uploadPicMap.get(0), uploadPicMap.get(1));
                        }
                    }

                    @Override
                    public void onProcess(long len)
                    {

                    }

                    @Override
                    public void onFail(JSONObject response, File file)
                    {
                        mContext.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                ToastUtils.showShort("上传图片失败，请重试");
                                if (pDialog != null && pDialog.isShowing())
                                {
                                    pDialog.dismiss();
                                }
                            }
                        });
                    }
                });
            }
        }).start();


    }

    @Override
    public void callServerErrorCallback(String interfaceName, String code, String errBody)
    {
        switch (interfaceName)
        {
            case NetInterfaceConstant.LiveC_realName:
                try
                {
                    if (code.equals("IDCARD_ERROR"))
                    {
                        JSONObject body = new JSONObject(errBody);
                        String count = body.getString("count");
                        if (!count.equals("0"))
                        {
                            ToastUtils.showShort("姓名与身份证号不统一,您还剩余" + count + "次。");
                        }
                        else
                        {
                            ToastUtils.showShort("您的次数已用尽，请24小时后重试！");
                        }
                    }
                    else
                    {
                        Logger.t(TAG).d("错误码为：%s", code);
                    }

                } catch (JSONException e)
                {
                    e.printStackTrace();
                } catch (Exception e)
                {
                    e.printStackTrace();
                } finally
                {
                    mContext.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            if (pDialog != null && pDialog.isShowing())
                            {
                                pDialog.dismiss();
                            }
                        }
                    });
                }
                mContext.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (pDialog != null && pDialog.isShowing())
                        {
                            pDialog.dismiss();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    public void requestNetError(Call call, Exception e, final String exceptSource)
    {
        loadingView = View.inflate(mContext, R.layout.view_loading_cover, null);
        LoadingProgressUtil.showWithErrorLoadingView(loadingView, true, 1, new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            }
        });
        NetHelper.handleNetError(mContext, "", exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getRealNameStateCallBack(String response)
    {
//        LoadingProgressUtil.showWithErrorLoadingView(loadingView, false, 0, null);
        Logger.t(TAG).d("返回结果：" + response);
        try
        {
            JSONObject body = new JSONObject(response);
            final String realName = body.getString("realName");
            final String idCard = body.getString("idCard");
            final String idCardUrl = body.getString("idCardUrl");
            final String posPhUrl = body.getString("posPhUrl");
            rmAnFlg = body.getString("rmAnFlg");
            final String rejectReason = body.getString("rejectReason");
            //防止出现接口访问过快而导致跳转Activity出现闪的情况
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        Thread.sleep(600);
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("realName", realName);
                                map.put("idCard", idCard);
                                map.put("idCardUrl", idCardUrl);
                                map.put("posPhUrl", posPhUrl);
                                map.put("rmAnFlg", rmAnFlg);
                                map.put("rejectReason", rejectReason);
                                setUi(map);
                            }
                        });
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (JSONException e)
        {
            Logger.t(TAG).d("异常：" + e.getMessage());
        }

    }

    @Override
    public void postRealNameCallBack(String response)
    {
        Logger.t(TAG).d("返回结果：" + response);
        ToastUtils.showShort("您的证件已经提交审核，预计24小时内完成，请您耐心等待");
        if (pDialog != null && pDialog.isShowing())
        {
            pDialog.dismiss();
        }
        finish();
    }

    private void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        mContext.getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (TextUtils.isEmpty(source))
        {
            //由于小米系列手机在拍照返回后，可能将homeact已经销毁了,故需要重启
            Intent intent = new Intent(mContext, HomeAct.class);
            intent.putExtra("showPage", 4);
            startActivity(intent);
        }

    }
}
