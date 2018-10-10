package com.echoesnet.eatandmeet.models.bean;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wangben on 2016/5/5.
 */
public class RestaurantBean
{
    //编号
    private String rId;
    //位置点
    private String mapId;
    //评级
    private String rStar = "5";
    //平均消费
    private String perPrice;
    //最低消费
    private String minConsume;
    //餐厅赞数
    private String rPraise = "0";
    // 餐厅签名
    private String rSign;
    //营业时间
    private String rTime;
    // 餐厅地址
    private String rAddr;
    // 餐厅已订桌数目
    private String bookedNum;
    private String collected;
    //餐厅活动类型 0没有活动，1：主题2：特惠
    private String activity;

    //电话号码，有可能多个
    private List<String> phoneNums;
    // 餐厅总座位数
    private String rTotal;
    //时间间隔
    private String shorterTime;
    //可用时间段
    private String useTime;

    //是否提供私人定制服务
    private String isSupplyPrivateS;
    //今日已经预定的餐桌数
    private String reserveDesks;
    //退款须知
    private String refundClause;
    // 所属位置（市）
    private String city;
    // 所属位置（县）
    private String county;
    // 所属位置（区）
    private String region;
    // 所属商圈
    private String circle;
    // 大分类
    private String mCategory;
    // 小分类
    private String sCategory;
    // 起订价
    private String lessPrice;

    private String snapshot;

    //距离
    private String distance;
    //餐厅名称
    private String rName;
    //餐厅展示图
    private String photo;

    private String rMobile;
    //品牌故事链接
    private String resStory;


    //打折信息,一期不做
    private List<String> disCountItems;
    //大咖评价，从cdn上拿数据
    private List<BigVcommentBean> bigVs;
    //屌丝评价
    private List<CommonUserCommentBean> evaList;
    //餐桌集合
    private List<TableBean> tableEntities;

    // ============ 餐厅首页dw添加start ==============
    // 人物头像
    private String[] resPraiseList;
    private String classUrl;
    private String floor;
    private String keepTime;
    private String keepUse;
    private String preOrderTime;
    private String cos;
    private String[] location;
    private String rpUrls;
    // 是否已点赞
    private String praisedOrNot;
    private String testDistance;

    private String rStatus;
    private String posxy;
    //117.1857370000
    private String posx;
    //39.1407170000
    private String posy;

    private String weekFlg;
    private String advDay;
    private String billing;
    private String bkTime;
    private String restTime;
    private String videoUrl;
    private String videoPic;


    // ============ 餐厅首页dw添加end ==============


    public String getVideoUrl()
    {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl)
    {
        this.videoUrl = videoUrl;
    }

    public String getVideoPic()
    {
        return videoPic;
    }

    public void setVideoPic(String videoPic)
    {
        this.videoPic = videoPic;
    }

    public RestaurantBean()
    {
    }

    public String getPosx()
    {
        return posx;
    }

    public void setPosx(String posx)
    {
        this.posx = posx;
    }

    public String getPosy()
    {
        return posy;
    }

    public void setPosy(String posy)
    {
        this.posy = posy;
    }

    public String getrTotal()
    {
        return rTotal;
    }

    public void setrTotal(String rTotal)
    {
        this.rTotal = rTotal;
    }

    public String getPreOrderTime()
    {
        return preOrderTime;
    }

    public void setPreOrderTime(String preOrderTime)
    {
        this.preOrderTime = preOrderTime;
    }

    public String getKeepUse()
    {
        return keepUse;
    }

    public void setKeepUse(String keepUse)
    {
        this.keepUse = keepUse;
    }

    public String getKeepTime()
    {
        return keepTime;
    }

    public void setKeepTime(String keepTime)
    {
        this.keepTime = keepTime;
    }

    public String getFloor()
    {
        return floor;
    }

    public void setFloor(String floor)
    {
        this.floor = floor;
    }

    public String getClassUrl()
    {
        return classUrl;
    }

    public void setClassUrl(String classUrl)
    {
        this.classUrl = classUrl;
    }

    public String getSign()
    {
        return rSign;
    }

    public void setSign(String rSign)
    {
        this.rSign = rSign;
    }

    public String getPraise()
    {
        return rPraise;
    }

    public void setPraise(String rPraise)
    {
        this.rPraise = rPraise;
    }

    public String getMapId()
    {
        return mapId;
    }

    public void setMapId(String mapId)
    {
        this.mapId = mapId;
    }

    public List<BigVcommentBean> getBigVs()
    {
        return bigVs;
    }

    public void setBigVs(List<BigVcommentBean> bigVs)
    {
        this.bigVs = bigVs;
    }

    public List<String> getDisCountItems()
    {
        return disCountItems;
    }

    public void setDisCountItems(List<String> disCountItems)
    {
        this.disCountItems = disCountItems;
    }

    public String isSupplyPrivateS()
    {
        return isSupplyPrivateS;
    }

    public void setSupplyPrivateS(String supplyPrivateS)
    {
        isSupplyPrivateS = supplyPrivateS;
    }

    public List<CommonUserCommentBean> getEvaList()
    {
        return evaList;
    }

    public void setEvaList(List<CommonUserCommentBean> evaList)
    {
        this.evaList = evaList;
    }

    public String getMinConsume()
    {
        return minConsume;
    }

    public void setMinConsume(String minConsume)
    {
        this.minConsume = minConsume;
    }


    public String getrTime()
    {
        return rTime;
    }

    public void setrTime(String rTime)
    {
        this.rTime = rTime;
    }

    public String getPerPrice()
    {
        return perPrice;
    }

    public void setPerPrice(String perPrice)
    {
        this.perPrice = perPrice;
    }

    public List<String> getPhoneNums()
    {
        return phoneNums;
    }

    public void setPhoneNums(List<String> phoneNums)
    {
        this.phoneNums = phoneNums;
    }

    public String getRefundClause()
    {
        return refundClause;
    }

    public void setRefundClause(String refundClause)
    {
        this.refundClause = refundClause;
    }

    public String getReserveDesks()
    {
        return reserveDesks;
    }

    public void setReserveDesks(String reserveDesks)
    {
        this.reserveDesks = reserveDesks;
    }

    public String getRid()
    {
        return rId;
    }

    public void setRid(String rid)
    {
        this.rId = rid;
    }

    public String getrStar()
    {
        return rStar;
    }

    public void setrStar(String rStar)
    {
        this.rStar = rStar;
    }

    public List<TableBean> getTableEntities()
    {
        return tableEntities;
    }

    public void setTableEntities(List<TableBean> tableEntities)
    {
        this.tableEntities = tableEntities;
    }

    public String getSnapshot()
    {
        return snapshot;
    }

    public void setSnapshot(String snapshot)
    {
        this.snapshot = snapshot;
    }

    public String getDistance()
    {
        return distance;
    }

    public void setDistance(String distance)
    {
        this.distance = distance;
    }

    public String getrName()
    {
        return rName;
    }

    public void setrName(String rName)
    {
        this.rName = rName;
    }

    public String getCost()
    {
        return perPrice;
    }

    public void setCost(String cost)
    {
        this.perPrice = cost;
    }


    public String getPhoto()
    {
        return photo;
    }

    public void setPhoto(String photo)
    {
        this.photo = photo;
    }

    public String[] getResPraiseList()
    {
        return resPraiseList;
    }

    public void setResPraiseList(String[] resPraiseList)
    {
        this.resPraiseList = resPraiseList;
    }

    public String getCos()
    {
        return cos;
    }

    public void setCos(String cos)
    {
        this.cos = cos;
    }

    public String[] getLocation()
    {
        return location;
    }

    public void setLocation(String[] location)
    {
        this.location = location;
    }

    public String getRpUrls()
    {
        return rpUrls;
    }

    public void setRpUrls(String rpUrls)
    {
        this.rpUrls = rpUrls;
    }

    public String getCircle()
    {
        return circle;
    }

    public void setCircle(String circle)
    {
        this.circle = circle;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getCounty()
    {
        return county;
    }

    public void setCounty(String county)
    {
        this.county = county;
    }

    public String getIsSupplyPrivateS()
    {
        return isSupplyPrivateS;
    }

    public void setIsSupplyPrivateS(String isSupplyPrivateS)
    {
        this.isSupplyPrivateS = isSupplyPrivateS;
    }

    public String getLessPrice()
    {
        return lessPrice;
    }

    public void setLessPrice(String lessPrice)
    {
        this.lessPrice = lessPrice;
    }

    public String getmCategory()
    {
        return mCategory;
    }

    public void setmCategory(String mCategory)
    {
        this.mCategory = mCategory;
    }

    public String getrAddr()
    {
        return rAddr;
    }

    public void setrAddr(String rAddr)
    {
        this.rAddr = rAddr;
    }

    public String getRegion()
    {
        return region;
    }

    public void setRegion(String region)
    {
        this.region = region;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getrPraise()
    {
        return rPraise;
    }

    public void setrPraise(String rPraise)
    {
        this.rPraise = rPraise;
    }

    public String getrSign()
    {
        return rSign;
    }

    public void setrSign(String rSign)
    {
        this.rSign = rSign;
    }

    public String getsCategory()
    {
        return sCategory;
    }

    public void setsCategory(String sCategory)
    {
        this.sCategory = sCategory;
    }

    public String getShorterTime()
    {
        return shorterTime;
    }

    public void setShorterTime(String shorterTime)
    {
        this.shorterTime = shorterTime;
    }

    public String getBookedNum()
    {
        return bookedNum;
    }

    public void setBookedNum(String bookedNum)
    {
        this.bookedNum = bookedNum;
    }

    public String getUseTime()
    {
        return useTime;
    }

    public void setUseTime(String useTime)
    {
        this.useTime = useTime;
    }

    public String getrMobile()
    {
        return rMobile;
    }

    public void setrMobile(String rMobile)
    {
        this.rMobile = rMobile;
    }

    public String getCollected()
    {
        return collected;
    }

    public void setCollected(String collected)
    {
        this.collected = collected;
    }

    public String getPraisedOrNot()
    {
        return praisedOrNot;
    }

    public void setPraisedOrNot(String praisedOrNot)
    {
        this.praisedOrNot = praisedOrNot;
    }

    public String getTestDistance()
    {
        return testDistance;
    }

    public void setTestDistance(String testDistance)
    {
        this.testDistance = testDistance;
    }

    public String getrStatus()
    {
        return rStatus;
    }

    public void setrStatus(String rStatus)
    {
        this.rStatus = rStatus;
    }

    public String getActivity()
    {
        return activity;
    }

    public void setActivity(String activity)
    {
        this.activity = activity;
    }

    public String getPosxy()
    {
        return posxy;
    }

    public void setPosxy(String posxy)
    {
        this.posxy = posxy;
    }

    public String getResStory()
    {
        return resStory;
    }

    public void setResStory(String resStory)
    {
        this.resStory = resStory;
    }

    public String getWeekFlg()
    {
        return weekFlg;
    }

    public void setWeekFlg(String weekFlg)
    {
        this.weekFlg = weekFlg;
    }

    public String getAdvDay()
    {
        return advDay;
    }

    public void setAdvDay(String advDay)
    {
        this.advDay = advDay;
    }

    public String getBilling()
    {
        return billing;
    }

    public void setBilling(String billing)
    {
        this.billing = billing;
    }

    public String getBkTime()
    {
        return bkTime;
    }

    public void setBkTime(String bkTime)
    {
        this.bkTime = bkTime;
    }

    @Override
    public String toString()
    {
        return "RestaurantBean{" +
                "activity='" + activity + '\'' +
                ", rId='" + rId + '\'' +
                ", mapId='" + mapId + '\'' +
                ", rStar='" + rStar + '\'' +
                ", perPrice='" + perPrice + '\'' +
                ", minConsume='" + minConsume + '\'' +
                ", rPraise='" + rPraise + '\'' +
                ", rSign='" + rSign + '\'' +
                ", rTime='" + rTime + '\'' +
                ", rAddr='" + rAddr + '\'' +
                ", bookedNum='" + bookedNum + '\'' +
                ", collected='" + collected + '\'' +
                ", phoneNums=" + phoneNums +
                ", rTotal='" + rTotal + '\'' +
                ", shorterTime='" + shorterTime + '\'' +
                ", useTime='" + useTime + '\'' +
                ", isSupplyPrivateS='" + isSupplyPrivateS + '\'' +
                ", reserveDesks='" + reserveDesks + '\'' +
                ", refundClause='" + refundClause + '\'' +
                ", city='" + city + '\'' +
                ", county='" + county + '\'' +
                ", region='" + region + '\'' +
                ", circle='" + circle + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", sCategory='" + sCategory + '\'' +
                ", lessPrice='" + lessPrice + '\'' +
                ", snapshot='" + snapshot + '\'' +
                ", distance='" + distance + '\'' +
                ", rName='" + rName + '\'' +
                ", photo='" + photo + '\'' +
                ", rMobile='" + rMobile + '\'' +
                ", resStory='" + resStory + '\'' +
                ", disCountItems=" + disCountItems +
                ", bigVs=" + bigVs +
                ", evaList=" + evaList +
                ", tableEntities=" + tableEntities +
                ", resPraiseList=" + Arrays.toString(resPraiseList) +
                ", classUrl='" + classUrl + '\'' +
                ", floor='" + floor + '\'' +
                ", keepTime='" + keepTime + '\'' +
                ", keepUse='" + keepUse + '\'' +
                ", preOrderTime='" + preOrderTime + '\'' +
                ", cos='" + cos + '\'' +
                ", location=" + Arrays.toString(location) +
                ", rpUrls='" + rpUrls + '\'' +
                ", praisedOrNot='" + praisedOrNot + '\'' +
                ", testDistance='" + testDistance + '\'' +
                ", rStatus='" + rStatus + '\'' +
                ", posxy='" + posxy + '\'' +
                ", posx='" + posx + '\'' +
                ", posy='" + posy + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof RestaurantBean))
            return false;
        RestaurantBean bean = (RestaurantBean) o;
        return this.getrId().equals(bean.getrId());
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 17;
        result = PRIME * result + rId.hashCode();
        return result;
    }
}
