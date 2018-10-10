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
package com.echoesnet.eatandmeet.utils.IMUtils;


public class Constant
{
    public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
    public static final String GROUP_USERNAME = "item_groups";
    public static final String CHAT_ROOM = "item_chatroom";
    public static final String ACCOUNT_REMOVED = "account_removed";
    public static final String ACCOUNT_CONFLICT = "conflict";
    public static final String CHAT_ROBOT = "item_robots";
    public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
    public static final String ACTION_GROUP_CHANAGED = "action_group_changed";
    public static final String ACTION_CONTACT_CHANAGED = "action_contact_changed";

    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";

    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
    //表情图片的url
    public static final String MESSAGE_ATTR_EXPRESSION_URL = "em_expression_url";

    public static final String MESSAGE_ATTR_AT_MSG = "em_at_message";

    public static final String MESSAGE_ATTR_IS_LIVE_SHARE = "is_live_share";
    public static final String MESSAGE_ATTR_IS_PARTY_SHARE = "party";
    //游戏
    public static final String MESSAGE_ATTR_IS_GAME_MESSAGE = "is_game_message";
    //撤回消息标识
    public static final String MESSAGE_ATTR_IS_RECALL_MESSAGE = "is_recall_message";
    //是否是自己撤回
    public static final String MESSAGE_ATTR_IS_RECALL_SENDER = "is_recall_sender";

    public static final String MESSAGE_ATTR_SENSITIVE_CONTENT = "sensitive_content";

    public static final String MESSAGE_ATTR_RECALL_DESC = "recall_desc";

    public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;

    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_TO_EASEUSER = "toEaseUser";

    public static final String EVENT_ADD_EMOJI = "ease_ui_add_emoji";

}
