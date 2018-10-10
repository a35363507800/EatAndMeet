package com.echoesnet.eatandmeet.activities.live;

import com.echoesnet.eatandmeet.activities.CPickContactNoCheckboxAct;

/**
 * Created by Administrator on 2016/11/16.
 */

public class LShareAroundAct extends CPickContactNoCheckboxAct
{
//    private EaseUser selectUser;
//    //房间名
//    private String roomName;
//    private String roomId;
//
//    private String titleImage;
//    private String openSouse;
//    private String isLivingOpen = "0";
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        CommonUtils.setTransparentTopBar(this);
//        Intent intent = getIntent();
//        openSouse = intent.getStringExtra("openSouse");
//        if(!TextUtils.isEmpty(openSouse))
//        {
//            if (!"inLive".equals(openSouse))
//            {
//                //房间名
//                roomName = intent.getStringExtra("roomName");
//                titleImage = intent.getStringExtra("titleImage");
//                roomId = intent.getStringExtra("roomId");
//            }
//            else
//            {
//                Logger.t("livePlay").d("直播中分享");
//                //房间名
//                roomName = intent.getStringExtra("roomName");
//                titleImage = intent.getStringExtra("titleImage");
//                roomId = intent.getStringExtra("roomId");
//                uid = intent.getStringExtra("uid");
//                isLivingOpen = "1";
//            }
//        }
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState)
//    {
//        super.onSaveInstanceState(outState, outPersistentState);
//
//    }
//
//    @Override
//    protected void onListItemClick(int position)
//    {
//        selectUser = contactAdapter.getItem(position);
//        new LiveShared2FriendAlertDialog(this)
//                .builder()
//                .setTitle("分享")
//                .setMsg(getString(R.string.confirm_forward_to, selectUser.getNick()))
//                .setTitleImage(titleImage)
//                .setPositiveButton("确定", new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        if (selectUser == null)
//                            return;
//                        NetHelper.addLiveShareCount(LShareAroundAct.this);
//                        EMMessage message = EMMessage.createTxtSendMessage("【直播】" + roomName, selectUser.getUsername());
//                        message.setAttribute("isLiveShare", true);
//                        message.setAttribute("roomId", roomId);
//                        message.setAttribute("RoomName", roomName);
//                        message.setAttribute("roomUrl", titleImage);
//                        message.setAttribute("sender", SharePreUtils.getHxId(LShareAroundAct.this));
//                        EMClient.getInstance().chatManager().sendMessage(message);
//                        Intent intent = new Intent(LShareAroundAct.this, CChatActivity.class);
//                        // it is single chat
//                        intent.putExtra("userId", selectUser.getUsername());
//                        //从直播界面过来，
//                        //intent.putExtra("forward_msg_id",  message.getMsgId());
//
//                        if ("1".equals(isLivingOpen))
//                        {
//                            intent.putExtra("isLiveShare", "false");
//                        }
//                        else
//                        {
//                            intent.putExtra("isLiveShare", "true");
//                        }
//                        startActivity(intent);
//                        finish();
//                    }
//                })
//                .setNegativeButton("取消", new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//
//                    }
//                }).show();
//    }
}
