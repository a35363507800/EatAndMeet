package com.echoesnet.eatandmeet.views.widgets.orderDishesView;

import com.echoesnet.eatandmeet.models.bean.RestaurantBean;

import java.util.List;

/**
 * Created by Administrator on 2016/6/1.
 */
public class OrderDishesListEvent {

    private List<RestaurantBean> list;

    public OrderDishesListEvent(List<RestaurantBean> list) {
        this.list = list;
    }

    public List<RestaurantBean> getList() {
        return list;
    }
}
