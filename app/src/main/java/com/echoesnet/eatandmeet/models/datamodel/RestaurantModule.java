package com.echoesnet.eatandmeet.models.datamodel;

/**
 * Created by wangben on 2016/4/26.
 */
//@Module
public class RestaurantModule
{
    //@Provides
    Restaurant provideRestInstance()
    {
        return new Restaurant();
    }
}
