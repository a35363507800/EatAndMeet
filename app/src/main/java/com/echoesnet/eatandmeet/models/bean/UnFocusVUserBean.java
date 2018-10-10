package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/9/15 0015
 * @description
 */
public class UnFocusVUserBean
{
    private List<UnFocusVuserItemBean> focusVuserList;

    public List<UnFocusVuserItemBean> getFocusVuserList()
    {
        return focusVuserList;
    }

    public void setFocusVuserList(List<UnFocusVuserItemBean> focusVuserList)
    {
        this.focusVuserList = focusVuserList;
    }
}
