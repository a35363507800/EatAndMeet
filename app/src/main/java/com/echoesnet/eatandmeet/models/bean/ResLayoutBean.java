package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by wangben on 2016-6-9.
 */
public class ResLayoutBean
{
    private String layoutId;
    private FloorBean floor;
    private List<TableBean> tables;

    public ResLayoutBean()
    {

    }

    public ResLayoutBean(String layoutId, FloorBean floorBean, List<TableBean> tables)
    {
        this.layoutId = layoutId;
        this.floor = floorBean;
        this.tables = tables;
    }

    public String getLayoutId()
    {
        return layoutId;
    }

    public void setLayoutId(String layoutId)
    {
        this.layoutId = layoutId;
    }

    public FloorBean getFloor()
    {
        return floor;
    }

    public void setFloor(FloorBean floorBean)
    {
        this.floor = floorBean;
    }

    public List<TableBean> getTables()
    {
        return tables;
    }

    public void setTables(List<TableBean> tables)
    {
        this.tables = tables;
    }

    @Override
    public String toString()
    {
        return "ResLayoutBean{" +
                "floor=" + floor +
                ", layoutId='" + layoutId + '\'' +
                ", tables=" + tables +
                '}';
    }
}
