package com.echoesnet.eatandmeet.models.eventmsgs;

import com.echoesnet.eatandmeet.models.bean.ResLayoutBean;
import com.echoesnet.eatandmeet.models.bean.TableBean;

import java.util.List;

/**
 * Created by Administrator on 2016/5/13.
 */
public class OrderTableMsg
{
    private  List<ResLayoutBean> layoutEntities;
    private  List<TableBean> selectedTables;
    private  String restId;
    private  String floorNum;
    private  String orderTime;



//    private List<HashMap<String,String>> selectedTableInfo;

    public OrderTableMsg(String floorNum, List<ResLayoutBean> layoutEntities, String orderTime, String restId,List<TableBean> selectedTables)
    {
        this.floorNum = floorNum;
        this.layoutEntities = layoutEntities;
        this.orderTime = orderTime;
        this.restId = restId;
        this.selectedTables=selectedTables;
       // this.selectedTableInfo=selectedTableInfo;
    }

    public List<ResLayoutBean> getLayoutEntities()
    {
        return layoutEntities;
    }

    public String getFloorNum()
    {
        return floorNum;
    }

    public String getOrderTime()
    {
        return orderTime;
    }

    public String getRestId()
    {
        return restId;
    }

    public List<TableBean> getSelectedTables()
    {
        return selectedTables;
    }

    @Override
    public String toString()
    {
        return "OrderTableMsg{" +
                "floorNum='" + floorNum + '\'' +
                ", layoutEntities=" + layoutEntities +
                ", selectedTables=" + selectedTables +
                ", restId='" + restId + '\'' +
                ", orderTime='" + orderTime + '\'' +
                '}';
    }
}
