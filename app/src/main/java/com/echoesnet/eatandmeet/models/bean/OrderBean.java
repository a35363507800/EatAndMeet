package com.echoesnet.eatandmeet.models.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ben on 2016/6/13.
     使用IED添加setter and getter 方法时候请注意，千万不要设置如下方法
     public static OrderBean getOrderBean()
     {
     return orderBean;
     }

 */
public class OrderBean implements Parcelable
{
    /* 餐厅id*/
    private String rId;
    /* 餐厅名称*/
    private String rName;
    /* 优惠值*/
    private String discount;
    /* 订餐时间（yyyy-MM-dd HH :mm:ss）*/
    private String orderTime;
    /* 订餐人手机号*/
    private String mobile;
    /* 订餐人昵称*/
    private String nicName;
    /* 订餐人性别*/
    private String sex;
    /* 订餐桌号 layoutId(布局id即楼层) |桌号）*/
    private String sits;
    //桌子的名称信息 楼层|桌号|类型
    private String sitsName;

    //0:未点菜未订桌 1:订桌但未点菜 2：点菜单未订桌 3：两者都具备
    private String type = "0";

    /* 订单优惠前总价*/
    private String orderCos1;
    /* 订单优惠后总价*/
    private String orderCos2;

    private String floor = "1";
    private String fewDishes;
    //订餐备注
    private String remark;

    //@Expose(serialize = false)
    private transient String recommendHostUid;
    private List<DishBean> dishBeen;

    private OrderBean()
    {
    }

    private static OrderBean orderBean = null;

    public static OrderBean getOrderBeanInstance()
    {
        if (orderBean == null)
        {
            orderBean = new OrderBean();
        }
        return orderBean;
    }
    //不可随便使用，
    public static void setOrderBean(OrderBean orderBeanIn)
    {
       orderBean = orderBeanIn;
    }
    public static void destroyOrderBeanInstance()
    {
        orderBean = null;
    }

    public String getDiscount()
    {
        return discount;
    }

    public void setDiscount(String discount)
    {
        this.discount = discount;
    }

    public List<DishBean> getDishBeen()
    {
        return dishBeen;
    }

    public void setDishBeen(List<DishBean> dishBeen)
    {
        this.dishBeen = dishBeen;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getNicName()
    {
        return nicName;
    }

    public void setNicName(String nicName)
    {
        this.nicName = nicName;
    }

    public String getOrderTime()
    {
        return orderTime;
    }

    public void setOrderTime(String orderTime)
    {
        this.orderTime = orderTime;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getrName()
    {
        return rName;
    }

    public void setrName(String rName)
    {
        this.rName = rName;
    }

    public String getSex()
    {
        return sex;
    }

    public void setSex(String sex)
    {
        this.sex = sex;
    }

    public String getSits()
    {
        return sits;
    }

    public void setSits(String sits)
    {
        this.sits = sits;
    }

    /**
     * 0:未点菜未订桌 1:订桌但未点菜 2：点菜单未订桌 3：两者都具备
     *
     * @return
     */
    public String getType()
    {
        return type;
    }

    /**
     * 0:未点菜未订桌 1:订桌但未点菜 2：点菜单未订桌 3：两者都具备
     *
     * @param type
     */
    public void setType(String type)
    {
        this.type = type;
    }

    public String getOrderCos1()
    {
        return orderCos1;
    }

    public void setOrderCos1(String orderCos1)
    {
        this.orderCos1 = orderCos1;
    }

    public String getOrderCos2()
    {
        return orderCos2;
    }

    public void setOrderCos2(String orderCos2)
    {
        this.orderCos2 = orderCos2;
    }

    public String getFewDishes()
    {
        return fewDishes;
    }

    public void setFewDishes(String fewDishes)
    {
        this.fewDishes = fewDishes;
    }

    public String getFloor()
    {
        return floor;
    }

    public void setFloor(String floor)
    {
        this.floor = floor;
    }

    public String getSitsName()
    {
        return sitsName;
    }

    public void setSitsName(String sitsName)
    {
        this.sitsName = sitsName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getRecommendHostUid()
    {
        return recommendHostUid;
    }

    public void setRecommendHostUid(String recommendHostUid)
    {
        this.recommendHostUid = recommendHostUid;
    }

    @Override
    public String toString()
    {
        return "OrderBean{" +
                "discount='" + discount + '\'' +
                ", rId='" + rId + '\'' +
                ", rName='" + rName + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nicName='" + nicName + '\'' +
                ", sex='" + sex + '\'' +
                ", sits='" + sits + '\'' +
                ", sitsName='" + sitsName + '\'' +
                ", type='" + type + '\'' +
                ", orderCos1='" + orderCos1 + '\'' +
                ", orderCos2='" + orderCos2 + '\'' +
                ", floor='" + floor + '\'' +
                ", dishBeen=" + dishBeen +
                ", fewDishes='" + fewDishes + '\'' +
                ", remark='" + remark + '\'' +
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
        dest.writeString(this.rId);
        dest.writeString(this.rName);
        dest.writeString(this.discount);
        dest.writeString(this.orderTime);
        dest.writeString(this.mobile);
        dest.writeString(this.nicName);
        dest.writeString(this.sex);
        dest.writeString(this.sits);
        dest.writeString(this.sitsName);
        dest.writeString(this.type);
        dest.writeString(this.orderCos1);
        dest.writeString(this.orderCos2);
        dest.writeString(this.floor);
        dest.writeString(this.fewDishes);
        dest.writeString(this.remark);
        dest.writeList(this.dishBeen);
    }

    protected OrderBean(Parcel in)
    {
        this.rId = in.readString();
        this.rName = in.readString();
        this.discount = in.readString();
        this.orderTime = in.readString();
        this.mobile = in.readString();
        this.nicName = in.readString();
        this.sex = in.readString();
        this.sits = in.readString();
        this.sitsName = in.readString();
        this.type = in.readString();
        this.orderCos1 = in.readString();
        this.orderCos2 = in.readString();
        this.floor = in.readString();
        this.fewDishes = in.readString();
        this.remark = in.readString();
        this.dishBeen = new ArrayList<DishBean>();
        in.readList(this.dishBeen, DishBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<OrderBean> CREATOR = new Parcelable.Creator<OrderBean>()
    {
        @Override
        public OrderBean createFromParcel(Parcel source)
        {
            return new OrderBean(source);
        }

        @Override
        public OrderBean[] newArray(int size)
        {
            return new OrderBean[size];
        }
    };
}
