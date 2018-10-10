package com.echoesnet.eatandmeet.activities;

import android.os.Bundle;
import android.view.View;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.utils.CommonUtils;
import com.joanzapata.iconify.widget.IconTextView;



import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class GiftDialogAct extends BaseActivity
{
    @BindView(R.id.itv_gift_close)
    IconTextView itvClose;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gift_dialog);
        ButterKnife.bind(this);

    }

    @OnClick({R.id.itv_gift_close})
    void onViewClick(View view)
    {
        switch (view.getId())
        {
            case R.id.itv_gift_close:
                finish();
                break;

            default:
                break;
        }
    }

}
