package com.echoesnet.eatandmeet.utils.redPacket;

import android.support.v4.app.FragmentActivity;

/**
 * Created by Administrator on 2016/7/26.
 */
public class RPOpenPacketUtil
{
    private static RPOpenPacketUtil instance;

    public static RPOpenPacketUtil getInstance()
    {
        if (null == instance)
        {
            //Class var0 = RPOpenPacketUtil.class;
            synchronized (RPOpenPacketUtil.class)
            {
                if (null == instance)
                {
                    instance = new RPOpenPacketUtil();
                }
            }
        }

        return instance;
    }

    private RPOpenPacketUtil()
    {
    }

    public void openRedPacket(RedPacketInfo var1, FragmentActivity var2, RPOpenPacketUtil.RPOpenPacketCallBack var3)
    {
        if (var3 == null)
        {
            throw new IllegalArgumentException("callback is null!");
        }
        else
        {
/*            OpenPacketPresenter var4 = new OpenPacketPresenter(var2, var1, new g(this, var3, var2));
            if (b.b(var2))
            {
                var4.openMoney(var1.moneyID, 0, 12);
                var3.showLoading();
            }
            else
            {
                var3.hideLoading();
                Toast.makeText(var2, var2.getResources().getString(string.no_network_conected), 0).show();
            }*/

        }
    }

    public interface RPOpenPacketCallBack
    {
        void onSuccess(String var1, String var2);

        void showLoading();

        void hideLoading();

        void onError(String var1, String var2);
    }
}
