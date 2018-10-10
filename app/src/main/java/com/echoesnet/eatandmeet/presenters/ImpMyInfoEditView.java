package com.echoesnet.eatandmeet.presenters;

import android.app.Activity;
import android.content.Context;

import com.echoesnet.eatandmeet.activities.MyInfoEditAct;
import com.echoesnet.eatandmeet.controllers.subscribers.SilenceSubscriber2;
import com.echoesnet.eatandmeet.http4retrofit2.ApiException;
import com.echoesnet.eatandmeet.http4retrofit2.HttpMethods;
import com.echoesnet.eatandmeet.models.bean.UserEditInfoBean;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyInfoEditView;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.utils.ImageUtils.ImageUtils;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.CdnHelper;
import com.echoesnet.eatandmeet.utils.NetUtils.IOnCdnFeedbackListener;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/15.
 * Refactor by ben on 2017/3/10
 */

public class ImpMyInfoEditView extends BasePresenter<MyInfoEditAct>
{
    private final String TAG = ImpMyInfoEditView.class.getSimpleName();

    public void getUserInfo()
    {
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                try
                {
                    JSONObject bodyObj = new JSONObject(response);
                    UserEditInfoBean userInfo = new Gson().fromJson(bodyObj.getString("userBean"), UserEditInfoBean.class);
                    if (getView() != null)
                        getView().getUserInfoCallback(userInfo);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, NetInterfaceConstant.UserC_queryUInfo, reqParamMap);
    }

    private void postUserTxtInfo(final UserEditInfoBean bean)
    {
        final IMyInfoEditView iMyInfoEditView = getView();
        if (iMyInfoEditView == null)
        {
            return;
        }
        Map<String, String> reqParamMap = NetHelper.getCommonPartOfParam(null);
        reqParamMap.put("user", new Gson().toJson(bean, UserEditInfoBean.class));
        HttpMethods.getInstance().startServerRequest(new SilenceSubscriber2<String>()
        {
            @Override
            public void onNext(String response)
            {
                super.onNext(response);
                if (iMyInfoEditView != null)
                    iMyInfoEditView.postUserInfoCallback(response, bean);
            }

            @Override
            public void onHandledError(ApiException apiE)
            {
                super.onHandledError(apiE);
                if (iMyInfoEditView != null)
                    iMyInfoEditView.callServerErrorCallback(NetInterfaceConstant.UserC_uInfo, apiE.getErrorCode(), apiE.getErrBody());
            }
        }, NetInterfaceConstant.UserC_uInfo, reqParamMap);
    }

    /**
     * 提交个人信息中的图片,//先传图片，结果返回后，根据结果来提交评价
     *
     * @param imgUrlsCopy  图片列表集合的副本（新new一个对象）不包含+号图片
     * @param remoteImgLst 已经在线上的图片
     * @param userBean
     */
    public void postUserProfileInfo(final List<String> imgUrlsCopy, final List<String> remoteImgLst, final UserEditInfoBean userBean)
    {
        //先传图片，结果返回后，根据结果来提交评价
        final TreeMap<Integer, String> sortMap = new TreeMap<>();
        final List<String> galleryImgUrlsCopy = new ArrayList<>(imgUrlsCopy);
        galleryImgUrlsCopy.removeAll(remoteImgLst);
        //本地需要上传的图片集合
        final ArrayList<String> localImgUrls = new ArrayList<>(galleryImgUrlsCopy);

        //没有图片(只有加号)
        if (localImgUrls.size() == 0)
        {
            userBean.setUpUrls(CommonUtils.listToStrWishSeparator(remoteImgLst, CommonUtils.SEPARATOR));
            postUserTxtInfo(userBean);
        }
        else
        {
            final IMyInfoEditView iMyInfoEditView = getView();
            if (iMyInfoEditView == null)
            {
                return;
            }
            Context mContext = (MyInfoEditAct) getView();
            CdnHelper.getInstance().setOnCdnFeedbackListener(new IOnCdnFeedbackListener()
            {
                @Override
                public void onSuccess(JSONObject response, File file, String fileKeyName, int uploadOrder)
                {
                    Logger.t(TAG).d("r>" + response.toString() + "key>" + fileKeyName + "order>" + uploadOrder);
                    file.delete();
                    String imgUrl = CdnHelper.CDN_ORIGINAL_SITE + fileKeyName;
                    sortMap.put(uploadOrder, imgUrl);
                    //全部上传成功，提交
                    if (sortMap.size() == localImgUrls.size())
                    {
                        List<String> tempUrls = new ArrayList<String>(remoteImgLst);
                        for (String s : sortMap.values())
                        {
                            tempUrls.add(s);
                        }
                        userBean.setUpUrls(CommonUtils.listToStrWishSeparator(tempUrls, CommonUtils.SEPARATOR));
                        postUserTxtInfo(userBean);
                    }
                }

                @Override
                public void onProcess(long len)
                {
                }

                @Override
                public void onFail(JSONObject response, File file)
                {
                    file.delete();
                    Logger.t(TAG).d("错误：" + response.toString());
                    if (iMyInfoEditView != null)
                        iMyInfoEditView.postUserGalleryImagesCallback(response);
                }
            });

            for (int i = 0; i < localImgUrls.size(); i++)
            {
                final String fileKeyName = CdnHelper.userImage + SharePreUtils.getUserMobile(mContext) + UUID.randomUUID().toString().substring(0, 8) + ".jpg";
                final String outPutImagePath = NetHelper.getRootDirPath(mContext) + fileKeyName;
                final String filePath = localImgUrls.get(i);
                try
                {
                    final File outFile = new File(outPutImagePath);
                    int degree = ImageUtils.readPictureDegree(filePath.replace("/raw", ""));
                    ImageUtils.compressBitmap(ImageUtils.getBitmapFromFile((Activity) mContext, filePath), outFile.getPath(), 150, degree);
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
