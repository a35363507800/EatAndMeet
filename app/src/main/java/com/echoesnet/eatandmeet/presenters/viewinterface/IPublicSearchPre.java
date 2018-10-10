package com.echoesnet.eatandmeet.presenters.viewinterface;


import com.echoesnet.eatandmeet.models.bean.SearchUserBean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author zdw
 * @version 1.0
 * @modifier
 * @createDate 2017/7/27
 * @description
 */

public interface IPublicSearchPre
{
    void getSearchInfoPresent(String startIndex, String num, String type, String keyWord, String operateType);

    void focusPerson(List<SearchUserBean> list, String operFlag, final int position);
}
