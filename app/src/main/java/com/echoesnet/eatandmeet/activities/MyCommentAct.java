package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.CommentBean;
import com.echoesnet.eatandmeet.models.bean.CommentInfoBean;
import com.echoesnet.eatandmeet.presenters.ImpIMyCommentView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyCommentView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.ErrorCodeTable;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.CommentAdapter;
import com.echoesnet.eatandmeet.views.adapters.ResCommentImgsAdapter;
import com.echoesnet.eatandmeet.views.widgets.DialogUtil;
import com.echoesnet.eatandmeet.views.widgets.EditViewWithCharIndicate;
import com.echoesnet.eatandmeet.views.widgets.GenderView;
import com.echoesnet.eatandmeet.views.widgets.LevelHeaderView;
import com.echoesnet.eatandmeet.views.widgets.LevelView;
import com.echoesnet.eatandmeet.views.widgets.TopBar.ITopbarClickListener;
import com.echoesnet.eatandmeet.views.widgets.TopBar.TopBar;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import okhttp3.Call;

//import com.zfdang.multiple_images_selector.SelectorSettings;


/**
 * 我的点评
 */
public class MyCommentAct extends MVPBaseActivity<IMyCommentView, ImpIMyCommentView> implements IMyCommentView
{
    private static final String TAG = MyCommentAct.class.getSimpleName();
    private final int TO_COMPLAINT_USER = 101;
    public static final int TO_COMPLAINT_USER_OK = 102;
    @BindView(R.id.top_bar)
    TopBar topBar;

    @BindView(R.id.btnComment)
    Button btnComment;
    @BindView(R.id.rv_comment_imgs)
    RecyclerView rcvCommentImgs;
    @BindView(R.id.evw_input_feedback)
    EditViewWithCharIndicate ewciFeedBack;
    @BindView(R.id.lv_comment)
    ListView lvComment;


    @BindView(R.id.lhvAvatar)
    LevelHeaderView lhvAvatar;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.gvGender)
    GenderView gvGender;
    @BindView(R.id.lvLevel)
    LevelView lvLevel;


    private Activity mAct;
    private Dialog pDialog;
    private String luId = "";
    private String streamId = "";

    private List<CommentBean> commentList;
    private HashMap<String, Boolean> chosenGroup;
    private CommentAdapter adapter;
    private List<CommentBean> list;

    /*private int ivLvImg[] = {R.drawable.lv_0, R.drawable.lv_1, R.drawable.lv_2, R.drawable.lv_3,
            R.drawable.lv_4, R.drawable.lv_5, R.drawable.lv_6, R.drawable.lv_7,};*/

    //要上传图片的Url
    private List<String> imgLst;
    private String addImg = "android.resource://com.echoesnet.eatandmeet/drawable/add_comment";
    ArrayMap<Integer, String> map = new ArrayMap<Integer, String>();
    private ResCommentImgsAdapter mResCommentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_comment);
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
    }

    private void afterViews()
    {
        mAct = this;
        btnComment.requestFocus();
        luId = getIntent().getStringExtra("luid");
        streamId = getIntent().getStringExtra("streamId");
        if (mPresenter != null)
            mPresenter.getCommentText(streamId);
        topBar.setTitle(getResources().getString(R.string.act_Comment_Title_Text));
        topBar.getRightButton().setVisibility(View.VISIBLE);
        topBar.getRightButton().setText("投诉");
        topBar.setOnClickListener(new ITopbarClickListener()
        {
            @Override
            public void leftClick(View view)
            {
                mAct.finish();
            }

            @Override
            public void left2Click(View view)
            {
            }

            @Override
            public void rightClick(View view)
            {
                Intent intent = new Intent(mAct, ComplaintUserAct.class);
                intent.putExtra("streamId", streamId);
                intent.putExtra("luId", luId);
                startActivityForResult(intent, TO_COMPLAINT_USER);
            }
        });

        commentList = new ArrayList<>();
        list = new ArrayList<>();
        chosenGroup = new HashMap<>();

        pDialog = DialogUtil.getCommonDialog(mAct, "正在提交...");
        pDialog.setCancelable(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mAct);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rcvCommentImgs.setLayoutManager(linearLayoutManager);
        imgLst = new ArrayList<>();
        imgLst.add(addImg);
        mResCommentAdapter = new ResCommentImgsAdapter(mAct, imgLst);
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
                            .start(mAct, EamConstant.EAM_OPEN_IMAGE_PICKER);
                    //ImageUtils.openImagePicker(mAct, maxImg, 100 * 1024, true);
                } else
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
                    showContextMenuBox(finalImgLst, position, view);
                }
            }
        });
        rcvCommentImgs.setAdapter(mResCommentAdapter);
    }


    private Dialog showContextMenuBox(final ArrayList<String> operUrls, final int position, final View view)
    {
        Logger.t(TAG).d("operUrls--->" + operUrls.size());
        final Dialog dialog = new Dialog(mAct, R.style.AlertDialogStyle);
        View contentView = LayoutInflater.from(mAct).inflate(R.layout.dialog_img_operation, null);
        dialog.setContentView(contentView);
        TextView tvCheckImg = (TextView) contentView.findViewById(R.id.tv_check_img);
        TextView tvDeleteImg = (TextView) contentView.findViewById(R.id.tv_delete_img);
        tvCheckImg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                CommonUtils.showImageBrowser(mAct, operUrls, position, view);
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
        lp.width = CommonUtils.dp2px(mAct, 250);
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
    protected ImpIMyCommentView createPresenter()
    {
        return new ImpIMyCommentView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logger.t(TAG).d("requestCode>>" + requestCode + "resultCode>>>" + resultCode);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_IMAGE_PICKER:
                if (resultCode == RESULT_OK)
                {
                    ArrayList<String> mResults = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
//                    String flag = data.getStringExtra("isOpenCamera");
//                    Logger.t(TAG).d("flag:" + flag);
//                    Logger.t(TAG).d("Build.BRAND:" + Build.BRAND);
//                    Logger.t(TAG).d("Build.MANUFACTURER:" + Build.MANUFACTURER);
//                    Logger.t(TAG).d("os.name:" + System.getProperty("os.name"));
//                    Logger.t(TAG).d("os.version:" + System.getProperty("os.version"));
                    /*if(((Build.MANUFACTURER).toLowerCase()).contains("samsung"))
                    {
                        for (String s : mResults)
                        {
                            Logger.t(TAG).d("修改前path"+s);
                            Uri hostUri = Uri.fromFile(new File(s));
                            Logger.t(TAG).d("hostUri.getPath():"+hostUri.getPath());
                            int degree = ImageUtils.readPictureDegree(hostUri.getPath());
                            Logger.t(TAG).d("照片旋转角度--> " + degree);
                            Bitmap bitmap = ImageUtils.rotaingImageView2(degree, hostUri, mAct);
                            ImageUtils.convertImageToFile(bitmap,s,100);
                            mResults.remove(s);
                            mResults.add(hostUri.getPath());
                        }
                    }*/
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
            case TO_COMPLAINT_USER:
                if (resultCode == TO_COMPLAINT_USER_OK)
                {  //完成投诉
                    finish();
                }
                break;
        }
    }

    private void beginCrop(Uri source)
    {
        String fileName = "a_" + SharePreUtils.getUserMobile(mAct) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
        Logger.t(TAG).d("文件名为：" + fileName);
        Uri destination = Uri.fromFile(new File(getCacheDir(), CommonUtils.toMD5(fileName)));
        Crop.of(source, destination).asSquare().withMaxSize(400, 400).start(this);
    }

    @Override
    public void requestNetError(Call call, Exception e, String exceptSource)
    {
        NetHelper.handleNetError(mAct, null, exceptSource, e);
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void postResCommentTextCallback(String response)
    {
        Logger.t(TAG).json(response);
        ToastUtils.showShort("评价成功");
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
        mAct.finish();


    }

    @Override
    public void postResCommentPicCallback(String response)
    {
        ToastUtils.showShort("上传图片失败，请稍后重试!");
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void getCommentTextCallback(String response)
    {

        Logger.t(TAG).d("获取评价内容--> " + response);
        if (!TextUtils.isEmpty(response))
        {
            setDataToUI(response);
        }
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();


    }

    public void setListViewHeightBasedOnChildren(ListView listView)
    {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
        {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++)
        {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @OnClick({R.id.btnComment})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnComment:
                /*if (TextUtils.isEmpty(ewciFeedBack.getInputText()))
                {
                    ToastUtils.showShort(mAct,"描述问题和意见不能为空");
                    return;
                }
                MyResCommentBean resCommentBean =new MyResCommentBean();
                resCommentBean.setEvalContent(ewciFeedBack.getInputText());
                postResCommentImgs(imgLst, resCommentBean);*/

                /*if (TextUtils.isEmpty(ewciFeedBack.getInputText())) {
                    ToastUtils.showShort(mAct, "描述问题和意见不能为空");
                    return;
                }*/
                if (list.size() != commentList.size())
                {
                    ToastUtils.showShort( "评价选项必须全选");
                    return;
                }
                CommentInfoBean commentInfoBean = new CommentInfoBean();
                commentInfoBean.setCommentStr(ewciFeedBack.getInputText());
                commentInfoBean.setCommentBeanList(list);
                if (mPresenter != null)
                {
                    List<String> galleryImgUrlsNoAdd = new ArrayList<String>(imgLst);
                    if (galleryImgUrlsNoAdd.contains(addImg))
                        galleryImgUrlsNoAdd.remove(addImg);
                    if (pDialog != null && !pDialog.isShowing())
                        pDialog.show();
                    mPresenter.postResCommentImgs(galleryImgUrlsNoAdd, commentInfoBean, streamId);
                }

                for (CommentBean bean : list)
                {
                    Logger.t(TAG).d("最终--> " + bean.getLabelStr() + "-" + bean.isState());
                }
                Logger.t(TAG).d("最终1--> " + list.size() + " , " + list.toString() + " , " + commentList.size());

                Logger.t(TAG).d("格式化--> " + new Gson().toJson(list));

                break;
        }
    }

    private void setDataToUI(String response)
    {
        try
        {
            JSONObject jsonResponse = new JSONObject(response);
            // 被评价的主播头像

            String url = jsonResponse.getString("url");
            String userLevel = jsonResponse.getString("userLevel");
            String level = jsonResponse.getString("level");

            if (!TextUtils.isEmpty(url))
            {

                lhvAvatar.setLiveState(false);
                lhvAvatar.setHeadImageByUrl(url);
//                lhvAvatar.setLevel(userLevel); // 头像不要圈儿
                lvLevel.setLevel(level, LevelView.HOST);
            }

            // 被评价的主播等级
//            String level = jsonResponse.getString("level");
//            if (!TextUtils.isEmpty(level))
//            {
//                ivLv.setImageResource(ivLvImg[Integer.parseInt(level)]);
//            }
            // 被评价的主播昵称
            String nicName = jsonResponse.getString("nicName");
            if (!TextUtils.isEmpty(nicName))
            {
                tvUserName.setText(nicName);
            }
            // 被评价的主播性别
            String sex = jsonResponse.getString("sex");
            String age = jsonResponse.getString("age");


            if (!TextUtils.isEmpty(sex) && !TextUtils.isEmpty(age))
            {
//                if (sex.equals("男"))
//                {
//                    gvGender.setBackgroundResource(R.drawable.shape_round_2corner_15_mc7);
//                    .setText(String.format("%s %s", "{eam-e950}",age));
//                }
//                else
//                {
//                    gvGender.setBackgroundResource(R.drawable.shape_round_2corner_15_mc5);
//                    gvGender.setText(String.format("%s %s", "{eam-e94f}",age));
//                }

                gvGender.setSex(age, sex);
            }
            // 被评价的主播年龄

//            if (!TextUtils.isEmpty(age))
//            {
//              //  tvAge.setText(age + "岁");
//            }

            // 评价内容json串
            String content = jsonResponse.getString("content");
            Logger.t(TAG).d("content--> " + content);
            if (!TextUtils.isEmpty(content))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(content);
                    Iterator it = jsonObject.keys();
                    while (it.hasNext())
                    {
                        String key = (String) it.next();
                        String value = jsonObject.getString(key);
                        commentList.add(new CommentBean(value));
                    }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                for (int i = 0; i < commentList.size(); i++)
                {
                    chosenGroup.put(commentList.get(i).getLabelStr(), false);
                }

                adapter = new CommentAdapter(commentList, chosenGroup, mAct);
                adapter.setChosenListener(new CommentAdapter.ChosenListener()
                {

                    @Override
                    public void onChosenCallback(CommentBean bean, boolean value)
                    {
                        for (int i = 0; i < list.size(); i++)
                        {
                            if (list.get(i).getLabelStr().equals(bean.getLabelStr()))
                            {
                                list.remove(i);
                            }
                        }
                        list.add(bean);
                        adapter.notifyDataSetChanged();
                    }
                });

                lvComment.setAdapter(adapter);
                setListViewHeightBasedOnChildren(lvComment);
            }
            // 被评价的主播图标
            String levelicon = jsonResponse.getString("levelicon");
            if (!TextUtils.isEmpty(levelicon))
            {
                /*GlideApp.with(EamApplication.getInstance())
                        .load(levelicon)
                        .asBitmap()
                        .placeholder(R.drawable.userhead)
                        .error(R.drawable.userhead)
                        .into(ivLevelIcon);*/
            }
            // 被评价的主播UID
            //String anchor = jsonResponse.getString("anchor");

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


}
