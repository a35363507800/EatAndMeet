package com.echoesnet.eatandmeet.activities.liveplay.managers;

import android.animation.Animator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.OnCompositionLoadedListener;
import com.bumptech.glide.request.RequestOptions;
import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.GlideApp;
import com.echoesnet.eatandmeet.utils.LruCacheBitmapLoader;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by TDJ on 2016/7/6.
 */
public final class LargeGiftManager
{
    private Activity mAct;
    private Window mWindow;
    private RelativeLayout mContainer;
    private final LinkedList<SlidGift.GiftRecord> prepList = new LinkedList<>();

    private ImageView ivGift;
    private LottieAnimationView lottieBigGift;
    private Map<String, ArrayList<String>> map4GiftResPath = new HashMap<>();
    private String hostAvatar = "";
    private AnimEndListener animEndListener;
    private int sW, sH;
    private boolean isShowing = false;

    private boolean waiting = false;

    public void waiting()
    {
        waiting = true;
        prepList.clear();
    }

    public void working()
    {
        waiting = false;
    }


    public LargeGiftManager(Window window, int container, Activity act, String hostAvatar)
    {
        mWindow = window;
        mAct = act;
        this.hostAvatar = hostAvatar;
        prepList.clear();
        map4GiftResPath.clear();

        mContainer = (RelativeLayout) mWindow.findViewById(container);
        if (mContainer == null)
            return;

        ivGift = (ImageView) mContainer.findViewById(R.id.ivBigGift);
        lottieBigGift = (LottieAnimationView) mContainer.findViewById(R.id.lottieBigGift);
        lottieBigGift.useHardwareAcceleration(true);
        lottieBigGift.addAnimatorListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (animEndListener != null)
                    animEndListener.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
        //ad = new AnimationDrawable();

        sW = CommonUtils.getScreenWidth(mAct);
        sH = CommonUtils.getScreenHeight(mAct);
    }

    public void recycle()
    {
        mWindow = null;
        ivGift = null;
        mContainer = null;
        prepList.clear();
        mAct = null;
    }


    public void put(SlidGift.GiftRecord record)
    {
        if (waiting)
        {
            return;
        }

        if (record == null)
            throw new ExceptionInInitializerError("without gift");

        Logger.t("LargeGiftManager").d("isShowing > " + isShowing);
        if (!isShowing)
        {
            Logger.t("LargeGiftManager").d("isShowing > " + isShowing + " | record:" + record
                    .toString());

            doAnim(record);
            return;
        }
        prepList.offer(record);
    }

/*    private void doNext(SlidGift.GiftRecord record)
    {
        if (null != record)
        {
            if (!isShowing)
            {
                doAnim(record);
            }
        }
    }*/

    private SlidGift.GiftRecord giftRecord;
    private JSONArray jsonArrRules = null;
    private HashMap<String, Bitmap> resMap = new HashMap<>();

    private synchronized void doAnim(@NonNull SlidGift.GiftRecord record)
    {
        isShowing = true;
        if (mContainer!= null){
            mAct.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    if (ivGift != null && mContainer != null)
                    {
                        ivGift.setVisibility(View.VISIBLE);
                        mContainer.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

//        LruCacheBitmapLoader.getInstance().runBigGiftAnimation(mAct, "g_" + record.gid, sW, sH,
// 10, ivGift, 45, cb);
        giftRecord = record;
        checkHyperGift();

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                int lostTimes = 0;
                int pIndex = 0;
                boolean lottiePlay = false;
                String giftDir = NetHelper.getRootDirPath(mAct) + "gift" + File.separator +
                        "bigGift" + File.separator + "g_" + giftRecord.gid;
                File script = new File(giftDir + File.separator + "script");
                if (script.exists())
                {
                    lottiePlay = true;
                    doLottieAnim(lottieBigGift, script, new File(giftDir + File.separator +
                            "images"));
                    mAct.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            ivGift.setVisibility(View.GONE);
                            ivGift.setImageBitmap(null);
                        }
                    });
                    animEndListener = new AnimEndListener()
                    {
                        @Override
                        public void onAnimationEnd(Animator animation)
                        {
                            if (prepList.size() > 0)
                                doAnim(prepList.poll());
                            else
                                isShowing = false;

                            lottieBigGift.setVisibility(View.GONE);
                        }
                    };
                }
                while (isShowing && !lottiePlay)
                {
                    String filePath = NetHelper.getRootDirPath(mAct) + "gift" + File.separator +
                            "bigGift" + File.separator + "g_" + giftRecord.gid + File.separator + pIndex;
                    File file = new File(filePath);
//                    暂时保留，这里是用户收到大礼物时本地没有资源的提示代码   ----yqh
//                    String giftFolder = NetHelper.getRootDirPath(mAct) + "gift" + File.separator +
//                            "bigGift" + File.separator + "g_" + giftRecord.gid;
//                    File fileFolder = new File(giftFolder);
//                    if (!fileFolder.exists())
//                    {
//                        mAct.runOnUiThread(() -> ToastUtils.showShort("大礼物资源异常,无法正常展示。"));
//                        break;
//                    }
                    if (file.exists())
                    {
                        lostTimes = 0; //本帧存在，清零最大失帧数
                        final String file4Path = filePath;

                        long start = System.currentTimeMillis();
                        final Bitmap bitMap = (null != jsonArrRules) ? compose(filePath, pIndex)
                                : BitmapFactory.decodeFile(file4Path);
                        long end = System.currentTimeMillis();
                        int interval = 0, loadPigTime = (int) (end - start);
                        if (loadPigTime > 45)
                            interval = 0;
                        else
                            interval = 45 - loadPigTime;
                        if (mAct == null)//当真正播放大礼物时候推出直播间，mAct=null,不做处理会崩溃--wb
                            break;
                        mAct.runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                if (ivGift != null)
                                    ivGift.setImageBitmap(bitMap);
                            }
                        });
                        try
                        {
                            Thread.sleep(interval);
                        } catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        lostTimes++;
                    }
                    //work done;
                    pIndex++; // new next file,
                    if (lostTimes > 20)
                    {
                        if (prepList.size() > 0)
                        {
                            doAnim(prepList.poll());
                            break;
                        }
                        else
                        {
                            isShowing = false;
                            mAct.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    ivGift.setVisibility(View.GONE);
                                    ivGift.setImageBitmap(null);
                                }
                            });
                        }
                    }
                }
            }
        }).start();
    }

    private void checkHyperGift()
    {
        // check rules files;
        String pRules = NetHelper.getRootDirPath(mAct) + "gift" + File.separator + "bigGift" +
                File.separator + "g_" + giftRecord.gid + File.separator + "rules";
        File fRules = new File(pRules);

        jsonArrRules = null;
        Iterator it = resMap.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            resMap.get(key).recycle();
//            resMap.remove(key);
        }
        if (fRules.exists())
        {
//            String objs = "{\"objs\":[" +
//                        "{\"id\":\"hostAvatar\",\"x\":\"0.3627\",\"y\":\"0.4175\",
// \"r\":\"0.112\",\"sframe\":\"203\",\"gframe\":\"480\"," +
//                                    "\"anim\":[" +
//                                            "{\"type\":\"gradient\",\"s\":\"203\",
// \"e\":\"216\",\"sg\":\"0\",\"eg\":\"100\"}," +
//                                            "{\"type\":\"gradient\",\"s\":\"408\",
// \"e\":\"480\",\"sg\":\"100\",\"eg\":\"0\"}" +
//                                        "]}," +
//                        "{\"id\":\"sendAvatar\",\"x\":\"0.4867\",\"y\":\"0.3973\",
// \"r\":\"0.112\",\"sframe\":\"203\",\"gframe\":\"480\"," +
//                                    "\"anim\":[" +
//                                            "{\"type\":\"gradient\",\"s\":\"203\",
// \"e\":\"216\",\"sg\":\"0\",\"eg\":\"100\"}," +
//                                            "{\"type\":\"gradient\",\"s\":\"408\",
// \"e\":\"480\",\"sg\":\"100\",\"eg\":\"0\"}" +
//                                    "]}" +
//                    "]}";
            try
            {
//                JSONObject jsonObj = new JSONObject(objs);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(new
                        FileInputStream(fRules));
                ByteArrayOutputStream memStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = bufferedInputStream.read(buffer)) != -1)
                {
                    memStream.write(buffer, 0, len);
                }
                byte[] data = memStream.toByteArray();
                bufferedInputStream.close();
                memStream.close();
                bufferedInputStream.close();

                JSONObject jsonObj = new JSONObject(new String(data));
                jsonArrRules = jsonObj.optJSONArray("objs");


                if (null != jsonArrRules)
                {
                    Log.i("iiiioooo", "has");
                    final RequestOptions requestOptions = RequestOptions.circleCropTransform();
                    for (int i = 0; i < jsonArrRules.length(); i++)
                    {
                        JSONObject rule = jsonArrRules.optJSONObject(i);
                        switch (rule.optString("id", ""))
                        {
                            case "hostAvatar":
                                new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            resMap.put("hostAvatar", GlideApp.with(mAct).asBitmap()
                                                    .load(hostAvatar)
                                                    .apply(requestOptions).submit().get());

                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                break;
                            case "sendAvatar":
                                new Thread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        try
                                        {
                                            resMap.put("sendAvatar",
                                                    GlideApp.with(EamApplication.getInstance()).asBitmap()
                                                            .load(giftRecord.usrIcon)
                                                            .apply(requestOptions).submit().get());
                                        } catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }).start();
                                break;
                            default:
                                break;
                        }
                    }
                }
                else
                {
                    Log.i("iiiioooo", "null");
                }

            } catch (Exception e)
            {
                Log.i("iiiioooo", "crash");
                e.printStackTrace();
            }
        }
    }


    private Bitmap compose(String imagePath, int frameIndex)
    {
//        if(true){
//            return BitmapFactory.decodeFile(imagePath);
//        }
//        Bitmap imageFrame = BitmapFactory.decodeFile(imagePath);
//        int fWidth = imageFrame.getWidth() * 2;
//        int fHeight = imageFrame.getHeight() * 2;
//        Bitmap bFrame = Bitmap.createBitmap(fWidth, fHeight, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bFrame);
//        canvas.drawBitmap(Bitmap.createBitmap(imageFrame, 0, 0, imageFrame.getWidth(),
// imageFrame.getHeight(), matrixBG, true), 0, 0, null);
//        canvas.drawBitmap(imageFrame, 0, 0, null);


        Bitmap imageFrame = BitmapFactory.decodeFile(imagePath);
        int fWidth = imageFrame.getWidth() * 2;
        int fHeight = imageFrame.getHeight() * 2;
        Matrix matrixBG = new Matrix();
        matrixBG.postScale(2, 2);

        Bitmap bFrame = Bitmap.createBitmap(imageFrame, 0, 0, imageFrame.getWidth(), imageFrame
                .getHeight(), matrixBG, true);
        Canvas canvas = new Canvas(bFrame);
        imageFrame.recycle();


        for (int i = 0; i < jsonArrRules.length(); i++)
        {
            JSONObject rule = jsonArrRules.optJSONObject(i);
            int sf = rule.optInt("sframe", -1);
            int gf = rule.optInt("gframe", -1);
            if (frameIndex >= sf && frameIndex <= gf)
            {
                int x = (int) (fWidth * rule.optDouble("x", 0.0));
                int y = (int) (fHeight * rule.optDouble("y", 0.0));
                int r = (int) (fWidth * rule.optDouble("r", 0.0));
                if (null != resMap.get(rule.optString("id", "")))
                {
                    Bitmap tile = resMap.get(rule.optString("id", ""));
                    int width = tile.getWidth();
                    int height = tile.getHeight();

                    float scaleWidth = ((float) r) / width;
                    float scaleHeight = ((float) r) / height;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    // 根据参数计算渐变
                    Paint animPaint = new Paint();
                    JSONArray jsonArrAnim = rule.optJSONArray("anim");
                    if (jsonArrAnim != null)
                    {
                        for (int j = 0; j < jsonArrAnim.length(); j++)
                        {
                            JSONObject anim = jsonArrAnim.optJSONObject(j);
                            int as = anim.optInt("s", -1);
                            int ae = anim.optInt("e", -1);
                            if (frameIndex >= as && frameIndex <= ae)
                            {
                                switch (anim.optString("type"))
                                {
                                    case "gradient":
                                        int asg = anim.optInt("sg", -1);
                                        int aeg = anim.optInt("eg", -1);
//                                        Log.i("PPPPPAlpha",String.format("inde:%d | ae:%d,%d |
// g:%d,%d",frameindex,as,ae,asg,aeg));
//                                        Log.i("PPPPPAlpha",""+((float)(frameindex-as)/(ae-as)));
//                                        Log.i("PPPPPAlpha",""+(aeg-asg));
//                                        Log.i("PPPPPAlpha",""+(asg+((float)(frameindex-as)/
// (ae-as))*(aeg-asg)));
//                                        Log.i("PPPPPAlpha",
// "---------------------------------------");
                                        animPaint.setAlpha((int) ((asg + ((float) (frameIndex -
                                                as) / (ae - as)) * (aeg - asg)) / 100 * 255));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                    canvas.drawBitmap(Bitmap.createBitmap(tile, 0, 0, width, height, matrix,
                            true), x, y, animPaint);
                }
            }
        }

        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        return bFrame;
    }

/*    private LruCacheBitmapLoader.FrameAnimCallBack cb = new LruCacheBitmapLoader.FrameAnimCallBack()
    {
        @Override
        public void onAnimFinished()
        {
            isShowing = false;
            if (ivGift != null && mContainer != null)
            {
                ivGift.setVisibility(View.GONE);
                mContainer.setVisibility(View.GONE);
                ivGift.setImageBitmap(null);
                SlidGift.GiftRecord record = prepList.poll();
                doNext(record);
            }
        }
    };*/


    private void doLottieAnim(LottieAnimationView lottieAnimationView, File jsonFile, File
            imagesDir)
    {
        lottieAnimationView.setImageAssetDelegate(new ImageAssetDelegate()
        {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset asset)
            {
                BitmapFactory.Options opts = new BitmapFactory.Options();
                //     opts.inDensity = 160;
                String name = imagesDir + File.separator + asset.getFileName().replace(".png", "");

                return BitmapFactory.decodeFile(name, opts);
            }
        });

        try
        {
            LottieComposition.Factory.fromInputStream(mAct, new FileInputStream(jsonFile), new
                    OnCompositionLoadedListener()
                    {
                        @Override
                        public void onCompositionLoaded(LottieComposition composition)
                        {

                            lottieAnimationView.setComposition(composition);
                            lottieAnimationView.playAnimation();
                            lottieAnimationView.setVisibility(View.VISIBLE);
                        }
                    });
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    private interface AnimEndListener
    {
        void onAnimationEnd(Animator animation);
    }

    //缓存取bitmap
    private Bitmap getBitmapById(int resId)
    {
        if (resId == 0)
        {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        final String imageKey = String.valueOf(resId);

        Bitmap bitmap = LruCacheBitmapLoader.getInstance().getBitmapFromMemCache(imageKey);

        if (bitmap == null)
        {
            InputStream ins = mAct.getResources().openRawResource(resId);
            bitmap = BitmapFactory.decodeStream(ins, null, null);

            LruCacheBitmapLoader.getInstance().addBitmapToMemoryCache(String.valueOf(resId), bitmap);
            return bitmap;
        }

        return bitmap;
    }
}
