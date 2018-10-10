package com.echoesnet.eatandmeet.presenters.viewinterface;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/1/19
 * @description
 */
public interface IRoomSearchPre
{
    void getResList(String startIdx, String num, final String operateType, String keyword,String resType);
}
