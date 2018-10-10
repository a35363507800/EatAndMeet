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

public class FoodBean implements Parcelable
{

    private String name; //食物名称
    private String num; //数量
    private String unit;  //单位
    private String url;  //食物图片

    public String getNum()
    {
        return num;
    }

    public void setNum(String num)
    {
        this.num = num;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
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
        return "FoodBean{" +
                "name='" + name + '\'' +
                ", num='" + num + '\'' +
                ", unit='" + unit + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.name);
        dest.writeString(this.num);
        dest.writeString(this.unit);
        dest.writeString(this.url);
    }

    public FoodBean()
    {
    }

    protected FoodBean(Parcel in)
    {
        this.name = in.readString();
        this.num = in.readString();
        this.unit = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<FoodBean> CREATOR = new Parcelable.Creator<FoodBean>()
    {
        @Override
        public FoodBean createFromParcel(Parcel source)
        {
            return new FoodBean(source);
        }

        @Override
        public FoodBean[] newArray(int size)
        {
            return new FoodBean[size];
        }
    };
}
