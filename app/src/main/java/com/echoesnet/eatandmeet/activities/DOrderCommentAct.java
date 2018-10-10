package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.DishBean;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.presenters.ImpDOrderCommentView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IDOrderCommentView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.MResDishCommentAdapter;
import com.echoesnet.eatandmeet.views.adapters.ResCommentImgsAdapter;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.CustomRatingBar;
import com.echoesnet.eatandmeet.views.widgets.CustomRatingBar.IRatingBarClickedListener;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.linearlistview.LinearListView;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

//import com.zfdang.multiple_images_selector.SelectorSettings;

/**
 * 订单评价页
 */

public class DOrderCommentAct extends MVPBaseActivity<IDOrderCommentView, ImpDOrderCommentView> implements IDOrderCommentView
{
    //region 变量
    public final static String TAG = DOrderCommentAct.class.getSimpleName();
    @BindView(R.id.top_bar)
    TopBar topBar;
    @BindView(R.id.et_refund_reason)
    EditViewWithCharIndicate etResComment;
    @BindView(R.id.rv_comment_imgs)
    RecyclerView rcvCommentImgs;
    //菜品列表
    @BindView(R.id.lv_res_dish_lst)
    LinearListView llvDishComments;
    @BindView(R.id.rateBar)
    CustomRatingBar ratingBar;

    @BindView(R.id.btn_commit_comment)
    Button btnCommentRes;
    @BindView(R.id.all_dish)
    AutoLinearLayout allDish;

    private Activity mContext;
    private ResCommentImgsAdapter mResCommentAdapter;
    private MResDishCommentAdapter mResDishCommentAdapter;
    //要上传图片的Url
    private List<String> imgLst;
    private Dialog pDialog;
    ArrayMap<Integer, String> map = new ArrayMap<Integer, String>();
    private String orderId, resId, resStar = "0", resName, addImg = "android.resource://com.echoesnet.eatandmeet/drawable/wode_addcz_xhdpi";
    private List<DishBean> dishLst;
    private String orderType;
    private boolean isClubComments = false;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_order_comment);
        ButterKnife.bind(this);
        initAfterViews();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        pDialog = null;
    }


    void initAfterViews()
    {
        mContext = this;
        resName = getIntent().getStringExtra("resName");
        topBar.setTitle(TextUtils.isEmpty(resName) ? "评价" : resName);
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
        orderId = getIntent().getStringExtra("orderId");
        resId = getIntent().getStringExtra("resId");
        dishLst = (ArrayList<DishBean>) getIntent().getSerializableExtra("dishLst");
        // 区分正常订单和闪付订单
        orderType = getIntent().getStringExtra("orderType");
        Logger.t(TAG).d("订单列表传的标识类型--> " + orderType);
        if (orderType.equals("quickType"))
        {
            allDish.setVisibility(View.GONE);
            Logger.t(TAG).d("获取订单类型--> " + orderType);
        }
        else if (orderType.equals("normalType"))
        {
            allDish.setVisibility(View.VISIBLE);
            Logger.t(TAG).d("获取订单类型--> " + orderType);
        }
        else
        {
            allDish.setVisibility(View.GONE);
            Logger.t(TAG).d("获取订单类型--> " + orderType);
        }

        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCommentImgs.setLayoutManager(linearLayoutManager);
        imgLst = new ArrayList<>();
        imgLst.add(addImg);
        //设置适配器
        mResCommentAdapter = new ResCommentImgsAdapter(mContext, imgLst);
        mResCommentAdapter.setOnItemClickListener(new ResCommentImgsAdapter.OnItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Logger.t(TAG).d("点击位置：" + position);
                //只有点击最后一个item时候打开图片选择器
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
                    //原来是长按触发，现在改为单击触发
                    Logger.t(TAG).d("长按位置：" + position);
                    final ArrayList<String> finalImgLst = new ArrayList<String>(imgLst);
                    for (String s : finalImgLst)
                    {
                        Logger.t(TAG).d("url" + s);
                        if (s.equals(addImg))
                        {
                            finalImgLst.remove(s);
                        }
                    }
                    showContextMenuBox(finalImgLst, position);
                }
            }
        });
        rcvCommentImgs.setAdapter(mResCommentAdapter);
        //rcvCommentImgs.setOn
        ratingBar.setIndicator(false);
        //设置餐厅评星回调
        ratingBar.setIRatingBarClickedListener(new IRatingBarClickedListener()
        {
            @Override
            public void startClicked(int starNum)
            {
                resStar = String.valueOf(starNum);
            }
        });

        mResDishCommentAdapter = new MResDishCommentAdapter(mContext, dishLst, "commit");
        llvDishComments.setAdapter(mResDishCommentAdapter);

        pDialog = DialogUtil.getCommonDialog(mContext, "正在处理...");
        pDialog.setCancelable(false);
    }

    //在 onResume() 前调用
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_IMAGE_PICKER:
                if (resultCode == RESULT_OK)
                {
                    ArrayList<String> mResults = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    String flag = data.getStringExtra("isOpenCamera");
                    Logger.t(TAG).d("flag:" + flag);
                    Logger.t(TAG).d("Build.BRAND:" + Build.BRAND);
                    Logger.t(TAG).d("Build.MANUFACTURER:" + Build.MANUFACTURER);
                    Logger.t(TAG).d("os.name:" + System.getProperty("os.name"));
                    Logger.t(TAG).d("os.version:" + System.getProperty("os.version"));
                    if (((Build.MANUFACTURER).toLowerCase()).contains("samsung"))
                    {
                        for (String s : mResults)
                        {
                            Logger.t(TAG).d("修改前path" + s);
                            Uri uri = Uri.fromFile(new File(s));
                            Logger.t(TAG).d("hostUri.getPath():" + uri.getPath());
                            int degree = ImageUtils.readPictureDegree(uri.getPath());
                            Logger.t(TAG).d("照片旋转角度--> " + degree);
                            Bitmap bitmap = ImageUtils.rotaingImageView2(degree, uri, mContext);
                            ImageUtils.convertImageToFile(bitmap, s, 100);
                            mResults.remove(s);
                            mResults.add(uri.getPath());
                        }
                    }
                    assert mResults != null;
                    imgLst.addAll(imgLst.size() - 1, mResults);
                    if (imgLst.size() > 6)
                    {
                        imgLst.remove(imgLst.size() - 1);
                    }
                    mResCommentAdapter.notifyDataSetChanged();
                    for (String str : mResults)
                    {
                        Logger.t(TAG).d(str);
                    }
                }
                break;
            case Crop.REQUEST_PICK:
                if (resultCode == RESULT_OK)
                {
                    beginCrop(data.getData());
                }
                break;
            case Crop.REQUEST_CROP:
                // handleCrop(resultCode, data);
                break;
            default:
                break;
        }
    }

    private void beginCrop(Uri source)
    {
        String fileName = "a_" + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        Logger.t(TAG).d("文件名为：" + fileName);
        Uri destination = Uri.fromFile(new File(getCacheDir(), CommonUtils.toMD5(fileName)));
        Crop.of(source, destination).asSquare().withMaxSize(400, 400).start(this);
    }

/*    private void handleCrop(int resultCode, Intent result)
    {
        if (resultCode == RESULT_OK)
        {
            Uri hostUri=Crop.getOutput(result);
            imgLst.add(imgLst.size()-1,hostUri);
            mResCommentAdapter.notifyDataSetChanged();

        } else if (resultCode == Crop.RESULT_ERROR)
        {
            if(Crop.getError(result).getMessage().contains("GIF"))
            {
                ToastUtils.showShort(mContext,"不能上传GIF格式的图片");
                return;
            }
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    //region 废弃的函数

    /**
     * 提交文本
     */
    /*private void postResCommentText(MyResCommentBean resCommentBean)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(pDialog!=null&&!pDialog.isShowing())
                    pDialog.show();
            }
        });

        Map<String,String> reqParamMap=NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put("evalBean",new Gson().toJson(resCommentBean,MyResCommentBean.class));

        Logger.t(TAG).d("resComment 的josn"+ new Gson().toJson(reqParamMap));
        OkHttpUtils.postString()
                .url(NetHelper.SERVER_SITE)
                .content(NetHelper.getRequestJsonStr("EvalC/orderEval",new Gson().toJson(reqParamMap)))
                .build()
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                        NetHelper.handleNetError(mContext,null,TAG,e);
                        if(pDialog!=null&&pDialog.isShowing())
                            pDialog.dismiss();
                    }
                    @Override
                    public void onResponse(String response)
                    {
                        Logger.t(TAG).json(response);
                        try
                        {
                            JSONObject jsonResponse = new JSONObject(response);
                            int status=jsonResponse.getInt("status");
                            if (status==0)
                            {
                                ToastUtils.showShort(mContext,"评价成功");
                                if(orderType.equals("quickType")) {
                                    Logger.t(TAG).d("闪付订单");
                                    Intent intent = DQuickPayOrderDetailAct_.intent(mContext).get();
                                    intent.putExtra("orderId", orderId);
                                    mContext.startActivity(intent);
                                } else {
                                    Logger.t(TAG).d("预定订单");
                                    Intent intent = DOrderRecordDetail_.intent(mContext).get();
                                    intent.putExtra("orderId", orderId);
                                    mContext.startActivity(intent);
                                }
                                mContext.finish();
                            }
                            else if (status==1)
                            {
                                String code=jsonResponse.getString("code");
                                if (!ErrorCodeTable.handleErrorCode(code,mContext))
                                     ToastUtils.showShort(mContext, ErrorCodeTable.parseErrorCode(code));
                                Logger.t(TAG).d("错误码为：%s",code);
                            }
                        } catch (JSONException e)
                        {
                            Logger.t(TAG).d(e.getMessage());
                            e.printStackTrace();
                        }
                        finally
                        {
                            if(pDialog!=null&&pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                });
    }*/
    //endregion
    @OnClick({R.id.btn_commit_comment})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_commit_comment:
                if (TextUtils.isEmpty(etResComment.getInputText()))
                {
                    ToastUtils.showShort("评论后才可以提交哦~");
                    return;
                }
                if (resStar.equals("0"))
                {
                    ToastUtils.showShort("评星是必评的哦~");
                    return;
                }
                for (DishBean dishBean : dishLst)
                {
                    if (dishBean.getDishStar().equals("0"))
                    {
                        ToastUtils.showShort("评星是必评的哦~");
                        return;
                    }
                }
                MyResCommentBean resCommentBean = new MyResCommentBean();
                resCommentBean.setrId(resId);
                resCommentBean.setoId(orderId);
                resCommentBean.setEvalContent(etResComment.getInputText());
                resCommentBean.setrStar(resStar);
                resCommentBean.setDishLevel(dishLst);
                if (mPresenter != null)
                {
                    List<String> galleryImgUrlsNoAdd = new ArrayList<String>(imgLst);
                    if (galleryImgUrlsNoAdd.contains(addImg))
                        galleryImgUrlsNoAdd.remove(addImg);
                    if (pDialog != null && !pDialog.isShowing())
                        pDialog.show();
                    mPresenter.postResCommentImgs(galleryImgUrlsNoAdd, resCommentBean);
                }

                break;
            default:
                break;
        }
    }

    private Dialog showContextMenuBox(final ArrayList<String> operUrls, final int position)
    {
        Logger.t(TAG).d("operUrls--->" + operUrls.size());
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
        //当删除到第5张时出现添加按钮
        if (imgLst.size() == 5)
        {
            if (!imgLst.contains(addImg))
                imgLst.add(addImg);
        }
        mResCommentAdapter.notifyDataSetChanged();
    }


    @Override
    protected ImpDOrderCommentView createPresenter()
    {
        return new ImpDOrderCommentView();
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mContext, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void postResCommentTextCallback(String response)
    {
        Logger.t(TAG).json(response);
        ToastUtils.showShort("评价成功");
        if (orderType.equals("quickType"))
        {
            Logger.t(TAG).d("闪付订单");
            Intent intent = new Intent(mContext, DQuickPayOrderDetailAct.class);
            intent.putExtra("orderId", orderId);
            mContext.startActivity(intent);
        }
        else
        {
            Logger.t(TAG).d("预定订单");
            Intent intent = new Intent(mContext, DOrderRecordDetail.class);
            intent.putExtra("orderId", orderId);
            mContext.startActivity(intent);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        mContext.finish();
    }

    @Override
    public void postResCommentPicCallback(JSONObject response)
    {
        ToastUtils.showShort("上传图片失败，请稍后重试!");
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

}
