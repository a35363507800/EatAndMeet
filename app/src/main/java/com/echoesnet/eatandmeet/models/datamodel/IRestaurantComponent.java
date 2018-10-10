package com.echoesnet.eatandmeet.models.datamodel;

import com.echoesnet.eatandmeet.fragments.RestaurantFrg;

/**
 * Created by Administrator on 2016/4/26.
 */
//@Component(modules = {RestaurantModule.class})
public interface IRestaurantComponent
{
    void inject(RestaurantFrg resFrg);
}
