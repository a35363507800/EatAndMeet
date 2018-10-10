package com.echoesnet.eatandmeet.models.eventmsgs;


import com.echoesnet.eatandmeet.models.bean.RestaurantBean;

import java.util.List;

/**
 * Created by Administrator on 2016/4/21.
 */
public class HomeEvent {

    private String Type;

    public HomeEvent(String type) {
        Type = type;
    }

    public String getType() {
        return Type;
    }
}
