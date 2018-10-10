package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.Context;

import com.echoesnet.eatandmeet.activities.MyCommentAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.CommentInfoBean;
import com.echoesnet.eatandmeet.models.bean.ConstCodeTable;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyCommentView;
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
 * Created by Administrator on 2017/1/6.
 */

public class ImpIMyCommentView extends BasePresenter<IMyCommentView>
{
    private final String TAG = ImpIMyCommentView.class.getSimpleName();

    public void getCommentText(String streamId)
    {
        final IMyCommentView commentView = getView();
        if (commentView == null)
        {
            return;
        }
        Context mContext = (MyCommentAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.streamId, streamId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (commentView != null)
                    commentView.getCommentTextCallback(response);
            }
        },NetInterfaceConstant.ReceiveC_evaluateContent,reqParamMap);
    }

    public void postResCommentText(CommentInfoBean resCommentBean, String streamId)
    {
        final IMyCommentView commentView = getView();
        if (commentView == null)
        {
            return;
        }
        Context mContext = (MyCommentAct) getView();
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(mContext);
        reqParamMap.put(ConstCodeTable.context, new Gson().toJson(resCommentBean, CommentInfoBean.class));
        reqParamMap.put(ConstCodeTable.streamId, streamId);

        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (commentView != null)
                    commentView.postResCommentTextCallback(response);
            }
        },NetInterfaceConstant.ReceiveC_evaluateReceive,reqParamMap);
    }

    /**
     * 提交评价
     *
     * @param imgUrls        图片的url集合
     * @param resCommentBean
     */
    public void postResCommentImgs(final List<String> imgUrls, final CommentInfoBean resCommentBean, final String streamId)
    {
        //先传图片，结果返回后，根据结果来提交评价
        //没有图片
        final IMyCommentView commentView = getView();
        if (commentView == null)
        {
            return;
        }
        Activity mContext = (MyCommentAct) getView();
        final TreeMap<Integer, String> sortMap = new TreeMap<>();
        if (imgUrls.size() == 0)
        {
            //Logger.t(TAG).d("提交评价++++没有图片");
            resCommentBean.setEpUrls("");
            postResCommentText(resCommentBean, streamId);
        }
        else
        {
            CdnHelper.getInstance().setOnCdnFeedbackListener(new IOnCdnFeedbackListener()
            {
                @Override
                public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                {
                    Logger.t(TAG).d("成功：" + response.toString());
                    file.delete();
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
                        postResCommentText(resCommentBean, streamId);
                    }
                }

                @Override
                public void onProcess(long len)
                {
                }

                @Override
                public void onFail(JSONObject response,File file)
                {
                    file.delete();
                    Logger.t(TAG).d("错误：" + response.toString());
                    commentView.postResCommentPicCallback(response.toString());
                }
            });

            for (int i = 0; i < imgUrls.size(); i++)
            {
                final String fileKeyName = CdnHelper.resImage + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".JPEG";
                final String outPutImagePath = NetHelper.getRootDirPath(mContext) + fileKeyName;
                final String filePath = imgUrls.get(i);
                try
                {
                    final File outFile = new File(outPutImagePath);
                    ImageUtils.compressBitmap(ImageUtils.getBitmapFromFile(mContext, filePath), outFile.getPath(), 130, 0);
                    //Logger.t(TAG).d("压缩成功"+outFile.length()/1024);
                    CdnHelper.getInstance().putFile(outFile, "img", fileKeyName, i);
                } catch (Exception e)
                {
                    Logger.t(TAG).d(e.getMessage());
                }
            }
        }
    }

}
