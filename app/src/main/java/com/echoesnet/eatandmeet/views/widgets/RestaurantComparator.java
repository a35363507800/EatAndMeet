package com.echoesnet.eatandmeet.views.widgets;

import com.echoesnet.eatandmeet.models.bean.RestaurantBean;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/6/16.
 */
public class RestaurantComparator implements Comparator<RestaurantBean> {
    @Override
    public int compare(RestaurantBean bean1, RestaurantBean bean2) {
        return Integer.parseInt(bean1.getPerPrice()) - Integer.parseInt(bean2.getPerPrice());
    }
}
