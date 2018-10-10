package com.echoesnet.eatandmeet.models.bean;

    /**
     * Created by liuchao on 2017/7/12 15.
     */

    public class SysNewsTopBean
    {
        private String systemUnreadNum;
        private String tvSysTime;
        private CharSequence tvSysContent;


        public String getSystemUnreadNum()
        {
            return systemUnreadNum;
        }

        public void setSystemUnreadNum(String systemUnreadNum)
        {
            this.systemUnreadNum = systemUnreadNum;
        }

        public String getTvSysTime()
        {
            return tvSysTime;
        }

        public void setTvSysTime(String tvSysTime)
        {
            this.tvSysTime = tvSysTime;
        }

        public CharSequence getTvSysContent()
        {
            return tvSysContent;
        }

        public void setTvSysContent(CharSequence tvSysContent)
        {
            this.tvSysContent = tvSysContent;
        }

        @Override
        public String toString()
        {
            return "SysNewsTopBean{" +
                    "systemUnreadNum='" + systemUnreadNum + '\'' +
                    ", tvSysTime='" + tvSysTime + '\'' +
                    ", tvSysContent='" + tvSysContent + '\'' +
                    '}';
        }
    }
