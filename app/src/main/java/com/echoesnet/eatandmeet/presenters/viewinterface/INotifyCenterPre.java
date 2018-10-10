package com.echoesnet.eatandmeet.presenters.viewinterface;

import android.view.View;

/**
 * Created by lc on 2017/7/12 14.
 */

public interface INotifyCenterPre
{
    /**
     * 获取通知消息
     *
     * @param startIdx 起始位置
     * @param num      拉取数量
     * @param type     类型
     */
    void getAllNotification(String startIdx, String num, String type);

    /**
     * 忽略未读
     */
    void ignoreUnread();

    /**
     * 关注、取消关注
     *
     * @param luId     对方Id
     * @param operFlag 1：关注，0：取消关注
     * @param position 索引
     * @param view     view
     */
    void focusPerson(String luId, String operFlag, int position, View view);

    /**
     * 删除通知消息
     *
     * @param messageId 消息id
     * @param position  索引
     */
    void deleteMessage(String messageId, int position);

}
