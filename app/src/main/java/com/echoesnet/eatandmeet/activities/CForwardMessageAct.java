/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.echoesnet.eatandmeet.activities;

public class CForwardMessageAct extends CPickContactNoCheckboxAct
{
//    private EaseUser selectUser;
//    private String forward_msg_id;
//    //房间名
//    private String roomName;
//    private String roomId;
//
//    private String titleImage;
//    private String openSouse;
//    private String uid;
//    private String isLivingOpen = "0";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        CommonUtils.setTransparentTopBar(this);
//        forward_msg_id = getIntent().getStringExtra("forward_msg_id");
//        if(savedInstanceState==null)
//        {
//            savedInstanceState = new Bundle();
//            savedInstanceState.putString("uid",uid);
//        }
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    protected void onListItemClick(int position)
//    {
//        selectUser = contactAdapter.getItem(position);
//        new CustomAlertDialog(this)
//                .builder()
//                .setTitle("提示")
//                .setMsg(getString(R.string.confirm_forward_to, selectUser.getNick()))
//                .setPositiveButton("确定", new View.OnClickListener()
//                {
//                    @Override
//                    public void onClick(View v)
//                    {
//                        if (selectUser == null)
//                            return;
//                        try
//                        {
//                            CChatActivity.activityInstance.finish();
//                        } catch (Exception e)
//                        {
//                            Logger.t("F").d(e.getMessage());
//                        }
//                        Intent intent = new Intent(CForwardMessageAct.this, CChatActivity.class);
//                        // it is single chat
//                        intent.putExtra("userId", selectUser.getUsername());
//                        intent.putExtra("forward_msg_id", forward_msg_id);
//                        startActivity(intent);
//                        finish();
//                    }
//                }).setNegativeButton("取消", null)
//                .show();
//    }
}
