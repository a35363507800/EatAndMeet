package com.echoesnet.eatandmeet.models;

import com.echoesnet.eatandmeet.activities.DemoAct;
import com.echoesnet.eatandmeet.utils.OSHelper;

//import dagger.Component;

/**
 * Created by Administrator on 2016/4/12.
 */
//@ScopeActivity 注解，这个不是Dagger自带的，是自定义的。因为AppComponent使用@Singleton注解，
//@Singletion是一个特殊的@Scope，所以ActivityComponent如果没有使用 @Scope 注解，那么将无法依赖 AppComponent
//@Component(dependencies=AppComponent.class)
//@ScopeActivity
public interface ActivityComponent
{
    void inject(DemoAct activity);
    //Activity的依赖项函数都写在这个地方，例如要调用某个类里面的函数，则将此函数写在这里
    OSHelper getOSHelper();
}
