package com.echoesnet.eatandmeet.utils.videoUtil;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2018/1/8 0008
 * @description
 */
public interface VideoUtilListener
{
    void start();
    void complete(String outPath,String err);
}
