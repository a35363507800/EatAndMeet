package com.echoesnet.eatandmeet.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.echoesnet.eatandmeet.utils.BigGiftUtil.BigGiftUtil;
import com.echoesnet.eatandmeet.utils.NetInterfaceConstant;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.ToastUtils;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2016/8/15.
 * 此页面会每次创建实例
 */
public class NetworkChangeReceiver extends BroadcastReceiver
{
    private static final String TAG = NetworkChangeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        int status = NetHelper.getNetworkStatus(context);
        Logger.t("NetworkChangeReceiver").d("网络状态变化：" + status);

        if (status == -1)
        {
            ToastUtils.showShort("当前无网络连接，请检测您的网络环境");
            BigGiftUtil.isBigGiftDownloading = false;
        }
        else if (status == 1)
        {
            Logger.t(TAG).d("gift------------>netChange():status==1:" + BigGiftUtil.getInstance().lastNetStatus);
            if (BigGiftUtil.getInstance().lastNetStatus != 1)
                BigGiftUtil.startCheckBigGif(context, NetInterfaceConstant.FILE_GIFT_VERSION, false);
        }
        if (listener != null)
            listener.onNetworkChanged(status);
        BigGiftUtil.getInstance().lastNetStatus = status;
//        lastNetStatus = 0;
//        lastDownloadStatus = false;
    }

    private static IOnNetworkChangedListener listener;

    public interface IOnNetworkChangedListener
    {
        void onNetworkChanged(int status);
    }

    public static void setOnNetworkChangedListener(IOnNetworkChangedListener changedListener)
    {
        listener = changedListener;
    }
}
