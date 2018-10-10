package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/7
 * @description
 */
public class ClubCommentBean
{
        /**
         * head : 头像
         * nickName : 昵称
         * age : uId
         * sex : 头像
         * level : 昵称
         * score : 分
         * bigV : 1是0不是大v
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
        private String time;
        private String content;
        private List<String> url;

        public String getHead()
        {
            return head;
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
