package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PhotoBean;
import com.echoesnet.eatandmeet.models.bean.PhotoFloder;
import com.echoesnet.eatandmeet.utils.AnimationHelper;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.MyProgressDialog;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.utils.videoUtil.VideoUtil;
import com.echoesnet.eatandmeet.utils.videoUtil.VideoUtilListener;
import com.echoesnet.eatandmeet.views.adapters.PreviewAdapter;
import com.echoesnet.eatandmeet.views.widgets.ViewPagerFixed;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 图片浏览
 */
public class PreviewActivity extends AppCompatActivity
{

    public static final String TAG = PreviewActivity.class.getSimpleName();
    public static final String EXTRA_SHOW_DIR = "extra_show_dir";
    public static final String EXTRA_SELECTS = "extra_selects";
    public static final String EXTRA_CURRENT_ITEM = "extra_current_item";
    public static final String EXTRA_MAX = "extra_max";
    public static final String EXTRA_SUCCESS = "extra_success";
    public static final String EXTRA_VIDEO = "extra_video";
    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_ORIGINAL = "extra_original";//原图
    public static final int REQUEST_PREVIEW = 100;
    private int mMax = 6;
    @BindView(R.id.viewpager)
    public ViewPagerFixed viewPager;
    @BindView(R.id.original_picture)
    public CheckBox originalCheck;//原图
    @BindView(R.id.select)
    public CheckBox checked;//选中
    @BindView(R.id.appbar)
    public AppBarLayout appBar;//导航栏
    @BindView(R.id.bottom_container)
    public FrameLayout bottomLayout;//底部状态栏
    @BindView(R.id.pick_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.pick_end)
    public Button sendBtn;
    @BindView(R.id.fl_photo_container)
    public FrameLayout flPhotoContainer;
    @BindView(R.id.ll_edit_video)
    public LinearLayout llEditVideo;
    private PreviewAdapter adapter;
    private List<PhotoBean> mList;
    private ArrayList<String> mSelectList;
    private int currentPosition;
    private String currentPath;
    private Activity mAct;
    private boolean isOriginal;//是否选择原图
    private int type = 0;
    private long duration = 0;
    private MyProgressDialog myProgressDialog;
    private String mShowDir;//显示的文件夹


    /**
     * 预览照片
     *
     * @param activity 选择的界面
     */
    public static void previewPhoto(Activity activity, int type,
                                    ArrayList<String> mSelectList, int currentPosition, int maxCount, boolean isOriginal, int requestCode, String showDir)
    {
        Intent intent = new Intent(activity, PreviewActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putStringArrayListExtra(EXTRA_SELECTS, mSelectList);
        intent.putExtra(EXTRA_CURRENT_ITEM, currentPosition);
        intent.putExtra(EXTRA_MAX, maxCount);
        intent.putExtra(EXTRA_SHOW_DIR, showDir);
        intent.putExtra(EXTRA_ORIGINAL, isOriginal);
        activity.startActivityForResult(intent, requestCode);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mAct = this;
        ButterKnife.bind(this);
        try
        {
            FFmpeg.getInstance(mAct).loadBinary(new LoadBinaryResponseHandler());
        } catch (FFmpegNotSupportedException e)
        {
            e.printStackTrace();
        }
        handleIntent();
        initView();
        getImages();
        setListener();
    }

    boolean isVisiable = true;

    private void setListener()
    {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {

                PhotoBean bean = mList.get(position);
                currentPosition = position;
                currentPath = bean.getPath();
                duration = bean.getDurantion();
                updateState(currentPath);//更新界面状态
            }


            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onBackPressed();
            }
        });
    }

    @OnClick({R.id.tv_edit_video, R.id.pick_end, R.id.select, R.id.original_picture})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.tv_edit_video:
                if (TextUtils.isEmpty(currentPath))
                    break;
                Intent intent = new Intent(PreviewActivity.this, CutTimeActivity.class);
                intent.putExtra("path", currentPath);
                startActivityForResult(intent, EamConstant.EAM_OPEN_CUT_TIME);
                break;
            case R.id.pick_end:
                selectFinish(true);
                break;
            case R.id.select:
                if (mSelectList.contains(currentPath))
                {
                    mSelectList.remove(currentPath);
                } else
                {
                    if (mSelectList.size() == mMax)
                    {
                        ToastUtils.showShort("最多只能选择" + mMax + "张图片");
                        checked.setChecked(false);//将选中状态设为未选择 如不设置checkbox将会被选中
                        return;
                    }
                    mSelectList.add(currentPath);
                }
                updateState(currentPath);
                break;
            case R.id.original_picture:
                updateOriginalSize();
                break;

        }
    }

    private void updateCheke(String currentPath)
    {
        if (mSelectList.contains(currentPath))
        {
            checked.setChecked(true);
        } else
        {
            checked.setChecked(false);
        }
    }

    /**
     * 更新确定按钮状态
     */
    private void updateSendBtn()
    {
        if (type == 1)
            bottomLayout.setVisibility(duration > 11000 ? View.VISIBLE : View.GONE);
        if (mSelectList.size() > 0 || (type == 1 && duration < 11000))
        {
            sendBtn.setClickable(true);
            sendBtn.setBackgroundResource(R.drawable.shape_pick_btn);
            sendBtn.setText(type == 1 ? "完成" : "完成(" + mSelectList.size() + "/" + mMax + ")");
        } else
        {
            sendBtn.setBackgroundResource(R.drawable.shape_pick_btn_unselect);
            sendBtn.setClickable(false);
            sendBtn.setText("完成");
        }
        updateOriginalSize();
    }


    /**
     * 更新原图大小
     */
    private void updateOriginalSize()
    {
        long totail = 0;
        if (mSelectList.size() > 0)
        {
            for (String path : mSelectList)
            {
                totail += FileUtils.getFileSize(path);
            }
            originalCheck.setText("原图(" + FileUtils.formatSize(totail) + ")");
        } else
        {
            originalCheck.setText("原图");
        }
    }


    private void initData()
    {
        if (mList == null) mList = new ArrayList<>();
        adapter = new PreviewAdapter(this, mList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition);
        viewPager.setOffscreenPageLimit(5);
        if (mList.size() > currentPosition)
        {
            duration = mList.get(currentPosition).getDurantion();
            currentPath = mList.get(currentPosition).getPath();
//        //初始播放状态
//        if (mList.get(currentPosition).getType() == 1)
//        {
//            originalCheck.setVisibility(View.GONE);
//        } else
//        {
//            originalCheck.setVisibility(View.VISIBLE);
//        }
            adapter.setOnClickListener(new PreviewAdapter.OnClickListener()
            {
                @Override
                public void onClickListener(int position)
                {
                    //影藏导航栏
                    //影藏底部状态
                    //影藏toolbar
                    if (isVisiable)
                    {
                        appBar.setVisibility(View.GONE);
                        appBar.setAnimation(AnimationHelper.moveVertical(0.0f, -1.0f));
                        if (type == 0 || duration > 11000)
                            bottomLayout.setAnimation(AnimationHelper.moveVertical(0.0f, 1.0f));
                        bottomLayout.setVisibility(View.GONE);
                    } else
                    {
                        appBar.setVisibility(View.VISIBLE);
                        appBar.setAnimation(AnimationHelper.moveVertical(-1.0f, 0.0f));
                        if (!bottomLayout.isShown() && (type == 0 || duration > 11000))
                        {
                            bottomLayout.setAnimation(AnimationHelper.moveVertical(1.0f, 0.0f));
                            bottomLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    isVisiable = !isVisiable;
                }

                @Override
                public void playVideo(PhotoBean photoBean, int position)
                {
                    Intent intent = new Intent(mAct, TrendsPlayVideoAct.class);
                    intent.putExtra("url", photoBean.getPath());
                    startActivity(intent);
                }
            });
            updateState(currentPath);
        }
    }

    /**
     * 更新界面状态
     *
     * @param currentPath
     */
    private void updateState(String currentPath)
    {
        updateActionbar();//更新图片位置
        updateSendBtn();
        updateCheke(currentPath);//更新选择状态
    }

    private void handleIntent()
    {
        Intent intent = getIntent();
        mSelectList = intent.getStringArrayListExtra(EXTRA_SELECTS);
        type = intent.getIntExtra(EXTRA_TYPE, 0);
        mMax = intent.getIntExtra(EXTRA_MAX, 9);
        mShowDir = intent.getStringExtra(EXTRA_SHOW_DIR);
        isOriginal = intent.getBooleanExtra(EXTRA_ORIGINAL, false);
        currentPosition = intent.getIntExtra(EXTRA_CURRENT_ITEM, 0);
    }

    private void initView()
    {
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_photo_back);
        originalCheck.setChecked(isOriginal);
        flPhotoContainer.setVisibility(type == 1 ? View.GONE : View.VISIBLE);
        llEditVideo.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
    }

    private void updateActionbar()
    {
        toolbar.setTitle((currentPosition + 1) + "/" + mList.size());

    }

    @Override
    public void onBackPressed()
    {
        selectFinish(false);
    }

    private void selectFinish(boolean isSuccess)
    {
        Intent intent = getIntent();
        intent.putExtra(EXTRA_SUCCESS, isSuccess);
        if (type == 1 && isSuccess)
        {
            File file = new File(NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO);
            if (!file.exists())
                file.mkdirs();
            float rotation = 0;
            String thumbnailPath = NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO + System.currentTimeMillis();
            String videoOutPath = NetHelper.getRootDirPath(mAct) + NetHelper.CUT_VIDEO + System.currentTimeMillis() + ".mp4";
            if (myProgressDialog == null)
                myProgressDialog = new MyProgressDialog()
                        .setOutTime(false)
                        .buildDialog(mAct).setDescription("请稍后....");
            myProgressDialog.setCancelable(false);
            myProgressDialog.show();
            String duration = null;
            String end = null;
            SimpleDateFormat simpleDateFormat = null;
            try
            {
                MediaMetadataRetriever retr = new MediaMetadataRetriever();
                retr.setDataSource(currentPath);
                duration = retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                rotation = Float.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
                Logger.t(TAG).d("duration>>" + duration);
                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
                end = simpleDateFormat.format(new Date(Long.valueOf(duration)));
            } catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            float finalRotation = rotation;
            VideoUtil.cutVideo(mAct, "00:00:00", end, currentPath, videoOutPath, new VideoUtilListener()
            {
                @Override
                public void start()
                {

                }

                @Override
                public void complete(String outPath, String err)
                {
                    VideoUtil.getVideoMediaThumbnail(mAct, 0, currentPath, thumbnailPath, finalRotation,true,
                            new VideoUtilListener()
                            {
                                @Override
                                public void start()
                                {

                                }

                                @Override
                                public void complete(String outPath, String err)
                                {
                                    myProgressDialog.dismiss();
                                    File outFile = new File(videoOutPath);
                                    intent.putExtra(EXTRA_VIDEO, true);
                                    intent.putExtra("path", outFile.exists()?videoOutPath:currentPath);
                                    intent.putExtra("thumbnailPath", outPath);
                                    intent.putExtra("showType", VideoUtil.getVideoOrientation(currentPath));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                }
                            });
                }
            });


        } else
        {
            intent.putExtra(EXTRA_ORIGINAL, originalCheck.isChecked());
            intent.putStringArrayListExtra(EXTRA_SELECTS, mSelectList);
            setResult(RESULT_OK, intent);
            super.onBackPressed();
        }

    }


    List<String> fileNames = new ArrayList<>();

    /**
     * 加载sd卡照片
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            ToastUtils.showShort("未发现外部储存");
            return;
        }
        if (mList == null)
            mList = new ArrayList<>();
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (type != -1)
                {
                    if (!TextUtils.isEmpty(mShowDir))
                    {
                        loadImages(mShowDir);
                    } else
                    {
                        List<String> mDirPaths = new ArrayList<>();
                        String firstImage = null;
                        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        Uri mVedioUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        Log.i(TAG, mImageUri + "\n" + MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        ContentResolver mContentResolver = getContentResolver();
                        // 只查询jpeg和png的图片
                        Cursor mCursor;
                        if (type == 0)
                        {
                            //读取图片
                            mCursor = mContentResolver.query(mImageUri, null,
                                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                                            + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                            + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                            + MediaStore.Video.Media.MIME_TYPE + "=?",
                                    new String[]{"image/jpeg", "image/png"
                                            , "image/jpg", "image/gif"
                                    },
                                    MediaStore.Images.Media.DATE_MODIFIED + " desc");
                            Log.i(TAG, mCursor.getCount() + "");
                            while (mCursor.moveToNext())
                            {
                                // 获取图片的路径
                                String path = mCursor.getString(mCursor
                                        .getColumnIndex(MediaStore.Images.Media.DATA));
                                if (firstImage == null)
                                    firstImage = path;
// 获取该图片的父路径名
                                final File parentFile = new File(path).getParentFile();
                                if (parentFile == null)
                                    continue;
                                String dirPath = parentFile.getAbsolutePath();
                                PhotoFloder photoFloder = null;
                                // 防止多次扫描同一个文件夹
                                if (mDirPaths.contains(dirPath)
                                        || fileNames.contains(parentFile.getName())
                                        )
                                {
                                    continue;
                                } else
                                {
                                    mDirPaths.add(dirPath);
                                    // 初始化imageFloder
                                    photoFloder = new PhotoFloder();
                                    photoFloder.setDir(dirPath);
                                    photoFloder.setCover(path);
                                }
                                if (parentFile.list() == null) continue;

                                int picSize = parentFile.list(new FilenameFilter()
                                {
                                    @Override
                                    public boolean accept(File dir, String filename)
                                    {
                                        if (filename.endsWith(".jpg")
                                                || filename.endsWith(".png")
                                                || filename.endsWith(".gif")
                                                || filename.endsWith(".jpeg")
                                                )
                                        {
                                            PhotoBean photoBean = new PhotoBean();
                                            String path = dir.getPath() + File.separator + filename;
                                            photoBean.setPath(path);
                                            File file = new File(path);
                                            if (file.exists())
                                                photoBean.setTime(file.lastModified());//最后操作时间
                                            mList.add(photoBean);
                                            return true;
                                        }
                                        return false;
                                    }
                                }).length;
                                photoFloder.setNumber(picSize);
                                fileNames.add(photoFloder.getFileName());
//                    floderList.add(photoFloder);
                            }
                        } else
                        {
                            //读取视频
                            mCursor = mContentResolver.query(mVedioUri, null,
                                    MediaStore.Video.Media.MIME_TYPE + "=?",
                                    new String[]{"video/mp4"},
                                    MediaStore.Video.Media.DATE_MODIFIED + " desc");
                            PhotoBean bean;
                            File file;
                            while (mCursor.moveToNext())
                            {
                                bean = new PhotoBean();
                                // 获取图片的路径
                                String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                if (path.endsWith(".mp4"))
                                {
                                    long duration = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                                    bean.setPath(path);
                                    bean.setDurantion(duration);
                                    file = new File(path);
                                    if (file.exists())
                                        bean.setTime(file.lastModified());
                                    mList.add(bean);
                                }
                                bean = null;
                            }
                        }
                        mCursor.close();
                        Collections.sort(mList, new Comparator<PhotoBean>()
                        {
                            @Override
                            public int compare(PhotoBean o1, PhotoBean o2)
                            {
                                if (o1 == null && o2 == null)
                                {
                                    return 0;
                                }
                                if (o1 == null)
                                {
                                    return -1;
                                }
                                if (o2 == null)
                                {
                                    return 1;
                                }
                                if (o1.getTime() == o2.getTime())
                                    return 0;
                                return o1.getTime() > o2.getTime() ? -1 : 1;
                            }
                        });
                    }
                } else
                {
                    for (String s : mSelectList)
                    {
                        PhotoBean photoBean = new PhotoBean();
                        photoBean.setPath(s);
                        mList.add(photoBean);
                    }
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initData();
                    }
                });
            }
        }).start();
    }

    /**
     * 加载指定文件夹文件
     *
     * @param showDir
     */
    private void loadImages(String showDir)
    {
        File file = new File(showDir);
        File[] files = file.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                if (name.endsWith(".jpg")
                        || name.endsWith(".png")
                        || name.endsWith(".gif")
                        || name.endsWith(".jpeg")
                        )
                    return true;
                return false;
            }
        });
        if (mList != null)
            mList.clear();
        PhotoBean photoBean;
        for (File bean : files)
        {
            photoBean = new PhotoBean();
            photoBean.setPath(showDir + File.separator + bean.getName());
            photoBean.setTime(bean.lastModified());//最后操作时间
            mList.add(photoBean);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case EamConstant.EAM_OPEN_CUT_TIME:
                if (resultCode == RESULT_OK)
                {
                    data.putExtra(EXTRA_SUCCESS, true);
                    data.putExtra(EXTRA_VIDEO, true);
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        if (myProgressDialog != null && myProgressDialog.isShowing())
            myProgressDialog.dismiss();
        super.onDestroy();
    }
}
