package com.echoesnet.eatandmeet.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.controllers.EamApplication;
import com.echoesnet.eatandmeet.utils.FileUtils;
import com.echoesnet.eatandmeet.utils.NetUtils.NetHelper;
import com.echoesnet.eatandmeet.utils.SharePreUtils;
import com.orhanobut.logger.Logger;

import butterknife.BindView;

/**
 * Copyright (C) 2017 在线回声（天津）科技发展有限公司
 * 在线回声完全享有此软件的著作权，违者必究
 *
 * @author Administrator
 * @version 1.0
 * @modifier
 * @createDate 2017/8/21
 * @description
 */
public class LuanchAct extends BaseActivity
{
    @Override
    protected void onCreate(Bundle arg0)
    {
        super.onCreate(arg0);
        if (0 == SharePreUtils.getVersionCode(EamApplication.getInstance()))
        {
            Intent intent = new Intent(LuanchAct.this, LeadAct.class);
            startActivity(intent);
        } else
        {
            Intent intent = new Intent(LuanchAct.this, WelcomeAct.class);
            startActivity(intent);
        }
        finish();


    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
