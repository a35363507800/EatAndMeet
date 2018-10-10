package com.echoesnet.eatandmeet.models.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wang on 2016/6/29.
 */
public class MyResCommentBean implements Serializable
{
    private String rId;
    /* 订单id */
    private String oId;
    /* 评语*/
    private String evalContent;
    /* 评价图片urls ,以分隔符分割*/
    private String epUrls;
    /* 餐厅评级*/
    private String rStar = "0";

    private String resName;

    private List<DishBean> dishLevel;
        /* 菜品评级*/
    //private Map<String,String> dishLevel;

/*        public Map<String, String> getDishLevel()
        {
            return dishLevel;
        }

        public void setDishLevel(Map<String, String> dishLevel)
        {
            this.dishLevel = dishLevel;
        }*/

    public List<DishBean> getDishLevel()
    {
        return dishLevel;
    }

    public void setDishLevel(List<DishBean> dishLevel)
    {
        this.dishLevel = dishLevel;
    }

    public String getEpUrls()
    {
        return epUrls;
    }

    public void setEpUrls(String epUrls)
    {
        this.epUrls = epUrls;
    }

    public String getEvalContent()
    {
        return evalContent;
    }

    public void setEvalContent(String evalContent)
    {
        this.evalContent = evalContent;
    }

    public String getoId()
    {
        return oId;
    }

    public void setoId(String oId)
    {
        this.oId = oId;
    }

    public String getrId()
    {
        return rId;
    }

    public void setrId(String rId)
    {
        this.rId = rId;
    }

    public String getrStar()
    {
        return rStar;
    }

    public void setrStar(String rStar)
    {
        this.rStar = rStar;
    }

    public String getResName()
    {
        return resName;
    }

    public void setResName(String resName)
    {
        this.resName = resName;
    }

    @Override
    public String toString()
    {
        return "ResComment{" +
                "dishLevel=" + dishLevel +
                ", rId='" + rId + '\'' +
                ", oId='" + oId + '\'' +
                ", evalContent='" + evalContent + '\'' +
                ", epUrls='" + epUrls + '\'' +
                ", rStar='" + rStar + '\'' +
                '}';
    }
}
