package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by lC on 2017/7/18 19.
 */

public class YueValuateBean
{

  //     "0可以约 1约会中",
//             "desc":"描述",
//             "price":"价格",

    private String status;
    private String desc;
    private String price;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    @Override
    public String toString()
    {
        return "YueValuateBean{" +
                "status='" + status + '\'' +
                ", desc='" + desc + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
