package com.echoesnet.eatandmeet.presenters.viewinterface;

import okhttp3.Call;

/**
 * Created by an on 2017/2/28.
 */

public interface IMyTaskActView
{
//    void requestNetError(Call call, Exception e, String exceptSource);

    /**
     *  任务成就未读显示接口回调
     * @param task 任务是否有未读数据     0无  1有
     * @param successes 成就是否有未读数据  0无  1有
     */
    void updateTaskOkCallBack(String task,String successes);
}
