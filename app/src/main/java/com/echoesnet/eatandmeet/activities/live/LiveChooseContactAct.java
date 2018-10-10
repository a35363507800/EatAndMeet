package com.echoesnet.eatandmeet.activities.live;

import android.os.Bundle;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaseActivity;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopBarSwitch;
import com.echoesnet.eatandmeet.views.widgets.TopBarSwich.TopbarSwitchSkeleton;

/**
 * Created by Administrator on 2017/3/30.
 */

public class LiveChooseContactAct extends BaseActivity
{
    private TopBarSwitch topBarSwitch;
    //private RelativeLayout fragmentContainer;
//    private ContactListFragment contactListFragment;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_choose_ccontact);
        initView();
    }

    private void initView()
    {
        topBarSwitch = (TopBarSwitch) findViewById(R.id.top_bar_switch);
        //fragmentContainer = (RelativeLayout) findViewById(R.id.fragment_container);
        topBarSwitch.inflateTextCenter(new TopbarSwitchSkeleton()
        {
            @Override
            public void leftClick(View view)
            {
                finish();
            }

            @Override
            public void right2Click(View view)
            {
            }
        }).setText("选择联系人");
//        contactListFragment = ContactListFragment.newInstance(2);
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragment_container, contactListFragment)
//                .show(contactListFragment)
//                .commit();
    }

}
