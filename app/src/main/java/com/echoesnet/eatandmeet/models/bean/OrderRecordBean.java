package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wangben on 2016/6/20.
 * 用于已经提交的订单信息
 */
public class OrderRecordBean implements Serializable
{
    private String anchorId;//主播Id
    private String anchorName;//主播昵称
    private String anchorUrl;//主播头像
    /* 订单id(订单号) */
    private String ordId;
    /* 餐厅id*/
    private String rId;
    /* 餐厅名称*/
    private String rName;
    /* 订单状态*/
    private String status;
    /* 过期时间 */
    private String overTime;
    /* 消费码 */
    private String oCode;
    /* 商家地址*/
    private String resAddr;
    /* 优惠值*/
    private String discount;
    /* 订餐时间（yyyy-MM-dd HH :mm:ss）*/
    private String orderTime;

    private String smtTime;
    /* 订餐人手机号*/
    private String mobile;
    /* 订餐人昵称*/
    private String nicName;
    /* 温馨提示 */
    private String oPrompt;
    /* 订餐人性别*/
    private String sex;
    /* 订餐桌号 layoutId(布局id即楼层) |桌号）*/
    private String sits;
    //商家电话
    private String resMobile="";
    /* 订单优惠前总价*/
    private String orderCos1;
    /* 订单优惠后总价*/
    private String orderCos2;
    //订单类型，0：正常预定的订单；1：闪付
    private String source;
    /* 订单备注 */
    private String remark;
    /* 约吃饭订单id */
    private String receiveId;
    /* 就餐顾问 id */
    private String consultant;
    /* 就餐顾问name */
    private String consultantName;
    /* 就餐顾问头像 */
    private String consultantPhurl;
    /* 轰趴 */
    private String homeparty;
    private String hpName;
    private String pName;
    private String price;
    private String orderId;
    private String overTime2;

    public String getOverTime2()
    {
        return overTime2;
    }

    public void setOverTime2(String overTime2)
    {
        this.overTime2 = overTime2;
    }

    public String getPrice()
    {
        return price;
    }

    public void setPrice(String price)
    {
        this.price = price;
    }

    public String getOrderId()
    {
        return orderId;
    }

    public void setOrderId(String orderId)
    {
        this.orderId = orderId;
    }
    //               "orderId":"订单号",
//               "homeparty":"1是轰趴馆订单0普通订单",
//               "orderTime":"支付订单时间",
//               "status":"0：待付款1：待使用2：待评价3：已关闭4：已过期5：退款中6：已退款 7已评价 8 过期退",
//               "hpName":"轰趴馆名称",
//               "pName":"套餐名称",
//               "price":"价格",
//               "oCode":"消费码",
//               "overTime":"预定结束时间"

    public String getpName()
    {
        return pName;
    }

    public void setpName(String pName)
    {
        this.pName = pName;
    }
    public String getHomeparty()
    {
        return homeparty;
    }

    public void setHomeparty(String homeparty)
    {
        this.homeparty = homeparty;
    }

    public String getHpName()
    {
        return hpName;
    }

    public void setHpName(String hpName)
    {
        this.hpName = hpName;
    }
    private List<DishBean> dishBeen;


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

    public String getOrderTime()
    {
        return orderTime;
    }

    public void setOrderTime(String orderTime)
    {
        this.orderTime = orderTime;
    }

    public String getOrdId()
    {
        return ordId;
    }

    public void setOrdId(String ordId)
    {
        this.ordId = ordId;
    }

    public String getOverTime()
    {
        return overTime;
    }

    public void setOverTime(String overTime)
    {
        this.overTime = overTime;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getoCode()
    {
        return oCode;
    }

    public void setoCode(String oCode)
    {
        this.oCode = oCode;
    }

    public String getResAddr()
    {
        return resAddr;
    }

    public void setResAddr(String resAddr)
    {
        this.resAddr = resAddr;
    }

    public String getoPrompt()
    {
        return oPrompt;
    }

    public void setoPrompt(String oPrompt)
    {
        this.oPrompt = oPrompt;
    }

    public String getResMobile()
    {
        return resMobile;
    }

    public void setResMobile(String resMobile)
    {
        this.resMobile = resMobile;
    }

    public String getSmtTime()
    {
        return smtTime;
    }

    public void setSmtTime(String smtTime)
    {
        this.smtTime = smtTime;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getAnchorId()
    {
        return anchorId;
    }

    public void setAnchorId(String anchorId)
    {
        this.anchorId = anchorId;
    }

    public String getAnchorName()
    {
        return anchorName;
    }

    public void setAnchorName(String anchorName)
    {
        this.anchorName = anchorName;
    }

    public String getAnchorUrl()
    {
        return anchorUrl;
    }

    public void setAnchorUrl(String anchorUrl)
    {
        this.anchorUrl = anchorUrl;
    }

    public String getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(String receiveId) {
        this.receiveId = receiveId;
    }

    public String getConsultant() {
        return consultant;
    }

    public void setConsultant(String consultant) {
        this.consultant = consultant;
    }

    public String getConsultantName() {
        return consultantName;
    }

    public void setConsultantName(String consultantName) {
        this.consultantName = consultantName;
    }

    public String getConsultantPhurl() {
        return consultantPhurl;
    }

    public void setConsultantPhurl(String consultantPhurl) {
        this.consultantPhurl = consultantPhurl;
    }

    @Override
    public String toString()
    {
        return "OrderRecordBean{" +
                "anchorId='" + anchorId + '\'' +
                ", anchorName='" + anchorName + '\'' +
                ", anchorUrl='" + anchorUrl + '\'' +
                ", ordId='" + ordId + '\'' +
                ", rId='" + rId + '\'' +
                ", rName='" + rName + '\'' +
                ", status='" + status + '\'' +
                ", overTime='" + overTime + '\'' +
                ", oCode='" + oCode + '\'' +
                ", resAddr='" + resAddr + '\'' +
                ", discount='" + discount + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", smtTime='" + smtTime + '\'' +
                ", mobile='" + mobile + '\'' +
                ", nicName='" + nicName + '\'' +
                ", oPrompt='" + oPrompt + '\'' +
                ", sex='" + sex + '\'' +
                ", sits='" + sits + '\'' +
                ", resMobile='" + resMobile + '\'' +
                ", orderCos1='" + orderCos1 + '\'' +
                ", orderCos2='" + orderCos2 + '\'' +
                ", source='" + source + '\'' +
                ", remark='" + remark + '\'' +
                ", receiveId='" + receiveId + '\'' +
                ", consultant='" + consultant + '\'' +
                ", consultantName='" + consultantName + '\'' +
                ", consultantPhurl='" + consultantPhurl + '\'' +
                ", homeparty='" + homeparty + '\'' +
                ", hpName='" + hpName + '\'' +
                ", pName='" + pName + '\'' +
                ", price='" + price + '\'' +
                ", orderId='" + orderId + '\'' +
                ", overTime2='" + overTime2 + '\'' +
                ", dishBeen=" + dishBeen +
                '}';
    }
}
