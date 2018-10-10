package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author ben
 * @version 1.0
 * @createDate 2017 /7/14 14:24
 * @description app主页的功能接口
 */
public interface IHomePre
{
    /**
     * 获取app最新版本号.
     */
    void getVersionCode();

    /**
     * 下载APP启动背景图片.
     */
    void downloadBgImg();

    /**
     * 下载位置控件数据源
     */
    void downloadProvinceData();

    /**
     * 隐藏某一文件夹里面的图片文件
     */
    void hindAppFolder();

    /**
     * 发送用户位置开关状态.
     */
    void sendLocationSwitch();

    /**
     * 更新消息红点状态.
     */
    void updateTaskOk();
}
