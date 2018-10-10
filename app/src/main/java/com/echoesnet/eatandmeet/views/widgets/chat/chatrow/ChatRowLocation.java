package com.echoesnet.eatandmeet.views.widgets.chat.chatrow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.echoesnet.eatandmeet.R;
import com.echoesnet.eatandmeet.activities.BaiduMapActivity;
import com.echoesnet.eatandmeet.utils.EamConstant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.LatLng;
import com.orhanobut.logger.Logger;

public class ChatRowLocation extends ChatRow
{

    private TextView locationView;
    private TextView locationAddressView;
    private EMLocationMessageBody locBody;

    public ChatRowLocation(Context context, EMMessage message, int position, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter)
    {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView()
    {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_location : R.layout.ease_row_sent_location, this);
    }

    @Override
    protected void onFindViewById()
    {
        locationView = (TextView) findViewById(R.id.tv_location);
        locationAddressView = (TextView) findViewById(R.id.tv_location_address);
    }


    @Override
    protected void onSetUpView()
    {
        locBody = (EMLocationMessageBody) message.getBody();

        try
        {
            String locationAddressName = message.getStringAttribute(EamConstant.MESSAGE_ATTR_ADDRESS_NAME);
            String locationDetailAddress = message.getStringAttribute(EamConstant.MESSAGE_ATTR_DETAIL_ADDRESS);
            locationView.setText(locationAddressName);
            locationAddressView.setText(locationDetailAddress);
        } catch (HyphenateException e)
        {
            e.printStackTrace();
            Logger.t(TAG).d("获取地址信息失败");
        }
        // handle sending message
        if (message.direct() == EMMessage.Direct.SEND)
        {
            setMessageSendCallback();
            switch (message.status())
            {
                case CREATE:
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    statusView.setVisibility(View.GONE);
                    break;
                case FAIL:
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS:
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
        else
        {
            if (!message.isAcked() && message.getChatType() == ChatType.Chat)
            {
                try
                {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onUpdateView()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick()
    {
        Intent intent = new Intent(context, BaiduMapActivity.class);
        intent.putExtra("latitude", locBody.getLatitude());
        intent.putExtra("longitude", locBody.getLongitude());
        intent.putExtra("address", locBody.getAddress());
        activity.startActivity(intent);
    }

    @Override
    protected void onBubbleLongClick()
    {

    }

    /*
     * listener for map clicked
	 */
    protected class MapClickListener implements View.OnClickListener
    {

        LatLng location;
        String address;

        public MapClickListener(LatLng loc, String address)
        {
            location = loc;
            this.address = address;

        }

        @Override
        public void onClick(View v)
        {

        }
    }

}
