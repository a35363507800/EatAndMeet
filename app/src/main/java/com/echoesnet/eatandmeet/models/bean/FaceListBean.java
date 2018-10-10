package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */

public class FaceListBean
{
    private String face;
    private String level;
    private List<faceList> faceList;

    public String getLevel()
    {
        return level;
    }

    public void setLevel(String level)
    {
        this.level = level;
    }

    public String getFace()
    {
        return face;
    }

    public void setFace(String face)
    {
        this.face = face;
    }

    public List<FaceListBean.faceList> getFaceList()
    {
        return faceList;
    }

    public void setFaceList(List<FaceListBean.faceList> faceList)
    {
        this.faceList = faceList;
    }

    public class faceList
    {
        private String getAmount;
        private String rechargeAmount;

        public String getGetAmount()
        {
            return getAmount;
        }

        public void setGetAmount(String getAmount)
        {
            this.getAmount = getAmount;
        }

        public String getRechargeAmount()
        {
            return rechargeAmount;
        }

        public void setRechargeAmount(String rechargeAmount)
        {
            this.rechargeAmount = rechargeAmount;
        }
    }


}
