package com.echoesnet.eatandmeet.models;

import com.echoesnet.eatandmeet.controllers.EamApplication;

/*import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;*/

/**
 * Created by benwang on 2016/4/12.
 */

//@Module
public class AppModule
{
    private EamApplication mEamApp;
    public AppModule(EamApplication app)
    {
        mEamApp=app;
    }

/*    @Provides
    @Singleton
    Application provideApplication()
    {
        return mEamApp;
    }

    @Provides
    @Singleton
    Context provideContext()
    {
        return mEamApp;
    }*/
}
