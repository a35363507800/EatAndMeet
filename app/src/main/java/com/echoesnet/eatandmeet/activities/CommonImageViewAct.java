package com.echoesnet.eatandmeet.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.GlideRequests;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.echoesnet.eatandmeet.views.widgets.PhotoViewPager;
import com.echoesnet.eatandmeet.views.widgets.smoothImageView.MNGestureView;
import com.echoesnet.eatandmeet.views.widgets.smoothImageView.SmoothImageView;
import com.joanzapata.iconify.widget.IconTextView;
import com.mob.tools.gui.ScaledImageView;
import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片浏览器
 */
public class CommonImageViewAct extends BaseActivity
{
    private static final String TAG = CommonImageViewAct.class.getSimpleName();

    @BindView(R.id.vp_show_img_viewpager)
    PhotoViewPager mViewPager;
    // @BindView(R.id.cpi_pager_indicator)
    // CirclePageIndicator circlePageIndicator;
    @BindView(R.id.itv_download)
    IconTextView itvDownload;
    @BindView(R.id.tv_indicator)
    TextView indicatorTv;
    @BindView(R.id.rl_black_bg)
    RelativeLayout rlBlackBg;
    @BindView(R.id.mnGestureView)
    MNGestureView mnGestureView;

    private int currentItem = 0;
    private Activity mAct;
    //    private List<PhotoView> list;
    private SmoothImageView photoView;
    private List<String> mUrls;
    private int locationX, locationY, width, height;
    private boolean isHeadPic;
    private File saveImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        window.setBackgroundDrawableResource(R.color.transparent);
        setContentView(R.layout.act_common_imge_view);
        ButterKnife.bind(this);
        afterViews();
    }

    private void afterViews()
    {
        mAct = this;
        initIntents();
        initData();
        rlBlackBg.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(mAct, R.anim.common_image_black_anim_in);
        rlBlackBg.setAnimation(anim);
        initViewPager(this.mUrls);

        String picUrls = CommonUtils.listToStrWishSeparator(this.mUrls, " | ");
        Logger.t(TAG).d("图片 url" + currentItem + " , " + picUrls);
    }

    private void initIntents()
    {
        currentItem = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_CURRENT_ITEM, 0);
        mUrls = getIntent().getStringArrayListExtra(EamConstant.EAM_SHOW_IMG_URLS);
        locationX = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_LOCATION_X, 0);
        locationY = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_LOCATION_Y, 0);
        width = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_WIDTH, 0);
        height = getIntent().getIntExtra(EamConstant.EAM_SHOW_IMG_HEIGHT, 0);

        isHeadPic = getIntent().getBooleanExtra(EamConstant.EAM_SHOW_IMG_HEAD_PIC, false);
    }

    private void initData()
    {
        if (isHeadPic)
        {
            indicatorTv.setVisibility(View.GONE);
        }
        indicatorTv.setText(String.format("%d/%d", currentItem + 1, mUrls.size()));
    }

    private void initViewPager(final List<String> mUrls)
    {
        if (mUrls == null)
        {
            ToastUtils.showShort("没有可显示的图片");
            return;
        }
        mViewPager.setAdapter(new MyAdapter());

        mViewPager.setPageMargin(30);
        mViewPager.setOffscreenPageLimit(2);
//        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mViewPager.setCurrentItem(currentItem);
        //  if (mUrls.get(currentItem).contains("http://") || mUrls.get(currentItem).contains("https://"))
        itvDownload.setVisibility(View.VISIBLE);
        //  else
        //       itvDownload.setVisibility(View.GONE);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                currentItem = position;
                String s = mUrls.get(position);
                itvDownload.setVisibility(View.VISIBLE);
                indicatorTv.setText(String.format("%d/%d", currentItem + 1, mUrls.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
        mnGestureView.setOnSwipeListener(new MNGestureView.OnSwipeListener()
        {
            @Override
            public void downSwipe()
            {
                finishBrowser();
            }

            @Override
            public void onSwiping(float deltaY)
            {
                indicatorTv.setVisibility(View.GONE);
                itvDownload.setVisibility(View.GONE);
                float mAlpha = 1 - deltaY / 500;
                if (mAlpha < 0.3)
                {
                    mAlpha = 0.3f;
                }
                if (mAlpha > 1)
                {
                    mAlpha = 1;
                }
                rlBlackBg.setAlpha(mAlpha);
            }

            @Override
            public void overSwipe()
            {
                indicatorTv.setVisibility(View.VISIBLE);
                itvDownload.setVisibility(View.VISIBLE);
                rlBlackBg.setAlpha(1);
            }
        });

    }

    private class MyAdapter extends PagerAdapter
    {

        @Override
        public int getCount()
        {
            return mUrls.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View inflate = LayoutInflater.from(mAct).inflate(R.layout.common_image_item, container, false);
            SmoothImageView imageView = (SmoothImageView) inflate.findViewById(R.id.smoothImageView);
            SubsamplingScaleImageView scaleImageView = inflate.findViewById(R.id.scale_image_view);
            RelativeLayout rl_browser_root = (RelativeLayout) inflate.findViewById(R.id.rl_browser_root);
            imageView.setOriginalInfo(width, height, locationX, locationY);
            imageView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener()
            {
                @Override
                public void onViewTap(View view, float x, float y)
                {
                    finishBrowser();
                    Animation anim = AnimationUtils.loadAnimation(mAct, R.anim.common_image_black_anim_out);
                    rlBlackBg.setVisibility(View.GONE);
                    rlBlackBg.setAnimation(anim);
                }
            });
            rl_browser_root.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    finishBrowser();
                    Animation anim = AnimationUtils.loadAnimation(mAct, R.anim.common_image_black_anim_out);
                    rlBlackBg.setVisibility(View.GONE);
                    rlBlackBg.setAnimation(anim);
                }
            });
//            String thumbnailUrl = CommonUtils.getThumbnailImageUrlByUCloud(mUrls.get(position), ImageDisposalType.THUMBNAIL, 1, 50);
            String imgUrl = mUrls.get(position);
            GlideRequests glideRequests = GlideApp.with(EamApplication.getInstance());
            if (imgUrl.endsWith(".gif"))
            {
                scaleImageView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
                glideRequests.asGif()
                        .load(imgUrl)
                        .error(R.drawable.qs_photo)
                        .into(imageView);
            }
            else
            {
                imageView.transformIn();
                loadImage(glideRequests,imgUrl,imageView,scaleImageView);
                scaleImageView.setOnClickListener((v) ->
                {
                    finishBrowser();
                    Animation anim = AnimationUtils.loadAnimation(mAct, R.anim.common_image_black_anim_out);
                    rlBlackBg.setVisibility(View.GONE);
                    rlBlackBg.setAnimation(anim);
                });
            }
            container.addView(inflate);
            return inflate;
        }
    }

    private void loadImage(GlideRequests glideRequests, String imgUrl, ImageView imageView, SubsamplingScaleImageView scaleImageView)
    {
        glideRequests
                .asBitmap()
                .load(imgUrl)
                .error(R.drawable.qs_photo)
                .into(new SimpleTarget<Bitmap>()
                {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition)
                    {
                        int imageHeight = resource.getHeight();
                        int imageWidht = resource.getWidth();
                        int screenHeight = CommonUtils.getScreenHeight(mAct);
                        Logger.t("SmoothImageViewTest").d("imageHeight>>" + imageHeight + "||" + screenHeight);
                        if (imageHeight / imageWidht >= 3)
                        {
                            imageView.setVisibility(View.GONE);
                            scaleImageView.setVisibility(View.VISIBLE);
                            if (!imgUrl.contains("http"))
                            {
                                float ra = (CommonUtils.getScreenWidth(mAct) * 1f) / imageWidht;
                                Logger.t(TAG).d("图片放大" + ra);
                                scaleImageView.setMaxScale(ra);
                                scaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                scaleImageView.setImage(ImageSource.uri(imgUrl), new ImageViewState(ra, new PointF(0, 0), 0));
                            }
                            else
                            {
                                Observable.create(new ObservableOnSubscribe<File>()
                                {
                                    @Override
                                    public void subscribe(ObservableEmitter<File> e) throws Exception
                                    {

                                        String outPath = NetHelper.getRootDirPath(mAct) + NetHelper.DOWNLOAD_IMAGE_FOLDER;
                                        File outFile = new File(outPath);
                                        if (!outFile.exists())
                                            outFile.mkdirs();
                                        outPath = outPath + System.currentTimeMillis();
                                        try
                                        {
                                            FileOutputStream fos = new FileOutputStream(new File(outPath));
                                            resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                            fos.flush();
                                            fos.close();
                                            Log.d(TAG, "图片保存成功||" + outPath);
                                        } catch (FileNotFoundException err)
                                        {
                                            err.printStackTrace();
                                            Log.d(TAG, "图片保存失败||" + err.getMessage());
                                        } catch (IOException err)
                                        {
                                            err.printStackTrace();
                                            Log.d(TAG, "图片保存失败||" + err.getMessage());
                                        }
                                        e.onNext(new File(outPath));
                                    }
                                }).subscribeOn(Schedulers.computation())
                                        .unsubscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Consumer<File>()
                                        {
                                            @Override
                                            public void accept(File file) throws Exception
                                            {
                                                float ra = (CommonUtils.getScreenWidth(mAct) * 1f) / imageWidht;
                                                Logger.t(TAG).d("图片放大" + ra + "|" + imageWidht + "|" + CommonUtils.getScreenWidth(mAct));
                                                scaleImageView.setMaxScale(ra);
                                                scaleImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                                                scaleImageView.setImage(ImageSource.uri(Uri.fromFile(file)),
                                                        new ImageViewState(ra, new PointF(0, 0), 0));
                                            }
                                        });
                            }

                        }
                        else
                        {
                            imageView.setVisibility(View.VISIBLE);
                            scaleImageView.setVisibility(View.GONE);
                            imageView.setImageBitmap(resource);
                        }
                    }
                });
    }

    private void finishBrowser()
    {
        indicatorTv.setVisibility(View.GONE);
        itvDownload.setVisibility(View.GONE);
        rlBlackBg.setAlpha(0);
//        rlBlackBg.setVisibility(View.GONE);
//        Animation anim = AnimationUtils.loadAnimation(mAct, R.anim.common_image_black_anim_out);
//        rlBlackBg.setAnimation(anim);
        finish();
        this.overridePendingTransition(0, R.anim.browser_exit_anim);
    }

    @OnClick({R.id.itv_download})
    public void onViewClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.itv_download:
                ToastUtils.showShort("正在下载...");
                itvDownload.setClickable(false);
                String imageUrl = mUrls.get(currentItem);
                String msg = "图片保存至:\r\n内部存储/EatAndMeet/\r\nEAMImages/";
                if (imageUrl.contains("http://") || imageUrl.contains("https://"))
                {
                    String destFileName = "eam_export_" + System.currentTimeMillis();
                    if (imageUrl.endsWith(".gif"))
                        destFileName = destFileName + ".gif";
                    else
                        destFileName = destFileName + ".jpg";
                    try
                    {
                        OkHttpUtils.get()
                                .url(imageUrl)
                                .build()
                                .execute(new FileCallBack(NetHelper.getRootDirPath(mAct) + NetHelper.DOWNLOAD_IMAGE_FOLDER, destFileName)
                                {
                                    @Override
                                    public void onError(Call call, Exception e)
                                    {
                                        ToastUtils.showShort("下载失败，请重试！");
                                        itvDownload.setClickable(true);
                                    }

                                    @Override
                                    public void onResponse(final File response)
                                    {
                                        if(!mAct.isFinishing())
                                        {
                                            View view = LayoutInflater.from(mAct).inflate(R.layout.toast_ok_bg, null);
                                            IconTextView tvIcon = (IconTextView) view.findViewById(R.id.toast_bg_g);
                                            IconTextView tvContent = (IconTextView) view.findViewById(R.id.toast_content);
                                            tvIcon.setTextSize(60);
                                            tvContent.setText(msg);
                                            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
                                            ToastUtils.showCustomShortSafe(view);
                                            ToastUtils.cancel();
                                        }
//                                        ImageUtils.saveImageToGallery(mAct, response);
                                        // 最后通知图库更新
//                                        mAct.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//                                                Uri.parse("file://" + Environment.getExternalStorageDirectory() + "EatAndMeet")));
                                        mAct.sendBroadcast(
                                                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(response)));
                                        itvDownload.setClickable(true);
                                    }

                                    @Override
                                    public void inProgress(float mProgress, long total)
                                    {
                                        int progress = (int) (mProgress * 100);
                                    }
                                });
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        ToastUtils.showShort("保存图片失败");
                        itvDownload.setClickable(true);
                    }
                }
                else
                {
                    try
                    {
//                        String fileName = System.currentTimeMillis() + "";
//                        NetHelper.getRootDirPath(mAct) + NetHelper.DOWNLOAD_IMAGE_FOLDER + "eam_export_" + fileName + ".jpg"
                        String destFileName = NetHelper.getRootDirPath(mAct) + NetHelper.DOWNLOAD_IMAGE_FOLDER + "eam_export_" + System.currentTimeMillis();
                        if (imageUrl.endsWith(".gif"))
                            destFileName = destFileName + ".gif";
                        else
                            destFileName = destFileName + ".jpg";

                        FileUtils.copyFile(imageUrl.substring(imageUrl.indexOf("/storage"), imageUrl.length()), destFileName);
                        if (!mAct.isFinishing())
                        {
                            View toastView = LayoutInflater.from(mAct).inflate(R.layout.toast_ok_bg, null);
                            IconTextView tvIcon = (IconTextView) toastView.findViewById(R.id.toast_bg_g);
                            IconTextView tvContent = (IconTextView) toastView.findViewById(R.id.toast_content);
                            tvIcon.setTextSize(60);
                            tvContent.setText(msg);
                            ToastUtils.setGravity(Gravity.CENTER, 0, 0);
                            ToastUtils.showCustomShortSafe(toastView);
                            ToastUtils.cancel();
                        }
                        mAct.sendBroadcast(
                                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(destFileName))));
//                        ImageUtils.saveImageToGallery(mAct, new File(destFileName));
                        itvDownload.setClickable(true);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        ToastUtils.showShort("保存图片失败");
                        itvDownload.setClickable(true);
                    }

                }

                indicatorTv.setText(String.format("%d/%d", currentItem + 1, mUrls.size()));
                break;
/*            case R.id.rl_img_container:
                finish();
                break;*/
            default:
                break;
        }

    }


    @Override
    public void onBackPressed()
    {
        finishBrowser();
    }

}
