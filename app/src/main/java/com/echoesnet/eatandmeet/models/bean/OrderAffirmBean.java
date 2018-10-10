package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author an
 * @version 1.0
 * @modifier
 * @createDate 2017/11/21 0021
 * @description
 */
public class OrderAffirmBean
{

    /**
     * sId : 约会id
     * date : 约会时间（格式化待定）
     * userList : [{"uId":"uId","nicName":"昵称","phUrl":"头像"}]
     */

    private String sId;
    private String date;
    private boolean isSelect = false;
    private List<UserListBean> userList;

    public boolean isSelect()
    {
        return isSelect;
    }

    public void setSelect(boolean select)
    {
        isSelect = select;
    }

    public String getSId()
    {
        return sId;
    }

    public void setSId(String sId)
    {
        this.sId = sId;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public List<UserListBean> getUserList()
    {
        return userList;
    }

    public void setUserList(List<UserListBean> userList)
    {
        this.userList = userList;
    }

    public static class UserListBean
    {
        /**
         * uId : uId
         * nicName : 昵称
         * phUrl : 头像
         */

        private String uId;
        private String nicName;
        private String phUrl;

        public String getUId()
        {
            return uId;
        }

        public void setUId(String uId)
        {
            this.uId = uId;
        }

        public String getNicName()
        {
            return nicName;
        }

        public void setNicName(String nicName)
        {
            this.nicName = nicName;
        }

        public String getPhUrl()
        {
            return phUrl;
        }

        public void setPhUrl(String phUrl)
        {
            this.phUrl = phUrl;
        }

        @Override
        public String toString()
        {
            return "UserListBean{" +
                    "uId='" + uId + '\'' +
                    ", nicName='" + nicName + '\'' +
                    ", phUrl='" + phUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString()
    {
        return "OrderAffirmBean{" +
                "sId='" + sId + '\'' +
                ", date='" + date + '\'' +
                ", userList=" + userList +
                '}';
    }
}
