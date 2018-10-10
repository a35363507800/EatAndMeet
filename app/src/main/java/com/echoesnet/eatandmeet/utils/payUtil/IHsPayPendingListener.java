package com.echoesnet.eatandmeet.utils.payUtil;

import com.jungly.gridpasswordview.GridPasswordView;

/**
 * Created by wangben on 2016/7/13.
 * 当输入完正确密码后触发，然后去调用相应的后台接口
 */
public interface IHsPayPendingListener
{
    void payPending(String passWord, GridPasswordView gridPasswordView);
}
