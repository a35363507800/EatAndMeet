package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/7.
 *
 * @author ling
 */

public class ClubInfoBean
{


    public List<ReserveDateBean> getReserveDate()
    {
        return reserveDate;
    }

    public void setReserveDate(List<ReserveDateBean> reserveDate)
    {
        this.reserveDate = reserveDate;
    }

    public List<PackagesBean> getPackages()
    {
        return packages;
    }

    public void setPackages(List<PackagesBean> packages)
    {
        this.packages = packages;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<ThemeBean> getTheme() {
        return theme;
    }

    public void setTheme(List<ThemeBean> theme) {
        this.theme = theme;
    }

    private List<ReserveDateBean> reserveDate;    //预定日期
    private List<PackagesBean> packages;    //套餐规格详细
    private List<ThemeBean> theme;    //会所主题
    private String url;          //图片


    public class ThemeBean
    {

        private String id; //主题id
        private String name; //主题名称
        private String url;  //主题图片
        private String price;  //主题价格

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getWeek() {
            return url;
        }

        public void setWeek(String week) {
            this.url = week;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "ThemeBean{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", week='" + url + '\'' +
                    ", price='" + price + '\'' +
                    '}';
        }
    }

    public class ScreeningsBean
    {

        private String name; //场次名
        private String start; //开始时间
        private String end;  //结束时间
        private String index;  //场次编号
        private String status;  //可选状态 1已用  0可用

        public String getIndex()
        {
            return index;
        }

        public void setIndex(String index)
        {
            this.index = index;
        }

        public String getStatus()
        {
            return status;
        }

        public void setStatus(String status)
        {
            this.status = status;
        }

        public String getStart()
        {
            return start;
        }

        public void setStart(String start)
        {
            this.start = start;
        }

        public String getEnd()
        {
            return end;
        }

        public void setEnd(String end)
        {
            this.end = end;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return "ScreeningsBean{" +
                    "name='" + name + '\'' +
                    ", start='" + start + '\'' +
                    ", end='" + end + '\'' +
                    ", index='" + index + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    public class ReserveDateBean
    {
        private String date; //日期
        private String week; //日期
        private String state="0"; //是否灰

        public String getStates() {
            return state;
        }

        public void setStates(String state) {
            this.state = state;
        }

        private List<ScreeningsBean> screenings;

        public List<ScreeningsBean> getScreenings()
        {
            return screenings;
        }

        public void setScreenings(List<ScreeningsBean> screenings)
        {
            this.screenings = screenings;
        }

        public String getWeek()
        {
            return week;
        }

        public void setWeek(String week)
        {
            this.week = week;
        }



        public String getDate()
        {
            return date;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        @Override
        public String toString()
        {
            return "ReserveDateBean{" +
                    "date='" + date + '\'' +
                    ", week='" + week + '\'' +
                    '}';
        }
    }


    @Override
    public String toString()
    {
        return "ClubInfoBean{" +

                ", reserveDate=" + reserveDate +
                ", packages=" + packages +
                ", url='" + url + '\'' +
                '}';
    }

}
