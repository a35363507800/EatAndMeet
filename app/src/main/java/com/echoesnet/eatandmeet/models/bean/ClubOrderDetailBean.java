package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2018/2/8
 * @description
 */
public class ClubOrderDetailBean
{

    /**
     * orderId : 订单号
     * status :"0：待付款 1：待使用 2：待评价 3：已关闭 4：已过期 5：退款中 6：已退款  7已评价  8 过期退",
     * oCode : 消费码
     * price : 价格
     * endTime : 结束时间
     * payTime : 支付时间
     * evaluate : 1已评价 0未评价
     * food : [{"url":"图","name":"名字","num":"数量"}]
     * address : 地址
     * mobile : 联系方式
     * name : 轰趴馆名字
     * collect:1已收藏 0未收藏"
     * id :轰趴馆id
     * "themeName":"主题名称",
     * "remark":"主题备注",

     */

    private String orderId;
    private String status;
    private String code;
    private String price;
    private String endTime;
    private String payTime;
    private String evaluate;
    private String address;
    private String collect;
    private String mobile;
    private String name;
    private String id;

    private String themeName;
    private String remark;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getThemeName()
    {
        return themeName;
    }

    public void setThemeName(String themeName)
    {
        this.themeName = themeName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    private List<FoodBean> food;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCollect()
    {
        return collect;
    }

    public void setCollect(String collect)
    {
        this.collect = collect;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getOCode()
    {
        return code;
    }

    public void setOCode(String oCode)
    {
        this.code = oCode;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public void setEndTime(String endTime)
    {
        this.endTime = endTime;
    }

    public String getPayTime()
    {
        return payTime;
    }

    public void setPayTime(String payTime)
    {
        this.payTime = payTime;
    }

    public String getEvaluate()
    {
        return evaluate;
    }

    public void setEvaluate(String evaluate)
    {
        this.evaluate = evaluate;
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

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<FoodBean> getFood()
    {
        return food;
    }

    public void setFood(List<FoodBean> food)
    {
        this.food = food;
    }

    public static class FoodBean
    {
        /**
         * url : 图
         * name : 名字
         * num : 数量
         */

        private String url;
        private String name;
        private String num;

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

        public String getNum()
        {
            return num;
        }

        public void setNum(String num)
        {
            this.num = num;
        }
    }

    @Override
    public String toString()
    {
        return "ClubOrderDetailBean{" +
                "orderId='" + orderId + '\'' +
                ", status='" + status + '\'' +
                ", code='" + code + '\'' +
                ", price='" + price + '\'' +
                ", endTime='" + endTime + '\'' +
                ", payTime='" + payTime + '\'' +
                ", evaluate='" + evaluate + '\'' +
                ", address='" + address + '\'' +
                ", collect='" + collect + '\'' +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", themeName='" + themeName + '\'' +
                ", remark='" + remark + '\'' +
                ", food=" + food +
                '}';
    }
}
