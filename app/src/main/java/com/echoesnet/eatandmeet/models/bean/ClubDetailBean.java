package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/6
 * @description
 */
public class ClubDetailBean
{


    /**
     * urls : ["轮播图"]
     * introduce : 介绍
     * item : 设施
     * packageUrl : ["套餐图"]
     * perPrice : 人均消费
     * address : 地址
     * mobile : 联系方式
     * comments : [{"urls":"uId","head":"头像","nickName":"昵称","age":"uId","sex":"头像","level":"昵称","score":"分","url":["评论图"],"content":"内容"}]
     */

    private String introduce;
    private String item;
    private String perPrice;
    private String address;
    private String mobile;
    private String collect;
    private String name;
    private List<String> urls;
    private List<PackagesPicBean> packageUrl;
    private List<CommentsBean> comments;
    private String posx;
    private String posy;

    public String getPosx()
    {
        return posx;
    }

    public void setPosx(String posx)
    {
        this.posx = posx;
    }

    public String getPosy()
    {
        return posy;
    }

    public void setPosy(String posy)
    {
        this.posy = posy;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getIntroduce()
    {
        return introduce;
    }

    public void setIntroduce(String introduce)
    {
        this.introduce = introduce;
    }

    public String getItem()
    {
        return item;
    }

    public void setItem(String item)
    {
        this.item = item;
    }

    public String getPerPrice()
    {
        return perPrice;
    }

    public void setPerPrice(String perPrice)
    {
        this.perPrice = perPrice;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public List<String> getUrls()
    {
        return urls;
    }

    public void setUrls(List<String> urls)
    {
        this.urls = urls;
    }

    public List<PackagesPicBean> getPackageUrl()
    {
        return packageUrl;
    }

    public void setPackageUrl(List<PackagesPicBean> packageUrl)
    {
        this.packageUrl = packageUrl;
    }

    public List<CommentsBean> getComments()
    {
        return comments;
    }

    public void setComments(List<CommentsBean> comments)
    {
        this.comments = comments;
    }

    public String getCollect()
    {
        return collect;
    }

    public void setCollect(String collect)
    {
        this.collect = collect;
    }

    public static class PackagesPicBean
    {
        /**
         * name :
         * url :
         */
        private String name;
        private List<String> urls;


        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public List<String> getUrl()
        {
            return urls;
        }

        public void setUrl(List<String> urls)
        {
            this.urls = urls;
        }
    }

    public static class CommentsBean
    {
        /**
         * head : 头像
         * nickName : 昵称
         * age : uId
         * sex : 头像
         * level : 昵称
         * score : 分
         * bigV : 1是 0不是大v
         * time : 时间
         * url : ["评论图"]
         * content : 内容
         */

        private String head;
        private String nickName;
        private String age;
        private String sex;
        private String level;
        private String score;
        private String bigV;
        private String uId;
        private String time;
        private String content;
        private List<String> url;

        public String getHead()
        {
            return head;
        }

        public String getuId()
        {
            return uId;
        }

        public void setuId(String uId)
        {
            this.uId = uId;
        }

        public void setHead(String head)
        {
            this.head = head;
        }

        public String getNickName()
        {
            return nickName;
        }

        public void setNickName(String nickName)
        {
            this.nickName = nickName;
        }

        public String getAge()
        {
            return age;
        }

        public void setAge(String age)
        {
            this.age = age;
        }

        public String getSex()
        {
            return sex;
        }

        public void setSex(String sex)
        {
            this.sex = sex;
        }

        public String getLevel()
        {
            return level;
        }

        public void setLevel(String level)
        {
            this.level = level;
        }

        public String getScore()
        {
            return score;
        }

        public void setScore(String score)
        {
            this.score = score;
        }

        public String getBigV()
        {
            return bigV;
        }

        public void setBigV(String bigV)
        {
            this.bigV = bigV;
        }

        public String getTime()
        {
            return time;
        }

        public void setTime(String time)
        {
            this.time = time;
        }

        public String getContent()
        {
            return content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public List<String> getUrl()
        {
            return url;
        }

        public void setUrl(List<String> url)
        {
            this.url = url;
        }

    }

    @Override
    public String toString()
    {
        return "ClubDetailBean{" +
                "introduce='" + introduce + '\'' +
                ", item='" + item + '\'' +
                ", perPrice='" + perPrice + '\'' +
                ", address='" + address + '\'' +
                ", mobile='" + mobile + '\'' +
                ", collect='" + collect + '\'' +
                ", name='" + name + '\'' +
                ", urls=" + urls +
                ", packageUrl=" + packageUrl +
                ", comments=" + comments +
                ", posx='" + posx + '\'' +
                ", posy='" + posy + '\'' +
                '}';
    }
}
