package com.echoesnet.eatandmeet.models.eventmsgs;

import com.echoesnet.eatandmeet.models.bean.OrderBean;

/**
 * Created by Administrator on 2016/6/13.
 */
public class OrderMsg
{
    public OrderBean orderBean;

    public OrderMsg(OrderBean orderBean) {
        this.orderBean = orderBean;
    }
    public OrderBean getOrderBean()
    {
        return orderBean;
    }

    @Override
    public String toString()
    {
        return "OrderMsg{" +
                "orderBean=" + orderBean +
                '}';
    }
}
