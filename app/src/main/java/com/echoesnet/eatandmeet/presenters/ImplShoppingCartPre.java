package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.presenters.viewinterface.IMWalletPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IShoppingCartPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IShoppingCartView;
import com.echoesnet.eatandmeet.presenters.viewinterface.MWalletActView;

import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ImplShoppingCartPre extends BasePresenter<IShoppingCartView> implements IShoppingCartPre
{
    private Context mContext;
    public ImplShoppingCartPre(Context context)
    {
        this.mContext=context;
    }



}
