package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/6/25.
 * Modified by wangben on 2016/10/9
 * Content:修改
 */
public class PayBean
{
    private String orderId;  // 订单号（如果是订单支付，传订单号，如果是其他消费，传空字符串）
    private String amount;   // 付款金额
    private String id = "app_SaHiTK404G8SKinP";       // 支付使用的 app 对象的 id
    private String channel;  // 支付使用的第三方支付渠道
    private String currency = "cny"; // 三位 ISO 货币代码，人民币 cny
    private String client_ip = "127.0.0.1"; // 发起支付请求客户端的 IP 地址
    private String subject; // 商品的标题
    private String body; // 商品的描述信息
    /**
     * 用于标示是哪种支付，是支付订单，还是红包，还是存值
     */
    private String myPayType = "order";

    public PayBean()
    {
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }

    public String getAmount()
    {
        return amount;
    }
    /**
     * 由于ping++使用的分为单位，我们应用传入的是以元为单位，所以需要换算成分
     * @param amount 传入的金额（请注意：单位是元）
     */
    public void setAmount(String amount)
    {
        this.amount = String.valueOf((int) (Double.parseDouble(amount) * 100));
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getClient_ip()
    {
        return client_ip;
    }

    public void setClient_ip(String client_ip)
    {
        this.client_ip = client_ip;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public String getBody()
    {
        return body;
    }

    public void setBody(String body)
    {
        this.body = body;
    }

    public String getMyPayType()
    {
        return myPayType;
    }

    public void setMyPayType(String myPayType)
    {
        this.myPayType = myPayType;
    }
}
