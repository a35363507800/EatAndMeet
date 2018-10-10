package com.echoesnet.eatandmeet.models.bean;

import java.util.List;

/**
 * Created by an on 2017/3/30 0030.
 */

public class FRestaurannt4FindBean {
    private List<FRestaurantItemBean> recommend;
    private List<FRestaurantItemBean> todayRecommend;

    public List<FRestaurantItemBean> getRecommend() {
        return recommend;
    }

    public void setRecommend(List<FRestaurantItemBean> recommend) {
        this.recommend = recommend;
    }

    public List<FRestaurantItemBean> getTodayRecommend() {
        return todayRecommend;
    }

    public void setTodayRecommend(List<FRestaurantItemBean> todayRecommend) {
        this.todayRecommend = todayRecommend;
    }
}
