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

public class PackagesBean implements Parcelable
{

        private String name; //套餐名称
        private String price;          //价格
        private String id;          //id

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    private List<FoodBean> food; //日期

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public List<FoodBean> getFood()
    {
        return food;
    }

    public void setFood(List<FoodBean> food)
    {
        this.food = food;
    }

    @Override
    public String toString()
    {
        return "PackagesBean{" +
                "name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", food=" + food +
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
        dest.writeString(this.price);
        dest.writeTypedList(this.food);
    }

    public PackagesBean()
    {
    }

    protected PackagesBean(Parcel in)
    {
        this.name = in.readString();
        this.price = in.readString();
        this.food = in.createTypedArrayList(FoodBean.CREATOR);
    }

    public static final Parcelable.Creator<PackagesBean> CREATOR = new Parcelable
            .Creator<PackagesBean>()
    {
        @Override
        public PackagesBean createFromParcel(Parcel source)
        {
            return new PackagesBean(source);
        }

        @Override
        public PackagesBean[] newArray(int size)
        {
            return new PackagesBean[size];
        }
    };
}
