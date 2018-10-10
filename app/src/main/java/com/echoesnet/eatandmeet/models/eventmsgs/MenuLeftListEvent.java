package com.echoesnet.eatandmeet.models.eventmsgs;

import com.echoesnet.eatandmeet.models.bean.DishRightMenuGroupBean;
import com.echoesnet.eatandmeet.models.bean.OrderMenuLeftBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/21.
 */
public class MenuLeftListEvent {

    private ArrayList<DishRightMenuGroupBean> leftList;


    public MenuLeftListEvent(ArrayList<DishRightMenuGroupBean> leftList) {
        this.leftList = leftList;
    }

    public ArrayList<DishRightMenuGroupBean> getLeftListMsg() {
        return leftList;
    }


}
