package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.models.bean.PhotoBean;
import com.echoesnet.eatandmeet.models.bean.PhotoFloder;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.adapters.PhotoFloderAdapter;
import com.echoesnet.eatandmeet.views.adapters.PhotoPickAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PhotoPickActivity extends AppCompatActivity
{
    private static final String TAG = PhotoPickActivity.class.getSimpleName();
    Toolbar toolbar;
    private Button pickEnd;
    private RecyclerView recyclerView;
    private TextView selectFile;
    private Button showSelect;
    View bottomView;
    List<PhotoFloder> floderList = new ArrayList<>();
    List<PhotoBean> imageList = new ArrayList<>();//用于显示
    List<PhotoBean> allImages = new ArrayList<>();//所有图片
    List<PhotoBean> videoList = new ArrayList<>();//所有视频
    List<PhotoBean> imageAndVideos = new ArrayList<>();//所有视频和图片
    PhotoPickAdapter adapter;
    private int mScreenHeight;
    public PhotoFloderAdapter popAdapter;
    public static final int MAX_COUNT = 6;//最大选择数量
    public static final int SINGLE = 0;//单选
    public static final int MULTIPLE = 1;//多选

    public static final String KEY_COUNT = "count";//选择数量
    public static final String KEY_MODE = "mode";//选择方式 单选/多选
    public static final String KEY_FILETYPE = "fileType";//选择文件类型  图片/视频

    public static final int FILETYPE_ALL = 0;//选择文件类型  视频和图片
    public static final int FILETYPE_IMAGE = 1;//选择文件类型  图片
    int photoCount;//最大选择数量
    int check_mode;//图片选择模式
    int fileType = FILETYPE_ALL;
    public static final String EXTRA_RESULT = "select_success";
    public static final String EXTRA_ORiGINAL = "select_original";
    public static final String EXTRA_VIDEO = "select_video";
    public static final String EXTRA_VIDEO_PATH = "select_video_path";
    public static final String EXTRA_THUMBNAIL_PATH = "select_thumbnail_path";
    public static final String EXTRA_SHOW_TYPE = "show_type";
    boolean isoriginal;//是否选中原图
    private String mShowDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mScreenHeight = outMetrics.heightPixels;
        setContentView(R.layout.activity_photo_pick);
        handleIntent();
        initView();
        initDatas();
        setListener();
        getImages();
        initPop();

    }


    /**
     * 选择照片
     *
     * @param activity    选择的界面
     * @param requestCode 请求code
     * @param count       选择数量
     * @param mode        选择模式
     * @param type        选择类型
     */
    public static void pickPhotos(Activity activity, int requestCode, int count, int mode, int type) {
        Intent intent = new Intent(activity, PhotoPickActivity.class);
        intent.putExtra(KEY_COUNT, count);
        intent.putExtra(KEY_MODE, mode);
        intent.putExtra(KEY_FILETYPE, type);
        activity.startActivityForResult(intent, requestCode);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        //选择数量
        photoCount = intent.getIntExtra(KEY_COUNT, MAX_COUNT);
        photoCount = photoCount > MAX_COUNT ? MAX_COUNT : photoCount;
        //选择模式
        check_mode = intent.getIntExtra(KEY_MODE, MULTIPLE);
        //选择类型
        fileType = intent.getIntExtra(KEY_FILETYPE, FILETYPE_ALL);

    }

    private void initDatas() {
        adapter = new PhotoPickAdapter(imageList, PhotoPickActivity.this, getItemImageWidth(), check_mode == SINGLE);
        recyclerView.setAdapter(adapter);

    }

    ArrayList<String> selectList = new ArrayList<>();


    private void setListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectFile.setOnClickListener(clickListener);
        showSelect.setOnClickListener(clickListener);
        pickEnd.setOnClickListener(clickListener);
        adapter.setOnChildClickListener(new PhotoPickAdapter.OnChildClickListener() {
            @Override
            public void onCheckedClickListener(int position, String path) {

                if (check_mode == SINGLE) {//单选图片
                    onSingleImageSelected(path);
                } else {//多选图片
                    if (selectList.contains(path)) {//已经选择该照片
                        selectList.remove(path);
                    } else {//未选择该照片
                        if (selectList.size() == photoCount) {//是否选者最大数量
                            ToastUtils.showShort( "最多只能选择" + photoCount + "张图片");
                            return;
                        }
                        selectList.add(path);
                    }
                    refreshActionStatus();
                    adapter.selectPhoto(selectList, position);
                }
            }

            @Override
            public void onImageClickListener(PhotoBean bean,int position,String path) {
                if (bean.getType() == 1 && selectList.size() > 0)
                {
                    ToastUtils.showShort("不能同时选择图片和视频");
                    return;
                }

                isShow = false; //当pop在显示时点击图片造成返回后 pop显示异常
//                if (check_mode == SINGLE) {//单选图片
//                    onSingleImageSelected(path);
//                } else {
                    if (bean.getType() == 0)
                    {
                        if (TextUtils.isEmpty(mShowDir))
                            position = allImages.indexOf(bean);
                        else
                            position = imageList.indexOf(bean);
                    }
                    else
                        position = videoList.indexOf(bean);
                    PreviewActivity.previewPhoto(PhotoPickActivity.this, bean.getType(),
                            selectList, position < 0 ? 0:position, photoCount, isoriginal, PreviewActivity.REQUEST_PREVIEW,mShowDir);
//                }
            }
        });
        refreshActionStatus();
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.filename) {//选中照片文件夹
                showPop();
            } else if (v.getId() == R.id.show) {//预览选中照片
                if (selectList == null || selectList.size() == 0) return;
                List<PhotoBean> selectPhotos = new ArrayList<>();
                for (String str : selectList) {
                    PhotoBean bean = new PhotoBean();
                    bean.setPath(str);
                    selectPhotos.add(bean);
                }
                PreviewActivity.previewPhoto(PhotoPickActivity.this, -1, selectList, 0,
                        selectList.size(), isoriginal, PreviewActivity.REQUEST_PREVIEW,mShowDir);
            } else if (v.getId() == R.id.pick_end) {//选择完成
                complete(false,"","","");
            }
        }
    };

    /**
     * 单选照片完成
     *
     * @param path
     */
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        selectList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, selectList);
        setResult(RESULT_OK, data);
        finish();
    }

    // 返回已选择的图片数据 视频
    private void complete(Boolean isVideo,String videoPath,String thumbnailPath,String showType) {
        Intent data = new Intent();
        data.putStringArrayListExtra(EXTRA_RESULT, selectList);
        data.putExtra(EXTRA_ORiGINAL, isoriginal);//选择原图
        data.putExtra(EXTRA_VIDEO, isVideo);
        data.putExtra(EXTRA_VIDEO_PATH, videoPath);
        data.putExtra(EXTRA_THUMBNAIL_PATH, thumbnailPath);
        data.putExtra(EXTRA_SHOW_TYPE, showType);
        setResult(RESULT_OK, data);
        if (selectList != null)
            Logger.t(TAG).d("select>>" + selectList.toString());
        finish();
    }

    /**
     * 刷新操作按钮状态
     */
    private void refreshActionStatus() {
        if (selectList.size() > 0) {
            pickEnd.setBackgroundResource(R.drawable.shape_pick_btn);
            showSelect.setTextColor(getResources().getColor(R.color.C0412));
            pickEnd.setClickable(true);
            showSelect.setClickable(true);
            pickEnd.setText("完成(" + selectList.size() + "/" + photoCount + ")");
            showSelect.setText("预览(" + selectList.size() + ")");
        } else {
            pickEnd.setBackgroundResource(R.drawable.shape_pick_btn_unselect);
            showSelect.setTextColor(getResources().getColor(R.color.C0331));
            pickEnd.setClickable(false);
            showSelect.setClickable(false);
            pickEnd.setText("完成");
            showSelect.setText("预览");
        }
    }

    private void initView() {
        selectFile = (TextView) findViewById(R.id.filename);
        toolbar = (Toolbar) findViewById(R.id.pick_toolbar);
        if (fileType == FILETYPE_ALL) {
            toolbar.setTitle("图片和视频");
            selectFile.setText("图片和视频");
        } else {
            toolbar.setTitle("所有图片");
            selectFile.setText("所有图片");
        }
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_photo_back);
        pickEnd = (Button) findViewById(R.id.pick_end);
        recyclerView = (RecyclerView) findViewById(R.id.pick_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, getNumColnums()));

        showSelect = (Button) findViewById(R.id.show);
        bottomView = findViewById(R.id.bottom_container);


    }

    /**
     * 获取Item宽度
     *
     * @return
     */
    private int getItemImageWidth() {
        int cols = getNumColnums();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    /**
     * 根据屏幕宽度与密度计算显示的列数， 最少为三列
     *
     * @return
     */
    private int getNumColnums() {
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        return cols < 3 ? 3 : cols;
    }

    List<String> fileNames = new ArrayList<>();

    /**
     * 加载sd卡照片
     */
    public void getImages() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            ToastUtils.showShort("未发现外部储存");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> mDirPaths = new ArrayList<>();
                String firstImage = null;
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                Uri mVedioUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                Log.i(TAG, mImageUri + "\n" + MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                ContentResolver mContentResolver = getContentResolver();
                // 只查询jpeg和png的图片
                Cursor mCursor;
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
                while (mCursor.moveToNext()) {
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
                            ) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        photoFloder = new PhotoFloder();
                        photoFloder.setDir(dirPath);
                        photoFloder.setCover(path);
                    }
                    if (parentFile.list() == null) continue;

                    int picSize = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String filename) {
                            if (filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".gif")
                                    || filename.endsWith(".jpeg")
                                    ) {
                                PhotoBean photoBean = new PhotoBean();
                                String path = dir.getPath() + File.separator + filename;
                                photoBean.setPath(path);
                                File file = new File(path);
                                if (file.exists())
                                    photoBean.setTime(file.lastModified());//最后操作时间
                                allImages.add(photoBean);
                                return true;
                            }
                            return false;
                        }
                    }).length;
                    photoFloder.setNumber(picSize);
                    fileNames.add(photoFloder.getFileName());
                    floderList.add(photoFloder);
                }
                //读取视频
                mCursor = mContentResolver.query(mVedioUri, null,
                        MediaStore.Video.Media.MIME_TYPE + "=?",
                        new String[]{"video/mp4"},
                        MediaStore.Video.Media.DATE_MODIFIED + " desc");
                loadVideo(mCursor);
                mCursor.close();
                Collections.sort(allImages, new Comparator<PhotoBean>() {
                    @Override
                    public int compare(PhotoBean o1, PhotoBean o2) {
                        if(o1 == null && o2 == null) {
                            return 0;
                        }
                        if(o1 == null) {
                            return -1;
                        }
                        if(o2 == null) {
                            return 1;
                        }
                        if (o1.getTime() == o2.getTime())
                            return 0;
                        return o1.getTime() >  o2.getTime() ? -1 : 1;
                    }
                });
                Collections.sort(videoList, new Comparator<PhotoBean>() {
                    @Override
                    public int compare(PhotoBean o1, PhotoBean o2) {
                        if(o1 == null && o2 == null) {
                            return 0;
                        }
                        if(o1 == null) {
                            return -1;
                        }
                        if(o2 == null) {
                            return 1;
                        }
                        if (o1.getTime() == o2.getTime())
                            return 0;
                        return o1.getTime() >  o2.getTime() ? -1 : 1;
                    }
                });
                mHandler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void loadVideo(Cursor mCursor) {
        PhotoBean bean;
        File file;
        while (mCursor.moveToNext()) {
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
                videoList.add(bean);
            }
            bean = null;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            imageAndVideos.addAll(allImages);
            if (fileType == FILETYPE_ALL) {
                imageAndVideos.addAll(videoList);
                //将视频加入文件列表
                PhotoFloder video = new PhotoFloder();
                video.setFileName("视频");
                video.setNumber(videoList.size());
                if (videoList.size() > 0)
                    video.setCover(videoList.get(0).getPath());
                floderList.add(0, video);
            }
            //将所有图片和视频加入文件列表
            PhotoFloder allFloder = new PhotoFloder();
            allFloder.setFileName(fileType == FILETYPE_ALL ? "图片和视频" : "所有图片");
            allFloder.setCover(allImages.get(0).getPath());
            floderList.add(0, allFloder);
            updateShowName(allFloder);
            Collections.sort(imageAndVideos, new Comparator<PhotoBean>() {
                @Override
                public int compare(PhotoBean o1, PhotoBean o2) {
                    if(o1 == null && o2 == null) {
                        return 0;
                    }
                    if(o1 == null) {
                        return -1;
                    }
                    if(o2 == null) {
                        return 1;
                    }
                    if (o1.getTime() == o2.getTime())
                        return 0;
                    return o1.getTime() >  o2.getTime() ? -1 : 1;
                }
            });
            adapter.refresh(imageAndVideos);
            imageList.addAll(imageAndVideos);


        }
    };

    private void loadImages(PhotoFloder item) {
        if (item == null)
            return;
        mShowDir = item.getDir();
        File file = new File(item.getDir());
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".jpg")
                        || name.endsWith(".png")
                        || name.endsWith(".gif")
                        || name.endsWith(".jpeg")
                        )
                    return true;
                return false;
            }
        });
        PhotoBean photoBean;
        for (File bean : files) {
            photoBean = new PhotoBean();
            photoBean.setPath(item.getDir() + File.separator + bean.getName());
            photoBean.setTime(bean.lastModified());//最后操作时间
            imageList.add(photoBean);
        }
    }


    PopupWindow popupWindow;
    ListView listView;

    private void initPop() {
        popAdapter = new PhotoFloderAdapter(PhotoPickActivity.this, floderList);
        View view = LayoutInflater.from(this).inflate(R.layout.list_dir, null);
        listView = (ListView) view.findViewById(R.id.id_list_dir);
        listView.setAdapter(popAdapter);
        view.setFocusableInTouchMode(true);
        view.setFocusable(true);
        popupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7));
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.anim_popup_dir);
        popAdapter.refresh(floderList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mShowDir = "";
                isShow = !isShow;
                popAdapter.setSelectPosition(position);//设置选择相册
                popupWindow.dismiss();
                imageList.clear();
                PhotoFloder bean = (PhotoFloder) parent.getItemAtPosition(position);
                if (position == 0) {//图片和视频
                    imageList.addAll(imageAndVideos);
                } else if (position == 1 && fileType == FILETYPE_ALL) {//视频
                    imageList.addAll(videoList);
                } else {//加载指定相册图片
                    loadImages(bean);
                }
                adapter.refresh(imageList);
                updateShowName(bean);

            }
        });
    }

    private void updateShowName(PhotoFloder bean) {
        selectFile.setText(bean.getFileName());//设置选中文件名
        toolbar.setTitle(bean.getFileName());//设置title未选中文件名
    }

    boolean isShow;

    private void showPop() {
        if (!isShow) {
            popAdapter.refresh(floderList);
            popupWindow.showAtLocation(bottomView, Gravity.BOTTOM, 0, bottomView.getHeight());
        }
        isShow = !isShow;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case PreviewActivity.REQUEST_PREVIEW:
                    selectList = data.getStringArrayListExtra(PreviewActivity.EXTRA_SELECTS);
                    isoriginal = data.getBooleanExtra(PreviewActivity.EXTRA_ORIGINAL, false);
                    boolean isSuccess = data.getBooleanExtra(PreviewActivity.EXTRA_SUCCESS, false);
                    boolean isVideo = data.getBooleanExtra(PreviewActivity.EXTRA_VIDEO, false);
                    if (isSuccess) {//预览界面点击完成
                        Logger.t(TAG).d("showType>>>>>>" + data.getStringExtra("showType"));
                        complete(isVideo,data.getStringExtra("path"),data.getStringExtra("thumbnailPath"),data.getStringExtra("showType"));
                        return;
                    }
                    adapter.selectPhotos(selectList);
                    refreshActionStatus();
                    break;
            }
        }
    }
}
