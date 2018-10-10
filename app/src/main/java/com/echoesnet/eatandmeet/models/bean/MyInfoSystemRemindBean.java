package com.echoesnet.eatandmeet.models.bean;

/**
 * Created by Administrator on 2016/8/11.
 */
public class MyInfoSystemRemindBean
{

    /**
     * date : 2017-02-25 13:30:34
     * map : {"id":"100027","nicName":"186*****264"}
     * msg : 你与推荐人 186*****264（ID：100027）绑定已过期，你可以通过续绑重新与推荐人建立绑定关系
     * tip : CONSULTANT_BIND_INVALID
     * uId : 84bb7380-9547-4779-a766-231d6929c4e1
     */

    private String date;
    private MapBean map;
    private String msg;
    private String tip;
    private String uId;

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public MapBean getMap()
    {
        return map;
    }

    public void setMap(MapBean map)
    {
        this.map = map;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public String getTip()
    {
        return tip;
    }

    public void setTip(String tip)
    {
        this.tip = tip;
    }

    public String getUId()
    {
        return uId;
    }

    public void setUId(String uId)
    {
        this.uId = uId;
    }

    public static class MapBean
    {
        /**
         * id : 100027
         * nicName : 186*****264
         */

        private String id;
        private String nicName;

        public String getId()
        {
            return id;
        }

        public void setId(String id)
        {
            this.id = id;
        }

        public String getNicName()
        {
            return nicName;
        }

        public void setNicName(String nicName)
        {
            this.nicName = nicName;
        }
    }

    @Override
    public String toString()
    {
        return "MyInfoSystemRemindBean{" +
                "date='" + date + '\'' +
                ", map=" + map +
                ", msg='" + msg + '\'' +
                ", tip='" + tip + '\'' +
                ", uId='" + uId + '\'' +
                '}';
    }
}
