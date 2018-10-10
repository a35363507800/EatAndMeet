package com.echoesnet.eatandmeet.models.eventmsgs;

import com.echoesnet.eatandmeet.models.bean.BigVcommentBean;

/**
 * Created by wangben on 2016/6/15.
 */
public class BigVcommentMsg
{
    public final BigVcommentBean bigVcommentBean;

    public BigVcommentMsg(BigVcommentBean bigVcommentBean)
    {
        this.bigVcommentBean = bigVcommentBean;
    }

    public BigVcommentBean getBigVcommentBean()
    {
        return bigVcommentBean;
    }
}
