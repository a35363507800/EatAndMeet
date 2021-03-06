package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;

import com.echoesnet.eatandmeet.activities.DClubOrderCommentAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.MyResCommentBean;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/26
 * @description
 */
public class ImpDClubOrderCommentPre extends BasePresenter<DClubOrderCommentAct>
{

    private final String TAG = ImpDClubOrderCommentPre.class.getSimpleName();

    public void postResCommentText(MyResCommentBean resCommentBean)
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put("evalBean", new Gson().toJson(resCommentBean, MyResCommentBean.class));

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                Logger.t(TAG).d("轰趴评价返回值--> " + response);
                if (getView() != null)
                getView().postResCommentTextCallback(response);

            }
        }, NetInterfaceConstant.HomepartyC_comment, reqParamMap);

    }

    /**
     * 提交评价
     *
     * @param imgUrls        图片的url集合
     * @param resCommentBean
     */
    public void postResCommentImgs(final List<String> imgUrls, final MyResCommentBean resCommentBean)
    {
        //先传图片，结果返回后，根据结果来提交评价
        //没有图片

        if (getView() == null)
        {
            return;
        }
        Activity mContext = (DClubOrderCommentAct) getView();
        final TreeMap<Integer, String> sortMap = new TreeMap<>();
        if (imgUrls.size() == 0)
        {
            //Logger.t(TAG).d("提交评价++++没有图片");
            resCommentBean.setEpUrls("");
            postResCommentText(resCommentBean);
        } else
        {
            CdnHelper.getInstance().setOnCdnFeedbackListener(new IOnCdnFeedbackListener()
            {
                @Override
                public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                {
                    file.delete();
                    Logger.t(TAG).d("成功：" + response.toString());
                    String imgUrl = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;

                    Logger.t(TAG).d("imgUrl:" + imgUrl);
                    sortMap.put(uploadOrder, imgUrl);

                    if (sortMap.size() == imgUrls.size())
                    {
                        List<String> tempImgUrls = new ArrayList<>();
                        for (String s : sortMap.values())
                        {
                            tempImgUrls.add(s);
                        }
                        //Logger.t(TAG).d("提交评价===");
                        String epUrls = CommonUtils.listToStrWishSeparator(tempImgUrls, CommonUtils.SEPARATOR);
                        resCommentBean.setEpUrls(epUrls);
                        postResCommentText(resCommentBean);
                    }
                }

                @Override
                public void onProcess(long len)
                {
                }

                @Override
                public void onFail(JSONObject response, File file)
                {
                    Logger.t(TAG).d("错误：" + response.toString());
                    file.delete();
                    if (getView() != null)
                        getView().postResCommentPicCallback(response);
                }
            });

            for (int i = 0; i < imgUrls.size(); i++)
            {
                final String fileKeyName = CdnHelper.resImage + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".JPEG";
                final String outPutImagePath = NetHelper.getRootDirPath(mContext) + CommonUtils.toMD5(fileKeyName);
                final String filePath = imgUrls.get(i);
                try
                {
                    final File outFile = new File(outPutImagePath);
                    int degree = ImageUtils.readPictureDegree(filePath.replace("/raw",""));
                    ImageUtils.compressBitmap(ImageUtils.getBitmapFromFile((Activity) getView(), filePath), outFile.getPath(), 150,degree);
                    Logger.t(TAG).d("压缩成功" + (outFile.length() / 1024));
                    CdnHelper.getInstance().putFile(outFile, "img", fileKeyName, i);


                } catch (Exception e)
                {
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }
    }

}
