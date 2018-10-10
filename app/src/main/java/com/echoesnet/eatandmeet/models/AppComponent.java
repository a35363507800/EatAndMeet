package com.echoesnet.eatandmeet.models;

//import javax.inject.Singleton;

//import dagger.Component;
import com.echoesnet.eatandmeet.controllers.EamApplication;

/**
 * Created by wangben on 2016/4/12.
 */
//@Component的作用是为Module和Injection提供接口
//@Component(modules = {AppModule.class,DemoModule.class})
//@Singleton
public interface AppComponent
{
    EamApplication inject(EamApplication eamApp);
}
