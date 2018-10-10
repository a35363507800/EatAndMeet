package com.echoesnet.eatandmeet.presenters;

import android.content.Context;

import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFriendStatePre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IMyFriendStateView;
import com.echoesnet.eatandmeet.presenters.viewinterface.IShoppingCartPre;
import com.echoesnet.eatandmeet.presenters.viewinterface.IShoppingCartView;

/**
 * Created by Administrator on 2017/7/17.
 */

public class ImplMyFriendStatePre extends BasePresenter<IMyFriendStateView> implements IMyFriendStatePre
{
    private Context mContext;
    public ImplMyFriendStatePre(Context context)
    {
        this.mContext=context;
    }



}
